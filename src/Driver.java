import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Driver {
    private static int port;

    public static void main(String[] args) {
        while (true) {
            try {
            	//Displays a menu to users
                System.out.println("<------SELECT AN OPTION------>");
                System.out.println("1. BLACKLIST A HOST OR IP ADDRESS");
                System.out.println("2. VIEW BLACKLIST");
                System.out.println("3. RUN PROXY SERVER");
                System.out.println("4. EXIT");
                System.out.print("OPTION---->");
                Scanner s = new Scanner(System.in);
                int option = Integer.parseInt(s.nextLine());	//converts user input to type Integer and stores as option
                if (option == 1) {
                    System.out.println("Please enter website to be blacklisted: ");
                    String record = String.valueOf(s.nextLine()); //accepts user input as type String and saves it in a variable 
                    if (record.contains("http://")) {		
                        record = record.replace("http://", ""); 	//removes "http://" from URLs entered and add them to the blacklist file
                        Blacklist.addToFile(record);
                    }
                    else if (record.isBlank()) {
                        System.err.println("ERROR: Nothing entered");
                    } else if (record.contains("https://")) {
                        System.err.println("ERROR: Proxy cannot handle https traffic!!");
                    }
                    else{
                        Blacklist.addToFile(record);
                    }
                }
                else if (option == 2){		//Option to allow users to view blacklisted URLs
                    Blacklist.blacklist.clear();
                    Blacklist.update();
                    Blacklist.print();
                }
                else if (option == 3) {		
                    System.out.println("Enter port to be used by Proxy-->");
                    port = Integer.parseInt(s.nextLine());
                    System.out.println("Server is working on localhost:" + port + "\n");

                    //Creates a server socket to listen on the port specified by the user
                    try (ServerSocket server = new ServerSocket(port)) { 
                    	//Prints a list of blacklisted URLS
                        System.out.println("Blacklist:");
                        Blacklist.blacklist.clear();
                        Blacklist.update();
                        Blacklist.print();

                        BlockedPage.load();

                        while (true) {
                        	//Accepts connection to the server
                            Socket socket = server.accept();

                            //Creates a new thread to run an instance of the socket
                            Thread proxyThread = new Thread(new ProxyHandler(socket));
                            proxyThread.start();
                        }
                    } catch (IOException e) {
                        System.err.println("IOException occurred");
                        e.printStackTrace();
                    }
                } else {
                    s.close();
                    return;
                }
                s.close();
            }
            catch(InputMismatchException x){
                System.err.println("Input MisMatch Exception Occurred");
                x.printStackTrace();
            }
            catch(Exception x){
                System.err.println("Exception Occurred");
                x.printStackTrace();
            }
        }
    }
}
