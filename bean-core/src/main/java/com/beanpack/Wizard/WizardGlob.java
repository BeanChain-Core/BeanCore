package com.beanpack.Wizard;

import org.iq80.leveldb.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class WizardGlob {

    private DB db;

    public WizardGlob() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        db = factory.open(new File("data/WizardGlob"), options);
    }

    
    public void saveKey(String label, String encryptedKey) { //save contract keys by hash 
        db.put(bytes(label), bytes(encryptedKey));
    }

    //save from unencrypted version
    public void saveRawKey(String label, String key, String password) throws Exception{
        String encryptedKey = wizard.getEncryptedWizardKey(key, password);
        db.put(bytes(label), bytes(encryptedKey));
    }

    
    public String getEncryptedKey(String label) {
        byte[] data = db.get(bytes(label));
        return data != null ? asString(data) : null;
    }

    //get raw key 
    public String getRawKey(String label, String password) throws Exception {
        byte[] data = db.get(bytes(label));
        String dataString = asString(data);
        return wizard.decryptWizKey(dataString, password);
        
    }

    
    public void deleteKey(String label) {
        db.delete(bytes(label));
    }

    
    public List<String> listLabels() {
        List<String> keys = new ArrayList<>();
        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                keys.add(asString(iterator.peekNext().getKey()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public void close() throws Exception {
        db.close();
    }

    
    private byte[] bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    private String asString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
