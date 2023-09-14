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

            String buffer = "";
            byte[] response = new byte[4];
            int responseCode;

            // --------------- ADDFILE ---------------------------//

            String filePath = "E:\\UTFPR\\SD\\Atividade-1 Socket TCP\\Enunciado-2\\assets\\image.png"; // Colocando o caminho até a imagem 
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            
            Mensagem mensagem = new Mensagem(1 ,data.length, "crocs.png"); //MensageType = 1 realiza ADDFILE
            
            out.write(mensagem.getMensagem()); // Enviando Mensagem
            in.readFully(response); // Recebendo Resposta Servidor
            responseCode = ByteBuffer.wrap(response).getInt();
            System.out.println("Server Response:" + responseCode);

            out.write(data);

            // --------------- DELETE ---------------------------//

            Mensagem mensageDelete = new Mensagem(2, 0, "croc.png");
            out.write(mensageDelete.getMensagem());

            // --------------- GETLIST ---------------------------//

            Mensagem mensageList = new Mensagem(3, 0, "");
            out.write(mensageList.getMensagem());

            // --------------- DOWNLOAD ---------------------------//

            Mensagem mensageDownload = new Mensagem(4, 0, "image.png");
            out.write(mensageDownload.getMensagem());
            byte[] sizeb = new byte[4];
            in.readFully(sizeb);

            int sz = ByteBuffer.wrap(sizeb).getInt();
            byte[] arquivoDownload = new byte[sz];

            in.readFully(arquivoDownload); 
            File arquivo = new File("clientpasta/imagedownload.png");

            if (!arquivo.exists()) {
                FileOutputStream fos = new FileOutputStream(arquivo); // cria um fluxo de saída para o arquivo
                fos.write(Arrays.copyOfRange(arquivoDownload, 0, arquivoDownload.length)); // escreve os bytes no arquivo
                fos.close(); // fecha o fluxo
            } else {
                System.out.println("O arquivo já existe!"); // imprime uma mensagem de erro
            }

        } catch(IOException e) {
            System.out.println(e);
        }

    }
}

class Mensagem {

    private byte[] mensagem;
    

    public Mensagem(int comandIdt ,int fileSize, String nome){
        this.mensagem = new byte[264];
        Arrays.fill(this.mensagem, (byte) 0xFF);
        
        byte[] comand = ByteBuffer.allocate(4).putInt(comandIdt).array();
        byte[] SizeFile = ByteBuffer.allocate(4).putInt(fileSize).array();
        byte[] bnome = nome.getBytes();
        
        System.arraycopy(comand, 0, this.mensagem, 0, comand.length);
        System.arraycopy(SizeFile, 0, this.mensagem, 4, SizeFile.length);
        System.arraycopy(bnome, 0, this.mensagem, 8, bnome.length);

        // int num = ByteBuffer.wrap(this.mensagem).getInt();
    }

    public byte[] getMensagem() {
        return this.mensagem;
    }
}
