package com.ab;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import com.datastax.spark.connector.japi.SparkContextJavaFunctions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class App {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("Simple Application")
                .setMaster("local")
                .set("spark.cassandra.connection.host", "127.0.0.1");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SparkContextJavaFunctions cassandraFunctions = CassandraJavaUtil.javaFunctions(sc);

        JavaRDD<CassandraRow> employeeRdd = cassandraFunctions
                .cassandraTable("my_cassandra_keyspace", "employee");
        print(employeeRdd);

        JavaRDD<CassandraRow> departmentRdd = cassandraFunctions
                .cassandraTable("my_cassandra_keyspace", "department");
        print(departmentRdd);

        JavaPairRDD<Integer, Tuple2<Integer, CassandraRow>> departmentIdToEmployeePairs = employeeRdd
                .mapToPair((CassandraRow row) -> new Tuple2(row.getInt("dep_id"), row));
        JavaPairRDD<Integer, Tuple2<Integer, CassandraRow>> idToDeparmentPairs = departmentRdd
                .mapToPair((CassandraRow row) -> new Tuple2(row.getInt("id"), row));

        JavaRDD<Tuple2<Tuple2<Integer, CassandraRow>, Tuple2<Integer, CassandraRow>>> employeeWithDeparment =
                departmentIdToEmployeePairs.join(idToDeparmentPairs).values();
        print(employeeWithDeparment);

        sc.stop();
    }

    private static void print(JavaRDD<?> rdd) {
        String str = rdd
                .map(Object::toString)
                .reduce((s1, s2) -> s1 + '\n' + s2);
        System.out.println(str);
    }

}
