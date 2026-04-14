package adventuresInJava;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;




public class GamePanel extends JPanel implements Runnable, java.awt.event.KeyListener {

    // Screen settings for the black box
    final int tileSize = 48;
    final int maxScreenCol = 10;
    final int maxScreenRow = 10;

    //UI Bottom Panel for info
    private int mapHeight = maxScreenRow * tileSize; //480
    private int mapWidth = maxScreenCol * tileSize;
    
    private int rightPanelWidth = 260;
    private int bottomPanelHeight = 140;
    
    //Screen Width and height
    private int screenWidth = mapWidth + rightPanelWidth;
    private int screenHeight = mapHeight + bottomPanelHeight;
    
    
    
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
    
    //Over world and Town maps
    private GameMap overworldGameMap;
    private GameMap townGameMap;
    
    /*
     * 
     * BATTLE FIELDS
     * 
     */
    
    //Battle Maps
    private Tile[][] battleMap;
    private GameMap battleGameMap;
    
    //Battle UX for Units
    //unit selected
    private BattleUnit playerBattleUnit;
    private BattleUnit allyBattleUnit;
    private List<BattleUnit> enemyUnits = new ArrayList<>();
    private BattleUnit selectedBattleUnit;
    private boolean battleUnitSelected = false;
    
    //Battle Cursor
    private int battleCursorCol = 0;
    private int battleCursorRow = 0;
    
    //Selected unit location
    private int selectedUnitStartCol = -1;
    private int selectedUnitStartRow = -1;
    
    //Battle Menu
    private boolean battleActionMenuOpen = false;
    private String[]battleMenuOptions = {"Attack", "Skill", "Wait"};
    private int battleMenuIndex = 0;
    
    //BattleSkill
    private boolean battleSkillPreviewOpen = false;
    private BattleUnit skillAttacker = null;
    private BattleUnit skillDefender = null;
    private boolean battleSkillTargetSelectOpen = false;
    
    //Combat Log
    private List<String> battleLog = new ArrayList<>();
    
    //Turn Phases
    private String battlePhase = "PLAYER";
    
    //Battle Phase Banner
    private String battlePhaseBannerText = "";
    private int battlePhaseBannerTimer = 0;
    private final int BATTLE_PHASE_BANNER_DURATION = 60;
    
    //Random Rolls
    private Random random = new Random();
    
    //Battle Pause timer to pace the combat
    private int battlePauseTimer = 0;
    
    //Attack Preview
    private boolean battleAttackPreviewOpen = false;
    private BattleUnit previewAttacker = null;
    private BattleUnit previewDefender = null;
    
    //Target preview
    private boolean battleTargetSelectOpen = false;
    private List<BattleUnit> availableTargets = new ArrayList<>();
    private int currentTargetIndex = 0;
    
    //Objective Typing
    //Defeat All
    private ObjectiveType currentObjective = ObjectiveType.DEFEAT_ALL;
    //Survive Wave
    private int surviveTurnTarget = 0;
    private int currentBattleTurn = 1;
    //reach certain tile
    private int objectiveCol = -1;
    private int objectiveRow = -1;
    
    //Zoom combat
    private boolean battleZoomCombatOpen = false;
    
    //Who is attacking 
    private BattleUnit zoomAttacker = null;
    private BattleUnit zoomDefender = null;
    
    //What was used
    private boolean zoomIsSkill = false;
    private String zoomActionName = "";
    
    //Resolved or not
    private boolean zoomAttackResolved = false;
    
    /*
     * GAMESTATES
     */
    
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
    		
    		//#, #, #, #, #, #,
    		//Weapon Name, minimum range, max range, attack bonus, # of  die thrown, # of sides per die, damage bonus, is magic
    		Weapon ironSword = new Weapon("Iron Sword", 1, 1, 3, 1, 6, 2, false); 
    		Weapon shortBow = new Weapon("Short Bow", 2, 2, 2, 1, 6, 1, false);
    		Weapon banditAxe = new Weapon("Bandit Axe", 1, 1, 2, 1, 8, 1, false);
    		
    		//Class Name, Max HP, Armor Class, Movement Range
    		CharacterClass fighterClass = new CharacterClass("Fighter", 12, 12, 4);
    		CharacterClass archerClass = new CharacterClass("Archer", 10, 11, 5);
    		CharacterClass banditClass = new CharacterClass("Bandit", 10, 10, 4);
    		//CharacterClass knightClass = new CharacterClass("Knight", 16, 15, 3);
    		
    		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance, Movement
    		UnitStats leaderStats = new UnitStats(12, 4, 0, 4, 4, 2, 2, 1, 4);
    		UnitStats archerStats = new UnitStats(10, 3, 0, 5, 5, 3, 1, 2, 5);
    		UnitStats banditStats = new UnitStats(10, 4, 0, 3, 3, 1, 1, 0, 4);
    		
    		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance
    		GrowthRates leaderGrowth = new GrowthRates(80, 55, 10, 50, 45, 35, 30, 20);
    		GrowthRates archerGrowth = new GrowthRates(65, 40, 5, 60, 55, 40, 20, 25);
    		GrowthRates banditGrowth = new GrowthRates(70, 50, 0, 30, 36, 15, 25, 10);
    		
    		//Name, Spawn column, Spawn row, Enemy or not, Weapon name, Class name
    		playerBattleUnit = new BattleUnit(
    				"Leader", 1, 1, false, ironSword, fighterClass, leaderStats, leaderGrowth, "Power Strike");
    		
    		allyBattleUnit = new BattleUnit(
    				"Archer Ally", 2, 1, false, shortBow, archerClass, archerStats, archerGrowth, "Precise Shot");
    		
    		//Array for enemy units
    		enemyUnits.clear();
    		enemyUnits.add(new BattleUnit(
    				"Bandit A", 6, 6, true, banditAxe, banditClass, banditStats, banditGrowth,""));
    		
    		enemyUnits.add(new BattleUnit(
    				"Bandit B", 7, 4, true, banditAxe, banditClass, banditStats, banditGrowth,""));
    		
    		selectedBattleUnit = null;
    		battleUnitSelected= false;
    		
    		battleCursorCol = playerBattleUnit.getCol();
    		battleCursorRow = playerBattleUnit.getRow();
    		
    		//Switch if needed
    		//currentObjective = ObjectiveType.DEFEAT_ALL;
    		
    		//currentObjective = ObjectiveType.SURVIVE_TURNS;
    	    surviveTurnTarget = 4;
    	    currentBattleTurn = 1;
    	    
    	    //Reach Tile
    	    currentObjective = ObjectiveType.REACH_TILE;
    	    objectiveCol = 8;
    	    objectiveRow = 2;
    	    currentBattleTurn = 1;

    	    
    		battlePhase = "PLAYER";
    		clearBattleLog();
    		addBattleMessage("Player Phase");
    		
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
    	
    	//Battle Time pacer
    	if (battlePauseTimer > 0) {
    		battlePauseTimer--;
    	}
    	
    	//Battle Phase Banner Timer
    	if (battlePhaseBannerTimer > 0) {
    		battlePhaseBannerTimer--;
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
    	
    	drawRightPanel(g);
        drawBottomPanel(g);
    	
    }
    
    //Wrapper for the text to hit the limit and move on and fix bleeding into borders
    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth, int lineHeight) {
    	
    	java.awt.FontMetrics fm = g.getFontMetrics();
    	String[] words = text.split(" ");
    	String line = "";
    	
    	for (String word : words) {
    		String testLine = line.isEmpty() ? word : line + " " + word;
    		
    		if (fm.stringWidth(testLine) > maxWidth) {
    			g.drawString(line, x, y);
    			y += lineHeight;
    			line = word;
    		} else {
    			line = testLine;
    		}
    	}
    	
    	if (!line.isEmpty()) {
    		g.drawString(line, x, y);
    	}
    	
    }
    
    
    //Detailed Information Selection
    private void drawBottomPanel(Graphics g) {

        int panelX = 0;
        int panelY = mapHeight;
        int panelWidth = screenWidth;
        int panelHeight = bottomPanelHeight;

        // background
        g.setColor(new Color(20, 20, 20));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);

        // border
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, panelY, screenWidth, panelY);

        // vertical split
        g.drawLine(260, panelY, 260, screenHeight);
        g.drawLine(500, panelY, 500, screenHeight);

        g.setColor(Color.WHITE);

        // left section
        switch(currentState) {

            case OVERWORLD:
                g.drawString("Day: " + day, 20, panelY + 25);
                g.drawString("Movement: " + movementLeft + "/" + maxMovement, 20, panelY + 50);
                g.drawString("State: Overworld", 20, panelY + 75);
                break;

            case TOWN:
                g.drawString("State: Town", 20, panelY + 25);
                g.drawString("Press ENTER to interact.", 20, panelY + 50);
                g.drawString("Explore the town.", 20, panelY + 75);
                break;

            case SHOP:
                g.drawString("State: Shop", 20, panelY + 25);
                g.drawString("Browse goods.", 20, panelY + 50);
                g.drawString("ESC to return.", 20, panelY + 75);
                break;

            case DIALOGUE:
                g.drawString("Dialogue", 20, panelY + 25);
                g.drawString("ENTER to continue.", 20, panelY + 50);
                break;

            case BATTLE:
            	
            	//if Preview is open do this first
            	g.drawString("Objective:", 20, panelY + 25);
            	g.drawString(getObjectiveText(), 20, panelY + 45);
            	g.drawString("Turn " + currentBattleTurn, 20, panelY + 65);
            	
            	if (battleZoomCombatOpen) {
            	    g.drawString("Zoom Combat", 20, panelY + 90);

            	    if (!zoomAttackResolved) {
            	        g.drawString("ENTER to resolve attack", 20, panelY + 110);
            	    } else {
            	        g.drawString("ENTER to return to battle", 20, panelY + 110);
            	    }

            	} else if (battleAttackPreviewOpen) {
            	    g.drawString("Attack Forecast", 20, panelY + 90);
            	    g.drawString("ENTER confirm, ESC cancel", 20, panelY + 110);

            	} else if (battleSkillPreviewOpen) {
            	    g.drawString("Skill Preview", 20, panelY + 90);
            	    g.drawString("ENTER confirm, ESC cancel", 20, panelY + 110);

            	} else if (battleTargetSelectOpen) {
            	    g.drawString("Select Attack Target", 20, panelY + 90);
            	    g.drawString("Arrow keys switch targets", 20, panelY + 110);

            	} else if (battleSkillTargetSelectOpen) {
            	    g.drawString("Select Skill Target", 20, panelY + 90);
            	    g.drawString("Arrow keys switch targets", 20, panelY + 110);

            	} else if (!battleUnitSelected || selectedBattleUnit == null) {
            	    g.drawString("Select a unit.", 20, panelY + 90);

            	} else if (battleActionMenuOpen) {
            	    g.drawString("Choose an action.", 20, panelY + 90);

            	} else {
            	    g.drawString("Choose destination.", 20, panelY + 90);
            	}
            	
            	break;
        }

        // center section
        switch(currentState) {

            case OVERWORLD:
            	
            case TOWN:
                TileType currentTile = currentMap.getTiles()[player.col][player.row].getType();
                g.drawString("Tile: " + currentTile, 280, panelY + 25);
                drawWrappedText(g, getTileDescription(currentTile), 280, panelY + 50, 200, 18);
                break;
                
            case SHOP:
            	
            case DIALOGUE:

            case BATTLE:
            	
            	BattleUnit displayUnit = selectedBattleUnit != null ? selectedBattleUnit : playerBattleUnit;
            	
                if (displayUnit != null) {
                    g.drawString("Player: " + displayUnit.getName(), 280, panelY + 25);
                    g.drawString("Class: " + displayUnit.getCharacterClass().getName(), 280, panelY + 45);
                    g.drawString("HP: " + displayUnit.getHp() + "/" + displayUnit.getMaxHp(), 280, panelY + 65);
                    g.drawString("Weapon: " + displayUnit.getWeapon().getName(), 280, panelY + 85);
                    g.drawString("AC: " + displayUnit.getArmorClass(), 280, panelY + 105);
                    g.drawString("LV: " + displayUnit.getLevel() + " EXP: " + displayUnit.getExperience(), 280, panelY + 125);
                    
                }
                break;
        }

        // right section
        switch(currentState) {


            case OVERWORLD:
            	
            case TOWN:
                g.drawString("Prompt", 520, panelY + 25);
                g.drawString("Move with arrows", 520, panelY + 45);
                g.drawString("ENTER to interact", 520, panelY + 65);
                break;
                
            case SHOP:
            	
            case DIALOGUE:
            	
            case BATTLE:
            	
            	BattleUnit displayEnemy = previewDefender;
            	
            	if (displayEnemy == null) {
            		for (BattleUnit enemy : enemyUnits) {
            			if (enemy != null && enemy.isAlive()) {
            				displayEnemy = enemy;
            				break;
            			}
            		}
            	}
            	
                if (displayEnemy != null) {
                    g.drawString("Enemy: " + displayEnemy.getName(), 520, panelY + 25);
                    g.drawString("HP: " + displayEnemy.getHp() + "/" + displayEnemy.getMaxHp(), 520, panelY + 45);
                    g.drawString("AC: " + displayEnemy.getArmorClass(), 520, panelY + 65);
                    g.drawString("Weapon: " + displayEnemy.getWeapon().getName(), 520, panelY + 85);

                } else {
                    g.drawString("Enemy: None", 520, panelY + 25);
                }
                break;
            	
            
        }
    }
    
    //battle logs and other prompts
    private void drawRightPanel(Graphics g) {
    	
    	int panelX = mapWidth;
        int panelY = 0;
        int panelWidth = rightPanelWidth;
        int panelHeight = mapHeight;

        // background
        g.setColor(new Color(20, 20, 20));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);

        // border
        g.setColor(Color.DARK_GRAY);
        g.drawLine(panelX, 0, panelX, mapHeight);

        g.setColor(Color.WHITE);

        switch(currentState) {

            case OVERWORLD:
                g.drawString("Overworld", panelX + 20, 30);
                g.drawString("Move and explore.", panelX + 20, 55);
                break;

            case TOWN:
                g.drawString("Town", panelX + 20, 30);
                g.drawString("Talk, shop, or leave.", panelX + 20, 55);
                break;

            case SHOP:
                g.drawString("Shop", panelX + 20, 30);
                g.drawString("Nothing for sale yet.", panelX + 20, 55);
                g.drawString("ESC to leave.", panelX + 20, 80);
                break;

            case DIALOGUE:
                g.drawString("Dialogue", panelX + 20, 30);
                g.drawString("Press ENTER to continue.", panelX + 20, 55);
                break;

            case BATTLE:
                g.drawString("Battle", panelX + 20, 30);
                g.drawString("Phase: " + battlePhase, panelX + 20, 55);

                int logY = 100;
                g.drawString("Battle Log", panelX + 20, logY);

                int maxVisible = 12;
                int start = Math.max(0, battleLog.size() - maxVisible);

                for (int i = 0; i < Math.min(maxVisible, battleLog.size()); i++) {
                    g.drawString(battleLog.get(start + i), panelX + 20, logY + 25 + (i * 18));
                }
                
                if (battleAttackPreviewOpen && previewAttacker != null && previewDefender != null) {
                    drawAttackPreview(g, panelX, panelY);

                } else if (battleSkillPreviewOpen && skillAttacker != null && skillDefender != null) {
                    drawSkillPreview(g, panelX, panelY);

                } else if (battleTargetSelectOpen && !availableTargets.isEmpty()) {
                    drawTargetSelection(g, panelX, panelY);

                } else if (battleSkillTargetSelectOpen && !availableTargets.isEmpty()) {
                    drawSkillTargetSelection(g, panelX, panelY);

                } else {
                    drawBattleActionMenu(g);
                }
                
                break;
                
        }
    	
    }
    
    //Objective Definer
    private String getObjectiveText() {
    	
    	switch (currentObjective) {
    	
    	case DEFEAT_ALL:
    		return "Defeat all enemies";
    		
    	case SURVIVE_TURNS:
    		return "Survive " + surviveTurnTarget + " turns";
    	
    	case REACH_TILE:
    		return "Reach the objective tile";
    		
    	default:
    		return "Objective unknown";
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
		
		if (allyBattleUnit != null) {
			allyBattleUnit.draw(g, tileSize);
		}
		
		if (currentObjective == ObjectiveType.REACH_TILE) {
			int tileSize = 48;
			
			int x = objectiveCol * tileSize;
			int y = objectiveRow * tileSize;
			
			g.setColor(Color.YELLOW);
			g.drawRect(x, y, tileSize, tileSize);
			g.drawRect( x + 1, y + 1, tileSize - 2, tileSize - 2);
		}
		
		for (BattleUnit enemy : enemyUnits) {
			if (enemy != null && enemy.isAlive()) {
				enemy.draw(g, tileSize);
			}
			
		}
		
		drawBattleCursor(g);
		drawBattlePhaseBanner(g);
		drawTargetHighlight(g);
		drawZoomCombat(g);
    	
    }
    
    
    //Unique battle Movement highlights for battles
    private void drawBattleMovementRange(Graphics g) {
    	
    	if (!battleUnitSelected || selectedBattleUnit == null) return;
    	
    	//uses classes movement range while in battle
    	//Can be changed to selectedBattleUnit.getCharacterClass().getMovementRange(); if switching to class movement based
    	int movementRange = selectedBattleUnit.getStats().getMovement();
    	
    	for (int col = 0; col < maxScreenCol; col++) {
    		for (int row = 0; row < maxScreenRow; row++) {
    			
    			int x = col * tileSize;
    			int y = row * tileSize;
    			
    			int distance = Math.abs(col - selectedUnitStartCol) + Math.abs(row - selectedUnitStartRow);
    			
    			if (distance <= movementRange && currentMap.getTiles()[col][row].isPassable()) {
    				g.setColor(new Color(100, 100, 100, 170));
    				g.fillRect(x, y, tileSize, tileSize);
    			}
    			
    		}
    	}
    	
    }
    
    //Gives you a complex tactics preview of your actions
    private void drawAttackPreview(Graphics g, int panelX, int panelY) {
    	
    	int boxX = panelX + 20;
    	int boxY = mapHeight - 170;
    	int boxWidth = rightPanelWidth - 40;
    	int boxHeight = 140;
    	
    	g.setColor(new Color(30, 30, 30, 230));
    	g.fillRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.setColor(Color.WHITE);
    	g.drawRect(boxX, boxY, boxWidth, boxHeight);
    	
    	Weapon weapon = previewAttacker.getWeapon();
    	
    	int hitChance = calculateHitChance(previewAttacker, previewDefender);
    	int minDamage = calculateMinDamage(previewAttacker, previewDefender);
    	int maxDamage = calculateMaxDamage(previewAttacker, previewDefender);
    	int critChance = calculateCritChance(previewAttacker);
    	boolean counter = canCounterattack(previewAttacker, previewDefender);
    	
    	g.drawString("Attack Preview", boxX + 15, boxY + 20);
    	g.drawString(previewAttacker.getName() + " -> " + previewDefender.getName(), boxX + 15, boxY + 40);
    	g.drawString("Weapon: " + weapon.getName(), boxX + 15, boxY + 60);
    	g.drawString("Hit: " + hitChance + "%", boxX + 15, boxY + 80);
    	g.drawString("Crit: " + critChance + "%", boxX + 15, boxY + 100);
    	g.drawString("Damage: " + minDamage + " - " + maxDamage, boxX + 15, boxY + 120);
    	g.drawString("Counter: " + (counter ? "Yes" : "No"), boxX + 15, boxY + 140);
    }
    
    //Same as Attack preview but for skills
    private void drawSkillPreview(Graphics g, int panelX, int panelY) {
    	
    	int boxX = panelX + 20;
    	int boxY = mapHeight - 170;
    	int boxWidth = rightPanelWidth - 40;
    	int boxHeight = 140;
    	
    	g.setColor(new Color(30, 30, 30, 230));
    	g.fillRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.setColor(Color.WHITE);
    	g.drawRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.drawString("Skill Preview", boxX + 15, boxY + 20);
    	g.drawString(skillAttacker.getName() + " -> " + skillDefender.getName(), boxX + 15, boxY + 40);
    	g.drawString("Skill: " + skillAttacker.getSkillName(), boxX + 15, boxY + 60);
    	
    	if (skillAttacker.getSkillName().equals("Power Strike")) {
    		g.drawString("Effect: +3 damage", boxX + 15, boxY + 80);
    		
    	} else if (skillAttacker.getSkillName().equals("Precise Shot")) {
    		g.drawString("Effect: +2 hit bonus", boxX + 15, boxY + 80);
    	}
    	
    	g.drawString("Enter confirm", boxX + 15, boxY + 100);
    	g.drawString("ESC cancel", boxX + 15, boxY + 120);
    	
    }
    
    
    private void drawBattleActionMenu(Graphics g) {
    	
    	if (!battleActionMenuOpen) return;
    	
    	int menuX = mapWidth + 20;
    	int menuY = mapHeight - 120;
    	int menuWidth = rightPanelWidth - 40;
    	int menuHeight = 80;
    	
    	g.setColor(new Color(30, 30, 30, 220));
    	g.fillRect(menuX, menuY, menuWidth, menuHeight);
    	
    	g.setColor(Color.WHITE);
    	g.drawRect(menuX, menuY, menuWidth, menuHeight);
    	
    	for (int i = 0; i < battleMenuOptions.length; i++) {
    		
    		if (i == battleMenuIndex) {
    			g.setColor(Color.YELLOW);
    			
    		} else {
    			g.setColor(Color.WHITE);
    		}
    		
    		g.drawString(battleMenuOptions[i], menuX + 15, menuY + 25 + (i * 25));
    	}
    	
    }
    
    
    //Cursor in battle is a yellow select box
    private void drawBattleCursor(Graphics g) {
    	
    	if (currentState != GameState.BATTLE) return;
    	
    	g.setColor(Color.YELLOW);
    	g.drawRect(battleCursorCol * tileSize, battleCursorRow * tileSize, tileSize, tileSize);
    	g.drawRect(battleCursorCol * tileSize + 1, battleCursorRow * tileSize + 1, tileSize - 2, tileSize - 2);
    }
    
    //Attack range adjacency check
    private boolean isEnemyInRange(BattleUnit attacker, BattleUnit defender) {
    	
    	if (attacker == null || defender == null || !defender.isAlive()) {
    		
    		return false;
    	}
    	
    	int distance = Math.abs(attacker.getCol() - defender.getCol())
    			+ Math.abs(attacker.getRow() - defender.getRow());
    	
    	Weapon weapon = attacker.getWeapon();
    	
    	return distance >= weapon.getMinRange() && distance <= weapon.getMaxRange();
    	
    }
    
    private boolean isSkillInRange(BattleUnit attacker, BattleUnit defender) {
    	
    	return isEnemyInRange(attacker, defender);
    }
    
    //Target Selection Box
    private void drawTargetSelection(Graphics g, int panelX, int panelY) {
    	
    	int boxX = panelX + 20;
    	int boxY = mapHeight - 160;
    	int boxWidth = rightPanelWidth - 40;
    	int boxHeight = 120;
    	
    	g.setColor(new Color(30, 30, 30, 230));
    	g.fillRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.setColor(Color.WHITE);
    	g.drawRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.drawString("Select Target", boxX + 15, boxY + 20);
    	g.drawString("ENTER confirm", boxX + 15, boxY + 40);
    	g.drawString("ESC cancel", boxX + 15, boxY + 60);
    	
    	BattleUnit target = availableTargets.get(currentTargetIndex);
    	
    	g.drawString("Target: " + target.getName(), boxX + 15, boxY + 85);
    	g.drawString("HP: " + target.getHp() + "/" + target.getMaxHp(), boxX + 15, boxY + 105);
    }
    
    
    //target selection but for skills
    private void drawSkillTargetSelection(Graphics g, int panelX, int panelY) {
    	
    	int boxX = panelX + 20;
    	int boxY = mapHeight - 160;
    	int boxWidth = rightPanelWidth - 40;
    	int boxHeight = 120;
    	
    	g.setColor(new Color(30, 30, 30, 230));
    	g.fillRect(boxX, boxY, boxWidth, boxHeight);
    	
    	g.setColor(Color.WHITE);
    	g.drawRect(boxX, boxY, boxWidth, boxHeight);
    	
    	BattleUnit target = availableTargets.get(currentTargetIndex);
    	
    	g.drawString("Select Skill Target", boxX + 15, boxY + 20);
    	g.drawString("Target: " + target.getName(), boxX + 15, boxY + 45);
    	g.drawString("HP: " + target.getHp() + "/" + target.getMaxHp(), boxX + 15, boxY + 65);
    	g.drawString("ENTER confirm: ", boxX + 15, boxY + 90);
    	g.drawString("ESC cancel: ", boxX + 15, boxY + 110);
    }
    
    
    //highlights the target the users cursor is on
    private void drawTargetHighlight(Graphics g) {
    	
    	if (!battleTargetSelectOpen || availableTargets.isEmpty()) return;
    	
    	BattleUnit target = availableTargets.get(currentTargetIndex);
    	
    	g.setColor(Color.ORANGE);
    	g.drawRect(target.getCol() * tileSize, target.getRow() * tileSize, tileSize, tileSize);
    	g.drawRect(target.getCol() * tileSize + 1, target.getRow() * tileSize + 1, tileSize - 2, tileSize - 2);
    }
    
    //Helper to gather enemies within range of units
    private List<BattleUnit> getEnemiesInRange(BattleUnit attacker) {
    	
    	List<BattleUnit> targets = new ArrayList<>();
    	
    	for (BattleUnit enemy : enemyUnits) {
    		if (enemy != null && enemy.isAlive() && isEnemyInRange(attacker, enemy)) {
    			targets.add(enemy);
    		}
    	}
    	
    	return targets;
    	
    }
    
    //battle phase banner during turn switch
    private void drawBattlePhaseBanner(Graphics g) {

        if (battlePhaseBannerTimer <= 0) return;

        Graphics2D g2 = (Graphics2D) g;
        Font originalFont = g2.getFont();

        int alpha = (int)(255 * (battlePhaseBannerTimer / (float) BATTLE_PHASE_BANNER_DURATION));

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(80, mapHeight / 2 - 40, mapWidth - 160, 80);

        g2.setColor(new Color(255, 255, 255, alpha));
        g2.setFont(g2.getFont().deriveFont(28f));

        int textWidth = g2.getFontMetrics().stringWidth(battlePhaseBannerText);
        int textX = (mapWidth - textWidth) / 2;
        int textY = (mapHeight / 2) + 10;

        g2.drawString(battlePhaseBannerText, textX, textY);
        g2.setFont(originalFont);
    }
    
    //battle banner
    private void showBattlePhaseBanner(String text) {
    	
    	battlePhaseBannerText = text;
    	battlePhaseBannerTimer = BATTLE_PHASE_BANNER_DURATION;
    }

    
    //Battle logs will heap with players seeing actions done on screen
    private void addBattleMessage(String message) {
    	
    	battleLog.add(message);
    	
    	//Keeps only the most recent 5 messages in the log
    	if (battleLog.size() > 5) {
    		battleLog.remove(0);
    	}
    }
    
    //Battle Timer 
    private void startBattlePause(int frames) {
    	
    	battlePauseTimer = frames;
    }
    
    //helper to help clear battle messages
    private void clearBattleLog() {
    	
    	battleLog.clear();
    }
    
    private void checkBattleEnd() {
    	
    	switch (currentObjective) {
    	
    	case DEFEAT_ALL:
    		checkDefeatAllObjective();
    		break;
    		
    	case SURVIVE_TURNS:
    		checkSurviveTurnsObjective();
    		break;
    		
    	case REACH_TILE:
    		checkReachTileObjective();
    		break;
    	
    	}
    	
    	
    }
    
    
    //Checks if the defeat all battle as concluded its objective
    private void checkDefeatAllObjective() {
    	
    	boolean anyEnemyAlive = false;
    	
    	for (BattleUnit enemy : enemyUnits) {
    		if (enemy != null && enemy.isAlive()) {
    			anyEnemyAlive = true;
    			break;
    		}
    	}
    	
    	if (!anyEnemyAlive) {
    		addBattleMessage("Victory! Returning to the overworld...");
    		
    		currentMap =  overworldGameMap;
    		currentState = GameState.OVERWORLD;
    		
    		player.col = 3;
    		player.row = 1;
    	}
    }
    
  //Checks if the Survive turns battle as concluded its objective
    private void checkSurviveTurnsObjective() {
    	
    	if (currentBattleTurn > surviveTurnTarget) {
    		addBattleMessage("You Survived! Returning to the overworld...");
    		
    		currentMap =  overworldGameMap;
    		currentState = GameState.OVERWORLD;
    		
    		player.col = 3;
    		player.row = 1;
    	}
    }
    
  //Checks if there is a player on the tile in the objective
    private void checkReachTileObjective() {
    	
    	if (playerBattleUnit != null &&
    			playerBattleUnit.isAlive() &&
    			playerBattleUnit.getCol() == objectiveCol &&
    			playerBattleUnit.getRow() == objectiveRow) {
    		addBattleMessage("Objective Reached!");
    		
    		currentMap =  overworldGameMap;
    		currentState = GameState.OVERWORLD;
    		
    		player.col = 3;
    		player.row = 1;
    	}
    }
    
    
    
    //starts the player phase after ends
    private void startPlayerPhase() {
        battlePhase = "PLAYER";
        addBattleMessage("Player Phase");
        showBattlePhaseBanner("Player Phase");

        currentBattleTurn++;

        if (playerBattleUnit != null && playerBattleUnit.isAlive()) {
            playerBattleUnit.setHasMoved(false);
            playerBattleUnit.setHasActed(false);
        }

        if (allyBattleUnit != null && allyBattleUnit.isAlive()) {
            allyBattleUnit.setHasMoved(false);
            allyBattleUnit.setHasActed(false);
        }

        checkBattleEnd();
    }
    
    //Check helps end player phase after all acted
    private boolean allPlayerUnitsHaveActed() {
    	
    	if (playerBattleUnit != null && !playerBattleUnit.hasActed()) {
    		return false;
    	}
    	
    	if (allyBattleUnit != null && !allyBattleUnit.hasActed()) {
    		return false;
    	}
    	
    	return true;
    }
    
    //helps end the player phase
    private void endPlayerPhase() {
    	battlePhase = "ENEMY";
    	addBattleMessage("Enemy Phase");
    	showBattlePhaseBanner("Enemy Phase");
    	
    	enemyTurn();
    }
    
    //Stop Leader and allies from stacking on the same tile
    private boolean isTileOccupiedByOtherFriendly(int col, int row, BattleUnit currentUnit) {
    	
    	if (playerBattleUnit != null && playerBattleUnit != currentUnit &&
    			playerBattleUnit.getCol() == col && playerBattleUnit.getRow() == row) {
    		return true;
    	}
    	
    	if (allyBattleUnit != null && allyBattleUnit != currentUnit &&
    			allyBattleUnit.getCol() == col && allyBattleUnit.getRow() == row) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isTileOccupiedByAnyFriendly(int col, int row) {
    	
    	if (playerBattleUnit != null &&
    			playerBattleUnit.isAlive() &&
    			playerBattleUnit.getCol() == col &&
    			playerBattleUnit.getRow() == row) {
    		return true;
    	}
    	
    	if (allyBattleUnit != null &&
    			allyBattleUnit.isAlive() &&
    			allyBattleUnit.getCol() == col &&
    			allyBattleUnit.getRow() == row) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isTileOccupiedByEnemy(int col, int row) {
    	
    	for (BattleUnit enemy : enemyUnits) {
    		if (enemy != null && enemy.isAlive() &&
    				enemy.getCol() == col && enemy.getRow() == row) {
    			return true;
    		}
    	}
    	
    	return false;
    	
    }
    
    //
    private boolean isTileOccupiedByOtherEnemy(int col, int row, BattleUnit currentEnemy) {
    	
    	for (BattleUnit enemy : enemyUnits) {
    		if (enemy != null && enemy != currentEnemy && enemy.isAlive() &&
    				enemy.getCol() == col && enemy.getRow() == row) {
    			return true;
    		}
    	}
    	
    	return false;
    	
    }
    
    
    
    //enemy turn
    private void enemyTurn() {
    	
    	boolean anyPlayerAlive = false;
    	
    	if (playerBattleUnit != null && playerBattleUnit.isAlive()) anyPlayerAlive = true;
    	if (allyBattleUnit != null && allyBattleUnit.isAlive()) anyPlayerAlive = true;
    	
    	
    	if (!anyPlayerAlive) {
    		addBattleMessage("Defeat!");
    		return;
    	}
    	
    	for (BattleUnit enemy : enemyUnits) {
    		
    		if (enemy == null || !enemy.isAlive()) {
    			continue;
    		}
    		
    		BattleUnit target = getEnemyTarget(enemy);
    		
    		if (target == null) {
    			continue;
    			
    		}
    		
    		if (isEnemyInRange(enemy, target)) {
        		performAttack(enemy, target);
        		
        		
        		startBattlePause(45);
        		
        	} else {
        		moveEnemyTowardTarget(enemy, target);
        		startBattlePause(45);
        	}
        	
        	
    	}
    	startPlayerPhase();
	
    }
    
    
    //enemy that cannot yet attack move towards the player
    private void moveEnemyTowardTarget(BattleUnit actingEnemy, BattleUnit target) {

        if (actingEnemy == null || target == null) return;

        int movement = actingEnemy.getCharacterClass().getMovementRange();

        for (int step = 0; step < movement; step++) {

            // stop if already in attack range
            if (isEnemyInRange(actingEnemy, target)) {
                return;
            }

            int enemyCol = actingEnemy.getCol();
            int enemyRow = actingEnemy.getRow();

            int targetCol = target.getCol();
            int targetRow = target.getRow();

            int newCol = enemyCol;
            int newRow = enemyRow;

            // choose direction
            if (Math.abs(targetCol - enemyCol) > Math.abs(targetRow - enemyRow)) {
                if (targetCol > enemyCol) newCol++;
                else if (targetCol < enemyCol) newCol--;
            } else {
                if (targetRow > enemyRow) newRow++;
                else if (targetRow < enemyRow) newRow--;
            }

            // check bounds + can pass + cannot pass friends
            if (newCol >= 0 && newCol < maxScreenCol &&
                newRow >= 0 && newRow < maxScreenRow &&
                currentMap.getTiles()[newCol][newRow].isPassable() &&
                !isTileOccupiedByAnyFriendly(newCol, newRow) &&
            	!isTileOccupiedByOtherEnemy(newCol, newRow, actingEnemy)) {
            	
            	actingEnemy.setPosition(newCol, newRow);
                } else {
                    return;
                }
        }

        addBattleMessage(actingEnemy.getName() + " moved.");
    }
    
    //Helper for enemies to attack other units not just leader
    private BattleUnit getEnemyTarget(BattleUnit actingEnemy) {
    	
    	BattleUnit target = null;
    	int closestDistance = Integer.MAX_VALUE;
    	
    	if (playerBattleUnit != null && playerBattleUnit.isAlive()) {
    		int distance = Math.abs(actingEnemy.getCol() - playerBattleUnit.getCol())
    				+ Math.abs(actingEnemy.getRow() - playerBattleUnit.getRow());
    		target = playerBattleUnit;
    		closestDistance = distance;
    	}
    	
    	if (allyBattleUnit != null && allyBattleUnit.isAlive()) {
    		int distance = Math.abs(actingEnemy.getCol() - allyBattleUnit.getCol())
    				+ Math.abs(actingEnemy.getRow() - allyBattleUnit.getRow());
    		
    		if (distance < closestDistance) {
    			target = allyBattleUnit;
        		closestDistance = distance;
    		}
    		
    	}
    	
    	return target;
    }
    
    //Skill Attacker for chosen skill
    private void performSkill(BattleUnit attacker, BattleUnit defender) {

        String skillName = attacker.getSkillName();

        if (skillName.equals("Power Strike")) {
            performPowerStrike(attacker, defender);
            return;
        }

        if (skillName.equals("Precise Shot")) {
            performPreciseShot(attacker, defender);
            return;
        }

        addBattleMessage(attacker.getName() + " has no usable skill.");
    }
    
    //Stronger Version of a normal strike
    private void performPowerStrike(BattleUnit attacker, BattleUnit defender) {
    	
    	Weapon weapon = attacker.getWeapon();
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2; //Skill effects hit rating
    	int roll = random.nextInt(20) + 1; //1 through 20
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus;
    	
    	addBattleMessage(attacker.getName() + " used Power Strike");
    	
    	if (totalAttack >= defender.getArmorClass()) {
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = attacker.getStats().getStrength();
    		int defenseStat = defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat + 3 - defenseStat;
    		if (damage < 0) damage = 0;
    	
    		defender.takeDamage(damage);
    		
    		addBattleMessage("Power Strike Hit!");
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		
    	} else {
    		addBattleMessage("Power Strike Missed!");
    	}
    }
    
    //More accurate than a regular shot
    private void performPreciseShot(BattleUnit attacker, BattleUnit defender) {
    	
    	Weapon weapon = attacker.getWeapon();
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2;
    	int roll = random.nextInt(20) + 1;
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus + 10;
    	
    	addBattleMessage(attacker.getName() + " used Precise Shot");
    	
    	if (totalAttack >= defender.getArmorClass()) {
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = attacker.getStats().getStrength();
    		int defenseStat = defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat - defenseStat;
    		if (damage < 0) damage = 0;
    	
    		defender.takeDamage(damage);
    		
    		addBattleMessage("Precise Shot Hit!");
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		
    	} else {
    		addBattleMessage("Precise Shot Missed!");
    	}
    }
    
    
    
    //Damage as well as attack rolls
    private boolean performAttack(BattleUnit attacker, BattleUnit defender) {
    	
    	System.out.println("performAttack called");
    	Weapon weapon = attacker.getWeapon();
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2; //Skill effects hit rating
    	int roll = random.nextInt(20) + 1; //1 through 20
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus;
    	
    	addBattleMessage(attacker.getName() + " used " + weapon.getName() + ".");
    	
    	if (totalAttack >= defender.getArmorClass()) {
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = weapon.isMagical() ? attacker.getStats().getMagic() : attacker.getStats().getStrength();
    		int defenseStat = weapon.isMagical() ? defender.getStats().getResistance() : defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat - defenseStat;
    		if (damage < 0) damage = 0;
    		
    		//Critical
    		boolean critical = false;
    		int critRoll = random.nextInt(100) + 1;
    		int critChance = calculateCritChance(attacker);
    		
    		if (critRoll <= critChance) {
    			critical = true;
    			damage *= 2;
    		}
    			
    		//Lucky Break checker
    		if (damage >= defender.getHp()) {
    			if (tryLuckyBreak(defender)) {
    				defender.setHp(1);
    				
    				addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defender.getArmorClass() + " -> HIT!");
    				if (critical) {
    					addBattleMessage("Critical Hit!");
    				}
    				addBattleMessage(defender.getName() + " triggered Lucky Break ");
    				addBattleMessage(defender.getName() + " survived at 1 HP ");
    				
    				return true;
    			}
    		}
    		
    		defender.takeDamage(damage);
    		
    		addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defender.getArmorClass() + " -> HIT!");
    		if (critical) {
				addBattleMessage("Critical Hit!");
			}
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		return true;
    		
    	} else {
    		addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defender.getArmorClass() + " -> MISS!");
    		
    		return false;
    	}
    	
    }
    
    
    //Forecast for players to read on when attacking and defending
    private int calculateHitChance(BattleUnit attacker, BattleUnit defender) {
    	
    	int hitScore = 50
    			+ (attacker.getWeapon().getAttackBonus() * 10)
    			+ ((attacker.getStats().getSkill() / 2) * 10)
				- ((defender.getArmorClass() - 10) * 5);
    	if (hitScore < 5) hitScore = 5;
    	if (hitScore > 95) hitScore = 95;
    	
    	return hitScore;
    }
    
    //Minimum damage
    private int calculateMinDamage(BattleUnit attacker, BattleUnit defender) {
    	
    	Weapon weapon = attacker.getWeapon();
    	
    	int baseMin = attacker.getWeapon().getDamageDiceCount() + attacker.getWeapon().getDamageBonus();
    	int attackStat = weapon.isMagical() ? attacker.getStats().getMagic() : attacker.getStats().getStrength();
    	int defenseStat = weapon.isMagical() ? defender.getStats().getResistance() : defender.getStats().getDefense();
    	
    	int total = baseMin + attackStat - defenseStat;
    	return Math.max(0, total);
    	
    	}
    
    //Maximum Damage
    private int calculateMaxDamage(BattleUnit attacker, BattleUnit defender) {
    	
    	Weapon weapon = attacker.getWeapon();
    	
    	int baseMax = (weapon.getDamageDiceCount() * weapon.getDamageDiceSides() + weapon.getDamageBonus());
    	int attackStat = weapon.isMagical() ? attacker.getStats().getMagic() : attacker.getStats().getStrength();
    	int defenseStat = weapon.isMagical() ? defender.getStats().getResistance() : defender.getStats().getDefense();
    	
    	int total = baseMax + attackStat - defenseStat;
    	return Math.max(0, total);
    	
    	
    }
    
    //Counter Attack gives defenders chance to hit back
    private boolean canCounterattack(BattleUnit attacker, BattleUnit defender) {
    	
    	return isEnemyInRange(defender, attacker);
    	
    }
    
    
    private int rollWeaponDamage(Weapon weapon) {
    	
    	int totalDamage = 0;
    	
    	for (int i = 0; i < weapon.getDamageDiceCount(); i++) {
    		totalDamage += random.nextInt(weapon.getDamageDiceSides()) + 1;
    	}
    	
    	totalDamage += weapon.getDamageBonus();
    	
    	return totalDamage;
    }
    
    //Critical Strike chance 
    private int calculateCritChance(BattleUnit attacker) {
    	
    	int critChance = attacker.getStats().getLuck() / 2;
    	
    	if (critChance < 0) critChance = 0;
    	if (critChance > 50) critChance = 50;
    	
    	return critChance;
    }
    
    //Lucky break: Saves your life
    
   private int calculateLuckyBreakChance(BattleUnit unit) {
	   
	   int critChance = unit.getStats().getLuck() / 4;
   	
   	if (critChance < 0) critChance = 0;
   	if (critChance > 20) critChance = 20;
   	
   	return critChance;
	   
   }
   
   //Lucky break helper
   private boolean tryLuckyBreak(BattleUnit defender) {
	   
	   int chance = calculateLuckyBreakChance(defender);
	   int roll = random.nextInt(100) + 1;
	   
	   return roll <= chance;
   }
   
   
    
    private void levelUpUnit(BattleUnit unit) {
    	
    	addBattleMessage(unit.getName()+ " leveled up!"); 
    		
		GrowthRates growths = unit.getGrowthRates();
		UnitStats stats = unit.getStats();
		
		//HP
		if (random.nextInt(100) < growths.getHp()) {
	        stats.setMaxHp(stats.getMaxHp() + 1);
	        unit.setMaxHp(unit.getMaxHp() + 1);
	        unit.setHp(unit.getHp() + 1);
	        addBattleMessage("HP +1");
	    }
		
		//STR
		if (random.nextInt(100) < growths.getStrength()) {
			stats.setStrength(stats.getStrength() + 1);
			addBattleMessage("STR +1");
		}
		
		// MAG
	    if (random.nextInt(100) < growths.getMagic()) {
	        stats.setMagic(stats.getMagic() + 1);
	        addBattleMessage("MAG +1");
	    }

	    // SKL
	    if (random.nextInt(100) < growths.getSkill()) {
	        stats.setSkill(stats.getSkill() + 1);
	        addBattleMessage("SKL +1");
	    }

	    // SPD
	    if (random.nextInt(100) < growths.getSpeed()) {
	        stats.setSpeed(stats.getSpeed() + 1);
	        addBattleMessage("SPD +1");
	    }

	    // LCK
	    if (random.nextInt(100) < growths.getLuck()) {
	        stats.setLuck(stats.getLuck() + 1);
	        addBattleMessage("LCK +1");
	    }

	    // DEF
	    if (random.nextInt(100) < growths.getDefense()) {
	        stats.setDefense(stats.getDefense() + 1);
	        addBattleMessage("DEF +1");
	    }

	    // RES
	    if (random.nextInt(100) < growths.getResistance()) {
	        stats.setResistance(stats.getResistance() + 1);
	        addBattleMessage("RES +1");
	    }

	    // level up
	    incrementUnitLevel(unit);
	    
	    
	}
		
    
    //Increment Levels
    private void incrementUnitLevel(BattleUnit unit) {
    	
    	unit.levelUp();
    	
    }
    
    //Experience can overflow if needed
    private void checkLevelUp(BattleUnit unit) {

        while (unit.getExperience() >= 100) {
            unit.gainExperience(-100);
            levelUpUnit(unit);
        }
    }
    
    //ZOOM IN COMBAT
    private void openZoomCombat(BattleUnit attacker, BattleUnit defender, boolean isSkill, String actionName) {
    	
    	battleZoomCombatOpen = true;
    	zoomAttacker = attacker;
    	zoomDefender = defender;
    	zoomIsSkill = isSkill;
    	zoomActionName = actionName;
    	zoomAttackResolved = false;
    	
    	
    }
    
    //Draw Zoom in combat
    private void drawZoomCombat(Graphics g) {
    	
    	if (!battleZoomCombatOpen || zoomAttacker == null || zoomDefender == null) return;
    	
    	//back round overlay
    	g.setColor(new Color(0, 0, 0, 220));
    	g.fillRect(0, 0, mapWidth, mapHeight);
    	
    	//left combat panel for attackers
    	g.setColor(new Color(40, 40, 80));
    	g.fillRect(40, 120, 160, 180);
    	
    	//right combat panel for defenders
    	g.setColor(new Color(80, 40, 40));
    	g.fillRect(mapWidth - 200, 120, 160, 180);
    	
    	//center information box
    	g.setColor(new Color(30, 30, 30));
    	g.fillRect(140, 330, 200, 90);
    	
    	g.setColor(Color.WHITE);
    	
    	//Attacker info
    	g.drawString(zoomAttacker.getName(), 60, 150);
    	g.drawString("HP: " + zoomAttacker.getHp() + "/" + zoomAttacker.getMaxHp(), 60, 175);
    	g.drawString("Class: " + zoomAttacker.getCharacterClass().getName(), 60, 200);
    	g.drawString("Weapon: " + zoomAttacker.getWeapon().getName(), 60, 225);
    	
    	//Defender info
    	g.drawString(zoomDefender.getName(), mapWidth - 180, 150);
    	g.drawString("HP: " + zoomDefender.getHp() + "/" + zoomDefender.getMaxHp(), mapWidth - 180, 175);
    	g.drawString("Class: " + zoomDefender.getCharacterClass().getName(), mapWidth - 180, 200);
    	g.drawString("Weapon: " + zoomDefender.getWeapon().getName(), mapWidth - 180, 225);
    	
    	//Action text
    	g.drawString(zoomActionName, 200, 355);
    	
    	if(!zoomAttackResolved) {
    		g.drawString("Enter to resolve", 180, 385);
    	} else {
    		g.drawString("Enter to return", 185, 385);
    	}	
    	
    }
    
    //Dialogue
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
                return;
            }
            
        }
        
        if (code == KeyEvent.VK_ESCAPE) {

            if (currentState == GameState.SHOP) {
                currentState = GameState.TOWN;
                repaint();
                return;
            }
            	
        }
        
        //CURRENT STATE BATTLE
        if (currentState == GameState.BATTLE) {
        	
        	if (battlePhase.equals("Enemy")) {
        		return;
        	}
        	
        	//timer causes delay in attack slamming
        	if (battlePauseTimer > 0) {
        		return;
        	}
        	
        	//Zoom combat will handle much of the combat systems now
        	if (battleZoomCombatOpen) {

        	    if (code == KeyEvent.VK_ENTER) {

        	        if (!zoomAttackResolved) {

        	            if (zoomIsSkill) {
        	                performSkill(zoomAttacker, zoomDefender);
        	                zoomAttacker.setSkillUsed(true);
        	            } else {
        	                performAttack(zoomAttacker, zoomDefender);
        	            }

        	            zoomAttacker.setHasActed(true);

        	            zoomAttacker.gainExperience(10);

        	            if (!zoomDefender.isAlive()) {
        	                zoomAttacker.gainExperience(25);
        	                addBattleMessage(zoomDefender.getName() + " was defeated!");
        	            }

        	            checkLevelUp(zoomAttacker);

        	            zoomAttackResolved = true;
        	            repaint();
        	            return;
        	        }

        	        // second ENTER closes zoom scene and returns to battle map
        	        battleZoomCombatOpen = false;

        	        zoomAttacker = null;
        	        zoomDefender = null;
        	        zoomActionName = "";
        	        zoomIsSkill = false;
        	        zoomAttackResolved = false;

        	        selectedBattleUnit = null;
        	        battleUnitSelected = false;

        	        selectedUnitStartCol = -1;
        	        selectedUnitStartRow = -1;

        	        repaint();
        	        checkBattleEnd();

        	        if (currentState == GameState.BATTLE && allPlayerUnitsHaveActed()) {
        	            endPlayerPhase();
        	        }

        	        return;
        	    }

        	    return;
        	}
        	
        	
        	//Battle Preview Before the actual menu first
        	if (battleAttackPreviewOpen) {
        		
        		if (code == KeyEvent.VK_ESCAPE) {
        			battleAttackPreviewOpen = false;
        			battleActionMenuOpen = true;
        			
        			repaint();
        			return;
        		}
        		
        		if (code == KeyEvent.VK_ENTER) {

        			openZoomCombat(previewAttacker, previewDefender, false, previewAttacker.getWeapon().getName());
        			
        			battleAttackPreviewOpen = false;
        			previewAttacker = null;
        			previewDefender = null;
        			
        			repaint();
        			return;
        			
        		}
        		
        	}
        	
        	
        	//battle skill preview
        	if (battleSkillPreviewOpen) {

        	    if (code == KeyEvent.VK_ESCAPE) {
        	        battleSkillPreviewOpen = false;
        	        battleSkillTargetSelectOpen = true;

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_ENTER) {
        	    	
        			openZoomCombat(skillAttacker, skillDefender, false, skillAttacker.getSkillName());
        			
        			battleSkillPreviewOpen = false;
        			battleSkillTargetSelectOpen = false;
        			
        			skillAttacker = null;
        			skillDefender = null;
        			
        			repaint();
        			return;
        	        
        	    }
        	}
        	
        	
        	if (battleTargetSelectOpen) {
        		
        		if (code == KeyEvent.VK_ESCAPE) {
        			battleTargetSelectOpen = false;
        			battleActionMenuOpen = true;
        			
        			previewAttacker = null;
        			previewDefender = null;
        			
        			repaint();
        			return;
        		}
        		
        		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT) {
        			currentTargetIndex--;
        			if (currentTargetIndex < 0) {
        				currentTargetIndex = availableTargets.size() - 1;        			
        				
        			
        			}
        			
        			previewAttacker = selectedBattleUnit;
        			previewDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        			
        		}
        		
        		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) {
        			currentTargetIndex++;
        			if (currentTargetIndex >= availableTargets.size()) {
        				currentTargetIndex = 0;        			
        				
        			
        			}
        			
        			previewAttacker = selectedBattleUnit;
        			previewDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        			
        		}
        		
        		if (code == KeyEvent.VK_ENTER) {
        			battleTargetSelectOpen = false;
        			battleAttackPreviewOpen = true;
        			
        			previewAttacker = selectedBattleUnit;
        			previewDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        		}	
        			
        		
        	}
        	
        	
        	//Battle Skill before the menu
        	if (battleSkillTargetSelectOpen) {
        		
        		if (code == KeyEvent.VK_ESCAPE) {
        			battleSkillTargetSelectOpen = false;
        			battleActionMenuOpen = true;
        			
        			skillAttacker = null;
        			skillDefender = null;
        			
        			repaint();
        			return;
        		}
        		
        		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT) {
        			currentTargetIndex--;
        			if (currentTargetIndex < 0) {
        				currentTargetIndex = availableTargets.size() - 1;
        			}
        			
        			skillAttacker = selectedBattleUnit;
        			skillDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        		}
        		
        		if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) {
        			currentTargetIndex++;
        			if (currentTargetIndex >= availableTargets.size()) {
        				currentTargetIndex = 0;
        			}
        			
        			skillAttacker = selectedBattleUnit;
        			skillDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        		}
        		
        		if (code == KeyEvent.VK_ENTER) {
        			battleSkillTargetSelectOpen = false;
        			battleSkillPreviewOpen = true;

        			skillAttacker = selectedBattleUnit;
        			skillDefender = availableTargets.get(currentTargetIndex);
        			
        			repaint();
        			return;
        		}
        	}
        	
        	
        	//Battle menu movement
    		if (battleActionMenuOpen) {
    			
    			if (code == KeyEvent.VK_UP) {
    				battleMenuIndex--;
    				if (battleMenuIndex < 0) {
    					battleMenuIndex = battleMenuOptions.length - 1;
    				}
    				repaint();
    				return;
    			}
    			
    			if (code == KeyEvent.VK_DOWN) {
    				battleMenuIndex++;
    				if (battleMenuIndex >= battleMenuOptions.length) {
    					battleMenuIndex = 0;
    				}
    				repaint();
    				return;
    			}
    			
    			if (code == KeyEvent.VK_ESCAPE) {
    				battleActionMenuOpen = false;
    				
    				if (selectedBattleUnit != null) {
    					selectedBattleUnit.setPosition(selectedUnitStartCol, selectedUnitStartRow);
    					selectedBattleUnit.setHasMoved(false);
    					
    					battleCursorCol = selectedUnitStartCol;
    					battleCursorRow = selectedUnitStartRow;
    					
    				}
    				
    				//cursor will remain on the unit if backed 
    				repaint();
    				return;
    				
    			}
    			
    			
    			
    			//Battle Menu
    			if (code == KeyEvent.VK_ENTER) {
    				
    				String selectedOption = battleMenuOptions[battleMenuIndex];
    				System.out.println("Menu option selected: " + selectedOption);
    				
    				if (selectedOption.equals("Wait")) {
    					selectedBattleUnit.setHasActed(true);
    					selectedBattleUnit.setHasMoved(true);
    					
    					battleActionMenuOpen = false;
    					selectedBattleUnit = null;
    					battleUnitSelected = false;
    					
    					selectedUnitStartCol = -1;
    					selectedUnitStartRow = -1;
    					
    					repaint();
    					if (allPlayerUnitsHaveActed()) {
    						endPlayerPhase();
    					}
    					return;
    				}
    				
    				
    				if (selectedOption.equals("Attack")) {
    					
    					if (selectedBattleUnit != null) {
    						
    						availableTargets = getEnemiesInRange(selectedBattleUnit);
    						
    						if (!availableTargets.isEmpty()) {
    							battleTargetSelectOpen = true;
    							battleActionMenuOpen = false;
    							currentTargetIndex = 0;
    							
    							previewAttacker = selectedBattleUnit;
    							previewDefender = availableTargets.get(currentTargetIndex);
    							
    							repaint();
    							return;
    							
    						} else {
    							addBattleMessage("No enemies in range.");
    							repaint();
    							return;
    						}
    						
    					}
    					
    				
    				}
    				
    				
    				if (selectedOption.equals("Skill")) {

    				    if (selectedBattleUnit == null) {
    				        repaint();
    				        return;
    				    }

    				    if (selectedBattleUnit.hasUsedSkill()) {
    				        addBattleMessage(selectedBattleUnit.getSkillName() + " has already been used.");
    				        repaint();
    				        return;
    				    }

    				    availableTargets = getEnemiesInRange(selectedBattleUnit);

    				    if (!availableTargets.isEmpty()) {

    				        // Clear normal attack state
    				        battleAttackPreviewOpen = false;
    				        previewAttacker = null;
    				        previewDefender = null;
    				        battleTargetSelectOpen = false;

    				        battleSkillTargetSelectOpen = true;
    				        battleActionMenuOpen = false;
    				        currentTargetIndex = 0;

    				        skillAttacker = selectedBattleUnit;
    				        skillDefender = availableTargets.get(currentTargetIndex);

    				        repaint();
    				        return;

    				    } else {
    				        addBattleMessage("No enemy in range for skill.");
    				        repaint();
    				        return;
    				    }
    				}
    				
    			}
    			
    		}
        	
        	//Enter selects the unit if none are already selected
        	if (code == KeyEvent.VK_ENTER) {
        		
        		//unit  Selection
        		if (!battleUnitSelected) {
        			
        			//Leader
        			if (playerBattleUnit != null &&
        					battleCursorCol == playerBattleUnit.getCol() &&
        					battleCursorRow == playerBattleUnit.getRow() &&
        					!playerBattleUnit.hasActed()) {
        				
        				
        				selectedBattleUnit = playerBattleUnit;
        				battleUnitSelected = true;
        				
        				selectedUnitStartCol = selectedBattleUnit.getCol();
        				selectedUnitStartRow = selectedBattleUnit.getRow();
        				
        				battleCursorCol = selectedBattleUnit.getCol();
        				battleCursorRow = selectedBattleUnit.getRow();
        				
        				repaint();
        				return;
        			}
        			
        			//Ally Selection
        			if (allyBattleUnit != null &&
        					battleCursorCol == allyBattleUnit.getCol() &&
        					battleCursorRow == allyBattleUnit.getRow() &&
        					!allyBattleUnit.hasActed()) {
        				
        				
        				selectedBattleUnit = allyBattleUnit;
        				battleUnitSelected = true;
        				
        				selectedUnitStartCol = selectedBattleUnit.getCol();
        				selectedUnitStartRow = selectedBattleUnit.getRow();
        				
        				battleCursorCol = selectedBattleUnit.getCol();
        				battleCursorRow = selectedBattleUnit.getRow();
        			
        			
        			repaint();
        			return;
        			
        			}
        		}
        	}
        	
        	//ESC cancels the current unit selected
        	if (code == KeyEvent.VK_ESCAPE) {
        		if (battleUnitSelected) {
        			selectedBattleUnit = null;
        			battleUnitSelected = false;
        			
        			repaint();
        			return;
        			
        		} else {
        			currentMap = overworldGameMap;
        			currentState = GameState.OVERWORLD;
        			
        			player.col = 3; //temporary return spot
        			player.row = 1;
        			
        			repaint();
        			return;
        		}
        	}
        	
        	//Cursor movement before selected unit
        	if (!battleUnitSelected) {
        		
        		int newCursorCol = battleCursorCol;
        		int newCursorRow = battleCursorRow;
        		
        		if (code == KeyEvent.VK_UP) newCursorRow--;
                if (code == KeyEvent.VK_DOWN) newCursorRow++;
                if (code == KeyEvent.VK_LEFT) newCursorCol--;
                if (code == KeyEvent.VK_RIGHT) newCursorCol++;
        		
                if (newCursorCol >= 0 && newCursorCol < maxScreenCol &&
                		newCursorRow >= 0 && newCursorRow < maxScreenRow) {
                	
                	battleCursorCol = newCursorCol;
                	battleCursorRow = newCursorRow;
                }
                
                repaint();
                return;
        		
        	}
        	
        	//move only if a unit was selected
        	if (battleUnitSelected && selectedBattleUnit != null) {
        		
        		//Enter confirms movement
        		if (code == KeyEvent.VK_ENTER) {
        			
        			int distance = Math.abs(battleCursorCol - selectedUnitStartCol)
        					+ Math.abs(battleCursorRow - selectedUnitStartRow);
        			
        			if (distance <= selectedBattleUnit.getCharacterClass().getMovementRange() &&
        					currentMap.getTiles()[battleCursorCol][battleCursorRow].isPassable() &&
        					!isTileOccupiedByEnemy(battleCursorCol, battleCursorRow)) {
        				
        			if (!isTileOccupiedByOtherFriendly(battleCursorCol, battleCursorRow, selectedBattleUnit)) {
        				
        				selectedBattleUnit.setPosition(battleCursorCol, battleCursorRow);
        				selectedBattleUnit.setHasMoved(true);
        				
        				checkBattleEnd();
        				
        				//Open action menu
        				battleActionMenuOpen = true;
        				battleMenuIndex = 0;
        				
        				repaint();
            			
        				
        				}
        			
        			return;
        			}

        		}
        		
        		//ESC cancels battle
        		if (code == KeyEvent.VK_ESCAPE) {
        			
        			selectedBattleUnit = null;
        			battleUnitSelected = false;
        			selectedUnitStartCol = -1;
        			selectedUnitStartRow = -1;
        			
        			battleCursorCol = playerBattleUnit.getCol();
        			battleCursorRow = playerBattleUnit.getRow();
        			
        			repaint();
        			return;
        		}
        		
        		//ArrowKeys move cursor only
        		int newCursorCol = battleCursorCol;
        		int newCursorRow = battleCursorRow;
        		
        		if (code == KeyEvent.VK_UP) newCursorRow--;
                if (code == KeyEvent.VK_DOWN) newCursorRow++;
                if (code == KeyEvent.VK_LEFT) newCursorCol--;
                if (code == KeyEvent.VK_RIGHT) newCursorCol++;
                
                int distance = Math.abs(newCursorCol - selectedUnitStartCol)
    					+ Math.abs(newCursorRow - selectedUnitStartRow);
                
                if (newCursorCol >= 0 && newCursorCol < maxScreenCol &&
                		newCursorRow >= 0 && newCursorRow <maxScreenRow &&
                		distance <= selectedBattleUnit.getCharacterClass().getMovementRange() &&
                		currentMap.getTiles()[newCursorCol][newCursorRow].isPassable() &&
                		!isTileOccupiedByOtherFriendly(newCursorCol, newCursorRow, selectedBattleUnit)) {
                	
                	battleCursorCol = newCursorCol;
                	battleCursorRow = newCursorRow;
                }
        		         
        	}
        	
        	repaint();
        	return;
        	
        }
        

        if ((currentState == GameState.OVERWORLD 
        		|| currentState == GameState.TOWN)
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
