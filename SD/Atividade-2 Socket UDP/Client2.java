import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class Client2 {
 
    public static void main(String[] args) {
        DatagramSocket dgramSocket = null;
        try {
            dgramSocket = new DatagramSocket(6666);

            while (true){
                byte[] buffer = new byte[1000];

                DatagramPacket dgramPacket = new DatagramPacket(buffer, buffer.length);
                dgramSocket.receive(dgramPacket);

                byte[] mensageType = new byte[4];
                System.arraycopy(dgramPacket.getData(), 0, mensageType, 0, 4);
                int mtype = ByteBuffer.wrap(mensageType).getInt();
                System.out.println(mtype);
            }
        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
