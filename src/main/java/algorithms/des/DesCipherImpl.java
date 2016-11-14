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

    private byte[] mainMethod(byte[] text, CipherMode mode) throws Exception {
        text = selectBitsByTable(text, DES_TABLES.getInitialPermutationTable());
        int blockSize = DES_TABLES.getInitialPermutationTableLength();
        byte[] leftPart = selectBits(text, 0, blockSize / 2);
        byte[] rightPart = selectBits(text, blockSize / 2, blockSize / 2);
        int numberOfAdditionalKeys = additionalKeys.length;
        for (int i = 0; i < numberOfAdditionalKeys; i++) {
            // tmp storage for right part
            byte[] tmpStorage = rightPart;
            rightPart = selectBitsByTable(rightPart, DES_TABLES.getExpansionPermutationTable());
            if (mode.equals(CipherMode.ENCRYPT)) {
                // xor operation between right part and keys from the beginning
                rightPart = XOR(rightPart, additionalKeys[i]);
            } else {
                // xor operation between right part and keys from the end
                rightPart = XOR(rightPart, additionalKeys[numberOfAdditionalKeys - i - 1]);
            }
            rightPart = substitution6to4(rightPart);
            rightPart = selectBitsByTable(rightPart, DES_TABLES.getDirectPermutationTable());
            rightPart = XOR(leftPart, rightPart);
            leftPart = tmpStorage;
        }
        byte[] blockResult = concatenateBits(rightPart, blockSize / 2, leftPart, blockSize / 2);
        blockResult = selectBitsByTable(blockResult, DES_TABLES.getFinalPermutationTable());
        return blockResult;
    }

    // substitution six to four bits
    private byte[] substitution6to4(byte[] input) throws Exception {
        input = expandBits(input, 6);
        byte[] output = new byte[input.length / 2];
        int leftHalfByte = 0;
        for (int i = 0; i < input.length; i++) {
            byte byteValue = input[i];
            int right = 2 * (byteValue >> 7 & 0x0001) + (byteValue >> 2 & 0x0001);
            int center = byteValue >> 3 & 0x000F;
            int halfByte = DES_TABLES.getSubstitutionTable(i + 1)[right << 4 | center];
            if (i % 2 == 0) {
                leftHalfByte = halfByte;
            } else {
                output[i / 2] = (byte) (leftHalfByte << 4 | halfByte);
            }
        }
        return output;
    }

    private byte[] expandBits(byte[] source, int length) {
        int numberOfBytes = (8 * source.length - 1) / length + 1;
        byte[] output = new byte[numberOfBytes];
        for (int i = 0; i < numberOfBytes; i++) {
            for (int j = 0; j < length; j++) {
                int value = getBit(source, length * i + j);
                setBit(output, 8 * i + j, value);
            }
        }
        return output;
    }


    // ok here some bad code, for cases where length of arrays not the same it will not work!
    private byte[] XOR(byte[] firstPart, byte[] secondPart) {
        byte[] output = new byte[firstPart.length];
        for (int i = 0; i < firstPart.length; i++) {
            output[i] = (byte) (firstPart[i] ^ secondPart[i]);
        }
        return output;
    }

    private String cutOff(String s) {
        StringBuilder returnSource = new StringBuilder();
        for (char element : s.toCharArray()) {
            if (element != (char) 0x0f)
                returnSource.append(element);
        }
        return returnSource.toString();
    }

    public String decrypt(String textToDecrypt) {
        byte[][] splittedMessage = splitMessage(textToDecrypt.getBytes());
        byte[][] resultStorage = new byte[splittedMessage.length][];
        for (int i = 0; i < splittedMessage.length; i++) {
            try {
                resultStorage[i] = mainMethod(splittedMessage[i], CipherMode.ENCRYPT);
            } catch (Exception e) {
                System.out.println("something went wrong" + e);
            }
        }
        return cutOff(new String(gatherMessage(resultStorage)));
    }

    public String encrypt(String textToEncrypt) {
        byte[][] splittedMessage = splitMessage(normalizeMessage(textToEncrypt).getBytes());
        byte[][] resultStorage = new byte[splittedMessage.length][];
        for (int i = 0; i < splittedMessage.length; i++) {
            try {
                resultStorage[i] = mainMethod(splittedMessage[i], CipherMode.ENCRYPT);
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e);
            }
        }
        return new String(gatherMessage(resultStorage));
    }

    private byte[] gatherMessage(byte[][] input) {
        ByteBuffer buffer = ByteBuffer.allocate(input.length * 8);
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                buffer.put(input[i][j]);
            }
        }
        return buffer.array();
    }
}