import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Implements the class Runnable to facilitate multi-threading
public class ProxyHandler implements Runnable{
    private Socket socket;

    public ProxyHandler(Socket socket) {
        this.socket = socket;
    }

    /** Creates a new instance of ProxyHandler that reads and outputs data **/
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
        	Request request = new Request(reader);

            if (!request.getRequestMethod().equals("CONNECT")) {
                String requestLine = request.getRequestLines().elementAt(0);
                Audit.record(Thread.currentThread().getId(), "Received " + requestLine);
                for (String line : request.getRequestLines()) {
                    if (line.contains("Host:")) {
                        Audit.record(Thread.currentThread().getId(), "\t\t\t" + line);
                    }
                }
                request.changeRequest();

                Response response = request.sendToHost();
                try {
                    response.sendToBrowser(outputStream);
                } finally {
                    response.closeStreams();
                }
            }

        } catch (IOException e) {
            System.err.println("IOException occurred");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("IOException occurred");
            e.printStackTrace();
        }
    }
}
