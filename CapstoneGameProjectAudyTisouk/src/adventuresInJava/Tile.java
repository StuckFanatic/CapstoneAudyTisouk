package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;



public class Tile {

	//Player should not be able to pass water or other types of terrain
	TileType type;
	boolean passable;
	private String scenarioId;
	private String eventId;
	
	
	public Tile(TileType type) {
		
		
		this.type = type;
		this.scenarioId = "";
		this.eventId = "";
		
		//Water should NOT be able to be passed
		switch(type) {
		
		case WATER:
			passable = false;
			break;
			
		case STONE_WALL:
		    passable = false;
		    break;
		    
		case LAVA:
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
		
		else if (type == TileType.FOREST) {
			
			//Base Forest look
			g.setColor(new Color(0, 100, 0));
			g.fillRect(x, y, tileSize, tileSize);
			
		}
		
		else if (type == TileType.SHORE) {
			
			//Base Beach look
			g.setColor(new Color(210, 180, 140));
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
		
		else if (type == TileType.ROAD) {
			
			//Base Road look
			g.setColor(new Color(160, 82, 45));
			g.fillRect(x, y, tileSize, tileSize);
			
		}
		
		else if (type == TileType.EXIT) {
			
			//exit town/shop etc.
			g.setColor(new Color(180, 180, 60));
			g.fillRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.NPC) {
			
			//Non-pc
			g.setColor(new Color(160, 82, 45));
			g.fillRect(x, y, tileSize, tileSize);
			
			g.setColor(Color.WHITE);
		    g.fillOval(x + 12, y + 8, 24, 24);
		}
		
		else if (type == TileType.SHOP) {
		    g.setColor(new Color(184, 134, 11));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(Color.BLACK);
		    g.drawRect(x + 10, y + 10, 28, 20);
		}
		
		else if (type == TileType.ENEMY) {
		    g.setColor(new Color(120, 0, 0));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(Color.WHITE);
		    g.drawRect(x + 10, y + 10, 28, 20);
		}
		
		else if (type == TileType.RUINS_FLOOR) {
		    g.setColor(new Color(95, 95, 100));
		    g.fillRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.STONE_WALL) {
		    g.setColor(new Color(45, 45, 50));
		    g.fillRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.PEDESTAL) {
		    g.setColor(new Color(120, 120, 130));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(230, 230, 255));
		    g.fillOval(x + 12, y + 12, tileSize - 24, tileSize - 24);
		}
		
		else if (type == TileType.EVENT) {
		    g.setColor(new Color(90, 80, 110));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(220, 220, 255));
		    g.drawRect(x + 10, y + 10, tileSize - 20, tileSize - 20);
		    g.drawString("?", x + 20, y + 30);
		}
		
		else if (type == TileType.QUEST_BOARD) {
		    g.setColor(new Color(90, 55, 25));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(230, 210, 150));
		    g.fillRect(x + 10, y + 8, tileSize - 20, tileSize - 16);

		    g.setColor(Color.BLACK);
		    g.drawRect(x + 10, y + 8, tileSize - 20, tileSize - 16);
		}
		
		else if (type == TileType.LAUNDRY) {
		    g.setColor(new Color(230, 230, 255));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(80, 120, 220));
		    g.drawLine(x + 8, y + 16, x + tileSize - 8, y + 16);

		    g.setColor(Color.WHITE);
		    g.fillRect(x + 12, y + 18, 10, 16);
		    g.fillRect(x + 26, y + 18, 12, 16);

		    g.setColor(Color.BLACK);
		    g.drawRect(x + 12, y + 18, 10, 16);
		    g.drawRect(x + 26, y + 18, 12, 16);
		}
		
		else if (type == TileType.FLOWER) {
		    g.setColor(new Color(34, 139, 34));
		    g.fillRect(x, y, tileSize, tileSize);

		    // flower stem
		    g.setColor(new Color(20, 100, 20));
		    g.drawLine(x + tileSize / 2, y + 28, x + tileSize / 2, y + 38);

		    // flower petals
		    g.setColor(new Color(230, 120, 220));
		    g.fillOval(x + 18, y + 14, 12, 12);
		    g.fillOval(x + 24, y + 14, 12, 12);
		    g.fillOval(x + 21, y + 9, 12, 12);

		    // center
		    g.setColor(Color.YELLOW);
		    g.fillOval(x + 24, y + 17, 8, 8);
		}
		
		else if (type == TileType.LAVA) {
		    g.setColor(new Color(190, 50, 20));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(255, 140, 40));
		    g.drawLine(x + 4, y + 12, x + tileSize - 4, y + 18);
		    g.drawLine(x + 4, y + 30, x + tileSize - 4, y + 24);
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
	
	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
	    this.scenarioId = scenarioId;
	}
	
	public String getEventId() {
	    return eventId;
	}

	public void setEventId(String eventId) {
	    this.eventId = eventId;
	}
	
	
	
	
}
