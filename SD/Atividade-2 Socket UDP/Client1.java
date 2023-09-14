import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;


public class Client1 { 
    
    public static void main(String[] args) {
        String nick = "Guilherme";
        String mensagem = "oi bom dia";

        byte[] cabecalho = create_mensage(nick, mensagem);
        
        DatagramSocket dgramSocket;

        try {
            dgramSocket = new DatagramSocket(6661);

            InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
            int serverPort = 6666;

            DatagramPacket request = new DatagramPacket(cabecalho, cabecalho.length, serverAddr, serverPort);

            dgramSocket.send(request);

        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }

    }
    
    
    public static byte[] create_mensage(String nome, String msg) {

        byte[] cacalho = new byte[331];

        byte[] nick = nome.getBytes(); 
        if (nick.length > 64) {
            System.out.println("Nome fornecido é maior que o permitido: " + nick.length);
            return cacalho;
        }

        byte[] mensagem = msg.getBytes();
        if (mensagem.length > 256) {
            System.out.println("Mensagem fornecido é maior que o permitido");
            return cacalho;
        }

        byte[] tipo_mensagem = ByteBuffer.allocate(4).putInt(1).array();
        byte[] nick_size = ByteBuffer.allocate(4).putInt(nick.length).array();
        byte[] mensagem_size = ByteBuffer.allocate(4).putInt(mensagem.length).array();

        System.arraycopy(tipo_mensagem, 0, cacalho, 0, tipo_mensagem.length);
        System.arraycopy(nick_size, 0, cacalho, 4, nick_size.length);
        System.arraycopy(nick, 0, cacalho, 8, nick.length);
        System.arraycopy(mensagem_size, 0, cacalho, 72, mensagem_size.length);
        System.arraycopy(mensagem, 0, cacalho, 76, mensagem.length);
        
        return cacalho;
    }

}