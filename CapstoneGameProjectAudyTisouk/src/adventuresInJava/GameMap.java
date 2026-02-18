package adventuresInJava;

public class GameMap {
	
	private Tile [][] tiles;
	private String mapName;
	
	
	public GameMap(Tile[][] tiles, String mapName) {
		
		this.tiles = tiles;
		this.mapName = mapName;
		
		
	}
	
	public Tile[][] getTiles() {
		
		return tiles;
		
	}
	
	public String getMapName() {
		
		return mapName;
		
	}
	
	
}
