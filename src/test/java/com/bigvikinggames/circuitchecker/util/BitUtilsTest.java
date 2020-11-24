/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigvikinggames.circuitchecker.util;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the BitUtils.
 * 
 * @author gcl
 */
public class BitUtilsTest {

	@Test
	public void testReadNoBits() {
		byte[] bytes = new byte[2];
		bytes[0] = 0b01011001;
		bytes[1] = 0b00110110;
		Offset offset = new Offset(0, 0);
		
		// no offset
		int result = BitUtils.readBits(0, bytes, offset);
		assertEquals(0, result);
		assertEquals(0, offset.getBitOffset());
		assertEquals(0, offset.getByteOffset());
		
		// bit offset
		offset = new Offset(0, 3);
		result = BitUtils.readBits(0, bytes, offset);
		assertEquals(0, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(0, offset.getByteOffset());
		
		// byte offset
		offset = new Offset(1, 0);
		result = BitUtils.readBits(0, bytes, offset);
		assertEquals(0, result);
		assertEquals(0, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// both
		offset = new Offset(1, 3);
		result = BitUtils.readBits(0, bytes, offset);
		assertEquals(0, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
	}
	
	@Test
	public void testSmallReads() {
		byte[] bytes = new byte[2];
		bytes[0] = 0b01011001;
		bytes[1] = 0b00110110;
		
		// no offset
		Offset offset = new Offset(0, 0);
		int result = BitUtils.readBits(3, bytes, offset);
		assertEquals(2, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(0, offset.getByteOffset());
		
		// bit offset
		offset = new Offset(0, 4);
		result = BitUtils.readBits(2, bytes, offset);
		assertEquals(2, result);
		assertEquals(6, offset.getBitOffset());
		assertEquals(0, offset.getByteOffset());
		
		// byte offset
		offset = new Offset(1, 0);
		result = BitUtils.readBits(3, bytes, offset);
		assertEquals(1, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// both offsets
		offset = new Offset(1, 2);
		result = BitUtils.readBits(3, bytes, offset);
		assertEquals(6, result);
		assertEquals(5, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// offset that gets alignment
		offset = new Offset(1, 6);
		result = BitUtils.readBits(2, bytes, offset);
		assertEquals(2, result);
		assertEquals(0, offset.getBitOffset());
		assertEquals(2, offset.getByteOffset());
	}
	
	@Test
	public void testAlignedReads() {
		byte[] bytes = new byte[2];
		bytes[0] = 0b01011001;
		bytes[1] = 0b00110110;
		
		// no offset
		Offset offset = new Offset(0, 0);
		int result = BitUtils.readBits(3, bytes, offset);
		assertEquals(2, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(0, offset.getByteOffset());
		
		offset = new Offset(0, 0);
		result = BitUtils.readBits(8, bytes, offset);
		assertEquals(Byte.toUnsignedInt(bytes[0]), result);
		assertEquals(0, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// byte offset
		offset = new Offset(1, 0);
		result = BitUtils.readBits(3, bytes, offset);
		assertEquals(1, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		offset = new Offset(1, 0);
		result = BitUtils.readBits(8, bytes, offset);
		assertEquals(Byte.toUnsignedInt(bytes[1]), result);
		assertEquals(0, offset.getBitOffset());
		assertEquals(2, offset.getByteOffset());
	}
	
	@Test
	public void testByteCrossingReads() {
		byte[] bytes = new byte[3];
		bytes[0] = 0b01011001;
		bytes[1] = 0b00110110;
		bytes[2] = 0b00011110;
		
		// no offset
		Offset offset = new Offset(0, 0);
		int result = BitUtils.readBits(11, bytes, offset);
		assertEquals(0b01011001001, result);
		assertEquals(3, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// bit offset
		offset = new Offset(0, 6);
		result = BitUtils.readBits(4, bytes, offset);
		assertEquals(4, result);
		assertEquals(2, offset.getBitOffset());
		assertEquals(1, offset.getByteOffset());
		
		// byte offset
		offset = new Offset(1, 0);
		result = BitUtils.readBits(12, bytes, offset);
		assertEquals(0b001101100001, result);
		assertEquals(4, offset.getBitOffset());
		assertEquals(2, offset.getByteOffset());
		
		// both offset
		offset = new Offset(1, 6);
		result = BitUtils.readBits(6, bytes, offset);
		assertEquals(0b100001, result);
		assertEquals(4, offset.getBitOffset());
		assertEquals(2, offset.getByteOffset());

	}
	
	@Test
	public void testLargeReads() {
		byte[] bytes = new byte[6];
		bytes[0] = 0b01011001;
		bytes[1] = 0b00110110;
		bytes[2] = 0b00011110;
		bytes[3] = 0b00100110;
		bytes[4] = 0b01001011;
		bytes[5] = 0b01111011;
		
		// no offset
		Offset offset = new Offset(0, 0);
		int result = BitUtils.readBits(30, bytes, offset);
		assertEquals(0b010110010011011000011110001001, result);
		assertEquals(6, offset.getBitOffset());
		assertEquals(3, offset.getByteOffset());
		
		// bit offset
		offset = new Offset(0, 6);
		result = BitUtils.readBits(14, bytes, offset);
		assertEquals(0b01001101100001, result);
		assertEquals(4, offset.getBitOffset());
		assertEquals(2, offset.getByteOffset());
		
		// byte offset
		offset = new Offset(1, 0);
		result = BitUtils.readBits(20, bytes, offset);
		assertEquals(0b00110110000111100010, result);
		assertEquals(4, offset.getBitOffset());
		assertEquals(3, offset.getByteOffset());
		
		// both offset
		offset = new Offset(1, 6);
		result = BitUtils.readBits(14, bytes, offset);
		assertEquals(0b10000111100010, result);
		assertEquals(4, offset.getBitOffset());
		assertEquals(3, offset.getByteOffset());
	}
	
	@Test
	public void testSimpleWrite() {
		byte[] expectedBytes = new byte[1];
		expectedBytes[0] = 0b01000000;
		
		List<BitSet> bits = Arrays.asList(new BitSet(2, 1));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(1, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
	}
	
	@Test
	public void testSimpleWrites() {
		byte[] expectedBytes = new byte[1];
		expectedBytes[0] = 0b01101000;
		
		List<BitSet> bits = Arrays.asList(new BitSet(2, 1), new BitSet(3, 5));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(1, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
	}
	
	@Test
	public void testFullByteWrite() {
		byte[] expectedBytes = new byte[1];
		expectedBytes[0] = 0b01000001;
		
		List<BitSet> bits = Arrays.asList(new BitSet(8, 65));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(1, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
	}
	
	@Test
	public void testFullByteWrites() {
		byte[] expectedBytes = new byte[1];
		expectedBytes[0] = 0b01000001;
		
		List<BitSet> bits = Arrays.asList(new BitSet(3, 2), new BitSet(5, 1));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(1, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
	}
	
	@Test
	public void testLargeWrite() {
		byte[] expectedBytes = new byte[2];
		expectedBytes[0] = 0b00111100;
		expectedBytes[1] = 0b01000000;
		
		List<BitSet> bits = Arrays.asList(new BitSet(10, 241));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(2, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
		assertEquals(expectedBytes[1], resultBytes[1]);
	}
	
	@Test
	public void testLargeWrites() {
		byte[] expectedBytes = new byte[3];
		expectedBytes[0] = 0b00111100;
		expectedBytes[1] = 0b01110010;
		expectedBytes[2] = 0b01000000;
		
		List<BitSet> bits = Arrays.asList(new BitSet(10, 241), new BitSet(8, 201));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(3, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
		assertEquals(expectedBytes[1], resultBytes[1]);
		assertEquals(expectedBytes[2], resultBytes[2]);
	}
	
	@Test
	public void testMixedWrites() {
		byte[] expectedBytes = new byte[3];
		expectedBytes[0] = 0b00111100;
		expectedBytes[1] = 0b01100111;
		expectedBytes[2] = 0b00100100;
		
		List<BitSet> bits = Arrays.asList(new BitSet(10, 241), new BitSet(4, 9), new BitSet(8, 201));
		byte[] resultBytes = BitUtils.bitsToBytes(bits);
		assertEquals(3, resultBytes.length);
		assertEquals(expectedBytes[0], resultBytes[0]);
		assertEquals(expectedBytes[1], resultBytes[1]);
		assertEquals(expectedBytes[2], resultBytes[2]);
	}
	
}
