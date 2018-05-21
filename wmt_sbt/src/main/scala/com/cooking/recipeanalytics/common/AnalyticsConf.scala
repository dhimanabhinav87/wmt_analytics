package com.cooking.recipeanalytics.common

import java.io.File

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

class AnalyticsConf {
  val LOGGER: Logger = LoggerFactory.getLogger(this.getClass().getName)

  var csvSrcDirectory = "/home/cloudera/IN/cooking/recipe/data/"
  var archivePath = "/user/cloudera/wmt/cooking/archive/"
  var dstDirectory = "/user/cloudera/wmt/cooking/data/"
  var cassandraHost = "127.0.0.1"
  var recipeHiveTbl="wmt_analytics.recipe_hourly"
  var recipeCassandraKeySpace="wmt_analytics"
  var recipeCassandraTbl="recipes"
  var recipeIngredientsCassandraTbl="recipe_ingredients"

  /**
    * Read Properties
    * @param configPath Configuration Path
    */
  def run(configPath : String): Unit = {
    try{

      LOGGER.info("Reading Configuration From : " + configPath)

      val config = ConfigFactory.parseFile(new File(configPath))

      csvSrcDirectory = config.getString("csvSrcDirectory")
      archivePath = config.getString("archivePath")
      dstDirectory=config.getString("dstDirectory")
      cassandraHost = config.getString("cassandraHost")
      recipeHiveTbl = config.getString("recipeHiveTbl")
      recipeCassandraKeySpace=config.getString("recipeCassandraKeySpace")
      recipeCassandraTbl=config.getString("recipeCassandraTbl")
      recipeIngredientsCassandraTbl=config.getString("recipeIngredientsCassandraTbl")


    } catch {
      case e : Exception => {

        LOGGER.error("Error reading the config file")
        LOGGER.error(e.toString + e.printStackTrace())
        sys.exit(1)
      }
    }
  }


}

