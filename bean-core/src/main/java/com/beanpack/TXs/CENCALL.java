package com.beanpack.TXs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;

import com.beanpack.Utils.ParamBuilder;
import com.beanpack.Utils.hex;
import com.beanpack.crypto.SHA256TransactionSigner;
import com.beanpack.crypto.WalletGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CENCALL {
    private String caller;
    private String callerPublicKeyHex;
    private String callerPrivateKeyHex;
    private String callHash;
    private String callSignature;
    private String contract;
    private String contractHash;
    private String cenIP;
    private String method;
    private String params;
    private ParamBuilder paramBuilder = new ParamBuilder();

    public String getCaller() { return caller; }
    public String getContract() { return contract; }
    public String getContractHash() { return contractHash; }
    public String getCenIP() { return cenIP; }
    public String getMethod() { return method; }
    public String getParams() { return this.params;}

    public void setCaller(String caller) { this.caller = caller; }
    public void setCallerPublicKeyHex(String pubKey) { this.callerPublicKeyHex = pubKey; }
    public void setCallerPrivateKeyHex(String privKey) { this.callerPrivateKeyHex = privKey; }
    public void setCallHash(String callHash) { this.callHash = callHash; }
    public void setCallSignature(String sig) { this.callSignature = sig; }
    public void setContract(String contract) { this.contract = contract; }
    public void setContractHash(String contractHash) { this.contractHash = contractHash; }
    public void setCenIP(String cenIP) { this.cenIP = cenIP; }
    public void setMethod(String method) { this.method = method; }
    public void setParams(String paramsJson) {
        this.params = paramsJson;
        this.paramBuilder.loadFromJson(paramsJson); 
    }
    

    public CENCALL() {
        this.paramBuilder = new ParamBuilder();
    }

    public CENCALL(String caller, String callerPrivateKeyHex, String contract, String contractHash, String cenIP, String method) throws Exception {
        this.caller = caller;
        this.contract = contract;
        this.callerPrivateKeyHex = callerPrivateKeyHex;
        this.contractHash =contractHash;
        this.cenIP = cenIP;
        this.method = method;
        this.callHash = this.genHash(); 
        this.callSignature = this.sign(); 

        this.paramBuilder 
                .add("contractHash", this.contractHash)
                .add("cenIP", String.valueOf(this.cenIP))
                .add("callHash", this.callHash)
                .add("caller", this.caller)
                .add("callerPublicKey", this.callerPublicKeyHex)
                .add("callSignature", this.callSignature);
    }

    public String genHash(){
        try {
            String data = caller + contract + contractHash + method;
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

    public String sign() throws Exception {
        PrivateKey key = WalletGenerator.restorePrivateKey(callerPrivateKeyHex);
        byte[] hash = hex.hexToBytes(callHash);
        String signature = SHA256TransactionSigner.signSHA256Transaction(key, hash);

        return signature;
    }

    //params 

    public CENCALL addParam(String key, String value) {
        this.paramBuilder.add(key, value);
        return this;
    }
    
    public CENCALL addParam(String key, int value) {
        this.paramBuilder.add(key, value);
        return this;
    }
    
    public CENCALL addParam(String key, boolean value) {
        this.paramBuilder.add(key, value);
        return this;
    }

    public void finalizeParams() {
        this.params = this.paramBuilder.build();
        
    }

    // for sending call via network sockets 

    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("caller", caller);
        node.put("callerPublicKeyHex", callerPublicKeyHex);
        node.put("callHash", callHash);
        node.put("callSignature", callSignature);
        node.put("contract", contract);
        node.put("contractHash", contractHash);
        node.put("cenIP", String.valueOf(cenIP));
        node.put("method", method);
        node.put("params", params);

        return node.toString();
    }

    public static CENCALL fromJSON(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = (ObjectNode) mapper.readTree(json);
    
        CENCALL call = new CENCALL(
            node.get("caller").asText(),
            null, // no private key on incoming call
            node.get("contract").asText(),
            node.get("contractHash").asText(),
            null, // IP isn't needed server-side
            node.get("method").asText()
        );
    
        call.callHash = node.get("callHash").asText();
        call.callSignature = node.get("callSignature").asText();
        call.callerPublicKeyHex = node.get("callerPublicKeyHex").asText();
        call.params = node.get("params").asText();
    
        return call;
    }

}
