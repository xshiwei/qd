package com.qvd.smartswitch.utils;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public class CommandUtils {
    /**
     * 十六进制与一般数据类型之间的互相转换
     */
    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F',
    };

    /**
     * byte[] -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(byte[] input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        StringBuilder builder = new StringBuilder(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            builder.append(HEX_DIGITS[input[i] >>> 4 & 0xf]);
            builder.append(HEX_DIGITS[input[i] & 0xf]);
        }
        return builder.toString();
    }

    /**
     * hexString -> byte[]
     *
     * @param input
     * @return
     */
    public static byte[] decodeToByteArray(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() % 2 != 0)
            throw new IllegalArgumentException("input should be divisible by four");

        String alphabet = new String(HEX_DIGITS);
        String temp = input.toUpperCase();

        byte[] output = new byte[temp.length() / 2];
        for (int i = 0; i < output.length; i++) {

            int highByte = alphabet.indexOf(temp.charAt(i * 2));
            int lowByte = alphabet.indexOf(temp.charAt(i * 2 + 1));
            if (highByte == -1 || lowByte == -1)
                throw new IllegalArgumentException("input contiain illegal character");

            output[i] = (byte) (highByte << 4 | lowByte);
        }
        return output;
    }

    /**
     * short -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(byte input) {
        return encode(new byte[]{input});
    }

    /**
     * hexString -> short
     *
     * @param input
     * @return
     */
    public static byte decodeToByte(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() != 2)
            throw new IllegalArgumentException("input should be equal to two");

        byte[] temp = decodeToByteArray(input);
        return temp[0];
    }

    /**
     * short -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(short input) {
        return encode(new byte[]{
                (byte) (input >>> 8 & 0xff),
                (byte) (input & 0xff)});
    }

    /**
     * hexString -> short
     *
     * @param input
     * @return
     */
    public static int decodeToShort(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() != 4)
            throw new IllegalArgumentException("input should be equal to four");

        byte[] temp = decodeToByteArray(input);
        return
                (short) (temp[0] & 0xff) << 8 |
                        (short) (temp[1] & 0xff);
    }

    /**
     * integer -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(int input) {
        return encode(new byte[]{
                (byte) (input >>> 24 & 0xff),
                (byte) (input >>> 16 & 0xff),
                (byte) (input >>> 8 & 0xff),
                (byte) (input & 0xff)});
    }

    /**
     * hexString -> integer
     *
     * @param input
     * @return
     */
    public static int decodeToInt(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() != 8)
            throw new IllegalArgumentException("input should be equal to eight");

        byte[] temp = decodeToByteArray(input);
        return
                (temp[0] & 0xff) << 24 |
                        (temp[1] & 0xff) << 16 |
                        (temp[2] & 0xff) << 8 |
                        (temp[3] & 0xff);
    }

    /**
     * long -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(long input) {
        return encode(new byte[]{
                (byte) (input >>> 56 & 0xff),
                (byte) (input >>> 48 & 0xff),
                (byte) (input >>> 40 & 0xff),
                (byte) (input >>> 32 & 0xff),
                (byte) (input >>> 24 & 0xff),
                (byte) (input >>> 16 & 0xff),
                (byte) (input >>> 8 & 0xff),
                (byte) (input & 0xff)});
    }

    /**
     * hexString -> long
     *
     * @param input
     * @return
     */
    public static long decodeToLong(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() != 16)
            throw new IllegalArgumentException("input should be equal to sixteen");

        byte[] temp = decodeToByteArray(input);
        return
                (long) (temp[0] & 0xff) << 56 |
                        (long) (temp[1] & 0xff) << 48 |
                        (long) (temp[2] & 0xff) << 40 |
                        (long) (temp[3] & 0xff) << 32 |
                        (long) (temp[4] & 0xff) << 24 |
                        (long) (temp[5] & 0xff) << 16 |
                        (long) (temp[6] & 0xff) << 8 |
                        (long) (temp[7] & 0xff);
    }

    /**
     * 修改密码指令操作
     */
    public static String updatePassword(String s) {
        String password = encode(Integer.parseInt(s));
        String m4 = password.substring(0, 2);
        String m3 = password.substring(2, 4);
        String m2 = password.substring(4, 6);
        String m1 = password.substring(6);
        String command = "fe0402" + m1 + m2 + m3 + m4 + "04ffffffffffffffffffffffff";
        return command;
    }
}
