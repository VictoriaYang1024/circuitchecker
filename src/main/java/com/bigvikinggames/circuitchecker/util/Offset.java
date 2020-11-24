package com.bigvikinggames.circuitchecker.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Bit and byte offsets.
 * @author gcl
 */
public class Offset implements Comparable<Offset> {
	protected int bitOffset;
	protected int byteOffset;

	public Offset(int byteOffset, int bitOffset) {
		this.bitOffset = bitOffset;
		this.byteOffset = byteOffset;
	}
	
	public int getBitOffset() {
		return bitOffset;
	}

	public void setBitOffset(int bitOffset) {
		this.bitOffset = bitOffset;
	}

	public int getByteOffset() {
		return byteOffset;
	}

	public void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(7, 29, this);
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
		final Offset other = (Offset) obj;
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int compareTo(Offset o) {
		if (byteOffset != o.byteOffset) {
			return Integer.compare(byteOffset, o.byteOffset);
		}
		return Integer.compare(bitOffset, o.bitOffset);
	}
	
	
}
