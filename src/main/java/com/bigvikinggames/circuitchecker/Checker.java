package com.bigvikinggames.circuitchecker;

import com.bigvikinggames.circuitchecker.logging.LoggingConfig;
import com.bigvikinggames.circuitchecker.model.Grid;
import com.bigvikinggames.circuitchecker.model.Line;
import com.bigvikinggames.circuitchecker.model.Tile;
import com.bigvikinggames.circuitchecker.model.TileType;
import com.bigvikinggames.circuitchecker.model.ConnectionDirection;



import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;



public class Checker {
	private static final Logger logger = LoggerFactory.getLogger(Checker.class);
	
	/**
	 * Process the bytes.
	 * 
	 * @param bytes
	 * @param fileName 
	 */
	private static void processBytes(byte[] bytes, String fileName) {
		logger.info("Determining source and sink connections for {}", fileName);
		
		// deserialize
		Grid grid = Grid.fromBytes(bytes);
		logger.info("\n{}", grid.prettyPrint());

		// TODO: the rest is up to you

		int numOfSink = checkSinkOrSourceNumber(grid, TileType.SINK);
		int numOfSource = checkSinkOrSourceNumber(grid, TileType.SOURCE);

		Boolean sourceToSink = false;
		Boolean sinkToSource = false;



		for (int i = 0; i <  grid.getLines().size(); i++){

			Line line = grid.getLines().get(i);
			for (int j = 0; j < line.getTiles().size(); j++){
				Tile tile = line.getTiles().get(j);

				if (tile.getType() == TileType.SOURCE){
					ArrayList<Tile> readTile = new ArrayList<Tile>();
					ArrayList<Tile> checkSource = new ArrayList<Tile>();
					bfs(tile, grid, TileType.SINK, checkSource, readTile);
					if (checkSource.size() == numOfSink){
						sourceToSink = true;
						System.out.println("There exists one source connecte to all sink");
					}	
				}

				if (tile.getType() == TileType.SINK){
					ArrayList<Tile> readTileForSink = new ArrayList<Tile>();
					ArrayList<Tile> checkSink = new ArrayList<Tile>();
					bfs(tile, grid, TileType.SINK, checkSink, readTileForSink);
					if (checkSink.size() == numOfSource){
						sinkToSource = true;
						System.out.println("There exists one sink connecte to all source");
					}	
				}
			}
		}
		if (!sourceToSink){
			logger.info("(0 indexed) coordinates of the sink that can’t be connected");
		}
		if (!sinkToSource){
			logger.info("(0 indexed) coordinates of the source that can’t be connected");
		}
	}

	private static void bfs(Tile tile, Grid grid, TileType type, ArrayList<Tile> checkSourceOrSink, ArrayList<Tile> readTile){
		ArrayList<Tile> tileList = new ArrayList<Tile>();
		tileList.add(tile);
		int numberOfLine = grid.getLines().size();

		while(tileList.size() != 0){

			Tile temp = tileList.remove(0);
			int x = temp.getX();
			int y = temp.getY();

			Line line = grid.getLines().get(y);
			int numOfTiles = line.getTiles().size();

			if (readTile.contains(temp)){
				continue;
			}
			readTile.add(temp);

			if (temp.getType() == type){
				if (!checkSourceOrSink.contains(temp)){
					checkSourceOrSink.add(temp);
				} 
			}

			if (x-1 >= 0 && y >= 0 && y < numberOfLine){
				if (temp.hasConnection(ConnectionDirection.LEFT) && getTileForDfs(grid, x-1, y).hasConnection(ConnectionDirection.RIGHT)){
					tileList.add(getTileForDfs(grid, x-1, y));
				}
			}

			if (x+1 < numOfTiles && y >= 0 && y < numberOfLine){
				if (temp.hasConnection(ConnectionDirection.RIGHT) && getTileForDfs(grid, x+1, y).hasConnection(ConnectionDirection.LEFT)){
					tileList.add(getTileForDfs(grid, x+1, y));
				}
			}

			if (y-1 >= 0){
				numOfTiles = grid.getLines().get(y-1).getTiles().size();
			 	if(x < numOfTiles && x >= 0){
					if (temp.hasConnection(ConnectionDirection.TOP) && getTileForDfs(grid, x, y-1).hasConnection(ConnectionDirection.BOTTOM)){
						tileList.add(getTileForDfs(grid, x, y-1));
					}
				}
			}

			if (y+1 < numberOfLine){

				numOfTiles = grid.getLines().get(y+1).getTiles().size();
			 	if(x < numOfTiles && x >= 0){
					if (temp.hasConnection(ConnectionDirection.BOTTOM) && getTileForDfs(grid, x, y+1).hasConnection(ConnectionDirection.TOP)){
						tileList.add(getTileForDfs(grid, x, y+1));
					}
				}
			}

		}

	}


	private static int checkSinkOrSourceNumber(Grid grid, TileType type){
		int count = 0;
		for (int i = 0; i <  grid.getLines().size(); i++){
			Line line = grid.getLines().get(i);
			for (int j = 0; j < line.getTiles().size(); j++){
				Tile tile = line.getTiles().get(j);
				if (tile.getType() == type){
					count++;
				}
			}
		}
		return count;
	}

	private static Tile getTileForDfs(Grid grid, int x, int y){
		Line lineTemp = grid.getLines().get(y);
		Tile tileTemp = lineTemp.getTiles().get(x);
		return tileTemp;
	}
	
	/**
	 * Process the given file.
	 * 
	 * @param path 
	 */
	private static void processFile(Path path) {
		logger.info("Processing {}", path);
		
		try {
			byte[] bytes = Files.readAllBytes(path);
			processBytes(bytes, path.getFileName().toString());
		} catch (IOException e) {
			logger.error("Could not read bytes for {}: {}", path, e);
		}
	}
	
	/**
	 * Entry point.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		LoggingConfig.configureLog4j();
		
		// make sure at least the folder exists
		Path dataFolder = Paths.get("data");
		if (! Files.isDirectory(dataFolder)) {
			logger.error("Data folder not found at `{}`", dataFolder);
			return;
		}
		
		// get all the data files
		List<Path> dataFiles;
		try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(dataFolder, "*.dat")) {
			dataFiles = IteratorUtils.toList(fileStream.iterator());
		} catch (IOException e) {
			logger.error("Could not walk directory stream: {}", e);
			return;
		}
		if (dataFiles.isEmpty()) {
			logger.error("No files in data folder to read.");
			return;
		}
		
		// process them
		dataFiles.forEach(Checker::processFile);
		
	}
}
