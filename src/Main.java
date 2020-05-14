import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
        } else {
            System.out.println("Enter working port as parameter");
            return;
        }

        System.out.println("Server is working on localhost:" + port + "\n");

        try (ServerSocket server = new ServerSocket(port)){

            System.out.println("Blacklist:");
            Blacklist blacklist = new Blacklist();
            blacklist.update();
            blacklist.print();

            ErrorPage.load();

            while (true) {
                Socket socket = server.accept();

                Thread proxyThread = new Thread(new ProxyConnection(socket));
                proxyThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
