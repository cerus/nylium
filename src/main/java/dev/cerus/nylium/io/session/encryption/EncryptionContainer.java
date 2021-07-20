package dev.cerus.nylium.io.session.encryption;

import java.security.NoSuchAlgorithmException;

public interface EncryptionContainer {

    void initCommon(byte[] key) throws Exception;

    int encryptCommon(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception;

    int decryptCommon(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws Exception;

    byte[] encryptExchange(byte[] input) throws Exception;

    byte[] decryptExchange(byte[] input) throws Exception;

    int getCommonEncryptOutputSize(int length);

    int getCommonDecryptOutputSize(int length);

    int getPublicKeyLength();

    byte[] getPublicKey();

    byte[] getVerifyToken();

    String getHash() throws NoSuchAlgorithmException;

}
