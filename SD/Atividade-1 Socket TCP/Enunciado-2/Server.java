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
            
            while (true) {
                byte[] Solicitacao = new byte[272];
                in.readFully(Solicitacao);
        
                byte[] comand_bytes = new byte[4];
                System.arraycopy(Solicitacao, 0, comand_bytes, 0, 4);
                int comando = ByteBuffer.wrap(comand_bytes).getInt();
                
          
                if (comando == 1) { 
                    System.out.println("oi");

                    byte[] fileSize_byte = new byte[4];
                    System.arraycopy(Solicitacao, 268, fileSize_byte, 0, 4);
                    int fileSize = ByteBuffer.wrap(fileSize_byte).getInt();
                    System.out.println(fileSize);
                    // byte[] arq = new byte[sz];
                    // in.readFully(arq); 
                    // File arquivo = new File("addFile/"+nomeFile + "." + tipo);
        
                    // if (!arquivo.exists()) {
                    //     FileOutputStream fos = new FileOutputStream(arquivo); // cria um fluxo de saída para o arquivo
                    //     fos.write(Arrays.copyOfRange(arq, 0, arq.length)); // escreve os bytes no arquivo
                    //     fos.close(); // fecha o fluxo
                    // } else {
                    //     System.out.println("O arquivo já existe!"); // imprime uma mensagem de erro
                    // }

                    out.write(CreateResposta(1, 12, 200));
                }
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }     
    }


    public static byte[] CreateResposta(int mensageType, int comandIdt, int status) {

        byte[] resposta = new byte[12];

        byte[] msgT_bytes = ByteBuffer.allocate(4).putInt(mensageType).array();
        System.arraycopy(msgT_bytes, 0, resposta, 0, 4);

        byte[] comand_bytes = ByteBuffer.allocate(4).putInt(comandIdt).array();
        System.arraycopy(comand_bytes, 0, resposta, 4, 4);

        byte[] status_bytes = ByteBuffer.allocate(4).putInt(status).array();
        System.arraycopy(status_bytes, 0, resposta, 8, 4);

        return resposta;
    }
}