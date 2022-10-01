import com.github.thelampgod.snowflake.util.EncryptionUtil;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.junit.jupiter.api.Test;
import org.pgpainless.PGPainless;

import java.io.*;
import java.net.Socket;

public class ClientTest2 {

    public static void main(String... args) throws IOException {
        try (Socket s = new Socket("127.0.0.1", 2147)) {
            System.out.println("Connected to Snowflake");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            out.writeByte(0);
            out.writeUTF(ALICE_CERT);
            out.flush();

            PGPSecretKeyRing priv = PGPainless.readKeyRing().secretKeyRing(ALICE_KEY);
            while (true) {
                try {
                    byte[] msg = new byte[in.readInt()];
                    in.readFully(msg);

                    String decrypted = EncryptionUtil.decrypt(msg, priv, "");

                    out.writeUTF(decrypted);
                    out.flush();

                    out.writeByte(1);
                    //this would be encrypted against all the recipients public keys
                    out.writeUTF("position");
                    out.flush();
                    if (in.readByte() == 1) {
                        System.out.println(in.readUTF());
                    }

                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

    @Test
    void addBobAsRecipient() throws IOException {
        try (Socket s = new Socket("127.0.0.1", 2147)) {
            System.out.println("Connected to Snowflake");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            out.writeByte(0);
            out.writeUTF(ALICE_CERT);
            out.flush();
            System.out.println(in.readUTF());

            PGPSecretKeyRing priv = PGPainless.readKeyRing().secretKeyRing(ALICE_KEY);
            while (true) {
                try {
                    String msg = in.readUTF();
                    System.out.println(msg);

                    String decrypted = EncryptionUtil.decrypt(msg.getBytes(), priv, "");

                    out.writeUTF(decrypted);
                    out.flush();

                    //"Authenticated" message
                    System.out.println(in.readUTF());

                    //add recipient packet
                    out.writeByte(2);
                    out.writeUTF(BOB_CERT);
                    out.flush();
                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

    @Test
    void trySendPosition() throws IOException {
        try (Socket s = new Socket("127.0.0.1", 2147)) {
            System.out.println("Connected to Snowflake");

            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

            out.writeByte(0);
            out.writeUTF(ALICE_CERT);
            out.flush();

            PGPSecretKeyRing priv = PGPainless.readKeyRing().secretKeyRing(ALICE_KEY);
            while (true) {
                try {
                    String msg = in.readUTF();
                    System.out.println(msg);

                    String decrypted = EncryptionUtil.decrypt(msg.getBytes(), priv, "");

                    out.writeUTF(decrypted);
                    out.flush();

                    out.writeByte(1);
                    //this would be encrypted against all the recipients public keys
                    out.writeUTF("position");
                    out.flush();
                    if (in.readByte() == 1) {
                        System.out.println(in.readUTF());
                    }

                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

    public static final String TEST_PRIVKEY_PASS = "test";

    public static final String TEST_PUBKEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "\n" +
            "mQGNBGMZC24BDADRgrFckijB3V4bv/GZX3EVXck89pdhTqC1K+ekMJbGSXL7fVoZ\n" +
            "bFMmkAZgJKE5cAbtsOi8wSi+pbUApRPcBmOjqsy3MXFDTVv6DFMMjqakftrvkA4i\n" +
            "P+xBKos0H/4tabNijPebOOEDx7297JRIqarDfVwSjqHEuG0D2aqcpgqvs89fNPJF\n" +
            "oZflk2kPM5WkuwbXMwg50nYMnScJmM6D+aXXakRgg5pkuwSyuTLs4aB8payqaNZ0\n" +
            "mP+GvVajdYVZ2rLMFMHQN7DjFSVVpBknV4jUYF9KmTlj5NI/R8G5gptc3WYFFuXo\n" +
            "G/507uzxSW+wifpcbYRZf9JQJg4JQnHH7ikSwpTiR/B582TkHzW/j8OlZtOnRt+z\n" +
            "C/epKs50E0XdQeCCIn6VUr1bYXS3AqA7ONLk/zxmwd4w1frZMoIWDnj76uGyXX8p\n" +
            "LNmDfMigmfKEJvxGNEPWK4gNI+O3kheriGGTdJNL/ZxhhxcakB0VLy2e/yhjvTep\n" +
            "UXI5VnBgMaWfxC0AEQEAAbQUdGVzdHVzZXIgPHRlc3RAdGVzdD6JAdQEEwEIAD4W\n" +
            "IQSBLDWkpJQToxum/I5Q33LjnbQSnQUCYxkLbgIbAwUJA8MYsgULCQgHAgYVCgkI\n" +
            "CwIEFgIDAQIeAQIXgAAKCRBQ33LjnbQSnRpKC/92DpLb3ylgVgT8VluspLNoosxa\n" +
            "bVLA2MIAzn/a82WCRz+SvrA5A23+XAnPDBZf2W56uu4BqqW5US9cvAyNJrhqUDsH\n" +
            "54Nhv3RPJ37WF0hqC5QdluP95VI6QBqdwe8QE7KaSRR3nwsHbtX7oWSm+9Eq7k/I\n" +
            "kfN2tqhfGtKUyffG3a7lnI4NDwjaU08rzOyFCEAJlB0nDMBrIHQPHjEyrk/9I89J\n" +
            "ek9FYFs+bhoskvNJYi+Ia2GOtmtrj8RRCx1Spiewquinfpt5mM9DDN8GzAuh3n8A\n" +
            "we0QYsojiUVLe/dOaqCqdpT1D7l+MA/ddIZgO055Ja8gbUIGL2Rimras9pGTN45c\n" +
            "iZo4OYLKIz8T3LdIGZuVreVAv0UW60p+T/ynHyJFgtROjebzOVGh1S/uEA4fjdHO\n" +
            "40SPP2SvS95DJT0FL/UpFPLxXv+p5wxQF99VQoo5bbw8khHz3nmTkayNBy+6S6OR\n" +
            "pC+0m+woPPC6I7cu+OzXGU6ZzIcNMTYeNxg+UDO5AY0EYxkLbgEMALulHrginNKP\n" +
            "nv1F+8kS1eBCU9eRSrIO4GvdmO6P5yPsknKpEY7kxrqbMl+f27n35o2jxLTqTdrZ\n" +
            "NCo7tPQ6Tq8XGoHkXu2p34FLFSIQXATFyr8MrtqW8qmmWd5eKHXB+hqiyvLhHMEV\n" +
            "ATKsQ0lFO17OgVG0yFwqmjj1imktM2cCaP+eCXUHQGzjhodd58h0ejL4/K9/iIz3\n" +
            "UeUUUQOdjD4rdfTJQlvhdbfx48VaZ02vZ7TD6i6nWWZS2yPVR7N9w70MLvpVENRv\n" +
            "rQDQsboTyu3xZpN90L/LzO7tqymh7k+KbTEqMCaiKOaZB0aoVRVGvTb6oxVectjH\n" +
            "djcaW56di5utz7ixlXnO+W2rUYqXqeWG+CnTrv4ol969nUKv453Abxh3xETbTBtV\n" +
            "Fr1BEBRyBTmmxLslKcVB/UWh4U2YN8dPKyTFoMbXJo9B4bo8gIX9+hsR7agyWWi0\n" +
            "pBGNNJ19i7aDGukdnZzZOz5VCPWNpdkExRbwwvADb3qBv820mtN2rwARAQABiQG8\n" +
            "BBgBCAAmFiEEgSw1pKSUE6MbpvyOUN9y4520Ep0FAmMZC24CGwwFCQPDGLIACgkQ\n" +
            "UN9y4520Ep1/7Av9HDKsBrADOEkifl5JGYX0zrUyjhCpN6MRntaBa2QCvsp+eYAn\n" +
            "XZMo2AmYdzj52cCrTmFR5N7rZqC8Q/GCFe7NPMJaX2ffTOWAzfKVKTN5tEv6m5Jk\n" +
            "KYwtj8CtGz945720lC+XsN/dS9uaSHN+7QNd8wvsxAUyIZB6rPWpenfPD9Wn7gf5\n" +
            "j08Jng2vWBCjJ+NO1gsNyv9aNIOKG2Xdhz0iz9P3CDNI7d3zT98WGXZ4ywn+/B1R\n" +
            "tn+AmkxzMbw0ckIPJGIDEJ9hx4FqLS1OZk2ZAzmiP7kjQCUJKCOi6zOHT9oxkBdZ\n" +
            "9rdDan1lcBEe+iRNq7cVCBcxm5TFSNPfU9FQoTZI0HY6kHkZqJI5dzJQ1iSvXVLl\n" +
            "YDzfbmoqbNGZotTleYq2VCZhoydrywH1WqljfCk3pemRj8ABXDk22Bm/mE8rtMC/\n" +
            "sPcBcyu9R+HyrDEh+ffmDVnnSPgw+17orP141p+0mPNEtQtI3MM+w7MVchilOEC1\n" +
            "lYt+w9xRElCNaCps\n" +
            "=OCCi\n" +
            "-----END PGP PUBLIC KEY BLOCK-----\n";

    public static final String TEST_PRIVKEY = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
            "\n" +
            "lQWGBGMZC24BDADRgrFckijB3V4bv/GZX3EVXck89pdhTqC1K+ekMJbGSXL7fVoZ\n" +
            "bFMmkAZgJKE5cAbtsOi8wSi+pbUApRPcBmOjqsy3MXFDTVv6DFMMjqakftrvkA4i\n" +
            "P+xBKos0H/4tabNijPebOOEDx7297JRIqarDfVwSjqHEuG0D2aqcpgqvs89fNPJF\n" +
            "oZflk2kPM5WkuwbXMwg50nYMnScJmM6D+aXXakRgg5pkuwSyuTLs4aB8payqaNZ0\n" +
            "mP+GvVajdYVZ2rLMFMHQN7DjFSVVpBknV4jUYF9KmTlj5NI/R8G5gptc3WYFFuXo\n" +
            "G/507uzxSW+wifpcbYRZf9JQJg4JQnHH7ikSwpTiR/B582TkHzW/j8OlZtOnRt+z\n" +
            "C/epKs50E0XdQeCCIn6VUr1bYXS3AqA7ONLk/zxmwd4w1frZMoIWDnj76uGyXX8p\n" +
            "LNmDfMigmfKEJvxGNEPWK4gNI+O3kheriGGTdJNL/ZxhhxcakB0VLy2e/yhjvTep\n" +
            "UXI5VnBgMaWfxC0AEQEAAf4HAwJ9jMD4PI2lp8XBJcLQlxopQHGFUBQT0yG8K7Hf\n" +
            "u0NciaCqZRlrt82CnJarrNp8qNPJY3xCcijU8ku1MfvZpFCf0LVBHRY3gxlWQhu0\n" +
            "jC/JELh/nL8brQpTdXv6dq6rWVS/bfYN/cp3Mr+m/mhOXm7X9Ulac4PRwi++2lVA\n" +
            "Cdc3QQl1Xz/XV5D2zlrHWZkU/SZmPl1mCG1y2adyry82DGVfjyrevJ6j9uDo68Rf\n" +
            "5QmIEqdVXvJOUzKnBQyXNHssafaWy21FVKbObqn4263IUcNFa1a2UnfJrKrDfDLC\n" +
            "bp7iU/zJW9uQJyPlJC2cN8ivHSGtfLdl1CXDhca6KHcat5tUWHSC4VE3fyxSsISN\n" +
            "DHrIVKATPrdaUFxEBzEAMBlR6E/mRzWSgMu/PRtad0rdO2Pm2sX5cnhpFfOMx2PR\n" +
            "/iUO0GHxSRF1jTEHz/nsT4O+0oICgWoM6LtAGDWJeDPiVPkZlusOEeiYycU+bLBD\n" +
            "sWFmwwWiMyboUpibeFhlCVenK3aoeqnJEahZEkkb/Zz08RHlpWAILvqzFX+X+d0H\n" +
            "CLqcfcrRRLgFTWY0b5cOCzfDD69WqTfwXwo9VAAz41X4FrH24Em5zH97fMgPFk0R\n" +
            "YIvlZImRwMZ/AlZwdSEJs0OgWKJchRUbKcQ0vaHbMSHiYioqRBZJj46VCAbyK+3j\n" +
            "XTEWRbqatoy+TJKpFvhF6L7vVG2uIzZoQrnCu+JbD6JVdccBTdoTb2TC1qyubtDM\n" +
            "ntDowCIjyjTHdlVoV1fqoVQ/+pxc28PATizg4j4dNqSTWgFS5nAMvSMfj3iZElb/\n" +
            "xl+xlifucoiV+OJaBNXK3mtK7aZs56LUzBnWjcBiaWWOYolYkWODuzvtq1Jk4qL1\n" +
            "BXC2qElc1wpx7O1sVMQgRbX0Ug3TqRGJWWviKqd3AodhBtR36kga1BLyL2SeEY9L\n" +
            "0qa9DgQCEkH45jvt5hU8mBLUmvz29O/uHvGKTaE8RPbb039gcXrJ/o4jn2zfd6qe\n" +
            "PZZrSsZV/HPS3w7VGSjRnHTVQbyXhXN+Uy4flP8LvgVamyLwC6CEDy0netMAeMFa\n" +
            "ZqMIcJEI+X2XfCsr5jYEK0cIINaoGA+thzL15lRNzicdvcW6SVFps4X5B6knIYDN\n" +
            "/0rdqYVdZpOJLr0U1qLcJJP/xxsOmapbm2pnEHNnhr7xbzQoUjAAkH0QfncjaquM\n" +
            "md8kfVo9L+bqe2lgVhBHZQHr966bso8/qPVXp6QyMfvkLf+Fq2JOi8Y6GtmVrA8w\n" +
            "B8z9BPwnvnOQq+sdYq2MdGlOPMleEwTvBA2Af+ps03sIb3H+kJuX5CghgVbRTdYf\n" +
            "fauwa+ANQWlFmuga+Yh2H9987Zj3rSGGkrQUdGVzdHVzZXIgPHRlc3RAdGVzdD6J\n" +
            "AdQEEwEIAD4WIQSBLDWkpJQToxum/I5Q33LjnbQSnQUCYxkLbgIbAwUJA8MYsgUL\n" +
            "CQgHAgYVCgkICwIEFgIDAQIeAQIXgAAKCRBQ33LjnbQSnRpKC/92DpLb3ylgVgT8\n" +
            "VluspLNoosxabVLA2MIAzn/a82WCRz+SvrA5A23+XAnPDBZf2W56uu4BqqW5US9c\n" +
            "vAyNJrhqUDsH54Nhv3RPJ37WF0hqC5QdluP95VI6QBqdwe8QE7KaSRR3nwsHbtX7\n" +
            "oWSm+9Eq7k/IkfN2tqhfGtKUyffG3a7lnI4NDwjaU08rzOyFCEAJlB0nDMBrIHQP\n" +
            "HjEyrk/9I89Jek9FYFs+bhoskvNJYi+Ia2GOtmtrj8RRCx1Spiewquinfpt5mM9D\n" +
            "DN8GzAuh3n8Awe0QYsojiUVLe/dOaqCqdpT1D7l+MA/ddIZgO055Ja8gbUIGL2Ri\n" +
            "mras9pGTN45ciZo4OYLKIz8T3LdIGZuVreVAv0UW60p+T/ynHyJFgtROjebzOVGh\n" +
            "1S/uEA4fjdHO40SPP2SvS95DJT0FL/UpFPLxXv+p5wxQF99VQoo5bbw8khHz3nmT\n" +
            "kayNBy+6S6ORpC+0m+woPPC6I7cu+OzXGU6ZzIcNMTYeNxg+UDOdBYYEYxkLbgEM\n" +
            "ALulHrginNKPnv1F+8kS1eBCU9eRSrIO4GvdmO6P5yPsknKpEY7kxrqbMl+f27n3\n" +
            "5o2jxLTqTdrZNCo7tPQ6Tq8XGoHkXu2p34FLFSIQXATFyr8MrtqW8qmmWd5eKHXB\n" +
            "+hqiyvLhHMEVATKsQ0lFO17OgVG0yFwqmjj1imktM2cCaP+eCXUHQGzjhodd58h0\n" +
            "ejL4/K9/iIz3UeUUUQOdjD4rdfTJQlvhdbfx48VaZ02vZ7TD6i6nWWZS2yPVR7N9\n" +
            "w70MLvpVENRvrQDQsboTyu3xZpN90L/LzO7tqymh7k+KbTEqMCaiKOaZB0aoVRVG\n" +
            "vTb6oxVectjHdjcaW56di5utz7ixlXnO+W2rUYqXqeWG+CnTrv4ol969nUKv453A\n" +
            "bxh3xETbTBtVFr1BEBRyBTmmxLslKcVB/UWh4U2YN8dPKyTFoMbXJo9B4bo8gIX9\n" +
            "+hsR7agyWWi0pBGNNJ19i7aDGukdnZzZOz5VCPWNpdkExRbwwvADb3qBv820mtN2\n" +
            "rwARAQAB/gcDAoW9YZbBs50sxbRuCGlYHKQpes8hJslXl9AWeA3KpBiKWaXCVYzM\n" +
            "PHb4qlWBL1TEaQKVq4TBiRs+mTsmM+zzN1yLFuKCWSfjPr+1JDZBjG1/bJ/v4gRT\n" +
            "UYYcJtq/UMJzKJoKOSap+R5COEbIkYR64e+QrkNHadWfIY9EKpwTkiEwBQBDbndc\n" +
            "6jb+kIkuI9lFM8dqB1wtUN0IkzyF85GJftwZMtrfHFx7SJYFBCDTWfIwo7kAZITP\n" +
            "2SGbuUpCpwUFvxAULLQcZtMH/mLYkhr6n8Q5i5rtdzgycQp4oByQc7+sq6UeMgJf\n" +
            "w7oOxPTuznTKB/BnCKGziFwhvntMrRKWbkjGmgQ1rj9V8XKuA53p8jn2MbCtQr83\n" +
            "L4+BvPgp0MXjfHaIDB7hAMCxAmozBX4iY9C8kLwHlium6wwN2KvbD0TZ92apjinQ\n" +
            "altOONVg0eBTWTw6QjVCjCzf/kC9lKFhtaCiHvjqx/h/wZH2JbLiIrG0l4diCM8E\n" +
            "9s+1K8crTbyHaKNzxIPAsvBaBqL4OEKgTAtqvl28pQXHMPy3qViH7/GtfxdmTMN4\n" +
            "eMbi7aWka662lruGkZBDAeDhI82/IcJtQlz9NfenHEN7fFKgmsJW8d3s3KDXMFV1\n" +
            "MYdnles0o2Rp7RHyX1PMkZw/AWssbwYYCh5YGPzlUpPxdMO6iOI52d6B9rmn0bxK\n" +
            "D3Hmwl0UAcaDP+OeQhblZUaC2Ws2A+SXeG2jfdF+1vemmaYe56tMS04N/2zLEXEv\n" +
            "aG5h014mlZ7mT/ZfaVE+uocsJxzkbN5nzn77vnIg7/rwb5dCKmdNTnrqBEnI6mDI\n" +
            "tuIRzAzQmN31EFhYHA30hbLSFhbe5g3lrrmsAesHyEktzSkda9QAk1mkoiJd18KL\n" +
            "h+jS11HbJYZYuyTkdZvCyyKNQICI49U8uOgc531/s/MtS26yBR4Ug5Tm8I1I6fkP\n" +
            "Ts1syjLD4WI/UHScuVi4nDismK/jyCHf19WJ7znRYWPX4vEjStDLMrmWL65ZK02a\n" +
            "CD0xVkYMbtsmCN55k//7Cqv4UdAXWFSV4UXMDef0LvnIL4nFlvneXGUx4wuzc5mY\n" +
            "mVQ+JUNDlQwCJO+7cRjYu5mbV4E7QP6Z80QEIHazsIB7LrQEky28JkoGZSpMPKi3\n" +
            "ekV7hJGUq72ESm+q9Fdbircm0GN/oH5jhI7mRDEv81h1orPdexak+H6/dZeXtOsi\n" +
            "iQgh/b+t5x8ZmaYwyJASTRFMmwEmkvNvNQmspDnT7WvF9sePu/0Cu3s0hkXqTeXj\n" +
            "3ySo1WZm1uy3Hus2lzgzsMSj2Fnb5ryTjJpW6xPMv0pSIT8HHAsCAWAEEbeXCa4o\n" +
            "Yj1i3AEIZbLoCW58TR7PiQG8BBgBCAAmFiEEgSw1pKSUE6MbpvyOUN9y4520Ep0F\n" +
            "AmMZC24CGwwFCQPDGLIACgkQUN9y4520Ep1/7Av9HDKsBrADOEkifl5JGYX0zrUy\n" +
            "jhCpN6MRntaBa2QCvsp+eYAnXZMo2AmYdzj52cCrTmFR5N7rZqC8Q/GCFe7NPMJa\n" +
            "X2ffTOWAzfKVKTN5tEv6m5JkKYwtj8CtGz945720lC+XsN/dS9uaSHN+7QNd8wvs\n" +
            "xAUyIZB6rPWpenfPD9Wn7gf5j08Jng2vWBCjJ+NO1gsNyv9aNIOKG2Xdhz0iz9P3\n" +
            "CDNI7d3zT98WGXZ4ywn+/B1Rtn+AmkxzMbw0ckIPJGIDEJ9hx4FqLS1OZk2ZAzmi\n" +
            "P7kjQCUJKCOi6zOHT9oxkBdZ9rdDan1lcBEe+iRNq7cVCBcxm5TFSNPfU9FQoTZI\n" +
            "0HY6kHkZqJI5dzJQ1iSvXVLlYDzfbmoqbNGZotTleYq2VCZhoydrywH1WqljfCk3\n" +
            "pemRj8ABXDk22Bm/mE8rtMC/sPcBcyu9R+HyrDEh+ffmDVnnSPgw+17orP141p+0\n" +
            "mPNEtQtI3MM+w7MVchilOEC1lYt+w9xRElCNaCps\n" +
            "=EauE\n" +
            "-----END PGP PRIVATE KEY BLOCK-----\n";

    private static final String ALICE_KEY = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
            "Version: PGPainless\n" +
            "Comment: 12E3 4F04 C66D 2B70 D16C  960D ACF2 16F0 F93D DD20\n" +
            "Comment: alice@pgpainless.org\n" +
            "\n" +
            "lFgEYksu1hYJKwYBBAHaRw8BAQdAIhUpRrs6zFTBI1pK40jCkzY/DQ/t4fUgNtlS\n" +
            "mXOt1cIAAP4wM0LQD/Wj9w6/QujM/erj/TodDZzmp2ZwblrvDQri0RJ/tBRhbGlj\n" +
            "ZUBwZ3BhaW5sZXNzLm9yZ4iPBBMWCgBBBQJiSy7WCRCs8hbw+T3dIBYhBBLjTwTG\n" +
            "bStw0WyWDazyFvD5Pd0gAp4BApsBBRYCAwEABAsJCAcFFQoJCAsCmQEAAOOTAQDf\n" +
            "UsRQSAs0d/Nm4YIrq+gU7gOdTJuf33f/u/u1nGM1fAD/RY7I3gQoZ0lWbvXVkRAL\n" +
            "Cu9cUJdvL7kpW1oYtYg21QucXQRiSy7WEgorBgEEAZdVAQUBAQdA60F84k6MY/Uy\n" +
            "BCZe4/WP8JDw/Efu5/Gyk8hcd3HzHFsDAQgHAAD/aC8DOOkK0XNVz2hkSVczmNoJ\n" +
            "Umog0PfQLRujpOTqonAQKIh1BBgWCgAdBQJiSy7WAp4BApsMBRYCAwEABAsJCAcF\n" +
            "FQoJCAsACgkQrPIW8Pk93SCd6AD/Y3LF2RvgbEaOBtAvH6w0ZBPorB3rk6dx+Ae0\n" +
            "GvW4E8wA+QHmgNo0pdkDxTl0BN1KC7BV1iRFqe9Vo7fW2LLfhlEEnFgEYksu1hYJ\n" +
            "KwYBBAHaRw8BAQdAPtqap21/zmVzxOHk++891/EZSNikwWkq9t0pmYjhtJ8AAP9N\n" +
            "m/G6nbiEB8mu/TkNnb7vdhSmLddL9kdKh0LzWD95LBF0iNUEGBYKAH0FAmJLLtYC\n" +
            "ngECmwIFFgIDAQAECwkIBwUVCgkIC18gBBkWCgAGBQJiSy7WAAoJEOEz2Vo79Yyl\n" +
            "zN0A/iZAVklSJsfQslshR6/zMBufwCK1S05jg/5Ydaksv3QcAQC4gsxdFFne+H4M\n" +
            "mos4atad6hMhlqr0/Zyc71ZdO5I/CAAKCRCs8hbw+T3dIGhqAQCIdVtCus336cDe\n" +
            "Nug+E9v1PEM3F/dt6GAqSG8LJqdAGgEA8cUXdUBooOo/QBkDnpteke8Z3IhIGyGe\n" +
            "dc8OwJyVFwc=\n" +
            "=ARAi\n" +
            "-----END PGP PRIVATE KEY BLOCK-----\n";
    private static final String ALICE_CERT = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "Version: PGPainless\n" +
            "Comment: 12E3 4F04 C66D 2B70 D16C  960D ACF2 16F0 F93D DD20\n" +
            "Comment: alice@pgpainless.org\n" +
            "\n" +
            "mDMEYksu1hYJKwYBBAHaRw8BAQdAIhUpRrs6zFTBI1pK40jCkzY/DQ/t4fUgNtlS\n" +
            "mXOt1cK0FGFsaWNlQHBncGFpbmxlc3Mub3JniI8EExYKAEEFAmJLLtYJEKzyFvD5\n" +
            "Pd0gFiEEEuNPBMZtK3DRbJYNrPIW8Pk93SACngECmwEFFgIDAQAECwkIBwUVCgkI\n" +
            "CwKZAQAA45MBAN9SxFBICzR382bhgiur6BTuA51Mm5/fd/+7+7WcYzV8AP9Fjsje\n" +
            "BChnSVZu9dWREAsK71xQl28vuSlbWhi1iDbVC7g4BGJLLtYSCisGAQQBl1UBBQEB\n" +
            "B0DrQXziToxj9TIEJl7j9Y/wkPD8R+7n8bKTyFx3cfMcWwMBCAeIdQQYFgoAHQUC\n" +
            "Yksu1gKeAQKbDAUWAgMBAAQLCQgHBRUKCQgLAAoJEKzyFvD5Pd0gnegA/2Nyxdkb\n" +
            "4GxGjgbQLx+sNGQT6Kwd65OncfgHtBr1uBPMAPkB5oDaNKXZA8U5dATdSguwVdYk\n" +
            "RanvVaO31tiy34ZRBLgzBGJLLtYWCSsGAQQB2kcPAQEHQD7amqdtf85lc8Th5Pvv\n" +
            "PdfxGUjYpMFpKvbdKZmI4bSfiNUEGBYKAH0FAmJLLtYCngECmwIFFgIDAQAECwkI\n" +
            "BwUVCgkIC18gBBkWCgAGBQJiSy7WAAoJEOEz2Vo79YylzN0A/iZAVklSJsfQslsh\n" +
            "R6/zMBufwCK1S05jg/5Ydaksv3QcAQC4gsxdFFne+H4Mmos4atad6hMhlqr0/Zyc\n" +
            "71ZdO5I/CAAKCRCs8hbw+T3dIGhqAQCIdVtCus336cDeNug+E9v1PEM3F/dt6GAq\n" +
            "SG8LJqdAGgEA8cUXdUBooOo/QBkDnpteke8Z3IhIGyGedc8OwJyVFwc=\n" +
            "=GUhm\n" +
            "-----END PGP PUBLIC KEY BLOCK-----\n";

    private static final String BOB_KEY = "-----BEGIN PGP PRIVATE KEY BLOCK-----\n" +
            "Version: PGPainless\n" +
            "Comment: A0D2 F316 0F6B 2CE5 7A50  FF32 261E 5081 9736 C493\n" +
            "Comment: bob@pgpainless.org\n" +
            "\n" +
            "lFgEYksu1hYJKwYBBAHaRw8BAQdAXTBT1OKN1GAvGC+fzuy/k34BK+d5Saa87Glb\n" +
            "iQgIxg8AAPwMI5DGqADFfl6H3Nxj3NxEZLasiFDpwEszluLVRy0jihGbtBJib2JA\n" +
            "cGdwYWlubGVzcy5vcmeIjwQTFgoAQQUCYksu1gkQJh5QgZc2xJMWIQSg0vMWD2ss\n" +
            "5XpQ/zImHlCBlzbEkwKeAQKbAQUWAgMBAAQLCQgHBRUKCQgLApkBAADvrAD/cWBW\n" +
            "mRkSfoCbEl22s59FXE7NPENrsJK8jxmWsWX3jbEA/AyXMCjwH6IhDgdgO7wH2z1r\n" +
            "cUb/hokiCcCaJs6hjKcInF0EYksu1hIKKwYBBAGXVQEFAQEHQCeURSBi9brhisUH\n" +
            "Dz0xN1NCgU5yeirx53xrQDFFx+d6AwEIBwAA/1GHX9+4Rg0ePsXGm1QIWL+C4rdf\n" +
            "AReCTYoS3EBiZVdADoyIdQQYFgoAHQUCYksu1gKeAQKbDAUWAgMBAAQLCQgHBRUK\n" +
            "CQgLAAoJECYeUIGXNsST8c0A/1dEIO9gsFB15UWDlTzN3S0TXQNN8wVzIMdW7XP2\n" +
            "7c6bAQCB5ChqQA9AB1020DLr28BAbSjI7mPdIWg2PpE7B1EXC5xYBGJLLtYWCSsG\n" +
            "AQQB2kcPAQEHQKP5NxT0ZhmRbrl3S6uwrUN248g1TEUR0DCVuLgyGSLpAAEA6bMa\n" +
            "GaUf3S55rkFDjFC4Cv72zc8E5ex2RKgbpxXxqhYQN4jVBBgWCgB9BQJiSy7WAp4B\n" +
            "ApsCBRYCAwEABAsJCAcFFQoJCAtfIAQZFgoABgUCYksu1gAKCRDJLjPCA2NIfylD\n" +
            "AP4tNFV23FBlrC57iesHVc+TTfNJ8rd+U7mbJvUgykcSNAEAy64tKPuVj+aA1bpm\n" +
            "gHxfqdEJCOko8UhVVP6ltiDUcAoACgkQJh5QgZc2xJP9TQEA1DNgFno3di+xGDEN\n" +
            "pwe9lmz8d/RWy/kuBT9S/3CMJjQBAKNBhHPuFfvk7RFbsmMrHsSqDFqIuUfGqq39\n" +
            "VzmiMp8N\n" +
            "=LpkJ\n" +
            "-----END PGP PRIVATE KEY BLOCK-----\n";
    private static final String BOB_CERT = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
            "Version: PGPainless\n" +
            "Comment: A0D2 F316 0F6B 2CE5 7A50  FF32 261E 5081 9736 C493\n" +
            "Comment: bob@pgpainless.org\n" +
            "\n" +
            "mDMEYksu1hYJKwYBBAHaRw8BAQdAXTBT1OKN1GAvGC+fzuy/k34BK+d5Saa87Glb\n" +
            "iQgIxg+0EmJvYkBwZ3BhaW5sZXNzLm9yZ4iPBBMWCgBBBQJiSy7WCRAmHlCBlzbE\n" +
            "kxYhBKDS8xYPayzlelD/MiYeUIGXNsSTAp4BApsBBRYCAwEABAsJCAcFFQoJCAsC\n" +
            "mQEAAO+sAP9xYFaZGRJ+gJsSXbazn0VcTs08Q2uwkryPGZaxZfeNsQD8DJcwKPAf\n" +
            "oiEOB2A7vAfbPWtxRv+GiSIJwJomzqGMpwi4OARiSy7WEgorBgEEAZdVAQUBAQdA\n" +
            "J5RFIGL1uuGKxQcPPTE3U0KBTnJ6KvHnfGtAMUXH53oDAQgHiHUEGBYKAB0FAmJL\n" +
            "LtYCngECmwwFFgIDAQAECwkIBwUVCgkICwAKCRAmHlCBlzbEk/HNAP9XRCDvYLBQ\n" +
            "deVFg5U8zd0tE10DTfMFcyDHVu1z9u3OmwEAgeQoakAPQAddNtAy69vAQG0oyO5j\n" +
            "3SFoNj6ROwdRFwu4MwRiSy7WFgkrBgEEAdpHDwEBB0Cj+TcU9GYZkW65d0ursK1D\n" +
            "duPINUxFEdAwlbi4Mhki6YjVBBgWCgB9BQJiSy7WAp4BApsCBRYCAwEABAsJCAcF\n" +
            "FQoJCAtfIAQZFgoABgUCYksu1gAKCRDJLjPCA2NIfylDAP4tNFV23FBlrC57iesH\n" +
            "Vc+TTfNJ8rd+U7mbJvUgykcSNAEAy64tKPuVj+aA1bpmgHxfqdEJCOko8UhVVP6l\n" +
            "tiDUcAoACgkQJh5QgZc2xJP9TQEA1DNgFno3di+xGDENpwe9lmz8d/RWy/kuBT9S\n" +
            "/3CMJjQBAKNBhHPuFfvk7RFbsmMrHsSqDFqIuUfGqq39VzmiMp8N\n" +
            "=1MqZ\n" +
            "-----END PGP PUBLIC KEY BLOCK-----\n";
}
