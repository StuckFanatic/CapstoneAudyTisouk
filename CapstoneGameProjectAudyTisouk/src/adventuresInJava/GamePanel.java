package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;




public class GamePanel extends JPanel implements Runnable {

    // Screen settings for the black box
    final int tileSize = 48;
    final int maxScreenCol = 10;
    final int maxScreenRow = 10;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    
    Player player;

    Thread gameThread;
    
    Tile[][] worldMap;
    

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        
        player = new Player(tileSize);
        
        worldMap = new Tile[maxScreenCol][maxScreenRow];
        generateWorld();
        
       
    }
    
    //For now we will randomly generate the world with tiles to see it visually
    //The plan will be to generate the world with a text file or hard code it
    public void generateWorld() {
    	
    	for(int col = 0; col < maxScreenCol; col++) {
    		for( int row = 0; row <maxScreenRow; row++) {
    			
    			
    			if((col + row) % 7 == 0) {
    				worldMap[col][row] = new Tile(TileType.WATER);
    				
    			}else if((col + row) % 5 == 0) {
    				worldMap[col][row] = new Tile(TileType.HILL);
    				
    			}else {
    				worldMap[col][row] = new Tile(TileType.GRASS);
    				
    			}	
    			
    		}
    		
    		
    	}	
    	
    }
    //Game Logic goes here
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        while(gameThread != null) {
            update();
            repaint();
        }
    }

    public void update() {
    }

    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        
        //Grid Tiles lines here
        //Updated with the new TileTypes
        for(int col = 0; col <maxScreenCol; col++) {
        	
        	for( int row = 0; row <maxScreenRow; row++) {
        		
        		int x = col * tileSize;
        		int y = row * tileSize;

        		worldMap[col][row].draw(g, x, y, tileSize);
        	}
        	
        }
        
        player.draw(g);
        
        g.dispose();
    }
}
