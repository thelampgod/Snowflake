import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;

public class ClientTest {

    @Test
    void sendMessageToServer() throws IOException {
        try (Socket s = new Socket("127.0.0.1", 2147)) {
            System.out.println("Connected to Snowflake");
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            System.out.println(in.readUTF());

            out.writeUTF("Hello server");
            out.flush();
            System.out.println(in.readUTF());

        }
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try (Socket s = new Socket("127.0.0.1", 2147)) {
            System.out.println("Connected to Snowflake");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
            Thread send = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        while (true) {
                            if (scan.hasNext()) {
                                msg = scan.nextLine();
                                System.out.println(msg);
                                out.writeUTF(msg);
                                out.flush();
                            }
                        }
                    } catch (IOException ignored) {

                    }
                }
            });
            send.start();
            String msg;
            try {
                msg = in.readUTF();
                while (msg != null) {
                    System.out.println(msg);
                    msg = in.readUTF();
                }
                out.close();
            } catch (IOException ignored) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void secureRandom() {
        String secret = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
        System.out.println(secret);
    }
}
