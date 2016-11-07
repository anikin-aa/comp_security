package algorithms.des;

import algorithms.CipherInterface;
import algorithms.CipherMode;

import java.nio.ByteBuffer;


public class DesCipherImpl implements CipherInterface {
    private static final DesTables DES_TABLES = DesTables.INSTANCE;

    private String key;
    private byte[] keyBytes;
    private byte[][] additionalKeys;


    public DesCipherImpl(String key) {
        this.key = key;
        this.getKeyBytes();
        this.getAdditionalKeys();
    }

    private void getKeyBytes() {
        this.keyBytes = long2Byte((long) this.key.hashCode());
    }

    private byte[] long2Byte(long input) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(input);
        return buffer.array();
    }

    private void getAdditionalKeys() {
        int actualKeySize = DES_TABLES.getKeyPermutationTableLength();
        int numberOfAdditionalKeys = DES_TABLES.getKeyShiftsLength();
        byte[] actualKey = selectBitsByTable(this.keyBytes, DES_TABLES.getKeyPermutationTable());
        int halfKeySize = actualKeySize / 2;

        // selecting key parts (left and right)
        byte[] firstKeyPart = selectBits(actualKey, 0, halfKeySize);
        byte[] secondKeyPart = selectBits(actualKey, halfKeySize, halfKeySize);

        // init array of additional keys
        this.additionalKeys = new byte[numberOfAdditionalKeys][];
        int[] keyShifts = DES_TABLES.getKeyShifts();

        for (int i = 0; i < numberOfAdditionalKeys; i++) {
            firstKeyPart = rotateLeftBits(firstKeyPart, halfKeySize, keyShifts[i]);
            secondKeyPart = rotateLeftBits(secondKeyPart, halfKeySize, keyShifts[i]);
            byte[] concatenation = concatenateBits(
                    firstKeyPart,
                    halfKeySize,
                    secondKeyPart,
                    halfKeySize
            );
            this.additionalKeys[i] = selectBitsByTable(concatenation, DES_TABLES.getCompressionPermutationTable());
        }
    }

    private byte[] concatenateBits(byte firstPart[], int lengthFirstPart, byte[] secondPart, int lengthSecondPart) {
        int resSize = (lengthFirstPart + lengthSecondPart - 1) / 8 + 1;
        byte[] output = new byte[resSize];
        int k = 0;
        for (int i = 0; i < lengthFirstPart; i++) {
            int value = getBit(firstPart, i);
            setBit(output, k, value);
            k++;
        }
        for (int i = 0; i < lengthSecondPart; i++) {
            int value = getBit(secondPart, i);
            setBit(output, k, value);
            k++;
        }
        return output;
    }

    // rotate bytes to left with step
    private byte[] rotateLeftBits(byte[] source, int length, int step) {
        int numberOfBytes = (length - 1) / 8 + 1;
        byte[] output = new byte[numberOfBytes];
        for (int i = 0; i < length; i++) {
            int value = getBit(source, (i + step) % length);
            setBit(output, i, value);
        }
        return output;
    }

    private byte[] selectBitsByTable(byte[] inputBytes, int[] table) {
        int numberOfBytes = (table.length - 1) / 8 + 1;
        byte[] output = new byte[numberOfBytes];
        for (int i = 0; i < inputBytes.length; i++) {
            int value = getBit(inputBytes, table[i] - 1);
            setBit(output, i, value);
        }
        return output;
    }

    private byte[] selectBits(byte[] inputBytes, int position, int length) {
        int numberOfBytes = (length - 1) / 8 + 1;
        byte[] output = new byte[numberOfBytes];
        for (int i = 0; i < length; i++) {
            int value = getBit(inputBytes, position + i);
            setBit(output, i, value);
        }
        return output;
    }

    private int getBit(byte[] inputBytes, int position) {
        int bytePosition = position / 8;
        int bitPosition = position % 8;
        byte byteValue = inputBytes[bytePosition];
        return (byteValue >> (8 - (bitPosition + 1)) & 0x0001);
    }

    private void setBit(byte[] destination, int position, int value) {
        int bytePosition = position / 8;
        int bitPosition = position % 8;
        byte oldByte = destination[bytePosition];
        oldByte = (byte) (((0xFF7F >> bitPosition) & oldByte) & 0x00FF);
        byte newByte = (byte) ((value << (8 - (bitPosition + 1))) | oldByte);
        destination[bytePosition] = newByte;
    }

    private byte[][] splitMessage(byte[] bytes2split) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        int commonCapacity = bytes2split.length / 8;
        byte[][] split = new byte[commonCapacity][];
        int j = 0;
        for (byte bytte : bytes2split) {
            buffer.put(bytte);
            if (!buffer.hasRemaining()) {
                split[j] = buffer.array();
                j++;
                buffer = ByteBuffer.allocate(8);
            }
        }
        return split;
    }

    private String normalizeMessage(String text) {
        StringBuilder copy = new StringBuilder();
        copy.append(text);
        while (copy.length() % 8 != 0) {
            copy.append((char) 0x0f);
        }
        return copy.toString();
    }

    private byte[] mainMethod(byte[] text, CipherMode mode) {
        byte[] result = new byte[5];
        if (mode.equals(CipherMode.DECRYPT)) {

        } else {

        }
        return result;
    }

    public String decrypt(String textToDecrypt) {
        byte[][] splittedMessage = splitMessage(textToDecrypt.getBytes());
        byte[][] resultStorage = new byte[splittedMessage.length][];
        for (int i = 0; i < splittedMessage.length; i++) {
            resultStorage[i] = mainMethod(splittedMessage[i], CipherMode.ENCRYPT);
        }
        return "";
    }

    public String encrypt(String textToEncrypt) {
        byte[][] splittedMessage = splitMessage(normalizeMessage(textToEncrypt).getBytes());
        byte[][] resultStorage = new byte[splittedMessage.length][];
        for (int i = 0; i < splittedMessage.length; i++) {
            resultStorage[i] = mainMethod(splittedMessage[i], CipherMode.ENCRYPT);
        }
        // TODO add methods for getting string from bytes
        return "";
    }
}