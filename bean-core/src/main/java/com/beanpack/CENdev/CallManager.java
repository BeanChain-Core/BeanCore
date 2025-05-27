package com.beanpack.CENdev;


import com.beanpack.TXs.TX;
import com.beanpack.Utils.hex;
import com.beanpack.crypto.TransactionVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The {@code CallManager} class is responsible for parsing and handling the
 * contents of a CENCALL JSON string. It provides structured access
 * to the contract name, method, parameters, and caller metadata, along with 
 * validation and transaction extraction utilities.
 * 
 * <p>This class is primarily used by Contract Execution Nodes (CENs) to unpack,
 * validate, and route incoming smart contract calls submitted as raw JSON.
 * 
 * <p>A JSON string compatible with this class can be obtained by calling 
 * {@code toJsonString()} on a {@code CENCALL} instance.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CENCALL call = new CENCALL(...);
 * String json = call.toJsonString();
 * CallManager cm = new CallManager(json);
 * if (cm.isSignatureValid()) {
 *     // Handle the contract call
 * }
 * }</pre>
 */
public class CallManager {
    private String contractName;
    private String method;
    private String paramsRaw;
    private JsonNode fullCall;
    private JsonNode unpackedParams;

    public CallManager(String cencallJSON) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        this.fullCall = mapper.readTree(cencallJSON);

        this.contractName = fullCall.get("contract").asText();
        this.method = fullCall.get("method").asText();
        
        if (!fullCall.get("params").isTextual()) {
            throw new IllegalArgumentException("params field must be a JSON string");
        }

        this.paramsRaw = fullCall.get("params").asText(); 
        this.unpackedParams = mapper.readTree(paramsRaw);
    }

    public String getContractName() {
        return contractName;
    }

    public String getMethod() {
        return method;
    }

    public String getParams() {
        return paramsRaw;
    }

    // debox params
    public JsonNode getParamsAsJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(paramsRaw);
    }

    // raw param 
    public String getContractHash() {
        return unpackedParams.get("contractHash").asText();
    }

    public String getCaller() {
        return unpackedParams.get("caller").asText();
    }

    public String getCallSignature() {
        return unpackedParams.get("callSignature").asText();
    }

    public String getCallHash() {
        return unpackedParams.get("callHash").asText();
    }

    public String getCallerPublicKey() {
        return unpackedParams.get("callerPublicKey").asText();
    }

    public String getCENIP() {
        return unpackedParams.get("cenIP").asText();
    }

    // sig verify 
    public boolean isSignatureValid() {
        byte[] hashBytes = hex.hexToBytes(getCallHash());
        try {
            return TransactionVerifier.verifySHA256Transaction(
                getCallerPublicKey(),
                hashBytes,
                getCallSignature()
            );
        } catch (Exception e) {
            return false;
        }
    }

    //custom param helpers 

    public boolean hasParam(String key) {
        return unpackedParams.has(key);
    }

    public JsonNode getParam(String key) {
        return unpackedParams.get(key);
    }

    // funded CENCALLs 

    public String getEmbeddedTXJson() {
        return unpackedParams.has("originalStakeTx") ? unpackedParams.get("tx").toString() : null; //updated for node correction 
    }

    public String getFundingHash() throws Exception{
        String txRaw = getEmbeddedTXJson();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tx = mapper.readTree(txRaw);
        return tx.get("txHash").asText();
    }

    public TX getEmbeddedTX() throws Exception {
        String txJson = getEmbeddedTXJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(txJson, TX.class); 
    }


}
