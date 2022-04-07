import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Blacklist {
	//Inner class is created to handle the path and host of the URL
    public static class BannedURL {
        public String path = "/";
        public String host = "";
    }
    
    /** Creates an object of an array that stores all the banned URLS **/
    public static  ArrayList<BannedURL> blacklist = new ArrayList<>();

    /*** Updates the arraylist of Banned URLs***/
    public static void update() {
        try {
            File f = new File("blacklist.txt");			//Creates a file object to access the blacklist file
            Scanner scanner = new Scanner(f);			//Creates a scanner object to read from the blacklist file
            String line;
            scanner.useDelimiter("\n");					//Reads the data after each new line is detected
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();				
                BannedURL address = new BannedURL();	
                if (line.contains("/")) {					
                    int slashPos = line.indexOf('/');					//Finds the forward slash in the given URL and returns its position 
                    address.host = line.substring(0, slashPos);			//Between the string found between position 0 and the slash is returned 
                    address.path = line.substring(slashPos);			//The string following the slash is returned
                } else {
                    address.host = line;		
                }
                blacklist.add(address);      //URL is added to the blacklist array
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

    
    /** Add URL to the blacklist file **/
    public static void addToFile(String record){
        try{
            FileWriter fw = new FileWriter("blacklist.txt",true); 	//Creates a file writer object that appends URL to the end of the blacklist file
            fw.write(record+"\n");									//Writes URL to file
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

    /** Prints all URLs stored in the BannedURL arraylist **/
    public static void print() {
        for (BannedURL i : blacklist) {
            System.out.println(i.host + i.path);
        }
    }
    /** Checks if a URL blocked **/
    public static boolean isBlocked(String host, String path) {
        for (BannedURL address : blacklist) {
            if (address.host.equals(host)) {
                return true;
            }
        }
        return false;
    }

}
