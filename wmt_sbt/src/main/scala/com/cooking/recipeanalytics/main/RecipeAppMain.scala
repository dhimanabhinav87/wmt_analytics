package com.cooking.recipeanalytics
import java.io.File

import com.cooking.recipeanalytics.common.AnalyticsConf
import com.cooking.recipeanalytics.processfile.{FileOperations, RecipeAnalyticsProcess}
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

package object RecipeAppMain {
  val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)


  def main(args: Array[String]): Unit = {
    LOGGER.info("Initialize WMT_Analytics Main Application")
    if (args.length < 1) {
      LOGGER.error("Missing parameters")
      System.exit(1)
    }
    val analyticsConf: AnalyticsConf = new AnalyticsConf
    //reading configuration
    analyticsConf.run(args(0))
    LOGGER.info("Configuration File: " + args(0))
    try {
      val fileOperations = new FileOperations
      LOGGER.info("Getting list of files to be processed from "+ analyticsConf.csvSrcDirectory)
      val csvFiles: List[File] = fileOperations.getListOfFiles(new File(analyticsConf.csvSrcDirectory),List("csv"))
      LOGGER.info("Processing each big file at a time")
      for(file <- csvFiles){
        LOGGER.info("Now Processing file :"+ file)
        val analyticsProces= new RecipeAnalyticsProcess
        analyticsProces.run(analyticsConf,file,fileOperations)
        LOGGER.info("Processing done for  file :"+ file)

      }


    }
    catch {
      case e: Exception => {
        LOGGER.error("The WMT_Analytics process failed")
        LOGGER.error(s"$e")
        sys.exit(1)
      }
    }

  }

}


