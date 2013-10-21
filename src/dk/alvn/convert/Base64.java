package dk.alvn.convert;

public class Base64 {

	static final byte[] charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();


	public static String encode(String s) {
		return encode(s.getBytes());
	}

	public static String encode(byte[] bytes) {
		return encode(bytes, 0, bytes.length);
	}

	public static String encode(final byte[] bytes, final int start, final int length) {
		final byte[] encoded = new byte[ calculateLengthOfEncoding(length) ];
		int index = 0, triplet = 0;
		
		for (int i = start; i < length; i++) {
			switch(triplet++) {
			case 0:
				encoded[index++] = charSet[ firstSix(bytes[i]) ];
				break;
			case 1:
				encoded[index++] = charSet[ lastTwoPlusFour(bytes[i-1], bytes[i]) ];
				break;
			case 2:
				encoded[index++] = charSet[ lastFourPlusTwo(bytes[i-1], bytes[i]) ];
				encoded[index++] = charSet[ lastSix(bytes[i]) ];
				triplet = 0;
				break;
			}
		}
		
		return new String(encoded);
	}
	
	/**
	 * Get the first 6 bits of an octet/byte
	 * @param b
	 * @return
	 */
	static final byte firstSix(byte b) {
		return (byte) ((0xFC & b) >> 2);
	}
	
	/**
	 * Get the least significant two bits of a
	 * and the most significant four bits of b
	 * as a single byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte lastTwoPlusFour(byte a, byte b) {
		return (byte) (((a & 0x03) << 4) ^ (b >> 4));
	}
	
	/**
	 * Get the least significant four bits of a
	 * and the most significant two bits of b
	 * as a single byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte lastFourPlusTwo(byte a, byte b) {
		return (byte) (((a & 0x0F) << 2) ^ (b >> 6));
	}

	/**
	 * Get the least significant 6 bits
	 * @param b
	 * @return
	 */
	static final byte lastSix(byte b) {
		return (byte) (b & 0x3F);
	}
	
	static final int calculateLengthOfEncoding(int l) {
//		return l / 3 * 4 + ((l % 3 > 0) ? 4 : 0);
		return (l + 2) / 3 * 4;
	}

}
