package com.bigvikinggames.circuitchecker.model;

import com.bigvikinggames.circuitchecker.util.Offset;
import com.bigvikinggames.circuitchecker.util.BitSet;
import com.bigvikinggames.circuitchecker.util.BitUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for a tile.
 * 
 * @author gcl
 */
public class Tile {
	
	private final TileType type;
	private final List<ConnectionDirection> connectionGroup;
	private final int x;
	private final int y;
	
	public Tile(int x, int y, TileType type, List<ConnectionDirection> connectionGroup) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.connectionGroup = connectionGroup;
	}

	public TileType getType() {
		return type;
	}

	public List<ConnectionDirection> getConnectionGroup() {
		return connectionGroup;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public boolean hasConnection(ConnectionDirection direction) {
		return connectionGroup.contains(direction);
	}
	
	public List<BitSet> toBits() {
		List<BitSet> bits = new ArrayList<>();
		bits.add(new BitSet(2, type.ordinal()));
		if (type != TileType.BLANK) {
			bits.add(new BitSet(4, convertDirectionsToValues(connectionGroup)));
		}
		return bits;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.type);
		hash = 79 * hash + Objects.hashCode(this.connectionGroup);
		hash = 79 * hash + this.x;
		hash = 79 * hash + this.y;
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
		final Tile other = (Tile) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return Objects.equals(this.connectionGroup, other.connectionGroup);
	}

	@Override
	public String toString() {
		return "Tile{" + "type=" + type + ", connectionGroup=" + connectionGroup + ", x=" + x + ", y=" + y + '}';
	}
	
	public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        switch (type) {
                case BLANK:
                        sb.append("B");
                        break;
                case SOURCE:
                        sb.append("I");
                        break;
                case SINK:
                        sb.append("O");
                        break;
                case CONNECTION_GROUPS:
                        sb.append("G");
                        break;
        }
        sb.append(" ");
        if (connectionGroup.contains(ConnectionDirection.LEFT)) {
                sb.append("←");
        } else {
                sb.append(" ");
        }
        if (connectionGroup.contains(ConnectionDirection.TOP)) {
                sb.append("↑");
        } else {
                sb.append(" ");
        }
        if (connectionGroup.contains(ConnectionDirection.BOTTOM)) {
                sb.append("↓");
        } else {
                sb.append(" ");
        }
        if (connectionGroup.contains(ConnectionDirection.RIGHT)) {
                sb.append("→");
        } else {
                sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
	}
	
	public static List<ConnectionDirection> convertValuesToDirections(int values) {
		return IntStream.range(0, ConnectionDirection.values().length)
				.filter(idx -> ((1 << idx) & values) != 0)
				.mapToObj(idx -> ConnectionDirection.values()[idx])
				.collect(Collectors.toList());
	}
	
	public static int convertDirectionsToValues(List<ConnectionDirection> tileDirections) {
		ConnectionDirection[] directions = ConnectionDirection.values();
		int value = 0;
		for (int idx = 0; idx < directions.length; idx++) {
			if (tileDirections.contains(directions[idx])) {
				value |= (1 << idx);
			}
		}
		return value;
	}
	
	private static Tile blankTile(int x, int y) {
		return new Tile(x, y, TileType.BLANK, Collections.emptyList());
	}
	
	private static Tile normalTile(TileType type, int x, int y, int connectionValues) {
		return new Tile(x, y, type, convertValuesToDirections(connectionValues));
	}

	public static Tile fromBytes(byte[] bytes, Offset offset, int x, int y) {
		int typeNum = BitUtils.readBits(2, bytes, offset);
		TileType type = TileType.values()[typeNum];
		if (type == TileType.BLANK) {
			return blankTile(x, y);
		}
		int connectionValues = BitUtils.readBits(4, bytes, offset);
		return normalTile(type, x, y, connectionValues);
	}
}
