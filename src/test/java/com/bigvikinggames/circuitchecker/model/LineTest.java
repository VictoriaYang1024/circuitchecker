/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bigvikinggames.circuitchecker.model;

import com.bigvikinggames.circuitchecker.util.Offset;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the lines in a grid.
 * 
 * @author gcl
 */
public class LineTest {
	
	Tile blankTile1 = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
	Tile blankTile2 = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
	Tile blankTile3 = new Tile(0, 0, TileType.BLANK, Collections.emptyList());
	Tile connectionGroupTile1 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
	Tile connectionGroupTile2 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
	Tile connectionGroupTile3 = new Tile(0, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
	Tile sourceTile1 = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
	Tile sourceTile2 = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
	Tile sourceTile3 = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
	Tile sinkTile1 = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
	Tile sinkTile2 = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
	Tile sinkTile3 = new Tile(0, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
	
	@Test
	public void testEquals() {
		// blank lines
		Line blankLine1 = new Line(Collections.emptyList());
		Line blankLine2 = new Line(Collections.emptyList());
		assertEquals(blankLine1, blankLine2);
		
		// single item lines
		Line singleItemLine1 = new Line(Arrays.asList(blankTile1));
		Line singleItemLine2 = new Line(Arrays.asList(blankTile2));
		Line singleItemLine3 = new Line(Arrays.asList(sourceTile3));
		assertEquals(singleItemLine1, singleItemLine2);
		assertNotEquals(singleItemLine1, singleItemLine3);
		
		// multiple item lines
		Line multiLine1 = new Line(Arrays.asList(sourceTile1, blankTile1, sinkTile1));
		Line multiLine2 = new Line(Arrays.asList(sourceTile2, blankTile2, sinkTile2));
		Line multiLine3 = new Line(Arrays.asList(sourceTile2, blankTile2, connectionGroupTile3));
		assertEquals(multiLine1, multiLine2);
		assertNotEquals(multiLine1, multiLine3);
		
		// differing sizes
		Line line1 = new Line(Collections.emptyList());
		Line line2 = new Line(Arrays.asList(sourceTile2));
		Line line3 = new Line(Arrays.asList(sourceTile3, connectionGroupTile3, blankTile3, sinkTile3));
		assertNotEquals(line1, line2);
		assertNotEquals(line1, line3);
		assertNotEquals(line2, line3);
	}
	
	@Test
	public void testFromBytes() {
		// blank line
		Line blankLine = new Line(Collections.emptyList());
		byte[] bytes = new byte[] {
			0b00000000
		};
		Offset offset = new Offset(0, 0);
		Line readLine = Line.fromBytes(bytes, offset, 0);
		assertEquals(blankLine, readLine);
		
		// single item line
		Line singleItemLine1 = new Line(Arrays.asList(blankTile1));
		bytes = new byte[] {
			0b00000001,
			0b00000000
		};
		offset = new Offset(0, 0);
		readLine = Line.fromBytes(bytes, offset, 0);
		assertEquals(singleItemLine1, readLine);
		Line singleItemLine2 = new Line(Arrays.asList(connectionGroupTile1));
		bytes = new byte[] {
			0b00000001,
			(byte)0b11000100
		};
		offset = new Offset(0, 0);
		readLine = Line.fromBytes(bytes, offset, 0);
		assertEquals(singleItemLine2, readLine);
		
		// multiple item line
		Tile multiSourceTile = new Tile(0, 0, TileType.SOURCE, Arrays.asList(ConnectionDirection.TOP));
		Tile multiConnectionGroupTile = new Tile(1, 0, TileType.CONNECTION_GROUPS, Arrays.asList(ConnectionDirection.TOP));
		Tile multiSinkTile = new Tile(2, 0, TileType.SINK, Arrays.asList(ConnectionDirection.TOP));
		Tile multiBlankTile = new Tile(3, 0, TileType.BLANK, Collections.emptyList());
		Line multipleItemLine = new Line(Arrays.asList(multiSourceTile, multiConnectionGroupTile, multiSinkTile, multiBlankTile));
		bytes = new byte[] {
			0b00000100,
			0b01000111,
			0b00011000,
			0b01000000
		};
		offset = new Offset(0, 0);
		readLine = Line.fromBytes(bytes, offset, 0);
		assertEquals(multipleItemLine, readLine);
	}
	
}
