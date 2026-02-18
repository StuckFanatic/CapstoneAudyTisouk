package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Font;




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
    
    //Adds in Dialogue Manager to the game Panel Class
    private DialogueManager dialogueManager = new DialogueManager();
    private GameState previousState;
    private GameState nextState;
    
    Player player;
    Thread gameThread;
    
    //Over world Map
    Tile[][] worldMap;
    
    //Current Map
    private Tile[][] currentMap;
    
    //Town Map
    private Tile[][] townMap;
    
    //Current State of Game
    private GameState currentState = GameState.OVERWORLD;
    
    //Temp Home for a enum
	private enum GameState {
		OVERWORLD,
		TOWN,
		BATTLE,
		DIALOGUE
	}
    
    

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
    

    
    //This will be the tile descriptions that the UI calls
    private String getTileDescription(TileType type) {
    	
    	if(type == TileType.GRASS) {
    		//Grass
    		return "An open field of grass as the eye can see.";
    	}
    	//Water
    	else if(type == TileType.WATER) {
    		return "How are you standing on this tile? Cheater.";
    	}
    	//Hills
    	else if(type == TileType.HILL) {
    		return "Rocky mounds of earth";
    	}
    	//Town
    	else if (type == TileType.TOWN) {
    		return "A peaceful and lively town with open gates.";
    	}
    	
    	return "";
    	
    }
    
    //For now we will randomly generate the world with tiles to see it visually
    //The plan will be to generate the world with a text file or hard code it
    //Replace generate world with a hand crafted map
    public void generateWorld() {
    	
    	currentMap = worldMap;
    	
    	int[][] mapLayout = {
    			
    			{0,0,0,0,1,1,2,0,2,2},
    			{0,0,0,0,1,1,0,0,0,0},
    			{0,0,0,0,1,1,0,0,2,2},
    			{0,0,0,0,1,1,0,1,1,1},
    			{0,0,0,0,0,0,0,0,0,0},
    			{0,0,3,1,1,1,2,2,2,0},
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
    				
    			}
    			else if (tileValue == 1) {
    				worldMap[col][row] = new Tile(TileType.WATER);
    				
    			}
    			else if (tileValue == 2){
    				worldMap[col][row] = new Tile(TileType.HILL);
    				
    			}
    			else if (tileValue == 3) {
    				worldMap[col][row] = new Tile(TileType.TOWN);
    				
    				
    			}
    				
    					
    			
    			
    		}
    		
    		
    	}	
    	
    }
    
    //Explore tiles method- will add more?
    
    private void exploreTile() {
    	
    	TileType tile = currentMap[player.col][player.row].getType();
    	
    	System.out.println("Day "+ day + " Exploring tile: " + tile);
    	
    	if (tile ==TileType.GRASS) {
    		System.out.println("You found nothing but grass");
    	}
    	else if (tile ==TileType.HILL) {
    		System.out.println("You found treasure hidden in the hills!");
    		
    	}
    	else if (tile == TileType.TOWN) {

    	    startDialogue(new String[] {
    	        "Welcome to the town.",
    	        "We appreciate your stay"
    	    }, GameState.TOWN);
    	    
    	    return;
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
    	
    	//Separating the logic by current states the game is in
    	switch(currentState) {
    	
    	case OVERWORLD:
    		updateOverworld();
    		break;
    		
    	case TOWN:
    		updateTown();
    		break;
    	
    	case BATTLE:
    		updateBattle();
    		break;
    	
    	case DIALOGUE:
    		dialogueManager.update();
    		break;
    	}

    	
    }
    
    private void updateOverworld() {
    	
    	//Timer each time an end turn occurs the banner will appear 
        if(dayBannerTimer > 0) {
        	dayBannerTimer--;
        }
    	
    }
    
    private void updateTown() {
    	
    }
    
    private void updateBattle() {
    	
    }
    
    private void updateDialogue() {
    	
    }

    //This is where the tile lines start
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Switch for the different states
        switch(currentState) {
        
        case OVERWORLD:
    		drawOverworld(g);
    		break;
    		
    	case TOWN:
    		drawTown(g);
    		break;
    	
    	case BATTLE:
    		drawBattle(g);
    		break;
    	
    	case DIALOGUE:
    		drawDialogue(g);
    		break;
        
        }
        drawGlobalUI(g);
        
        if(currentState == GameState.DIALOGUE) {
            dialogueManager.draw(g, screenWidth, screenHeight);
        }
        
    }
    
    //Changed the panel to be able to match current state of player
    private void drawGlobalUI(Graphics g) {
		
    	int panelY = getHeight() - uiPanelHeight;

        // Panel background
        g.setColor(new Color(20,20,20));
        g.fillRect(0, panelY, getWidth(), uiPanelHeight);

        // Border line
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, panelY, getWidth(), panelY);

        // Text color
        g.setColor(Color.WHITE);

        switch(currentState) {

            case OVERWORLD:

                TileType currentTile = worldMap[player.col][player.row].getType();

                String tileName = "Tile: " + currentTile;
                String tileDescription = getTileDescription(currentTile);

                g.drawString(tileName, 20, panelY + 30);
                g.drawString(tileDescription, 20, panelY + 55);

                g.drawString("Day: " + day, 500, panelY + 30);
                g.drawString("Movement: " + movementLeft + "/" + maxMovement, 500, panelY + 55);

                break;

            case TOWN:

                g.drawString("Town of ???", 20, panelY + 30);
                g.drawString("Visit shops or talk to NPCs", 20, panelY + 55);

                g.drawString("Gold: 0", 500, panelY + 30);
                g.drawString("Press ESC to leave", 500, panelY + 55);

                break;

            case BATTLE:

                g.drawString("Battle Mode", 20, panelY + 30);
                g.drawString("Select a unit to act", 20, panelY + 55);

                g.drawString("Turn: Player", 500, panelY + 30);
                g.drawString("Units Remaining: ?", 500, panelY + 55);

                break;

            case DIALOGUE:

                g.drawString("Dialogue", 20, panelY + 30);
                g.drawString("Press ENTER to continue...", 20, panelY + 55);

                break;
        }
    	
	}
    
    private void startDialogue(String[] lines, GameState nextState) {
    	
    	previousState = nextState; 
        currentState = GameState.DIALOGUE;

        dialogueManager.startDialogue(lines);
    }
    
    

	private void drawOverworld(Graphics g) {
    	
    	//Draw Tiles
        for(int col = 0; col <maxScreenCol; col++) {
        	
        	for( int row = 0; row <maxScreenRow; row++) {
        		
        		int x = col * tileSize;
        		int y = row * tileSize;

        		currentMap[col][row].draw(g, x, y, tileSize);
        		
        		//Will calculate if distance is within players current movement then highlight it
        		int distance = Math.abs(col - player.col) + Math.abs(row - player.row);
        		
        		if (distance <= movementLeft && currentMap[col][row].isPassable()) {
        			g.setColor(new Color(100, 100, 100, 170)); //Darker. Will change later?
        			g.fillRect(x, y, tileSize, tileSize);
        		}
        	}
        }
       
        //Draw Banner
        if(dayBannerTimer > 0) {
        	
        	Graphics2D g2 = (Graphics2D) g;
        	
        	//Saves the font
        	Font originalFont = g2.getFont();
        	
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
        	
        	g2.setFont(originalFont);
        }
        
        
        g.setColor(Color.WHITE);
        g.drawString("Day: " + day, 10, 20);
        g.drawString("Movement Left:" + movementLeft, 10, 40);
        
        
        //draws panel
        //drawGlobalUI(g);
        //draw player
        player.draw(g);
       
        //g.dispose();
    }
    
    private void drawTown(Graphics g) {
    	
    	g.setColor(Color.DARK_GRAY);
    	g.fillRect(0, 0, getWidth(), getHeight());
    	
    	g.setColor(Color.WHITE);
    	g.drawString("Town Map Placeholder", 300, 300);
    }
    
    private void drawBattle(Graphics g) {
    	
    	g.setColor(Color.RED);
    	g.fillRect(0, 0, getWidth(), getHeight());
    	
    	g.setColor(Color.WHITE);
    	g.drawString("Battle Screen Placeholder", 300, 300);
    	
    }
    
    private void drawDialogue(Graphics g) {
    	
    	drawOverworld(g); //Dialogue is displayed over the over world for now
    }
    
    
    
    //keys need to be pressed for movement
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (currentState == GameState.DIALOGUE) {

            if (code == KeyEvent.VK_ENTER) {
                dialogueManager.nextLine();

                if (!dialogueManager.isActive()) {
                    currentState = previousState;
                }
            }

            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            if (currentState == GameState.OVERWORLD) {
                exploreTile();
                repaint();
            }
            return;
        }

        if (currentState == GameState.OVERWORLD && movementLeft > 0) {

            int newCol = player.col;
            int newRow = player.row;

            if (code == KeyEvent.VK_UP) newRow--;
            if (code == KeyEvent.VK_DOWN) newRow++;
            if (code == KeyEvent.VK_LEFT) newCol--;
            if (code == KeyEvent.VK_RIGHT) newCol++;

            if (newCol >= 0 && newCol < maxScreenCol &&
                newRow >= 0 && newRow < maxScreenRow &&
                currentMap[newCol][newRow].isPassable()) {

                player.col = newCol;
                player.row = newRow;
                movementLeft--;
            }
        }

        repaint();
    }
    
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {}
    
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {}
    
}
