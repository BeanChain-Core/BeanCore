package com.beanpack.TXs;

import org.junit.jupiter.api.Test;

import com.beanpack.TXs.TX;
import com.beanpack.crypto.WalletGenerator;

import java.security.PrivateKey;

import static org.junit.jupiter.api.Assertions.*;

public class TXTest {

    @Test
    public void testHashIsDeterministic() {
        TX tx1 = new TX("bean1", "pubkey123", "bean2", 10.5, 1, 1000);
        TX tx2 = new TX("bean1", "pubkey123", "bean2", 10.5, 1, 1000);

        tx1.setTimeStamp(tx2.getTimeStamp());  // align timestamps to ensure same hash
        tx1.setTxHash(tx1.generateHash());
        tx2.setTxHash(tx2.generateHash());

        assertEquals(tx1.getTxHash(), tx2.getTxHash(), "Hashes should match for identical input");
    }

    @Test
    public void testToJsonAndFromJson() {
        TX original = new TX("bean1", "pubkey123", "bean2", 5.6789, 2, 200);
        original.setMeta("test-meta");
        original.setStatus("pending");
        original.setType("transfer");

        String json = original.createJSON();
        assertNotNull(json);

        TX parsed = TX.fromJSON(json);
        assertNotNull(parsed);
        assertEquals("bean1", parsed.getFrom());
        assertEquals("bean2", parsed.getTo());
        assertEquals(2, parsed.getNonce());
        assertEquals("test-meta", parsed.getMeta());
        assertEquals("transfer", parsed.getType());
        assertEquals(200, parsed.getGasFee());
    }

    @Test
    public void testAmountIsRoundedTo8Decimals() {
        TX tx = TX.fromJSON("""
        {
            "from": "bean1",
            "to": "bean2",
            "amount": 0.123456789123,
            "nonce": 0,
            "timeStamp": 123456789,
            "publicKeyHex": "abc",
            "txHash": "xyz",
            "signature": "sig",
            "gasFee": 1000
        }
        """);

        assertNotNull(tx);
        assertEquals(0.12345679, tx.getAmount(), 1e-9);
    }

    @Test
    public void testSignatureIsCreated() throws Exception {
        String privateKey = WalletGenerator.generatePrivateKey();
        PrivateKey privateKeyRaw = WalletGenerator.restorePrivateKey(privateKey);
        String publicKey = WalletGenerator.generatePublicKey(privateKeyRaw);
        TX tx = new TX("bean1", publicKey, "bean2", 100, 0, 500);
        tx.sign(privateKeyRaw);

        assertNotNull(tx.getSignature());
        assertEquals(publicKey, tx.getPublicKeyHex());
    }

    
}

