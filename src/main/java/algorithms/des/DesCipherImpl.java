package algorithms.des;

import algorithms.CipherInterface;

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

    public String decrypt(String textToDecrypt) {
        return "";
    }

    public String encrypt(String textToEncrypt) {
        return "";
    }
}