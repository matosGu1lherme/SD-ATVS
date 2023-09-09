import java.io.File;
import java.util.Scanner;

public class teste {
    public static void main (String args[]) {
        Scanner reader = new Scanner(System.in);
        try {

            String path = reader.nextLine();
            File dir = new File("E:/UTFPR/SD\\Atividade-1 Socket TCP");
            dir = new File(path);
            File[] arquivos = dir.listFiles();
    
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) System.out.println(arquivo.getName());
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }
}