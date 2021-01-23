package crypto;

import com.sun.net.httpserver.Authenticator;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.*;

public class Pgp{
    JTabbedPane pgpPanes;
    JFrame mainFrame;

    // RSA
    JTabbedPane rsaPane = new JTabbedPane();
    static File defaultKeyPath;
    static Rsa rsaBase = new Rsa();
    JPanel rsaGenerate = new JPanel();
    JPanel rsaEnDecrypt = new JPanel();
    JPanel rsaSaveLoad = new JPanel();

    public static void genRSA(int length) {
        // RSA Generation with Helpful Progress updates
        new Thread(() -> {

            System.out.println(length);
            long startTime = new Date().getTime();
            JFrame window = new JFrame();
            window.setSize(400, 300);
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
            textArea.append("By the end of key generation, you should have 2 keys.\n Please note that generation takes time.\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            BigInteger phi;
            int size = 256;
            do {
                textArea.append("Generating 2 large Prime Numbers\n");
                var b = rsaBase.twoPrimeGen(256);

                textArea.append("Calculating N and φ\n");
                phi = rsaBase.nphi(b);

                textArea.append("Calculating e and d keys\n");
                rsaBase.edcalc(phi, b, size);

                textArea.append("Packaging the Keys (B64)\n");
                rsaBase.packageKeys();
                textArea.setCaretPosition(textArea.getDocument().getLength());

            } while (rsaBase.fencrypt("TEST").length() != 1644 || !(rsaBase.e.multiply(rsaBase.d).mod(phi).equals(BigInteger.ONE)));

            textArea.append("\nThe size of the public key is: " + rsaBase.e.toString(2).length() + " bits long");
            textArea.append("\nThe size of the private key is: " + rsaBase.d.toString(2).length() + " bits long");
            textArea.append("\nThe RSA Generation has completed in " + (new Date().getTime() - startTime) / 1000 + " seconds.\n This window will automatically close in 10 seconds.\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());

            try {
                Thread.sleep(10000);
            } catch (Exception ignored) {/*pass*/}

            window.setEnabled(false);
            window.setVisible(false);

        }).start();
    }

    enum status{
        Error,
        Success
    }

    private void setupRSA() {
        // var gl = new GridLayout(10, 10);
        // this.rsaGenerate = new JPanel();
        rsaPane.add("Generate", this.rsaGenerate);
        rsaPane.add("Encrypt/Decrypt", this.rsaEnDecrypt);
        rsaPane.add("Load/Save keys", this.rsaSaveLoad);

        // Generate Key
        this.rsaGenerate.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextField lengthField = new JTextField("2048", 7);
        lengthField.setHorizontalAlignment(JTextField.RIGHT);
//        var menuBar = new MenuBar();
//        var menu = new Menu("A Menu");
//        menu.getAccessibleContext().setAccessibleDescription(
//                "The only menu in this program that has menu items");
//        menu.add(new MenuItem("Status"));
//        menuBar.add(menu);
//        mainFrame.setMenuBar(menuBar);

        class ButtonListener implements ActionListener {

            void statusDialog(String title, String message, status st){
                switch (st) {
                    case Error:
                        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    case Success:
                        JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                    default:
                        break;
                }
            }

            @Override
            public void actionPerformed(ActionEvent eventPushed) {
                switch (eventPushed.getActionCommand()) {
                    case "Generate Keys": {
                        try {
                            genRSA(256);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                    case "Load Private Key": {
                        // FIXME: CHANGE TO PROPER SAVABLE PATH IN GUI, SETTINGS
                        var fc = new JFileChooser("C:\\Users\\shynn");
                        fc.setDialogTitle("Select your Private Key");
                        fc.setApproveButtonText("Load");
                        fc.setApproveButtonToolTipText("Load Private Key");
                        int returnVal = fc.showOpenDialog(mainFrame);

                        FileInputStream fileInputStream;
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            var file = fc.getSelectedFile();
                            try {
//                                myReader = new BufferedReader(new FileReader(file));
                                fileInputStream = new FileInputStream(file);
                                String text = "";
                                byte[] crunchifyValue = new byte[(int) file.length()];
                                fileInputStream.read(crunchifyValue);
                                fileInputStream.close();

                                String fileContent = new String(crunchifyValue, "UTF-8");
                                int loadingStatus = rsaBase.unpackagePrivate(fileContent);
                                if (loadingStatus == 1){

                                } else if (loadingStatus == -1){
                                    statusDialog("Error", "Your key does not seem to be valid", status.Error);
                                }
                            } catch (Exception e){
                                System.out.println("Error");
                            } finally {
                            }
                            statusDialog("Success", "Succesfully Loaded Private Key", status.Success);
                        }
                        break;
                    }
                    case "Load Public Key": {
                        // FIXME: CHANGE TO PROPER SAVABLE PATH IN GUI, SETTINGS
                        var fc = new JFileChooser("C:\\Users\\shynn");
                        fc.setDialogTitle("Select your Public Key");
                        fc.setApproveButtonText("Load");
                        fc.setApproveButtonToolTipText("Load Public Key");
                        int returnVal = fc.showOpenDialog(mainFrame);

                        FileInputStream fileInputStream;
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            var file = fc.getSelectedFile();
                            try {
//                                myReader = new BufferedReader(new FileReader(file));
                                fileInputStream = new FileInputStream(file);
                                String text = "";
                                byte[] crunchifyValue = new byte[(int) file.length()];
                                fileInputStream.read(crunchifyValue);
                                fileInputStream.close();

                                String fileContent = new String(crunchifyValue, "UTF-8");
                                int loadingStatus = rsaBase.unpackagePublic(fileContent);
                                if (loadingStatus == 1){

                                } else if (loadingStatus == -1){
                                    statusDialog("Error", "Your key does not seem to be valid", status.Error);
                                }
                            } catch (Exception e){
                                System.out.println("Error");
                            } finally {
                            }
                            statusDialog("Success", "Succesfully Loaded Public Key", status.Success);
                        }
                        break;
                    }
                    case "Save Private Key": {
                        // FIXME: CHANGE TO PROPER SAVABLE PATH IN GUI, SETTINGS
                        JFileChooser fileChooser = new JFileChooser();
                        int option = fileChooser.showSaveDialog(mainFrame);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File file = new File(fileChooser.getSelectedFile().toString() + ".privatekey");
                            try {
                                file.createNewFile();
                                FileWriter myWriter = new FileWriter(file);
                                myWriter.write(rsaBase.privateKey);
                                myWriter.close();
                                statusDialog("Success", "Private Key saved", status.Success);
                            } catch (IOException e) {
                                statusDialog("Error", e.getStackTrace().toString(), status.Error);
                            }
                        }
                        break;
                    }
                    case "Save Public Key": {
                        // FIXME: CHANGE TO PROPER SAVABLE PATH IN GUI, SETTINGS
                        JFileChooser fileChooser = new JFileChooser();
                        int option = fileChooser.showSaveDialog(mainFrame);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File file = new File(fileChooser.getSelectedFile().toString() + ".pubkey");
                            try {
                                file.createNewFile();
                                FileWriter myWriter = new FileWriter(file);
                                myWriter.write(rsaBase.publicKey);
                                myWriter.close();
                                statusDialog("Success", "Public Key saved", status.Success);
                            } catch (IOException e) {
                                statusDialog("Error", e.getStackTrace().toString(), status.Error);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 1;
        this.rsaGenerate.add(new JLabel("Key size (in bits): "), gbc);

        JButton generateKeys = new JButton("Generate Keys");
        generateKeys.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1; gbc.gridy = 1;
        this.rsaGenerate.add(lengthField, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        this.rsaGenerate.add(generateKeys, gbc);
        generateKeys.addActionListener(
                new ButtonListener() // See ButtonListener
        );

        // Encrypt and Decrypt

        // To Decrypt/Encrypt text
        JTextArea cipherPlainField = new JTextArea("", 100, 100);
        var textPanel = new JScrollPane(cipherPlainField);
        textPanel.setVisible(true);
        textPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        cipherPlainField.setEditable(true);
        rsaEnDecrypt.add(textPanel);
        textPanel.add(cipherPlainField);

        // Load/Save keys
        rsaSaveLoad.setLayout(new GridBagLayout());
        JButton saveDKeys = new JButton("Save Private Key");
        saveDKeys.addActionListener(new ButtonListener());
        JButton saveEKeys = new JButton("Save Public Key");
        saveEKeys.addActionListener(new ButtonListener());
        JButton loadDKeys = new JButton("Load Private Key");
        loadDKeys.addActionListener(new ButtonListener());
        JButton loadEKeys = new JButton("Load Public Key");
        loadEKeys.addActionListener(new ButtonListener());

        saveDKeys.setPreferredSize(new Dimension(150, 30));
        saveEKeys.setPreferredSize(new Dimension(150, 30));
        loadDKeys.setPreferredSize(new Dimension(150, 30));
        loadEKeys.setPreferredSize(new Dimension(150, 30));

        var z = new JLabel("<--Save/Load-->");
        z.setPreferredSize(new Dimension(125, 25));
        z.setFont(new Font("Times New Roman", Font.BOLD, 16));

        gbc.gridx = 0; gbc.gridy = 1;
        rsaSaveLoad.add(saveDKeys, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        rsaSaveLoad.add(saveEKeys, gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        rsaSaveLoad.add(loadDKeys, gbc);
        gbc.gridx = 2; gbc.gridy = 3;
        rsaSaveLoad.add(loadEKeys, gbc);

        var descriptor = new JTextArea("Use this tool to save or load your stored public and private keys.\n" +
                "Please name your keys appropriately:\n" +
                "You cannot get them back if you accidentally delete them.");
        descriptor.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 16));
        descriptor.setBackground(new Color(0xEEEEEE));
        gbc.insets = new Insets(50, 10, 10, 10);
        gbc.gridwidth = 7; gbc.gridheight = 3;
        gbc.gridx = 0; gbc.gridy = 5;
        rsaSaveLoad.add(descriptor, gbc);

        gbc.gridheight = 1; gbc.gridwidth = 1;
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        rsaSaveLoad.add(z, gbc);
    }

    Pgp() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {;}
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
