package cluster

import org.apache.log4j.Logger
import org.apache.log4j.Level

import scala.reflect.io.Directory
import java.io._


import scala.io.Source.fromFile
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.Row
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD


import org.apache.spark.mllib.clustering.{GaussianMixture, GaussianMixtureModel}
import org.apache.spark.mllib.linalg.Vectors

object StringImplicits {
  implicit class StringImprovements(val s: String) {
  import scala.util.control.Exception.catching
  def toDoubleSafe = catching(classOf[NumberFormatException]) opt s.toDouble
 
  }
}


object Brisbane {
  import StringImplicits._

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  
  

  
  //get double value from s2 if s1's missing
  def getValue(s1:String,s2:String): Option[Double] = (s1,s2) match {
    case (null,null) => None
    case (null,s2)   => s2.toDoubleSafe
    case (s1,_)      => s1.toDoubleSafe
    case _           => None
  
  }
  
  def main(args : Array[String]) {
 
    val spark = SparkSession.builder().getOrCreate()  
      
    import spark.implicits._
    
    val sc = spark.sparkContext
 
    val inputPath =args(0)
    
    val ghLog =  spark.read.json(spark.sparkContext.wholeTextFiles(inputPath).values)
    
    def getValueUdf = udf( (s1:String,s2:String) => getValue(s1,s2))
    
    val cleaned=ghLog.select(
        $"id",
        getValueUdf($"latitude", $"coordinates.latitude").alias("latitude"),
        getValueUdf($"longitude", $"coordinates.longitude").alias("longitude")
    )
    // eliminate non correct coordinates
    val filtered=cleaned.filter("latitude is not null  and  longitude is not null ")
    
    val inputData = filtered.map( row =>   (row.getDouble(0),Vectors.dense(Array(row.getDouble(1),row.getDouble(2)))) ).rdd.cache() 
    
    val gmm = new GaussianMixture().setK(args(1).toInt).run(inputData.values)
    
    //clean previously created results folder
    val directory = new Directory(new File(args(2)))
    directory.deleteRecursively()
    
    //printing GMM results
    for (i <- 0 until gmm.k) {  println("weight=%f\nmu=%s\nsigma=\n%s\n" format    (gmm.weights(i), gmm.gaussians(i).mu, gmm.gaussians(i).sigma))}
    
    //writing out clustering results:
    inputData.map{ 
      case ( id,vector) => "%.2f \t %d".format(id, gmm.predict(vector) ) 
    }.saveAsTextFile(args(2))

  
  }
    
}