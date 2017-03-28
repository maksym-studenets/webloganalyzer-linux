package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import statistics.LogInsights;

import java.util.List;


/**
 * Required libraries:
 * <li>Spark Core 2.10</li>
 * <li>Spark Streaming </li>
 * <li>Spark MLib</li>
 * <li>Retrofit 2.2.0</li>
 * <li>Retrofit Converter Gson 2.2.0</li>
 */
public class Main extends Application {

    private static JavaSparkContext javaSparkContext;
    private static List<String> ipAddresses;
    private static final String LOG_PATH = "/home/maksym/PROGRAMS/Java/2/webloganalyzer/res/rkc.txt";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        javaSparkContext = initializeSpark();

        LogInsights insights = new LogInsights();
        insights.setLogLines(javaSparkContext.textFile(LOG_PATH));
        List<String> ipAddressData = insights.getIpStats(50);

        insights.getTrafficStatistics().toString();

        javaSparkContext.stop();

        launch(args);
    }

    private static JavaSparkContext initializeSpark() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("Web Log Analyzer")
                .setMaster("local[2]");
        return new JavaSparkContext(sparkConf);
    }
}
