package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;

public class Player {

	int col;
	int row;
	
	int tileSize;
	
	public Player(int tileSize) {
		
		this.tileSize = tileSize;
		
		//This will be the starting position
		
		col = 0;
		row = 0;
		
		
	}
		
	
	
	public void draw(Graphics g) {
			
		g.setColor(Color.red);
			
		g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
    }
	
	
}
