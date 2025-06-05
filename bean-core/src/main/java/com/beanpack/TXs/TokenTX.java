package com.beanpack.TXs;

import com.beanpack.Utils.ParamBuilder;


public class TokenTX extends TX {
    private ParamBuilder paramBuilder = new ParamBuilder();

    public TokenTX() {
        this.setType("token");
    }

    public TokenTX(String from, String publicKeyHex, String to, double amount, int layer2Nonce, String tokenHash, long gasFee) {
        this.setFrom(from);
        this.setPublicKeyHex(publicKeyHex);
        this.setTo(to);
        this.setAmount(amount);
        this.setNonce(layer2Nonce);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("token");
        this.setGasFee(gasFee);

        this.paramBuilder
            .add("tokenHash", tokenHash)
            .add("amount", amount);

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }

     
    public TokenTX(String contractAddress, String contractPubKeyHex, String to, double amount, String tokenHash, long gasFee, String caller, String callHash, String callerPublicKey, String callSignature, int callerLayer2Nonce, String contract, String callMethod, String contractHash) {
        this.setFrom(contractAddress);
        this.setPublicKeyHex(contractPubKeyHex);
        this.setTo(to);
        this.setAmount(amount);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("token");
        this.setGasFee(gasFee);

        this.paramBuilder
            .add("isCEN", true)
            .add("caller", caller)
            .add("callHash", callHash)
            .add("callerPublicKey", callerPublicKey)
            .add("callerLayer2Nonce", callerLayer2Nonce)
            .add("callSignature", callSignature)
            .add("tokenHash", tokenHash)
            .add("amount", amount)
            .add("contract", contract)
            .add("contractHash", contractHash)
            .add("callMethod", callMethod);

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }

    public TokenTX(String contractAddress, String contractPubKeyHex, String to, double amount, String tokenHash, long gasFee, String caller, String callHash, String callerPublicKey, String callSignature, int callerLayer2Nonce, String contract, String callMethod, String contractHash, String executeMethod) {
        this.setFrom(contractAddress);
        this.setPublicKeyHex(contractPubKeyHex);
        this.setTo(to);
        this.setAmount(amount);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("token");
        this.setGasFee(gasFee);

        this.paramBuilder
            .add("isCEN", true)
            .add("caller", caller)
            .add("callHash", callHash)
            .add("callerPublicKey", callerPublicKey)
            .add("callerLayer2Nonce", callerLayer2Nonce)
            .add("callSignature", callSignature)
            .add("tokenHash", tokenHash)
            .add("amount", amount)
            .add("contract", contract)
            .add("contractHash", contractHash)
            .add("callMethod", callMethod)
            .add("execute", executeMethod);

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }


    public TokenTX(String from, String publicKeyHex, double amount, int layer2Nonce, String tokenHash, long gasFee) {
        this.setFrom(from);
        this.setPublicKeyHex(publicKeyHex);
        this.setTo("BEANX:0xBURNTOKEN");
        this.setAmount(amount);
        this.setNonce(layer2Nonce);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("token");
        this.setGasFee(gasFee);

        String executeValue = "burn";

        this.paramBuilder
            .add("tokenHash", tokenHash)
            .add("amount", amount)
            .add("execute", executeValue);

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }

    

    public TokenTX addParam(String key, boolean value) {
        this.paramBuilder.add(key, value);
        return this;
    }

    public void finalizeParams() {
        if (this.getMeta() == null) {
            this.setMeta(this.paramBuilder.build());
            this.setTxHash(this.generateHash()); // Refresh hash if meta was changed
        }
    }
}
