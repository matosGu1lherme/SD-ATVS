import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.math.BigInteger;


public class ClientTCP {
    public static void main (String args[]) {
        Socket clientSocket = null;
        Scanner reader = new Scanner(System.in);
        
        try {
            int serverPort = 6666;
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
     
            clientSocket = new Socket(serverAddress, serverPort);

            DataInputStream in = new DataInputStream( clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());

            String buffer = "";
            while (true) {
                System.out.println("Entre com CONNECT user password:");
                buffer = reader.nextLine();
    
                String[] bufferCmd = buffer.split(" ");

                if (bufferCmd[0].equals("CONNECT")) {
                    String hashPw = getSHA512(bufferCmd[2]);
    
                    out.writeUTF(bufferCmd[1] + " " + hashPw);
                    buffer = in.readUTF();
                    System.out.println("CONNECT: " + buffer);

                    if (buffer.equals("SUCCESS")){
                        break;
                    }
                }
            }

            while (true) {
                buffer = reader.nextLine();

                String[] bufferCmd = buffer.split(" ");
                
                if (bufferCmd[0].equals("PWD")) {
                    out.writeUTF("PWD");
                    System.out.println(in.readUTF());
                }

                if (bufferCmd[0].equals("CHDIR")) {
                    buffer = buffer.replaceFirst("CHDIR ", "");
                    out.writeUTF("CHDIR "+buffer);
                    System.out.println(in.readUTF());
                }

                if (bufferCmd[0].equals("GETFILES")) {
                    out.writeUTF("GETFILES");
                    System.out.println(in.readUTF());
                }

                if (bufferCmd[0].equals("GETDIRS")) {
                    out.writeUTF("GETDIRS");
                    System.out.println(in.readUTF());
                }

                 if (bufferCmd[0].equals("EXIT")) {
                    out.writeUTF("EXIT");
                    System.out.println(in.readUTF());
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static String getSHA512(String input) {
        String output = null;
        try {
            // Obtém uma instância do algoritmo SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // Calcula o hash da string
            byte[] digest = md.digest(input.getBytes("UTF-8"));

            // Converte o array de bytes em uma string hexadecimal
            BigInteger bigInt = new BigInteger(1, digest);
            output = bigInt.toString(16);

            // Preenche com zeros à esquerda se necessário
            while (output.length() < 32) {
                output = "0" + output;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
