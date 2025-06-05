package com.beanpack.Utils;

import java.util.ArrayList;
import java.util.List;

import com.beanpack.TXs.TX;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TXSorter {
    private List<TX> transferTX = new ArrayList<>();
    private List<TX> mintTX = new ArrayList<>();
    private List<TX> tokenTX = new ArrayList<>();
    private List<TX> tokenCENTX = new ArrayList<>();
    private List<TX> stakeTX = new ArrayList<>();
    private List<TX> fundedCallTX = new ArrayList<>();

    public void sort(List<TX> memPool) throws Exception {
        for (TX tx : memPool) {
            String type = tx.getType();
            if (type == null) continue;

            switch (type) {
                case "transfer":
                case "airdrop":
                    transferTX.add(tx);
                    break;
                case "mint":
                    mintTX.add(tx);
                    break;
                case "token":
                    JsonNode metaNode = getMetaNode(tx);
                    boolean isCEN = metaNode != null && metaNode.has("isCEN") && metaNode.get("isCEN") != null && metaNode.get("isCEN").asBoolean();
                    if (isCEN) {
                        tokenCENTX.add(tx);
                    } else {
                        tokenTX.add(tx);
                    }
                    break;
                case "stake":
                    stakeTX.add(tx);
                    break;
                case "cen":
                    fundedCallTX.add(tx);
                    break;
                default:
                    System.out.println("ERROR: Unrecognized TX type for hash: " + tx.getTxHash());
                    break;
            }
        }
    }

    private JsonNode getMetaNode(TX tx) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(tx.getMeta());
    }

    public List<TX> getTransferTX() { return transferTX; }
    public List<TX> getMintTX() { return mintTX; }
    public List<TX> getTokenTX() { return tokenTX; }
    public List<TX> getTokenCENTX() { return tokenCENTX; }
    public List<TX> getStakeTX() { return stakeTX; }
    public List<TX> getFundedCallTX() { return fundedCallTX; }

    private int getLayer2Nonce(TX tx) {
        try {
            JsonNode meta = new ObjectMapper().readTree(tx.getMeta());
            if (meta.has("callerLayer2Nonce")) {
                return meta.get("callerLayer2Nonce").asInt(); // for tokenCENTX
            }
        } catch (Exception e) {
            // fallback 
        }
        return tx.getNonce(); // normal tokenTX
    }

    private List<TX> sortByFeeAndNonce(List<TX> txs) {
        txs.sort((a, b) -> {
            int feeCompare = Long.compare(b.getGasFee(), a.getGasFee());
            if (feeCompare != 0) return feeCompare;
    
            // For tokenTX and tokenCENTX, use L2 sender and L2 nonce
            boolean isL2 = a.getType().equals("token") || a.getType().equals("tokenCENTX");
    
            String fromA = isL2 ? getL2Sender(a) : a.getFrom();
            String fromB = isL2 ? getL2Sender(b) : b.getFrom();
    
            int fromCompare = fromA.compareTo(fromB);
            if (fromCompare != 0) return fromCompare;
    
            int nonceA = isL2 ? getLayer2Nonce(a) : a.getNonce();
            int nonceB = isL2 ? getLayer2Nonce(b) : b.getNonce();
    
            return Integer.compare(nonceA, nonceB);
        });
    
        return txs;
    }

    private String getL2Sender(TX tx) {
        try {
            JsonNode meta = new ObjectMapper().readTree(tx.getMeta());
            if (meta.has("caller")) {
                return meta.get("caller").asText();
            }
        } catch (Exception e) {}
        return tx.getFrom();
    }

    public void sortEachList() {
        transferTX = sortByFeeAndNonce(transferTX);
        mintTX = sortByFeeAndNonce(mintTX);
        tokenTX = sortByFeeAndNonce(tokenTX);
        tokenCENTX = sortByFeeAndNonce(tokenCENTX);
        stakeTX = sortByFeeAndNonce(stakeTX);
        fundedCallTX = sortByFeeAndNonce(fundedCallTX);
    }
}
