package com.beanpack.TXs;

import com.beanpack.Utils.ParamBuilder;

public class FundedCallTX extends TX{
    private ParamBuilder paramBuilder = new ParamBuilder();

    public FundedCallTX() {
        this.setType("cen");
    }

    public FundedCallTX(String from, String publicKeyHex, String contractAddress, double amount, int nonce, String tokenHash, long gasFee, String method, String params) {
        this.setFrom(from);
        this.setPublicKeyHex(publicKeyHex);
        this.setTo(contractAddress);
        this.setAmount(amount);
        this.setNonce(nonce);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("cen");
        this.setGasFee(gasFee);

        this.paramBuilder.add("method", method);
        this.paramBuilder.add("params", params);

        if (tokenHash != null) {
            this.paramBuilder.add("tokenHash", tokenHash);
        }

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }
}
