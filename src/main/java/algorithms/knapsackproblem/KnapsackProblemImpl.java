
package algorithms.knapsackproblem;

import algorithms.CipherInterface;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KnapsackProblemImpl implements CipherInterface {
    Logger log = Logger.getLogger("knapsackLog");

    private Integer N = 31;

    public List<Integer> publicKey = new ArrayList<>();
    private List<Integer> privateKey = new ArrayList<>();

    public KnapsackProblemImpl(String inputSequence) {
        this.publicKey = parseInputSequence(inputSequence);
        this.privateKey = generatePrivateKeyByPublic();
    }

    public List<Integer> getPublicKey() {
        return publicKey;
    }

    private List<Integer> generatePrivateKeyByPublic() {
        List<Integer> res = new ArrayList<>();
        Integer m = publicKey.stream().reduce(0, (x, y) -> x + y) + 1;
//        N = 588;
//        Integer m = 881;
        publicKey.forEach(val -> res.add((val * N) % m));
        return res;
    }

    private List<Integer> parseInputSequence(String inputSequence) {
        List<Integer> res = new ArrayList<>();
        String chunks[] = inputSequence.trim().split(",");
        Arrays.stream(chunks).forEach(val -> res.add(Integer.valueOf(val)));
        return res;
    }


    public String decrypt(String textToDecrypt) throws Exception {
        List<Integer> listToDecrypt = parseInputSequence(textToDecrypt);
        if (privateKey != null) {
        } else {
            throw new Exception("encrypt first!");
        }
        return null;
    }

    public String encrypt(String textToEncrypt) throws Exception {
        String stringInBits = getBitsArrayFromString(textToEncrypt);
        log.log(Level.INFO, "stringInBits: " + stringInBits);
        List<Integer> encryptedList = new ArrayList<>();
        if (stringInBits.length() % privateKey.size() == 0) {
            List<char[]> listOfCharsArrays = splitBitsToArraysList(stringInBits);
            for (char[] splittedMessage : listOfCharsArrays) {
                Integer encryptedMessage = 0;
                for (int i = 0; i < splittedMessage.length; i++) {
                    if ('1' == splittedMessage[i]) {
                        encryptedMessage += privateKey.get(i);
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