import java.io.File;

public class teste {
    public static void main (String args[]) {
        try {

            File dir = new File("E:/UTFPR/SD\\Atividade-1 Socket TCP");
            dir = new File("E:\\Cacic\\CACIC");
            File[] arquivos = dir.listFiles();
    
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) System.out.println(arquivo.getName());
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }
}