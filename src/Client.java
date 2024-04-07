import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Client {
    private static PublicKey serverPublicKey;
    private static PrivateKey clientPrivateKey;

    public static void main(String[] args) {
        try {
            //1- Generar las claves para el client
            KeyPair clientKeyPair = Crypto.randomGenerate(2048);
            clientPrivateKey = clientKeyPair.getPrivate();

            // 2- conectarse al servidor
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Conexión establecida con el servidor.");

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            // 3- Recibe la clave publica del server
            serverPublicKey = (PublicKey) inputStream.readObject();


            outputStream.writeObject(clientKeyPair.getPublic()); // 4- Enviar clave pub del cliente al server
            outputStream.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Cliente: ");
                String clientMessage = reader.readLine();

                // 5- Enviar mensaje cifrado al server
                byte[] encryptedMessage = Crypto.encryptData(clientMessage.getBytes(), serverPublicKey);
                outputStream.writeObject(encryptedMessage);
                outputStream.flush();

                // 6- rebre la respote del server
                byte[] encryptedResponse = (byte[]) inputStream.readObject();
                byte[] decryptedResponse = Crypto.decryptData(encryptedResponse, clientPrivateKey);
                String response = new String(decryptedResponse);
                System.out.println("Servidor: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
