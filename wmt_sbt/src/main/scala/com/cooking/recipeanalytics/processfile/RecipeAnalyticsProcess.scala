package com.cooking.recipeanalytics.processfile

import java.io.File

import com.cooking.recipeanalytics.common.AnalyticsConf
import com.cooking.recipeanalytics.main.KryoRegistration
import com.cooking.recipeanalytics.util.{CassandraOperations, RecipeAnalyticsHelper}
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

class RecipeAnalyticsProcess {
  val LOGGER: Logger = LoggerFactory.getLogger(this.getClass().getName)
  LOGGER.info("Creating File operation handle")
  /**
    *
    * @param analyticsconf
    * @param file
    * @param fileOperations
    */
  def run(analyticsconf: AnalyticsConf, file: File,fileOperations: FileOperations): Unit = {
    LOGGER.info("Copying file to hdfs")
    fileOperations.copyFileToHdfs(file.getAbsolutePath,analyticsconf.dstDirectory)
    LOGGER.info("File Copy done")

    val getFilename: String = fileOperations.getFileName(file.getAbsolutePath)
    LOGGER.info("File: "+getFilename)

    val file_ts: String = fileOperations.convertFileMetadatToTimstamp(getFilename)
    LOGGER.info("FileTS: "+file_ts)

    val fileHdfsPath: String = "hdfs://quickstart.cloudera:8020"+analyticsconf.dstDirectory+getFilename
    LOGGER.info("fileHdfsPath: "+fileHdfsPath)

    val sparkConf: SparkConf = new SparkConf().setAppName("WMT_Analytics")
      .set("spark.sql.orc.filterPushdown", "true")
      .set("spark.sql.hive.convertMetastoreOrc", "false")
      .set("spark.ui.port", (4040 + scala.util.Random.nextInt(1000)).toString)
      .set("spark.cassandra.connection.host", analyticsconf.cassandraHost)
      .set("spark.executor.heartbeatInterval", "100s")

    KryoRegistration.register(sparkConf)

    val spark = SparkSession.builder
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    val sc: SparkContext = spark.sparkContext
    val hiveContext: SQLContext = spark.sqlContext
    hiveContext.setConf("hive.exec.dynamic.partition", "true")
    hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
    hiveContext.setConf("hive.execution.engine", "tez")

    try {
      val recipeDataDf: DataFrame = hiveContext.read.format("csv")
        .option("header", "true")
        .option("inferSchema", "true")
        .load(fileHdfsPath)
      LOGGER.info("Getting unique list of recipes for updating cassandra")
      val uniqueRecipes: DataFrame = recipeDataDf.select("recipe_name").dropDuplicates()
      LOGGER.info("Getting recipe to ingredients association for updating cassandra")
      val recipeIngredientAssociation: DataFrame = recipeDataDf.select("recipe_name", "ingredient").dropDuplicates()

      LOGGER.info("updating Cassandra table "+ analyticsconf.recipeCassandraTbl)
      CassandraOperations.saveDfToCassandra(analyticsconf.recipeCassandraTbl, analyticsconf, uniqueRecipes)

      LOGGER.info("updating Cassandra table "+ analyticsconf.recipeIngredientsCassandraTbl)
      CassandraOperations.saveDfToCassandra(analyticsconf.recipeIngredientsCassandraTbl, analyticsconf, recipeIngredientAssociation)

      LOGGER.info("Attaching source file metadata")
      val external_df = RecipeAnalyticsHelper.appendSourceFileTsToDf(file_ts, analyticsconf, recipeDataDf)

      external_df.show()
      LOGGER.info("saving the currently processed df to hive staging schema")
      external_df.coalesce(1).write.mode(SaveMode.Overwrite).format("orc").saveAsTable("ext_wmt_analytics.recipe_hourly_temp")

      LOGGER.info("Loading the data into final table from staging")
      val finalRecipeHiveDf = hiveContext.sql(RecipeAnalyticsHelper.loadFinalHiveTableRecipeQuery())

      LOGGER.info("Saving the processed file to archive")
      fileOperations.archiveToHdfs(file.getAbsolutePath, analyticsconf.archivePath)
    }
      catch {
        case e: Exception => {
          LOGGER.error(e.toString + e.printStackTrace())
          LOGGER.error("In catch of process summary")
          throw e
        }
      }
      finally {
        LOGGER.info("Closing Spark Context and End of Program")
        sc.stop()
        spark.stop()
      }
    }

  }

