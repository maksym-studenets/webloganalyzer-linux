package model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model class for Apache access log
 */
public class AccessLog {
    private static final Logger logger = Logger.getLogger("AccessLog");

    private static final String LOG_ENTRY_PATTERN =
            "\\\\[([\\\\w:/]+\\\\s[+\\\\-]\\\\d{4})\\\\] (\\\\S) \\\\[(\\\\w+) ([\\\\d.]+) ([\\\\d.]+)" +
                    " (\\\\w+) (\\\\w+.*)\\\\] (\\\\w.*?\\\\d{3}) ([\\\\d.]+) \\\"([^\\\"]*)\\\" \\\"" +
                    "([^\\\"]*)\\\" (\\\\S) (\\\\w+) (.*)";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

    private String ipAddress;
    private String clientId;
    private String userId;
    private String dateTimeString;
    private String method;
    private String endpoint;
    private String protocol;
    private int responseCode;
    private long contentSize;

    /**
     * Converts every line of the log file into a new {@code AccesLog} object
     * @param logLine String representation of an individual log line
     *                @return new {@code AccessLog} object with fields initialized
     *                by the values that were parsed using {@link Matcher}
     * */
    public static AccessLog parseLog(String logLine) throws IOException {
        Matcher matcher = PATTERN.matcher(logLine);
        try {
            if (!matcher.find()) {
                logger.log(Level.ALL, "Error parsing line{0}: ", logLine);
                throw new IOException("Error parsing log line");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.ALL, "Error ---- ");
            return null;
        }

        return new AccessLog(matcher.group(1), matcher.group(2), matcher.group(3),
                matcher.group(4), matcher.group(5), matcher.group(6),
                matcher.group(7), matcher.group(8), matcher.group(9));
    }

    private AccessLog(String ipAddress, String clientId, String userId,
                      String dateTimeString, String method, String endpoint,
                      String protocol, String responseCode, String contentSize) {
        this.ipAddress = ipAddress;
        this.clientId = clientId;
        this.userId = userId;
        this.dateTimeString = dateTimeString;
        this.method = method;
        this.endpoint = endpoint;
        this.protocol = protocol;
        this.responseCode = Integer.parseInt(responseCode);
        if (contentSize.equals("-")) {
            this.contentSize = 0;
        } else {
            this.contentSize = Long.parseLong(contentSize);
        }
    }

    /**
     * Represents current {@code AccessLog} object in a {@link String} format.
     * @return new {@link String} that displays all the properties of the object in
     * sequential order
     * */
    @Override
    public String toString() {
        return String.format("%s %s %s [%s] \"%s %s %s\" %s %s",
                ipAddress, clientId, userId, dateTimeString, method, endpoint,
                protocol, responseCode, contentSize);
    }


    /**
     * Returns IP address of the current object
     * @return IP address of the client
     * */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets an IP address for the current object to the value provided as a parameter
     * @param ipAddress String representing a 32-bit IPv4 address
     * */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns a {@link String} that represents date and time of the event as provided by the log.
     * Date is represented in the following format DD/MMM/YYYY:HH:MM::SS +offset
     * @return String representing date of the current log object
     * */
    public String getDateTimeString() {
        return dateTimeString;
    }

    /**
     * Sets a date to the current log object in the format DD/MMM/YYYY:HH:MM::SS +offset
     * @param dateTimeString date String that will be set as a record's date
     * */
    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }
}
