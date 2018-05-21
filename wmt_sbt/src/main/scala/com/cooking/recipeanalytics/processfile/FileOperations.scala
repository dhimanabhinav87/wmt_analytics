package com.cooking.recipeanalytics.processfile
import java.io.File
import java.nio.file.Paths
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path

import scala.tools.nsc.classpath.FileUtils

class FileOperations {
  val hadoopConf: Configuration = new Configuration()
  hadoopConf.addResource(new Path("/etc/hadoop/conf/core-site.xml"))
  val hdfs: FileSystem = FileSystem.get(hadoopConf)

  def copyFileToHdfs(srcFilePath: String, dstFilePath: String): Unit = {
    val src: Path = new Path(srcFilePath)
    val dest: Path = new Path(dstFilePath)
    val copyFile: Unit = hdfs.copyFromLocalFile(src, dest)

  }
  def archiveToHdfs(srcFilePath: String, archivePath: String): Unit = {
    val copyToArchive= copyFileToHdfs(srcFilePath,archivePath)
    //val deleteLocalFile: Boolean = new File(srcFilePath).delete()

  }

  def getFileName(filePath : String): String ={
    val p = Paths.get(filePath)
    val file = p.getFileName.toString
    return file
  }

  /**
    *
    * @param fileName
    * @return
    */
  def convertFileMetadatToTimstamp (fileName: String): String = {
    val y_m_d: String = fileName.substring(0,10)
    val hour: String = fileName.substring(11,13)
    return y_m_d+" "+hour+":00:00"
  }

  /**
    *
    * @param dir
    * @param extensions
    * @return
    */
  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

}
