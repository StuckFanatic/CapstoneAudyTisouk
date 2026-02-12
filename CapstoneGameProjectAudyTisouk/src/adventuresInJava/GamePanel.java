package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;




public class GamePanel extends JPanel implements Runnable, java.awt.event.KeyListener {

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
        this.setFocusable(true);
        this.addKeyListener(this);
        
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
    //Start
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Runs 
    @Override
    public void run() {

        while(gameThread != null) {
            update();
            repaint();
        }
    }

    public void update() {
    }

    //This is where the tile lines start
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
    
    
    //keys need to be pressed for movement
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
    	
    	int code = e.getKeyCode();
    	
    	int newCol = player.col;
    	int newRow = player.row;
    	
    	if(code ==java.awt.event.KeyEvent.VK_UP) {
    		newRow--;
    	}
    	
    	if(code ==java.awt.event.KeyEvent.VK_DOWN) {
    		 newRow++;
    	}
    	
    	if(code ==java.awt.event.KeyEvent.VK_LEFT) {
    		newCol--;
    	}
    	
    	if(code ==java.awt.event.KeyEvent.VK_RIGHT) {
    		newCol++;
    	}
    	
    	//checks the boundaries of the screen
    	if(newCol >= 0 && newCol < maxScreenCol &&
    			newRow >= 0 && newRow <maxScreenRow) {
    		
    		//Checks if terrain is passable
    		if(worldMap[newCol][newRow].isPassable()) {
    			player.col = newCol;
    			player.row = newRow;
    		}
    	}
    	
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {}
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {}
    
}
