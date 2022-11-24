import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.InputMismatchException;

public class SingleThreadedServer {
    private int listeningPort;
    private DatagramSocket ds;
    private InetAddress internetAddress;
    private int defaultArrayLength = 8192;
    private ZoneId TIMEZONE = ZoneId.of("America/Los_Angeles");
    private ZonedDateTime timeStamp;

    public SingleThreadedServer(int port) throws IOException {
        this.listeningPort = port;
        this.internetAddress = InetAddress.getLocalHost();

        // open a new socket
        ds = new DatagramSocket(listeningPort);

        // start up server log
        System.out.println("SERVER-LOG " + getTimeStamp() + " -> Server started\n");

        // Create a new byte array to use to receive and deserialize incoming packets
        byte[] receiveByteArray = new byte[defaultArrayLength];
        DatagramPacket receiveDP = new DatagramPacket(receiveByteArray, receiveByteArray.length);

        // receive packets from the client, deserialize, log, and perform business logic to obtain response
        while (true) {
            ds.receive(receiveDP);
            String inputMsg = new String(receiveByteArray, 0, receiveDP.getLength());
            String serverLog = logNewRequest(inputMsg);
            System.out.println(serverLog);

            // Serialize the response message object to a byte array so that it can be sent to the client's port in datagram packets
            String responseMsg = "Hello, Client! The Server has recorded your message: " + inputMsg;
            byte[] sendByteArray = responseMsg.getBytes();
            DatagramPacket sendDP = new DatagramPacket(sendByteArray, sendByteArray.length, internetAddress, receiveDP.getPort());

            // Send response message back to client
            ds.send(sendDP);
        }
    }

    // Get TimeStamp
    public ZonedDateTime getTimeStamp(){
        return ZonedDateTime.now(TIMEZONE);
    }

    // Log request from client
    public String logNewRequest(String inputMsg) {
        timeStamp = getTimeStamp();
        String newLogEntry = "SERVER-LOG " + timeStamp + " -> REQUEST received from localhost:" + listeningPort + " Message: " + inputMsg;
        return newLogEntry;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
            int port = Integer.parseInt(args[0]);
            System.out.println("Server listening on port: " + port + " ...");
            SingleThreadedServer newServer = new SingleThreadedServer(port);
        } catch (InputMismatchException ex) {
            System.out.println("SERVER INITIALIZATION ERROR -> " + ex.toString() + " -> Port number must be of numerical value");
        }
    }
}
