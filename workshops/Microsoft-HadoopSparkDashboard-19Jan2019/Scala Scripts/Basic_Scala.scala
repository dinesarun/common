//Create a new dataframe with column name.
var jsonDataFrame = Seq((0,"Male","Yes")).toDF("total_bill","sex","smoker");

//Delete a column
val deletedColumn = jsonDataFrame.drop(jsonDataFrame(("smoker")))
deletedColumn.show();

//Add a column to the existing dataframe
var addDF = deletedColumn.withColumn("AddedNew", jsonDataFrame("total_bill"))
addDF.show();

//rename column of existing dataframe
addDF = addDF.withColumnRenamed("AddedNew","Renamed")
addDF.show();

//Read csv file
var dataframe = spark.read.option("header",false).option("delimiter"," ").format("csv").load("file:///D:/SampleNASAFile.txt");

//Print first ‘n’ rows of dataframe
dataFrame.take(10).foreach(println)

//Read json from local
val dataload = spark.read.format("json").load("/Data/Spark/Resources/People.json")
dataload.show()

//Get the sum of specific column
val sumColumn = dataload.agg(sum("age").as("sumColumn"))

Reference Links: https://spark.apache.org/docs/2.2.0/api/scala/index.html#package 
