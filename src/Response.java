import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Response {

    public final DataInputStream inputStream;
    public final DataOutputStream outputStream;
    public final Socket socket;

    public Response(DataInputStream inputStream, DataOutputStream outputStream, Socket socket) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
    }

    private String getResponse(byte[] bytes, int count) {
        byte[] buffBytes = Arrays.copyOf(bytes, count);
        String buffStr = new String(buffBytes);
        Scanner scanner = new Scanner(buffStr);
        String retLine = "";
        if (scanner.hasNextLine()) {
            retLine = scanner.nextLine();
        }
        scanner.close();
        return retLine;
    }

    public void sendToBrowser(DataOutputStream browserOutputStream){
        try {
            byte[] buffer = new byte[1024];
            int count = inputStream.read(buffer);
            Audit.record(Thread.currentThread().getId(), "Host response > " + getResponse(buffer, count));
            Audit.record(Thread.currentThread().getId(), "\t\t\t" + "Sending data to browser");
            while (count != -1) {
                send(buffer, count, browserOutputStream);
                count = inputStream.read(buffer);
            }
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
    }

    public void closeStreams(){
        try {
            inputStream.close();
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
    }

    private void send(byte[] bytes, int count, DataOutputStream stream){
        try {
            stream.write(bytes, 0, count);
            stream.flush();
        }
        catch(IOException x){
            System.err.println("IOException occurred");
            x.printStackTrace();
        }
    }
}