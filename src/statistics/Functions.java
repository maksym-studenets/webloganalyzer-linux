package statistics;

import org.apache.spark.api.java.function.Function2;

/**
 * Defines functions that are used to mine data
 */
public class Functions {
    public static Function2<Long, Long, Long> SUM_REDUCER = (a, b) -> a + b;
}
