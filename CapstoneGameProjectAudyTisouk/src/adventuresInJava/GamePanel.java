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

//Save & Load
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;




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
    
    //Save State
    private final String SAVE_FILE = "save_data.txt";
    
    /*
     * PARTY
     */
    //Leader level, Leader EXP, Leader points, Archer level, Archer EXP, Archer points
    //This save state allows more to be saved when pressing S
    //unit fields
    private PartyMember leaderMember;
    private PartyMember archerMember;
    private PartyMember mageMember;
    
    private List<PartyMember> partyMembers = new ArrayList<>();
    
    
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
    
    //EXPLORATION
    private Tile[][] ruinsMap;
    private GameMap ruinsGameMap;
    
    //SHOP
    private boolean selectingShopBuyer = true;
    private int shopBuyerIndex = 0;
    private int shopItemIndex = 0;
    private List<ShopItem> shopItems = new ArrayList<>();
    
    //Equipment MENU
    private int equipmentUnitIndex = 0; //which party member is selected
    private int equipmentWeaponIndex = 0;//Which weapon is selected
    private boolean selectingEquipmentUnit = true; //When choosing a character
    private GameState equipmentReturnState = GameState.OVERWORLD;
    
    //STATUS MENU
    private int statusMenuIndex = 0;
    private GameState statusReturnState = GameState.OVERWORLD;
    
    //CAMP MENU
    private String[] campMenuOptions = {"Rest", "Gather", "Bond", "Leave"};
    private int campMenuIndex = 0;
    
    //BONDS
    private boolean campBondMenuOpen = false;
    private int campBondIndex = 0;
    
    private int penelopeBond = 0;
    private int deanBond = 0;

    private int penelopeLastTalkedChapter = -1;
    private int deanLastTalkedChapter = -1;
    

    
    /*
     * NPC
     */
    private List<NPC> townNpcs = new ArrayList<>();
    
    /*
     * CHAPTERS
     */
    
    // Story progression prologue 0
    private int storyChapter = 0;
    private boolean hasCreationSword = false;
    private boolean creationAwakened = false;
    
    
    
    /*
     * QUESTS
     */
    //Tile Completion
    private int encounterSourceCol = -1;
    private int encounterSourceRow = -1;
    
    private int gold = 500; 
    private boolean banditQuestRewardClaimed = false;
    
    //Quest Flags
    private boolean banditQuestAccepted = false;
    private boolean banditQuestCompleted = false;
    
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
    private BattleUnit mageBattleUnit;
    
    private List<BattleUnit> playerBattleUnits = new ArrayList<>();
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
    
    //Ally Target Preview
    private boolean battleHealTargetSelectOpen = false;
    private boolean battleHealPreviewOpen = false;

    //healing targets
    private BattleUnit healCaster = null;
    private BattleUnit healTarget = null;
    
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
    
    //Zoom in combat text pops
    private String zoomFloatingText = "";
    private int zoomFloatingTextX = 0;
    private int zoomFloatingTextY = 0;
    private int zoomFloatingTextTimer = 0;
    private final int ZOOM_FLOATING_TEXT_DURATION = 60;
    
    //A second zoom in text pop for combinations
    private String zoomFloatingText2 = "";
    private int zoomFloatingText2X = 0;
    private int zoomFloatingText2Y = 0;
    private int zoomFloatingText2Timer = 0;
    
    //memory for pop ups
    private boolean lastAttackHit = false;
    private boolean lastAttackCrit = false;
    private boolean lastAttackLuckyBreak = false;
    private int lastAttackDamage = 0;
    
    //Zoom in counterActions
    private boolean zoomCounterPending = false; //can counter but await it
    private boolean zoomCounterResolved = false; //counter already dealt
    private boolean zoomShowingCounter = false; // currently countering
    
    //Battle Scenarios for maps
    private BattleScenario currentBattleScenario;
    
    //Introduction before combat
    private BattleScenario pendingBattleScenario = null;
    
    //This is for post battle dialogue and possibly scenes as well
    private boolean pendingReturnToOverworldAfterDialogue = false;
    
    
    /*
     * GAMESTATES
     */
    
    //Current State of Game
    private GameState currentState = GameState.OVERWORLD;

	private enum GameState {
		OVERWORLD,
		TOWN,
		BATTLE,
		DIALOGUE,
		SHOP,
		EXPLORATION,
		EQUIPMENT,
		STATUS,
		CAMP
		
	}
    
    

    public GamePanel() {    	

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(this);
        
        player = new Player(tileSize);
        
        createPartyMembers();
        createShopInventory();
        
        worldMap = new Tile[maxScreenCol][maxScreenRow];
        generateWorld();
        generateTown();
        generateRuinsMap();
        
       
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
    	//Forest
    	else if(type == TileType.FOREST) {
    		return "A dense group of trees";
    	}
    	//Shore
    	else if(type == TileType.SHORE) {
    		return "A sandy edge between land and water";
    	}
    	//Town
    	else if (type == TileType.TOWN) {
    		return "A peaceful and lively town with open gates.";
    	}
    	//Road
    	else if(type == TileType.ROAD) {
    		return "A worn path most traveled";
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
    	//Cave
    	else if (type == TileType.RUINS_FLOOR) {
    	    return "Ancient stone floor worn smooth by time.";
    	}
    	
    	else if (type == TileType.STONE_WALL) {
    	    return "A ruined wall blocks the way.";
    	}
    	//Prologue Alter
    	else if (type == TileType.PEDESTAL) {
    	    return "An old pedestal. Something important rests here.";
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
    			{2,5,4,1,1,0,0,0,0,0},
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
    				
    				//Enemy Encounter Tile
    				Tile enemyTile = new Tile(TileType.ENEMY);
    				enemyTile.setScenarioId("bandit_field");
    				worldMap[col][row] = enemyTile;
    				
    			}
    			
    			else if (tileValue == 5) {
    				
    				//Quest Tile
    				Tile questEnemyTile = new Tile(TileType.ENEMY);
    				questEnemyTile.setScenarioId("forest_ambush");
    				worldMap[col][row] = questEnemyTile;
    				
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
    		//helper for over world generation after quests
    		updateOverworldQuestTiles();
    		
    	}	
    	
    }
    
    //Map for Town
    private void generateTown() {
    	
    	townMap = new Tile[10][10];
    	
    	int[][] layout = {

    	        {1,1,1,1,1,1,1,1,1,1},
    	        {1,0,0,0,4,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,3,0,3,0,0,0,1},
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
    	
    	createTownNpcs();
    }
    
    //BattleMap1?
    
    private void generateBattle() {
    	
    	battleMap = new Tile[10][10];
    	
    	int[][] layout = {
    			
    	{1,1,1,1,1,1,1,1,1,1},
    	{1,3,3,2,4,0,0,0,0,1},
    	{1,3,2,2,4,0,0,0,2,1},
    	{1,0,0,0,4,0,0,0,3,1},
    	{1,0,0,0,4,4,4,4,4,1},
    	{1,5,0,0,0,0,0,2,3,1},
    	{1,5,0,0,0,0,0,0,0,1},
    	{1,5,0,0,0,0,3,3,3,1},
    	{1,5,0,0,0,0,3,3,3,1},
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
    			
    			else if (value == 3) {
    				battleMap[col][row] = new Tile(TileType.FOREST);
    			}
    			
    			else if (value == 4) {
    				battleMap[col][row] = new Tile(TileType.ROAD);
    			}
    			
    			else if (value == 5) {
    				battleMap[col][row] = new Tile(TileType.SHORE);
    			}
    			
    			
    		}
    	}
    	
    }
    
    //Prologue Ruins Map
    private void generateRuinsMap() {

        ruinsMap = new Tile[10][10];

        int[][] layout = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,0,1,1,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,0,0,2,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,1,1,0,0,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    ruinsMap[col][row] = new Tile(TileType.RUINS_FLOOR);
                }
                else if (value == 1) {
                    ruinsMap[col][row] = new Tile(TileType.STONE_WALL);
                }
                else if (value == 2) {
                    ruinsMap[col][row] = new Tile(TileType.PEDESTAL);
                }
            }
        }

        ruinsGameMap = new GameMap(ruinsMap, "Ancient Ruins");
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

    		startDialogue("Narrator", new String[] {
    			    "Welcome to the town.",
    			    "We appreciate your stay."
    			}, GameState.TOWN, townGameMap, 5, 8);
    	    
    	    return;
    	}
    	
    	else if (tile == TileType.ENEMY) {
    		
    		Tile currentTile = currentMap.getTiles()[player.col][player.row];
    		String scenarioId = currentTile.getScenarioId();
    		
    		if (scenarioId == null || scenarioId.isEmpty()) {
    			scenarioId = "bandit_field";
    		}
    		
    		//Deletion upon completion
    	    encounterSourceCol = player.col;
    	    encounterSourceRow = player.row;
    		
    	    //If the scenario has introduction dialogue play; however if there is none then start immediately
    	    BattleScenario scenario = BattleScenarioLibrary.getScenario(scenarioId);

    	    if (scenario.getIntroDialogue() != null && scenario.getIntroDialogue().length > 0) {
    	        pendingBattleScenario = scenario;
    	        startDialogue(scenario.getIntroDialogue(), GameState.OVERWORLD);
    	        return;
    	    }

    	    loadBattleScenario(scenario);
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
    	
    	if (currentState == GameState.EXPLORATION) {
    	    interactInExploration(tile);
    	    return;
    	}

    }
    
    private void interactInTown(TileType tile) {
    	
    	NPC adjacentNpc = getAdjacentNpc();
    	
    	if (adjacentNpc != null) {
    		interactWithNpc(adjacentNpc);
    		return;
    	}
    	
    	if (tile == TileType.EXIT) {
    		currentMap = overworldGameMap;
    		currentState = GameState.OVERWORLD;
    		
    		if (movementLeft <= 0) {
    		    endTurn();
    		}
    		
    		//Temporary return location
    		player.col = 2;
    		player.row = 5;
    		
    		return; 
    		
    	}
    	
    	if (tile == TileType.SHOP) {
    	    currentState = GameState.SHOP;
    	    selectingShopBuyer = true;
    	    shopBuyerIndex = 0;
    	    shopItemIndex = 0;
    	    return;
    	}
    	
    	if (tile == TileType.GRASS) {
    		System.out.println("There is nothing here.");
    	}
    }
    
    //Campsite
    private void openCamp() {
        currentState = GameState.CAMP;
        campMenuIndex = 0;
    }
    
    //new for exploration tiles
    private void interactInExploration(TileType tile) {

        if (tile == TileType.PEDESTAL) {
            triggerCreationSwordEvent();
            return;
        }

        System.out.println("There is nothing to inspect here.");
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
    		
    	case EXPLORATION:
    	    updateExploration();
    	    break;
    	    
    	case EQUIPMENT:
    	    updateEquipment();
    	    break;
    	    
    	case STATUS:
    	    updateStatus();
    	    break;
    	    
    	case CAMP:
    	    updateCamp();
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
    	
    	//Floating text for battle Y will cause the text to float up
    	if (zoomFloatingTextTimer > 0) {
    		zoomFloatingTextTimer--;
    		zoomFloatingTextY--;
    	}
    	
    	if (zoomFloatingText2Timer > 0) {
    		zoomFloatingText2Timer--;
    		zoomFloatingText2Y--;
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
    
    private void updateExploration() {
    	
    }
    
    private void updateEquipment() {
    	
    }
    
    private void updateStatus() {
    	
    }
    
    private void updateCamp() {
    	
    }
    
    //Allows quests to change the tiles of into a quest marker and back
    private void updateOverworldQuestTiles() {

        if (overworldGameMap == null) return;

        Tile[][] tiles = overworldGameMap.getTiles();

        // Example quest encounter tile location
        int questCol = 7;
        int questRow = 2;

        if (banditQuestAccepted && !banditQuestCompleted) {
            Tile questTile = new Tile(TileType.ENEMY);
            questTile.setScenarioId("forest_ambush");
            tiles[questCol][questRow] = questTile;

        } else {
            tiles[questCol][questRow] = new Tile(TileType.GRASS);
        }
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
    		
    	case EXPLORATION:
    	    drawExploration(g);
    	    break;
    	    
    	case EQUIPMENT:
    	    drawEquipment(g);
    	    break;
    	    
    	case STATUS:
    	    drawStatus(g);
    	    break;
    	    
    	case CAMP:
    	    drawCamp(g);
    	    break;

        }
        
        if (currentState != GameState.STATUS &&
        	    currentState != GameState.EQUIPMENT &&
        	    currentState != GameState.CAMP &&
        	    currentState != GameState.DIALOGUE) {
        	    drawGlobalUI(g);
        	}
        
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
                
            case EXPLORATION:
                g.drawString("State: Exploration", 20, panelY + 25);
                g.drawString("Move freely.", 20, panelY + 50);
                g.drawString("ENTER to inspect.", 20, panelY + 75);
                break;
                
            case EQUIPMENT:
                g.drawString("State: Equipment", 20, panelY + 25);

                if (selectingEquipmentUnit) {
                    g.drawString("Choose a party member.", 20, panelY + 50);
                } else {
                    g.drawString("Choose a weapon.", 20, panelY + 50);
                }

                g.drawString("ENTER confirm, ESC back", 20, panelY + 75);
                break;
                
            case STATUS:
                g.drawString("State: Status", 20, panelY + 25);
                g.drawString("Inspect party members.", 20, panelY + 50);
                g.drawString("UP/DOWN select, ESC close", 20, panelY + 75);
                break;
                
            case CAMP:
            	break;

            case BATTLE:
            	
            	//if Preview is open do this first
            	g.drawString("Objective:", 20, panelY + 25);
            	g.drawString(getObjectiveText(), 20, panelY + 45);
            	g.drawString("Turn " + currentBattleTurn, 20, panelY + 65);
            	
            	if (battleHealPreviewOpen) {
            	    g.drawString("Heal Preview", 20, panelY + 90);
            	    g.drawString("ENTER confirm, ESC cancel", 20, panelY + 110);

            	} else if (battleHealTargetSelectOpen) {
            	    g.drawString("Select Heal Target", 20, panelY + 90);
            	    g.drawString("Arrow keys switch allies", 20, panelY + 110);
            	
            	} else if (battleZoomCombatOpen) {
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
            case EXPLORATION:
                TileType currentTile = currentMap.getTiles()[player.col][player.row].getType();
                g.drawString("Tile: " + currentTile, 280, panelY + 25);
                drawWrappedText(g, getTileDescription(currentTile), 280, panelY + 50, 200, 18);
                break;
                
            case SHOP:
            	
            case DIALOGUE:
            	
            case EQUIPMENT:
            	
            case STATUS:
            	
            case CAMP:
   

            case BATTLE:
            	
            	BattleUnit displayUnit = selectedBattleUnit;
            	//updates display unit in bottom panel
            	if (displayUnit == null && !playerBattleUnits.isEmpty()) {
            	    displayUnit = playerBattleUnits.get(0);
            	}
            	
                if (displayUnit != null) {
                    g.drawString("Player: " + displayUnit.getName(), 280, panelY + 25);
                    g.drawString("HP: " + displayUnit.getHp() + "/" + displayUnit.getMaxHp(), 280, panelY + 45);
                    g.drawString("Weapon: " + displayUnit.getWeapon().getName(), 280, panelY + 65);
                    g.drawString("MP: " + displayUnit.getStats().getCurrentMana() + "/" + displayUnit.getStats().getMaxMana(), 280, panelY + 85);
                    g.drawString("LV: " + displayUnit.getLevel() + " EXP: " + displayUnit.getExperience(), 280, panelY + 105);
                    
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
            	
            case EXPLORATION:
                g.drawString("Prompt", 520, panelY + 25);
                g.drawString("Move with arrows", 520, panelY + 45);
                g.drawString("ENTER to inspect", 520, panelY + 65);
                break;
                
            case EQUIPMENT:
            	
            case STATUS:
            	
            case CAMP:
            
            	
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
                g.drawString(getStoryChapterDisplayName(), panelX + 20, 85);

                drawQuestLog(g, panelX, 140);
                break;

            case TOWN:
                g.drawString("Town", panelX + 20, 30);
                g.drawString("Talk, shop, or leave.", panelX + 20, 55);
                g.drawString("Gold: " + gold, panelX + 20, 85);
                g.drawString(getStoryChapterName(), panelX + 20, 105);
                
                drawQuestLog(g, panelX, 140);
                break;

            case SHOP:
                g.drawString("Shop", panelX + 20, 30);
                g.drawString("Gold: " + gold, panelX + 20, 55);
                g.drawString("ESC to leave.", panelX + 20, 80);
                break;

            case DIALOGUE:
                g.drawString("Dialogue", panelX + 20, 30);
                g.drawString("Press ENTER to continue.", panelX + 20, 55);
                break;
                
            case EXPLORATION:
                g.drawString("Exploration", panelX + 20, 30);
                g.drawString(currentMap.getMapName(), panelX + 20, 55);
                g.drawString("Inspect objects.", panelX + 20, 80);
                break;
                
            case EQUIPMENT:
                g.drawString("Equipment", panelX + 20, 30);

                if (selectingEquipmentUnit) {
                    g.drawString("Choose a unit.", panelX + 20, 55);
                } else {
                    g.drawString("Choose a weapon.", panelX + 20, 55);
                }

                g.drawString("ENTER confirm", panelX + 20, 80);
                g.drawString("ESC back/close", panelX + 20, 105);
                break;
                
            case STATUS:
                g.drawString("Status", panelX + 20, 30);
                g.drawString("View party stats.", panelX + 20, 55);
                g.drawString("ESC close", panelX + 20, 80);
                break;
                
            case CAMP:
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
                    
                } else if (battleHealPreviewOpen && healCaster != null && healTarget != null) {
                    drawHealPreview(g, panelX, panelY);

                } else if (battleTargetSelectOpen && !availableTargets.isEmpty()) {
                    drawTargetSelection(g, panelX, panelY);

                } else if (battleSkillTargetSelectOpen && !availableTargets.isEmpty()) {
                    drawSkillTargetSelection(g, panelX, panelY);
                    
                } else if (battleHealTargetSelectOpen && !availableTargets.isEmpty()) {
                    drawHealTargetSelection(g, panelX, panelY);


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
    		//Prologue
    	    if (currentBattleScenario != null &&
    	        currentBattleScenario.getId().equals("prologue_ruins")) {
    	        return "Reach the pedestal";
    	    }

    	    //Otherwise
    	    return "Reach the objective tile";
    		
    	default:
    		return "Objective unknown";
    	}
    }
    
    //Equipment unit selection screen
    private void openEquipmentMenu() {
        equipmentReturnState = currentState;
        currentState = GameState.EQUIPMENT;

        selectingEquipmentUnit = true;
        equipmentUnitIndex = 0;
        equipmentWeaponIndex = 0;
    }
    
    //returns the currently selected party members
    private PartyMember getSelectedEquipmentMember() {

        if (partyMembers == null || partyMembers.isEmpty()) {
            return null;
        }

        if (equipmentUnitIndex < 0) {
            equipmentUnitIndex = 0;
        }

        if (equipmentUnitIndex >= partyMembers.size()) {
            equipmentUnitIndex = partyMembers.size() - 1;
        }

        return partyMembers.get(equipmentUnitIndex);
    }
    
    //Status Menu just like equipment menu system
    private void openStatusScreen() {
        statusReturnState = currentState;
        currentState = GameState.STATUS;
        statusMenuIndex = 0;
    }
    
    //Campsite
    //REST DIALOGUE TALK CAMP
    // For now only restore max HP and MP.
    private void restParty() {

        for (PartyMember member : partyMembers) {
            UnitStats stats = member.getStats();

            stats.restoreManaToFull();
        }

        System.out.println("The party rested. MP restored.");

        startDialogue(new DialogueLine[] {
            new DialogueLine("Narrator", "The party rests beneath the quiet night sky.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Narrator", "Everyone's mana has been restored.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.CAMP);
    }
    
    //CAMP conversations for ALL
    private void startCampGatherConversation() {

        if (storyChapter == 0) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "I still say this is the best idea that I have ever had.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "That isn't a very long list.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "HEY! It has at least three things on it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Name one that did not get us in trouble.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", ".....", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Look you're both focusing on the wrong part.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "We found ruins. Real ruins. This is how stories begin.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Stories also have warnings at the beginning.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "We'll be diligent. We leave if things get bad.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Reasonable, cautious, and boring. That's why we balance each other out.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "I'm not sure that this is balance.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
            
        }

        if (storyChapter >= 1) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "So. First official camp as wandering adventurers.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "We're calling ourselves adventurers already?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Of course. It sounds better than 'three people who left home and are not sure what they are doing.'", 
                		DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "That might be more accurate for us.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Accuracy is less important than presentation.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Food, medicine, and sleep are also important.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Penelope is right you know.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "She usually is. It's terrible for my lore.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "We can figure this out one day at a time.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "That sounds manageable Art.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "And heroic. Do not forget heroic.", DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
        }
        
        
    }
    
    //Personal Dialogue Starters
    private void startPersonalCampConversation(PartyMember member) {

        if (member.getId().equals("archer_ally")) {
            startDeanCampConversation();
            return;
        }

        if (member.getId().equals("mage")) {
            startPenelopeCampConversation();
            return;
        }

        startDialogue(new DialogueLine[] {
            new DialogueLine(member.getName(), "There is not much to say tonight.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then rest while you can.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.CAMP);
    }
    
  //START OF DEAN CONVO
    private void startDeanCampConversation() {

    	//Last talked to
        if (deanLastTalkedChapter == storyChapter) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "Art! You came back for more heroic wisdom?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Is that what you're calling it?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Of course. I have been practicing my future legendary speeches.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Should I be worried?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Only if you are standing too close when I strike a dramatic pose. Ha HA!", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
        }

        deanLastTalkedChapter = storyChapter;
        deanBond++;

        //Prologue Dean
        if (storyChapter == 0) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "Can you believe we actually found ruins?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Real ruins, Art. The kind heroes discover before something amazing happens.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Or the kind people tell kids not to enter because the floor might collapse.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "That is exactly what makes it exciting.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You and I have very different ideas of exciting.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Come on. You can't tell me you have never wanted something bigger than Cerebella.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Hmm. I have thought about it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "See? That is the beginning of every great story.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I don't know if I want a great story.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Don't worry I'll want enough for the both of us.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
            
        }

        //Chapter 1 Dean
        if (storyChapter >= 1) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "This is it, Art. Actual quests. Actual daaaanger. Actual chances to become known across the lands.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You sound too happy about the danger part.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I'm happy about the hero part. Danger is just the part before people cheer.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "That isn't usually how danger works.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Maybe not for normal people.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "And we are not normal people?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Not anymore. We left home with weapons and a purpose. That is at least 30 Percent heroic already.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I still feel like I am figuring out the purpose part.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "That's fine. You figure out the purpose. I will work on the heroic entrance.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Don't trip during it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "If I do, I'll make it look intentional.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
            
        }
        
    }
    
    //START OF PENELOPE CONVO
    private void startPenelopeCampConversation() {

        if (penelopeLastTalkedChapter == storyChapter) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "You should rest too, Art.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I will.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "You say that, but then you keep checking everyone's supplies.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Someone has to.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Then let someone check on you for once.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I trust you for that.", DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
        }

        penelopeLastTalkedChapter = storyChapter;
        penelopeBond++;

        //Pro
        if (storyChapter == 0) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "Art... do you think we should tell someone where we went?", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Probably.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "That was not very reassuring.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I know. I am still deciding whether Dean would survive being told no.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Hehe. He'd complain loudly.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You're worried about the ruins?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "A little.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Only a little?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Mmmm...More than a little.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "But if you two insist on going forward, then I am going with you.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You don't have to you know.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "I know. That is why I am choosing to.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
            
        }

        //Chapter 1
        if (storyChapter >= 1) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "I checked everyone's bandages. We are running lower than I would like.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You've been keeping track of all that?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Someone has to.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "You sound like me.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Maybe that is why I worry about you.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I'm alright.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "You always say that before you do something reckless for someone else.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Well, don't think helping Dean calms you down.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "It doesn't. But don't forget that you are someone too.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "...I will try to remember that.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Good. Because if you forget, I'll remind you.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
            
        }
    }
    
    private PartyMember getSelectedBondMember() {

        List<PartyMember> bondOptions = getBondOptions();

        if (bondOptions.isEmpty()) {
            return null;
        }

        if (campBondIndex < 0) {
            campBondIndex = 0;
        }

        if (campBondIndex >= bondOptions.size()) {
            campBondIndex = bondOptions.size() - 1;
        }

        return bondOptions.get(campBondIndex);
        
    }
    
    //Everyone except Art from the party list will show up. Cannot bond with self
    private List<PartyMember> getBondOptions() {

        List<PartyMember> options = new ArrayList<>();

        for (PartyMember member : partyMembers) {
            if (member == null) continue;

            // Art should not bond with himself in this menu
            if (member.getId().equals("leader")) continue;

            options.add(member);
        }

        return options;
        
    }
    
    
    
    
    //Three Start Dialogues, Simple and Full
    private void startDialogue(String speakerName, String[] lines, GameState nextState) {
        startDialogue(speakerName, lines, nextState, null, -1, -1);
    }
    
    //Multiple Speakers
    private void startDialogue(DialogueLine[] dialogueLines, GameState nextState) {
        previousState = nextState;
        currentState = GameState.DIALOGUE;
        dialogueManager.startDialogue(dialogueLines);
    }
    
    private void startDialogue(String speakerName, String[] lines, GameState nextState, GameMap nextMap, int nextCol, int nextRow) {

        previousState = nextState;
        currentState = GameState.DIALOGUE;

        dialogueNextMap = nextMap;
        dialogueNextCol = nextCol;
        dialogueNextRow = nextRow;

        dialogueManager.startDialogue(speakerName, lines);
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
    
    //NPC for towns
    private void createTownNpcs() {
    	
    	townNpcs.clear();
    	
    	townNpcs.add(new NPC(
    			"Village Elder",
    			6,
    			4,
    			"bandit_quest",
    			
    			new String[] {
    					"Safe travels, stranger."
    			},
    			
    			new String[] {
    					"Bandits have been spotted in the forest.",
    					"Please drive them away before the attack travelers.",
    					"I've marked their location on your map."   					
    			},
    			
    			new String[] {
    					"The bandits are still out there.",
    					"Please clear them out of the forest." 					
    			},
    			
    			new String[] {
    					"You dealt with the bandits?",
    					"Thank you. The roads will be safer now." 					
    			}		
    		));
    	
    	townNpcs.add(new NPC(
    			"Townsperson",
    			4,
    			4,
    			"",
    			
    			new String[] {
    					"Beautiful weather today.",
    					"Be careful outside the town walls."
    			},
    			null,
    			null,
    			null
    			
    			));
    	
    }
    
    //DRAWS SHOP
    private void drawShop(Graphics g) {

        g.setColor(new Color(45, 30, 20));
        g.fillRect(0, 0, getWidth(), getHeight());

        int menuX = 80;
        int menuY = 70;
        int menuWidth = 560;
        int menuHeight = 360;

        g.setColor(new Color(25, 25, 30));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        g.drawString("Town Weapon Shop", menuX + 20, menuY + 30);
        g.drawString("Gold: " + gold, menuX + 430, menuY + 30);

        if (selectingShopBuyer) {
            drawShopBuyerSelection(g, menuX, menuY, menuWidth, menuHeight);
        } else {
            drawShopItemSelection(g, menuX, menuY, menuWidth, menuHeight);
        }
    }
    
    
    
    private void drawShopItemSelection(Graphics g, int menuX, int menuY, int menuWidth, int menuHeight) {

        PartyMember buyer = getSelectedShopBuyer();

        g.setColor(Color.WHITE);

        if (buyer == null) {
            g.drawString("No buyer selected.", menuX + 20, menuY + 65);
            return;
        }

        g.drawString("Buying For: " + buyer.getName(), menuX + 20, menuY + 65);
        g.drawString("Class: " + buyer.getCharacterClass().getName(), menuX + 220, menuY + 65);

        List<ShopItem> availableItems = getShopItemsForBuyer(buyer);

        if (availableItems.isEmpty()) {
            g.drawString("No weapons available for this class.", menuX + 20, menuY + 100);
            g.drawString("ESC back", menuX + 20, menuY + menuHeight - 25);
            return;
        }

        for (int i = 0; i < availableItems.size(); i++) {
            ShopItem item = availableItems.get(i);
            

            if (i == shopItemIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == shopItemIndex) ? "> " : "  ";

            g.drawString(prefix + item.getDisplayName(), menuX + 40, menuY + 100 + (i * 30));
            g.drawString(item.getPrice() + "g", menuX + 250, menuY + 100 + (i * 30));
        }

        if (!shopItems.isEmpty()) {
            ShopItem selectedItem = availableItems.get(shopItemIndex);
            Weapon selectedWeapon = createWeaponById(selectedItem.getWeaponId());

            if (selectedWeapon != null) {
                int detailY = menuY + 220;

                g.setColor(Color.WHITE);
                g.drawString("Selected: " + selectedWeapon.getName(), menuX + 20, detailY);
                g.drawString("Range: " + selectedWeapon.getMinRange() + "-" + selectedWeapon.getMaxRange(), menuX + 20, detailY + 25);
                g.drawString("Hit Bonus: +" + selectedWeapon.getAttackBonus(), menuX + 20, detailY + 50);
                g.drawString(
                    "Damage: " + selectedWeapon.getDamageDiceCount() + "d" +
                    selectedWeapon.getDamageDiceSides() + " + " + selectedWeapon.getDamageBonus(),
                    menuX + 20,
                    detailY + 75
                );

                String type = selectedWeapon.isMagical() ? "Magic" : "Physical";
                g.drawString("Type: " + type, menuX + 250, detailY + 25);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString("ENTER buy | ESC back", menuX + 20, menuY + menuHeight - 25);
    }
    
    
    private void drawShopBuyerSelection(Graphics g, int menuX, int menuY, int menuWidth, int menuHeight) {

        g.setColor(Color.WHITE);
        g.drawString("Choose Buyer", menuX + 20, menuY + 65);

        for (int i = 0; i < partyMembers.size(); i++) {
            PartyMember member = partyMembers.get(i);

            if (i == shopBuyerIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == shopBuyerIndex) ? "> " : "  ";

            String weaponName = "None";
            if (member.getEquippedWeapon() != null) {
                weaponName = member.getEquippedWeapon().getName();
            }

            g.drawString(prefix + member.getName(), menuX + 40, menuY + 100 + (i * 30));

            g.setColor(Color.LIGHT_GRAY);
            g.drawString("Equipped: " + weaponName, menuX + 220, menuY + 100 + (i * 30));
        }

        g.setColor(Color.WHITE);
        g.drawString("ENTER choose | ESC leave", menuX + 20, menuY + menuHeight - 25);
    }
    
    //SHOP HERE
    private void createShopInventory() {

        shopItems.clear();

        shopItems.add(new ShopItem("steel_sword", "Steel Sword", 80));
        shopItems.add(new ShopItem("long_bow", "Long Bow", 90));
        shopItems.add(new ShopItem("fire_tome_plus", "Fire Tome+", 100));
    }
    
    //Stops Items from being bought by wrong members of the party
    private List<ShopItem> getShopItemsForBuyer(PartyMember buyer) {

        List<ShopItem> filteredItems = new ArrayList<>();

        if (buyer == null) {
            return filteredItems;
        }

        CharacterClass characterClass = buyer.getCharacterClass();

        for (ShopItem item : shopItems) {
            Weapon weapon = createWeaponById(item.getWeaponId());

            if (weapon != null &&
                characterClass.canUseWeaponType(weapon.getWeaponType())) {

                filteredItems.add(item);
            }
        }

        return filteredItems;
    }
    
    private PartyMember getSelectedShopBuyer() {

        if (partyMembers == null || partyMembers.isEmpty()) {
            return null;
        }

        if (shopBuyerIndex < 0) {
            shopBuyerIndex = 0;
        }

        if (shopBuyerIndex >= partyMembers.size()) {
            shopBuyerIndex = partyMembers.size() - 1;
        }

        return partyMembers.get(shopBuyerIndex);
    }
    
    //ONLY sell usable weapons to the selected character
    private void buySelectedShopItem() {

        PartyMember buyer = getSelectedShopBuyer();

        if (buyer == null) {
            return;
        }

        List<ShopItem> availableItems = getShopItemsForBuyer(buyer);

        if (availableItems.isEmpty()) {
            System.out.println("No usable weapons for this class.");
            return;
        }

        if (shopItemIndex < 0 || shopItemIndex >= availableItems.size()) {
            shopItemIndex = 0;
        }

        ShopItem item = availableItems.get(shopItemIndex);

        if (gold < item.getPrice()) {
            System.out.println("Not enough gold.");
            return;
        }

        Weapon weapon = createWeaponById(item.getWeaponId());

        if (weapon == null) {
            System.out.println("Weapon not found: " + item.getWeaponId());
            return;
        }

        // Final safety check
        if (!buyer.getCharacterClass().canUseWeaponType(weapon.getWeaponType())) {
            System.out.println(buyer.getName() + " cannot use " + weapon.getName() + ".");
            return;
        }

        gold -= item.getPrice();
        buyer.addWeapon(weapon);

        System.out.println(buyer.getName() + " bought " + weapon.getName() + " for " + item.getPrice() + " gold.");
    }
    
    
    //Exploration Types
    private void drawExploration(Graphics g) {
        drawMap(g);
        drawPlayer(g);
    }
    
    //Equipment is it own big UI thing 
    private void drawEquipment(Graphics g) {

        // Draw scene behind the menu
        if (equipmentReturnState == GameState.OVERWORLD) {
            drawOverworld(g);
        } else if (equipmentReturnState == GameState.TOWN) {
            drawTown(g);
        } else if (equipmentReturnState == GameState.EXPLORATION) {
            drawExploration(g);
        }

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenWidth, screenHeight);

        int menuX = 120;
        int menuY = 70;
        int menuWidth = 460;
        int menuHeight = 340;

        g.setColor(new Color(25, 25, 30));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        if (selectingEquipmentUnit) {
            drawEquipmentUnitSelection(g, menuX, menuY, menuWidth, menuHeight);
        } else {
            drawEquipmentWeaponSelection(g, menuX, menuY, menuWidth, menuHeight);
        }
    }
    
    //displays all party members and their currently equipped weapons
    private void drawEquipmentUnitSelection(Graphics g, int menuX, int menuY, int menuWidth, int menuHeight) {

        g.setColor(Color.WHITE);
        g.drawString("Choose Unit", menuX + 20, menuY + 30);

        if (partyMembers == null || partyMembers.isEmpty()) {
            g.drawString("No party members.", menuX + 20, menuY + 65);
            return;
        }

        int startY = menuY + 70;

        for (int i = 0; i < partyMembers.size(); i++) {
            PartyMember member = partyMembers.get(i);

            if (i == equipmentUnitIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == equipmentUnitIndex) ? "> " : "  ";

            String weaponName = "None";
            if (member.getEquippedWeapon() != null) {
                weaponName = member.getEquippedWeapon().getName();
            }

            g.drawString(prefix + member.getName(), menuX + 30, startY + (i * 30));

            g.setColor(Color.LIGHT_GRAY);
            g.drawString("Equipped: " + weaponName, menuX + 180, startY + (i * 30));
        }

        g.setColor(Color.WHITE);
        g.drawString("ENTER choose | ESC close", menuX + 20, menuY + menuHeight - 25);
    }
    
    //draws the selected character’s weapons and details
    private void drawEquipmentWeaponSelection(Graphics g, int menuX, int menuY, int menuWidth, int menuHeight) {

        PartyMember member = getSelectedEquipmentMember();

        if (member == null) {
            g.setColor(Color.WHITE);
            g.drawString("No unit selected.", menuX + 20, menuY + 30);
            return;
        }

        g.setColor(Color.WHITE);
        g.drawString("Equipment - " + member.getName(), menuX + 20, menuY + 30);

        String equippedName = "None";
        if (member.getEquippedWeapon() != null) {
            equippedName = member.getEquippedWeapon().getName();
        }

        g.drawString("Equipped: " + equippedName, menuX + 20, menuY + 55);

        List<Weapon> weapons = member.getWeapons();

        if (weapons == null || weapons.isEmpty()) {
            g.drawString("No weapons.", menuX + 20, menuY + 90);
            g.drawString("ESC back", menuX + 20, menuY + menuHeight - 25);
            return;
        }

        for (int i = 0; i < weapons.size(); i++) {
            Weapon weapon = weapons.get(i);

            if (i == equipmentWeaponIndex) {
                g.setColor(Color.YELLOW);
            } else if (weapon == member.getEquippedWeapon()) {
                g.setColor(new Color(80, 160, 255));
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == equipmentWeaponIndex) ? "> " : "  ";
            String equippedMark = (weapon == member.getEquippedWeapon()) ? " [E]" : "";

            g.drawString(prefix + weapon.getName() + equippedMark, menuX + 30, menuY + 90 + (i * 25));
        }

        Weapon selected = weapons.get(equipmentWeaponIndex);

        int detailY = menuY + 210;

        g.setColor(Color.WHITE);
        g.drawString("Selected Weapon", menuX + 20, detailY);
        g.drawString("Range: " + selected.getMinRange() + "-" + selected.getMaxRange(), menuX + 20, detailY + 25);
        g.drawString("Hit Bonus: +" + selected.getAttackBonus(), menuX + 20, detailY + 50);
        g.drawString(
            "Damage: " + selected.getDamageDiceCount() + "d" +
            selected.getDamageDiceSides() + " + " + selected.getDamageBonus(),
            menuX + 20,
            detailY + 75
        );

        String damageType = selected.isMagical() ? "Magic" : "Physical";
        g.drawString("Type: " + damageType, menuX + 220, detailY + 25);

        g.drawString("ENTER equip | ESC back", menuX + 220, detailY + 75);
        
    }
    
    //draws the overlay for STATUS
    private void drawStatus(Graphics g) {

        // Draw scene behind the status screen
        if (statusReturnState == GameState.OVERWORLD) {
            drawOverworld(g);
        } else if (statusReturnState == GameState.TOWN) {
            drawTown(g);
        } else if (statusReturnState == GameState.EXPLORATION) {
            drawExploration(g);
        }

        // Dark overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenWidth, screenHeight);

        int menuX = 80;
        int menuY = 60;
        int menuWidth = 580;
        int menuHeight = 400;

        g.setColor(new Color(25, 25, 30));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        g.drawString("Party Status", menuX + 20, menuY + 30);

        if (partyMembers == null || partyMembers.isEmpty()) {
            g.drawString("No party members.", menuX + 20, menuY + 65);
            return;
        }

        drawStatusPartyList(g, menuX, menuY);
        drawStatusDetails(g, menuX, menuY);

        g.setColor(Color.WHITE);
        g.drawString("UP/DOWN select | ESC close", menuX + 20, menuY + menuHeight - 20);
        
    }
    
    
    private void drawStatusPartyList(Graphics g, int menuX, int menuY) {

        int listX = menuX + 20;
        int listY = menuY + 70;

        g.setColor(Color.WHITE);
        g.drawString("Members", listX, listY - 25);

        for (int i = 0; i < partyMembers.size(); i++) {
            PartyMember member = partyMembers.get(i);

            if (i == statusMenuIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == statusMenuIndex) ? "> " : "  ";
            g.drawString(prefix + member.getName(), listX, listY + (i * 28));
        }
    }
    
    
    private void drawStatusDetails(Graphics g, int menuX, int menuY) {

        if (statusMenuIndex < 0 || statusMenuIndex >= partyMembers.size()) {
            statusMenuIndex = 0;
        }

        PartyMember member = partyMembers.get(statusMenuIndex);
        UnitStats stats = member.getStats();

        int detailX = menuX + 210;
        int detailY = menuY + 70;

        g.setColor(Color.WHITE);
        //g.drawString("AC: " + displayUnit.getArmorClass(), 280, panelY + 105); //AC FOR LATER
        g.drawString("Name: " + member.getName(), detailX, detailY);
        g.drawString("Class: " + member.getCharacterClass().getName(), detailX, detailY + 25);
        g.drawString("Level: " + member.getLevel(), detailX, detailY + 50);
        g.drawString("EXP: " + member.getExperience(), detailX, detailY + 75);

        String weaponName = "None";
        if (member.getEquippedWeapon() != null) {
            weaponName = member.getEquippedWeapon().getName();
        }

        g.drawString("Weapon: " + weaponName, detailX, detailY + 100);
        g.drawString("Skill: " + member.getSkillName(), detailX, detailY + 125);

        // Stats column 1
        int statX1 = detailX;
        int statY = detailY + 165;

        g.drawString("HP: " + stats.getMaxHp(), statX1, statY);
        g.drawString("MP: " + stats.getCurrentMana() + "/" + stats.getMaxMana(), statX1, statY + 25);
        g.drawString("STR: " + stats.getStrength(), statX1, statY + 50);
        g.drawString("MAG: " + stats.getMagic(), statX1, statY + 75);
        g.drawString("SKL: " + stats.getSkill(), statX1, statY + 100);
        g.drawString("SPD: " + stats.getSpeed(), statX1, statY + 125);

        // Stats column 2
        int statX2 = detailX + 120;

        g.drawString("LCK: " + stats.getLuck(), statX2, statY);
        g.drawString("DEF: " + stats.getDefense(), statX2, statY + 25);
        g.drawString("RES: " + stats.getResistance(), statX2, statY + 50);
        g.drawString("MOV: " + stats.getMovement(), statX2, statY + 75);

        // Weapon details
        if (member.getEquippedWeapon() != null) {
            Weapon weapon = member.getEquippedWeapon();

            int weaponX = detailX + 260;

            g.drawString("Weapon Info", weaponX, detailY);
            g.drawString("Range: " + weapon.getMinRange() + "-" + weapon.getMaxRange(), weaponX, detailY + 25);
            g.drawString("Hit: +" + weapon.getAttackBonus(), weaponX, detailY + 50);
            g.drawString(
                "Damage: " + weapon.getDamageDiceCount() + "d" +
                weapon.getDamageDiceSides() + " + " + weapon.getDamageBonus(),
                weaponX,
                detailY + 75
            );

            String type = weapon.isMagical() ? "Magic" : "Physical";
            g.drawString("Type: " + type, weaponX, detailY + 100);
            g.drawString("Wpn Type: " + weapon.getWeaponType(), weaponX, detailY + 125);
        }
        
    }
    
    //backround setting for campsite 
    private void drawCampBackground(Graphics g) {

        // Background
        g.setColor(new Color(10, 15, 30));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Stars / simple atmosphere
        g.setColor(Color.WHITE);
        g.fillOval(80, 60, 3, 3);
        g.fillOval(180, 90, 2, 2);
        g.fillOval(300, 50, 3, 3);
        g.fillOval(430, 80, 2, 2);

        // Campfire
        int fireX = mapWidth / 2 - 20;
        int fireY = mapHeight / 2;

        g.setColor(new Color(120, 70, 30));
        g.fillRect(fireX - 15, fireY + 25, 70, 12);

        g.setColor(new Color(255, 140, 30));
        g.fillOval(fireX, fireY, 40, 50);

        g.setColor(new Color(255, 220, 80));
        g.fillOval(fireX + 10, fireY + 10, 20, 30);
    }
    
    //drawing for Campsite and camp style
    private void drawCamp(Graphics g) {

        drawCampBackground(g);

        // Menu panel
        int menuX = mapWidth + 20;
        int menuY = 100;
        int menuWidth = rightPanelWidth - 40;
        int menuHeight = 200;

        g.setColor(new Color(25, 25, 35));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        g.drawString("Camp", menuX + 20, menuY + 25);
        
        if (campBondMenuOpen) {
            drawCampBondMenu(g, menuX, menuY, menuWidth, menuHeight);
            return;
        }

        for (int i = 0; i < campMenuOptions.length; i++) {
            if (i == campMenuIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == campMenuIndex) ? "> " : "  ";
            g.drawString(prefix + campMenuOptions[i], menuX + 25, menuY + 55 + (i * 25));
        }

        g.setColor(Color.WHITE);
        g.drawString("ENTER confirm", menuX + 20, menuY + 155);
        
        
    }
    
    
    
    private void drawCampBondMenu(Graphics g, int menuX, int menuY, int menuWidth, int menuHeight) {

        List<PartyMember> bondOptions = getBondOptions();

        g.setColor(new Color(25, 25, 35));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        g.drawString("Bond", menuX + 20, menuY + 25);

        if (bondOptions.isEmpty()) {
            g.drawString("No one is available.", menuX + 20, menuY + 60);
            return;
        }

        for (int i = 0; i < bondOptions.size(); i++) {
            PartyMember member = bondOptions.get(i);

            if (i == campBondIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == campBondIndex) ? "> " : "  ";
            g.drawString(prefix + member.getName(), menuX + 25, menuY + 55 + (i * 25));
        }
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Dean Bond: " + deanBond, menuX + 20, menuY + 155);
        g.drawString("Penelope Bond: " + penelopeBond, menuX + 20, menuY + 175);
        
        g.setColor(Color.WHITE);
        g.drawString("ENTER talk | ESC back", menuX + 20, menuY + 125);
        
        

        
    }
    
    
    
    //Battle will use battle specific units not over world logic
    private void drawBattle(Graphics g) {
    	
    	drawMap(g);
		drawObjectiveTile(g);
		drawBattleMovementRange(g);
		
		//Draws Unit
		for (BattleUnit unit : playerBattleUnits) {
		    if (unit != null && unit.isAlive()) {
		        unit.draw(g, tileSize);
		    }
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
	    drawTargetHighlight(g);
	    drawHealTargetHighlight(g);
	    drawZoomCombat(g);
	    drawBattlePhaseBanner(g);
    	
    }
    
    //Character Creations
    //Own helper to stop hard coding units in creation on battle start
    private BattleUnit createUnitFromId(String unitId, int col, int row, boolean enemy) {
    	
    	PartyMember partyMember = getPartyMemberById(unitId);

    	if (partyMember != null && !enemy) {
    	    return createBattleUnitFromPartyMember(partyMember, col, row); //hard coded leader/archer replaced with a call
    	}
    	
    	Weapon banditAxe = createWeaponById("bandit_axe");
    	Weapon enemyBow = createWeaponById("hunter_bow");
		
		//Class Name, Max HP, Armor Class, Movement Range, Weapon Type
		CharacterClass banditClass = new CharacterClass("Bandit", 10, 10, 4, new WeaponType[] { WeaponType.AXE });
		CharacterClass hunterClass = new CharacterClass("Hunter", 9, 11, 5, new WeaponType[] { WeaponType.BOW });
		//CharacterClass knightClass = new CharacterClass("Knight", 16, 15, 3);
		
		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance
		GrowthRates banditGrowth = new GrowthRates(70, 50, 0, 30, 36, 15, 25, 10);
		GrowthRates hunterGrowths = new GrowthRates(60, 35, 0, 55, 50, 25, 15, 20);
		
		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance, Movement
		UnitStats banditStats = new UnitStats(10, 0, 4, 0, 3, 3, 1, 1, 0, 4);
		UnitStats hunterStats = new UnitStats(9, 2, 3, 0, 5, 5, 2, 1, 1, 5);
		

		if (unitId.equals("bandit")) {
			return new BattleUnit("Bandit", col, row, enemy, banditAxe, banditClass, banditStats, banditGrowth, "", EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("hunter")) {
		    return new BattleUnit("Hunter", col, row, enemy, enemyBow, hunterClass, hunterStats, hunterGrowths, "", EnemyRole.RANGED);
		}
    	
    	return null;
    }
    
    //THIS IS FOR NEW PLAYERS UNITS ADD HERE!!!
    //Will make creation easier the above for players
    private void createPartyMembers() {
    	
    	partyMembers.clear();

    	
    	Weapon ironSword = createWeaponById("iron_sword");
    	Weapon shortBow = createWeaponById("short_bow");
    	//Weapon fireTome = createWeaponById("fire_tome");
    	Weapon trainingStaff = createWeaponById("training_staff");
        
        //Class Name, Max HP, Armor Class, Movement Range
        CharacterClass fighterClass = new CharacterClass("Fighter", 12, 12, 4, new WeaponType[] { WeaponType.SWORD });
        CharacterClass archerClass = new CharacterClass("Archer", 10, 11, 5, new WeaponType[] { WeaponType.BOW });
        CharacterClass penelopeClass = new CharacterClass("Cleric", 9, 10, 4, new WeaponType[] { WeaponType.STAFF, WeaponType.TOME });
        //CharacterClass mageClass = new CharacterClass("Mage", 8, 10, 4, new WeaponType[] { WeaponType.TOME });
        
        //Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance
        GrowthRates leaderGrowths = new GrowthRates(80, 55, 10, 50, 45, 35, 30, 20);
        GrowthRates archerGrowths = new GrowthRates(65, 40, 5, 60, 55, 40, 20, 25);
        //GrowthRates mageGrowths = new GrowthRates(50, 10, 60, 45, 45, 40, 15, 45);
        GrowthRates penelopeGrowths = new GrowthRates(60, 10, 45, 55, 45, 50, 20, 50);
        
        //Health, Mana, Strength, Magic, Skill, Speed, Luck, Defense, Resistance, Movement
        UnitStats leaderStats = new UnitStats(12, 5, 4, 0, 4, 4, 2, 2, 1, 4);
        UnitStats archerStats = new UnitStats(10, 3, 3, 0, 5, 5, 3, 1, 2, 5);
        //UnitStats mageStats = new UnitStats(8, 12, 0, 5, 4, 4, 4, 1, 3, 4);
        UnitStats penelopeStats = new UnitStats(10, 14, 0, 4, 5, 4, 5, 1, 4, 4);

        
        //Units
        leaderMember = new PartyMember(
            "leader",
            "Art Forger",
            1,
            0,
            leaderStats,
            leaderGrowths,
            fighterClass,
            ironSword,
            "Power Strike"
        );

        archerMember = new PartyMember(
            "archer_ally",
            "Dean Lokka",
            1,
            0,
            archerStats,
            archerGrowths,
            archerClass,
            shortBow,
            "Precise Shot"
        );
        
        mageMember = new PartyMember(
        	    "mage",
        	    "Penelope Godwinson",
        	    1,
        	    0,
        	    penelopeStats,
        	    penelopeGrowths,
        	    penelopeClass,
        	    trainingStaff,
        	    "Heal"
        	);
        
        partyMembers.add(leaderMember);
        partyMembers.add(archerMember);
        partyMembers.add(mageMember);
    }
    
    //Splitting and creating units from party ^Above
    private BattleUnit createBattleUnitFromPartyMember(PartyMember member, int col, int row) {

        BattleUnit unit = new BattleUnit(
            member.getName(),
            col,
            row,
            false,
            member.getWeapon(),
            member.getCharacterClass(),
            member.getStats(),
            member.getGrowthRates(),
            member.getSkillName(),
            null
        );

        unit.setLevel(member.getLevel());
        unit.setExperience(member.getExperience());

        return unit;
    }
    
    //Weapons list
    //#, #, #, #, #, #,
	//Weapon ID, Weapon Name, Weapon Type, Minimum range, Max range, Attack bonus, # Of  die thrown, # Of sides per die, Damage bonus, Is magic
    private Weapon createWeaponById(String weaponId) {

    	//Warriors
        if (weaponId.equals("iron_sword")) {
            return new Weapon("iron_sword", "Iron Sword", WeaponType.SWORD, 1, 1, 3, 1, 6, 2, false);
        }
        
        if (weaponId.equals("steel_sword")) {
            return new Weapon("steel_sword", "Steel Sword", WeaponType.SWORD, 1, 1, 2, 1, 8, 2, false);
        }

        //Archers
        if (weaponId.equals("short_bow")) {
            return new Weapon("short_bow", "Short Bow", WeaponType.BOW, 2, 2, 2, 1, 6, 1, false);
        }
        
        if (weaponId.equals("long_bow")) {
            return new Weapon("long_bow", "Long Bow", WeaponType.BOW, 2, 3, 2, 1, 8, 1, false);
        }
        
        if (weaponId.equals("hunter_bow")) {
            return new Weapon("hunter_bow", "Hunter Bow", WeaponType.BOW, 2, 2, 2, 1, 6, 1, false);
        }

        //Mage
        if (weaponId.equals("fire_tome")) {
            return new Weapon("fire_tome", "Fire Tome", WeaponType.TOME, 1, 2, 3, 1, 6, 2, true);
        }
        
        if (weaponId.equals("fire_tome_plus")) {
            return new Weapon("fire_tome_plus", "Fire Tome+", WeaponType.TOME, 1, 2, 3, 1, 8, 2, true);
        }
        
        //Cleric
        if (weaponId.equals("training_staff")) {
            return new Weapon("training_staff", "Training Staff", WeaponType.STAFF, 1, 1, 2, 1, 4, 1, true);
        }

        //Bandits
        if (weaponId.equals("bandit_axe")) {
            return new Weapon("bandit_axe", "Bandit Axe", WeaponType.AXE, 1, 1, 2, 1, 8, 1, false);
        }

        //Art Forger unique
        if (weaponId.equals("rusty_creation")) {
            return new Weapon("rusty_creation", "Rusty Creation",WeaponType.SWORD, 1, 1, 2, 1, 4, 1, false);
        }

        return null;
    }
    
    //Allows party member by the same ID in more maps/ spawns
    private PartyMember getPartyMemberById(String id) {

        for (PartyMember member : partyMembers) {
            if (member.getId().equals(id)) {
                return member;
            }
        }

        return null;
    }
    
    //Carries levels and xp and stats from battle to battle
    private void syncPartyMemberFromBattleUnit(BattleUnit battleUnit) {

        if (battleUnit == null) return;

        PartyMember member = getPartyMemberByName(battleUnit.getName());

        if (member == null) return;

        member.setLevel(battleUnit.getLevel());
        member.setExperience(battleUnit.getExperience());
        member.setStats(battleUnit.getStats());
    }
    
    //Calls Units By Name
    private PartyMember getPartyMemberByName(String name) {

        for (PartyMember member : partyMembers) {
            if (member.getName().equals(name)) {
                return member;
            }
        }

        return null;
    }
    
    //Now Stats are loaded based on they were when saved
    private void syncPartyProgressionFromBattle() {

    	for (BattleUnit unit : playerBattleUnits) {
            syncPartyMemberFromBattleUnit(unit);
        }
    }
    
    //Loads in the scenario player steps on 
    private void loadBattleScenario(BattleScenario scenario) {
    	
    	currentBattleScenario = scenario;
    	
    	currentObjective = scenario.getObjectiveType();
    	surviveTurnTarget = scenario.getSurviveTurnTarget();
    	currentBattleTurn = 1;
    	
    	if (scenario.getId().equals("prologue_ruins")) {
    	    objectiveCol = 4;
    	    objectiveRow = 4;
    	    
    	} else {
    	    objectiveCol = -1;
    	    objectiveRow = -1;
    	}
    	
    	//Map will build from scenario layout
    	Tile[][] battleMap = new Tile[maxScreenCol][maxScreenRow];
    	int[][] layout = scenario.getLayout();
    	
    	for (int col = 0; col < maxScreenCol; col++) {
    		for (int row = 0; row < maxScreenRow; row++) {
    			
    			int value = layout[row][col];
    			
    			if (value == 0) {
    				battleMap[col][row] = new Tile(TileType.GRASS);
    				
    			} else if (value == 1) {
    				battleMap[col][row] = new Tile(TileType.WATER);
    				
    			} else if (value == 2) {
    				battleMap[col][row] = new Tile(TileType.HILL);
    				
    			} else if (value == 3) {
    				battleMap[col][row] = new Tile(TileType.FOREST);
    				
    			} else if (value == 4) {
    				battleMap[col][row] = new Tile(TileType.ROAD);
    				
    			} else if (value == 5) {
    				battleMap[col][row] = new Tile(TileType.SHORE);
    				
    			}	
    		}
    	}
    	
    	// Current Map reset clear
    	currentMap = new GameMap(battleMap, scenario.getName());

    	playerBattleUnit = null;
    	allyBattleUnit = null;
    	mageBattleUnit = null;
    	
    	playerBattleUnits.clear();
    	enemyUnits.clear();

    	// New Player Spawns
    	for (UnitSpawn spawn : scenario.getPlayerSpawns()) {
    	    BattleUnit unit = createUnitFromId(spawn.getUnitId(), spawn.getCol(), spawn.getRow(), spawn.isEnemy());
    	    
    	    if (unit != null) {
    	        playerBattleUnits.add(unit);
    	    }

    	    //Older Code TEMP
    	    if (spawn.getUnitId().equals("leader")) {
    	        playerBattleUnit = unit;
    	    } else if (spawn.getUnitId().equals("archer_ally")) {
    	        allyBattleUnit = unit;
    	        
    	    } else if (spawn.getUnitId().equals("mage")) {
    	        mageBattleUnit = unit;
    	    }
    	    
    	    if (!playerBattleUnits.isEmpty()) {
    	        battleCursorCol = playerBattleUnits.get(0).getCol();
    	        battleCursorRow = playerBattleUnits.get(0).getRow();
    	    }
    	    
    	}

    	// New Enemy Spawns
    	for (UnitSpawn spawn : scenario.getEnemySpawns()) {
    	    BattleUnit enemy = createUnitFromId(spawn.getUnitId(), spawn.getCol(), spawn.getRow(), spawn.isEnemy());
    	    if (enemy != null) {
    	        enemyUnits.add(enemy);
    	    }
    	}
    		
    	//Load
    	battlePhase = "PLAYER";
    	clearBattleLog();
    	addBattleMessage("Player Phase");
    	showBattlePhaseBanner("Player Phase");
    	
    	if (!playerBattleUnits.isEmpty()) {
    	    battleCursorCol = playerBattleUnits.get(0).getCol();
    	    battleCursorRow = playerBattleUnits.get(0).getRow();
    	}
    	
    	currentState = GameState.BATTLE;
    	
    	
    }
    
    //Reinforcements Spawning
    private void checkReinforcements() {
    	
    	if (currentBattleScenario == null) return;
    	
    	for (ReinforcementSpawn reinforcement : currentBattleScenario.getReinforcement()) {
    		
    		if (!reinforcement.hasSpawned() && reinforcement.getTurn() == currentBattleTurn) {
    			
    			BattleUnit unit = createUnitFromId(
    					reinforcement.getUnitId(),
    					reinforcement.getCol(),
    					reinforcement.getRow(),
    					reinforcement.isEnemy()
    					
    			);
    			
    			if (unit != null) {
    				if (reinforcement.isEnemy()) {
    					enemyUnits.add(unit);
    					addBattleMessage("Enemy Reinforcements arrived!");
    				}
    			}
    			
    			reinforcement.setSpawned(true);
    		}
    	}
    }
    
    //Chapters to the game; could be hidden but UI and dialogue can display the current story state
    private String getStoryChapterName() {

        switch (storyChapter) {

            case 0:
                return "Prologue: The Sword in the Ruins";

            case 1:
                return "Chapter 1: Cerebella's Decline";

            case 2:
                return "Chapter 2: Checkmate";

            case 3:
                return "Chapter 3: The Merchant's Trap";

            case 4:
                return "Chapter 4: False Prophet";

            case 5:
                return "Chapter 5: The Torturer's Shrine";

            case 6:
                return "Chapter 6: The Broken Hero";

            case 7:
                return "Final Chapter: Rewrite";

            default:
                return "Unknown Chapter";
        }
    }
    
    //added in to stop potential spoilers due to chapter names
    private String getStoryChapterDisplayName() {

        switch (storyChapter) {

            case 0:
                return "Prologue";

            case 7:
                return "Final Chapter";

            default:
                return "Chapter " + storyChapter;
        }
    }
    
    //story changes, the over world may need to change too
    private void advanceStoryChapter(int newChapter) {

        storyChapter = newChapter;

        updateStoryWorldState();

        System.out.println("Story advanced to: " + getStoryChapterName());
    }
    
    //Story-chapter world change
    private void updateStoryWorldState() {

        if (overworldGameMap == null) return;

        updateOverworldQuestTiles();

        // Future story-based world changes will go here below as I see fit
    }
    
    //prologue
    private void triggerCreationSwordEvent() {
    	//Debug
    	System.out.println("hasCreationSword = " + hasCreationSword);

        if (hasCreationSword) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Art", "The pedestal is empty now.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "I still feel something strange here.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        hasCreationSword = true;

        Weapon rustyCreation = createWeaponById("rusty_creation");

        PartyMember art = getPartyMemberById("leader");

        if (art != null) {
            art.addWeapon(rustyCreation);
            art.equipWeapon(rustyCreation);
        }

        startDialogue(new DialogueLine[] {
            new DialogueLine("Dean", "Is that... a sword?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Art, wait. Something feels wrong.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "It feels like it is calling to me.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Narrator", "Art obtained Rusty Creation.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Narrator", "A flash of white light tears through the ruins.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);
    }
    
    //Importantly loads in creation when saving and loading while preventing duplicate Rusty Creation copies
    private void restoreCreationWeaponAfterLoad(String leaderEquippedWeaponId) {

        PartyMember art = getPartyMemberById("leader");

        if (art == null) {
            return;
        }

        if (hasCreationSword) {
            Weapon rustyCreation = createWeaponById("rusty_creation");

            if (rustyCreation != null && !partyMemberHasWeapon(art, "rusty_creation")) {
                art.addWeapon(rustyCreation);
            }
        }

        if (leaderEquippedWeaponId != null && !leaderEquippedWeaponId.isEmpty()) {
            Weapon weaponToEquip = findWeaponOnPartyMember(art, leaderEquippedWeaponId);

            if (weaponToEquip != null) {
                art.equipWeapon(weaponToEquip);
            }
        }
    }
    
    //Makes sure weapons are loaded properly
    private boolean partyMemberHasWeapon(PartyMember member, String weaponId) {

        for (Weapon weapon : member.getWeapons()) {
            if (weapon.getId().equals(weaponId)) {
                return true;
            }
        }

        return false;
    }
    
    //not allow re-equipping saved weapons
    private Weapon findWeaponOnPartyMember(PartyMember member, String weaponId) {

        for (Weapon weapon : member.getWeapons()) {
            if (weapon.getId().equals(weaponId)) {
                return weapon;
            }
        }

        return null;
    }
    
    //Quest accept helper
    private void acceptBanditQuest() {
        banditQuestAccepted = true;
        banditQuestCompleted = false;
        updateOverworldQuestTiles();
        addBattleMessage("Bandit quest accepted!"); // Battle Messages for looking at 
        System.out.println("Bandit quest accepted!"); // Can be deleted after
    }
    
    //Shows completion
    private void completeBanditQuest() {
        banditQuestCompleted = true;
        updateOverworldQuestTiles();
        System.out.println("Bandit quest completed!");
    }
    
    //Handles dialogue for before after and during the quest
    private boolean isBanditQuestActive() {
        return banditQuestAccepted && !banditQuestCompleted;
    }
    
    //Quest Display 
    private String getBanditQuestStatusText() {
        if (banditQuestRewardClaimed) {
            return "Reward Claimed";
        }

        if (banditQuestCompleted) {
            return "Completed";
        }

        if (isBanditQuestActive()) {
            return "Active";
        }

        return "Not Started";
    }
    
    //Quest Dialogue completion
    private String getBanditQuestObjectiveText() {
        if (banditQuestRewardClaimed) {
            return "Quest complete.";
        }

        if (banditQuestCompleted) {
            return "Return to the village elder.";
        }

        if (isBanditQuestActive()) {
            return "Clear the forest ambush.";
        }

        return "Talk to the village elder.";
    }
    
    private void drawQuestLog(Graphics g, int panelX, int startY) {
    	
    	g.setColor(Color.WHITE);
    	
    	g.drawString("Quest", panelX + 20, startY);
    	g.drawString("Bandit Trouble", panelX + 20, startY + 25);
    	g.drawString("Status: " + getBanditQuestStatusText(), panelX + 20, startY + 50);
    	
    	drawWrappedText(
    			g,
    			"Objective: " + getBanditQuestStatusText(),
    			panelX + 20,
    			startY + 75,
    			rightPanelWidth - 40,
    			18
    			
    			);
    	
    }
    
    //NPC Handling interaction
    private NPC getAdjacentNpc() {
    	
    	for (NPC npc : townNpcs) {
    		int distance = Math.abs(player.col - npc.getCol())
    				+ Math.abs(player.row - npc.getRow());
    		
    		if (distance == 1) {
    			return npc;
    		}
    	}
    	
    	return null;
    }
    
    //NPC interaction 
  //NPC interaction 
    private void interactWithNpc(NPC npc) {

        if (npc == null) return;

        
        // Special multi-speaker towns person conversation
        //DEbug: Test when advancing chapters for new dialogue testing
        if (npc.getName().equals("Townsperson")) {
        	
        	if (storyChapter == 0) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Townsperson", "You children should stay away from those ruins.", 
                    		DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("Leader", "We were just looking around.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.TOWN);

                return;
            }

            if (storyChapter >= 1) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Townsperson", "The crops have been failing lately.", DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("Leader", "Something feels wrong in Cerebella.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.TOWN);

                return;
            }
        }

        
        // Normal non-quest NPC dialogue
        if (!npc.hasQuest()) {
            startDialogue(npc.getName(), npc.getDefaultDialogue(), GameState.TOWN);
            return;
        }

        
        // Quest NPC dialogue
        if (npc.getQuestId().equals("bandit_quest")) {
        	
        	if (storyChapter < 2) {
                startDialogue(npc.getName(), new String[] {
                    "The roads are quiet for now.",
                    "Still, something feels uneasy these days."
                }, GameState.TOWN);

                return;
            }

            if (!banditQuestAccepted) {
                acceptBanditQuest();
                startDialogue(npc.getName(), npc.getQuestNotStartedDialogue(), GameState.TOWN);
                return;
            }

            if (isBanditQuestActive()) {
                startDialogue(npc.getName(), npc.getQuestActiveDialogue(), GameState.TOWN);
                return;
            }

            if (banditQuestCompleted && !banditQuestRewardClaimed) {
                gold += 100;
                banditQuestRewardClaimed = true;

                advanceStoryChapter(3);

                startDialogue(npc.getName(), new String[] {
                    "You dealt with the bandits?",
                    "Thank you. Please take this reward.",
                    "Received 100 gold."
                }, GameState.TOWN);

                return;
            }

            if (banditQuestRewardClaimed) {
                startDialogue(npc.getName(), new String[] {
                    "Thanks again for helping our village.",
                    "The roads are much safer now."
                }, GameState.TOWN);

                return;
            }

            if (banditQuestCompleted) {
                startDialogue(npc.getName(), npc.getQuestCompletedDialogue(), GameState.TOWN);
                return;
            }
            
        }

        
        startDialogue(npc.getName(), npc.getDefaultDialogue(), GameState.TOWN);
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
    	
    	TileType defenderTerrain = currentMap.getTiles()[previewDefender.getCol()][previewDefender.getRow()].getType();
    	int terrainBonus = getTerrainAcBonus(previewDefender);
    	
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
    	g.drawString("Terrain: " + defenderTerrain + " (+" + terrainBonus + " AC", boxX + 15, boxY + 160);
    }
    
    //Same as Attack preview but for skills
    private void drawSkillPreview(Graphics g, int panelX, int panelY) {
    	
    	int boxX = panelX + 20;
    	int boxY = mapHeight - 170;
    	int boxWidth = rightPanelWidth - 40;
    	int boxHeight = 160;
    	int manaCost = getSkillManaCost(skillAttacker.getSkillName());
    	
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
    	
    	g.drawString("Cost: " + manaCost + " MP", boxX + 15, boxY + 100);
    	g.drawString("Enter confirm", boxX + 15, boxY + 120);
    	g.drawString("ESC cancel", boxX + 15, boxY + 140);
    	
    }
    
    //Same as Skills but strictly for healing
    private void drawHealPreview(Graphics g, int panelX, int panelY) {

        int boxX = panelX + 20;
        int boxY = mapHeight - 170;
        int boxWidth = rightPanelWidth - 40;
        int boxHeight = 140;

        g.setColor(new Color(30, 30, 30, 230));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        int minHeal = calculateMinHeal(healCaster);
        int maxHeal = calculateMaxHeal(healCaster);
        int manaCost = getSkillManaCost("Heal");

        g.drawString("Heal Preview", boxX + 15, boxY + 20);
        g.drawString(healCaster.getName() + " -> " + healTarget.getName(), boxX + 15, boxY + 40);
        g.drawString("Skill: Heal", boxX + 15, boxY + 60);
        g.drawString("Restores: " + minHeal + " - " + maxHeal + " HP", boxX + 15, boxY + 80);
        g.drawString("Cost: " + manaCost + " MP", boxX + 15, boxY + 100);
        g.drawString("ENTER confirm", boxX + 15, boxY + 120);
        
        
    }
    
    //draws the targets for healing avaliable
    private void drawHealTargetSelection(Graphics g, int panelX, int panelY) {

        int boxX = panelX + 20;
        int boxY = mapHeight - 160;
        int boxWidth = rightPanelWidth - 40;
        int boxHeight = 120;

        g.setColor(new Color(30, 30, 30, 230));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        BattleUnit target = availableTargets.get(currentTargetIndex);

        g.drawString("Select Heal Target", boxX + 15, boxY + 20);
        g.drawString("Target: " + target.getName(), boxX + 15, boxY + 45);
        g.drawString("HP: " + target.getHp() + "/" + target.getMaxHp(), boxX + 15, boxY + 65);
        g.drawString("ENTER confirm", boxX + 15, boxY + 90);
        g.drawString("ESC cancel", boxX + 15, boxY + 110);
        
    }
    
    private void drawHealTargetHighlight(Graphics g) {

        if (!battleHealTargetSelectOpen || availableTargets.isEmpty()) {
            return;
        }

        BattleUnit target = availableTargets.get(currentTargetIndex);

        g.setColor(new Color(80, 220, 120));
        g.drawRect(target.getCol() * tileSize, target.getRow() * tileSize, tileSize, tileSize);
        g.drawRect(target.getCol() * tileSize + 1, target.getRow() * tileSize + 1, tileSize - 2, tileSize - 2);
        
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
    
    //Allies in range of healing or support skills
    private List<BattleUnit> getAlliesInRange(BattleUnit caster, int range) {

        List<BattleUnit> allies = new ArrayList<>();

        if (caster == null) {
            return allies;
        }

        for (BattleUnit unit : playerBattleUnits) {

            if (unit == null || !unit.isAlive()) {
                continue;
            }

            if (unit == caster) {
                continue; // for now do not allow self-heal
            }
            
            if (unit.getHp() >= unit.getMaxHp()) {
                continue; //Allows the UI to pick only injured allies 
            }

            int distance = Math.abs(caster.getCol() - unit.getCol())
                         + Math.abs(caster.getRow() - unit.getRow());

            if (distance <= range) {
                allies.add(unit);
            }
        }

        return allies;
    }
    
    //Skill menu branch decide whether to target enemies or allies
    private boolean isHealingSkill(String skillName) {
        return skillName.equals("Heal");
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
    	
    	if (currentBattleScenario != null &&
    		    currentBattleScenario.getId().equals("forest_ambush")) {
    		    completeBanditQuest();
    		}
    	
    	//Clears Tile
    	if (encounterSourceCol >= 0 && encounterSourceRow >= 0) {
    	    Tile clearedTile = new Tile(TileType.GRASS);
    	    overworldGameMap.getTiles()[encounterSourceCol][encounterSourceRow] = clearedTile;

    	    encounterSourceCol = -1;
    	    encounterSourceRow = -1;
    	}
    	
    	if (!anyEnemyAlive) {

    	    if (currentBattleScenario != null &&
    	        currentBattleScenario.getId().equals("forest_ambush")) {
    	        completeBanditQuest();
    	    }

    	    handleBattleVictory();
    	}
    }
    
  //Checks if the Survive turns battle as concluded its objective
    private void checkSurviveTurnsObjective() {
    	
    	//Clears Tile
    	if (encounterSourceCol >= 0 && encounterSourceRow >= 0) {
    	    Tile clearedTile = new Tile(TileType.GRASS);
    	    overworldGameMap.getTiles()[encounterSourceCol][encounterSourceRow] = clearedTile;

    	    encounterSourceCol = -1;
    	    encounterSourceRow = -1;
    	}
    	
    	if (currentBattleTurn > surviveTurnTarget) {
    		
    		handleBattleVictory();
    		
    	}
    }
    
  //Checks if there is a player on the tile in the objective
    private void checkReachTileObjective() {

        if (currentObjective != ObjectiveType.REACH_TILE) return;

        for (BattleUnit unit : playerBattleUnits) {
            if (unit != null &&
                unit.isAlive() &&
                unit.getCol() == objectiveCol &&
                unit.getRow() == objectiveRow) {

                handleBattleVictory();
                return;
            }
        }
    }
    
    //draws objective markers for reach tile missions
    private void drawObjectiveTile(Graphics g) {

        if (currentObjective != ObjectiveType.REACH_TILE) return;
        if (objectiveCol < 0 || objectiveRow < 0) return;

        int x = objectiveCol * tileSize;
        int y = objectiveRow * tileSize;

        g.setColor(Color.YELLOW);
        g.drawRect(x, y, tileSize, tileSize);
        g.drawRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
    }
    
    //Helps return player to over world correctly
    private void returnToOverworldAfterBattle() {

        currentMap = overworldGameMap;
        currentState = GameState.OVERWORLD;

        player.col = 3;
        player.row = 1;

        pendingReturnToOverworldAfterDialogue = false;
    }
    
    //victory logic; if there is a dialogue use it otherwise return to over world
    private void handleBattleVictory() {

    	syncPartyProgressionFromBattle();
    	
        addBattleMessage("Victory!");
        
        //Auto Advance from prologue
        if (currentBattleScenario != null &&
                currentBattleScenario.getId().equals("prologue_ruins")) {
                advanceStoryChapter(1);
            }

        if (currentBattleScenario != null &&
            currentBattleScenario.getOutroDialogue() != null &&
            currentBattleScenario.getOutroDialogue().length > 0) {

            pendingReturnToOverworldAfterDialogue = true;
            startDialogue(currentBattleScenario.getOutroDialogue(), GameState.BATTLE);
            return;
        }

        returnToOverworldAfterBattle();
    }
    
    
    //starts the player phase after ends
    //necessary to adding new unit
    private void startPlayerPhase() {
        battlePhase = "PLAYER";
        addBattleMessage("Player Phase");
        showBattlePhaseBanner("Player Phase");

        currentBattleTurn++;
        checkReinforcements();

        //Resets all at once
        for (BattleUnit unit : playerBattleUnits) {
            if (unit != null && unit.isAlive()) {
                unit.setHasMoved(false);
                unit.setHasActed(false);
            }
        }

        checkBattleEnd();
    }
    
    //Check helps end player phase after all acted updated enemy phase starts only when every living player unit has acted
    //necessary to adding new unit
    private boolean allPlayerUnitsHaveActed() {
    	
    	for (BattleUnit unit : playerBattleUnits) {
            if (unit != null && unit.isAlive() && !unit.hasActed()) {
                return false;
            }
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
    //necessary to adding new unit
    private boolean isTileOccupiedByOtherFriendly(int col, int row, BattleUnit currentUnit) {
    	
    	//Made it easier now friendly collision works for any number of player units
    	for (BattleUnit unit : playerBattleUnits) {
            if (unit != null &&
                unit != currentUnit &&
                unit.isAlive() &&
                unit.getCol() == col &&
                unit.getRow() == row) {
                return true;
            }
        }

        return false;
    }
    
    //make enemies be able to target Mage
    //necessary to adding new unit
    private boolean isTileOccupiedByAnyFriendly(int col, int row) {
    	
    	for (BattleUnit unit : playerBattleUnits) {
            if (unit != null &&
                unit.isAlive() &&
                unit.getCol() == col &&
                unit.getRow() == row) {
                return true;
            }
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
    	
    	//Any player alive check
    	boolean anyPlayerAlive = false;

    	for (BattleUnit unit : playerBattleUnits) {
    	    if (unit != null && unit.isAlive()) {
    	        anyPlayerAlive = true;
    	        break;
    	    }
    	}
    	
    	
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

    	    EnemyRole role = enemy.getEnemyRole();

    	    if (role == EnemyRole.RANGED) {
    	        handleRangedEnemyTurn(enemy, target);
    	    } else {
    	        handleAggressiveEnemyTurn(enemy, target);
    	    }
    	}

    	startPlayerPhase();
	
    }
    
    //Aggressive Enemy Trait
    private void handleAggressiveEnemyTurn(BattleUnit enemy, BattleUnit target) {

        // Attack immediately if already in range
        if (isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            startBattlePause(45);
            return;
        }

        // Otherwise move first
        moveEnemyTowardTarget(enemy, target);

        // After moving, check again and attack if now in range
        if (target.isAlive() && isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }
        }

        startBattlePause(45);
    }
    
    //Ranged Enemy Trait
    private void handleRangedEnemyTurn(BattleUnit enemy, BattleUnit target) {

        // If already in range, shoot immediately
        if (isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            startBattlePause(45);
            return;
        }

        // Otherwise reposition
        moveRangedEnemyTowardTarget(enemy, target);

        // After moving, check again and shoot if now in range
        if (target.isAlive() && isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }
        }

        startBattlePause(45);
    }
    
    //Helps with spacing
    private int getDistance(BattleUnit a, BattleUnit b) {
        return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
    }
    
    //Ranged Movement and Spacing Logic
    private void moveRangedEnemyTowardTarget(BattleUnit actingEnemy, BattleUnit target) {
        if (actingEnemy == null || target == null) return;

        int movement = actingEnemy.getStats().getMovement();
        boolean movedAtLeastOnce = false;

        for (int step = 0; step < movement; step++) {

            if (isEnemyInRange(actingEnemy, target)) {
                return;
            }

            int enemyCol = actingEnemy.getCol();
            int enemyRow = actingEnemy.getRow();

            int targetCol = target.getCol();
            int targetRow = target.getRow();

            int dx = targetCol - enemyCol;
            int dy = targetRow - enemyRow;

            java.util.List<int[]> candidateMoves = new java.util.ArrayList<>();

            // Prefer moves that bring enemy into range, but don't force adjacency if avoidable
            if (Math.abs(dx) > Math.abs(dy)) {

                if (dx > 0) candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
                else if (dx < 0) candidateMoves.add(new int[]{enemyCol - 1, enemyRow});

                if (dy > 0) candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
                else if (dy < 0) candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

            } else {

                if (dy > 0) candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
                else if (dy < 0) candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

                if (dx > 0) candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
                else if (dx < 0) candidateMoves.add(new int[]{enemyCol - 1, enemyRow});
            }

            // fallback side-steps
            candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
            candidateMoves.add(new int[]{enemyCol - 1, enemyRow});
            candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
            candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

            boolean moved = false;

            for (int[] move : candidateMoves) {
                int newCol = move[0];
                int newRow = move[1];

                if (newCol >= 0 && newCol < maxScreenCol &&
                    newRow >= 0 && newRow < maxScreenRow &&
                    currentMap.getTiles()[newCol][newRow].isPassable() &&
                    !isTileOccupiedByAnyFriendly(newCol, newRow) &&
                    !isTileOccupiedByOtherEnemy(newCol, newRow, actingEnemy)) {

                    int newDistance = Math.abs(newCol - target.getCol()) + Math.abs(newRow - target.getRow());

                    // For ranged enemies, prefer not to stop adjacent if they can help it
                    if (newDistance == 1 && actingEnemy.getWeapon().getMinRange() > 1) {
                        continue;
                    }

                    actingEnemy.setPosition(newCol, newRow);
                    moved = true;
                    movedAtLeastOnce = true;
                    break;
                }
            }

            if (!moved) {
                return;
            }
        }

        if (movedAtLeastOnce) {
            addBattleMessage(actingEnemy.getName() + " repositioned.");
        }
    }
    
    
    //enemy that cannot yet attack move towards the player
    private void moveEnemyTowardTarget(BattleUnit actingEnemy, BattleUnit target) {
        if (actingEnemy == null || target == null) return;

        int movement = actingEnemy.getStats().getMovement();
        boolean movedAtLeastOnce = false;

        for (int step = 0; step < movement; step++) {

            if (isEnemyInRange(actingEnemy, target)) {
                return;
            }

            int enemyCol = actingEnemy.getCol();
            int enemyRow = actingEnemy.getRow();

            int targetCol = target.getCol();
            int targetRow = target.getRow();

            int dx = targetCol - enemyCol;
            int dy = targetRow - enemyRow;

            java.util.List<int[]> candidateMoves = new java.util.ArrayList<>();

            // Prefer the larger-distance axis first
            if (Math.abs(dx) > Math.abs(dy)) {

                if (dx > 0) candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
                else if (dx < 0) candidateMoves.add(new int[]{enemyCol - 1, enemyRow});

                if (dy > 0) candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
                else if (dy < 0) candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

            } else {

                if (dy > 0) candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
                else if (dy < 0) candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

                if (dx > 0) candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
                else if (dx < 0) candidateMoves.add(new int[]{enemyCol - 1, enemyRow});
            }

            // Fall back for going around obstacles/other units
            candidateMoves.add(new int[]{enemyCol + 1, enemyRow});
            candidateMoves.add(new int[]{enemyCol - 1, enemyRow});
            candidateMoves.add(new int[]{enemyCol, enemyRow + 1});
            candidateMoves.add(new int[]{enemyCol, enemyRow - 1});

            boolean moved = false;

            for (int[] move : candidateMoves) {
                int newCol = move[0];
                int newRow = move[1];

                if (newCol >= 0 && newCol < maxScreenCol &&
                    newRow >= 0 && newRow < maxScreenRow &&
                    currentMap.getTiles()[newCol][newRow].isPassable() &&
                    !isTileOccupiedByAnyFriendly(newCol, newRow) &&
                    !isTileOccupiedByOtherEnemy(newCol, newRow, actingEnemy)) {

                    actingEnemy.setPosition(newCol, newRow);
                    moved = true;
                    movedAtLeastOnce = true;
                    break;
                }
            }

            if (!moved) {
                return;
            }
        }

        if (movedAtLeastOnce) {
            addBattleMessage(actingEnemy.getName() + " moved.");
        }
    }
    
    //Helper for enemies to attack other units not just leader updated to now any unit and future ones can be targets
    //necessary to adding new unit
    private BattleUnit getEnemyTarget(BattleUnit actingEnemy) {

    	BattleUnit target = null;
        int closestDistance = Integer.MAX_VALUE;

        for (BattleUnit unit : playerBattleUnits) {

            if (unit == null || !unit.isAlive()) {
                continue;
            }

            int distance = Math.abs(actingEnemy.getCol() - unit.getCol())
                         + Math.abs(actingEnemy.getRow() - unit.getRow());

            if (distance < closestDistance) {
                target = unit;
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
        
        if (skillName.equals("Fire Bolt")) {
            performFireBolt(attacker, defender);
            return;
        }

        addBattleMessage(attacker.getName() + " has no usable skill.");
    }
    
    //Gets and uses mana for skills 
    private int getSkillManaCost(String skillName) {

        if (skillName.equals("Power Strike")) {
            return 2;
        }

        if (skillName.equals("Precise Shot")) {
            return 2;
        }

        if (skillName.equals("Fire Bolt")) {
            return 4;
        }
        
        if (skillName.equals("Heal")) {
            return 4;
        }

        return 0;
    }
    
    //Stronger Version of a normal strike
    private void performPowerStrike(BattleUnit attacker, BattleUnit defender) {
    	
    	resetLastAttackResult();
    	Weapon weapon = attacker.getWeapon();
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2; //Skill effects hit rating
    	int roll = random.nextInt(20) + 1; //1 through 20
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus;
    	int defenderAc = getTotalArmorClass(defender);
    	
    	addBattleMessage(attacker.getName() + " used Power Strike");

    	if (totalAttack >= defenderAc) {
    		
    		lastAttackHit = true;
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = attacker.getStats().getStrength();
    		int defenseStat = defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat + 3 - defenseStat;
    		if (damage < 0) damage = 0;
    	
    		defender.takeDamage(damage);
    		lastAttackDamage = damage;
    		
    		addBattleMessage("Power Strike Hit!");
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		
    	} else {
    		lastAttackHit = false;
    		addBattleMessage("Power Strike Missed!");
    	}
    }
    
    //More accurate than a regular shot
    private void performPreciseShot(BattleUnit attacker, BattleUnit defender) {
    	
    	resetLastAttackResult();
    	Weapon weapon = attacker.getWeapon();
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2;
    	int roll = random.nextInt(20) + 1;
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus + 10;
    	int defenderAc = getTotalArmorClass(defender);
    	
    	addBattleMessage(attacker.getName() + " used Precise Shot");

    	if (totalAttack >= defenderAc) {
    		
    		lastAttackHit = true;
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = attacker.getStats().getStrength();
    		int defenseStat = defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat - defenseStat;
    		if (damage < 0) damage = 0;
    	
    		defender.takeDamage(damage);
    		lastAttackDamage = damage;
    		
    		addBattleMessage("Precise Shot Hit!");
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		
    	} else {
    		lastAttackHit = false;
    		addBattleMessage("Precise Shot Missed!");
    	}
    }
    
    //Mage
    private void performFireBolt(BattleUnit attacker, BattleUnit defender) {

        resetLastAttackResult();

        Weapon weapon = attacker.getWeapon();

        int statHitBonus = attacker.getStats().getSkill() / 2;
        int roll = random.nextInt(20) + 1;

        // Fire Bolt gets a small hit bonus because it is a focused spell
        int totalAttack = roll + weapon.getAttackBonus() + statHitBonus + 1;

        int defenderAc = getTotalArmorClass(defender);

        addBattleMessage(attacker.getName() + " cast Fire Bolt!");

        if (totalAttack >= defenderAc) {

            lastAttackHit = true;

            int baseDamage = rollWeaponDamage(weapon);
            int attackStat = attacker.getStats().getMagic();
            int defenseStat = defender.getStats().getResistance();

            int damage = baseDamage + attackStat + 2 - defenseStat;

            if (damage < 0) {
                damage = 0;
            }

            // Crit still uses Luck
            int critRoll = random.nextInt(100) + 1;
            int critChance = calculateCritChance(attacker);

            if (critRoll <= critChance) {
                lastAttackCrit = true;
                damage *= 2;
            }

            if (damage >= defender.getHp()) {
                if (tryLuckyBreak(defender)) {
                    lastAttackLuckyBreak = true;
                    lastAttackDamage = 0;

                    defender.setHp(1);

                    addBattleMessage("Fire Bolt hit!");
                    if (lastAttackCrit) {
                        addBattleMessage("Critical hit!");
                    }
                    addBattleMessage(defender.getName() + " triggered Lucky Break!");
                    return;
                }
            }

            defender.takeDamage(damage);
            lastAttackDamage = damage;

            addBattleMessage("Fire Bolt hit!");
            if (lastAttackCrit) {
                addBattleMessage("Critical hit!");
            }
            addBattleMessage(defender.getName() + " took " + damage + " damage.");

        } else {
            lastAttackHit = false;
            addBattleMessage("Fire Bolt missed!");
        }
    }
    
    
    private void resetLastAttackResult() {
        lastAttackHit = false;
        lastAttackCrit = false;
        lastAttackLuckyBreak = false;
        lastAttackDamage = 0;
    }
    
    
    
    //Damage as well as attack rolls
    private boolean performAttack(BattleUnit attacker, BattleUnit defender) {
    	
    	System.out.println("performAttack called");
    	resetLastAttackResult();
    	Weapon weapon = attacker.getWeapon();
    	
    	lastAttackHit = false;
    	lastAttackCrit = false;
    	lastAttackLuckyBreak = false;
    	lastAttackDamage = 0;
    	
    	
    	int statHitBonus = attacker.getStats().getSkill() / 2; //Skill effects hit rating
    	int roll = random.nextInt(20) + 1; //1 through 20
    	int totalAttack = roll + weapon.getAttackBonus() + statHitBonus;
    	
    	addBattleMessage(attacker.getName() + " used " + weapon.getName() + ".");
    	
    	int defenderAc = getTotalArmorClass(defender);
    	
    	if (totalAttack >= defenderAc) {
    		
    		
    		int baseDamage = rollWeaponDamage(weapon);
    		int attackStat = weapon.isMagical() ? attacker.getStats().getMagic() : attacker.getStats().getStrength();
    		int defenseStat = weapon.isMagical() ? defender.getStats().getResistance() : defender.getStats().getDefense();
    		
    		int damage = baseDamage + attackStat - defenseStat;
    		if (damage < 0) damage = 0;
    		
    		lastAttackHit = true; //Floating Damage dealt
    		lastAttackDamage = damage;
    		
    		//Critical
    		boolean critical = false;
    		int critRoll = random.nextInt(100) + 1;
    		int critChance = calculateCritChance(attacker);
    		
    		if (critRoll <= critChance) {
    			critical = true;
    			lastAttackCrit = true; //Floating Critical
    			damage *= 2;
    		}
    			
    		//Lucky Break checker
    		if (damage >= defender.getHp()) {
    			if (tryLuckyBreak(defender)) {
    				defender.setHp(1);
    				
    				addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defenderAc + " -> HIT!");
    				if (critical) {
    					addBattleMessage("Critical Hit!");
    				}
    				addBattleMessage(defender.getName() + " triggered Lucky Break ");
    				addBattleMessage(defender.getName() + " survived at 1 HP ");
    				
    				lastAttackLuckyBreak = true; //Floating Lucky Break
    				lastAttackDamage = 0;
    				
    				return true;
    			}
    		}
    		
    		defender.takeDamage(damage);
    		
    		addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defenderAc + " -> HIT!");
    		if (critical) {
				addBattleMessage("Critical Hit!");
			}
    		addBattleMessage(defender.getName() + " took " + damage + " damage.");
    		
    		return true;
    		
    	} else {
    		lastAttackHit = false;
    		addBattleMessage("Roll: " + roll + " + " + weapon.getAttackBonus()
    				+ " + SKL " + statHitBonus + " = " + totalAttack
    				+ " vs AC " + defenderAc + " -> MISS!");
    				
    		return false;
    	}
    	
    }
    
    //Will get total Armor class from all bonuses
    private int getTotalArmorClass(BattleUnit unit) {
    	
    	return unit.getArmorClass() + getTerrainAcBonus(unit);
    }
    
    
    //Forecast for players to read on when attacking and defending
    private int calculateHitChance(BattleUnit attacker, BattleUnit defender) {
    	
    	int defenderAc = getTotalArmorClass(defender);
    	
    	int hitScore = 50
    			+ (attacker.getWeapon().getAttackBonus() * 10)
    			+ ((attacker.getStats().getSkill() / 2) * 10)
				- ((defenderAc - 10) * 5);
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
    
    //Min heal
    private int calculateMinHeal(BattleUnit healer) {
        return 1 + healer.getStats().getMagic();
    }
    

    //Max heal
    private int calculateMaxHeal(BattleUnit healer) {
        return 6 + healer.getStats().getMagic();
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
    
    //healing methods
    private int rollHealAmount(BattleUnit healer) {
        int roll = random.nextInt(6) + 1;
        return roll + healer.getStats().getMagic();
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
    	
    	//Counter
    	zoomCounterPending = false;
    	zoomCounterResolved = false;
    	zoomShowingCounter = false;
    	
    	//Actual showing
    	zoomFloatingText = "";
    	zoomFloatingText2 = "";
    	zoomFloatingTextTimer = 0;
    	zoomFloatingText2Timer = 0;
    }
    
    //Draw Zoom in combat
    private void drawZoomCombat(Graphics g) {
    	
    	
    	if (!battleZoomCombatOpen || zoomAttacker == null || zoomDefender == null) return;
    	
    	//Player Terrain logic
    	Tile attackerTile = currentMap.getTiles()[zoomAttacker.getCol()][zoomAttacker.getRow()];
    	TileType terrain = attackerTile.getType();

    	Color terrainColor = getTerrainColor(terrain);
    	
    	//back round overlay that takes the terrain
    	g.setColor(terrainColor);
    	g.fillRect(0, 0, mapWidth, mapHeight);
    	
    	//left side (attacker)
    	g.setColor(getTerrainColor(terrain));
    	g.fillRect(0, 0, mapWidth / 2 , mapHeight);
    	
    	//right side (defenders)
    	g.setColor(getTerrainColor(terrain).darker());
    	g.fillRect(mapWidth / 2, 0, mapWidth / 2 , mapHeight);
    	
    	//Contrast
    	g.setColor(new Color(0, 0, 0, 120));
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
    	
    	//Show terrain name
    	g.setColor(Color.WHITE);
    	g.drawString("Terrain: " + terrain.toString(), mapWidth / 2 - 50, 30);
    	
    	//Terrain bonus text
    	int terrainBonus = getTerrainAcBonus(zoomAttacker);
    	g.drawString("Defense AC Bonus: +" + terrainBonus, mapWidth / 2 - 50, 50);
    	
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
    	
    		if (!zoomAttackResolved) {
    		    g.drawString("Opening battle...", 180, 385);

    		} else if (zoomCounterPending && !zoomCounterResolved && (zoomFloatingTextTimer <= 0 && zoomFloatingText2Timer <= 0)) {
    		    g.drawString("ENTER for counterattack", 160, 385);

    		} else if (zoomCounterResolved && (zoomFloatingTextTimer <= 0 && zoomFloatingText2Timer <= 0)) {
    		    g.drawString("ENTER to return", 185, 385);

    		} else {
    		    g.drawString("Resolving...", 190, 385);
    		}
    		
    	}
    	
    	if (!zoomShowingCounter) {
    	    g.drawString(zoomAttacker.getName() + " attacks!", 170, 105);
    	} else {
    	    g.drawString(zoomDefender.getName() + " counters!", 170, 105);
    	}
    	
    	drawZoomFloatingTexts(g);
    }
    
    //Helper for color logic
    private Color getTerrainColor(TileType type) {
    	
    	switch (type) {
    	
    	case GRASS: 
    		return new Color(60, 140, 60);
    		
    	case FOREST: 
    		return new Color(30, 100, 30);
    		
    	case SHORE: 
    		return new Color(70, 120, 160);
    		
    	case ROAD: 
    		return new Color(120, 90, 60);
    		
    	case HILL: 
    		return new Color(110, 85, 55);
    		
    	case TOWN: 
    		return new Color(150, 120, 80);
    		
    	case WATER: 
    		return new Color(70, 120, 160);
    		
    	default: 
    		return new Color(50, 50, 50);
    	
    	
    	
    	}
    }
    
    private int getTerrainAcBonus(BattleUnit unit) {
    	
    	if (unit == null || currentMap == null) {
    		return 0;
    	}
    	
    	TileType terrain = currentMap.getTiles()[unit.getCol()][unit.getRow()].getType();
    	
    	switch (terrain) {
    	case FOREST:
    		return 1;
    		
    	default: 
    		return 0;
    		
    	}
    }
    
    //Zoom in floating text
    private void showZoomFloatingText(String text, int x, int y) {
    	
    	zoomFloatingText = text;
    	zoomFloatingTextX = x;
    	zoomFloatingTextY = y;
    	zoomFloatingTextTimer = ZOOM_FLOATING_TEXT_DURATION;
    }
    
    
  //Zoom in floating text number 2
    private void showZoomFloatingText2(String text, int x, int y) {
    	
    	zoomFloatingText2 = text;
    	zoomFloatingText2X = x;
    	zoomFloatingText2Y = y;
    	zoomFloatingText2Timer = ZOOM_FLOATING_TEXT_DURATION;
    }
    
    //Fades out the text as the timer runs out
    private void drawZoomFloatingTexts(Graphics g) {
    	
    	Graphics2D g2 = (Graphics2D) g;
    	java.awt.Font originalFont = g2.getFont();    
    	
    	if (zoomFloatingTextTimer > 0) {
    		int alpha = (int)(255 * (zoomFloatingTextTimer / (float) ZOOM_FLOATING_TEXT_DURATION));
    		g2.setColor(new Color(255, 255, 255, alpha));
    		g2.setFont(g2.getFont().deriveFont(22f));
    		g2.drawString(zoomFloatingText, zoomFloatingTextX, zoomFloatingTextY);
    		
    	}
    	
    	if (zoomFloatingText2Timer > 0) {
    		int alpha = (int)(255 * (zoomFloatingText2Timer / (float) ZOOM_FLOATING_TEXT_DURATION));
    		g2.setColor(new Color(255, 220, 220, alpha));
    		g2.setFont(g2.getFont().deriveFont(22f));
    		g2.drawString(zoomFloatingText2, zoomFloatingText2X, zoomFloatingText2Y);
    		
    	}
    
    	g2.setFont(originalFont);
    }
    
    //helper will allow text defender to show when countering
    private void showZoomResultText(String actionName, boolean hit, boolean crit, boolean luckyBreak, int damage, boolean isSkill) {

        if (!hit) {
            showZoomFloatingText(isSkill ? "Skill Miss!" : "Miss!", mapWidth / 2 - 40, 180);
            return;
        }

        if (luckyBreak) {
            showZoomFloatingText("Lucky Break!", mapWidth / 2 - 55, 140);
            showZoomFloatingText2("1 HP", mapWidth / 2 - 10, 200);
            return;
        }

        if (crit) {
            showZoomFloatingText("Critical!", mapWidth / 2 - 45, 160);
            showZoomFloatingText2("-" + damage, mapWidth / 2 - 10, 200);
            return;
        }

        showZoomFloatingText(isSkill ? actionName + "!" : "Hit!", mapWidth / 2 - 25, 160);
        showZoomFloatingText2("-" + damage, mapWidth / 2 - 10, 200);
    }
    
    
    
    //Dialogue
    private void drawDialogue(Graphics g) {
    	
    	if (previousState == GameState.BATTLE) {
            drawBattle(g);
        }
        else if (previousState == GameState.TOWN) {
            drawTown(g);
        }
        else if (previousState == GameState.OVERWORLD) {
            drawOverworld(g);
        }
        else if (previousState == GameState.SHOP) {
            drawShop(g);
        }
        else if (previousState == GameState.EXPLORATION) {
            drawExploration(g);
        }
        else if (previousState == GameState.CAMP) {
            drawCampBackground(g);
        }
    	
    }
    
    //Allows freedom of movement
    private boolean canMove() {

        switch (currentState) {

            case OVERWORLD:
                return movementLeft > 0;

            case TOWN:
                return true;
                
            case EXPLORATION:
                return true;
                
            //battle will be based on turns and unit type later
            case BATTLE:
            	return true;

            default:
                return false;
        }
    }
    
    //Save game function allows the save game as a text form
    private void saveGame() {

        try {
            FileWriter writer = new FileWriter(SAVE_FILE);

            //Save Stats for you
            writer.write("playerCol=" + player.col + "\n");
            writer.write("playerRow=" + player.row + "\n");
            writer.write("day=" + day + "\n");
            writer.write("gold=" + gold + "\n");
            writer.write("storyChapter=" + storyChapter + "\n");
            writer.write("hasCreationSword=" + hasCreationSword + "\n");
            writer.write("creationAwakened=" + creationAwakened + "\n");
            
            //Camp bond data
            writer.write("penelopeBond=" + penelopeBond + "\n");
            writer.write("deanBond=" + deanBond + "\n");
            writer.write("penelopeLastTalkedChapter=" + penelopeLastTalkedChapter + "\n");
            writer.write("deanLastTalkedChapter=" + deanLastTalkedChapter + "\n");
                     
            //Quest
            writer.write("banditQuestAccepted=" + banditQuestAccepted + "\n");
            writer.write("banditQuestCompleted=" + banditQuestCompleted + "\n");
            writer.write("banditQuestRewardClaimed=" + banditQuestRewardClaimed + "\n");

            //Deletes Old Hard code in favor of calling 
            writer.write("partyCount=" + partyMembers.size() + "\n");

            for (int i = 0; i < partyMembers.size(); i++) {
                writePartyMember(writer, partyMembers.get(i), i);
            }

            writer.close();

            System.out.println("Game saved.");

        } catch (IOException e) {
            System.out.println("Save failed.");
            e.printStackTrace();
        }
    }
    
    //Load game after Save to give function
    private void loadGame() {

        File file = new File(SAVE_FILE);

        if (!file.exists()) {
            System.out.println("No save file found.");
            return;
        }

        try {
            Scanner scanner = new Scanner(file);
            
            int partyCount = 0;

            String[] partyIds = new String[20];
            int[] partyLevels = new int[20];
            int[] partyExps = new int[20];

            int[] partyMaxHp = new int[20];
            int[] partyMaxMana = new int[20];
            int[] partyCurrentMana = new int[20];

            int[] partyStr = new int[20];
            int[] partyMag = new int[20];
            int[] partySkl = new int[20];
            int[] partySpd = new int[20];
            int[] partyLck = new int[20];
            int[] partyDef = new int[20];
            int[] partyRes = new int[20];
            int[] partyMov = new int[20];

            int[] partyWeaponCounts = new int[20];
            String[][] partyWeaponIds = new String[20][20];
            String[] partyEquippedWeaponIds = new String[20];

            while (scanner.hasNextLine()) {
            	
                String line = scanner.nextLine();

                String[] parts = line.split("=");

                if (parts.length != 2) {
                    continue;
                }

                String key = parts[0];
                String value = parts[1];

                //General
                if (key.equals("playerCol")) {
                    player.col = Integer.parseInt(value);
                }
                else if (key.equals("playerRow")) {
                    player.row = Integer.parseInt(value);
                }
                else if (key.equals("day")) {
                    day = Integer.parseInt(value);
                }
                else if (key.equals("gold")) {
                    gold = Integer.parseInt(value);
                }
                else if (key.equals("storyChapter")) {
                    storyChapter = Integer.parseInt(value);
                }
                else if (key.equals("hasCreationSword")) {
                    hasCreationSword = Boolean.parseBoolean(value);
                }
                else if (key.equals("creationAwakened")) {
                    creationAwakened = Boolean.parseBoolean(value);
                }
                else if (key.equals("penelopeBond")) {
                    penelopeBond = Integer.parseInt(value);
                }
                else if (key.equals("deanBond")) {
                    deanBond = Integer.parseInt(value);
                }
                else if (key.equals("penelopeLastTalkedChapter")) {
                    penelopeLastTalkedChapter = Integer.parseInt(value);
                }
                else if (key.equals("deanLastTalkedChapter")) {
                    deanLastTalkedChapter = Integer.parseInt(value);
                }
                else if (key.equals("partyCount")) {
                    partyCount = Integer.parseInt(value);
                }
                //Active Quests
                else if (key.equals("banditQuestAccepted")) {
                    banditQuestAccepted = Boolean.parseBoolean(value);
                }
                else if (key.equals("banditQuestCompleted")) {
                    banditQuestCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("banditQuestRewardClaimed")) {
                    banditQuestRewardClaimed = Boolean.parseBoolean(value);
                }
                
                
                //leader
                else if (key.equals("leaderLevel")) {
                    leaderMember.setLevel(Integer.parseInt(value));
                }
                else if (key.equals("leaderExp")) {
                    leaderMember.setExperience(Integer.parseInt(value));
                }
                else if (key.equals("leaderMaxHp")) {
                    leaderMember.getStats().setMaxHp(Integer.parseInt(value));
                }
                else if (key.equals("leaderStr")) {
                    leaderMember.getStats().setStrength(Integer.parseInt(value));
                }
                else if (key.equals("leaderMag")) {
                    leaderMember.getStats().setMagic(Integer.parseInt(value));
                }
                else if (key.equals("leaderSkl")) {
                    leaderMember.getStats().setSkill(Integer.parseInt(value));
                }
                else if (key.equals("leaderSpd")) {
                    leaderMember.getStats().setSpeed(Integer.parseInt(value));
                }
                else if (key.equals("leaderLck")) {
                    leaderMember.getStats().setLuck(Integer.parseInt(value));
                }
                else if (key.equals("leaderDef")) {
                    leaderMember.getStats().setDefense(Integer.parseInt(value));
                }
                else if (key.equals("leaderRes")) {
                    leaderMember.getStats().setResistance(Integer.parseInt(value));
                }
                else if (key.equals("leaderMov")) {
                    leaderMember.getStats().setMovement(Integer.parseInt(value));
                }
                
                //archer
                else if (key.equals("archerLevel")) {
                    archerMember.setLevel(Integer.parseInt(value));
                }
                else if (key.equals("archerExp")) {
                    archerMember.setExperience(Integer.parseInt(value));
                }
                else if (key.equals("archerMaxHp")) {
                    archerMember.getStats().setMaxHp(Integer.parseInt(value));
                }
                else if (key.equals("archerStr")) {
                    archerMember.getStats().setStrength(Integer.parseInt(value));
                }
                else if (key.equals("archerMag")) {
                    archerMember.getStats().setMagic(Integer.parseInt(value));
                }
                else if (key.equals("archerSkl")) {
                    archerMember.getStats().setSkill(Integer.parseInt(value));
                }
                else if (key.equals("archerSpd")) {
                    archerMember.getStats().setSpeed(Integer.parseInt(value));
                }
                else if (key.equals("archerLck")) {
                    archerMember.getStats().setLuck(Integer.parseInt(value));
                }
                else if (key.equals("archerDef")) {
                    archerMember.getStats().setDefense(Integer.parseInt(value));
                }
                else if (key.equals("archerRes")) {
                    archerMember.getStats().setResistance(Integer.parseInt(value));
                }
                else if (key.equals("archerMov")) {
                    archerMember.getStats().setMovement(Integer.parseInt(value));
                }
                
                else if (key.startsWith("party")) {

                    String numberPart = key.replaceAll("[^0-9]", "");

                    if (!numberPart.isEmpty()) {
                        int index = Integer.parseInt(numberPart);

                        if (key.endsWith("Id")) {
                            partyIds[index] = value;
                        }
                        else if (key.endsWith("Level")) {
                            partyLevels[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Exp")) {
                            partyExps[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("MaxHp")) {
                            partyMaxHp[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("MaxMana")) {
                            partyMaxMana[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("CurrentMana")) {
                            partyCurrentMana[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Str")) {
                            partyStr[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Mag")) {
                            partyMag[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Skl")) {
                            partySkl[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Spd")) {
                            partySpd[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Lck")) {
                            partyLck[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Def")) {
                            partyDef[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Res")) {
                            partyRes[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("Mov")) {
                            partyMov[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("WeaponCount")) {
                            partyWeaponCounts[index] = Integer.parseInt(value);
                        }
                        else if (key.endsWith("EquippedWeapon")) {
                            partyEquippedWeaponIds[index] = value;
                        }
                        
                      //Parses individual weapon IDs for each individual party member
                        else if (key.contains("Weapon")) {

                            if (!key.endsWith("WeaponCount") && !key.endsWith("EquippedWeapon")) {

                                int weaponWordIndex = key.indexOf("Weapon");

                                String partyIndexText = key.substring(5, weaponWordIndex);
                                String weaponIndexText = key.substring(weaponWordIndex + "Weapon".length());

                                if (!partyIndexText.isEmpty() && !weaponIndexText.isEmpty()) {
                                    int partyIndex = Integer.parseInt(partyIndexText);
                                    int weaponIndex = Integer.parseInt(weaponIndexText);

                                    partyWeaponIds[partyIndex][weaponIndex] = value;
                                }
                            }
                        }
                    }
                }
            }

            scanner.close();
         
            //Party Reading for stats
            for (int i = 0; i < partyCount; i++) {

                if (partyIds[i] == null) {
                    continue;
                }

                //Apply Data after Reads
                UnitStats loadedStats = new UnitStats(
                    partyMaxHp[i],
                    partyMaxMana[i],
                    partyStr[i],
                    partyMag[i],
                    partySkl[i],
                    partySpd[i],
                    partyLck[i],
                    partyDef[i],
                    partyRes[i],
                    partyMov[i]
                );

                loadedStats.setCurrentMana(partyCurrentMana[i]);
                
                applyPartyMemberData(
                    partyIds[i],
                    partyLevels[i],
                    partyExps[i],
                    loadedStats
                );
            }
            
            //Reading Loop for Weapons
            for (int i = 0; i < partyCount; i++) {

                if (partyIds[i] == null) {
                    continue;
                }

                PartyMember member = getPartyMemberById(partyIds[i]);

                if (member == null) {
                    continue;
                }

                member.clearWeapons();

                for (int w = 0; w < partyWeaponCounts[i]; w++) {
                    String weaponId = partyWeaponIds[i][w];

                    if (weaponId != null && !weaponId.isEmpty()) {
                        Weapon weapon = createWeaponById(weaponId);

                        if (weapon != null) {
                            member.addWeapon(weapon);
                            
                            
                        }
                    }
                }
                
                if (member.getWeapons().isEmpty()) {
                    Weapon fallbackWeapon = createDefaultWeaponForPartyMember(member);

                    if (fallbackWeapon != null) {
                        member.addWeapon(fallbackWeapon);
                        member.equipWeapon(fallbackWeapon);
                    }
                }

                String equippedId = partyEquippedWeaponIds[i];

                if (equippedId != null && !equippedId.isEmpty()) {
                    Weapon equippedWeapon = findWeaponOnPartyMember(member, equippedId);

                    if (equippedWeapon != null) {
                        member.equipWeapon(equippedWeapon);
                    }
                }
            }
            
            currentMap = overworldGameMap;
            currentState = GameState.OVERWORLD;
            
            
            updateStoryWorldState();

            System.out.println("Game loaded.");

        } catch (IOException e) {
            System.out.println("Load failed.");
            e.printStackTrace();
        }
    }
    
    //This prevents a party member from having no weapon if something goes wrong with the save file
    private Weapon createDefaultWeaponForPartyMember(PartyMember member) {

        if (member.getId().equals("leader")) {
            return createWeaponById("iron_sword");
        }

        if (member.getId().equals("archer_ally")) {
            return createWeaponById("short_bow");
        }

        if (member.getId().equals("mage")) {
            return createWeaponById("fire_tome");
        }

        return null;
    }
    
    //method helps apply leader ID when saving and updates them
    private void applyPartyMemberData(String id, int level, int exp, UnitStats stats) {

        PartyMember member = getPartyMemberById(id);

        if (member == null) {
            System.out.println("No party member found with id: " + id);
            return;
        }

        member.setLevel(level);
        member.setExperience(exp);
        member.setStats(stats);
    }
    
    //Writes any party member instead of just listing just names
    private void writePartyMember(FileWriter writer, PartyMember member, int index) throws IOException {

        UnitStats stats = member.getStats();

        writer.write("party" + index + "Id=" + member.getId() + "\n");
        writer.write("party" + index + "Level=" + member.getLevel() + "\n");
        writer.write("party" + index + "Exp=" + member.getExperience() + "\n");

        writer.write("party" + index + "MaxHp=" + stats.getMaxHp() + "\n");
        writer.write("party" + index + "MaxMana=" + stats.getMaxMana() + "\n");
        writer.write("party" + index + "CurrentMana=" + stats.getCurrentMana() + "\n");

        writer.write("party" + index + "Str=" + stats.getStrength() + "\n");
        writer.write("party" + index + "Mag=" + stats.getMagic() + "\n");
        writer.write("party" + index + "Skl=" + stats.getSkill() + "\n");
        writer.write("party" + index + "Spd=" + stats.getSpeed() + "\n");
        writer.write("party" + index + "Lck=" + stats.getLuck() + "\n");
        writer.write("party" + index + "Def=" + stats.getDefense() + "\n");
        writer.write("party" + index + "Res=" + stats.getResistance() + "\n");
        writer.write("party" + index + "Mov=" + stats.getMovement() + "\n");

        //Loads and unloads weapons when saving and loading back in
        List<Weapon> weapons = member.getWeapons();

        writer.write("party" + index + "WeaponCount=" + weapons.size() + "\n");

        for (int i = 0; i < weapons.size(); i++) {
            writer.write("party" + index + "Weapon" + i + "=" + weapons.get(i).getId() + "\n");
        }

        if (member.getEquippedWeapon() != null) {
            writer.write("party" + index + "EquippedWeapon=" + member.getEquippedWeapon().getId() + "\n");
        }
    }
    
    //keys need to be pressed for movement
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        
        //Save
        if (code == KeyEvent.VK_S) {
            saveGame();
            repaint();
            return;
        }

        //Load
        if (code == KeyEvent.VK_L) {
            loadGame();
            repaint();
            return;
        }
        
        //Advances Chapters for testing
        if (code == KeyEvent.VK_C) {
            advanceStoryChapter(storyChapter + 1);

            if (storyChapter > 7) {
                storyChapter = 7;
            }

            repaint();
            return;
        }
        
        //Debug: Allows advancement to the prologue stage 
//        if (code == KeyEvent.VK_P) {
//            BattleScenario scenario = BattleScenarioLibrary.getScenario("prologue_ruins");
//
//            if (scenario.getIntroDialogue() != null && scenario.getIntroDialogue().length > 0) {
//                pendingBattleScenario = scenario;
//                startDialogue(scenario.getIntroDialogue(), GameState.OVERWORLD);
//            } else {
//                loadBattleScenario(scenario);
//            }
//
//            repaint();
//            return;
//        }
        
        //Exploration will delete the above later
        if (code == KeyEvent.VK_R) {
            currentMap = ruinsGameMap;
            currentState = GameState.EXPLORATION;

            player.col = 1;
            player.row = 8;

            repaint();
            return;
        }
        
        //open status screen
        if (code == KeyEvent.VK_P) {
            if (currentState == GameState.OVERWORLD ||
                currentState == GameState.TOWN ||
                currentState == GameState.EXPLORATION) {

                openStatusScreen();
                repaint();
                return;
            }
        }
        
        //Equip Swap
        if (code == KeyEvent.VK_E) {
            if (currentState == GameState.OVERWORLD ||
                currentState == GameState.TOWN ||
                currentState == GameState.EXPLORATION) {

                openEquipmentMenu();
                repaint();
                return;
            }
        }
        
        //opens Camp
        if (code == KeyEvent.VK_G) {
            if (currentState == GameState.OVERWORLD ||
                currentState == GameState.TOWN ||
                currentState == GameState.EXPLORATION) {

                openCamp();
                repaint();
                return;
            }
        }

        if (currentState == GameState.DIALOGUE) {

            if (code == KeyEvent.VK_ENTER) {
                dialogueManager.nextLine();

                if (!dialogueManager.isActive()) {
                	
                	//In
                	if (pendingBattleScenario != null) {
                        BattleScenario scenarioToLoad = pendingBattleScenario;
                        pendingBattleScenario = null;

                        loadBattleScenario(scenarioToLoad);
                        repaint();
                        return;
                    }
                	
                	//Out
                	if (pendingReturnToOverworldAfterDialogue) {
                	    returnToOverworldAfterBattle();
                	    repaint();
                	    return;
                	}

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
        
      //Status input handling
        if (currentState == GameState.STATUS) {

            if (code == KeyEvent.VK_ESCAPE) {
                currentState = statusReturnState;
                repaint();
                return;
            }

            if (partyMembers == null || partyMembers.isEmpty()) {
                currentState = statusReturnState;
                repaint();
                return;
            }

            if (code == KeyEvent.VK_UP) {
                statusMenuIndex--;

                if (statusMenuIndex < 0) {
                    statusMenuIndex = partyMembers.size() - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                statusMenuIndex++;

                if (statusMenuIndex >= partyMembers.size()) {
                    statusMenuIndex = 0;
                }

                repaint();
                return;
            }

            return;
        }
        
        //Gives the equipment menu controls to operate
        if (currentState == GameState.EQUIPMENT) {

            if (code == KeyEvent.VK_ESCAPE) {

                if (!selectingEquipmentUnit) {
                    selectingEquipmentUnit = true;
                    equipmentWeaponIndex = 0;
                } else {
                    currentState = equipmentReturnState;
                }

                repaint();
                return;
            }

            if (partyMembers == null || partyMembers.isEmpty()) {
                currentState = equipmentReturnState;
                repaint();
                return;
            }

            if (selectingEquipmentUnit) {

                if (code == KeyEvent.VK_UP) {
                    equipmentUnitIndex--;

                    if (equipmentUnitIndex < 0) {
                        equipmentUnitIndex = partyMembers.size() - 1;
                    }

                    repaint();
                    return;
                }

                if (code == KeyEvent.VK_DOWN) {
                    equipmentUnitIndex++;

                    if (equipmentUnitIndex >= partyMembers.size()) {
                        equipmentUnitIndex = 0;
                    }

                    repaint();
                    return;
                }

                if (code == KeyEvent.VK_ENTER) {
                    selectingEquipmentUnit = false;
                    equipmentWeaponIndex = 0;

                    repaint();
                    return;
                }

                return;
            }

            // Weapon selection mode
            PartyMember selectedMember = getSelectedEquipmentMember();

            if (selectedMember == null) {
                selectingEquipmentUnit = true;
                repaint();
                return;
            }

            List<Weapon> weapons = selectedMember.getWeapons();

            if (weapons == null || weapons.isEmpty()) {
                if (code == KeyEvent.VK_ENTER) {
                    selectingEquipmentUnit = true;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_UP) {
                equipmentWeaponIndex--;

                if (equipmentWeaponIndex < 0) {
                    equipmentWeaponIndex = weapons.size() - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                equipmentWeaponIndex++;

                if (equipmentWeaponIndex >= weapons.size()) {
                    equipmentWeaponIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {
                Weapon selectedWeapon = weapons.get(equipmentWeaponIndex);
                selectedMember.equipWeapon(selectedWeapon);

                System.out.println(selectedMember.getName() + " equipped " + selectedWeapon.getName());

                repaint();
                return;
            }

            return;
        }
        
        if (currentState == GameState.CAMP) {
        	
        	//Submenu for bonds
        	if (campBondMenuOpen) {

        	    List<PartyMember> bondOptions = getBondOptions();

        	    if (code == KeyEvent.VK_ESCAPE) {
        	        campBondMenuOpen = false;
        	        campBondIndex = 0;
        	        repaint();
        	        return;
        	    }

        	    if (bondOptions.isEmpty()) {
        	        campBondMenuOpen = false;
        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_UP) {
        	        campBondIndex--;

        	        if (campBondIndex < 0) {
        	            campBondIndex = bondOptions.size() - 1;
        	        }

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_DOWN) {
        	        campBondIndex++;

        	        if (campBondIndex >= bondOptions.size()) {
        	            campBondIndex = 0;
        	        }

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_ENTER) {
        	        PartyMember selected = getSelectedBondMember();

        	        if (selected != null) {
        	            startPersonalCampConversation(selected);
        	        }

        	        repaint();
        	        return;
        	    }

        	    return;
        	}

            if (code == KeyEvent.VK_UP) {
                campMenuIndex--;

                if (campMenuIndex < 0) {
                    campMenuIndex = campMenuOptions.length - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                campMenuIndex++;

                if (campMenuIndex >= campMenuOptions.length) {
                    campMenuIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {
                String selectedOption = campMenuOptions[campMenuIndex];

                if (selectedOption.equals("Rest")) {
                    restParty();
                    repaint();
                    return;
                }

                if (selectedOption.equals("Gather")) {
                    startCampGatherConversation();
                    repaint();
                    return;
                }

                if (selectedOption.equals("Bond")) {
                    campBondMenuOpen = true;
                    campBondIndex = 0;
                    repaint();
                    return;
                }

                if (selectedOption.equals("Leave")) {
                    currentState = GameState.OVERWORLD;
                    repaint();
                    return;
                }
            }

            if (code == KeyEvent.VK_ESCAPE) {
                currentState = GameState.OVERWORLD;
                repaint();
                return;
            }

            return;
        }
        
        
        //Shop Menu Control for selecting party member
        if (currentState == GameState.SHOP) {

            if (code == KeyEvent.VK_ESCAPE) {

                if (!selectingShopBuyer) {
                    selectingShopBuyer = true;
                    shopItemIndex = 0;
                } else {
                    currentState = GameState.TOWN;
                }

                repaint();
                return;
            }

            if (selectingShopBuyer) {

                if (code == KeyEvent.VK_UP) {
                    shopBuyerIndex--;

                    if (shopBuyerIndex < 0) {
                        shopBuyerIndex = partyMembers.size() - 1;
                    }

                    repaint();
                    return;
                }

                if (code == KeyEvent.VK_DOWN) {
                    shopBuyerIndex++;

                    if (shopBuyerIndex >= partyMembers.size()) {
                        shopBuyerIndex = 0;
                    }

                    repaint();
                    return;
                }

                if (code == KeyEvent.VK_ENTER) {
                    selectingShopBuyer = false;
                    shopItemIndex = 0;

                    repaint();
                    return;
                }

                return;
            }

            // Selecting items AFTER selecting who
            PartyMember buyer = getSelectedShopBuyer();
            List<ShopItem> availableItems = getShopItemsForBuyer(buyer);

            if (availableItems.isEmpty()) {
                if (code == KeyEvent.VK_ESCAPE) {
                    selectingShopBuyer = true;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_UP) {
                shopItemIndex--;

                if (shopItemIndex < 0) {
                    shopItemIndex = availableItems.size() - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                shopItemIndex++;

                if (shopItemIndex >= availableItems.size()) {
                    shopItemIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {
                buySelectedShopItem();

                repaint();
                return;
            }

            return;
        }
        

        if (code == KeyEvent.VK_ENTER) {
            if (currentState == GameState.OVERWORLD ||
                currentState == GameState.TOWN ||
                currentState == GameState.EXPLORATION) {

                interactWithTile();
                repaint();
                return;
            }
        }
        
        if (code == KeyEvent.VK_ESCAPE) {
        	
        	if (currentState == GameState.EXPLORATION) {
                currentMap = overworldGameMap;
                currentState = GameState.OVERWORLD;

                player.col = 3;
                player.row = 1;

                repaint();
                return;
            }

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

        		//First Enter is attacker
        	    if (code == KeyEvent.VK_ENTER) {

        	    	if (!zoomAttackResolved) {

        	    	    if (zoomIsSkill) {

        	    	        int manaCost = getSkillManaCost(zoomAttacker.getSkillName());

        	    	        if (!zoomAttacker.getStats().hasEnoughMana(manaCost)) {
        	    	            addBattleMessage("Not enough mana.");

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
        	    	            return;
        	    	        }

        	    	        zoomAttacker.getStats().spendMana(manaCost);
        	    	        performSkill(zoomAttacker, zoomDefender);

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

        	    	    showZoomResultText(
        	    	        zoomActionName,
        	    	        lastAttackHit,
        	    	        lastAttackCrit,
        	    	        lastAttackLuckyBreak,
        	    	        lastAttackDamage,
        	    	        zoomIsSkill
        	    	    );

        	    	    zoomAttackResolved = true;

        	    	    if (zoomDefender.isAlive() && canCounterattack(zoomAttacker, zoomDefender)) {
        	    	        zoomCounterPending = true;
        	    	    }

        	    	    repaint();
        	    	    return;
        	    	}
        	        
        	        
        	        // First Wait until counter floating text finishes before allowing close
        	        if (zoomFloatingTextTimer > 0 || zoomFloatingText2Timer > 0) {
        	            return;
        	        }
        	        
        	        
        	        // Second ENTER: defender counter attacks if pending
        	        if (zoomCounterPending && !zoomCounterResolved) {

        	            performAttack(zoomDefender, zoomAttacker);

        	            if (!zoomAttacker.isAlive()) {
        	                addBattleMessage(zoomAttacker.getName() + " was defeated!");
        	            }

        	            showZoomResultText(
        	                zoomDefender.getWeapon().getName(),
        	                lastAttackHit,
        	                lastAttackCrit,
        	                lastAttackLuckyBreak,
        	                lastAttackDamage,
        	                false
        	            );

        	            zoomCounterPending = false;
        	            zoomCounterResolved = true;
        	            zoomShowingCounter = true;

        	            repaint();
        	            return;
        	        }

        	        
        	        // Wait until counter floating text finishes before allowing close
        	        if (zoomFloatingTextTimer > 0 || zoomFloatingText2Timer > 0) {
        	            return;
        	        }

        	        // Final ENTER closes zoom scene and returns to battle map
        	        if (zoomAttackResolved) {
        	        	
        	        	if (zoomFloatingTextTimer > 0 || zoomFloatingText2Timer > 0) {
        	                return; 
        	        	}
        	        	
        	        	battleZoomCombatOpen = false;

            	        zoomAttacker = null;
            	        zoomDefender = null;
            	        zoomActionName = "";
            	        zoomIsSkill = false;
            	        zoomAttackResolved = false;
            	        
            	        zoomCounterPending = false;
            	        zoomCounterResolved = false;
            	        zoomShowingCounter = false;

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
   
        	    }

        	    return;
        	}
        	
        	//support zoom does not happen. Maybe will add in future
        	if (battleHealPreviewOpen) {

        	    if (code == KeyEvent.VK_ESCAPE) {
        	        battleHealPreviewOpen = false;
        	        battleHealTargetSelectOpen = true;

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_ENTER) {

        	        int manaCost = getSkillManaCost(healCaster.getSkillName());

        	        if (!healCaster.getStats().hasEnoughMana(manaCost)) {
        	            addBattleMessage("Not enough mana.");
        	            battleHealPreviewOpen = false;
        	            battleActionMenuOpen = true;
        	            repaint();
        	            return;
        	        }

        	        healCaster.getStats().spendMana(manaCost);

        	        int healAmount = rollHealAmount(healCaster);
        	        healTarget.heal(healAmount);

        	        addBattleMessage(healCaster.getName() + " used Heal.");
        	        addBattleMessage(healTarget.getName() + " recovered " + healAmount + " HP.");

        	        healCaster.setHasActed(true);

        	        battleHealPreviewOpen = false;
        	        battleHealTargetSelectOpen = false;

        	        healCaster = null;
        	        healTarget = null;

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

        	        openZoomCombat(skillAttacker, skillDefender, true, skillAttacker.getSkillName());

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
        	
        	//Battle Skill Heal before the menu
        	if (battleHealTargetSelectOpen) {

        	    if (code == KeyEvent.VK_ESCAPE) {
        	        battleHealTargetSelectOpen = false;
        	        battleActionMenuOpen = true;

        	        healCaster = null;
        	        healTarget = null;

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT) {
        	        currentTargetIndex--;

        	        if (currentTargetIndex < 0) {
        	            currentTargetIndex = availableTargets.size() - 1;
        	        }

        	        healCaster = selectedBattleUnit;
        	        healTarget = availableTargets.get(currentTargetIndex);

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT) {
        	        currentTargetIndex++;

        	        if (currentTargetIndex >= availableTargets.size()) {
        	            currentTargetIndex = 0;
        	        }

        	        healCaster = selectedBattleUnit;
        	        healTarget = availableTargets.get(currentTargetIndex);

        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_ENTER) {
        	        battleHealTargetSelectOpen = false;
        	        battleHealPreviewOpen = true;

        	        healCaster = selectedBattleUnit;
        	        healTarget = availableTargets.get(currentTargetIndex);

        	        repaint();
        	        return;
        	    }
        	}
        	
        	
        	//Battle Skill Attack before the menu
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

    				    String skillName = selectedBattleUnit.getSkillName();
    				    int manaCost = getSkillManaCost(skillName);

    				    if (!selectedBattleUnit.getStats().hasEnoughMana(manaCost)) {
    				        addBattleMessage("Not enough mana.");
    				        repaint();
    				        return;
    				    }

    				    if (isHealingSkill(skillName)) {

    				        availableTargets = getAlliesInRange(selectedBattleUnit, 1);

    				        if (!availableTargets.isEmpty()) {

    				            battleHealTargetSelectOpen = true;
    				            battleActionMenuOpen = false;
    				            currentTargetIndex = 0;

    				            healCaster = selectedBattleUnit;
    				            healTarget = availableTargets.get(currentTargetIndex);

    				            repaint();
    				            return;

    				        } else {
    				            addBattleMessage("No ally in range to heal.");
    				            repaint();
    				            return;
    				        }
    				    }

    				    // Non-healing skills still target enemies
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
        			
        			//selected automatically if it is in the list
        			for (BattleUnit unit : playerBattleUnits) {

        			    if (unit != null &&
        			        unit.isAlive() &&
        			        battleCursorCol == unit.getCol() &&
        			        battleCursorRow == unit.getRow() &&
        			        !unit.hasActed()) {

        			        selectedBattleUnit = unit;
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
        			
        			if (selectedBattleUnit != null) {
        		        battleCursorCol = selectedUnitStartCol;
        		        battleCursorRow = selectedUnitStartRow;
        		    } else if (!playerBattleUnits.isEmpty()) {
        		        battleCursorCol = playerBattleUnits.get(0).getCol();
        		        battleCursorRow = playerBattleUnits.get(0).getRow();
        		    }

        		    selectedBattleUnit = null;
        		    battleUnitSelected = false;
        		    selectedUnitStartCol = -1;
        		    selectedUnitStartRow = -1;
        		    
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
        		|| currentState == GameState.TOWN
        		|| currentState == GameState.EXPLORATION)
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
