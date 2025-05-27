package com.beanpack.Models;

import java.util.HashMap;
import java.util.Map;

import com.beanpack.Utils.beantoshinomics;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Layer2Wallet {
    private String address;
    private int l2Nonce;
    private Map<String, Long> tokenBalances;  // changed from Integer to Long

    // Required no-arg constructor for Jackson
    public Layer2Wallet() {
        this.tokenBalances = new HashMap<>();
    }

    public Layer2Wallet(String address) {
        this.address = address;
        this.l2Nonce = 0;
        this.tokenBalances = new HashMap<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getL2Nonce() {
        return l2Nonce;
    }

    public void setL2Nonce(int l2Nonce) {
        this.l2Nonce = l2Nonce;
    }

    public Map<String, Long> getTokenBalances() {
        return tokenBalances;
    }

    public void setTokenBalances(Map<String, Long> tokenBalances) {
        this.tokenBalances = tokenBalances;
    }

    public void incrementNonce() {
        this.l2Nonce++;
    }

    // Returns balance as BEAN (double) 
    public double getBalance(String tokenHash) {
        long beantoshi = tokenBalances.getOrDefault(tokenHash, 0L);
        return beantoshinomics.toBean(beantoshi);
    }

    // Sets balance using BEAN (double), stores as long
    public void setBalance(String tokenHash, double amountBean) {
        long beantoshi = beantoshinomics.toBeantoshi(amountBean);
        tokenBalances.put(tokenHash, beantoshi);
    }

    // Adjusts balance by delta in BEAN (double)
    public void adjustBalance(String tokenHash, double deltaBean) {
        long deltaBeantoshi = beantoshinomics.toBeantoshi(deltaBean);
        long current = tokenBalances.getOrDefault(tokenHash, 0L);
        tokenBalances.put(tokenHash, current + deltaBeantoshi);
    }

    // Optional: direct beantoshi getter for internal logic
    public long getBalanceRaw(String tokenHash) {
        return tokenBalances.getOrDefault(tokenHash, 0L);
    }

    public void setBalanceRaw(String tokenHash, long beantoshi) {
        tokenBalances.put(tokenHash, beantoshi);
    }

    public void adjustBalanceRaw(String tokenHash, long deltaBeantoshi) {
        long current = tokenBalances.getOrDefault(tokenHash, 0L);
        tokenBalances.put(tokenHash, current + deltaBeantoshi);
    }
}
