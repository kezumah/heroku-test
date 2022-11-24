import java.io.IOException;
import java.net.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.InputMismatchException;
import java.io.IOException;
import java.net.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.InputMismatchException;


public class Client {

    private InetAddress internetAddress;
    private DatagramSocket ds;
    private int defaultArrayLength = 8192;
    private int ServerPort;
    private int timeoutLengthMilliSeconds = 5000;
    private static ZoneId TIMEZONE = ZoneId.of("America/Los_Angeles");
    private static String HOST = "localhost";

    public Client(String IP, int ServerPort) throws SocketException, UnknownHostException {
        this.internetAddress = InetAddress.getByName(IP);
        this.ServerPort = ServerPort;

        // open a new socket and set timeout
        ds = new DatagramSocket();
        ds.setSoTimeout(timeoutLengthMilliSeconds);

    }

    public void sendMsgToServer(String message) throws IOException, ClassNotFoundException {

        // Serialize the message to a byte array so that it can be sent in datagram packets
        byte[] sendByteArray = message.getBytes();
        DatagramPacket sendDP = new DatagramPacket(sendByteArray, sendByteArray.length, internetAddress, ServerPort);

        // Send message
        ds.send(sendDP);

        // Create a new byte array to use to receive and deserialize incoming packets
        byte[] receiveByteArray = new byte[defaultArrayLength];
        DatagramPacket receiveDP = new DatagramPacket(receiveByteArray, receiveByteArray.length);
        ds.receive(receiveDP);

        // read response message
        String responseMsg = new String(receiveByteArray, 0, receiveDP.getLength());
        System.out.println("CLIENT-LOG " + getTimeStamp() + " -> Common.Message returned from server: " + responseMsg);

    }

    // Get TimeStamp
    public static ZonedDateTime getTimeStamp(){
        return ZonedDateTime.now(TIMEZONE);
    }


    public static void main(String[] args) throws IOException, InputMismatchException {
        try {
            String host = HOST;
            int ServerPort = Integer.parseInt(args[0]);
            String message = args[1];

            // define the message object to be serialized and sent to the server
            System.out.println("CLIENT-LOG" + getTimeStamp() + " -> Preparing to send the following message to the server: " + message);

            // send message to server
            Client testClient = new Client(host, ServerPort);
            testClient.sendMsgToServer(message);

        } catch (ConnectException ex) {
            System.out.println("CLIENT-LOG" + getTimeStamp() + " -> CLIENT ERROR -> " + ex.toString() + " -> Server not available at port");
        } catch (
                InputMismatchException ex) {
            System.out.println("CLIENT-LOG" + getTimeStamp() + " -> CLIENT ERROR -> " + ex.toString() + " -> Port number must be of numerical value; input args must be of format: " +
                    "<host> <port> <method> <key> <value>");
        } catch (ClassNotFoundException ex) {
            System.out.println("CLIENT-LOG" + getTimeStamp() + " -> CLIENT ERROR -> " + ex.toString() + " -> Class not found");
        } catch (IOException ex) {
            System.out.println("CLIENT-LOG" + getTimeStamp() + " -> CLIENT ERROR -> " + ex.toString());
        }
    }
}
