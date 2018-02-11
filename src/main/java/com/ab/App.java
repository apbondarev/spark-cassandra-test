package com.ab;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class App {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local")
                .set("spark.cassandra.connection.host", "127.0.0.1");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SparkContextJavaFunctions cassandraFunctions = CassandraJavaUtil.javaFunctions(sc);

        JavaRDD<String> cassandraRowsRDD = cassandraFunctions
                .cassandraTable("my_cassandra_keyspace", "employee")
                .map(CassandraRow::toString);

        String concat = cassandraRowsRDD.reduce((s1, s2) -> s1 + '\n' + s2);
        System.out.println("Data as CassandraRows: \n" + concat);

        sc.stop();
    }

}
