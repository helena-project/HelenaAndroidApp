package edu.stanford.cs.sing.common.helper;

public class ByteWork {
	



    /***
     * returns byte part of a byte array
     * @param bytes byte array
     * @param start starting index of byte array
     * @param end end index of byte array
     * @return byte[stop-end]
     */
    public static byte[] getBytes(byte[] bytes, int start, int end){
        byte[] b = new byte[end-start+1];
        for(int i=0; start <= end;i++){
            b[i]=bytes[start++];
        }
        return b;
    }

    public int unsignedIntFromByteArray(byte[] bytes) {
        int res=0;
        for (int i=0;i<bytes.length;i++){
            res = res | ((bytes[i] & 0xff) << i*8);
        }
        return res;
    }


    public static int convertTwoBytesToInt (byte[] bytes)
    {
        if (bytes.length !=2) throw new AssertionError("Expected 2 bytes");
        return (bytes[1] << 8) | (bytes[0] & 0xFF);
    }

    public static int convertFourBytesToInt (byte[] bytes)
    {
        if (bytes.length !=4 ) throw new AssertionError("Expected 4 bytes");
        return (bytes[3] << 24) | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
    }

    /***
     * Convert two unsigned bytes to int Litle Endian!
     * @param bytes
     * @return converted int
     */
    public static int convertTwoUnsignedBytesToInt (byte[] bytes)
    {
        if (bytes.length != 2) throw new AssertionError("Expected 2 bytes");
        return (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
    }

    /***
     * convert 4 unsigned bytes to long
     * @param bytes
     * @return converted long
     */
    public static long convertFourUnsignedBytesToLong (byte[] bytes)
    {
        if (bytes.length !=4) throw new AssertionError("Expected 4 bytes");
        return (long) (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

}
