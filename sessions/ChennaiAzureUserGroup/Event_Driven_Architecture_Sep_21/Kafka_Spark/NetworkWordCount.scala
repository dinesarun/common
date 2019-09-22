import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SQLContext
import com.microsoft.azure.sqldb.spark.config.Config
import com.microsoft.azure.sqldb.spark.query._
    
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._

object NetworkWordCount {
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Usage: NetworkWordCount <hostname> <port>")
      System.exit(1)
    }

    val ssc = new StreamingContext(sc, Seconds(10))
    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()

    wordCounts.foreachRDD( rdd => {
        val dfResults = rdd.toDF.select(concat_ws(",",rdd.toDF.columns.map(c => col(c)): _*))
        val rows = dfResults.select("concat_ws(,, _1, _2)").collect().map(_.getString(0)).mkString("    ")
        
        val sqlQuery = "insert into [dbo].[dinesh-spark-message] values ('" + rows + "');"

        val config = Config(Map(
          "url"          -> "dataplatformdemodata.syncfusion.com",
          "databaseName" -> "development",
          "user"         -> "development@data-platform-demo",
          "password"     -> "KW9&ueC$24M4WT",
          "queryCustom"  -> sqlQuery
        ))
        
        if(rows.length() == 0)
        {
          println("No data")
        }
        else
        {
          sqlContext.sqlDBQuery(config)
          println("Inserted into db - " + rows)
        }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}