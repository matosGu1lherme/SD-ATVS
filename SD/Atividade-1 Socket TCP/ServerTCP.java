import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class ServerTCP {
    public static void main(String args[]) {
        DataInputStream in;
        DataOutputStream out;

        try {
            int serverPort = 6666;

            ServerSocket listenSocket = new ServerSocket(serverPort);

            System.out.println("Servidor Aguarndando Conexao ...");
            while (true) {

                Socket clientSocket = listenSocket.accept();

                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());

                while (true){
                    String buffer = in.readUTF();
                    String[] bufferSplit = buffer.split(" ");
                    if (LogInServer.login(bufferSplit[0], bufferSplit[1])) {
                        System.out.println("Iniciando Thread");
                        ClientThread c = new ClientThread(clientSocket, bufferSplit[0]);
                        c.start();

                        out.writeUTF("SUCCESS");
                        break;
                    }else{
                        out.writeUTF("ERROR");
                        System.out.println("ERRO DE AUTENTICACAO");
                    }
                }


            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class LogInServer {
    
    public static HashMap<String, String> users = new HashMap<String, String>(3);

    static {
        users.put("jose", "ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413"); //123456
        users.put("Rodrigo", "49b187ea0ca53e9507f4f061dd5a7c7426c91f35f37de46524beaed54b1ce376fbe23daa03c7b460c720b44d430fc467838549fd6ec6646167d9b39d9a6ec680"); //sd2023
        users.put("maria", "5eb75177384354fa9104a2930b72dd10067a82a04ecfe351d4df99901dfb9eb91d8d2804fab3cb3f0f85e488c8a517e9bbfeeeca63f1412f23ff5d5f33d8bce6"); //camarao
    }


    public static boolean login(String user, String password) {
        if (users.containsKey(user)) {
            String serverPw = users.get(user);

            if (serverPw.equals(password)) {
                System.out.println("Usuario " + user + " Conectado");
                return true;
            }
        }
        return false;
    }
}

class ClientThread extends Thread {
    
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    File dir;

    public ClientThread(Socket clienSocket, String user) {
        try {
            this.clientSocket = clienSocket;
            in = new DataInputStream(clienSocket.getInputStream());
            out = new DataOutputStream(clienSocket.getOutputStream());

            String caminhoApp = new File("").getAbsolutePath();
            dir = new File(caminhoApp);

        } catch (IOException e) {
            System.out.println("Connect: " + e.getMessage());
        }
    }

    //Executa ao Rodar c.run()
    @Override
    public void run() {
        String buffer = "";
        String[] bufferCmd = null;
        try {
            while (true) {
                buffer = in.readUTF();
                bufferCmd = buffer.split(" ");

                if (bufferCmd[0].equals("PWD")) {
                    out.writeUTF(this.dir.toString()); 
                }

                if (bufferCmd[0].equals("CHDIR")) {
                    String path = buffer.replaceFirst("CHDIR", "");
                    System.out.println(this.dir);
                    String path2 = "E:/Cacic/CACIC";
                    this.dir = new File(path);
                    System.out.println(this.dir);
                    out.writeUTF("SUCCESS" + this.dir.toString());
                }

                if (bufferCmd[0].equals("GETFILES")) {
                    File[] arquivos = this.dir.listFiles();
                    String dataReturn = "";
                    int count = 1;
                    for (File arquivo : arquivos) {
                        if (arquivo.isFile()) {

                            dataReturn = dataReturn + count + " - " +arquivo.getName() + "\n";
                            count++;
                        }
                    }
                    out.writeUTF(dataReturn);
                }

                if (bufferCmd[0].equals("GETDIRS")) {
                    File[] arquivos = this.dir.listFiles();
                    String dataReturn = "";
                    int count = 1;
                    for (File arquivo : arquivos) {
                        if (arquivo.isDirectory()) {
                            dataReturn = dataReturn + count + " - " +arquivo.getName() + "\n";
                            count++;
                        }
                    }
                    out.writeUTF(dataReturn);
                }
            }
        } catch (IOException e) {
            System.out.println("Connect: " + e.getMessage());
        }
    }
}