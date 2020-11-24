package com.bigvikinggames.circuitchecker.model;

import com.bigvikinggames.circuitchecker.util.Offset;
import com.bigvikinggames.circuitchecker.util.BitSet;
import com.bigvikinggames.circuitchecker.util.BitUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class representing a line of tiles.
 * 
 * @author gcl
 */
public class Line {
	
	private final List<Tile> tiles;
	
	public Line(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public List<Tile> getTiles() {
		return tiles;
	}
	
	public Optional<Tile> getTile(int x) {
		if (x < 0 || x >= tiles.size()) {
			return Optional.empty();
		}
		return Optional.of(tiles.get(x));
	}
	
	public String prettyPrint() {
		return tiles.stream().map(Tile::prettyPrint).collect(Collectors.joining());
	}
	
	public List<BitSet> toBits() {
		List<BitSet> bits = new ArrayList<>();
		bits.add(new BitSet(8, tiles.size()));
		tiles.forEach(tile -> {
			bits.addAll(tile.toBits());
		});
		return bits;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.tiles);
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
		final Line other = (Line) obj;
		return Objects.equals(this.tiles, other.tiles);
	}

	@Override
	public String toString() {
		return "Line{" + "tiles=" + tiles + '}';
	}
	
	public static Line fromBytes(byte[] bytes, Offset offset, int y) {
		List<Tile> tiles = new ArrayList<>();
		int numTiles = BitUtils.readBits(8, bytes, offset);
		for (int idx = 0; idx < numTiles; idx++) {
			Tile tile = Tile.fromBytes(bytes, offset, idx, y);
			tiles.add(tile);
		}
		return new Line(tiles);
	}
}
