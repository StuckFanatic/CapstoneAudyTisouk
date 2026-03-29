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
    
    //Town Dialogue manager
    private GameMap dialogueNextMap = null;
    private int dialogueNextCol = -1;
    private int dialogueNextRow = -1;
    
    Player player;
    Thread gameThread;
    
    //Over world Map
    Tile[][] worldMap;
    
    
    //Current Map
    private GameMap currentMap;
    
    //Town Map
    private Tile[][] townMap;
    
    private GameMap overworldGameMap;
    private GameMap townGameMap;
    
    private Tile[][] battleMap;
    private GameMap battleGameMap;
    
    private BattleUnit playerBattleUnit;
    private BattleUnit enemyBattleUnit;
    
    //Current State of Game
    private GameState currentState = GameState.OVERWORLD;
    
    //Temporary
	private enum GameState {
		OVERWORLD,
		TOWN,
		BATTLE,
		DIALOGUE,
		SHOP
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
    	else if (type == TileType.EXIT) {
    		return "Exit town and retrun to world map.";
    	}
    	else if (type == TileType.NPC) {
    		return "A townsperson. Press ENTER to talk.";
    	}
    	else if (type == TileType.SHOP) {
    		return "A Shop. Press ENTER to browse.";
    	}
    	
    	return "";
    	
    }
    
    //For now we will randomly generate the world with tiles to see it visually
    //The plan will be to generate the world with a text file or hard code it
    //Replace generate world with a hand crafted map
    public void generateWorld() {
    	
    	currentMap = new GameMap(worldMap, "Overworld");
    	
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
    			{2,0,4,1,1,0,0,0,0,0},
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
    			else if (tileValue == 4) {
    				worldMap[col][row] = new Tile(TileType.ENEMY);
    				
    				
    			}
    			//Generates the Town method
    			generateTown();
    			
    			//Generates the BattleMap
    			generateBattle();
    			
    			
    		}
    		overworldGameMap = new GameMap(worldMap, "Overworld");
    		townGameMap = new GameMap(townMap, "Town");
    		battleGameMap = new GameMap(battleMap, "Battle");

    		currentMap = overworldGameMap;
    		
    	}	
    	
    }
    
    //Map for Town
    private void generateTown() {
    	
    	townMap = new Tile[10][10];
    	
    	int[][] layout = {

    	        {1,1,1,1,1,1,1,1,1,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,3,4,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,1,1,1,2,2,1,1,1,1}
    	    };
    	
    	for(int col = 0; col < 10; col++) {
    		for( int row = 0; row < 10; row++) {
    			
    			int value = layout[row][col];
    			
    			
    			if(value == 0) {
    				townMap[col][row] = new Tile(TileType.GRASS);
    				
    			}
    			else if (value == 1) {
    				townMap[col][row] = new Tile(TileType.WATER);
    			
    			}
    			else if (value == 2) {
    				townMap[col][row] = new Tile(TileType.EXIT);
    			}
    			else if (value == 3) {
    				townMap[col][row] = new Tile(TileType.NPC);
    			}
    			else if (value == 4) {
    				townMap[col][row] = new Tile(TileType.SHOP);
    			}
    			
    		}
    	}
    }
    
    //BattleMap1?
    
    private void generateBattle() {
    	
    	battleMap = new Tile[10][10];
    	
    	int[][] layout = {
    			
    	{1,1,1,1,1,1,1,1,1,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,2,2,2,2,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,0,0,0,0,0,0,0,0,1},
    	{1,1,1,1,1,1,1,1,1,1}
    	
    	};
    	
    	for (int col = 0; col < 10; col++) {
    		for (int row = 0; row <10; row++) {
    			
     			int value = layout[row][col];
    			
    			if (value == 0) {
    				battleMap[col][row] = new Tile(TileType.GRASS);
    				
    			}
    			
    			else if (value == 1) {
    				battleMap[col][row] = new Tile(TileType.WATER);
    				
    			}
    			
    			else if (value == 2) {
    				battleMap[col][row] = new Tile(TileType.HILL);
    				
    			}
    			
    			
    		}
    	}
    	
    }
    
    
    
    //Explore tiles method- will add more?
    
    private void exploreTile() {
    	
    	TileType tile = currentMap.getTiles()[player.col][player.row].getType();
    	
    	System.out.println("Day "+ day + " Exploring tile: " + tile);
    	
    	if (tile ==TileType.GRASS) {
    		System.out.println("You found nothing but grass");
    	}
    	else if (tile ==TileType.HILL) {
    		System.out.println("You found treasure hidden in the hills!");
    		
    	}
    	if (tile == TileType.TOWN) {

    		startDialogue(new String[] {
    			    "Welcome to the town.",
    			    "We appreciate your stay."
    			}, GameState.TOWN, townGameMap, 5, 8);
    	    
    	    return;
    	}
    	
    	else if (tile == TileType.ENEMY) {
    		
    		currentMap = battleGameMap;
    		currentState = GameState.BATTLE;
    		
    		playerBattleUnit = new BattleUnit("Leader", 1, 1, false);
    		enemyBattleUnit = new BattleUnit("Bandit", 6, 6, true);
    		
    		return;
    		
    	}
    	
    	endTurn();
    }
    
    //This make the enter key behave different depending on the GameState
    private void interactWithTile() {
    	
    	TileType tile = currentMap.getTiles()[player.col][player.row].getType();
    	
    	if (currentState == GameState.OVERWORLD) {
    		exploreTile();
    		return;
    	}
    	
    	if (currentState == GameState.TOWN) {
    		interactInTown(tile);
    		return;
    	}

    }
    
    private void interactInTown(TileType tile) {
    	
    	if (tile == TileType.EXIT) {
    		currentMap = overworldGameMap;
    		currentState = GameState.OVERWORLD;
    		
    		//Temporary return location
    		player.col = 2;
    		player.row = 5;
    		
    		return; 
    		
    	}
    	
    	if (tile == TileType.NPC) {
    		startDialogue(new String[] {
    			    "Welcome, traveler.",
    			    "The roads ahead can be dangerous."
    			}, GameState.TOWN);
    		return;
    	}
    	
    	if (tile == TileType.SHOP) {
            currentState = GameState.SHOP;
            return;
        }
    	
    	if (tile == TileType.GRASS) {
    		System.out.println("There is nothing here.");
    	}
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
    	
    	case SHOP:
    		updateShop();
    		break;
    	}
    	
    	//Timer each time an end turn occurs the banner will appear 
    	if(dayBannerTimer > 0) {
    	    dayBannerTimer--;
    	}

    	
    }
    

    
    private void updateOverworld() {
    	
    	
    }
    
    private void updateTown() {
    	
    }
    
    private void updateShop() {

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
    	
    	case SHOP:
    		drawShop(g);
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

            	TileType currentTownTile = currentMap.getTiles()[player.col][player.row].getType();

                g.drawString("Town of ???", 20, panelY + 30);
                g.drawString(getTileDescription(currentTownTile), 20, panelY + 55);

                g.drawString("Gold: 0", 500, panelY + 30);
                g.drawString("Press ENTER to interact", 500, panelY + 55);

                break;
                
                
            case BATTLE:
            	
            	g.drawString("Battlefield", 20, panelY + 30);
                g.drawString("Move your units into position.", 20, panelY + 55);

                g.drawString("Player:" + (playerBattleUnit != null ? playerBattleUnit.getName() : "-"), 500, panelY + 30);
                g.drawString("Enemy:" + (enemyBattleUnit != null ? enemyBattleUnit.getName() : "-"), 500, panelY + 55);
                
                break;


            case DIALOGUE:

                g.drawString("Dialogue", 20, panelY + 30);
                g.drawString("Press ENTER to continue...", 20, panelY + 55);

                break;
                
                
            case SHOP:

                g.drawString("Shop", 20, panelY + 30);
                g.drawString("Browse goods or sell your items!", 20, panelY + 55);

                g.drawString("Gold: 0", 500, panelY + 30);
                g.drawString("ESC to exit shop", 500, panelY + 55);

                break;
                
   	
            	
        }
    	
	}
    
    //Two Start Dialogues, Simple and Full
    private void startDialogue(String[] lines, GameState nextState) {
        startDialogue(lines, nextState, null, -1, -1);
    }
    
    private void startDialogue(String[] lines, GameState nextState, GameMap nextMap, int nextCol, int nextRow) {

        previousState = nextState;
        currentState = GameState.DIALOGUE;

        dialogueNextMap = nextMap;
        dialogueNextCol = nextCol;
        dialogueNextRow = nextRow;

        dialogueManager.startDialogue(lines);
    }
    
 

	private void drawOverworld(Graphics g) {
		drawMap(g);
		drawMovementRange(g);
		drawPlayer(g);
		drawDayBanner(g);
    }
	
	private void drawMap(Graphics g) {
		//Draw Tiles
        for(int col = 0; col <maxScreenCol; col++) {
        	
        	for( int row = 0; row <maxScreenRow; row++) {
        		
        		int x = col * tileSize;
        		int y = row * tileSize;

        		currentMap.getTiles()[col][row].draw(g, x, y, tileSize);
        		
        	}
        }
	}
		
	
	private void drawMovementRange(Graphics g) {
		
		//Draw Tiles
        for(int col = 0; col <maxScreenCol; col++) {
        	
        	for( int row = 0; row <maxScreenRow; row++) {
        		
        		int x = col * tileSize;
        		int y = row * tileSize;
        		
		//Will calculate if distance is within players current movement then highlight it
		int distance = Math.abs(col - player.col) + Math.abs(row - player.row);
		
		if (distance <= movementLeft && currentMap.getTiles()[col][row].isPassable()) {
			g.setColor(new Color(100, 100, 100, 170)); //Darker. Will change later?
			g.fillRect(x, y, tileSize, tileSize);
			
				}
        	}
		}
	}
    
	private void drawDayBanner(Graphics g) {
		
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
		
	}
	
	private void drawPlayer(Graphics g) {
		
		player.draw(g);;
		
	}
	
	
    private void drawTown(Graphics g) {
    	
    	drawMap(g);
        drawPlayer(g);
    }
    
    private void drawShop(Graphics g) {

        g.setColor(new Color(60, 40, 20));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawString("Town Shop", 320, 180);
        g.drawString("Welcome! Nothing is for sale yet.", 250, 240);
        g.drawString("Press ESC to return to town.", 250, 280);
    }
    
    //Battle will use battle specific units not over world logic
    private void drawBattle(Graphics g) {
    	
    	drawMap(g);
		drawBattleMovementRange(g);
		
		if (playerBattleUnit != null) {
			playerBattleUnit.draw(g, tileSize);
		}
		
		if (enemyBattleUnit != null) {
			enemyBattleUnit.draw(g, tileSize);
		}
    	
    }
    
    //Unique battle Movement highlights for battles
    private void drawBattleMovementRange(Graphics g) {
    	
    	if (playerBattleUnit == null) return;
    	
    	int movementRange = 4;
    	
    	for (int col = 0; col < maxScreenCol; col++) {
    		for (int row = 0; row < maxScreenRow; row++) {
    			
    			int x = col * tileSize;
    			int y = row * tileSize;
    			
    			int distance = Math.abs(col - playerBattleUnit.getCol()) + Math.abs(row - playerBattleUnit.getRow());
    			
    			if (distance <= movementRange && currentMap.getTiles()[col][row].isPassable()) {
    				g.setColor(new Color(100, 100, 100, 170));
    				g.fillRect(x, y, tileSize, tileSize);
    				
    			}
    			
    		}
    	}
    	
    	
    }
    
    private void drawDialogue(Graphics g) {
    	
    	drawMap(g);
        drawPlayer(g);
    }
    
    //Allows freedom of movement
    private boolean canMove() {

        switch (currentState) {

            case OVERWORLD:
                return movementLeft > 0;

            case TOWN:
                return true;
                
            //battle will be based on turns and unit type later
            case BATTLE:
            	return true;

            default:
                return false;
        }
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

                    if (dialogueNextMap != null) {
                        currentMap = dialogueNextMap;
                        player.col = dialogueNextCol;
                        player.row = dialogueNextRow;
                    }

                    dialogueNextMap = null;
                    dialogueNextCol = -1;
                    dialogueNextRow = -1;
                }
            }

            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            if (currentState == GameState.OVERWORLD || currentState == GameState.TOWN) {
                interactWithTile();
                repaint();
            }
            return;
        }
        
        if (code == KeyEvent.VK_ESCAPE) {

            if (currentState == GameState.SHOP) {
                currentState = GameState.TOWN;
                repaint();
                return;
            }
            
            if (currentState == GameState.BATTLE) {
            	currentMap = overworldGameMap;
            	currentState = GameState.OVERWORLD;
            	
            	player.col = 3;
            	player.row = 1;
            	
            	repaint();
            	return;
            }
            	
        }
        
        if (currentState == GameState.BATTLE && canMove()) {
        	
        	int newCol = playerBattleUnit.getCol();
        	int newRow = playerBattleUnit.getRow();
        	
        	if (code == KeyEvent.VK_UP) newRow--;
            if (code == KeyEvent.VK_DOWN) newRow++;
            if (code == KeyEvent.VK_LEFT) newCol--;
            if (code == KeyEvent.VK_RIGHT) newCol++;

            if (newCol >= 0 && newCol < maxScreenCol &&
                newRow >= 0 && newRow < maxScreenRow &&
                currentMap.getTiles()[newCol][newRow].isPassable()) {
            	
            	
            	//prevent from stepping into occupied tiles from enemies
            	if (!(enemyBattleUnit != null &&
            			enemyBattleUnit.getCol() == newCol &&
            			enemyBattleUnit.getRow() == newRow)) {
            		
            		playerBattleUnit.setPosition(newCol, newRow);
            	}
            	
            	
            }
        	
        	repaint();
        	return;
        	
        	
        }
        

        if ((currentState == GameState.OVERWORLD 
        		|| currentState == GameState.TOWN
        		|| currentState == GameState.BATTLE)
                && canMove()) {

            int newCol = player.col;
            int newRow = player.row;

            if (code == KeyEvent.VK_UP) newRow--;
            if (code == KeyEvent.VK_DOWN) newRow++;
            if (code == KeyEvent.VK_LEFT) newCol--;
            if (code == KeyEvent.VK_RIGHT) newCol++;

            if (newCol >= 0 && newCol < maxScreenCol &&
                newRow >= 0 && newRow < maxScreenRow &&
                currentMap.getTiles()[newCol][newRow].isPassable()) {

                player.col = newCol;
                player.row = newRow;
                
            }
            if (currentState == GameState.OVERWORLD) {
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
