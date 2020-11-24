package com.bigvikinggames.circuitchecker.util;

import java.util.List;

/**
 * Utilities for dealing with bits.
 * 
 * @author gcl
 */
public class BitUtils {
	/**
	 * Read in the given number of bits and output them while updating the offset.
	 * 
	 * @param numBits assumes 1-32 bits
	 * @param bytes
	 * @param offset outparam
	 * @return The bits that were read
	 */
	public static int readBits(int numBits, byte[] bytes, Offset offset) {
		int bitOffset = offset.getBitOffset();
		int byteOffset = offset.getByteOffset();
		int numBitsToRead = numBits;
		
		// unaligned read
		int value = 0;
		if (bitOffset != 0 && numBitsToRead > 0) {
			int leadingBitsToRead = Math.min(numBitsToRead, 8 - bitOffset);
			byte b = bytes[byteOffset];
			int shiftAmount = 8 - bitOffset - leadingBitsToRead;
			int readMask = ((1 << leadingBitsToRead) - 1) << shiftAmount;
			value = (Byte.toUnsignedInt(b) & readMask) >> shiftAmount;
			if (leadingBitsToRead == 8 - bitOffset) {
				byteOffset++;
			}
			bitOffset = (bitOffset + leadingBitsToRead) % 8;
			numBitsToRead -= leadingBitsToRead;
		}
		// bitOffset is now 0 or numBitsToRead is 0
		
		// aligned read
		while (numBitsToRead >= 8) {
			byte b = bytes[byteOffset];
			value <<= 8;
			value |= Byte.toUnsignedInt(b);
			numBitsToRead -= 8;
			byteOffset++;
		}
		
		// trailing bits
		if (numBitsToRead > 0) {
			byte b = bytes[byteOffset];
			value <<= numBitsToRead;
			int numTrailingBits = 8 - numBitsToRead;
			int readMask = ((1 << numBitsToRead) - 1) << numTrailingBits;
			value |= (Byte.toUnsignedInt(b) & readMask) >> numTrailingBits;
			bitOffset = numBitsToRead;
		}
		
		offset.setBitOffset(bitOffset);
		offset.setByteOffset(byteOffset);
		
		return value;
	}
	
	/**
	 * Convert the list of bits to a packed array of bytes.
	 * 
	 * @param bitSets
	 * @return The complete packed byte array.
	 */
	public static byte[] bitsToBytes(List<BitSet> bitSets) {
		// create the byte array
		int totalNumBits = bitSets.stream().mapToInt(BitSet::getNumBits).sum();
		int totalNumBytes = totalNumBits / 8;
		if (totalNumBits % 8 != 0) {
			totalNumBytes++;
		}
		byte[] bytes = new byte[totalNumBytes];
		
		// start filling in bits
		int bitOffset = 0;
		int byteOffset = 0;
		for (BitSet bitSet : bitSets) {
			int bits = bitSet.getBits();
			int numBits = bitSet.getNumBits();
			
			while (numBits > 0) {
				// easy insert
				int numBitsRemainingInByte = 8 - bitOffset;
				if (numBits < numBitsRemainingInByte) {
					int readMask = (1 << numBits) - 1;
					int writeOffset = numBitsRemainingInByte - numBits;
					bytes[byteOffset] |= (readMask & bits) << writeOffset;
					bitOffset += numBits;
					break; // nothing left to write
				}

				// hard insert (numBits > remaining)
				int offset = numBits - numBitsRemainingInByte;
				int readMask = ((1 << numBitsRemainingInByte) - 1) << offset;
				bytes[byteOffset] |= (readMask & bits) >> offset;
				bitOffset = 0;
				byteOffset++;
				numBits -= numBitsRemainingInByte;
			}
		}
		
		// done
		return bytes;
	}
}
