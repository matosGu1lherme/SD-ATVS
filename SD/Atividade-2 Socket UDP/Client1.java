import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.io.*;

public class Client1 {
 
    public static void main(String[] args) {
        System.out.println("Iniciando Cliente-2");
        DatagramSocket dgramSocket = null;
        try {
            dgramSocket = new DatagramSocket(6664);

            ReciveThread r = new ReciveThread(dgramSocket, "Douglas");
            SendThread s = new SendThread(dgramSocket, "Douglas");
            r.start();
            s.start();

        }catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        }
    }
}

//-------------------Thread de Recebimento-------------------------//

class ReciveThread extends Thread {

    String nick_def;
    DatagramSocket dgramSocket;
    InetAddress serverAddr;

    public ReciveThread(DatagramSocket dgramSocket, String nickName_def) {
        this.dgramSocket = dgramSocket;
        this.nick_def = nickName_def;
        try {
            serverAddr = InetAddress.getByName("127.0.0.1");
        }catch(UnknownHostException e) {
            System.out.println(e.getMessage());
        }
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

                if(mtype == 2) {
                    System.out.println("ECHO RECEBIDO");
                    byte[] response = SendThread.create_mensage(3, nick_def, "200 ACTIVE");
                    DatagramPacket request = new DatagramPacket(response, response.length, serverAddr, 6663);
                    dgramSocket.send(request);
                }else if(mtype == 3) {
                    byte[] nick_sz = new byte[4];
                    System.arraycopy(dgramPacket.getData(), 4, nick_sz, 0, 4);
                    int nick_sz_int = ByteBuffer.wrap(nick_sz).getInt();
    
                    byte[] nick_bytes = new byte[nick_sz_int];
                    System.arraycopy(dgramPacket.getData(), 8, nick_bytes, 0, nick_sz_int);
                    String nick_name = new String(nick_bytes, "UTF-8");
                    
                    System.out.println(nick_name + " :ATIVO");
                }else {
    
                    byte[] nick_sz = new byte[4];
                    System.arraycopy(dgramPacket.getData(), 4, nick_sz, 0, 4);
                    int nick_sz_int = ByteBuffer.wrap(nick_sz).getInt();
    
                    byte[] nick_bytes = new byte[nick_sz_int];
                    System.arraycopy(dgramPacket.getData(), 8, nick_bytes, 0, nick_sz_int);
                    String nick_name = new String(nick_bytes, "UTF-8");
    
                    byte[] msg_sz_bytes = new byte[4];
                    System.arraycopy(dgramPacket.getData(), 72, msg_sz_bytes, 0, 4);
                    int msg_sz_int = ByteBuffer.wrap(msg_sz_bytes).getInt();
    
                    byte[] msg_bytes = new byte[msg_sz_int];
                    System.arraycopy(dgramPacket.getData(), 76, msg_bytes, 0, msg_sz_int);
                    String msg = new String(msg_bytes, "UTF-8");
                    System.out.println(nick_name + ": " + msg);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] EchoMensage() {
        byte[] response = new byte[4];
        response = ByteBuffer.allocate(4).putInt(200).array();
        return response;
    }
}


//-------------------Thread de Envio-------------------------//

class SendThread extends Thread {

    DatagramSocket dgramSocket;
    InetAddress serverAddr;
    String nick_user;
    
    public SendThread(DatagramSocket dgramSocket, String nick_user) {
        try {
            this.dgramSocket = dgramSocket;
            serverAddr = InetAddress.getByName("127.0.0.1");
            this.nick_user = nick_user;
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
                String buffer = reader.nextLine();
                System.out.println("Voce: " + buffer);

                byte[] mensagem = new byte[331];
                if(buffer.equals("ECHO")) {
                    mensagem = create_mensage(2, this.nick_user, buffer);
                } else {
                    mensagem = create_mensage(1, this.nick_user, buffer);
                }

                DatagramPacket request = new DatagramPacket(mensagem, mensagem.length, serverAddr, 6663);
                dgramSocket.send(request);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] create_mensage(int msgType, String nome, String msg) {

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

        byte[] tipo_mensagem = ByteBuffer.allocate(4).putInt(msgType).array();
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