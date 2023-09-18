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

            byte[] bufferdelete = new byte[cabecalhoSize];
            in.readFully(bufferdelete);

            System.arraycopy(bufferdelete, 0, comandIdt, 0, 4);
            comando = ByteBuffer.wrap(comandIdt).getInt();

            if (comando == 2) {
                // Criar um objeto File para representar o arquivo a ser excluído
                String absolutePath = new File("").getAbsolutePath();
                File arquivo = new File(absolutePath + "/addFile/" + nomeFile + "." + tipo);

                // Tentar excluir o arquivo usando o método delete()
                boolean resultado = arquivo.delete();

                System.out.println("IMPRIMINDO DIRETORIO");;
                // Verificar o resultado e mostrar uma mensagem apropriada
                if (resultado) {
                    System.out.println("Arquivo excluido com sucesso.");
                } else {
                    System.out.println("Falha ao excluir o arquivo.");
                }
            }

            byte[] bufferList = new byte[cabecalhoSize];
            in.readFully(bufferList);

            System.arraycopy(bufferList, 0, comandIdt, 0, 4);
            comando = ByteBuffer.wrap(comandIdt).getInt();

            if (comando == 3) {
                String absPath = new File("").getAbsolutePath();

                File dir = new File(absPath + "/addFile");
                File[] arquivos = dir.listFiles();

                int contador = 1;
                for (File arq : arquivos) {
                    System.out.println(contador + " - " +arq.getName());
                }
            }

            byte[] bufferDownload = new byte[cabecalhoSize];
            in.readFully(bufferDownload);
            System.arraycopy(bufferDownload, 0, comandIdt, 0, 4);
            comando = ByteBuffer.wrap(comandIdt).getInt();
            
            if (comando == 4) {
                System.out.println("Chegou");
                String absPath = new File("").getAbsolutePath();
                String filePath = absPath + "/addFile/image.png"; // Colocando o caminho até a imagem 
                byte[] data = Files.readAllBytes(Paths.get(filePath));

                byte[] sizeDownload = ByteBuffer.allocate(4).putInt(data.length).array();
                out.write(sizeDownload);

                out.write(data);
            }
        } catch (IOException e) {
                System.out.println(e.getMessage());
        }
    }
}