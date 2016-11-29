
package algorithms.knapsackproblem;

import algorithms.CipherInterface;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnapsackProblemImpl implements CipherInterface {
    Logger log = Logger.getLogger("knapsackLog");

    private BigInteger N = BigInteger.valueOf(31L);
    private BigInteger M;

    public List<BigInteger> publicKey = new ArrayList<>();
    private List<BigInteger> privateKey = new ArrayList<>();
    private List<BigInteger> decryptedSequence = new ArrayList<>();

    public KnapsackProblemImpl(String inputSequence) {
        this.publicKey = parseInputSequence(inputSequence);
        this.privateKey = generatePrivateKeyByPublic();
    }

    public List<BigInteger> getPublicKey() {
        return publicKey;
    }

    private List<BigInteger> generatePrivateKeyByPublic() {
        List<BigInteger> res = new ArrayList<>();
        M = publicKey.stream().reduce(BigInteger.ZERO, BigInteger::add).add(BigInteger.ONE);
//        N = 588;
//        BigInteger m = 881;
        publicKey.forEach(val -> res.add((val.multiply(N)).mod(M)));
        return res;
    }

    private List<BigInteger> parseInputSequence(String inputSequence) {
        List<BigInteger> res = new ArrayList<>();
        String chunks[] = inputSequence.trim().split(",");
        Arrays.stream(chunks).forEach(val -> res.add(new BigInteger(val)));
        return res;
    }


    public String decrypt(String textToDecrypt) throws Exception {
        List<BigInteger> listToDecrypt = parseInputSequence(textToDecrypt);
        if (privateKey != null) {
            StringBuilder res = new StringBuilder();
            for (BigInteger partOfDecryptedMessage : listToDecrypt) {
                // first of all lets find our Inversed value of N by modulus M
                BigInteger inversedByModulus = N.modInverse(M);
                // next step multiply decryptedMessage
                BigInteger multiplied = partOfDecryptedMessage.multiply(inversedByModulus).mod(M);
                // find max element of sequence not more than 'multiplied'
                BigInteger firstElement = getMaxElementNotMoreThan(multiplied);
                decryptedSequence.add(firstElement);
                // finding elements by difference of first element and multiplied one until '0'
                while (true) {
                    multiplied = multiplied.subtract(firstElement);
                    if (multiplied.equals(BigInteger.ZERO)) break;
                    firstElement = getMaxElementNotMoreThan(multiplied);
                    decryptedSequence.add(firstElement);
                }
                res.append(generateDecryptedBySequence(decryptedSequence));
            }
            return res.toString();
        } else {
            throw new Exception("encrypt first!");
        }
    }

    // by collected elements create decrypted message
    private String generateDecryptedBySequence(List<BigInteger> decryptedSequence) {
        char[] res = new char[publicKey.size()];
        Arrays.fill(res, '0'); // init array with zeros
        for (BigInteger element : decryptedSequence) {
            res[publicKey.indexOf(element)] = '1';
        }
        return Arrays.toString(res);
    }

    private BigInteger getMaxElementNotMoreThan(BigInteger element) {
        List<BigInteger> listNotMoreThan = new ArrayList<>();
        for (BigInteger integer : publicKey) {
            if (integer.compareTo(element) < 0) {
                listNotMoreThan.add(integer);
            }
        }
        return Collections.max(listNotMoreThan);
    }

    public String encrypt(String textToEncrypt) throws Exception {
        String stringInBits = getBitsArrayFromString(textToEncrypt);
        log.log(Level.INFO, "stringInBits: " + stringInBits);
        List<BigInteger> encryptedList = new ArrayList<>();
        if (stringInBits.length() % privateKey.size() == 0) {
            List<char[]> listOfCharsArrays = splitBitsToArraysList(stringInBits);
            for (char[] splittedMessage : listOfCharsArrays) {
                BigInteger encryptedMessage = BigInteger.ZERO;
                for (int i = 0; i < splittedMessage.length; i++) {
                    if ('1' == splittedMessage[i]) {
                        encryptedMessage = encryptedMessage.add(privateKey.get(i));
                    }
                }
                encryptedList.add(encryptedMessage);
            }
        } else {
            throw new Exception("can't mod length of input String by size of key!" +
                    " length of inputString: " + stringInBits.length());
        }
        return encryptedList.toString();
    }

    private List<char[]> splitBitsToArraysList(String stringInBits) {
        List<char[]> res = new ArrayList<>();
        char[] allBits = stringInBits.toCharArray();
        Integer sizeOfChunk = publicKey.size();
        for (int i = 0; i < allBits.length - sizeOfChunk + 1; i += sizeOfChunk)
            res.add(Arrays.copyOfRange(allBits, i, i + sizeOfChunk));
        return res;
    }

    private String getBitsArrayFromString(String inputString) {
        return new BigInteger(inputString.getBytes()).toString(2);
    }
}