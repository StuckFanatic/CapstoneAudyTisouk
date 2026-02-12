package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;


public class Tile {

	//Player should not be able to pass water or other types of terrain
	TileType type;
	boolean passable;
	
	
	public Tile(TileType type) {
		
		
		this.type = type;
		
		//Water should not be passable
		switch(type) {
		
		case WATER:
			passable = false;
			break;
			
		default:
			passable = true;
					
		}
			
	}

	//Instead of black tiles this will color in tiles as placeholder to what they are
	public void draw(Graphics g, int x, int y, int tileSize) {
		
		
		switch(type) {
		
		case GRASS:
			g.setColor(Color.green);
			break;
		case WATER:
			g.setColor(Color.blue);
			break;
		case HILL:
			g.setColor(Color.gray);
			break;
			
		
		}
		
		g.fillRect(x, y, tileSize, tileSize);
		g.setColor(Color.black);
		g.drawRect(x, y, tileSize, tileSize);
		

	}
	
	public boolean isPassable() {
		
		return passable;
		
	}

	public TileType getType() {
		return type;
	}
	
	
	
}
