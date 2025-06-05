package com.beanpack.Block;

public class BlockHeader {
    private String validator;
    private int height;
    private String previousHash;
    private long gasFeeReward;

    public BlockHeader() {
    }

    
    public BlockHeader(String validator, int height, String previousHash, long gasFeeReward) {
        this.validator = validator;
        this.height = height;
        this.previousHash = previousHash;
        this.gasFeeReward = gasFeeReward;
    }

    // Getters and setters
    public String getValidator() { return validator; }
    public void setValidator(String validator) { this.validator = validator; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

    public long getGasFeeReward() { return gasFeeReward; }
    public void setGasFeeReward(long gasFeeReward) { this.gasFeeReward = gasFeeReward; }
}

