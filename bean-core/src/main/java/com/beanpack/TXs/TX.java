package com.beanpack.TXs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;

import com.beanpack.Utils.*;
import com.beanpack.crypto.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a core Layer 1 transaction on the BeanChain network.
 * 
 * <p>This class encapsulates all required fields for a valid blockchain transaction,
 * including sender, recipient, amount, gas fee, nonce, public key, and signature. 
 * Each transaction also includes metadata such as timestamp, status, type, and optional
 * user-defined metadata.
 * 
 * <p>Transactions can be signed, hashed, serialized to JSON, or parsed from JSON.
 * This class is used for both network communication and on-chain storage.
 */
public class TX {
    private String from;
    private int nonce;
    private String publicKeyHex;
    private String to;
    private double amount;
    private long timeStamp;
    private String txHash;
    private String signature;
    private long gasFee;
    // flags 
    private String status = "pending";
    private String type = "transfer";
    private String meta;


    public TX(){

    }

    public TX(String from, String publicKeyHex, String to, double amount, int nonce, long gasFee) {
        this.from = from;
        this.publicKeyHex = publicKeyHex;
        this.to = to;
        this.amount = amount;
        this.timeStamp = System.currentTimeMillis();
        this.nonce = nonce;
        this.gasFee = gasFee;
        this.txHash = generateHash();
    }

    public String getFrom() {return from;}
    public int getNonce() {return nonce;}
    public String getPublicKeyHex() {return publicKeyHex;}
    public String getTo() {return to;}
    public double getAmount() {return amount;}
    public long getTimeStamp() {return timeStamp;}
    public String getTxHash() {return txHash;}
    public String getSignature() {return signature;}
    public long getGasFee() {return gasFee;}
    public String getStatus() {return status;}
    public String getType() {return type;}
    public String getMeta() {return meta;}

    public void setFrom(String from) {this.from = from;}
    public void setNonce(int nonce) {this.nonce = nonce;}
    public void setPublicKeyHex(String publicKeyHex) {this.publicKeyHex = publicKeyHex;}
    public void setTo(String to) {this.to = to;}
    public void setAmount(double amount) {this.amount = amount;}
    public void setTimeStamp(long timeStamp) {this.timeStamp = timeStamp;}
    public void setTxHash(String txHash) {this.txHash = txHash;}
    public void setSignature(String signature) {this.signature = signature;}
    public void setGasFee(long gasFee) {this.gasFee = gasFee;}
    public void setStatus(String status) {this.status = status;}
    public void setType(String type) {this.type = type;}
    public void setMeta(String meta) {this.meta = meta;}

    public String generateHash(){
        // String metaSafe = (meta == null) ? "" : meta;
        // String typeSafe = (type == null) ? "" : type;

        try {
            String data = from + to + String.format("%.8f", amount) + timeStamp + nonce + gasFee; //  TODO: + metaSafe + typeSafe , add meta data to the hash so it cant be altered after tx validated
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for(byte b: hash){
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createJSON() {
        String jsonString = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            System.out.println(e);
        }
        return jsonString;
    }
 
    public static TX fromJSON(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TX tx = objectMapper.readValue(json, TX.class);
    
            // 🔧 Enforce 8-decimal precision on amount
            if (tx != null) {
                tx.amount = Math.round(tx.amount * 1e8) / 1e8;
            }
    
            return tx;
        } catch (Exception e) {
            System.out.println("Failed to parse TX JSON: " + e.getMessage());
            return null;
        }
    }

    public void sign(PrivateKey privateKey) throws Exception{
        publicKeyHex = WalletGenerator.generatePublicKey(privateKey);
        
        byte[] transactionHash = hex.hexToBytes(this.txHash);
        signature = SHA256TransactionSigner.signSHA256Transaction(privateKey, transactionHash);
        
    }

    public void debugHashValues() {
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Amount: " + amount);
        System.out.println("Timestamp: " + timeStamp);
        System.out.println("Nonce: " + nonce);
        System.out.println("Data: " + from + to + amount + timeStamp + nonce);
        System.out.println("Generated Hash: " + generateHash());
        System.out.println("Stored Hash: " + txHash);
        System.out.println("Gas Fee In Beantoshi: " + gasFee);
    }
}
