package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.Color;




public class GamePanel extends JPanel implements Runnable, java.awt.event.KeyListener {

    // Screen settings for the black box
    final int tileSize = 48;
    final int maxScreenCol = 10;
    final int maxScreenRow = 10;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    
    //Movement of the player
    private int maxMovement = 4;
    private int movementLeft = 4;
    
    
    Player player;

    Thread gameThread;
    //Over world Map
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
    
    //Explore tiles method- will add more?
    
    private void exploreTile() {
    	
    	TileType tile = worldMap[player.col][player.row].getType();
    	
    	System.out.println("Exploring tile: " + tile);
    	
    	if (tile ==TileType.GRASS) {
    		System.out.println("You found nothing but grass");
    	}
    	else if (tile ==TileType.HILL) {
    		System.out.println("You found treasure hidden in the hills!");
    		
    	}
    	
    	endTurn();
    	
    	
    }
    
    //run out of movement ends turn
    private void endTurn() {
    	movementLeft = maxMovement;
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
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        int newCol = player.col;
        int newRow = player.row;

        // Direction input
        if (code == KeyEvent.VK_UP) {
            newRow--;
        }
        if (code == KeyEvent.VK_DOWN) {
            newRow++;
        }
        if (code == KeyEvent.VK_LEFT) {
            newCol--;
        }
        if (code == KeyEvent.VK_RIGHT) {
            newCol++;
        }
    	
    	if(e.getKeyCode() == KeyEvent.VK_ENTER) {
    		exploreTile();
    	}
    	
    	if (movementLeft > 0) {

            // Check bounds
            if (newCol >= 0 && newCol < maxScreenCol &&
                newRow >= 0 && newRow < maxScreenRow) {

                // Check passable terrain
                if (worldMap[newCol][newRow].isPassable()) {

                    player.col = newCol;
                    player.row = newRow;
                    movementLeft--;

                    // Auto explore if out of movement
                    if (movementLeft == 0) {
                        exploreTile();
                    }
                }
            }
        }
    	
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {}
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {}
    
}
