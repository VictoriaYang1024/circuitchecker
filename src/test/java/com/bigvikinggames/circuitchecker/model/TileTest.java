/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigvikinggames.circuitchecker.model;

import com.bigvikinggames.circuitchecker.util.Offset;
import com.bigvikinggames.circuitchecker.util.BitSet;
import com.bigvikinggames.circuitchecker.util.BitUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for tiles.
 * 
 * @author gcl
 */
public class TileTest {
	
	@Test
	public void testConvertDirectionsToValues() {
		List<ConnectionDirection> directions = Arrays.asList(ConnectionDirection.TOP);
		int values = Tile.convertDirectionsToValues(directions);
		assertEquals(1, values);
		
		directions = Arrays.asList(ConnectionDirection.LEFT);
		values = Tile.convertDirectionsToValues(directions);
		assertEquals(2, values);
		
		directions = Arrays.asList(ConnectionDirection.BOTTOM);
		values = Tile.convertDirectionsToValues(directions);
		assertEquals(4, values);
		
		directions = Arrays.asList(ConnectionDirection.RIGHT);
		values = Tile.convertDirectionsToValues(directions);
		assertEquals(8, values);
		
		directions = Collections.emptyList();
		values = Tile.convertDirectionsToValues(directions);
		assertEquals(0, values);
		
		directions = Arrays.asList(ConnectionDirection.TOP, ConnectionDirection.LEFT, ConnectionDirection.BOTTOM, ConnectionDirection.RIGHT);
		values = Tile.convertDirectionsToValues(directions);
		assertEquals(15, values);
	}
	
	@Test
	public void testConvertValuesToDirections() {
		List<ConnectionDirection> directions = Tile.convertValuesToDirections(1);
		assertEquals(1, directions.size());
		assertEquals(ConnectionDirection.TOP, directions.get(0));
		
		directions = Tile.convertValuesToDirections(2);
		assertEquals(1, directions.size());
		assertEquals(ConnectionDirection.LEFT, directions.get(0));
		
		directions = Tile.convertValuesToDirections(4);
		assertEquals(1, directions.size());
		assertEquals(ConnectionDirection.BOTTOM, directions.get(0));
		
		directions = Tile.convertValuesToDirections(8);
		assertEquals(1, directions.size());
		assertEquals(ConnectionDirection.RIGHT, directions.get(0));
		
		directions = Tile.convertValuesToDirections(0);
		assertEquals(0, directions.size());
		
		directions = Tile.convertValuesToDirections(15);
		assertEquals(4, directions.size());
		assertTrue(directions.contains(ConnectionDirection.TOP));
		assertTrue(directions.contains(ConnectionDirection.LEFT));
		assertTrue(directions.contains(ConnectionDirection.BOTTOM));
		assertTrue(directions.contains(ConnectionDirection.RIGHT));
		
	}
	
	@Test
	public void testConvertDirectionsRoundTrip() {
		for (int idx = 0; idx < 16; idx++) {
			int value = Tile.convertDirectionsToValues(Tile.convertValuesToDirections(idx));
			assertEquals(value, idx);
		}
	}
	
	@Test
	public void testFromBytes() {
		// blank
		byte[] bytes = new byte[] {
			0b00
		};
		Offset offset = new Offset(0, 0);
		Tile blankTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(TileType.BLANK, blankTile.getType());
		assertEquals(0, blankTile.getConnectionGroup().size());
		
		// connection group tile
		bytes = new byte[] {
			(byte)0b11000100
		};
		offset = new Offset(0, 0);
		Tile connectionGroupTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(TileType.CONNECTION_GROUPS, connectionGroupTile.getType());
		assertEquals(1, connectionGroupTile.getConnectionGroup().size());
		assertEquals(ConnectionDirection.TOP, connectionGroupTile.getConnectionGroup().get(0));
		
		// source tile
		bytes = new byte[] {
			0b01000100
		};
		offset = new Offset(0, 0);
		Tile sourceTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(TileType.SOURCE, sourceTile.getType());
		assertEquals(1, sourceTile.getConnectionGroup().size());
		assertEquals(ConnectionDirection.TOP, sourceTile.getConnectionGroup().get(0));
		
		// source tile
		bytes = new byte[] {
			(byte)0b10000100
		};
		offset = new Offset(0, 0);
		Tile sinkTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(TileType.SINK, sinkTile.getType());
		assertEquals(1, sinkTile.getConnectionGroup().size());
		assertEquals(ConnectionDirection.TOP, sinkTile.getConnectionGroup().get(0));
	}
	
	@Test
	public void testToBits() {
		// blank tile
		Tile blankTile = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
		List<BitSet> bits = blankTile.toBits();
		assertEquals(1, bits.size());
		assertEquals(2, bits.get(0).getNumBits());
		assertEquals(0, bits.get(0).getBits());
		
		// connection group tile
		Tile connectionGroupTile = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
		bits = connectionGroupTile.toBits();
		assertEquals(2, bits.size());
		assertEquals(2, bits.get(0).getNumBits());
		assertEquals(3, bits.get(0).getBits());
		assertEquals(4, bits.get(1).getNumBits());
		assertEquals(1, bits.get(1).getBits());
		
		// source tile
		Tile sourceTile = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
		bits = sourceTile.toBits();
		assertEquals(2, bits.size());
		assertEquals(2, bits.get(0).getNumBits());
		assertEquals(1, bits.get(0).getBits());
		assertEquals(4, bits.get(1).getNumBits());
		assertEquals(1, bits.get(1).getBits());
		
		// sink tile
		Tile sinkTile = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
		bits = sinkTile.toBits();
		assertEquals(2, bits.size());
		assertEquals(2, bits.get(0).getNumBits());
		assertEquals(2, bits.get(0).getBits());
		assertEquals(4, bits.get(1).getNumBits());
		assertEquals(1, bits.get(1).getBits());
	}
	
	@Test
	public void testEquals() {
		// blank tile
		Tile blankTile1 = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
		Tile blankTile2 = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
		Tile blankTile3 = new Tile(1, 0, TileType.BLANK, Collections.emptyList());
		Tile blankTile4 = new Tile(0, 1, TileType.BLANK, Collections.emptyList());
		assertEquals(blankTile1, blankTile2);
		assertNotEquals(blankTile1, blankTile3);
		assertNotEquals(blankTile1, blankTile4);
		
		// connection group tile
		Tile connectionGroupTile1 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
		Tile connectionGroupTile2 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
		Tile connectionGroupTile3 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.LEFT));
		Tile connectionGroupTile4 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP, ConnectionDirection.LEFT));
		assertEquals(connectionGroupTile1, connectionGroupTile2);
		assertNotEquals(connectionGroupTile1, connectionGroupTile3);
		assertNotEquals(connectionGroupTile1, connectionGroupTile4);
		
		// source tile
		Tile sourceTile1 = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
		Tile sourceTile2 = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
		assertEquals(sourceTile1, sourceTile2);
		
		// sink tile
		Tile sinkTile1 = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
		Tile sinkTile2 = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
		assertEquals(sinkTile1, sinkTile2);
		
		// tile types
		assertNotEquals(blankTile1, connectionGroupTile1);
		assertNotEquals(blankTile1, sourceTile1);
		assertNotEquals(blankTile1, sinkTile1);
		assertNotEquals(connectionGroupTile1, sourceTile1);
		assertNotEquals(connectionGroupTile1, sinkTile1);
		assertNotEquals(sourceTile1, sinkTile1);
	}
	
	@Test
	public void testTileRoundTrip() {
		// blank tile
		Tile blankTile = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
		byte[] bytes = BitUtils.bitsToBytes(blankTile.toBits());
		Offset offset = new Offset(0, 0);
		Tile readTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(blankTile, readTile);
		
		// connection group tile
		Tile connectionGroupTile = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
		bytes = BitUtils.bitsToBytes(connectionGroupTile.toBits());
		offset = new Offset(0, 0);
		readTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(connectionGroupTile, readTile);
		
		// source tile
		Tile sourceTile = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
		bytes = BitUtils.bitsToBytes(sourceTile.toBits());
		offset = new Offset(0, 0);
		readTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(sourceTile, readTile);
		
		// sink tile
		Tile sinkTile = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
		bytes = BitUtils.bitsToBytes(sinkTile.toBits());
		offset = new Offset(0, 0);
		readTile = Tile.fromBytes(bytes, offset, 0, 0);
		assertEquals(sinkTile, readTile);
	}
}
