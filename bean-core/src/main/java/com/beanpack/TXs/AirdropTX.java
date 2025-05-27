package com.beanpack.TXs;

import com.beanpack.Utils.ParamBuilder;

public class AirdropTX extends TX {
    private ParamBuilder paramBuilder = new ParamBuilder();

    public AirdropTX() {
        this.setType("airdrop");
    }

    public AirdropTX(String from, String to, double amount, int nonce, String fundWallet, long gasFee) {
        this.setFrom(from); // RN signs this
        this.setTo(to);     // Recipient
        this.setAmount(amount);
        this.setNonce(nonce);
        this.setTimeStamp(System.currentTimeMillis());
        this.setType("airdrop");
        this.setGasFee(gasFee);

        this.paramBuilder.add("fundWallet", fundWallet);
        this.setMeta(this.paramBuilder.build());

        this.setTxHash(this.generateHash());
    }
}