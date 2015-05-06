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
    public void ensureEncodedLengthIncludingLinereturn() {
        assertEquals(364, Base64.calculateLengthOfEncodingIncludingLinereturn(269));
    }
    
    @Test
    public void correctEncoding() {
        assertEquals("YW55IGNhcm5hbCBwbGVhc3Vy", Base64.encode("any carnal pleasur"));
    }
    
    @Test
    public void correctEncodingBytes() {
        assertEquals("YW55IGNhcm5hbCBwbGVhc3Vy", Base64.encode("any carnal pleasur".getBytes()));
    }
    
    @Test
    public void correctEncodingBytesWithDelimiters() {
        assertEquals("YXN1", Base64.encode("pleasure.".getBytes(), 3, "asu".length()));
    }
    
    @Test
    public void correctPadding1() {
        assertEquals("YW55IGNhcm5hbCBwbGVhc3VyZS4=", Base64.encode("any carnal pleasure."));
    }
    
    @Test
    public void correctPadding2() {
        assertEquals("ZWFzdXJlLg==", Base64.encode("easure."));
    }
    
    @Test
    public void giganticText() {
        String txt =  "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
        String result = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\n"
                + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\n"
                + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\n"
                + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\n"
                + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        assertEquals(result, Base64.encode(txt));
    }
    
    @Test
    public void timingEncode() {
        long time = System.currentTimeMillis();
        String txt =  "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
        for (int i = 0; i < 50107; i++)
            Base64.encode(txt);
        System.out.println("encoding: " + (System.currentTimeMillis() - time));
    }
    
    
    @Test
    public void assertDecodeCharset() {
        assertEquals(0, Base64.decodeSet['A']);
        assertEquals(19, Base64.decodeSet['T']);
        assertEquals(22, Base64.decodeSet['W']);
        assertEquals(42, Base64.decodeSet['q']);
        assertEquals(55, Base64.decodeSet['3']);
        assertEquals(62, Base64.decodeSet['+']);
    }
    
    /*
     * decode 4 bytes at a time into three decoded
     */
    @Test
    public void firstByteDecoded() {
        assertEquals((byte)'M', Base64.decodeSixPlusTwo(Base64.decodeSet['T'], Base64.decodeSet['W']));
        assertEquals((byte)'a', Base64.decodeSixPlusTwo(Base64.decodeSet['Y'], Base64.decodeSet['W']));
    }
    
    @Test
    public void secondByteDecoded() {
        assertEquals((byte)'a', Base64.decodeLastFourPlusFour(Base64.decodeSet['W'], Base64.decodeSet['F']));
        assertEquals((byte)'n', Base64.decodeLastFourPlusFour(Base64.decodeSet['W'], Base64.decodeSet['5']));
    }
    
    @Test
    public void thirdByteDecoded() {
        assertEquals((byte)'n', Base64.decodeLastTwoPlusSix(Base64.decodeSet['F'], Base64.decodeSet['u']));
        assertEquals((byte)'y', Base64.decodeLastTwoPlusSix(Base64.decodeSet['5'], Base64.decodeSet['5']));
    }
    /*
     * decoding the individual bytes complete
     */
    
    @Test
    public void ensureDecodedLength() {
        assertEquals(3, Base64.calculateLengthOfDecoding("YXN1".getBytes(), 0, 4));
        assertEquals(3, Base64.calculateLengthOfDecoding("TWFu".getBytes(), 0, 4));
        assertEquals(18, Base64.calculateLengthOfDecoding("YW55IGNhcm5hbCBwbGVhc3Vy".getBytes(), 0, 24));
        assertEquals(7, Base64.calculateLengthOfDecoding("ZWFzdXJlLg==".getBytes(), 0, 12));
        assertEquals(20, Base64.calculateLengthOfDecoding("YW55IGNhcm5hbCBwbGVhc3VyZS4=".getBytes(), 0, 28));
    }
    
    @Test
    public void ensureDecodedLengthconsideringLinereturn() {
        String txt = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\n"
                + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\n"
                + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\n"
                + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\n"
                + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        byte[] input = txt.getBytes();
        assertEquals(269, Base64.calculateLengthOfDecodingWithoutLinereturn(input, 0, input.length));
    }
    
    @Test
    public void correctDecoding() {
        assertArrayEquals("any carnal pleasur".getBytes(), Base64.decode("YW55IGNhcm5hbCBwbGVhc3Vy"));
        assertArrayEquals("any carnal pleasu".getBytes(), Base64.decode("YW55IGNhcm5hbCBwbGVhc3U="));
        assertArrayEquals("any carnal pleas".getBytes(), Base64.decode("YW55IGNhcm5hbCBwbGVhcw=="));
    }
    
    @Test
    public void timingDecode() {
        long time = System.currentTimeMillis();
        String txt = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\n"
                + "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg\n"
                + "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\n"
                + "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\n"
                + "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        for (int i = 0; i < 50107; i++)
            Base64.decode(txt);
        System.out.println("decoding: " + (System.currentTimeMillis() - time));
    }
    
    
    @Test
    public void encodeDecode() {
        byte[] txt = "kagemandenfraOtterupfalderaltidpaahalen".getBytes();
        assertArrayEquals(txt, Base64.decode(Base64.encode(txt)));
        
        txt = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes();
        assertArrayEquals(txt, Base64.decode(Base64.encode(txt)));
    }
    
    @Test
    public void writeout() {
        System.out.println(Base64.encode("kagemanden \n fra Otterup..."));
    }

}
