package com.great.project.db.credentialstoreserviceapi.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import com.great.project.db.credentialstoreserviceapi.dto.CryptoKeysDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import org.apache.commons.codec.binary.Base64;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

    private static final AlgorithmConstraints CONTENT_ENCRYPTION_ALGORITHM_CONSTRAINTS = new AlgorithmConstraints(
            ConstraintType.WHITELIST, ContentEncryptionAlgorithmIdentifiers.AES_256_GCM);
    private static final AlgorithmConstraints KEY_ENCRYPTION_ALGORITHM_CONSTRAINTS = new AlgorithmConstraints(
            ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);

    private final CryptoKeysDto cryptoKeys;

    /**
     * Encription utility for the payload requests to Credential Store
     * 
     * @param cryptoKeys
     */
    public CryptoUtil(CryptoKeysDto cryptoKeys) {
        this.cryptoKeys = cryptoKeys;
    }

    /**
     * Decripts the encripted payload
     * 
     * @param encryptedValue
     * @return decripted json
     * @throws CredentialStoreException
     */
    public String decrypt(String encryptedValue) throws CredentialStoreException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] binaryKey = Base64.decodeBase64(cryptoKeys.getClienPrivateKey());
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(binaryKey));
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setAlgorithmConstraints(KEY_ENCRYPTION_ALGORITHM_CONSTRAINTS);
            jwe.setContentEncryptionAlgorithmConstraints(CONTENT_ENCRYPTION_ALGORITHM_CONSTRAINTS);
            jwe.setKey(privateKey);
            jwe.setCompactSerialization(encryptedValue);
            return jwe.getPayload();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JoseException e) {
            throw new CredentialStoreException(e.getMessage(), e);
        }
    }

    /**
     * Encripts the given payload
     * 
     * @param value
     * @return
     * @throws CredentialStoreException
     */
    public String encrypt(String value) throws CredentialStoreException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] binaryKey = Base64.decodeBase64(cryptoKeys.getServerPublicKey());
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(binaryKey));
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(value);
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
            jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_GCM);
            jwe.setKey(publicKey);
            long iat = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            jwe.getHeaders().setObjectHeaderValue("iat", iat);
            return jwe.getCompactSerialization();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JoseException e) {
            throw new CredentialStoreException(e.getMessage(), e);
        }
    }
}
