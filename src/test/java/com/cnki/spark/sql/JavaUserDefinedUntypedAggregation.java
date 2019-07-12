package com.cnki.spark.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class JavaUserDefinedUntypedAggregation {
	
	public static class MyAverage extends UserDefinedAggregateFunction{
		private StructType inputSchama;
		private StructType bufferScaham;
		
		public MyAverage(){
			List<StructField> inputFields = new ArrayList<>();
			inputFields.add(DataTypes.createStructField("inputColumn", DataTypes.LongType, true));
			inputSchama = DataTypes.createStructType(inputFields);
			
			List<StructField> bufferFields = new ArrayList<>();
			bufferFields.add(DataTypes.createStructField("sum", DataTypes.LongType, true));
			bufferFields.add(DataTypes.createStructField("count", DataTypes.LongType, true));
			bufferScaham = DataTypes.createStructType(bufferFields);
		}
		

		@Override
		public StructType bufferSchema() {
			return bufferScaham;
		}

		@Override
		public DataType dataType() {
			return DataTypes.DoubleType;
		}

		@Override
		public boolean deterministic() {
			return true;
		}

		@Override
		public Object evaluate(Row buffer) {
			return ((double) buffer.getLong(0)) / buffer.getLong(1);
		}

		@Override
		public void initialize(MutableAggregationBuffer buffer) {
			buffer.update(0, 0L);
			buffer.update(1, 0L);
		}

		@Override
		public StructType inputSchema() {
			return inputSchama;
		}

		@Override
		public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
			long mergedSum = buffer1.getLong(0) + buffer2.getLong(0);
			long mergedCount = buffer1.getLong(1) + buffer2.getLong(1);
			buffer1.update(0, mergedSum);
			buffer1.update(1, mergedCount);
		}

		@Override
		public void update(MutableAggregationBuffer buffer, Row input) {
			if(!input.isNullAt(0)){
				long updatedSum = buffer.getLong(0) + input.getLong(0);
				long updatedCount = buffer.getLong(1) + 1;
				buffer.update(0, updatedSum);
				buffer.update(1, updatedCount);
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		
		SparkSession spark = SparkSession
			.builder()
			.appName("JavaUserDefinedUntypedAggregation")
			.getOrCreate();
		
		spark.udf().register("myAverage", new MyAverage());
		
		Dataset<Row> df = spark.read().json("examples/src/main/resources/employees.json");
		
		df.createOrReplaceTempView("employees");
		df.show();
		
		Dataset<Row> result = spark.sql("SELECT myAverage(salary) as average_salary FROM employees");
		result.show();
		
		spark.stop();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
