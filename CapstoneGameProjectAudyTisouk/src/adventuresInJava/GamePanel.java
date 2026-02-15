package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Color;




public class GamePanel extends JPanel implements Runnable, java.awt.event.KeyListener {

    // Screen settings for the black box
    final int tileSize = 48;
    final int maxScreenCol = 10;
    final int maxScreenRow = 10;

    
    //Screen Width and height
    private int screenWidth = 800;
    private int screenHeight = 600;
    
    //UI Bottom Panel for info?
    private int uiPanelHeight = 120;
    
    //Movement of the player
    private int maxMovement = 4;
    private int movementLeft = 4;
    
    //Turn/DayCounter
    private int day = 1;
    
    //Game Banner: Will add to UI but for now add here
    private int dayBannerTimer = 0;
    private final int DAY_BANNER_DURATION = 120; 
    
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
    
    //This will be the start of the UI panels that will used 
    
    private void drawUI(Graphics g) {
    	
    	int panelY = getHeight() - uiPanelHeight;
    	
    	//panel background color for now use a default color
    	g.setColor(new Color(20,20,20));
    	g.fillRect(0, panelY, getWidth(), uiPanelHeight);
    	
    	
    	//Border Color with main game window
    	g.setColor(Color.DARK_GRAY);
    	g.drawLine(0, panelY, getWidth(), panelY);
    	
    	//text color
    	g.setColor(Color.WHITE);
    	
    	//Information panel will hold information
    	TileType currentTile = worldMap[player.col][player.row].getType();
    	
    	String tileName = "Tile: " + currentTile;
    	String tileDescription = getTileDescription(currentTile);
    	
    	g.drawString(tileName, 20, panelY + 30);
    	g.drawString(tileDescription, 20, panelY + 55);
    	
    	g.drawString("Day: " + day, 500, panelY +30);
    	g.drawString("Movement: " + movementLeft + "/" + maxMovement, 500, panelY + 55);
    	
    	
    }
    
    //This will be the tile descriptions that the UI calls
    private String getTileDescription(TileType type) {
    	
    	if(type == TileType.GRASS) {
    		
    		return "An open field of grass as the eye can see.";
    	}
    	else if(type == TileType.WATER) {
    		return "How are you standing on this tile? Cheater.";
    	}
    	else if(type == TileType.HILL) {
    		return "Rocky mounds of earth";
    	}
    	return "";
    	
    }
    
    //For now we will randomly generate the world with tiles to see it visually
    //The plan will be to generate the world with a text file or hard code it
    //Replace generate world with a hand crafted map
    public void generateWorld() {
    	
    	int[][] mapLayout = {
    			
    			{0,0,0,0,1,1,2,0,2,2},
    			{0,0,0,0,1,1,0,0,0,0},
    			{0,0,0,0,1,1,0,0,2,2},
    			{0,0,0,0,1,1,0,1,1,1},
    			{0,0,0,0,0,0,0,0,0,0},
    			{0,0,0,1,1,1,2,2,2,0},
    			{0,1,1,1,0,0,2,0,0,0},
    			{0,0,0,1,0,0,2,0,1,0},
    			{0,0,0,0,0,0,0,0,1,0},
    			{2,0,0,1,1,0,0,0,0,0},
    	};
    	
    	
    	
    	//replace the old map with the above hand made one. Still stuck on if-then vs switch values
    	for(int col = 0; col < maxScreenCol; col++) {
    		for( int row = 0; row <maxScreenRow; row++) {
    			
    			int tileValue = mapLayout[row][col];
    			
    			
    			if(tileValue == 0) {
    				worldMap[col][row] = new Tile(TileType.GRASS);
    				
    			}else if (tileValue == 1) {
    				worldMap[col][row] = new Tile(TileType.WATER);
    				
    			}else if (tileValue == 2){
    				worldMap[col][row] = new Tile(TileType.HILL);
    				
    			}	
    			
    		}
    		
    		
    	}	
    	
    }
    
    //Explore tiles method- will add more?
    
    private void exploreTile() {
    	
    	TileType tile = worldMap[player.col][player.row].getType();
    	
    	System.out.println("Day "+ day + " Exploring tile: " + tile);
    	
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
    	//Day
    	day++;
    	dayBannerTimer = DAY_BANNER_DURATION;
    	//Movement
    	movementLeft = maxMovement;
    	
    	System.out.println("---- End of Day ----");
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

        double drawInterval = 1000000000 / 60; 
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }


    public void update() {
    	
        //Timer each time an end turn occurs the banner will appear and 
        if(dayBannerTimer > 0) {
        	dayBannerTimer--;
        }
    }

    //This is where the tile lines start
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Grid Tiles lines here
        //Updated with the new TileTypes
        for(int col = 0; col <maxScreenCol; col++) {
        	
        	for( int row = 0; row <maxScreenRow; row++) {
        		
        		int x = col * tileSize;
        		int y = row * tileSize;

        		worldMap[col][row].draw(g, x, y, tileSize);
        		
        		//Will calculate if distance is within players current movement then highlight it
        		int distance = Math.abs(col - player.col) + Math.abs(row - player.row);
        		
        		if (distance <= movementLeft && worldMap[col][row].isPassable()) {
        			g.setColor(new Color(100, 100, 100, 170)); //Darker. Will change later?
        			g.fillRect(x, y, tileSize, tileSize);
        		}
        	}
        	
        	 
        }
       
        
        
        
        //Banner Day Overlay
        if(dayBannerTimer > 0) {
        	
        	Graphics2D g2 = (Graphics2D) g;
        	
        	
        	
        	float progress = 1f - (dayBannerTimer / (float) DAY_BANNER_DURATION);

        	// Smooth curve
        	int alpha = (int)(255 * Math.sin(progress * Math.PI));
        	
        	g2.setColor(new Color(0, 0, 0, alpha / 2));
        	g2.fillRect(0, 0, getWidth(), getHeight());
        	
        	g2.setColor(new Color(255, 255, 255, alpha));
        	g2.setFont(g2.getFont().deriveFont(36f));
        	
        	String text = "Day " + day;
        	
        	int textWidth = g2.getFontMetrics().stringWidth(text);
        	int x = (screenWidth - textWidth) / 2;
        	int y = screenHeight / 2;
        	
        	g2.drawString(text, x, y);
        }
        

        g.setColor(Color.WHITE);
        g.drawString("Day: " + day, 10, 20);
        g.drawString("Movement Left:" + movementLeft, 10, 40);
        
        //draws panel
        drawUI(g);
        
        //draw player
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
    		repaint();
    	    return;
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
                    }
                }
            }
    	repaint();
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {}
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {}
    
}
