package dev.cerus.nylium.io.session.encryption;

import java.security.NoSuchAlgorithmException;

/**
 *
 */
public interface EncryptionContainer {

    /**
     * Initialize the AES encryption used for every packet
     *
     * @param key The shared secret
     *
     * @throws Exception when the AES ciphers can't be initialized
     */
    void initCommon(byte[] key) throws Exception;

    /**
     * Encrypt something using AES
     *
     * @param input
     * @param inputOffset
     * @param inputLength
     * @param output
     * @param outputOffset
     *
     * @return
     *
     * @throws Exception
     */
    int encryptCommon(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception;

    /**
     * Decrypt something using AES
     *
     * @param input
     * @param inputOffset
     * @param inputLength
     * @param output
     * @param outputOffset
     *
     * @return
     *
     * @throws Exception
     */
    int decryptCommon(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception;

    /**
     * Encrypt something using RSA (only used when exchanging keys)
     *
     * @param input Bytes to encrypt
     *
     * @return Encrypted bytes
     *
     * @throws Exception
     */
    byte[] encryptExchange(byte[] input) throws Exception;

    /**
     * Decrypt something using RSA (only used when exchanging keys)
     *
     * @param input Bytes to decrypt
     *
     * @return Decrypted bytes
     *
     * @throws Exception
     */
    byte[] decryptExchange(byte[] input) throws Exception;

    /**
     * Get AES output length for the provided length
     *
     * @param length The length
     *
     * @return the output length
     */
    int getCommonEncryptOutputSize(int length);

    /**
     * Get AES output length for the provided length
     *
     * @param length The length
     *
     * @return the output length
     */
    int getCommonDecryptOutputSize(int length);

    /**
     * Get the length of the public key
     *
     * @return the length of the public key
     */
    int getPublicKeyLength();

    /**
     * Get the public key
     *
     * @return the length of the public key
     */
    byte[] getPublicKey();

    /**
     * Get the verification token
     *
     * @return the verification token
     */
    byte[] getVerifyToken();

    /**
     * Get the Sha1 hash used for Mojang auth
     *
     * @return The hash
     *
     * @throws NoSuchAlgorithmException
     */
    String getHash() throws NoSuchAlgorithmException;

}
