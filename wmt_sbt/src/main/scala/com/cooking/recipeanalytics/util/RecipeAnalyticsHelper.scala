package com.cooking.recipeanalytics.util

import com.cooking.recipeanalytics.common.AnalyticsConf
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

object RecipeAnalyticsHelper {
  val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)


  def appendSourceFileTsToDf(file_ts: String,analyticsConf:AnalyticsConf,df:DataFrame): DataFrame = {
    val y_m_d: String = file_ts.substring(0,10)
    val hour: String = file_ts.substring(11,13)
    val df_added_ts= df.withColumn("source_file_date", lit(y_m_d))
      .withColumn("hour", lit(hour))

    return df_added_ts
  }

 def loadFinalHiveTableRecipeQuery(): String = {
   s"""insert overwrite  table wmt_analytics.recipe_hourly partition(source_file_date,hour)
      |select recipe_id
      |,recipe_name
      |,description
      |,ingredient
      |,active
      |,updated_date
      |,created_date
      |,source_file_date
      |,hour from ext_wmt_analytics.recipe_hourly_temp
   """.stripMargin
 }
}
