import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static int port;

    public static void main(String[] args) {
        if (args.length == 1){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Wrong number format");
                return;
            }
        }
        while (true) {
            try {
                System.out.println("<------SELECT AN OPTION------>");
                System.out.println("1. BLACKLIST A HOST OR IP ADDRESS");
                System.out.println("2. VIEW BLACKLIST");
                System.out.println("3. RUN PROXY SERVER");
                System.out.println("4. EXIT");
                System.out.print("OPTION---->");
                Scanner s = new Scanner(System.in);
                int option = Integer.parseInt(s.nextLine());
                if (option == 1) {
                    System.out.println("Please enter website to be blacklisted: ");
                    String record = String.valueOf(s.nextLine());
                    if (record.contains("http://")) {
                        record = record.replace("http://", "");
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
                else if (option == 2){
                    Blacklist.blacklist.clear();
                    Blacklist.update();
                    Blacklist.print();
                }
                else if (option == 3) {
                    System.out.println("Enter working port as parameter");
                    port = Integer.parseInt(s.nextLine());
                    System.out.println("Server is working on localhost:" + port + "\n");

                    try (ServerSocket server = new ServerSocket(port)) {

                        System.out.println("Blacklist:");
                        Blacklist.blacklist.clear();
                        Blacklist.update();
                        Blacklist.print();

                        ErrorPage.load();

                        while (true) {
                            Socket socket = server.accept();

                            Thread proxyThread = new Thread(new ProxyConnection(socket));
                            proxyThread.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    s.close();
                    return;
                }
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
