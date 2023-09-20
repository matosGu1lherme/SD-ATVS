import java.io.IOException;
import java.nio.ByteBuffer;


public class Client { 
    
    
    public byte[] create_mensage(String nome, String msg) {

        byte[] cacalho = new byte[331];

        byte[] nick = nome.getBytes(); 
        if (nick.length < 64) {
            System.out.println("Nome fornecido é maior que o permitido");
            return cacalho;
        }

        byte[] mensagem = msg.getBytes();
        if (mensagem.length < 256) {
            System.out.println("Mensagem fornecido é maior que o permitido");
            return cacalho;
        }

        byte[] tipo_mensagem = ByteBuffer.allocate(4).putInt(1).array();
        byte[] nick_size = ByteBuffer.allocate(4).putInt(nick.length).array();
        byte[] mensagem_size = ByteBuffer.allocate(4).putInt(mensagem.length).array();

        System.arraycopy(tipo_mensagem, 0, cacalho, 0, tipo_mensagem.length);
        System.arraycopy(nick_size, 0, cacalho, 4, nick_size.length);
        System.arraycopy(mensagem_size, 0, cacalho, 4, nick_size.length);
        
        return cacalho;
    }

}