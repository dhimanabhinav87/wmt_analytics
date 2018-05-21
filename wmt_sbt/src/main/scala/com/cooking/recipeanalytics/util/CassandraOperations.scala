package com.cooking.recipeanalytics.util

import com.cooking.recipeanalytics.common.AnalyticsConf
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.slf4j.{Logger, LoggerFactory}
import org.apache.spark.sql.cassandra._

object CassandraOperations {
  val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    *
    * @param tblname
    * @param analyticsConf
    * @param df
    * @return
    */
  def saveDfToCassandra(tblname: String,analyticsConf:AnalyticsConf,df:DataFrame): Boolean = {
    df.coalesce(1).write.cassandraFormat(tblname, analyticsConf.recipeCassandraKeySpace).mode(SaveMode.Append).save()

  return true
  }

}
