package crypto;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Pgp extends Thread {
    JTabbedPane pgpPanes;
    JFrame mainFrame;

    @Override
    public synchronized void start() {

    }

    // RSA
    JTabbedPane rsaPane = new JTabbedPane();
    static Rsa rsaBase = new Rsa();
    JPanel rsaGenerate = new JPanel();
    JPanel rsaEnDecrypt = new JPanel();

    public static void genRSA() {
        // RSA Generation with Helpful Progress updates
        new Thread(() -> {
            long startTime = new Date().getTime();
            JFrame window = new JFrame();
            window.setSize(400, 300);
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("RSA Generation");

            var textArea = new JTextArea("", 5, 50);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setVisible(true);

            var textPanel = new JScrollPane(textArea);
            textPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            textPanel.setVisible(true);
            window.add(textPanel);

            window.setVisible(true);
            textArea.append("-------RSA-------\n");

            textArea.append("-Generating a large Prime Number-\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            BigInteger f = rsaBase.largePrime(256);

            textArea.append(f.toString());
            textArea.append("\nFound");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            textArea.append("\n-Generating another large Prime Number-\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            BigInteger k = rsaBase.largePrime(256);

            textArea.append(k.toString());
            textArea.append("\nFound");

            textArea.append("\nCalculating N");
            rsaBase.n = f.multiply(k);
            textArea.append("\nCalculated n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            textArea.append("\nCalculating φ");
            BigInteger phi = (f.subtract(BigInteger.valueOf(1)).multiply(k.subtract(BigInteger.valueOf(1))));
            textArea.append("\nCalculated φ");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            textArea.append("\nCalculating Public (e) key");
            BigInteger temp;
            do {
                temp = rsaBase.largePrime(255);
            } while (!(temp.gcd(phi).compareTo(BigInteger.ONE) == 0) || (temp.compareTo(f) != -1 || temp.compareTo(k) != -1));
            textArea.append("\nCalculated Public key");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            rsaBase.e = temp;
            textArea.append("\nCalculating Private Key using Extended Euclidean Algorithm");
            rsaBase.d = rsaBase.inverse(phi, rsaBase.e);
            textArea.append("\nCalculated Private key");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            // textArea.append("\nCombining N with E and D");
            // try{Thread.sleep(1000);} catch (Exception e) {}
            // textArea.append("\nCompleted.");

            textArea.append("\nSanity Check:");
            textArea.append("\nExpected answer: 1; Actual answer: " + ((rsaBase.e.multiply(rsaBase.d)).mod(phi)));
            textArea.setCaretPosition(textArea.getDocument().getLength());

            // try{Thread.sleep(5000);} catch (Exception e) {}
            // textArea.append("\nIf 'Actual Answer' is not 1, the RSA process failed. \nPlease report this incident and try again.");
            // textArea.setCaretPosition(textArea.getDocument().getLength());
            textArea.append("\nThe size of the public key is: " + rsaBase.e.toString(2).length() + " bits long");
            textArea.append("\nThe size of the private key is: " + rsaBase.d.toString(2).length() + " bits long");
            textArea.append("\nThe size of the n key is: " + rsaBase.n.toString(2).length() + " bits long");

            textArea.append("\nKey Generation has succesfully completed.");
            rsaBase.packageKeys();

            textArea.append("\nThe RSA Generation has been completed in " + (new Date().getTime() - startTime) / 1000 + " seconds.\n This window will automatically close in 10 seconds.\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            try {
                Thread.sleep(10000);
            } catch (Exception ignored) {
            }
            // window.setEnabled(false);
            // window.setVisible(false);
        }).start();

    }


    private void setupRSA() {
        var gl = new GridLayout(10, 10);
        // this.rsaGenerate = new JPanel();

        // Generate Key
        rsaPane.add("Generate", this.rsaGenerate);
        this.rsaGenerate.setLayout(gl);

        class ButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent eventPushed) {
                if (eventPushed.getActionCommand().equals("Generate")) {
                    try {
                        genRSA();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        JButton j = new JButton("Generate");
        j.setPreferredSize(new Dimension(100, 50));
        this.rsaGenerate.add(j);
        j.addActionListener(
                new ButtonListener()
        );

        // Encrypt and Decrypt
        rsaPane.add("Encrypt/Decrypt", this.rsaEnDecrypt);
        this.rsaEnDecrypt.setLayout(gl);

        JTextArea cipherPlainField = new JTextArea("", 100, 100);
        var textPanel = new JScrollPane(cipherPlainField);
        textPanel.setVisible(true);
        textPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textPanel.setVisible(true);
        rsaEnDecrypt.add(textPanel);

    }

    Pgp() {
        mainFrame = new JFrame();

        pgpPanes = new JTabbedPane();

        mainFrame.setTitle("PGP");

        try {
            File pathToFile = new File("crypto/logo.png");
            Image image = ImageIO.read(pathToFile);
            mainFrame.setIconImage(image);
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        mainFrame.setSize(500, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        pgpPanes.addTab("RSA", rsaPane);

        setupRSA();

        mainFrame.add(pgpPanes);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new Pgp();
    }
}
