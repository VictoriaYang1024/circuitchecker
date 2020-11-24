package com.bigvikinggames.circuitchecker.util;

/**
 * A set of no more than 32 bits.
 *
 * @author gcl
 */
public class BitSet {

	private final int numBits;
	private final int bits;

	public BitSet(int numBits, int bits) {
		this.numBits = numBits;
		this.bits = bits;
	}

	/**
	 * @return the numBits
	 */
	public int getNumBits() {
		return numBits;
	}

	/**
	 * @return the bits
	 */
	public int getBits() {
		return bits;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + this.numBits;
		hash = 97 * hash + this.bits;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BitSet other = (BitSet) obj;
		if (this.numBits != other.numBits) {
			return false;
		}
		return this.bits == other.bits;
	}

}
