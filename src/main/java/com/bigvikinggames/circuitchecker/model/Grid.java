package com.bigvikinggames.circuitchecker.model;

import com.bigvikinggames.circuitchecker.util.Offset;
import com.bigvikinggames.circuitchecker.util.BitSet;
import com.bigvikinggames.circuitchecker.util.BitUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Class for representing a grid.
 * 
 * @author gcl
 */
public class Grid {
	
	private final List<Line> lines;
	
	public Grid(List<Line> lines) {
		this.lines = lines;
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public Optional<Tile> getTile(int x, int y) {
		if (y < 0 || y >= lines.size()) {
			return Optional.empty();
		}
		return lines.get(y).getTile(x);
	}
	
	public List<BitSet> toBits() {
		List<BitSet> bits = new ArrayList<>();
		bits.add(new BitSet(8, lines.size()));
		lines.forEach(line -> {
			bits.addAll(line.toBits());
		});
		return bits;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.lines);
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
		final Grid other = (Grid) obj;
		return Objects.equals(this.lines, other.lines);
	}

	@Override
	public String toString() {
		return "Grid{" + "lines=" + lines + '}';
	}
	
	public String prettyPrint() {
		return lines.stream().map(Line::prettyPrint).collect(Collectors.joining(System.lineSeparator()));
	}
	
	public byte[] toBytes() {
		List<BitSet> bits = toBits();
		return BitUtils.bitsToBytes(bits);
	}
	
	public static Grid fromBytes(byte[] bytes) {
		List<Line> lines = new ArrayList<>();
		int numLines = Byte.toUnsignedInt(bytes[0]);
		Offset offset = new Offset(1, 0);
		for (int idx = 0; idx < numLines; idx++) {
			lines.add(Line.fromBytes(bytes, offset, idx));
		}
		return new Grid(lines);
	}
	
	private static List<ConnectionDirection> getRandomDirections() {
		List<ConnectionDirection> directions = new ArrayList<>();
		ConnectionDirection[] directionValues = ConnectionDirection.values();
		for (ConnectionDirection directionValue : directionValues) {
			if (ThreadLocalRandom.current().nextBoolean()) {
				directions.add(directionValue);
			}
		}
		return directions;
	}
	
	public static Grid createRandomGrid() {
		int numLines = ThreadLocalRandom.current().nextInt(3, 20);
		List<Line> lines = new ArrayList<>(numLines);
		for (int idxLine = 0; idxLine < numLines; idxLine++) {
			int numTiles = ThreadLocalRandom.current().nextInt(4, 20);
			List<Tile> tiles = new ArrayList<>(numTiles);
			for (int idxTile = 0; idxTile < numTiles; idxTile++) {
				TileType type = TileType.values()[ThreadLocalRandom.current().nextInt(TileType.values().length)];
				List<ConnectionDirection> directions;
				if (type == TileType.BLANK) {
					directions = Collections.emptyList();
				} else {
					directions = getRandomDirections();
				}
				tiles.add(new Tile(idxTile, idxLine, type, directions));
			}
			lines.add(new Line(tiles));
		}
		return new Grid(lines);
	}
}
