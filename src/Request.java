import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

public class Request {
    private int port = 80;
    private String host = "";
    private String path = "";
    public Response response;

    private final Vector<String> requestLines = new Vector<>(0);

    public Vector<String> getRequestLines() {
        return requestLines;
    }

    public Request(BufferedReader reader) {
        try {
            String requestLine;
            while ((requestLine = reader.readLine()) != null && !requestLine.equals("")) {
                requestLines.add(requestLine);
            }
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
    }

    public void changeRequest() {
        if (requestLines.size() > 0) {
            String requestLine = requestLines.elementAt(0);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String absolutePath = parts[1];
            String version = parts[2];

            try {
                URL url = new URL(absolutePath);
                path = url.getPath();
                host = url.getHost();
                if (url.getPort() != -1) {
                    port = url.getPort();
                }
            } catch (MalformedURLException e) {
                System.err.println("MalformedURLException occurred");
                e.printStackTrace();
            }

            requestLine = method + " " + path + " " + version;

            requestLines.set(0, requestLine);
            requestLines.add("Connection: close");

            for (int i = 0; i < requestLines.size(); i++) {
                if (requestLines.elementAt(i).contains("Accept-Encoding:")) {
                    requestLines.set(i, "Accept-Encoding: identity");
                }
            }
        }
    }

    public Response sendToHost() throws IOException {
        if (!(Blacklist.isBlocked(host, path))) {
            InetAddress address = InetAddress.getByName(host);

            Socket socket = new Socket(address, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            Audit.record(Thread.currentThread().getId(), "Sending " + requestLines.elementAt(0));

            for (String line : requestLines) {
                if (line.contains("Host:")) {
                    Audit.record(Thread.currentThread().getId(), "\t\t\t" + line);
                }
                send(line, outputStream);
            }
            send("", outputStream);
            response = new Response(inputStream, outputStream, socket);
        } else {
            Audit.record(Thread.currentThread().getId(), " Blocked " + requestLines.elementAt(0));

            String errorPage = "HTTP/1.1 403 Forbidden\r\n\r\n" + BlockedPage.getHtmlString(host + path);

            InputStream is = new ByteArrayInputStream(errorPage.getBytes());
            DataInputStream inputStream = new DataInputStream(is);
            response = new Response(inputStream, null, null);
        }
        return response;
    }

    private void send(String line, DataOutputStream stream){
        try {
            stream.writeBytes(line + "\r\n");
            stream.flush();
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
    }

    public String getRequestMethod() {
        if (requestLines.size() > 0) {
            String[] split = requestLines.elementAt(0).split(" ");
            return split[0];
        }
        return "";
    }
}
