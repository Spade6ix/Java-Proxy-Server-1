import java.util.ArrayList;

public class Journal {
    public static void add(long threadID, String line) {
        String record = " > On THREAD :" + String.valueOf(threadID) + " > " + line;
        System.out.println(record);
    }
}
