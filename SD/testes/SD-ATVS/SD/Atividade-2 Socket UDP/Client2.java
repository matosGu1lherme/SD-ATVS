import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.*;

public class Client2 {
 
    public static void main(String[] args) {
        System.out.println("Iniciando Cliente-2");
        DatagramSocket dgramSocket = null;
        try {
            dgramSocket = new DatagramSocket(6661);

            ReciveThread r = new ReciveThread(dgramSocket);
            SendThread s = new SendThread(dgramSocket, 6666);
            r.start();
            s.start();

        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }
    }
}

//-------------------Thread de Recebimento-------------------------//

class ReciveThread extends Thread {

    DatagramSocket dgramSocket;

    public ReciveThread(DatagramSocket dgramSocket) {
        this.dgramSocket = dgramSocket;
    }

    @Override
    public void run() {
        System.out.println("Recive Socket INICIADO");
        while (true){
            try {
                byte[] buffer = new byte[1000];
                DatagramPacket dgramPacket = new DatagramPacket(buffer, buffer.length);
                dgramSocket.receive(dgramPacket);
                
                byte[] mensageType = new byte[4];
                System.arraycopy(dgramPacket.getData(), 0, mensageType, 0, 4);
                int mtype = ByteBuffer.wrap(mensageType).getInt();
                System.out.println(mtype);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


//-------------------Thread de Envio-------------------------//

class SendThread extends Thread {

    DatagramSocket dgramSocket;
    InetAddress serverAddr;
    int serverPort;
    
    public SendThread(DatagramSocket dgramSocket, int serverPort) {
        try {
            this.dgramSocket = dgramSocket;
            serverAddr = InetAddress.getByName("127.0.0.1");
            serverPort = 6666;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("SEND Socket INICIADO");
        Scanner reader = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Cliente-1:");
    
                String buffer = reader.nextLine();
    
                byte[] mensagem = create_mensage("Sergio", buffer);
    
                DatagramPacket request = new DatagramPacket(mensagem, mensagem.length, serverAddr, serverPort);
                dgramSocket.send(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
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