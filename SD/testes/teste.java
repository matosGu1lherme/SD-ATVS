import java.math.BigInteger;
import java.nio.ByteBuffer;

public class teste {
    public static void main(String args[]) {
        int num = 22072; // int precisa de 4 bytes, ordem padrão ByteOrder.BIG_ENDIAN
        byte[] result = ByteBuffer.allocate(4).putInt(num).array();
        
        int number = ByteBuffer.wrap(result).getInt();

        System.out.println(number);
    }
}