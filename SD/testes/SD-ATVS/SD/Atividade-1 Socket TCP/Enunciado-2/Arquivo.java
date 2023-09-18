import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Arquivo {

    private String nomeArquivo;
    private String tipoArquivo;
    private String caminhoArquivo;
    private byte[] conteudo;


    public Arquivo(String nomeArquivo, String tipoArquivo, String caminhoArquivo) throws IOException {
        this.nomeArquivo = nomeArquivo;
        this.tipoArquivo = tipoArquivo;
        this.caminhoArquivo = caminhoArquivo;

        this.conteudo = Files.readAllBytes(Paths.get(caminhoArquivo));
    }

    public String getnome() {
        return this.nomeArquivo;
    }

    public String getTipo() {
        return this.tipoArquivo;
    }

    public String getCaminho() {
        return this.caminhoArquivo;
    }

    public byte[] getConteudo() {
        return this.conteudo;
    }
    

    public static void main(String[] args) throws IOException {
        String filePath = "E:\\UTFPR\\SD\\Atividade-1 Socket TCP\\Enunciado-2\\image.png";
        byte str = 1;
        System.out.println(str);
        byte[] data = Files.readAllBytes(Paths.get(filePath));


        // agora você pode usar o array de bytes como quiser

        File file = new File("new_file.png"); // seu arquivo de destino
        if (!file.exists()) { // verifica se o arquivo não existe
            FileOutputStream fos = new FileOutputStream(file); // cria um fluxo de saída para o arquivo
            fos.write(data); // escreve os bytes no arquivo
            fos.close(); // fecha o fluxo
        } else {
            System.out.println("O arquivo já existe!"); // imprime uma mensagem de erro
        }
    }
}
