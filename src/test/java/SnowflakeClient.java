import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bouncycastle.openpgp.*;
import org.pgpainless.PGPainless;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SnowflakeClient extends Thread {

    public static final Gson GSON = new GsonBuilder().create();// no pretty printing so its in one line

    public static void main(String[] args) {
        new SnowflakeClient("127.0.0.1", "", map -> {
            String pos = map.get("position");

        });
        /*
        step 1 authenticate
connect to server
send byte 0 to indicate login packet
send your public key (as writeUTF)
receive encrypted secret
send secret decrypted with your private key
read "Authenticate" success message (in.readUTF)

step 2 add recipient
send byte 2 to indicate add recipient packet
send the public key of the recipient you want to share with (as writeUTF)

step 3 send position
send byte 1 to indicate position packet
send position as writeUTF (either encrypted or not)
server forwards to your recipients
         */


    }

    private final String publicKey; // our public key
    private final PGPSecretKeyRing privateKey;// our private key
    private final String password;// password for privateKey
    private final DataOutputStream out;// output stream from SnowflakeClient to SnowflakeServer
    private final DataInputStream in;// input stream from SnowflakeServer to SnowflakeClient

    private final Consumer<Map<String, String>>consumer;

    public SnowflakeClient(String host, String password, Consumer<Map<String, String>> consumer) {
        super("SnowflakeClient");
        final File privateKeyFile = new File("privateKey.txt");
        final File publicKeyFile = new File("publicKey.txt");

        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            // TODO
        }

        this.password = password;
        this.consumer = consumer;
        try (Socket s = new Socket(host, 2147)) {


            publicKey = new BufferedReader(new FileReader(publicKeyFile))
                    .lines().collect(Collectors.joining());

            privateKey = PGPainless.readKeyRing()
                    .secretKeyRing(new BufferedReader(new FileReader(privateKeyFile))
                            .lines().collect(Collectors.joining()));

            out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            out.writeByte(0);
            out.writeUTF(publicKey);
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close(){
        this.interrupt();
    }

    public void updateData(Map<String, String> values) {
        try {
            out.writeUTF(GSON.toJson(values));
            out.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
               // String msg = ;
                /*System.out.println(msg);

                String decrypted = EncryptionUtil.decrypt(msg, privateKey, password);

                out.writeUTF(decrypted);
                out.flush();

                out.writeByte(1);
                //this would be encrypted against all the recipients public keys
                out.writeUTF("position");
                out.flush();
                if (in.readByte() == 1) {
                    System.out.println(in.readUTF());
                }*/


                consumer.accept(GSON.fromJson(in.readUTF(), Map.class));// TODO

            } catch (IOException e) {
                break;
            }
        }
    }


}
