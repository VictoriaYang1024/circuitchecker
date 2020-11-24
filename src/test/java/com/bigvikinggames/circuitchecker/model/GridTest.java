package com.bigvikinggames.circuitchecker.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Some integration tests to make sure the grid works as expected.
 * 
 * @author gcl
 */
public class GridTest {
	
	Grid grid;
	
	@Before
	public void setUp() {
		int[] lineSizes = new int[] {
			4,
			3,
			4,
			4
		};
		
		TileType[] types = new TileType[] {
			TileType.SOURCE, TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.BLANK,
			TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS,
			TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS,
			TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.CONNECTION_GROUPS, TileType.SINK
		};
		
		int[] connectionGroups = new int[] {
			0b1100, 0b1110, 0b1111, 0,
			0b0001, 0b0101, 0b0100,
			0b1100, 0b0011, 0b1001, 0b0011,
			0b1001, 0b1010, 0b1010, 0b0010
		};
		
		List<Line> lines = new ArrayList<>();
		for (int lineIdx = 0, tileIdx = 0; lineIdx < 4; lineIdx++) {
			int lineSize = lineSizes[lineIdx];
			List<Tile> tiles = new ArrayList<>();
			for (int tileNum = 0; tileNum < lineSize; tileNum++, tileIdx++) {
				List<ConnectionDirection> directions = Tile.convertValuesToDirections(connectionGroups[tileIdx]);
				Tile tile = new Tile(tileNum, lineIdx, types[tileIdx], directions);
				tiles.add(tile);
			}
			Line line = new Line(tiles);
			lines.add(line);
		}
		grid = new Grid(lines);
	}
	
	@Test
	public void roundTripTest() {
		byte[] bytes = grid.toBytes();
		Grid readGrid = Grid.fromBytes(bytes);
		assertEquals(grid, readGrid);
	}
	
}
