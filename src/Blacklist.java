import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Blacklist {
    private static String blacklistFilePath = "blacklist.txt";

    public static void setBlacklistFilePath(String blacklistFilePath) {
        Blacklist.blacklistFilePath = blacklistFilePath;
    }

    private static class BannedAdress {
        public String path = "/";
        public String host = "";
    }

    private static  ArrayList<BannedAdress> blacklist = new ArrayList<>();

    public void update() {
        try {
            Scanner scanner = new Scanner(new File(blacklistFilePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                BannedAdress address = new BannedAdress();
                if (line.contains("/")) {
                    int slashPos = line.indexOf('/');
                    address.host = line.substring(0, slashPos);
                    address.path = line.substring(slashPos);
                } else {
                    address.host = line;
                }
                blacklist.add(address);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void print() {
        for (int i = 0; i < blacklist.size(); i++) {
            System.out.println(blacklist.get(i).host + blacklist.get(i).path);
        }
    }

    public static boolean findBlocked(String host, String path) {
        for (BannedAdress address : blacklist) {
            if (address.host.equals(host) && address.path.equals(path)) {
                return true;
            }
        }
        return false;
    }
}
