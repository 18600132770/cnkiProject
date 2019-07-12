package com.cnki.spark.sql;

import static org.apache.spark.sql.functions.col;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.RelationalGroupedDataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import scala.Tuple2;

public class JavaSparkSQL {
	
	public static class Person implements Serializable{
		private String name;
		private int age;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		
	}
	
	public static void main(String[] args) throws AnalysisException {
		
		SparkSession spark = SparkSession
				.builder()
				.appName("JavaSparkSQL")
				.master("local")
				.getOrCreate();
		
		
//		runBasicDataFrameExample(spark);
//		runDatasetCreationExample(spark);
		runInferSchemaExample(spark);
		
	}
	
	
	
	private static void runBasicDataFrameExample(SparkSession spark) throws AnalysisException{
		
		Dataset<Row> dataset = spark.read().json("src/resources/people.json");
		
		dataset.show();
		
		dataset.printSchema();
		
		Row head = dataset.head();
		System.out.println(head);
		
		Row first = dataset.first();
		System.out.println(first);
		
		String[] columns = dataset.columns();
		for (String string : columns) {
			System.out.print(string+"\t");
		}
		
		System.out.println(dataset.count());
		
		Dataset<Row> select = dataset.select("name");
		select.show();
		
		dataset.select(col("name"), col("age").plus(1)).show();
		
		dataset.select(col("name"), col("age").isNotNull()).show();
		
		dataset.select(col("name"), col("age").gt(20)).show();
		
		dataset.filter(col("age").gt(25)).show();
		
		dataset.withColumn("gender", col("huag"));
		
		System.out.println("-------------------------");
		dataset.show();
		
		
		JavaRDD<Row> javaRDD = dataset.javaRDD();
		JavaRDD<Tuple2<Object, Object>> rdd = javaRDD.map(row -> new Tuple2<>(row.get(0),row.get(1)));
		
		rdd.foreach(tuple -> System.out.println(tuple._1+ "\t" + tuple._2));
		
		RelationalGroupedDataset groupBy = dataset.groupBy(col("age"));
		System.out.println(groupBy.count());
		
		dataset.groupBy("age").count().show();
		
		dataset.createOrReplaceTempView("person");
		
		Dataset<Row> sql = spark.sql("select * from person");
		sql.show();
		
		
		dataset.createGlobalTempView("person");
		
		spark.sql("select name from global_temp.person").show();
		
		spark.newSession().sql("select * from global_temp.person").show();
		
	}
	
	
	public static void runDatasetCreationExample(SparkSession spark){
		
		Person person = new Person();
		person.setAge(26);
		person.setName("huag");
		
		 // Encoders are created for Java beans
		Encoder<Person> personEncoder = Encoders.bean(Person.class);
		Dataset<Person> createDataset = spark.createDataset(
				Collections.singletonList(person), 
				personEncoder);
		
		createDataset.show();
		
		// Encoders for most common types are provided in class Encoders
		Encoder<Integer> integerEncoder = Encoders.INT();
		Dataset<Integer> createDataset2 = spark.createDataset(Arrays.asList(1,2,3,4,5), integerEncoder);
		
//		createDataset2.map((MapFunction<Integer, Integer>)value -> value*2, integerEncoder).foreach(value -> System.out.println(value));
		
		String path = "src/resources/people.json";
		
		Dataset<Person> as = spark.read().json(path).as(personEncoder);
		as.show();
		
	}
	
	
	public static void runInferSchemaExample(SparkSession spark){
		
		JavaRDD<Person> rdd = spark.read().textFile("src/resources/people.txt").javaRDD().map(line -> {
			Person person = new Person();
			person.setName(line.split(",")[0]);
			person.setAge(Integer.valueOf(line.split(",")[1].trim()));
			return person;
		});
		
		Dataset<Row> dataFrame = spark.createDataFrame(rdd, Person.class);
		
		dataFrame.createOrReplaceTempView("people");
		
		Dataset<Row> dataset = spark.sql("select * from people where age between 13 and 20");
		
		dataset.show();
		
		Encoder<String> stringEncoder = Encoders.STRING();
		dataset.map((MapFunction<Row, String>)row -> "name: "+ row.getString(1) , stringEncoder).show();
		
		dataset.map((MapFunction<Row, String>)row-> "name: "+row.getAs("name"), stringEncoder).show(); 
		
		Encoder<Integer> integerEncoder = Encoders.INT();
		
		dataset.map((MapFunction<Row, Integer>) row -> row.getAs("age"), integerEncoder).show();
		
	}
	
	
	public static void runProgrammaticSchemaExample(SparkSession spark){
		
		JavaRDD<String> rdd = spark.sparkContext().textFile("src/resources/people.txt", 1).toJavaRDD();
		
		List<StructField> fields = new ArrayList<StructField>();
		
		fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
		fields.add(DataTypes.createStructField("age", DataTypes.StringType, true));
		
		StructType schema = DataTypes.createStructType(fields);
		
		JavaRDD<Row> map = rdd.map((Function<String, Row>) line -> {
			String[] splits = line.split(",");
			return RowFactory.create(splits[0], splits[1].trim());
		});
		
		
		Dataset<Row> dataFrame = spark.createDataFrame(map, schema);
		
		dataFrame.createOrReplaceTempView("people");
		
		spark.sql("select * from people").show();
		
		dataFrame.map((MapFunction<Row, String>) row-> {
			return "name: "+ row.getAs("name");
		}, Encoders.STRING()).show();
		
		
	}
	
	
	
	
	
	
	
	
	
}
