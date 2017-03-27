package statistics;

import model.AccessLog;
import model.TrafficInfo;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.Comparator;
import java.util.List;

/**
 * Performs basic log insight operations
 */
public class LogInsights {
    private JavaRDD<AccessLog> accessLogs;
    private JavaRDD<Long> trafficData;
    private JavaRDD<String> logLines;


    public LogInsights() {
        accessLogs = null;
        trafficData = null;
        logLines = null;
    }


    public List<String> getIpStats(int quantity) {
        convertToAccessLog();
        List<String> ipAddresses;
        try {
            ipAddresses =
                    accessLogs.mapToPair(log -> new Tuple2<>(log.getIpAddress(), 1L))
                            .reduceByKey(Functions.SUM_REDUCER)
                            .filter(tuple -> tuple._2() > quantity)
                            .map(Tuple2::_1)
                            .take(100);
            //System.out.println(String.format("IPAddresses > " + quantity + "times: %s", ipAddresses));
            return ipAddresses;
        } catch (Exception e) {
            System.out.println("Something happened....");
            System.out.println("Stack trace: ");
            e.printStackTrace();
            return null;
        }
    }

    public TrafficInfo getTrafficStatistics() {
        trafficData = accessLogs.map(AccessLog::getContentSize).cache();
        long average = trafficData.reduce(Functions.SUM_REDUCER) / trafficData.count();
        long maximum = trafficData.max(Comparator.naturalOrder());
        long minimum = trafficData.min(Comparator.naturalOrder());

        return new TrafficInfo(average, maximum, minimum);
    }

    public JavaRDD<String> getLogLines() {
        return logLines;
    }

    public void setLogLines(JavaRDD<String> logLines) {
        this.logLines = logLines;
    }

    private void convertToAccessLog() {
        accessLogs = logLines.map(AccessLog::parseLog).cache();
    }
}
