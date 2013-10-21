package dk.alvn.convert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class Base64Test {

	@Test
	public void correctLength() {
		assertEquals(Base64.charSet.length, 64);
	}
	
	@Test
	public void correctCharset() {
		byte[] charset = new byte[64];
		int i = 0;
		for (byte c = 'A'; c <= 'Z'; c++)
			charset[i++] = c;
		for (byte c = 'a'; c <= 'z'; c++)
			charset[i++] = c;
		for (byte c = '0'; c <= '9'; c++)
			charset[i++] = c;
		charset[i++] = '+';
		charset[i] = '/';
		
		assertArrayEquals(charset, Base64.charSet);
	}
	
	@Test
	public void firstSixBitsOfAByte() {
		//Man -> TWFu
		assertEquals(19, Base64.firstSix((byte) 'M'));
		//any -> YW55
		assertEquals(24, Base64.firstSix((byte) 'a'));
	}
	
	@Test
	public void lastTwoPlusFirstFourBits() {
		assertEquals(22, Base64.lastTwoPlusFour((byte)'M', (byte)'a'));
		assertEquals(22, Base64.lastTwoPlusFour((byte)'a', (byte)'n'));
	}
	
	@Test
	public void LastFourPlusTwo() {
		assertEquals(5, Base64.lastFourPlusTwo((byte)'a', (byte)'n'));
		assertEquals(57, Base64.lastFourPlusTwo((byte)'n', (byte)'y'));
	}
	
	@Test
	public void lastSix() {
		assertEquals(46, Base64.lastSix((byte)'n'));
		assertEquals(57, Base64.lastSix((byte)'y'));
	}
	
	@Test
	public void lookup() {
		assertEquals((byte)'T', Base64.charSet[ Base64.firstSix((byte)'M') ]);
		assertEquals((byte)'W', Base64.charSet[ Base64.lastTwoPlusFour((byte)'M', (byte)'a') ]);
		assertEquals((byte)'F', Base64.charSet[ Base64.lastFourPlusTwo((byte)'a', (byte)'n') ]);
		assertEquals((byte)'u', Base64.charSet[ Base64.lastSix((byte) 'n') ]);
	}
	
	@Test
	public void ensureEncodedLength() {
		assertEquals(4, Base64.calculateLengthOfEncoding(3));
		assertEquals(8, Base64.calculateLengthOfEncoding(4));
		assertEquals(8, Base64.calculateLengthOfEncoding(5));
		assertEquals(8, Base64.calculateLengthOfEncoding(6));
		assertEquals(12, Base64.calculateLengthOfEncoding(7));
		assertEquals(84, Base64.calculateLengthOfEncoding(62));
	}
	
	@Test
	public void correctEncoding() {
		assertEquals("YW55IGNhcm5hbCBwbGVhc3Vy", Base64.encode("any carnal pleasu"));
	}
	
	/*@Test
	public void correctEncodingBytes() {
		assertEquals("YW55IGNhcm5hbCBwbGVhc3Vy", Base64.encode("any carnal pleasu".getBytes()));
	}
	
	@Test
	public void correctEncodingBytesWithDelimiters() {
		assertEquals("YXN1cmUu", Base64.encode("pleasure".getBytes(), 2, "asure".length()));
	}
	
	@Test
	public void correctPadding1() {
		assertEquals("YW55IGNhcm5hbCBwbGVhc3VyZS4=", Base64.encode("any carnal pleasure."));
	}
	
	@Test
	public void correctPadding2() {
		assertEquals("ZWFzdXJlLg==", Base64.encode("easure."));
	}*/

}
