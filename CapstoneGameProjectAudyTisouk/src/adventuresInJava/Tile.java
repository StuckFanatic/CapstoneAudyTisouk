package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;


public class Tile {

	//Player should not be able to pass water or other types of terrain
	TileType type;
	boolean passable;
	
	
	public Tile(TileType type) {
		
		
		this.type = type;
		
		//Water should NOT be passable
		switch(type) {
		
		case WATER:
			passable = false;
			break;
			
		default:
			passable = true;
					
		}
			
	}

	//Instead of black tiles this will color in tiles as placeholder to what they are
	//Changing the switch values to current types (grass,hill,water) to improve visual clarity for the future
	public void draw(Graphics g, int x, int y, int tileSize) {
		
		if(type == TileType.GRASS) {
			
			//Base Grass look?
			g.setColor(new Color(34, 139, 34));
			g.fillRect(x, y, tileSize, tileSize);
				

			
		}
		
		else if (type == TileType.WATER) {
			
			//Base water look
			g.setColor(new Color(30, 144, 255));
			g.fillRect(x, y, tileSize, tileSize);
			
			
			
		}
		
		else if (type == TileType.HILL) {
			
			//Base Hill look
			g.setColor(new Color(139, 69, 19));
			g.fillRect(x, y, tileSize, tileSize);
			
		}
		
		else if (type == TileType.TOWN) {
			
			//base Town look
			g.setColor(new Color(150, 75, 0));
			g.fillRect(x, y, tileSize, tileSize);
			
			g.setColor(Color.YELLOW);
			g.fillOval(x + 10, y + 10, tileSize - 20, tileSize - 20);
			
		}
		
		
		
		
		
		g.setColor(new Color(0, 0, 0, 100));
		g.drawRect(x, y, tileSize, tileSize);
		

	}
	
	public boolean isPassable() {
		
		return passable;
		
	}

	public TileType getType() {
		return type;
	}
	
	
	
}
