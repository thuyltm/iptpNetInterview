import java.time.format.DateTimeFormatter;

public class Constant {
    public static final String DATA_SOURCE = "data_java.xml";
    public static final String ROOT_NODE = "bar";
    public static final String RESULT_CSV = "result.csv";
    public static final String SEPARATE = ",";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final String DC_NAME = "dcname";
    public static final String DATA_CENTER = "datacenter";
    public static final String START_DATE = "startdate";
    public static final String END_DATE = "enddate";
    public static final String FREE_TIME = "FreeTime(from:to)";
    public static final String FROM = "from";
    public static final String TO = "to";
}
