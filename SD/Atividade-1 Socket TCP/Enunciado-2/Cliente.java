import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Cliente {
    public static void main (String args[]) {
        Socket clientSocket = null;
        
        try {
            int serverPort = 6666;
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            clientSocket = new Socket(serverAddress, serverPort);

            DataInputStream in = new DataInputStream( clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());

        } catch(IOException e) {
            System.out.println(e);
        }

    }

    public static byte[] CreateSolicitacao(int MensageType, int ComandIdent, int FileNameSize, String FileName) {
        
        byte[] Solicitacao = new byte[267];

        byte[] msgT_bytes = ByteBuffer.allocate(4).putInt(MensageType).array();
        System.arraycopy(msgT_bytes, 0, Solicitacao, 0, 4);

        byte[] CmdTp_bytes = ByteBuffer.allocate(4).
        
        return null; 
    }
}
