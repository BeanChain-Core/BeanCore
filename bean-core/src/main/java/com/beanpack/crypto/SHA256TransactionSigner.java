package com.beanpack.crypto;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;

import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import com.beanpack.Security.*;

public class SHA256TransactionSigner {

    static {
        SecuritySetup.run();
    }

    /**
     * Signs an already hashed SHA-256 transaction.
     * @param privateKey The EC private key
     * @param transactionHash The transaction hash (SHA-256, 32 bytes)
     * @return The signature in hex format (r, s, v)
     * @throws Exception if signing fails
     */
    public static String signSHA256Transaction(PrivateKey privateKey, byte[] transactionHash) throws Exception {
        if (transactionHash.length != 32) {
            throw new IllegalArgumentException("Transaction hash must be 32 bytes (SHA-256).");
        }

        // Convert private key to ECPrivateKeyParameters
        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;
        BigInteger privateKeyValue = ecPrivateKey.getS();

        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECDomainParameters domain = new ECDomainParameters(ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN(), ecSpec.getH());

        ECPrivateKeyParameters privateKeyParams = new ECPrivateKeyParameters(privateKeyValue, domain);

        // Sign the SHA-256 hash
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, privateKeyParams);
        BigInteger[] signature = signer.generateSignature(transactionHash);

        // Extract r and s
        BigInteger r = signature[0];
        BigInteger s = signature[1];

        // Normalize s for security (Bitcoin/Ethereum standard)
        if (s.compareTo(domain.getN().divide(BigInteger.TWO)) > 0) {
            s = domain.getN().subtract(s);
        }

        // Calculate v (recovery id)
        int v = calculateRecoveryId(privateKeyValue, transactionHash, r, s, domain);

        // Return the signature as a hex string
        return String.format("%064x%064x%02x", r, s, v);
    }

    /**
     * Computes the recovery ID (v) needed for signature verification.
     */
    private static int calculateRecoveryId(BigInteger privateKey, byte[] transactionHash, BigInteger r, BigInteger s, ECDomainParameters domain) {
        ECPoint G = domain.getG();
        ECPoint publicPoint = G.multiply(privateKey).normalize();
        BigInteger expectedX = publicPoint.getXCoord().toBigInteger();

        for (int i = 0; i < 2; i++) {
            ECPoint recovered = recoverPublicKey(i, r, s, transactionHash, domain);
            if (recovered != null && recovered.getXCoord().toBigInteger().equals(expectedX)) {
                return 27 + i; // Return 27 or 28
            }
        }
        throw new IllegalArgumentException("Could not determine valid recovery ID.");
    }

    /**
     * Recovers the public key from the signature.
     */
    private static ECPoint recoverPublicKey(int v, BigInteger r, BigInteger s, byte[] transactionHash, ECDomainParameters domain) {
        BigInteger n = domain.getN();
        BigInteger e = new BigInteger(1, transactionHash);
        ECPoint G = domain.getG();

        // Recover the R point
        ECPoint R = recoverRPoint(r, v, domain);
        if (R == null) return null;

        BigInteger rInv = r.modInverse(n);
        ECPoint Q = R.multiply(s).subtract(G.multiply(e)).multiply(rInv).normalize();

        return Q;
    }

    /**
     * Recovers the R point (x, y) from r.
     */
    private static ECPoint recoverRPoint(BigInteger r, int v, ECDomainParameters domain) {
        BigInteger x = r;
        BigInteger p = domain.getCurve().getField().getCharacteristic();
        BigInteger ySquared = x.modPow(BigInteger.valueOf(3), p).add(BigInteger.valueOf(7)).mod(p);
        BigInteger y = ySquared.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);

        boolean isYOdd = y.testBit(0);
        boolean shouldBeOdd = (v == 1);

        if (isYOdd != shouldBeOdd) {
            y = p.subtract(y);
        }

        return domain.getCurve().createPoint(x, y);
    }


}



