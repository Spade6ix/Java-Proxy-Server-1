import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ProxyConnection implements Runnable{
    private Socket socket;

    public ProxyConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            Request request = new Request(reader);

            if (!request.getRequestMethod().equals("CONNECT")) {
                String requestLine = request.getRequestLines().elementAt(0);
                Journal.add(Thread.currentThread().getId(), "Received " + requestLine);
                for (String line : request.getRequestLines()) {
                    if (line.contains("Host:")) {
                        Journal.add(Thread.currentThread().getId(), "\t\t\t" + line);
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

        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
