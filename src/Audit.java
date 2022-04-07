import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Audit {
    private static final Logger logger = LogManager.getLogger(Audit.class);

    /***Method to record each thread created ***/
    public static void record(long threadID, String line) {
        String record = "-> THREAD :" + String.valueOf(threadID) + " -> " + line;
        logger.trace(record);
        System.out.println(record);
    }
}
