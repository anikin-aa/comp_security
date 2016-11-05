package algorithms;

// common interface for ciphers
public interface CipherInterface {
    String decrypt(String textToDecrypt);

    String encrypt(String textToEncrypt);
}
