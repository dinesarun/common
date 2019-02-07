/**
Process Data (Filter rows, add columns, remove columns)
**/

// Now read the text file from local as csv format
var dataframe = spark.read.option("header",false).option("delimiter"," ").format("csv").load("file:///D:/SparkOutput/NasaCleanLogs");

// Read CSV file from local
var csvDataFrame = spark.read.option("header",true).csv("file:///D:/SparkOutput/SchemaDF");
// Display the contents of Dataframe
csvDataFrame.show();

// To Get more than 1 column,
//var multipleColumns = csvDataFrame.select("RequestName", "StatusCode");
//multipleColumns.show()

// Another way to select a specific column from dataframe.
var requestColumn = csvDataFrame.select(csvDataFrame("RequestName"));
requestColumn.show();

// Get the count of unique items of dataframe.
var countItems = requestColumn.map(x => x.toString()).groupByKey(x => x).count;
countItems.show();

// Save dataset as CSV in local //coalesce(1) - to combine part files - usually write action writes files in multiple part files.
countItems.coalesce(1).write.csv("file:///D:/SparkOutput/RequestCountCSV");
