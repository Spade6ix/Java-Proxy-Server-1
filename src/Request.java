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

    private final Vector<String> requestLines = new Vector<>(0);

    public Vector<String> getRequestLines() {
        return requestLines;
    }

    public Request(BufferedReader reader) throws IOException {
        String requestLine;
        while ((requestLine = reader.readLine()) != null && !requestLine.equals("")) {
            requestLines.add(requestLine);
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
        if (!Blacklist.findBlocked(host, path)) {
            InetAddress address = InetAddress.getByName(host);

            Socket socket = new Socket(address, port);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            Journal.add(Thread.currentThread().getId(), "Sending " + requestLines.elementAt(0));

            for (String line : requestLines) {
                if (line.contains("Host:")) {
                    Journal.add(Thread.currentThread().getId(), "\t\t\t" + line);
                }
                send(line, outputStream);
            }
            send("", outputStream);

            return new Response(inputStream, outputStream, socket);
        } else {
            Journal.add(Thread.currentThread().getId(), " Blocked " + requestLines.elementAt(0));

            String errorPage = "HTTP/1.1 403 Forbidden\r\n\r\n" + ErrorPage.getHtmlString(host + path);

            InputStream is = new ByteArrayInputStream(errorPage.getBytes());
            DataInputStream inputStream = new DataInputStream(is);
            return new Response(inputStream, null, null);
        }
    }

    private void send(String line, DataOutputStream stream) throws IOException {
        stream.writeBytes(line + "\r\n");
        stream.flush();
    }

    public String getRequestMethod() {
        if (requestLines.size() > 0) {
            String[] split = requestLines.elementAt(0).split(" ");
            return split[0];
        }
        return "";
    }
}
