import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.io.*;
import java.lang.reflect.Array;

public class Server {
    
    public static void main(String args[]) {
        DataInputStream in;
        DataOutputStream out;
        
        try {
            int serverPort = 6666;

            ServerSocket listenSocket = new ServerSocket(serverPort);
            
            
            while (true) {
                System.out.println("Servidor Aguarndando Conexao ...");
    
                Socket clientSocket = listenSocket.accept();
                
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());

                System.out.println("Iniciando Thread");
                ClientThread c = new ClientThread(clientSocket);
                c.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class ClientThread extends Thread {
    
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public ClientThread(Socket clienSocket) {
        try {
            this.clientSocket = clienSocket;
            in = new DataInputStream(clienSocket.getInputStream());
            out = new DataOutputStream(clienSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connect: " + e.getMessage());
        }
    }

    //Executa ao Rodar c.run()
    @Override
    public void run() {
        int cabecalhoSize = 264;

        
        try {
            byte[] comandIdt = new byte[4];
            byte[] fileSize = new byte[4];
            byte[] nome = new byte[256];
            byte[] buffer = new byte[cabecalhoSize];
            in.readFully(buffer);

            System.arraycopy(buffer, 0, comandIdt, 0, 4);
            System.arraycopy(buffer, 4, fileSize, 0, 4);
            System.arraycopy(buffer, 8, nome, 0, 256);


            int comando = ByteBuffer.wrap(comandIdt).getInt();
            int sz = ByteBuffer.wrap(fileSize).getInt();
            String str = new String(nome, StandardCharsets.UTF_8);

            String nomeFile = str.substring(0, str.indexOf(".")-1);
            String tipo = str.substring(str.indexOf(".")+1, str.indexOf(".")+4);

            byte[] comand = ByteBuffer.allocate(4).putInt(200).array();
            out.write(comand);

            if (comando == 1) { 
                byte[] arq = new byte[sz];
                in.readFully(arq); 
                File arquivo = new File("addFile/"+nomeFile + "." + tipo);

                if (!arquivo.exists()) {
                    FileOutputStream fos = new FileOutputStream(arquivo); // cria um fluxo de saída para o arquivo
                    fos.write(Arrays.copyOfRange(arq, 0, arq.length)); // escreve os bytes no arquivo
                    fos.close(); // fecha o fluxo
                } else {
                    System.out.println("O arquivo já existe!"); // imprime uma mensagem de erro
                }
            }

            
        } catch (IOException e) {
                System.out.println(e.getMessage());
        }
    }
}