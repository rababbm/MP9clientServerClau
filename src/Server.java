import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Server {
    private static PublicKey clientPublicKey;
    private static PrivateKey serverPrivateKey;

    public static void main(String[] args) {
        try {
            // 1- generar les claus pra el server
            KeyPair servidorKeyPair = Crypto.randomGenerate(2048);
            serverPrivateKey = servidorKeyPair.getPrivate();

            ServerSocket servidorSocket = new ServerSocket(1234); //servsersocket

            System.out.println("Servidor iniciado. Esperando conexiones...");

            Socket clientSocket = servidorSocket.accept();
            System.out.println("Cliente conectado."); //2- esperar al client

            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            outputStream.writeObject(servidorKeyPair.getPublic());// 3- Enviar la clau publica del server al client
            outputStream.flush();


            clientPublicKey = (PublicKey) inputStream.readObject(); //4- clau publica del client

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Servidor: ");
                String serverMessage = reader.readLine();

                byte[] encryptedMessage = Crypto.encryptData(serverMessage.getBytes(), clientPublicKey); //5- enviar mensaje
                outputStream.writeObject(encryptedMessage);
                outputStream.flush();

                byte[] encryptedResponse = (byte[]) inputStream.readObject();
                byte[] decryptedResponse = Crypto.decryptData(encryptedResponse, serverPrivateKey);
                String response = new String(decryptedResponse);
                System.out.println("Cliente: " + response);            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
