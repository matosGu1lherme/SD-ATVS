import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {
    public static void main (String args[]) {
        Socket clientSocket = null;
        Scanner reader = new Scanner(System.in);
        
        try {
            int serverPort = 6666;
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

            clientSocket = new Socket(serverAddress, serverPort);

            DataInputStream in = new DataInputStream( clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());

            while (true) {
                System.out.println("comand$: ");
                String buffer = reader.nextLine();
                String[] buffer_comands = buffer.split(" ");

                if (buffer_comands[0].equals("ADDFILE")) {
                    
                    String absPath = new File("").getAbsolutePath();
                    File file = new File(absPath + "/pasta-cliente/" + buffer_comands[1]);
                    byte[] arquivo_bytes = Files.readAllBytes(file.toPath());

                    byte[] solicitacao = CreateSolicitacao(1, 2, buffer_comands[1], arquivo_bytes.length);
                    System.out.println(solicitacao.length);
                    out.write(solicitacao);

                    byte[] resposta = new byte[12];
                    in.read(resposta);

                    byte[] status_byte =  new byte[4];
                    System.arraycopy(resposta, 8, status_byte, 0, 4);
                    int status = ByteBuffer.wrap(status_byte).getInt();
                    System.out.println("STATUS: " + status);

                    if (status == 200) {
                        System.out.println("Enviando Arquivo");
                        out.write(arquivo_bytes);
                        System.out.println("Enviado...");

                        in.read(resposta);
                        System.arraycopy(resposta, 8, status_byte, 0, 4);
                        status = ByteBuffer.wrap(status_byte).getInt();
                        System.out.println("STATUS: " + status);

                        if (status == 200) {
                            System.out.println("Arquivo Adicionado ao Servidor");
                        }
                    }else {
                        System.out.println("Erro ao enviar cabeçalho");
                    }
                }

                if (buffer_comands[0].equals("DELETE")) {

                    byte[] solicitacao = CreateSolicitacao(2, 2, buffer_comands[1], 0);
                    System.out.println(solicitacao.length);
                    out.write(solicitacao);

                    byte[] resposta = new byte[12];
                    in.read(resposta);
                    byte[] status_byte = new byte[4];
                    System.arraycopy(resposta, 8, status_byte, 0, 4);
                    int status = ByteBuffer.wrap(status_byte).getInt();
                    System.out.println("STATUS: " + status);
                }

                if (buffer_comands[0].equals("GETFILESLIST")) {

                    byte[] solicitacao = CreateSolicitacao(3, 2, "any", 0);
                    System.out.println(solicitacao.length);
                    out.write(solicitacao);

                    byte[] resposta = new byte[12];
                    in.read(resposta);
                    byte[] size_byte = new byte[4];
                    System.arraycopy(resposta, 4, size_byte, 0, 4);
                    int size = ByteBuffer.wrap(size_byte).getInt();
                    
                    byte[] lista_arquivos_byte = new byte[size];
                    in.read(lista_arquivos_byte);
                    String lista_arquivos = new String(lista_arquivos_byte, "UTF-8");
                    
                    System.out.println(lista_arquivos);
                }

                if (buffer_comands[0].equals("GETFILE")) {

                    byte[] solicitacao = CreateSolicitacao(4, 2, buffer_comands[1], 0);
                    System.out.println(solicitacao.length);
                    out.write(solicitacao);

                    byte[] resposta = new byte[12];
                    in.read(resposta);
                    byte[] size_byte = new byte[4];
                    System.arraycopy(resposta, 4, size_byte, 0, 4);
                    int size = ByteBuffer.wrap(size_byte).getInt();

                    byte[] arq = new byte[size];
                    in.readFully(arq); 
                    File arquivo = new File("pasta-cliente/" + buffer_comands[1]);
        
                    if (!arquivo.exists()) {
                        FileOutputStream fos = new FileOutputStream(arquivo); // cria um fluxo de saída para o arquivo
                        fos.write(Arrays.copyOfRange(arq, 0, arq.length)); // escreve os bytes no arquivo
                        fos.close(); // fecha o fluxo
                    } else {
                        System.out.println("O arquivo já existe!"); // imprime uma mensagem de erro
                    }

                }
            }

        } catch(IOException e) {
            System.out.println(e);
        }

    }

    public static byte[] CreateSolicitacao(int MensageType, int ComandIdent, String FileName, int fileSize) {
        
        byte[] Solicitacao = new byte[272];

        byte[] msgT_bytes = ByteBuffer.allocate(4).putInt(MensageType).array();
        System.arraycopy(msgT_bytes, 0, Solicitacao, 0, 4);

        byte[] CmdTp_bytes = ByteBuffer.allocate(4).putInt(ComandIdent).array();
        System.arraycopy(CmdTp_bytes, 0, Solicitacao, 4, 4);

        byte[] FileName_bytes = FileName.getBytes();
        byte[] FileNameSz = ByteBuffer.allocate(4).putInt(FileName_bytes.length).array();
        System.arraycopy(FileNameSz, 0, Solicitacao, 8, 4);
        System.arraycopy(FileName_bytes, 0, Solicitacao, 12, FileName_bytes.length);

        byte[] fileSz_byte = ByteBuffer.allocate(4).putInt(fileSize).array();
        System.arraycopy(fileSz_byte, 0, Solicitacao, 268, 4);
        
        return Solicitacao; 
    }
}
