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
		    
		case DEAD_FOREST:
		    passable = false;
		    break;
		   
		case CORRUPTED_WATER:
		    passable = false;
		    break;
		    
		case CARNIVAL_TENT:
		    passable = false;
		    break;
		    
		case CARNIVAL_BOOTH:
		    passable = false;
		    break;
		    
		case CARNIVAL_GATE:
		    passable = false;
		    break;
		    
		case CARNIVAL_STAGE:
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
		
		else if (type == TileType.DEAD_GRASS) {
		    g.setColor(new Color(85, 78, 55));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(55, 48, 35));
		    g.drawLine(x + 8, y + 34, x + 20, y + 20);
		    g.drawLine(x + 25, y + 38, x + 35, y + 18);
		}
		
		else if (type == TileType.DEAD_FOREST) {
		    g.setColor(new Color(45, 42, 38));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(70, 62, 55));
		    g.fillRect(x + 20, y + 12, 8, 28);
		    g.drawLine(x + 24, y + 18, x + 12, y + 8);
		    g.drawLine(x + 24, y + 20, x + 36, y + 10);
		}
		
		else if (type == TileType.CRACKED_ROAD) {
		    g.setColor(new Color(95, 85, 70));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(35, 30, 25));
		    g.drawLine(x + 8, y + 10, x + 20, y + 22);
		    g.drawLine(x + 20, y + 22, x + 14, y + 36);
		    g.drawLine(x + 28, y + 8, x + 34, y + 20);
		}
		
		else if (type == TileType.CORRUPTED_WATER) {
		    g.setColor(new Color(55, 35, 75));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(95, 65, 130));
		    g.drawLine(x + 4, y + 16, x + tileSize - 4, y + 12);
		    g.drawLine(x + 4, y + 30, x + tileSize - 4, y + 34);
		}
		
		else if (type == TileType.CARNIVAL_FLOOR) {
			g.setColor(new Color(115, 70, 155));
		    g.fillRect(x, y, tileSize, tileSize);

		    // Whimsical diagonal pattern
		    g.setColor(new Color(145, 90, 190));
		    g.drawLine(x, y + tileSize - 8, x + tileSize, y + 8);

		    g.setColor(new Color(205, 155, 80));
		    g.drawOval(x + 8, y + 8, 8, 8);

		    g.setColor(new Color(220, 95, 150));
		    g.drawOval(x + 28, y + 26, 7, 7);

		    g.setColor(new Color(75, 35, 105));
		    g.drawRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.CARNIVAL_PATH) {
			g.setColor(new Color(185, 125, 55));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(245, 205, 95));
		    g.drawLine(x + 4, y + 10, x + tileSize - 4, y + 12);
		    g.drawLine(x + 4, y + 28, x + tileSize - 4, y + 32);

		    g.setColor(new Color(120, 55, 35));
		    g.drawRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.CARNIVAL_TENT) {
			g.setColor(new Color(95, 15, 55));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(210, 170, 65));
		    g.fillPolygon(
		        new int[] {x + tileSize / 2, x + 6, x + tileSize - 6},
		        new int[] {y + 4, y + tileSize - 8, y + tileSize - 8},
		        3
		    );

		    g.setColor(new Color(35, 10, 25));
		    g.drawRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.CARNIVAL_BOOTH) {
			g.setColor(new Color(130, 40, 80));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(235, 190, 75));
		    g.fillRect(x + 5, y + 5, tileSize - 10, 8);

		    g.setColor(new Color(220, 65, 85));
		    g.fillRect(x + 8, y + 16, tileSize - 16, tileSize - 22);

		    g.setColor(new Color(35, 10, 25));
		    g.drawRect(x, y, tileSize, tileSize);
		}
		
		else if (type == TileType.CARNIVAL_GATE) {
		    g.setColor(new Color(35, 20, 35));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(230, 190, 80));
		    g.drawRect(x + 6, y + 6, tileSize - 12, tileSize - 12);
		    g.drawLine(x + 6, y + 6, x + tileSize - 6, y + tileSize - 6);
		}
		
		else if (type == TileType.CARNIVAL_STAGE) {
		    g.setColor(new Color(60, 20, 25));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(200, 160, 70));
		    g.drawRect(x + 4, y + 4, tileSize - 8, tileSize - 8);
		}
		
		else if (type == TileType.CARNIVAL_LIGHTS) {
			g.setColor(new Color(45, 20, 70));
		    g.fillRect(x, y, tileSize, tileSize);

		    g.setColor(new Color(250, 220, 95));
		    g.fillOval(x + 16, y + 12, 14, 14);

		    g.setColor(new Color(180, 50, 110));
		    g.fillOval(x + 8, y + 28, 10, 10);

		    g.setColor(new Color(45, 20, 70));
		    g.drawRect(x, y, tileSize, tileSize);
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
