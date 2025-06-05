package com.beanpack.Wizard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class wizard {
    

    public static void saveKeyToWizard(String privateHash, String path) {
        String wizardString = makeWizardString(privateHash);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(wizardString);
        } catch (IOException e) {

        }

    }

    public static void saveEncryptedWizKey(String encryptedKey, String path) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(encryptedKey);
        } catch (IOException e) {

        }
    }

    //password encrypted secure model 
    public static String getEncryptedWizardKey(String privateHash, String password) throws Exception{
        String encryptedHash = secure.encrypt(privateHash, password);
        return makeWizardString(encryptedHash);
        
    }

    public static String wizardRead(String path) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String wizardKey = reader.readLine();
        String[] parts = wizardKey.split("::");
        String middieRib = parts[1];
        String retrievedHash = middieRib.substring(4, middieRib.length() - 4);
        reader.close();
        return retrievedHash;
    }

    public static String wizardReadKey(String wizardKey) throws IOException{
        String[] parts = wizardKey.split("::");
        String middieRib = parts[1];
        String retrievedHash = middieRib.substring(4, middieRib.length() - 4);
        return retrievedHash;
    }

    //password unlocking wizardKey
    public static String decryptWizKey(String encryptedHash, String password) throws Exception{
        return secure.decrypt(encryptedHash, password);
    }
    


    
    public static String numberChainGenerator(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
        digits.append(random.nextInt(10));
        }
        return digits.toString();
    }

    public static String wizardCheck(String path) throws IOException{
        File file = new File(path);
        if (file.exists()) {
            System.out.println("File exists!");
            return (wizardRead(path));
        } else {
            System.out.println("File does not exist.");
            return null;
        }
        

    }

    private static String makeWizardString(String hash){
        return numberChainGenerator(42) + "::" + numberChainGenerator(4) + hash + numberChainGenerator(4) + "::" + numberChainGenerator(24);
    }



    
    
    public static void main(String[] args) throws Exception {
        try {
            // Simulated private key (hex string)
            String privateKeyHex = "deadbeefcafebabefeedface1234567890abcdefabcdef1234567890abcdef";

            // Password for encryption
            String password = "superSecure123";

            // File path to simulate storing the encrypted wizard key
            String filePath = "testEncryptedWizardKey.txt";

            // Step 1: Encrypt the private key and wrap it
            String encryptedWizardString = wizard.getEncryptedWizardKey(privateKeyHex, password);

            // Step 2: Save to file
            wizard.saveEncryptedWizKey(encryptedWizardString, filePath);
            System.out.println("✅ Encrypted wizard key saved.");

            // Step 3: Load from file
            String loadedEncryptedWizardString = wizard.wizardRead(filePath);
            System.out.println("📥 Loaded from file: " + loadedEncryptedWizardString);


            // Step 5: Decrypt
            String decryptedKey = wizard.decryptWizKey(loadedEncryptedWizardString, password);
            System.out.println("🔓 Decrypted private key: " + decryptedKey);

            // Verify
            if (decryptedKey.equals(privateKeyHex)) {
                System.out.println("✅ SUCCESS: Decryption matches original.");
            } else {
                System.out.println("❌ FAILURE: Decrypted key does not match.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
    
    
}
