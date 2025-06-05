package com.beanpack.CENdev;

import com.beanpack.CENdev.CallManager;
import com.beanpack.TXs.TX;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CallManagerTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String buildBasicCencallJson() {
        ObjectNode params = mapper.createObjectNode();
        params.put("contractHash", "abc123");
        params.put("caller", "bean1xyz");
        params.put("callSignature", "fakesig==");
        params.put("callHash", "aabbccddeeff0011");
        params.put("callerPublicKey", "fakePubKey==");
        params.put("cenIP", "127.0.0.1");

        ObjectNode cencall = mapper.createObjectNode();
        cencall.put("contract", "TestContract");
        cencall.put("method", "mintToken");
        cencall.put("params", params.toString());

        return cencall.toString();
    }

    @Test
    public void testParsesCencallJsonCorrectly() throws Exception {
        CallManager cm = new CallManager(buildBasicCencallJson());

        assertEquals("TestContract", cm.getContractName());
        assertEquals("mintToken", cm.getMethod());
        assertEquals("abc123", cm.getContractHash());
        assertEquals("bean1xyz", cm.getCaller());
        assertEquals("fakesig==", cm.getCallSignature());
        assertEquals("aabbccddeeff0011", cm.getCallHash());
        assertEquals("fakePubKey==", cm.getCallerPublicKey());
        assertEquals("127.0.0.1", cm.getCENIP());
    }

    @Test
    public void testHasParamReturnsTrue() throws Exception {
        CallManager cm = new CallManager(buildBasicCencallJson());
        assertTrue(cm.hasParam("caller"));
    }

    @Test
    public void testIsSignatureValidFailsForFakeData() throws Exception {
        CallManager cm = new CallManager(buildBasicCencallJson());
        assertFalse(cm.isSignatureValid(), "Fake signature should not validate");
    }

    @Test
    public void testThrowsOnMalformedJson() {
        String invalidJson = "{ not valid }";
        assertThrows(Exception.class, () -> new CallManager(invalidJson));
    }

    @Test
    public void testEmbeddedTXReturnsNullWhenMissing() throws Exception {
        CallManager cm = new CallManager(buildBasicCencallJson());
        assertNull(cm.getEmbeddedTXJson());
    }

    @Test
    public void testEmbeddedTXReturnsValueWhenPresent() throws Exception {
        ObjectNode tx = mapper.createObjectNode();
        tx.put("from", "bean1xyz");
        tx.put("to", "contractABC");
        tx.put("amount", 1000);
        tx.put("nonce", 1);
        tx.put("txHash", "embeddedhash001");
        tx.put("signature", "sig==");
        tx.put("publicKeyHex", "pub==");
        tx.put("timeStamp", 123456789);

        ObjectNode params = mapper.createObjectNode();
        params.put("contractHash", "abc123");
        params.put("caller", "bean1xyz");
        params.put("callSignature", "fakesig==");
        params.put("callHash", "aabbccdd");
        params.put("callerPublicKey", "fakepub==");
        params.put("cenIP", "127.0.0.1");
        params.put("originalStakeTx", true);
        params.set("tx", tx);

        ObjectNode cencall = mapper.createObjectNode();
        cencall.put("contract", "StakingContract");
        cencall.put("method", "stake");
        cencall.put("params", params.toString());

        CallManager cm = new CallManager(cencall.toString());

        assertNotNull(cm.getEmbeddedTXJson());
        TX parsed = cm.getEmbeddedTX();
        assertEquals("embeddedhash001", parsed.getTxHash());
    }

    @Test
    public void testMalformedEmbeddedTXJsonThrows() {
        ObjectNode params = mapper.createObjectNode();
        params.put("contractHash", "abc123");
        params.put("caller", "bean1xyz");
        params.put("callSignature", "fakesig==");
        params.put("callHash", "deadbeef");
        params.put("callerPublicKey", "pub==");
        params.put("cenIP", "127.0.0.1");
        params.put("originalStakeTx", true);
        params.put("tx", "{ bad json here }");  // Not valid JSON structure

        ObjectNode cencall = mapper.createObjectNode();
        cencall.put("contract", "BadContract");
        cencall.put("method", "stake");
        cencall.put("params", params.toString());

        assertThrows(Exception.class, () -> {
            CallManager cm = new CallManager(cencall.toString());
            cm.getEmbeddedTX();
        });
    }

    @Test
    public void testMissingTxHashInEmbeddedTXThrows() {
        ObjectNode tx = mapper.createObjectNode();
        tx.put("from", "bean1xyz");
        tx.put("to", "contractABC");
        tx.put("amount", 1000);
        tx.put("nonce", 1);
        tx.put("signature", "sig==");
        tx.put("publicKeyHex", "pub==");
        tx.put("timeStamp", 123456789);

        ObjectNode params = mapper.createObjectNode();
        params.put("contractHash", "abc123");
        params.put("caller", "bean1xyz");
        params.put("callSignature", "fakesig==");
        params.put("callHash", "deadbeef");
        params.put("callerPublicKey", "pub==");
        params.put("cenIP", "127.0.0.1");
        params.put("originalStakeTx", true);
        params.set("tx", tx);

        ObjectNode cencall = mapper.createObjectNode();
        cencall.put("contract", "MissingHashContract");
        cencall.put("method", "stake");
        cencall.put("params", params.toString());

        assertThrows(Exception.class, () -> {
            CallManager cm = new CallManager(cencall.toString());
            cm.getFundingHash(); // should fail due to missing txHash
        });
    }

    @Test
    public void testParamsIsNotJsonStringThrows() {
        String badParamsFormat = """
        {
            "contract": "BadParamsContract",
            "method": "doSomething",
            "params": 12345
        }
        """;

        assertThrows(Exception.class, () -> new CallManager(badParamsFormat));
    }
}


