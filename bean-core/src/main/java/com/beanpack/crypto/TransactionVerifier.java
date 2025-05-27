package com.beanpack.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.*;

public class TransactionVerifier {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean walletMatch(String publicHex, String addy) throws Exception {
        String addyTest = WalletGenerator.generateAddress(publicHex);
        if(addyTest.equals(addy)){
            return true;
        } else {
            return false;
        }
    }

    public static boolean verifySHA256Transaction(String publicHex, byte[] transactionHash, String signatureHex) throws Exception {
        if (transactionHash.length != 32) {
            throw new IllegalArgumentException("Transaction hash must be 32 bytes (SHA-256).");
        }
    
        //System.out.println("Transaction Hash at Verification: " + bytesToHex(transactionHash));
    
        BigInteger r = new BigInteger(signatureHex.substring(0, 64), 16);
        BigInteger s = new BigInteger(signatureHex.substring(64, 128), 16);
        int v = Integer.parseInt(signatureHex.substring(128, 130), 16);
    
        if (v >= 27) {
            v -= 27;
        }
    
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECDomainParameters domain = new ECDomainParameters(ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN(), ecSpec.getH());
    
        BigInteger n = domain.getN();
        s = s.mod(n);  // Ensure s is always within curve order
        if (s.compareTo(n.divide(BigInteger.TWO)) > 0) {
            s = n.subtract(s);  // Enforce low-s values
        }

        String publicKeyHex = publicHex;
        if (publicKeyHex == null) {
            throw new IllegalArgumentException("Public key not found for the given address.");
        }
        
        //PublicKey publicKey = WalletGenerator.restorePublicKey(publicKeyHex);
    
        ECPoint recoveredPublicPoint = recoverPublicKey(v, r, s, transactionHash, domain);
        if (recoveredPublicPoint == null) return false;
    
        // Convert recovered public key to uncompressed format
        String recoveredPublicKeyHex = "04" + String.format("%064x", recoveredPublicPoint.getAffineXCoord().toBigInteger()) +
                                            String.format("%064x", recoveredPublicPoint.getAffineYCoord().toBigInteger());
    
        // Debug Output
        //System.out.println("Expected Public Key:  " + publicKeyHex);
        //System.out.println("Recovered Public Key: " + recoveredPublicKeyHex);
        //System.out.println("Do they match? " + publicKeyHex.equalsIgnoreCase(recoveredPublicKeyHex));
    
        return publicKeyHex.equalsIgnoreCase(recoveredPublicKeyHex);
    }

    private static ECPoint recoverPublicKey(int v, BigInteger r, BigInteger s, byte[] messageHash, ECDomainParameters domain) {
        BigInteger n = domain.getN();
        BigInteger e = new BigInteger(1, messageHash);
        ECPoint G = domain.getG();
    
        // Recover the R point
        ECPoint R = recoverRPoint(r, v, domain);
        if (R == null) return null;
    
        BigInteger rInv = r.modInverse(n);
        
        // Correct calculation: Q = (r⁻¹ * (s * R - e * G))
        ECPoint Q = G.multiply(e).negate().add(R.multiply(s)).multiply(rInv).normalize();

    
        // Ensure Q is not infinity before returning
        if (Q.isInfinity()) {
            return null;
        }
    
        return Q;
    }

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






