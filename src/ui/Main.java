package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AccessLog;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import statistics.Functions;

import java.util.Comparator;
import java.util.List;

public class Main extends Application {

    private static List<String> ipAddresses;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf()
                .setAppName("Web Log Analyzer")
                .setMaster("local[1]");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> logLines = javaSparkContext.textFile(
                "/home/maksym/PROGRAMS/Java/2/webloganalyzer/res/rkc.log");
        JavaRDD<AccessLog> accessLogs = logLines.map(AccessLog::parseLog).cache();

        try {
            ipAddresses =
                    accessLogs.mapToPair(log -> new Tuple2<>(log.getIpAddress(), 1L))
                            .reduceByKey(Functions.SUM_REDUCER)
                            .filter(tuple -> tuple._2() > 10)
                            .map(Tuple2::_1)
                            .take(100);
            System.out.println(String.format("IPAddresses > 10 times: %s", ipAddresses));
        } catch (Exception e) {
            System.out.println("Something happened....");
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }


        JavaRDD<Long> contentSizes =
                accessLogs.map(AccessLog::getContentSize).cache();
        System.out.println(String.format("Content Size Avg: %s, Min: %s, Max: %s",
                contentSizes.reduce(Functions.SUM_REDUCER) / contentSizes.count(),
                contentSizes.min(Comparator.naturalOrder()),
                contentSizes.max(Comparator.naturalOrder())));

        javaSparkContext.stop();

        launch(args);
    }
}
