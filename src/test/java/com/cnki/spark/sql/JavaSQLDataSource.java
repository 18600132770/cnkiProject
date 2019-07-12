package com.cnki.spark.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class JavaSQLDataSource {
	
	public static class Square implements Serializable{
		private int value;
		private int square;
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public int getSquare() {
			return square;
		}
		public void setSquare(int square) {
			this.square = square;
		}
	}
	
	public static class Cube implements Serializable{
		private int value;
		private int cube;
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public int getCube() {
			return cube;
		}
		public void setCube(int cube) {
			this.cube = cube;
		}
	}
	
	public static void main(String[] args) {
		
		SparkSession spark = SparkSession
			.builder()
			.appName("JavaSQLDataSource")
			.getOrCreate();
		
		runBasicDataSourceExample(spark);
		runBasicParquetExample(spark);
		runParquetSchemaMergingExample(spark);
		runJsonDatasetExample(spark);
		runJdbcDatasetExample(spark);
		
		spark.stop();
		
	}
	
	private static void runBasicDataSourceExample(SparkSession spark){
		
		Dataset<Row> usersDF = spark.read().load("src/resources/users.parquet");
		usersDF.select("name", "favorite_color").write().save("namesAndFavColors.parquet");
		
		Dataset<Row> peopleDF = spark.read().format("json").load("src/resources/people.json");
		
		peopleDF.select("name", "age").write().format("parquet").save("namesAndAges.parquet");
		
		Dataset<Row> sqlDF = spark.sql("select * from parquet.`src/resources/users.parquet`");
		
		peopleDF.write().bucketBy(42, "name").sortBy("age").saveAsTable("people_bucketed");
		
		usersDF
			.write()
			.partitionBy("favorite_color")
			.format("parquet")
			.save("namesPartByColor.parquet");
		
		peopleDF
			.write()
			.partitionBy("favorite_color")
			.bucketBy(42, "name")
			.saveAsTable("people_partitioned_bucketed");
		
		spark.sql("DROP TABLE IF EXISTS people_bucketed");
		spark.sql("DROP TABLE IF EXISTS people_partitioned_bucketed");
	}
	
	private static void runBasicParquetExample(SparkSession spark){
		
		Dataset<Row> peopleDF = spark.read().json("examples/src/main/resources/people.json");
		peopleDF.write().parquet("people.parquet");
		
		Dataset<Row> parquetFileDF = spark.read().parquet("people.parquet");
		
		parquetFileDF.createOrReplaceTempView("parquetFile");
		Dataset<Row> namesDF = spark.sql("SELECT name FROM parquetFile WHERE age BETWEEN 13 AND 20");
		
		namesDF.map((MapFunction<Row, String>) row -> "Name: " + row.getString(0),  Encoders.STRING());
		
		namesDF.show();
	}
	
	private static void runParquetSchemaMergingExample(SparkSession spark){
		
		List<Square> squares = new ArrayList<>();
		for (int value = 1; value <=5; value++){
			Square square = new Square();
			square.setValue(value);
			square.setSquare(value * value);
			squares.add(square);
		}
		
		Dataset<Row> squareDF = spark.createDataFrame(squares, Square.class);
		squareDF.write().parquet("data/test_table/key=1");
		
		List<Cube> cubes = new ArrayList<>();
		for (int value = 6; value < 10; value++) {
			Cube cube = new Cube();
			cube.setValue(value);
			cube.setCube(value * value * value);
			cubes.add(cube);
		}
		
		Dataset<Row> cubeDF = spark.createDataFrame(cubes, Cube.class);
		cubeDF.write().parquet("data/test_table/key=2");
		
		Dataset<Row> mergedDF = spark.read().option("mergeSchema", true).parquet("data/test_table");
		mergedDF.printSchema();
		
	}
	
	private static void runJsonDatasetExample(SparkSession spark){
		
		Dataset<Row> people = spark.read().json("examples/src/main/resources/people.json");
		
		people.printSchema();
		
		people.createOrReplaceTempView("people");
		
		Dataset<Row> namesDF = spark.sql("SELECT name FROM people WHERE age BETWEEN 13 AND 20");
		
		namesDF.show();
		
		List<String> jsonData = Arrays.asList("{\"name\":\"Yin\",\"address\":{\"city\":\"Columbus\",\"state\":\"Ohio\"}}");
		
		Dataset<String> anotherPeopleDataset = spark.createDataset(jsonData, Encoders.STRING());
		Dataset<Row> anotherPeople = spark.read().json(anotherPeopleDataset);
		anotherPeople.show();
		
	}
	
	private static void runJdbcDatasetExample(SparkSession spark){
		
		Dataset<Row> jdbcDF = spark.read()
			.format("jdbc")
			.option("url", "jdbc:postgresql:dbnserver")
			.option("dbtable", "schema.tablename")
			.option("user", "username")
			.option("password", "password")
			.load();
		
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "username");
		connectionProperties.put("password", "password");
		Dataset<Row> jdbcDF2 = spark.read()
			.jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties);
		
		jdbcDF.write()
			.format("jdbc")
			.option("url", "jdbc:postgresql:dbserver")
			.option("dbtable", "schema.tablename")
			.option("user", "username")
			.option("password", "password")
			.save();
		
		jdbcDF2.write().jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties);
		
		jdbcDF.write().option("createTableColumnTypes", "name CHAR(64), comments VARCHAR(1024)")
			.jdbc("jdbc:postgresql:dbserver", "schema.tablename", connectionProperties);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
