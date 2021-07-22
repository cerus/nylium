package dev.cerus.nylium.io.session.encryption;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Default encryption container
 * Handles everything related to encrypting / decrypting during and after the key exchange
 */
public class DefaultEncryptionContainer implements EncryptionContainer {

    private static KeyPair keyPair;

    static {
        // Generate random key on server startup
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private final byte[] verifyToken;
    private SecretKeySpec sharedSecret;
    private Cipher aesEncryptionCipher;
    private Cipher aesDecryptionCipher;

    public DefaultEncryptionContainer() {
        this.verifyToken = new byte[] {
                (byte) ThreadLocalRandom.current().nextInt(128),
                (byte) ThreadLocalRandom.current().nextInt(128),
                (byte) ThreadLocalRandom.current().nextInt(128),
                (byte) ThreadLocalRandom.current().nextInt(128)
        };
    }

    @Override
    public void initCommon(final byte[] key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        this.sharedSecret = secretKeySpec;

        this.aesEncryptionCipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.aesEncryptionCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(secretKeySpec.getEncoded()));

        this.aesDecryptionCipher = Cipher.getInstance("AES/CFB8/NoPadding");
        this.aesDecryptionCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(secretKeySpec.getEncoded()));
    }

    @Override
    public int encryptCommon(final byte[] input, final int inputOffset, final int inputLength, final byte[] output, final int outputOffset) throws Exception {
        return this.aesEncryptionCipher.update(input, inputOffset, inputLength, output, outputOffset);
    }

    @Override
    public int decryptCommon(final byte[] input, final int inputOffset, final int inputLength, final byte[] output, final int outputOffset) throws Exception {
        return this.aesDecryptionCipher.update(input, inputOffset, inputLength, output, outputOffset);
    }

    @Override
    public byte[] encryptExchange(final byte[] input) throws ShortBufferException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        return cipher.doFinal(input);
    }

    @Override
    public byte[] decryptExchange(final byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(input);
    }

    @Override
    public byte[] getPublicKey() {
        return keyPair.getPublic().getEncoded();
    }

    @Override
    public int getPublicKeyLength() {
        return keyPair.getPublic().getEncoded().length;
    }

    @Override
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public int getCommonEncryptOutputSize(final int length) {
        return this.aesEncryptionCipher.getOutputSize(length);
    }

    @Override
    public int getCommonDecryptOutputSize(final int length) {
        return this.aesDecryptionCipher.getOutputSize(length);
    }

    @Override
    public String getHash() throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(this.sharedSecret.getEncoded());
        md.update(keyPair.getPublic().getEncoded());
        final byte[] digest = md.digest();
        return new BigInteger(digest).toString(16);
    }

}
