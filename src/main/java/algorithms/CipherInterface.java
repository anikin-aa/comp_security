package algorithms;

// common interface for ciphers
public interface CipherInterface {
    String decrypt(String textToDecrypt) throws Exception;

    String encrypt(String textToEncrypt) throws Exception;
}
