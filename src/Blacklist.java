import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Blacklist {
    public static class BannedURL {
        public String path = "/";
        public String host = "";
    }

    public static  ArrayList<BannedURL> blacklist = new ArrayList<>();

    public static void update() {
        try {
            File f = new File("blacklist.txt");
            Scanner scanner = new Scanner(f);
            String line;
            scanner.useDelimiter("\n");
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                BannedURL address = new BannedURL();
                if (line.contains("/")) {
                    int slashPos = line.indexOf('/');
                    address.host = line.substring(0, slashPos);
                    address.path = line.substring(slashPos);
                } else {
                    address.host = line;
                }
                blacklist.add(address);
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException occurred");
            e.printStackTrace();
        }
        catch(Exception x){
            System.err.println("Exception occurred");
            x.printStackTrace();
        }
    }

    public static void addToFile(String record){
        try{
            FileWriter fw = new FileWriter("blacklist.txt",true);
            fw.write(record+"\n");
            fw.flush();
            System.out.println("Record added to Blacklist");
            fw.close();
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
        catch(Exception x){
            System.err.println("Exception occurred");
            x.printStackTrace();
        }
    }

    public static void print() {
        for (BannedURL i : blacklist) {
            System.out.println(i.host + i.path);
        }
    }

    public static boolean isBlocked(String host, String path) {
        for (BannedURL address : blacklist) {
            if (address.host.equals(host)) {
                return true;
            }
        }
        return false;
    }

}
