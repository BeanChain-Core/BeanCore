package com.beanpack.TXs;

import com.beanpack.Utils.ParamBuilder;

public class StakeTX extends TX {
    private ParamBuilder paramBuilder = new ParamBuilder();

    public StakeTX() {
        this.setType("stake");
    }

    public StakeTX(String from, String publicKeyHex, double amount, int nonce, String mode, String ip, int port, String tokenHash, long gasFee) {
        this.setFrom(from);
        this.setPublicKeyHex(publicKeyHex);
        this.setTo("BEANX:0xSTAKECONTRACTPLACEHOLDER");
        this.setAmount(amount);
        this.setNonce(nonce);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("stake");
        this.setGasFee(gasFee);

        this.paramBuilder.add("mode", mode);
        this.paramBuilder.add("ip", ip);
        this.paramBuilder.add("port", port);

        if (tokenHash != null) {
            this.paramBuilder.add("tokenHash", tokenHash);
        }

        this.setMeta(this.paramBuilder.build());
        this.setTxHash(this.generateHash()); 
    }

    
}
