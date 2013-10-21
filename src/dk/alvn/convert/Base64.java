package dk.alvn.convert;

public class Base64 {

	static final byte[] charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
	static final byte[] decodeSet = new byte[256];//unsigned byte max
	static final byte paddingChar = (byte) '=';
	static {
		for(byte i = 0; i < 64; i++)
			decodeSet[ charSet[i] ] = i;
	}


	public static final String encode(String s) {
		return encode(s.getBytes());
	}

	public static final String encode(byte[] bytes) {
		return encode(bytes, 0, bytes.length);
	}

	/**
	 * MIME demands short lines of 6-bit characters, and the length of each line is considered to be
	 * a maximum of 76 characters.
	 * @param bytes
	 * @param start
	 * @param length
	 * @return
	 */
	public static final String encode(final byte[] bytes, final int start, final int length) {
		final byte[] encoded = new byte[ calculateLengthOfEncoding(length) ];
		int index = 0, triplet = 0;
		byte remaining = 0x00;
		
		for (int i = start; i < start + length; i++) {
			switch(triplet++) {
			case 0:
				encoded[index++] = charSet[ firstSix(bytes[i]) ];
				remaining = lastTwo(bytes[i]);
				break;
			case 1:
				encoded[index++] = charSet[ plusFour(remaining, bytes[i]) ];
				remaining = lastFour(bytes[i]);
				break;
			case 2:
				encoded[index++] = charSet[ plusTwo(remaining, bytes[i]) ];
				encoded[index++] = charSet[ lastSix(bytes[i]) ];
				triplet = 0;
				break;
			}
		}
		if (triplet > 0)
			encoded[index++] = charSet[ remaining ];
		while (index < encoded.length)
			encoded[index++] = '=';
		
		return new String(encoded);
	}
	
	public static final byte[] decode(final String s) {
		return decode(s.getBytes());
	}
	
	public static final byte[] decode(final byte[] bytes) {
		return decode(bytes, 0, bytes.length);
	}
	
	public static final byte[] decode(final byte[] bytes, final int start, final int length) {
		final int padding = numberOfPaddingChars(bytes, start, length), end = start + length -1 - padding;
		final byte[] decoded = new byte[ decodedLength(bytes, length, padding) ];
		
		int index = 0, quadlet = 0;
		byte output = 0x00;
		
		for(int i = start; i <= end; i++) {
			byte d  = decodeSet[bytes[i]];
			switch(quadlet++) {
			case 0:
				output = encodedFirstByte( d );
				break;
			case 1:
				decoded[index++] = encodedFirstPlusTwo(output, d);
				output = encodedLastFour(d);
				break;
			case 2:
				decoded[index++] = encodedPlusFour(output, d);
				output = encodedLastTwo(d);
				break;
			case 3:
				decoded[index++] = encodedPlusSix(output, d);
				quadlet = 0;
				break;
			}
		}
		
		return decoded;
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
	 * Get the least significant two bits of {@code a}<br>
	 * and the most significant four bits of {@code b}<br>
	 * as a single byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte lastTwoPlusFour(byte a, byte b) {
		return plusFour(lastTwo(a), b);
	}
	private static final byte lastTwo(byte a) {
		return (byte) ((a & 0x03) << 4);
	}
	private static final byte plusFour(byte a, byte b) {
		return (byte) (a ^ (b >> 4));
	}
	
	/**
	 * Get the least significant four bits of {@code a}<br>
	 * and the most significant two bits of {@code b}<br>
	 * as a single byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte lastFourPlusTwo(byte a, byte b) {
		return plusTwo(lastFour(a), b);
	}
	private static final byte lastFour(byte a) {
		return (byte) ((a & 0x0F) << 2);
	}
	private static final byte plusTwo(byte a, byte b) {
		return (byte) (a ^ (b >> 6));
	}

	/**
	 * Get the least significant 6 bits
	 * @param b
	 * @return
	 */
	static final byte lastSix(byte b) {
		return (byte) (b & 0x3F);
	}
	
	/**
	 * Each input byte has only populated the 6 least significant bits.
	 * First byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte decodeSixPlusTwo(byte a, byte b) {
		return encodedFirstPlusTwo(encodedFirstByte(a), b);
	}
	private static final byte encodedFirstByte(byte a) {
		return (byte) (a << 2);
	}
	private static final byte encodedFirstPlusTwo(byte a, byte b) {
		return (byte) (a ^ (b >> 4));
	}
	
	/**
	 * Second byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte decodeLastFourPlusFour(byte a, byte b) {
		return encodedPlusFour(encodedLastFour(a), b);
	}
	private static final byte encodedLastFour(byte a) {
		return (byte) (a << 4);
	}
	private static final byte encodedPlusFour(byte a, byte b) {
		return (byte) (a ^ (b >> 2));
	}
	
	/**
	 * Third byte
	 * @param a
	 * @param b
	 * @return
	 */
	static final byte decodeLastTwoPlusSix(byte a, byte b) {
		return encodedPlusSix(encodedLastTwo(a), b);
	}
	private static final byte encodedLastTwo(byte a) {
		return (byte) (a << 6);
	}
	private static final byte encodedPlusSix(byte a, byte b) {
		return (byte) (a ^ b);
	}
	
	/**
	 * For each triplet of bytes, four needs to be produced.
	 * If any remainder is found when dividing by 3, then additional 4 bytes
	 * needs to be added.<br>
	 * So the following needs to be calculated.<br>
	 * {@code length} / 3 * 4 + (({@code length} % 3 > 0) ? 4 : 0
	 * @param length
	 * @return
	 */
	static final int calculateLengthOfEncoding(int length) {
		return (length + 2) / 3 * 4;
	}
	
	static final int calculateLengthOfDecoding(final byte[] bytes, final int start, final int length) {
		return decodedLength(bytes, length, numberOfPaddingChars(bytes, start, length));
	}
	private static final int numberOfPaddingChars(final byte[] bytes, final int start, final int length) {
		int endings = 0;
		while (bytes[length + start - 1 - endings] == paddingChar) endings++;
		return endings;
	}
	private static final int decodedLength(final byte[] bytes, final int length, final int padding) {
		return length / 4 * 3 - padding;
	}

}
