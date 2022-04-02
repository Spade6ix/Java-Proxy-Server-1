import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Journal {
    private static final Logger logger = LogManager.getLogger(Journal.class);

    public static void add(long threadID, String line) {
        String record = " > On THREAD :" + String.valueOf(threadID) + " > " + line;
        logger.trace(record);
        System.out.println(record);
    }
}
