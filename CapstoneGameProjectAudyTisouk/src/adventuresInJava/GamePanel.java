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

    // Screen settings for the black box Audy Tisouk After Submitting Tester 
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
    
    //Title
    private String[] titleMenuOptions = {"New Game", "Load Game", "Controls", "Exit"};
    private int titleMenuIndex = 0;
    
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
    
    //Prologue white flash 
    // Creation / story flash effect
    private int whiteFlashTimer = 0;
    private final int WHITE_FLASH_DURATION = 60;
    
    //Map popup timer
    private int mapTitleTimer = 0;
    private final int MAP_TITLE_DURATION = 180;
    
    //Temporary save/load feedback message
    private String systemMessage = "";
    private int systemMessageTimer = 0;
    private final int SYSTEM_MESSAGE_DURATION = 180;
    
	// Story transition overlay
	private String storyTransitionText = ""; //Stores text on screen
	private int storyTransitionTimer = 0; 
	private final int STORY_TRANSITION_DURATION = 180;
	private boolean pendingChapterOneStart = false; //ends
    
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
    
    // Act Two Corrupted Overworld
    private Tile[][] actTwoWorldMap;
    private GameMap actTwoWorldGameMap;
      
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
    
    //Prologue part 1
    private Tile[][] prologueForestMap;
    private GameMap prologueForestGameMap;
    
    //Chapter 1 Quest Board Flowers
    private Tile[][] flowerFieldMap;
    private GameMap flowerFieldGameMap;
    
    //Chapter 1 Checkmate safe house
    private Tile[][] safehouseMap;
    private GameMap safehouseGameMap;
    
    //Chapter 2 Ruins
    private Tile[][] chapterTwoRuinsMap;
    private GameMap chapterTwoRuinsGameMap;
    
    //SHOP
    private int gold = 100;
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
    private GameState campReturnState = GameState.OVERWORLD;
    private GameMap campReturnMap = null;
    private int campReturnCol = 0;
    private int campReturnRow = 0;
    
    //BONDS
    private boolean campBondMenuOpen = false;
    private int campBondIndex = 0;
    
    private int penelopeBond = 0;
    private int deanBond = 0;

    private int penelopeLastTalkedChapter = -1;
    private int deanLastTalkedChapter = -1;
    
    private int taliBond = 0;
    private int taliLastTalkedChapter = -1;
    

    
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
    
    private boolean inspectedOldHeroesMural = false;
    private boolean inspectedWhiteBladeMural = false;
    private boolean inspectedBrokenHourMural = false;
    
    //Prologue in steps
    //    0 = Forest path
    //    1 = Camp before ruins
    //    2 = Ruins exploration
    //    3 = Sword obtained
    //    4 = Return to village
    //    5 = Prologue complete
    private int prologueStep = 0;
    private boolean pendingPrologueReturnHome = false;
    private boolean pendingPrologueChapterOne = false;
    
    
    /*
     * CHAPTER 1 Story Progression
     */
    // Chapter 1 story flow in steps
    private int chapterOneStep = 0;
    private boolean pendingRecruitmentScene = false;
    //Chapter 1 part 2
    private boolean pendingAdventurerIdeaScene = false;
    private boolean pendingStarterJobsCompleteScene = false;
    
    // Checkmate / Golden Sinners questline
    private int checkmateStep = 0;
    private boolean oldMillRoadCompleted = false;
    
    
    //Chapter 1 part 5
    private boolean safehouseUnlocked = false;
    
    private boolean inspectedSafehouseChildren = false;
    private boolean inspectedSafehouseDoctor = false;
    private boolean inspectedSafehouseSupplies = false;
    private boolean inspectedSafehouseOrders = false;
    
    //Chpater 1 part 6
    
    private boolean pendingTaliConfrontation = false;
    private boolean inspectedKingTent = false;
    
    private boolean taliConfrontationCompleted = false;
    
    // Tali Joins up Temp
    private boolean taliTemporaryAlly = false;
    private boolean taliRecruited = false;
    
    //Chapter 1 End
    private boolean pendingChapterOneEnding = false;
    
    private boolean pendingChapterOneCamp = false;
    
    private boolean pendingAdvanceToChapterTwo = false;
    
    
    
    /*
     * QUESTS
     */
    //Tile Completion
    private int encounterSourceCol = -1;
    private int encounterSourceRow = -1;
    
    private boolean banditQuestRewardClaimed = false;
    
    //Quest Flags
    private boolean banditQuestAccepted = false;
    private boolean banditQuestCompleted = false;
    
    //Quest Accept and Confirmation
    private boolean questConfirmOpen = false;
    private int questConfirmIndex = 0;
    private String pendingQuestName = "";
    private String activeQuestName = "";
    
    //Chapter 1 Quests
    private String[] questBoardOptions = {
    	    "Cellar Rats",
    	    "Missing Laundry",
    	    "Flower Picking",
    	    "Urgent Notice",
    	    "Leave"
    	};

    private int questBoardIndex = 0;
    
    private boolean cellarRatsCompleted = false;
    private boolean laundryCompleted = false;
    private boolean flowersCompleted = false;
    
    private boolean starterJobsComplete = false;
    private boolean banditQuestUnlocked = false;
    
    //Laundry Quest 
    private int laundryCollected = 0;
    private final int LAUNDRY_REQUIRED = 3;
    
    //Flower Quest
    private int flowersCollected = 0;
    private final int FLOWERS_REQUIRED = 3;
    
    /*
     * CHAPTER 2 STARTS HERE
     */
    
    //Chapter 2 story flow Skip
    private int chapterTwoStep = 0;
    private boolean pendingChapterTwoOpening = false;
    private boolean ruinsJobUnlocked = false;
    
    private boolean pendingRuinsJobUnlock = false;
    
    //Ruins
    private boolean inspectedChapterTwoMural = false;
    private boolean inspectedChapterTwoSeal = false;
    private boolean inspectedChapterTwoRelic = false;
    
    //Chapter 2 Golem
    private boolean pendingSilasBetrayal = false;
    
    //Silas trap
    private boolean pendingSilasTrapBattle = false;
    private boolean silasBetrayalTriggered = false;
    
    private boolean golemTurn2DialogueShown = false;
    private boolean golemTurn3DialogueShown = false;
    private boolean williamArrivedForGolem = false;
    private boolean pendingWilliamGolemRescue = false;
    
    //After Goblem Fight
    private boolean williamRecruited = false;
    
    private boolean pendingWilliamRecruitmentScene = false;
    private boolean pendingActOneEnding = false;
    
    private boolean showingRuinsExteriorScene = false;
    
    //End of Act 1 Chapter 2
    private boolean pendingEndActOneTransition = false;
    
    private boolean pendingReturnAfterActOne = false;
    
    
    
    /*
     * ACT TWO / CHAPTER 3
     */

    private int chapterThreeStep = 0;

    private boolean pendingActTwoOpening = false;
    
    private boolean pendingMoveToActTwoWorld = false;
    
    private boolean pendingChapterThreeOpening = false;
    private boolean pendingCorruptedRoadMission = false;
    private boolean pendingSilasTicketScene = false;

    private boolean corruptedRoadCompleted = false;
    private boolean carnalvalUnlocked = false;
    
    //Carnalval Maps
    private Tile[][] carnalvalMainMap;
    private Tile[][] laughingLaneMap;
    private Tile[][] gildedMidwayMap;
    private Tile[][] performersRowMap;
    private Tile[][] guestLodgingMap;
    private Tile[][] mainStageMap;

    private GameMap carnalvalMainGameMap;
    private GameMap laughingLaneGameMap;
    private GameMap gildedMidwayGameMap;
    private GameMap performersRowGameMap;
    private GameMap guestLodgingGameMap;
    private GameMap mainStageGameMap;
    
    /*
     * ACT TWO / CHAPTER 4 - CARNALVAL
     */

    private int chapterFourStep = 0;

    private boolean carnalvalEntered = false;
    private boolean pipIntroduced = false;
    private boolean silasWelcomeSeen = false;
    private boolean carnalvalExitDiscovered = false;
    private boolean lodgingUnlocked = false;
    private boolean mainStageUnlocked = false;

    private boolean laughingLaneVisited = false;
    private boolean gildedMidwayVisited = false;
    private boolean performersRowVisited = false;
    private boolean guestLodgingVisited = false;
    
    
    
    
    
    
    

    	
    	
    
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
    private BattleScenario pendingScenarioIntroAfterQuestAccept = null;
    
    //This is for post battle dialogue and possibly scenes as well
    private boolean pendingReturnToOverworldAfterDialogue = false;
    
    //Moves to flower field
    private boolean pendingFlowerFieldStart = false;
    
    //DEFEAT
    private String defeatMessage = "You were defeated...";
    private GameState defeatReturnState = GameState.OVERWORLD;
    private String[] defeatOptions = {"Retry Battle", "Return to Overworld"};
    private int defeatMenuIndex = 0;
    private BattleScenario lastBattleScenario = null;
    
    private GameMap battleReturnMap = null;
    private GameState battleReturnState = GameState.OVERWORLD;
    private int battleReturnCol = 0;
    private int battleReturnRow = 0;
    
    
    
    
    /*
     * GAMESTATES
     */
    
    //Current State of Game
    //Now starts on title!
    private GameState currentState = GameState.TITLE;

    //Game States that can be switched into from a button or tile
	private enum GameState {
		TITLE,
		CONTROLS,
		OVERWORLD,
		TOWN,
		BATTLE,
		DIALOGUE,
		SHOP,
		EXPLORATION,
		EQUIPMENT,
		STATUS,
		CAMP,
		QUEST_BOARD,
		DEFEAT,
		ACT_ONE_END
		
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
        generatePrologueForestMap();
        generateFlowerFieldMap();
        generateSafehouseMap();
        generateChapterTwoRuinsMap();
        generateActTwoWorld();
        generateCarnalvalMaps();
        
       
    }
    
    private void generateCarnalvalMaps() {

        generateCarnalvalMainMap();
        generateLaughingLaneMap();
        generateGildedMidwayMap();
        generatePerformersRowMap();
        generateGuestLodgingMap();
        generateMainStageMap();
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
    	//For all event types for now
    	else if (type == TileType.EVENT) {
    	    return "Something here can be inspected.";
    	}
    	
    	//Quest Board
    	else if (type == TileType.QUEST_BOARD) {
    	    return "A board covered in local requests.";
    	}
    	
    	//Laundry
    	else if (type == TileType.LAUNDRY) {
    	    return "Scattered laundry. Press ENTER to collect it.";
    	}
    	
    	//Flowers
    	else if (type == TileType.FLOWER) {
    	    return "Medicinal flowers. Press ENTER to pick them.";
    	}
    	
    	//Dead Grass
    	else if (type == TileType.DEAD_GRASS) {
    	    return "Dry, colorless grass. It crunches underfoot like old paper.";
    	}
    	//Dead Trees
    	else if (type == TileType.DEAD_FOREST) {
    	    return "A dead forest. The trees bend inward like they are listening.";
    	}
    	//Old Roads
    	else if (type == TileType.CRACKED_ROAD) {
    	    return "A cracked road stained by gray rain.";
    	}
    	
    	//Evil Water
    	else if (type == TileType.CORRUPTED_WATER) {
    	    return "Dark water with a violet sheen. Best not to touch it.";
    	}
    	
    	else if (type == TileType.CARNIVAL_FLOOR) {
    	    return "Painted carnival ground. The colors seem too bright for the gray sky.";
    	}
    	
    	else if (type == TileType.CARNIVAL_PATH) {
    	    return "A worn carnival path lined with strange footprints.";
    	}
    	
    	else if (type == TileType.CARNIVAL_TENT) {
    	    return "A striped tent. Music hums softly from inside.";
    	}
    	
    	else if (type == TileType.CARNIVAL_BOOTH) {
    	    return "A carnival booth waiting for guests.";
    	}
    	
    	else if (type == TileType.CARNIVAL_GATE) {
    	    return "A decorative gate painted red and gold.";
    	}
    	
    	else if (type == TileType.CARNIVAL_STAGE) {
    	    return "A stage entrance. The final performance is not ready.";
    	}
    	
    	else if (type == TileType.CARNIVAL_LIGHTS) {
    	    return "Lanterns glow without flame.";
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
    		updateTownQuestTiles();
    		updateOverworldQuestTiles();
    		showMapTitle();
    		
    	}	
    	
    }
    
    
    
    //Act 2 Region Map
    private void generateActTwoWorld() {

        actTwoWorldMap = new Tile[10][10];

        int[][] layout = {
            {3,3,3,3,1,1,3,3,3,3},
            {3,0,0,2,2,2,0,0,0,3},
            {3,0,1,1,2,0,0,1,0,3},
            {3,0,0,1,2,2,0,1,0,3},
            {3,1,0,0,0,2,0,0,0,3},
            {3,1,1,0,0,2,2,1,0,3},
            {3,0,0,0,1,1,2,0,0,3},
            {3,0,1,0,0,0,2,0,1,3},
            {3,0,0,0,1,0,2,0,0,3},
            {3,3,3,3,3,3,3,3,3,3}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    actTwoWorldMap[col][row] = new Tile(TileType.DEAD_GRASS);
                }
                else if (value == 1) {
                    actTwoWorldMap[col][row] = new Tile(TileType.DEAD_FOREST);
                }
                else if (value == 2) {
                    actTwoWorldMap[col][row] = new Tile(TileType.CRACKED_ROAD);
                }
                else if (value == 3) {
                    actTwoWorldMap[col][row] = new Tile(TileType.CORRUPTED_WATER);
                }
            }
        }

        actTwoWorldGameMap = new GameMap(actTwoWorldMap, "The Withered Roads");

        updateActTwoWorldQuestTiles();
    }
    
    
    
    
    
    
    
    //Map for Town
    private void generateTown() {
    	
    	townMap = new Tile[10][10];
    	
    	int[][] layout = {

    	        {1,1,1,1,1,1,1,1,1,1},
    	        {1,0,0,0,4,0,0,0,0,1},
    	        {1,0,0,0,0,0,0,0,0,1},
    	        {1,0,0,3,0,3,0,5,0,1},
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
    			else if (value == 5) {
    				townMap[col][row] = new Tile(TileType.QUEST_BOARD);
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
            {1,0,1,1,0,3,1,1,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,0,3,2,0,0,0,0,1},
            {1,0,0,0,0,0,3,0,0,1},
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
                
                //Event Inspectables
                else if (value == 3) {
                    Tile eventTile = new Tile(TileType.EVENT);

                    if (col == 5 && row == 2) {
                        eventTile.setEventId("mural_old_heroes");
                    }
                    else if (col == 3 && row == 4) {
                        eventTile.setEventId("mural_white_blade");
                    }
                    else if (col == 6 && row == 5) {
                        eventTile.setEventId("mural_broken_hour");
                    }

                    ruinsMap[col][row] = eventTile;
                }
            }
        }

        ruinsGameMap = new GameMap(ruinsMap, "Ancient Ruins");
    }
    
    //Prologue map is a winding path with many trees around and about to a unknown location
    private void generatePrologueForestMap() {

        prologueForestMap = new Tile[10][10];

        int[][] layout = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,3,3,0,0,0,0,3,3,1},
            {1,3,0,0,4,4,0,0,3,1},
            {1,0,0,3,3,4,4,0,0,1},
            {1,0,3,3,0,0,4,4,0,1},
            {1,0,0,0,0,3,3,4,0,1},
            {1,3,3,0,0,0,0,4,0,1},
            {1,3,0,0,3,3,0,4,0,1},
            {1,0,0,0,0,0,0,4,2,1},
            {1,1,1,1,1,1,1,1,1,1}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    prologueForestMap[col][row] = new Tile(TileType.GRASS);
                }
                else if (value == 1) {
                    prologueForestMap[col][row] = new Tile(TileType.FOREST);
                }
                else if (value == 2) {
                    prologueForestMap[col][row] = new Tile(TileType.EXIT);
                }
                else if (value == 3) {
                    prologueForestMap[col][row] = new Tile(TileType.FOREST);
                }
                else if (value == 4) {
                    prologueForestMap[col][row] = new Tile(TileType.ROAD);
                }
            }
        }

        prologueForestGameMap = new GameMap(prologueForestMap, "Cerebella Forest Path");
    }
    
    //Flower Quest Chapter 1
    private void generateFlowerFieldMap() {

        flowerFieldMap = new Tile[10][10];

        int[][] layout = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,3,0,0,0,0,1},
            {1,0,1,0,0,0,1,0,0,1},
            {1,0,0,0,0,3,0,0,1,1},
            {1,0,1,1,0,0,0,0,0,1},
            {1,0,0,0,0,1,1,0,0,1},
            {1,0,3,0,0,0,0,0,0,1},
            {1,0,0,0,1,0,0,1,0,1},
            {1,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    flowerFieldMap[col][row] = new Tile(TileType.GRASS);
                }
                else if (value == 1) {
                    flowerFieldMap[col][row] = new Tile(TileType.FOREST);
                }
                else if (value == 2) {
                    flowerFieldMap[col][row] = new Tile(TileType.EXIT);
                }
                else if (value == 3) {
                    flowerFieldMap[col][row] = new Tile(TileType.FLOWER);
                }
            }
        }

        flowerFieldGameMap = new GameMap(flowerFieldMap, "Forest Edge");
        
    }
    
    
    
    private void generateSafehouseMap() {

        safehouseMap = new Tile[10][10];

        int[][] layout = {
            {1,1,1,1,1,1,1,1,1,3},
            {1,0,0,0,3,0,0,0,0,1},
            {1,0,3,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,3,1},
            {1,0,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    safehouseMap[col][row] = new Tile(TileType.GRASS);
                }
                else if (value == 1) {
                    safehouseMap[col][row] = new Tile(TileType.FOREST);
                }
                else if (value == 2) {
                    safehouseMap[col][row] = new Tile(TileType.EXIT);
                }
                else if (value == 3) {
                    Tile eventTile = new Tile(TileType.EVENT);

                    if (col == 4 && row == 1) {
                        eventTile.setEventId("safehouse_children");
                    }
                    else if (col == 2 && row == 2) {
                        eventTile.setEventId("safehouse_doctor");
                    }
                    else if (col == 8 && row == 3) {
                        eventTile.setEventId("safehouse_supplies");
                    }
                    else if (col == 3 && row == 4) {
                        eventTile.setEventId("safehouse_guard");
                    }
                    else if (col == 5 && row == 4) {
                        eventTile.setEventId("safehouse_orders");
                    }
                    else if (col == 9 && row == 0) {
                        eventTile.setEventId("safehouse_king_tent");
                    }
                    
                    
                    else {
                        eventTile.setEventId("safehouse_camp_life");
                    }

                    safehouseMap[col][row] = eventTile;
                }
            }
        }

        safehouseGameMap = new GameMap(safehouseMap, "Golden Sinners Safehouse");
        
    }
    
    //Ruins Chapter 2 
    //Exploration
    private void generateChapterTwoRuinsMap() {

        chapterTwoRuinsMap = new Tile[10][10];

        int[][] layout = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,3,0,1,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,0,0,3,0,0,0,0,1},
            {1,0,0,0,0,0,3,0,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,1,1,0,0,1,1,0,1},
            {1,0,0,0,0,0,0,0,2,1},
            {1,1,1,1,1,1,1,1,1,1}
        };

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    chapterTwoRuinsMap[col][row] = new Tile(TileType.RUINS_FLOOR);
                }
                else if (value == 1) {
                    chapterTwoRuinsMap[col][row] = new Tile(TileType.STONE_WALL);
                }
                else if (value == 2) {
                    chapterTwoRuinsMap[col][row] = new Tile(TileType.EXIT);
                }
                else if (value == 3) {
                    Tile eventTile = new Tile(TileType.EVENT);

                    if (col == 5 && row == 2) {
                        eventTile.setEventId("chapter2_mural_creation");
                    }
                    else if (col == 4 && row == 4) {
                        eventTile.setEventId("chapter2_magic_seal");
                    }
                    else if (col == 6 && row == 5) {
                        eventTile.setEventId("chapter2_relic_pedestal");
                    }

                    chapterTwoRuinsMap[col][row] = eventTile;
                }
            }
        }

        chapterTwoRuinsGameMap = new GameMap(chapterTwoRuinsMap, "Old Relic Ruins");
    }
    
    
    //ACT 2 CHAPTER 4 CARNIVEL MAPS
    //MAIN AREA
    private void generateCarnalvalMainMap() {

        carnalvalMainMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,3,3,0,0,0,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,1,2,0,0,0,0,2,1,0},
        	    {0,1,0,0,8,6,0,0,1,0},
        	    {4,1,0,6,1,1,6,0,1,5},
        	    {4,1,0,6,1,1,6,0,1,5},
        	    {0,1,0,0,6,6,0,0,1,0},
        	    {0,1,2,0,0,0,0,2,1,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,0,0,0,9,7,0,0,0,0}
        	};

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    carnalvalMainMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    carnalvalMainMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    carnalvalMainMap[col][row] = new Tile(TileType.CARNIVAL_LIGHTS);
                }
                else if (value == 3) {
                    carnalvalMainMap[col][row] = createCarnalvalTransitionTile("to_main_stage");
                }
                else if (value == 4) {
                    carnalvalMainMap[col][row] = createCarnalvalTransitionTile("to_laughing_lane");
                }
                else if (value == 5) {
                    carnalvalMainMap[col][row] = createCarnalvalTransitionTile("to_gilded_midway");
                }
                else if (value == 6) {
                    carnalvalMainMap[col][row] = new Tile(TileType.CARNIVAL_BOOTH);
                }
                else if (value == 7) {
                    carnalvalMainMap[col][row] = createCarnalvalTransitionTile("to_guest_lodging");
                }
                
                else if (value == 8) {
                    carnalvalMainMap[col][row] = createCarnalvalEventTile("carnalval_rules_sign");
                }
                
                else if (value == 9) {
                    carnalvalMainMap[col][row] = createCarnalvalEventTile("carnalval_exit_loop");
                }
            }
        }

        carnalvalMainGameMap = new GameMap(carnalvalMainMap, "Main Carnival Grounds");
        
    }
    
    //Laughing Lanes
    private void generateLaughingLaneMap() {

        laughingLaneMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,0,0,0,0,0,0},
        	    {0,2,1,1,1,1,1,2,0,0},
        	    {0,1,3,0,1,0,3,1,0,0},
        	    {0,1,0,0,1,0,0,1,0,0},
        	    {0,1,1,1,1,1,1,1,0,4},
        	    {0,1,3,0,1,0,3,1,0,4},
        	    {0,1,0,0,1,0,0,1,0,0},
        	    {0,2,1,1,1,1,1,2,0,0},
        	    {0,0,0,0,0,0,0,0,0,0},
        	    {0,0,0,0,5,5,0,0,0,0}
        	};
        
        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    laughingLaneMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    laughingLaneMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    laughingLaneMap[col][row] = new Tile(TileType.CARNIVAL_LIGHTS);
                }
                else if (value == 3) {
                    laughingLaneMap[col][row] = new Tile(TileType.CARNIVAL_BOOTH);
                }
                else if (value == 4) {
                    laughingLaneMap[col][row] = createCarnalvalTransitionTile("to_carnalval_main");
                }
                else if (value == 5) {
                    laughingLaneMap[col][row] = createCarnalvalTransitionTile("to_performers_row");
                }
            }
        }

        laughingLaneGameMap = new GameMap(laughingLaneMap, "Laughing Lane");
        
    }
    
    //Gilded Midway
    private void generateGildedMidwayMap() {

        gildedMidwayMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,2,2,0,0,0,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,0,2,1,1,2,0,1,0},
        	    {4,1,1,1,1,1,1,1,1,0},
        	    {4,1,1,2,1,1,2,1,1,0},
        	    {0,1,0,0,0,0,0,0,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,0,0,0,5,5,0,0,0,0}
        	};

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    gildedMidwayMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    gildedMidwayMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    gildedMidwayMap[col][row] = new Tile(TileType.CARNIVAL_LIGHTS);
                }
                else if (value == 3) {
                    gildedMidwayMap[col][row] = new Tile(TileType.CARNIVAL_TENT);
                }
                else if (value == 4) {
                    gildedMidwayMap[col][row] = createCarnalvalTransitionTile("to_carnalval_main");
                }
                else if (value == 5) {
                    gildedMidwayMap[col][row] = createCarnalvalTransitionTile("to_performers_row");
                }
            }
        }

        gildedMidwayGameMap = new GameMap(gildedMidwayMap, "Gilded Midway");
    }
    
    
    //Performers Row
    private void generatePerformersRowMap() {

        performersRowMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,0,0,0,0,0,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,0,0,2,2,0,0,1,0},
        	    {4,1,0,3,1,1,3,0,1,5},
        	    {4,1,0,3,1,1,3,0,1,5},
        	    {0,1,0,0,2,2,0,0,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,0,0,0,0,0,0,0,0,0}
        	};

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    performersRowMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    performersRowMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    performersRowMap[col][row] = new Tile(TileType.CARNIVAL_LIGHTS);
                }
                else if (value == 3) {
                    performersRowMap[col][row] = new Tile(TileType.CARNIVAL_TENT);
                }
                else if (value == 4) {
                    performersRowMap[col][row] = createCarnalvalTransitionTile("to_laughing_lane");
                }
                else if (value == 5) {
                    performersRowMap[col][row] = createCarnalvalTransitionTile("to_gilded_midway");
                }
            }
        }

        performersRowGameMap = new GameMap(performersRowMap, "Performer’s Row");
        
    }
    
    //Guest Lodging- Camping Area
    private void generateGuestLodgingMap() {

        guestLodgingMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,4,4,0,0,0,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,0,0,2,2,0,0,1,0},
        	    {0,1,0,3,1,1,3,0,1,0},
        	    {0,1,0,3,1,1,3,0,1,0},
        	    {0,1,0,0,2,2,0,0,1,0},
        	    {0,1,3,0,0,0,0,3,1,0},
        	    {0,1,1,1,1,1,1,1,1,0},
        	    {0,0,0,0,0,0,0,0,0,0}
        	};

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    guestLodgingMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    guestLodgingMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    guestLodgingMap[col][row] = new Tile(TileType.CARNIVAL_LIGHTS);
                }
                else if (value == 3) {
                    guestLodgingMap[col][row] = new Tile(TileType.CARNIVAL_TENT);
                }
                else if (value == 4) {
                    guestLodgingMap[col][row] = createCarnalvalTransitionTile("to_carnalval_main");
                }
            }
        }

        guestLodgingGameMap = new GameMap(guestLodgingMap, "Guest Lodging");
    }
    
    
    //Final Act Main Stage
    //Cannot Be accessed until they unnlock the stage area
    private void generateMainStageMap() {

        mainStageMap = new Tile[10][10];

        int[][] layout = {
        	    {0,0,0,0,0,0,0,0,0,0},
        	    {0,3,3,3,3,3,3,3,3,0},
        	    {0,3,1,1,1,1,1,1,3,0},
        	    {0,3,1,2,2,2,2,1,3,0},
        	    {0,3,1,2,2,2,2,1,3,0},
        	    {0,3,1,2,2,2,2,1,3,0},
        	    {0,3,1,2,2,2,2,1,3,0},
        	    {0,3,1,1,1,1,1,1,3,0},
        	    {0,0,0,0,0,0,0,0,0,0},
        	    {0,0,0,0,4,4,0,0,0,0}
        	};

        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {

                int value = layout[row][col];

                if (value == 0) {
                    mainStageMap[col][row] = new Tile(TileType.CARNIVAL_FLOOR);
                }
                else if (value == 1) {
                    mainStageMap[col][row] = new Tile(TileType.CARNIVAL_PATH);
                }
                else if (value == 2) {
                    mainStageMap[col][row] = new Tile(TileType.CARNIVAL_STAGE);
                }
                else if (value == 3) {
                    mainStageMap[col][row] = new Tile(TileType.CARNIVAL_TENT);
                }
                else if (value == 4) {
                    mainStageMap[col][row] = createCarnalvalTransitionTile("to_carnalval_main");
                }
            }
        }

        mainStageGameMap = new GameMap(mainStageMap, "Main Stage");
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

    		startDialogue("", new String[] {
    			    "Welcome to the town.",
    			    "We appreciate your stay."
    			}, GameState.TOWN, townGameMap, 5, 8);
    	    
    	    return;
    	}
    	
    	//Enemy
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
    	
    	//Event
    	else if (tile == TileType.EVENT) {

    	    Tile currentTile = currentMap.getTiles()[player.col][player.row];
    	    
    	    String eventId = currentTile.getEventId();

    	    if (eventId != null && eventId.startsWith("to_")) {
    	        handleCarnalvalTransition(eventId);
    	        return;
    	    }

    	    if ("enter_safehouse".equals(currentTile.getEventId())) {
    	        enterSafehouse();
    	        return;
    	    }
    	    
    	    if ("enter_chapter_two_ruins".equals(currentTile.getEventId())) {
    	        enterChapterTwoRuins();
    	        return;
    	    }
    	    
    	    if ("enter_carnalval_gate".equals(currentTile.getEventId())) {
    	        enterCarnalvalGate();
    	        return;
    	    }
    	    
    	    
    	    
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
    	
    	NPC standingNpc = getNpcAt(player.col, player.row);

    	if (standingNpc != null) {
    	    interactWithNpc(standingNpc);
    	    return;
    	}

    	NPC adjacentNpc = getAdjacentNpc();

    	if (adjacentNpc != null) {
    	    interactWithNpc(adjacentNpc);
    	    return;
    	}
    	
    	if (tile == TileType.LAUNDRY) {
            collectLaundry();
            return;
        }
    	
    	if (tile == TileType.EXIT) {
    		currentMap = overworldGameMap;
    		showMapTitle();
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
    	
    	if (tile == TileType.QUEST_BOARD) {
    	    currentState = GameState.QUEST_BOARD;
    	    questBoardIndex = 0;
    	    return;
    	}
    	
    	if (tile == TileType.GRASS) {
    		System.out.println("There is nothing here.");
    	}
    }
    
    //Carnivel Only Event Tile to transition
    private Tile createCarnalvalTransitionTile(String eventId) {

        Tile tile = new Tile(TileType.EVENT);
        tile.setEventId(eventId);

        return tile;
    }
    
    private Tile createCarnalvalEventTile(String eventId) {

        Tile tile = new Tile(TileType.EVENT);
        tile.setEventId(eventId);

        return tile;
    }
    
    //Transitions from map to map in the carnivel chapter here
    private void handleCarnalvalTransition(String eventId) {

    	if (eventId.equals("to_carnalval_main")) {
    	    currentMap = carnalvalMainGameMap;
    	    showMapTitle();
    	    player.col = 5;
    	    player.row = 8;
    	}
        
        else if (eventId.equals("to_laughing_lane")) {
            currentMap = laughingLaneGameMap;
            showMapTitle();
            player.col = 8;
            player.row = 4;
            laughingLaneVisited = true;
        }
        
        else if (eventId.equals("to_gilded_midway")) {
            currentMap = gildedMidwayGameMap;
            showMapTitle();
            player.col = 1;
            player.row = 4;
            gildedMidwayVisited = true;
        }
        
        else if (eventId.equals("to_performers_row")) {
            currentMap = performersRowGameMap;
            showMapTitle();
            player.col = 5;
            player.row = 1;
            performersRowVisited = true;
        }
        
        else if (eventId.equals("to_guest_lodging")) {
            currentMap = guestLodgingGameMap;
            showMapTitle();
            player.col = 5;
            player.row = 1;
            guestLodgingVisited = true;
        }
        
        else if (eventId.equals("to_main_stage")) {

            if (!mainStageUnlocked) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("", "The Main Stage doors are sealed by red-and-gold chains.",
                            DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("", "A painted sign reads: \"Final Performance Not Yet Prepared.\"",
                            DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("Dean", "That is either good news or extremely bad news.",
                            DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Tali", "Come on Dean, in this place? Bad.",
                            DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            currentMap = mainStageGameMap;
            showMapTitle();
            player.col = 5;
            player.row = 8;
        }

        currentState = GameState.EXPLORATION;
        movementLeft = maxMovement;
        repaint();
    }
    
    //Campsite
    private void openCamp() {
    	campReturnState = currentState;
        campReturnMap = currentMap;
        campReturnCol = player.col;
        campReturnRow = player.row;

        currentState = GameState.CAMP;
        campMenuIndex = 0;
    }
    
    private void openCampWithReturn(GameState returnState, GameMap returnMap, int returnCol, int returnRow) {
        campReturnState = returnState;
        campReturnMap = returnMap;
        campReturnCol = returnCol;
        campReturnRow = returnRow;

        currentState = GameState.CAMP;
        campMenuIndex = 0;
    }
    
    //new for exploration tiles
    private void interactInExploration(TileType tile) {
    	
    	if (tile == TileType.EXIT) {

            if (currentMap == safehouseGameMap) {
                currentMap = overworldGameMap;
                showMapTitle();
                currentState = GameState.OVERWORLD;
                player.col = 1;
                player.row = 7;
                return;
            }

            if (currentMap == flowerFieldGameMap) {
                currentMap = townGameMap;
                showMapTitle();
                currentState = GameState.TOWN;
                player.col = 5;
                player.row = 8;
                return;
            }

            if (currentMap == chapterTwoRuinsGameMap) {
                currentMap = overworldGameMap;
                showMapTitle();
                currentState = GameState.OVERWORLD;
                player.col = 6;
                player.row = 8;
                return;
            }

            if (storyChapter == 0 && prologueStep == 0) {
                startPrologueCamp();
                return;
            }

            if (storyChapter == 0 && prologueStep == 4) {
                completePrologue();
                return;
            }

            System.out.println("There is nowhere to go right now.");
            return;
        }

        if (tile == TileType.FLOWER) {
            collectFlower();
            return;
        }

        if (tile == TileType.PEDESTAL) {
            triggerCreationSwordEvent();
            return;
        }

        if (tile == TileType.EVENT) {

        	Tile currentTile = currentMap.getTiles()[player.col][player.row];
            String eventId = currentTile.getEventId();

            if (eventId != null && eventId.startsWith("to_")) {
                handleCarnalvalTransition(eventId);
                return;
            }

            if ("enter_carnalval_gate".equals(eventId)) {
                enterCarnalvalGate();
                return;
            }

            if (isCarnalvalMap(currentMap)) {
                handleCarnalvalEvent(eventId);
                return;
            }

            handleExplorationEvent(eventId);
            return;
        }

        if (isCarnalvalMap(currentMap)) {
            showCarnalvalEmptyInteraction();
            return;
        }

        System.out.println("There is nothing to inspect here.");
        
    }
    
    
    //Title handler
    private void handleTitleMenuSelection() {

        String selected = titleMenuOptions[titleMenuIndex];

        if (selected.equals("New Game")) {
            startNewGame();
            return;
        }

        if (selected.equals("Load Game")) {
            loadGame();
            return;
        }
        
        if (selected.equals("Controls")) {
            currentState = GameState.CONTROLS;
            return;
        }

        if (selected.equals("Exit")) {
            System.exit(0);
        }
    }
    
    //THE GRAND RESET!//THE GRAND RESET!
    private void startNewGame() {

        // Basic campaign reset
        storyChapter = 0;
        prologueStep = 0;
        day = 1;
        gold = 0;

        // Quest reset
        banditQuestAccepted = false;
        banditQuestCompleted = false;
        banditQuestRewardClaimed = false;

        cellarRatsCompleted = false;
        laundryCompleted = false;
        flowersCompleted = false;
        activeQuestName = "";

        starterJobsComplete = false;
        banditQuestUnlocked = false;

        // Checkmate reset
        checkmateStep = 0;
        oldMillRoadCompleted = false;
        safehouseUnlocked = false;
        taliTemporaryAlly = false;
        taliRecruited = false;
        taliConfrontationCompleted = false;

        // Story item reset
        hasCreationSword = false;
        creationAwakened = false;

        // Camp bond reset
        penelopeBond = 0;
        deanBond = 0;
        taliBond = 0;

        penelopeLastTalkedChapter = -1;
        deanLastTalkedChapter = -1;
        taliLastTalkedChapter = -1;

        // Chapter 2 reset
        chapterTwoStep = 0;
        ruinsJobUnlocked = false;
        williamRecruited = false;

        // Recreate base party
        createPartyMembers();

        // Rebuild world state
        generateWorld();
        generateTown();
        generateRuinsMap();
        generatePrologueForestMap();
        generateFlowerFieldMap();
        generateSafehouseMap();
        generateChapterTwoRuinsMap();

        updateStoryWorldState();
        updateTownQuestTiles();

        // Start the story
        startPrologue();
        
    }
    //THE GRAND RESET!
    
    
    //Builds title screen
    private void drawTitle(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        Font originalFont = g.getFont();

        
        // Title
        g.setColor(Color.WHITE);
        g.setFont(originalFont.deriveFont(40f));

        String title = "Lost Time: Rewrite";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, 170);

        
        // Subtitle
        g.setFont(originalFont.deriveFont(16f));
        String subtitle = "Act One";
        int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (screenWidth - subtitleWidth) / 2, 205);

        // Menu
        g.setFont(originalFont.deriveFont(22f));

        int menuY = 290;

        
        for (int i = 0; i < titleMenuOptions.length; i++) {

            if (i == titleMenuIndex) {
                g.setColor(Color.YELLOW);
            } else {
            	
                g.setColor(Color.WHITE);
            }

            String prefix = (i == titleMenuIndex) ? "> " : "  ";
            String option = prefix + titleMenuOptions[i];

            int optionWidth = g.getFontMetrics().stringWidth(option);
            g.drawString(option, (screenWidth - optionWidth) / 2, menuY + (i * 40));
            
        }

        // Footer
        g.setFont(originalFont.deriveFont(14f));
        g.setColor(Color.GRAY);

        String footer = "UP/DOWN select | ENTER confirm";
        int footerWidth = g.getFontMetrics().stringWidth(footer);
        g.drawString(footer, (screenWidth - footerWidth) / 2, screenHeight - 50);

        g.setFont(originalFont);
        
    }
    
    
    //Draws the controls for the screen on title
    private void drawControls(Graphics g) {

    	
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        
        Font originalFont = g.getFont();

        g.setColor(Color.WHITE);
        g.setFont(originalFont.deriveFont(32f));

        String title = "Controls";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, 80);

        g.setFont(originalFont.deriveFont(16f));

        
        int x = 170;
        int y = 140;
        int lineHeight = 28;

        g.drawString("Arrow Keys  - Move / Navigate menus", x, y);
        g.drawString("ENTER       - Confirm / Interact / Advance dialogue", x, y + lineHeight);
        g.drawString("ESC         - Back / Cancel", x, y + lineHeight * 2);

        
        g.drawString("E           - Equipment menu", x, y + lineHeight * 4);
        g.drawString("Q         - Party status screen", x, y + lineHeight * 5);
        g.drawString("F           - Camp menu", x, y + lineHeight * 6);

        //Important
        g.drawString("S           - Save game", x, y + lineHeight * 8);
        g.drawString("L           - Load game", x, y + lineHeight * 9);

        g.setColor(Color.GRAY);
        g.drawString("Press ENTER or ESC to return to title.", x, screenHeight - 70);

        g.setFont(originalFont);
        
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
    	
    	case TITLE:
    	    updateTitle();
    	    break;
    	    
    	case CONTROLS:
    	    updateControls();
    	    break;
    	
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
    	    
    	case QUEST_BOARD:
    	    updateQuestBoard();
    	    break;
    	    
    	case DEFEAT:
    	    updateQuestBoard();
    	    break;
    	    
    	case ACT_ONE_END:
    		break;
    	    
    	}
    	
    	
    	
    	//Timer each time an end turn occurs the banner will appear 
    	if(dayBannerTimer > 0) {
    	    dayBannerTimer--;
    	}
    	
    	//White flash for Creation
    	if (whiteFlashTimer > 0) {
    	    whiteFlashTimer--;
    	}
    	
    	//Location pop
    	if (mapTitleTimer > 0) {
    	    mapTitleTimer--;
    	}
    	
    	//Temporary system message
    	if (systemMessageTimer > 0) {
    	    systemMessageTimer--;

    	    if (systemMessageTimer == 0) {
    	        systemMessage = "";
    	    }
    	}
    	
    	//Years Later
    	if (storyTransitionTimer > 0) {
    	    storyTransitionTimer--;

    	    if (storyTransitionTimer == 0) {
    	        finishStoryTransition();
    	    }
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
    

    private void updateTitle() {
    	
    }
    
    private void updateControls() {
       
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
    
    private void updateQuestBoard() {
    	
    }
    
    //Allows quests to change the tiles of into a quest marker and back
    private void updateOverworldQuestTiles() {

        if (overworldGameMap == null) return;

        Tile[][] tiles = overworldGameMap.getTiles();

        // Example quest encounter tile location
        int questCol = 7;
        int questRow = 2;
        
        // Old Mill Road location
        int millCol = 8;
        int millRow = 6;
        
        //Safe house
        int safehouseCol = 1;
        int safehouseRow = 7;
        
        //Cael Fight
        int caelCol = 2;
        int caelRow = 7;
        
        //Chap2 Ruins
        int ruinsCol = 6;
        int ruinsRow = 8;
        

        //bandit ambush
        if (banditQuestAccepted && !banditQuestCompleted) {
            Tile questTile = new Tile(TileType.ENEMY);
            questTile.setScenarioId("forest_ambush");
            tiles[questCol][questRow] = questTile;

        } else {
            tiles[questCol][questRow] = new Tile(TileType.GRASS);
        }
        
        
        
        //Old Mill Road appears after the elder reveals the next lead
        if (checkmateStep == 3) {
            Tile oldMillTile = new Tile(TileType.ENEMY);
            oldMillTile.setScenarioId("old_mill_road");
            tiles[millCol][millRow] = oldMillTile;
        } else {
            if (tiles[millCol][millRow] != null &&
                tiles[millCol][millRow].getType() == TileType.ENEMY) {

                tiles[millCol][millRow] = new Tile(TileType.GRASS);
            }
        }
        
        //Safe house
        if (safehouseUnlocked && checkmateStep >= 4 && checkmateStep < 5) {
            Tile safehouseTile = new Tile(TileType.EVENT);
            safehouseTile.setEventId("enter_safehouse");
            tiles[safehouseCol][safehouseRow] = safehouseTile;
            
        } else {
        	
            if (tiles[safehouseCol][safehouseRow] != null &&
                tiles[safehouseCol][safehouseRow].getType() == TileType.EVENT &&
                "enter_safehouse".equals(tiles[safehouseCol][safehouseRow].getEventId())) {

                tiles[safehouseCol][safehouseRow] = new Tile(TileType.GRASS);
            }
        }
        
        //Cael Fight 
        if (checkmateStep == 6 && taliTemporaryAlly && !taliRecruited) {
            Tile caelTile = new Tile(TileType.ENEMY);
            caelTile.setScenarioId("cael_usurper");
            tiles[caelCol][caelRow] = caelTile;
        } else {
            if (tiles[caelCol][caelRow] != null &&
                tiles[caelCol][caelRow].getType() == TileType.ENEMY &&
                "cael_usurper".equals(tiles[caelCol][caelRow].getScenarioId())) {

                tiles[caelCol][caelRow] = new Tile(TileType.GRASS);
            }
        }
        
        //Chap 2 ruins
        if (ruinsJobUnlocked && chapterTwoStep == 2) {
            Tile ruinsTile = new Tile(TileType.EVENT);
            ruinsTile.setEventId("enter_chapter_two_ruins");
            tiles[ruinsCol][ruinsRow] = ruinsTile;
        } else {
            if (tiles[ruinsCol][ruinsRow] != null &&
                tiles[ruinsCol][ruinsRow].getType() == TileType.EVENT &&
                "enter_chapter_two_ruins".equals(tiles[ruinsCol][ruinsRow].getEventId())) {

                tiles[ruinsCol][ruinsRow] = new Tile(TileType.GRASS);
            }
        }
        
        
    }
    
    
    
    //Act 2 
    private void updateActTwoWorldQuestTiles() {

        if (actTwoWorldGameMap == null) {
            return;
        }

        Tile[][] tiles = actTwoWorldGameMap.getTiles();

        int witheredRoadCol = 6;
        int witheredRoadRow = 5;

        int carnalvalGateCol = 8;
        int carnalvalGateRow = 1;

        // Withered Road mission
        if (storyChapter == 3 && chapterThreeStep == 2 && !corruptedRoadCompleted) {
            Tile witheredRoadTile = new Tile(TileType.ENEMY);
            witheredRoadTile.setScenarioId("withered_road");
            tiles[witheredRoadCol][witheredRoadRow] = witheredRoadTile;
        } else {
            if (tiles[witheredRoadCol][witheredRoadRow] != null &&
                tiles[witheredRoadCol][witheredRoadRow].getType() == TileType.ENEMY &&
                "withered_road".equals(tiles[witheredRoadCol][witheredRoadRow].getScenarioId())) {

                tiles[witheredRoadCol][witheredRoadRow] = new Tile(TileType.CRACKED_ROAD);
            }
        }

        // Carnalval gate later when unlocked
        if (carnalvalUnlocked) {
            Tile carnalvalTile = new Tile(TileType.EVENT);
            carnalvalTile.setEventId("enter_carnalval_gate");
            tiles[carnalvalGateCol][carnalvalGateRow] = carnalvalTile;
        } else {
            tiles[carnalvalGateCol][carnalvalGateRow] = new Tile(TileType.DEAD_GRASS);
        }
        
    }

    
    
    
    //This is where the tile lines start
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Switch for the different states
        switch(currentState) {
        
        case TITLE:
            drawTitle(g);
            break;
            
        case CONTROLS:
            drawControls(g);
            break;
        
        case OVERWORLD:
    		drawOverworld(g);
    		drawMapTitleOverlay(g);
    		break;
    		
    	case TOWN:
    		drawTown(g);
    		drawMapTitleOverlay(g);
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
    	    drawMapTitleOverlay(g);
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
    	    
    	case QUEST_BOARD:
    	    drawQuestBoard(g);
    	    break;
    	    
    	    
    	case DEFEAT:
    	    drawDefeat(g);
    	    break;
    	    
    	case ACT_ONE_END:
    	    drawActOneEnd(g);
    	    break;

        }
        
        
        
        
        if (currentState != GameState.TITLE &&
        		currentState != GameState.CONTROLS &&
        		currentState != GameState.STATUS &&
        	    currentState != GameState.EQUIPMENT &&
        	    currentState != GameState.CAMP &&
        	    currentState != GameState.DIALOGUE &&
        	    currentState != GameState.QUEST_BOARD &&
        	    currentState != GameState.DEFEAT &&
        	    currentState != GameState.ACT_ONE_END) {
        	    drawGlobalUI(g);
        	}
        
        if(currentState == GameState.DIALOGUE) {
            dialogueManager.draw(g, screenWidth, screenHeight);
        }
        
        drawWhiteFlash(g);
        drawStoryTransition(g);
        drawSystemMessage(g);
        
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
        
        	case TITLE:
        		break;
        		
        	case CONTROLS:
        		break;

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
            	
            case QUEST_BOARD:
            	break;
            	
            case DEFEAT:
            	break;
            	
            case ACT_ONE_END:
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

        
        	case TITLE:
        		
        	case CONTROLS:
        
        		
            case OVERWORLD:
            	
            case TOWN:
            case EXPLORATION:
            	Tile currentTile = currentMap.getTiles()[player.col][player.row];

            	g.drawString("Tile: " + getCurrentTileDisplayName(), 280, panelY + 25);
            	drawWrappedText(g, getCurrentTileDescription(), 280, panelY + 50, 200, 18);
            	break;
                
            case SHOP:
            	
            case DIALOGUE:
            	
            case EQUIPMENT:
            	
            case STATUS:
            	
            case CAMP:
            	
            case QUEST_BOARD:
            	
            case DEFEAT:
            case ACT_ONE_END:
   

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


        	case TITLE:
        		
        	case CONTROLS:
        		
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
            	
            case QUEST_BOARD:
            	
            case DEFEAT:
            	
            case ACT_ONE_END:
            
            	
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
        
        	case TITLE:
        		
        	case CONTROLS:
        

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
                g.drawString(getStoryChapterDisplayName(), panelX + 20, 105);
                
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
            	
            case QUEST_BOARD:
            	break;
            	
            case DEFEAT:
            	break;
            	
            case ACT_ONE_END:
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
    
    //This allows the map to be displayed for players who get confused on where they are
    private void drawMapTitleOverlay(Graphics g) {

        if (mapTitleTimer <= 0) {
            return;
        }

        if (currentMap == null) {
            return;
        }

        if (currentState != GameState.OVERWORLD &&
            currentState != GameState.TOWN &&
            currentState != GameState.EXPLORATION) {
            return;
        }

        String mapName = currentMap.getMapName();

        if (mapName == null || mapName.isEmpty()) {
            return;
        }

        Font oldFont = g.getFont();

        g.setFont(oldFont.deriveFont(18f));

        int padding = 10;
        int textWidth = g.getFontMetrics().stringWidth(mapName);
        int boxWidth = textWidth + padding * 2;
        int boxHeight = 30;

        int x = 12;
        int y = 12;

        int alpha = 170;

        if (mapTitleTimer < 60) {
            alpha = Math.max(0, mapTitleTimer * 170 / 60);
        }

        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(x, y, boxWidth, boxHeight);

        g.setColor(new Color(255, 255, 255, Math.min(255, alpha + 60)));
        g.drawRect(x, y, boxWidth, boxHeight);
        g.drawString(mapName, x + padding, y + 21);

        g.setFont(oldFont);
    }
    
    private void showMapTitle() {

        if (currentMap == null) {
            return;
        }

        mapTitleTimer = MAP_TITLE_DURATION;
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
    
    private String getCurrentTileDescription() {

        if (currentMap == null) {
            return "";
        }

        Tile tile = currentMap.getTiles()[player.col][player.row];

        if (tile == null) {
            return "";
        }

        if (tile.getType() == TileType.EVENT && tile.getEventId() != null) {
            String eventId = tile.getEventId();

            if (eventId.equals("to_laughing_lane")) {
                return "Path to Laughing Lane.";
            }

            if (eventId.equals("to_gilded_midway")) {
                return "Path to the Gilded Midway.";
            }

            if (eventId.equals("to_performers_row")) {
                return "Path to Performer’s Row.";
            }

            if (eventId.equals("to_guest_lodging")) {
                return "Path to Guest Lodging.";
            }

            if (eventId.equals("to_carnalval_main")) {
                return "Path back to the Main Carnival Grounds.";
            }

            if (eventId.equals("to_main_stage")) {
                return "The Main Stage entrance. The final performance is not ready.";
            }

            if (eventId.equals("carnalval_rules_sign")) {
                return "A painted rules sign for honored guests.";
            }

            if (eventId.equals("carnalval_exit_loop")) {
                return "The entrance gate. The road beyond it bends strangely.";
            }
        }

        return getTileDescription(tile.getType());
    }
    
    private String getCurrentTileDisplayName() {

        if (currentMap == null) {
            return "";
        }

        Tile tile = currentMap.getTiles()[player.col][player.row];

        if (tile == null) {
            return "";
        }

        if (tile.getType() == TileType.EVENT && tile.getEventId() != null) {
            String eventId = tile.getEventId();

            if (eventId.equals("to_laughing_lane")) {
                return "Laughing Lane Path";
            }

            if (eventId.equals("to_gilded_midway")) {
                return "Gilded Midway Path";
            }

            if (eventId.equals("to_performers_row")) {
                return "Performer’s Row Path";
            }

            if (eventId.equals("to_guest_lodging")) {
                return "Guest Lodging Path";
            }

            if (eventId.equals("to_carnalval_main")) {
                return "Main Grounds Path";
            }

            if (eventId.equals("to_main_stage")) {
                return "Main Stage Entrance";
            }

            if (eventId.equals("carnalval_rules_sign")) {
                return "Rules Sign";
            }

            if (eventId.equals("carnalval_exit_loop")) {
                return "Carnalval Gate";
            }
        }

        return tile.getType().toString();
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
            new DialogueLine("", "The party rests beneath the quiet night sky.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Everyone's mana has been restored.", DialogueSide.RIGHT, DialogueFaction.NPC)
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
        
        if (member.getId().equals("tali")) {
            startTaliCampConversation();
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
    
    
    //Start Tali Bond Convo
    private void startTaliCampConversation() {

        if (taliLastTalkedChapter == storyChapter) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Tali", "You already checked if I planned to run off.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "That was not what I was doing.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Sure looked like it.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "I was checking if you were alright.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "...That's worse.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.CAMP);

            return;
        }

        taliLastTalkedChapter = storyChapter;
        taliBond++;

        startDialogue(new DialogueLine[] {
            new DialogueLine("Tali", "The village looked at me like I was going to steal the moon.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "You did lead the Golden Sinners.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "I know what I did.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "I also know what Cael did with my name.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "People won't separate those overnight.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "I'm not asking them to.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then why stay?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Because if I walk away, every story Cael told about me becomes easier to believe.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "And because the people still wearing my mark deserve better than what I left behind."
            		+ "I gotta clear my name... Or at least try to make it up somehow.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "That sounds like a good reason for doing good.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Don't make it so noble. I hate that.", DialogueSide.RIGHT, DialogueFaction.ALLY)
        }, GameState.CAMP);
        
        
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
    
    //THIS EVENT handler allows player to interact with the surrounding maps in exploration mode
    private void handleExplorationEvent(String eventId) {

        if (eventId == null || eventId.isEmpty()) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Art", "There is something here, but I cannot make sense of it.", DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        if (eventId.equals("mural_old_heroes")) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "Look! Figures carved into the wall.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "They look like heroes. Look, that one has a cape.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "They are standing against something... larger.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "The stone is too worn to tell what it is.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Still. Heroes in ruins. There ought to mean treasure is nearby.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        if (eventId.equals("mural_white_blade")) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Art", "This carving is different.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "A sword... surrounded by light.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Now that looks important.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "There's words below it, but I can only read part of them.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Hmmm.'When the dark tide rises... the white blade...'", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "The rest is gone.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Convenient. Old stuff always stop explaining things right when they get interesting.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        if (eventId.equals("mural_broken_hour")) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "This one makes me uneasy.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Why?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "The circle here... it looks like a clock, but the hands are shattered.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Maybe old people were bad at drawing clocks.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Dean.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "What? I am helping.", DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }
        
        
        ///
        //Chapter 1 Safe House Event 1
        if (eventId.equals("safehouse_children")) {

            if (inspectedSafehouseChildren) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Dean", "They're still playing with sticks like swords.", DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Penelope", "They should not have to grow up around this.", DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedSafehouseChildren = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A few children chase each other between patched tents, waving sticks like blades.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Dean", "Huh.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "What?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I thought this place would be... I don't know. More skulls. Less tag.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "They're just kids.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine(" A Golden Sinner", "Kids with nowhere else to go.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "Why bring them here?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("A Golden Sinner", "Because outside these trees, no one bothers asking if they have somewhere to sleep.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
        }
        
      //Chapter 1 Safe House Event 2
        if (eventId.equals("safehouse_doctor")) {

            if (inspectedSafehouseDoctor) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Penelope", "They are still treating the wounded.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                    new DialogueLine("Art", "Bandits and refugees in the same line.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedSafehouseDoctor = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A woman in a dirty coat kneels beside a cot, wrapping a bloodied arm of a teenager"
                		+ " with strips of boiled cloth.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "That bandage is too loose.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Makeshift Doctor", "Then tighten it, healer girl.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "I... alright.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Did she just recruit Penelope by insulting the bandage?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Looks like it worked.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Makeshift Doctor", "People come here cut, starving, or scared. I don't ask who got them that way.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "You treat everyone?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Makeshift Doctor", "Everyone who can make it to my tent.", DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
        }
        
      //Chapter 1 Safe House Event 3
        if (eventId.equals("safehouse_supplies")) {

            if (inspectedSafehouseSupplies) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Art", "Food, medicine, blankets...", DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Dean", "Still stolen, probably.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedSafehouseSupplies = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "Pryed crates are stacked beneath a sagging tarp. Grain sacks, medicine bundles, patched blankets.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Dean", "This is a lot of supplies.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Some of it has the merchant seals still.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "And some of it is medicine.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "So they steal from roads and feed their camp with it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "That doesn't make the raids right.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "No. But it explains why people here defend them.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }
        
      //Chapter 1 Safe House Event 4
        if (eventId.equals("safehouse_guard")) {

            startDialogue(new DialogueLine[] {
                new DialogueLine("Golden Sinner Guard", "You've got brave feet walking in here.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
                new DialogueLine("Dean", "Thanks. They are attached to brave legs.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Golden Sinner Guard", "This is the funny one I presume?", DialogueSide.RIGHT, DialogueFaction.ENEMY),
                new DialogueLine("Art", "We're looking for The King.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Golden Sinner Guard", "Then you're looking above your weight.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
                new DialogueLine("Penelope", "Does The King know what is happening on the roads?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Golden Sinner Guard", "The King knows what the King needs to know.", DialogueSide.RIGHT, DialogueFaction.ENEMY)
            }, GameState.EXPLORATION);

            return;
        }
        
        if (eventId.equals("safehouse_orders")) {

            if (inspectedSafehouseOrders) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Art", "The orders are gone now.", DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Penelope", "But we know enough. Someone here can lead us closer to The King.", 
                    		DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedSafehouseOrders = true;

            if (checkmateStep < 5) {
                checkmateStep = 5;
            }

            updateOverworldQuestTiles();

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A torn page lies half-hidden beneath a crate.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "This has the Golden Sinners mark.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "It lists routes, storehouses, patrol times...", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "That is not petty theft. That is planning a war on wagons.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "There is a meeting point written here.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Do you think The King will be there?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "If not, someone important enough to regret meeting us.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("", "A new lead has been marked on your map.", DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
        }
        
        //Tali Sin
        if (eventId.equals("safehouse_king_tent")) {
            handleKingTentEvent();
            return;
        }
        
        //Chapter 2 Ruins
        //Mural
        if (eventId.equals("chapter2_mural_creation")) {

            if (inspectedChapterTwoMural) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Art", "The sword carving is still here.", DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Penelope", "It looks too much like the one near Cerebella.", DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedChapterTwoMural = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "This symbol again...", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "The blade surrounded by light.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "So your weird sword has cousins.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Or a reputation.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "Old civilizations reused symbols constantly. Swords, suns, crowns, wings. Very dramatic people.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "The Rusty Creation feels warmer here.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Thats comforting.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "Comfort is rarely found in old ruins, I'm afraid.",
                        DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
            
        }
        
        
        
        //magic Seals
        if (eventId.equals("chapter2_magic_seal")) {

            if (inspectedChapterTwoSeal) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Penelope", "This seal is still making the air feel heavy.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                    new DialogueLine("Tali", "Then stop standing on it.", DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            inspectedChapterTwoSeal = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A circular seal is carved deep into the stone floor.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "It looks like the broken clock carving from the old ruins.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I love when old ruins start repeating themselves. That always means nothing terrible.",
                        DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Silas, what is this?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "A lock, perhaps. Or a warning. Old places like this usually aren't worth it.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Tali", "That wasn't an answer.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "It was a cautious answer.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "The sword is reacting to it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Then maybe William was right.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "William worries professionally. I admire the commitment and boldness Come along now.",
                        DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
            
        }
        
        
        //Relic Pedestal
        if (eventId.equals("chapter2_relic_pedestal")) {

            if (silasBetrayalTriggered) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Art", "The pedestal is empty now.", DialogueSide.LEFT, DialogueFaction.ALLY),
                    new DialogueLine("Penelope", "I did not like how quickly everything changed here.", DialogueSide.RIGHT, DialogueFaction.ALLY)
                }, GameState.EXPLORATION);
                return;
            }

            silasBetrayalTriggered = true;
            inspectedChapterTwoRelic = true;
            pendingSilasTrapBattle = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("Silas", "There it is.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Dean", "That tiny thing? We came all this way for a shiny rock?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Dean, do not call unknown relics shiny rocks.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "I am with him. That is a lot of trouble for something I could throw at a wall.",
                        DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Silas. Wait.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "No need to worry. I know exactly what I am doing.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("", "Silas lifts the relic from the pedestal.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("", "The seal beneath it cracks with a sound like splitting ice.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "That was not a good sound.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "No. But it was a profitable one.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Dean", "I knew it. Nobody with a fit that clean is trustworthy.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Silas", "Do try to survive. I would hate for reliable guards to go to waste.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "Silas!", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("", "Stone grinds shut behind him. Heat rises as something wakes beneath the floor. ",
                        DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.EXPLORATION);

            return;
        }

        
        
        
        //End
        startDialogue(new DialogueLine[] {
            new DialogueLine("Art", "There is nothing else here.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
    }
    
    
    /*
     * BEGINING OF STORY
     * START OF PROLOGUE
     * Begining of story here!
     */
    
    private void startPrologue() {

        storyChapter = 0;
        prologueStep = 0;

        currentMap = prologueForestGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 1;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("Dean", "Come on, Art! The ruins are just past the old forest road.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "We shouldn't be this far from the village.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Just a look around, then we go home. That was the deal.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "That was your deal. I agreed to the exciting version.", DialogueSide.RIGHT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
    }
    
    //Camping before next part of Prologue
    private void startPrologueCamp() {

    	prologueStep = 1;

        openCampWithReturn(GameState.EXPLORATION, prologueForestGameMap, player.col, player.row);

        currentState = GameState.CAMP;
        campMenuIndex = 0;

        startDialogue(new DialogueLine[] {
        	new DialogueLine("Penelope", "I can't believe we're camping in the middle of nowhere.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Penelope real adventurers always camp before discovering something amazing.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Kids probably tell someone where they are going first. Deans Mom is scary.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Sorry Penelope. I think it's fine. I mean we've gotten in worse because of Dean before. We're still alive.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Hey!", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Look, we can sleep for one night. Tomorrow, we reach the ruins and then head home.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Tomorrow, we find treasure!", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "We're so in trouble.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            
        }, GameState.CAMP);
        
    }
    
    
    private void enterPrologueRuins() {

        prologueStep = 2;

        currentMap = ruinsGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 1;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("Dean", "There it is. I told you the ruins were real.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "It feels colder here.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Stay close. We will look around and leave together.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Together, yes. Leaving quickly, maybe not.", DialogueSide.RIGHT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
    }
    
    //Return Home
    private void startPrologueReturnHome() {

        prologueStep = 4;

        currentMap = prologueForestGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 8;
        player.row = 8;

        startDialogue(new DialogueLine[] {
        	new DialogueLine("", "Art awakens on the floor. He gets up slowly.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	new DialogueLine("Art", "Uhhg. My head", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Art... are you alright? You were knocked down for 10 minutes", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "I think so...", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Art picked up an old sword from an old ruin and never let go of it. What happened to spliting the loot?", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Dean.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Right. Serious faces. I can do serious.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "We should go home. Now.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Yes, let's get home.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
        
    }
    
    //Part 5 Finale before chapter switch
    private void completePrologue() {

        prologueStep = 5;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The children returned to Cerebella before dawn.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "No one believed their story of the white light.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "But from that day forward, the old sword never left Art's side.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);

        pendingPrologueChapterOne = true;
        
    }
    
    //Auto Advances now to Chapter one after the above part 5 finishes
    /*
     * START OF CHAPTER 1 BEGINS HERE
     * 
     */
    private void startChapterOne() {

        advanceStoryChapter(1);
        chapterOneStep = 0;

        currentMap = overworldGameMap;
        showMapTitle();
        currentState = GameState.OVERWORLD;

        player.col = 1;
        player.row = 5;

        movementLeft = maxMovement;

        pendingRecruitmentScene = true;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "Time passed, and Cerebella changed.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The harvest grew weaker each year. Animals wandered away from the woods in strange numbers.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Merchants and travelers spoke of bandits, missing caravans, and roads that no longer felt safe.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "King Amon of Astoria sent recruiters from village to village, searching for anyone willing"
            		+ " to join the kingdom's cause.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "However...", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.OVERWORLD);
        
        
    }
    
    //Chapter 1 Step 1
    private void startRecruitmentRejectionScene() {

        chapterOneStep = 1;
        pendingAdventurerIdeaScene = true;

        startDialogue(new DialogueLine[] {
            new DialogueLine("Dean", "This is the third time this month!", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Third! I counted because Penelope said yelling 'again' was not specific enough.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Recruiter", "And for the third time, the answer is no.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "Can we at least ask why this time sir?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Recruiter", "Sigh. Look you're undertrained, under-equipped, and this time one of you listed"
            		+ " 'future legend' as relevant experience.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "...Dean...", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Hey! It is relevant. Just early.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Recruiter", "Regardless, the king needs soldiers, not storybook volunteers.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "We just want to help. Things are getting worse here.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Recruiter", "Then help by staying out of the army's way. NEXT!", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The three of them leave disgruntled.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "...If the army won't take us, we'll have to find another way.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Yeah, well I'm getting desperate here. I'm tired of hunting for the butcher. He always rips me.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "I'm sure we can volunteer at the church again.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Heroic and unpaid. Very traditional.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.OVERWORLD);
        
    }
    
    //CHAPTER 1 Step 2
    private void startAdventurerIdeaScene() {

        chapterOneStep = 2;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "After leaving the recruitment table, the three found a quiet spot away from the line.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Art was a taller young man, Brown medium wavy hair with a fair light tone. "
            		+ "He worked as a lumberer over the years as he honed his skills in swordsmanship.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Penelope grew up in the church. Her Abilites honed from the Priest himself before he left her to the village"
            		+ "She has long blonde hair and pale skin tone.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Dean was a shorter man with dark brown hair and a darker skin tone. He worked as hunter as did his mother.",
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "I cannot believe that guy. 'Storybook volunteers.' Who says that to someone's face?",
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Someone who doesn't  want us joining the army.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Yeah, well, message received. Loudly. Rudely. What terrible manners.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Getting angry at him won't change anything.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "I know that. I am just choosing to be angry in a very productive way.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Is this productive?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Not yet. Give me a minute.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Well, if the army's not going to take us, then we need another way to help.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Another way that doesn't involve Dean chasing glory with no plan.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Erm actually. I have an idea.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Is the idea 'run at the recruiter begging for them to take us'?", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "...Let me keep thinking.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "What about the adventurer registry?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "The one in the next town?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "People post work there. Real work. Escorting, hunting, repairs, missing goods. Things the army ignores.",
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "So instead of soldiers, we become adventurers.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Registered adventurers. There is a difference.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Right. Registered heroes with paperwork.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "It may not be glamorous, but it is a start.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Better idea than whatever Dean was about to do.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "For the record, my suggestion had courage, vision, and maybe a little shame.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then we'll save that for later.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Fine. But when we become famous, I'm telling people this was my idea.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Of course you are.", DialogueSide.RIGHT, DialogueFaction.ALLY)
        }, GameState.OVERWORLD);
        
        
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
	
	private void drawWhiteFlash(Graphics g) {

	    if (whiteFlashTimer <= 0) {
	        return;
	    }

	    Graphics2D g2 = (Graphics2D) g;

	    //Smooth, Seemless white flash that curbs out
	    float progress = whiteFlashTimer / (float) WHITE_FLASH_DURATION;
	    int alpha = (int)(255 * progress * progress);

	    g2.setColor(new Color(255, 255, 255, alpha));
	    g2.fillRect(0, 0, screenWidth, screenHeight);
	    
	}
	
	
	private void startStoryTransition(String text) {
	    storyTransitionText = text;
	    storyTransitionTimer = STORY_TRANSITION_DURATION;
	    
	}
	
	//Transition to next part
	private void finishStoryTransition() {

		//act 1
	    if (pendingChapterOneStart) {
	        pendingChapterOneStart = false;
	        storyTransitionText = "";
	        startChapterOne();
	        return;
	    }
	    
	    //Act 2
	    if (pendingActTwoOpening) {
	        pendingActTwoOpening = false;
	        storyTransitionText = "";
	        startChapterThreeOpening();
	        return;
	    }

	    
	    if (pendingReturnAfterActOne) {
	    	pendingReturnAfterActOne = false;
	        storyTransitionText = "";
	        showingRuinsExteriorScene = false;
	        currentState = GameState.ACT_ONE_END;
	        return;
	    }

	    storyTransitionText = "";
	}
	
	//Draws out the next chapter transition
	private void drawStoryTransition(Graphics g) {

	    if (storyTransitionTimer <= 0) {
	        return;
	    }

	    Graphics2D g2 = (Graphics2D) g;
	    Font originalFont = g2.getFont();

	    // Full dark overlay
	    g2.setColor(new Color(0, 0, 0, 230));
	    g2.fillRect(0, 0, screenWidth, screenHeight);

	    // Fade text slightly in/out
	    float progress = storyTransitionTimer / (float) STORY_TRANSITION_DURATION;

	    int alpha;

	    if (progress > 0.75f) {
	        // fade in
	        alpha = (int)(255 * ((1f - progress) / 0.25f));
	    } else if (progress < 0.25f) {
	        // fade out
	        alpha = (int)(255 * (progress / 0.25f));
	    } else {
	        // hold
	        alpha = 255;
	    }

	    if (alpha < 0) alpha = 0;
	    if (alpha > 255) alpha = 255;

	    g2.setColor(new Color(255, 255, 255, alpha));
	    g2.setFont(originalFont.deriveFont(36f));

	    int textWidth = g2.getFontMetrics().stringWidth(storyTransitionText);
	    int x = (screenWidth - textWidth) / 2;
	    int y = screenHeight / 2;

	    g2.drawString(storyTransitionText, x, y);

	    g2.setFont(originalFont);
	    
	    
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
    
    //Draws Quest Board
    private void drawQuestBoard(Graphics g) {

        g.setColor(new Color(35, 25, 15));
        g.fillRect(0, 0, screenWidth, screenHeight);

        int menuX = 90;
        int menuY = 70;
        int menuWidth = 540;
        int menuHeight = 360;

        g.setColor(new Color(60, 40, 20));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);

        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        g.drawString("Adventurer Board", menuX + 20, menuY + 30);
        g.drawString("Choose a request.", menuX + 20, menuY + 55);
        
        if (hasActiveQuest()) {
            g.drawString("Active Request: " + activeQuestName, menuX + 20, menuY + 75);
        } else {
            g.drawString("No active request.", menuX + 20, menuY + 75);
        }

        for (int i = 0; i < questBoardOptions.length; i++) {
            String option = questBoardOptions[i];

            if (i == questBoardIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == questBoardIndex) ? "> " : "  ";

            String status = "";

            if (option.equals("Cellar Rats") && cellarRatsCompleted) {
                status = " [Done]";
            }
            else if (option.equals("Missing Laundry") && laundryCompleted) {
                status = " [Done]";
            }
            else if (option.equals("Flower Picking") && flowersCompleted) {
                status = " [Done]";
            }

            g.drawString(prefix + option + status, menuX + 40, menuY + 100 + (i * 35));
        }

        g.setColor(Color.WHITE);
        g.drawString("ENTER accept | ESC back", menuX + 20, menuY + menuHeight - 25);

        drawQuestBoardDetails(g, menuX, menuY);
        
        if (questConfirmOpen) {
            drawQuestConfirmPrompt(g);
        }
        
    }
    
    //Confirmation allows player to re choose again
    private void drawQuestConfirmPrompt(Graphics g) {

        int boxWidth = 260;
        int boxHeight = 120;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = (screenHeight - boxHeight) / 2;

        g.setColor(new Color(20, 20, 20, 230));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        g.drawString("Accept request?", boxX + 25, boxY + 30);
        g.drawString(pendingQuestName, boxX + 25, boxY + 50);

        if (questConfirmIndex == 0) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }

        g.drawString("> Accept", boxX + 40, boxY + 80);

        if (questConfirmIndex == 1) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }

        g.drawString("> Decline", boxX + 140, boxY + 80);
        
    }
    
    //details for quest board
    private void drawQuestBoardDetails(Graphics g, int menuX, int menuY) {

        String selected = questBoardOptions[questBoardIndex];

        int detailX = menuX + 280;
        int detailY = menuY + 100;

        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Details", detailX, detailY);

        g.setColor(Color.WHITE);

        ////
        if (selected.equals("Cellar Rats")) {
            drawWrappedText(g,
                "Clear rats from a tavern cellar. Reward: 20 gold.",
                detailX,
                detailY + 25,
                220,
                18
            );
        }
        
        ////
        else if (selected.equals("Missing Laundry")) {

            String detail = "Collect laundry scattered around town. Reward: 10 gold.";

            if (activeQuestName.equals("Missing Laundry")) {
                detail = "Active: collect laundry around town. Progress: " 
                        + laundryCollected + "/" + LAUNDRY_REQUIRED + ".";
            }

            drawWrappedText(g, detail, detailX, detailY + 25, 220, 18);
        }
        
        /////
        else if (selected.equals("Flower Picking")) {

            String detail = "Gather medicinal flowers near the forest. Reward: 15 gold.";

            if (activeQuestName.equals("Flower Picking")) {
                detail = "Active: gather medicinal flowers. Progress: "
                        + flowersCollected + "/" + FLOWERS_REQUIRED + ".";
            }

            drawWrappedText(g, detail, detailX, detailY + 25, 220, 18);
        }
        
        //Major Step Quest
        else if (selected.equals("Urgent Notice")) {

        	String detail;

            if (!banditQuestUnlocked) {
                detail = "No urgent requests are posted yet.";
            } else if (!banditQuestAccepted && !banditQuestCompleted) {
            	
                detail = "A serious road attack notice. Speak with the Village Elder.";
            } else if (isBanditQuestActive()) {
            	
                detail = "Bandit Trouble is active. Track the attackers near the forest road.";
            } else if (banditQuestCompleted && checkmateStep == 2) {
            	
                detail = "The road attack request is complete. The Golden Sinners clue remains.";
            } else {
                detail = "The urgent request has been handled.";
            }

            drawWrappedText(g, detail, detailX, detailY + 25, 220, 18);
        }
        
        
        else if (selected.equals("Leave")) {
            drawWrappedText(g,
                "Step away from the board.",
                detailX,
                detailY + 25,
                220,
                18
            );
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
        int menuHeight = 250;

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
        g.drawString("Dean Bond: " + deanBond, menuX + 20, menuY + 165);
        g.drawString("Penelope Bond: " + penelopeBond, menuX + 20, menuY + 185);
        g.drawString("Tali Bond: " + taliBond, menuX + 20, menuY + 205);
        
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
    	
    	if (unitId.equals("tali_guest") && !enemy) {
    	    return createTaliGuestUnit(col, row);
    	}
    	
    	
    	
    	//Enemy Weapons
    	Weapon banditAxe = createWeaponById("bandit_axe");
    	Weapon enemyBow = createWeaponById("hunter_bow");
    	Weapon ratBite = createWeaponById("rat_bite");
    	Weapon taliSpear = createWeaponById("tali_spear");
    	Weapon caelBlade = createWeaponById("cael_blade");
    	Weapon golemPulse = createWeaponById("golem_pulse");
    	Weapon claws = createWeaponById("corrupted_claws");
    	Weapon antlers = createWeaponById("twisted_antlers");
		
		//Class Name, Max HP, Armor Class, Movement Range, Weapon Type
		CharacterClass banditClass = new CharacterClass("Bandit", 10, 10, 4, new WeaponType[] { WeaponType.AXE });
		CharacterClass hunterClass = new CharacterClass("Hunter", 9, 11, 5, new WeaponType[] { WeaponType.BOW });
		CharacterClass ratClass = new CharacterClass("Rat", 5, 8, 5, new WeaponType[] { WeaponType.AXE });
		CharacterClass taliClass = new CharacterClass("Bandit King", 17, 13, 5, new WeaponType[] { WeaponType.LANCE });
		CharacterClass caelClass = new CharacterClass("Usurper", 20, 13, 5,new WeaponType[] { WeaponType.SWORD });
		CharacterClass golemClass = new CharacterClass("Seal Guardian", 24, 8, 0, new WeaponType[] { WeaponType.AXE });
		CharacterClass beastClass = new CharacterClass("Corrupted Beast", 12, 8, 5, new WeaponType[] { WeaponType.AXE });
		CharacterClass stagClass = new CharacterClass("Twisted Beast", 16, 8, 4, new WeaponType[] { WeaponType.AXE });
		
		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance
		GrowthRates banditGrowth = new GrowthRates(70, 50, 0, 30, 36, 15, 25, 10);
		GrowthRates hunterGrowths = new GrowthRates(60, 35, 0, 55, 50, 25, 15, 20);
		GrowthRates ratGrowths = new GrowthRates(0, 0, 0, 0, 0, 0, 0, 0);
		GrowthRates taliGrowths = new GrowthRates(70, 55, 10, 50, 50, 30, 35, 25);
		GrowthRates caelGrowths = new GrowthRates(70, 55, 10, 55, 55, 25, 30, 20);
		GrowthRates golemGrowths = new GrowthRates(0, 0, 0, 0, 0, 0, 0, 0);
		GrowthRates beastGrowths = new GrowthRates(0, 0, 0, 0, 0, 0, 0, 0);
		GrowthRates stagGrowths = new GrowthRates(0, 0, 0, 0, 0, 0, 0, 0);
		
		//Health, Strength, Magic, Skill, Speed, Luck, Defense, Resistance, Movement
		UnitStats banditStats = new UnitStats(10, 0, 4, 0, 3, 3, 1, 1, 0, 4);
		UnitStats hunterStats = new UnitStats(9, 2, 3, 0, 5, 5, 2, 1, 1, 5);
		UnitStats ratStats = new UnitStats(5, 0, 1, 0, 2, 5, 0, 0, 0, 5);
		UnitStats taliStats = new UnitStats(17, 4, 5, 0, 5, 5, 4, 3, 2, 5);
		UnitStats caelStats = new UnitStats(20, 6, 5, 0, 6, 6, 3, 3, 2, 5);
		UnitStats golemStats = new UnitStats(35, 0, 7, 0, 2, 0, 0, 28, 0, 0);
		UnitStats beastStats = new UnitStats(12, 0, 5, 0, 4, 6, 0, 2, 1, 5);
		UnitStats stagStats = new UnitStats(16, 0, 7, 0, 3, 4, 0, 3, 1, 4);
		

		if (unitId.equals("bandit")) {
			return new BattleUnit("Bandit", col, row, enemy, banditAxe, banditClass, banditStats, banditGrowth, "", EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("hunter")) {
		    return new BattleUnit("Hunter", col, row, enemy, enemyBow, hunterClass, hunterStats, hunterGrowths, "", EnemyRole.RANGED);
		}
		
		if (unitId.equals("rat")) {
		    return new BattleUnit("Cellar Rat", col, row, enemy, ratBite, ratClass, ratStats, ratGrowths, "", EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("tali_boss")) {
		    return new BattleUnit("Tali Sin", col, row, true, taliSpear, taliClass, taliStats,taliGrowths,"Piercing Thrust", EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("cael_boss")) {
		    return new BattleUnit("Cael", col, row, true, caelBlade, caelClass, caelStats, caelGrowths, "Dirty Cut",EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("stone_golem")) {
		    return new BattleUnit("Stone Golem", col, row, true, golemPulse, golemClass, golemStats, golemGrowths, "", EnemyRole.STATIONARY);
		}
		
		if (unitId.equals("corrupted_wolf")) {
			return new BattleUnit("Corrupted Wolf", col, row, true, claws, beastClass, beastStats, beastGrowths, "", EnemyRole.AGGRESSIVE);
		}
		
		if (unitId.equals("twisted_stag")) {
			return new BattleUnit("Twisted Stag", col, row, true, antlers, stagClass, stagStats, stagGrowths, "", EnemyRole.AGGRESSIVE);
		}
		
    	
    	return null;
    }
    
    
    //Temp Unit!
    private BattleUnit createTaliGuestUnit(int col, int row) {

        Weapon taliSpear = createWeaponById("tali_spear");

        CharacterClass taliClass = new CharacterClass(
            "Bandit King", 14, 13, 5, new WeaponType[] { WeaponType.LANCE });

        GrowthRates taliGrowths = new GrowthRates(70, 55, 10, 50, 50, 30, 35, 25);

        // HP, MP, STR, MAG, SKL, SPD, LCK, DEF, RES, MOV
        UnitStats taliStats = new UnitStats(16, 4, 5, 0, 5, 5, 4, 3, 2, 5);

        return new BattleUnit(
            "Tali Sin",col, row, false, taliSpear, taliClass, taliStats, taliGrowths, "Piercing Thrust",null);
        
    }
    
    
    //William Guest
    private BattleUnit createWilliamGuestUnit(int col, int row) {

        Weapon williamTome = createWeaponById("william_tome");
        CharacterClass williamClass = new CharacterClass("Scholar Mage", 12, 12, 4, new WeaponType[] { WeaponType.TOME });
        GrowthRates williamGrowths = new GrowthRates(60, 10, 70, 55, 40, 35, 20, 55);

        // HP, MP, STR, MAG, SKL, SPD, LCK, DEF, RES, MOV
        UnitStats williamStats = new UnitStats(14, 18, 0, 8, 6, 4, 4, 2, 6, 4);

        return new BattleUnit("William Winters", col, row, false, williamTome, williamClass, williamStats, williamGrowths, "Arcane Break", null);
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
    
    //Recruit Tali after End of Chapter 1
    private void recruitTali() {

        if (taliRecruited || getPartyMemberById("tali") != null) {
            taliRecruited = true;
            taliTemporaryAlly = false;
            return;
        }

        Weapon taliSpear = createWeaponById("tali_spear");
        CharacterClass taliClass = new CharacterClass("Spearfighter", 14, 13, 5, new WeaponType[] { WeaponType.LANCE });
        GrowthRates taliGrowths = new GrowthRates(70, 55, 10, 50, 50, 30, 35, 25);

        // HP, MP, STR, MAG, SKL, SPD, LCK, DEF, RES, MOV
        UnitStats taliStats = new UnitStats(16, 4, 5, 0, 5, 5, 4, 3, 2, 5);

        PartyMember tali = new PartyMember("tali", "Tali Sin", 1, 0, taliStats, taliGrowths, taliClass, taliSpear, "Piercing Thrust");

        partyMembers.add(tali);

        taliRecruited = true;
        taliTemporaryAlly = false;

        System.out.println("Tali has joined the party.");
    }
    
    
    
    private void recruitWilliam() {

        if (williamRecruited || getPartyMemberById("william") != null) {
            williamRecruited = true;
            return;
        }

        Weapon williamTome = createWeaponById("william_tome");

        CharacterClass williamClass = new CharacterClass(
            "Scholar Mage", 12, 12, 4, new WeaponType[] { WeaponType.TOME });

        GrowthRates williamGrowths = new GrowthRates(60, 10, 70, 55, 40, 35, 20, 55);

        // HP, MP, STR, MAG, SKL, SPD, LCK, DEF, RES, MOV
        UnitStats williamStats = new UnitStats(14, 18, 0, 8, 6, 4, 4, 2, 6, 4);

        PartyMember william = new PartyMember("william", "William Winters", 1, 0, williamStats, williamGrowths, 
        		williamClass, williamTome, "Arcane Break");

        partyMembers.add(william);

        williamRecruited = true;

        System.out.println("William has joined the party.");
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
        
        if (weaponId.equals("cael_blade")) {
            return new Weapon("cael_blade","Cael's Blade",WeaponType.SWORD, 1, 1, 4, 1, 8, 2,false);
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
        //Spears
        if (weaponId.equals("tali_spear")) {
            return new Weapon("tali_spear", "Tali's Spear", WeaponType.LANCE, 1, 2, 3, 1, 8, 2, false);
        }

        //Mage
        if (weaponId.equals("fire_tome")) {
            return new Weapon("fire_tome", "Fire Tome", WeaponType.TOME, 1, 2, 3, 1, 6, 2, true);
        }
        
        if (weaponId.equals("fire_tome_plus")) {
            return new Weapon("fire_tome_plus", "Fire Tome+", WeaponType.TOME, 1, 2, 3, 1, 8, 2, true);
        }
        
        if (weaponId.equals("william_tome")) {
            return new Weapon("william_tome", "Arcane Tome", WeaponType.TOME, 1, 2, 5, 1, 6, 4, true);
        }
        
        //Cleric
        if (weaponId.equals("training_staff")) {
            return new Weapon("training_staff", "Training Staff", WeaponType.STAFF, 1, 2, 2, 1, 4, 1, true);
        }

        //Bandits
        if (weaponId.equals("bandit_axe")) {
            return new Weapon("bandit_axe", "Bandit Axe", WeaponType.AXE, 1, 1, 2, 1, 8, 1, false);
        }
        
        //Non Humans
        if (weaponId.equals("rat_bite")) {
            return new Weapon("rat_bite", "Rat Bite", WeaponType.AXE, 1, 1, 1, 1, 4, 0, false);
        }
        
        if (weaponId.equals("golem_pulse")) {
            return new Weapon("golem_pulse", "Seal Pulse", WeaponType.AXE, 1, 6, 3, 1, 8, 2, false);
        }
        
        if (weaponId.equals("corrupted_claws")) {
            return new Weapon("corrupted_claws", "Corrupted Claws", WeaponType.AXE, 1, 1, 4, 1, 8, 2, false);
        }

        if (weaponId.equals("twisted_antlers")) {
            return new Weapon("twisted_antlers", "Twisted Antlers", WeaponType.AXE, 1, 1, 5, 1, 8, 2, false);
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
    	
    	rememberBattleReturnPoint();
    	
    	lastBattleScenario = scenario;
    	currentBattleScenario = scenario;
    	
    	//Golem Problem
    	if (scenario.getId().equals("golem_seal_trap")) {
    	    golemTurn2DialogueShown = false;
    	    golemTurn3DialogueShown = false;
    	    williamArrivedForGolem = false;
    	    pendingWilliamGolemRescue = false;
    	}
    	
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
    	
    	//Speical Casing
    	boolean isGolemTrap = scenario.getId().equals("golem_seal_trap");
    	boolean isWitheredRoad = scenario.getId().equals("withered_road");
    	
    	for (int col = 0; col < maxScreenCol; col++) {
    		for (int row = 0; row < maxScreenRow; row++) {
    			
    			int value = layout[row][col];
    			
    			if (isGolemTrap) {

                    if (value == 0) {
                        battleMap[col][row] = new Tile(TileType.RUINS_FLOOR);
                    }
                    else if (value == 1) {
                        battleMap[col][row] = new Tile(TileType.STONE_WALL);
                    }
                    else if (value == 6) {
                        battleMap[col][row] = new Tile(TileType.LAVA);
                    }

                } 
    			
                else if (isWitheredRoad) {

                    if (value == 0) {
                        battleMap[col][row] = new Tile(TileType.DEAD_GRASS);
                    }
                    else if (value == 1) {
                        battleMap[col][row] = new Tile(TileType.DEAD_FOREST);
                    }
                    else if (value == 2) {
                        battleMap[col][row] = new Tile(TileType.DEAD_GRASS);
                    }
                    else if (value == 3) {
                        battleMap[col][row] = new Tile(TileType.DEAD_FOREST);
                    }
                    else if (value == 4) {
                        battleMap[col][row] = new Tile(TileType.CRACKED_ROAD);
                    }
                    else if (value == 5) {
                        battleMap[col][row] = new Tile(TileType.CORRUPTED_WATER);
                    }
                
                } 
    			
                else {

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
                    else if (value == 6) {
                        battleMap[col][row] = new Tile(TileType.LAVA);
                    }
                }
                
                if (battleMap[col][row] == null) {
                    battleMap[col][row] = new Tile(TileType.GRASS);
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
    
    
    //Remembers last state
    private void rememberBattleReturnPoint() {

        if (currentState == GameState.BATTLE) {
            return;
        }

        if (currentState == GameState.DIALOGUE) {
            battleReturnState = previousState;
        } else {
            battleReturnState = currentState;
        }

        battleReturnMap = currentMap;
        battleReturnCol = player.col;
        battleReturnRow = player.row;

        System.out.println("Battle return saved:");
        System.out.println("State: " + battleReturnState);

        if (battleReturnMap != null) {
            System.out.println("Map: " + battleReturnMap.getMapName());
        }

        System.out.println("Position: " + battleReturnCol + "," + battleReturnRow);
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

        updateTownQuestTiles(); //also added
        updateOverworldQuestTiles();
        updateActTwoWorldQuestTiles();

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
        prologueStep = 3;
        pendingPrologueReturnHome = true;
        
        startWhiteFlash();

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
            new DialogueLine("", "Art obtained a Rusty Sword.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "A flash of white light tears through the ruins.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);
    }
    
    //When obtaining Creation
    private void startWhiteFlash() {
        whiteFlashTimer = WHITE_FLASH_DURATION;
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
        activeQuestName = "Bandit Trouble";

        if (checkmateStep < 1) {
            checkmateStep = 1;
        }

        updateOverworldQuestTiles();

        System.out.println("Bandit quest accepted! Checkmate step: " + checkmateStep);
    }
    
    //Shows completion
    private void completeBanditQuest() {
    	banditQuestCompleted = true;

        if (activeQuestName.equals("Bandit Trouble")) {
            activeQuestName = "";
        }

        if (checkmateStep < 2) {
            checkmateStep = 2;
        }

        updateOverworldQuestTiles();

        System.out.println("Bandit quest completed! Checkmate step: " + checkmateStep);
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
    
    //Quests WILL START HERE!
    private void drawQuestLog(Graphics g, int panelX, int startY) {

        g.setColor(Color.WHITE);

        g.drawString("Quest", panelX + 20, startY);

        String activeQuest = getActiveQuestDisplayName();

        if (activeQuest == null || activeQuest.isEmpty()) {
            g.drawString("None Active", panelX + 20, startY + 25);
            drawWrappedText(
                g,
                getActiveQuestObjectiveText(),
                panelX + 20,
                startY + 50,
                rightPanelWidth - 40,
                18
            );
            return;
        }

        g.drawString(activeQuest, panelX + 20, startY + 25);

        drawWrappedText(
            g,
            getActiveQuestObjectiveText(),
            panelX + 20,
            startY + 50,
            rightPanelWidth - 40,
            18
        );
    }
    
    //Displays the true current quest
    private String getActiveQuestDisplayName() {

        if (activeQuestName != null && !activeQuestName.isEmpty()) {
            return activeQuestName;
        }
        
        //Map Specific Quests
        if (currentMap == safehouseGameMap) {

            if (!inspectedKingTent) {
                return "Checkmate: Hidden Camp";
            }

            if (inspectedKingTent && !taliConfrontationCompleted) {
                return "Checkmate: The Bandit King";
            }

            return "Checkmate: Safehouse";
        }
        
        if (currentMap == chapterTwoRuinsGameMap) {

            if (!inspectedChapterTwoMural || !inspectedChapterTwoSeal) {
                return "Chapter 2: Old Relic Ruins";
            }

            if (!silasBetrayalTriggered) {
                return "Chapter 2: Relic Chamber";
            }

            return "Chapter 2: The Broken Seal";
        }
        
        //Prologue
        if (storyChapter == 0) {

            if (prologueStep == 0) {
                return "Prologue: Forest Path";
            }

            if (prologueStep == 1) {
                return "Prologue: Camp";
            }

            if (prologueStep == 2) {
                return "Prologue: Ancient Ruins";
            }

            if (prologueStep == 3) {
                return "Prologue: The Sword";
            }

            if (prologueStep == 4) {
                return "Prologue: Return Home";
            }

            return "Prologue";
        }

        // Checkmate arc
        if (checkmateStep == 3) {
            return "Checkmate: Old Mill Road";
        }

        if (checkmateStep == 4 && !safehouseUnlocked) {
            return "Checkmate: Report to Elder";
        }

        if (checkmateStep == 4 && safehouseUnlocked) {
            return "Checkmate: Hidden Camp";
        }

        if (checkmateStep == 5) {
            return "Checkmate: The King's Tent";
        }

        if (checkmateStep == 6 && taliTemporaryAlly && !taliRecruited) {
            return "Checkmate: Cael's Betrayal";
        }

        if (storyChapter < 2 && checkmateStep == 8 && taliRecruited) {
            return "Checkmate: Complete";
        }

        // Chapter 2 / Act One ending arc
        if (storyChapter == 2 && ruinsJobUnlocked && chapterTwoStep == 2 && !silasBetrayalTriggered) {
            return "Chapter 2: Old Relic Ruins";
        }

        if (storyChapter == 2 && silasBetrayalTriggered && !williamRecruited) {
            return "Chapter 2: The Broken Seal";
        }

        if (storyChapter == 2 && williamRecruited) {
            return "Act One: Complete";
        }

        // Urgent notice before Bandit Trouble
        if (banditQuestUnlocked && !banditQuestAccepted && !banditQuestCompleted) {
            return "Urgent Notice";
        }
        
        if (banditQuestCompleted && !banditQuestRewardClaimed && checkmateStep < 3) {
            return "Bandit Trouble: Report Back";
        }
        
        //Chapter 3 Act 2
        if (storyChapter == 3) {

        	if (chapterThreeStep == 2 && !corruptedRoadCompleted) {
                return "Chapter 3: Withered Road";
            }

            if (chapterThreeStep == 3 && !carnalvalUnlocked) {
                return "Chapter 3: The Invitation";
            }

            if (chapterThreeStep == 4 && carnalvalUnlocked) {
                return "Chapter 3: Carnalval Gates";
            }

            return "Chapter 3: The Corruption Trail";
        }
        
        if (storyChapter == 4) {

            if (chapterFourStep == 1) {
                return "Chapter 4: Welcome to the Carnalval";
            }

            if (!mainStageUnlocked) {
                return "Chapter 4: Explore the Carnalval";
            }

            return "Chapter 4: Final Performance";
        }
        
        
        

        return "";
    }
    
    //Details for Quests
    private String getActiveQuestObjectiveText() {

        if (activeQuestName != null && !activeQuestName.isEmpty()) {
        	
        	if (storyChapter == 0) {

        	    if (prologueStep == 0) {
        	        return "Follow the forest path and reach the campsite.";
        	    }

        	    if (prologueStep == 1) {
        	        return "Rest at camp, then leave when ready.";
        	    }

        	    if (prologueStep == 2) {
        	        return "Explore the ancient ruins and inspect the pedestal.";
        	    }

        	    if (prologueStep == 3) {
        	        return "Leave the ruins with the Rusty Creation.";
        	    }

        	    if (prologueStep == 4) {
        	        return "Follow the forest path back home.";
        	    }

        	    return "Continue the prologue.";
        	}
        	

            if (activeQuestName.equals("Cellar Rats")) {
                return "Clear the tavern cellar.";
            }

            if (activeQuestName.equals("Missing Laundry")) {
                return "Collect laundry around town: " + laundryCollected + "/" + LAUNDRY_REQUIRED;
            }

            if (activeQuestName.equals("Flower Picking")) {
                return "Gather medicinal flowers: " + flowersCollected + "/" + FLOWERS_REQUIRED;
            }

            if (activeQuestName.equals("Bandit Trouble")) {
                return "Investigate the old forest road.";
            }

            return "Continue your current request.";
        }

        // Checkmate arc after Bandit Trouble
        if (checkmateStep == 3) {
            return "Follow the Golden Sinners lead near Old Mill Road.";
        }

        if (checkmateStep == 4 && !safehouseUnlocked) {
            return "Report the Old Mill Road findings to the Village Elder.";
        }

        if (checkmateStep == 4 && safehouseUnlocked) {
            return "Investigate the hidden camp west of the old road.";
        }
        
        if (currentMap == safehouseGameMap) {

            if (!inspectedSafehouseChildren || 
                !inspectedSafehouseDoctor || 
                !inspectedSafehouseSupplies || 
                !inspectedSafehouseOrders) {

                return "Explore the hidden camp and inspect what the Golden Sinners are protecting.";
            }

            if (!inspectedKingTent) {
                return "Search deeper in the camp and inspect the King's tent.";
            }

            if (inspectedKingTent && !taliConfrontationCompleted) {
                return "Face the person known as the Bandit King.";
            }

            return "Leave the safehouse and continue the investigation.";
        }

        if (checkmateStep == 5) {
            return "Search the safehouse for the King's tent.";
        }

        if (checkmateStep == 6 && taliTemporaryAlly && !taliRecruited) {
            return "Confront Cael and recover what he stole.";
        }

        if (storyChapter < 2 && checkmateStep == 8 && taliRecruited) {
            return "The Golden Sinners crisis has been resolved.";
        }

        // Chapter 2 / Silas arc
        if (storyChapter == 2 && ruinsJobUnlocked && chapterTwoStep == 2 && !silasBetrayalTriggered) {
            return "Travel to the marked ruins and investigate Silas's relic job.";
        }
        
        if (currentMap == chapterTwoRuinsGameMap) {

            if (!inspectedChapterTwoMural || !inspectedChapterTwoSeal) {
                return "Explore the ruins and inspect the ancient markings.";
            }

            if (!silasBetrayalTriggered) {
                return "Inspect the relic pedestal and keep an eye on Silas.";
            }

            return "Survive the trap and escape the ruins.";
        }

        if (storyChapter == 2 && silasBetrayalTriggered && !williamRecruited) {
            return "Survive the seal trap and escape the ruins.";
        }

        if (storyChapter == 2 && williamRecruited) {
            return "Act One is complete.";
        }

        // Urgent Notice before Bandit Trouble is accepted
        if (banditQuestUnlocked && !banditQuestAccepted && !banditQuestCompleted) {
            return "Read the urgent notice and speak with the Village Elder.";
        }
        
        if (banditQuestCompleted && !banditQuestRewardClaimed && checkmateStep < 3) {
            return "Return to town and report the bandit attack to the Village Elder.";
        }
        
        
        //Chapter 3
        
        if (storyChapter == 3) {

            if (chapterThreeStep == 2 && !corruptedRoadCompleted) {
                return "Investigate the withered road where travelers have vanished.";
            }

            if (chapterThreeStep == 3 && !carnalvalUnlocked) {
                return "Return to camp and examine Silas's invitation.";
            }

            if (chapterThreeStep == 4 && carnalvalUnlocked) {
                return "Follow the ticket's path to the Carnalval of Desires.";
            }

            return "Continue following the corruption trail.";
        }
        
        //Chapter 4
        if (storyChapter == 4) {
        	
        	
        	if (currentMap == carnalvalMainGameMap) {
        	    return "Explore the Main Carnival Grounds and learn the rules of Silas's domain.";
        	}

        	if (currentMap == laughingLaneGameMap) {
        	    return "Explore Laughing Lane's games, booths, and strangely cheerful guests.";
        	}

        	if (currentMap == gildedMidwayGameMap) {
        	    return "Investigate the Gilded Midway and the desires hidden beneath its attractions.";
        	}

        	if (currentMap == performersRowGameMap) {
        	    return "Search Performer’s Row for clues about the Carnalval's bound workers.";
        	}

        	if (currentMap == guestLodgingGameMap) {
        	    return "Rest at the Guest Lodging and inspect the rooms prepared for the party.";
        	}

        	if (currentMap == mainStageGameMap) {
        	    return "Enter the Main Stage and confront Silas.";
        	}

           
        }

        return "No active request.";
    }
    
    //Check Active Quests
    private boolean hasActiveQuest() {
        return activeQuestName != null && !activeQuestName.isEmpty();
    }
    
    //Checks Done Quests
    private boolean isQuestCompleted(String questName) {

        if (questName.equals("Cellar Rats")) {
            return cellarRatsCompleted;
        }

        if (questName.equals("Missing Laundry")) {
            return laundryCompleted;
        }

        if (questName.equals("Flower Picking")) {
            return flowersCompleted;
        }

        return false;
        
    }
    
    
    //Quest: Quest Board Requests Chapter 1
    private void handleQuestBoardSelection() {

    	String selected = questBoardOptions[questBoardIndex];

        if (selected.equals("Leave")) {
            currentState = GameState.TOWN;
            return;
        }

        if (isQuestCompleted(selected)) {
            startDialogue("", new String[] {
                "This request has already been completed."
            }, GameState.QUEST_BOARD);
            return;
        }
        
        

        if (hasActiveQuest()) {
            startDialogue("", new String[] {
                "You already have an active request.",
                "Finish that one before taking another."
            }, GameState.QUEST_BOARD);
            return;
        }
        
        
        if (selected.equals("Urgent Notice") && !banditQuestUnlocked) {
            startDialogue("", new String[] {
                "There are no urgent requests posted right now.",
                "Only small local jobs are available."
            }, GameState.QUEST_BOARD);

            return;
        }
        
        if (selected.equals("Urgent Notice") && banditQuestUnlocked) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A fresh notice is pinned over the older requests.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("", "The writing is hurried. The word 'urgent' is underlined twice.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Art", "This is different from the others.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "It says road attacks have gotten worse.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Finally. A real request.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Dean.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "...A serious request. I meant serious.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("", "The notice asks capable adventurers to speak with the Village Elder.", 
                		DialogueSide.RIGHT, DialogueFaction.NPC)
            }, GameState.QUEST_BOARD);

            return;
        }

        pendingQuestName = selected;
        questConfirmOpen = true;
        questConfirmIndex = 0;  
        
    }
    
    //Saying no just goes back
    private void declineQuest() {
        questConfirmOpen = false;
        pendingQuestName = "";
        questConfirmIndex = 0;
    }
    
    //Starts the quest line RIGHT AWAY
    private void acceptQuest(String questName) {

    	activeQuestName = "Cellar Rats";
        questConfirmOpen = false;
        pendingQuestName = "";

        if (questName.equals("Cellar Rats")) {

            BattleScenario scenario = BattleScenarioLibrary.getScenario("cellar_rats");
            pendingScenarioIntroAfterQuestAccept = scenario;
           

            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "Our first official board job!", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "It says cellar rats.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Every legend starts somewhere.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Usually not under a tavern.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Under a tavern, over a tavern, beside a tavern. History will not care about the angle.", 
                		DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.QUEST_BOARD);

            return;
        }

        if (questName.equals("Missing Laundry")) {

            laundryCollected = 0;
            activeQuestName = "Missing Laundry";
            updateTownQuestTiles();

            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "Missing laundry?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "It says the wind scattered it across town.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "If it helps someone, it counts.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Fine. But if anyone asks, we fought the laundry devil.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Please don't tell people that.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.QUEST_BOARD);

            return;
        }

        if (questName.equals("Flower Picking")) {

            flowersCollected = 0;
            activeQuestName = "Flower Picking";

            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "This one is for medicinal flowers.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "Flowers. Great. Very cool.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Medicine is good Dean.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I know.", DialogueSide.LEFT, DialogueFaction.ALLY),
            }, GameState.QUEST_BOARD);

            pendingFlowerFieldStart = true;

            return;
        }
        
    }
    
    //Pre Quest Events
    //Chapter 1
    
    //Updates Laundry collected
    private void updateTownQuestTiles() {

        if (townGameMap == null) return;

        Tile[][] tiles = townGameMap.getTiles();

        // Only show laundry if Missing Laundry is active and not completed
        if (activeQuestName.equals("Missing Laundry") && !laundryCompleted) {

            if (laundryCollected < 1) {
                tiles[2][2] = new Tile(TileType.LAUNDRY);
            }

            if (laundryCollected < 2) {
                tiles[7][2] = new Tile(TileType.LAUNDRY);
            }

            if (laundryCollected < 3) {
                tiles[5][7] = new Tile(TileType.LAUNDRY);
            }

        } else {
            // Clear possible laundry spots when quest is not active
            if (tiles[2][2].getType() == TileType.LAUNDRY) {
                tiles[2][2] = new Tile(TileType.GRASS);
            }

            if (tiles[7][2].getType() == TileType.LAUNDRY) {
                tiles[7][2] = new Tile(TileType.GRASS);
            }

            if (tiles[5][7].getType() == TileType.LAUNDRY) {
                tiles[5][7] = new Tile(TileType.GRASS);
            }
            
        }
        
    }
    
    //Collect Laundry
    private void collectLaundry() {

        if (!activeQuestName.equals("Missing Laundry") || laundryCompleted) {
            startDialogue("", new String[] {
                "It is just laundry."
            }, GameState.TOWN);
            return;
        }

        laundryCollected++;

        // Remove the current tile
        currentMap.getTiles()[player.col][player.row] = new Tile(TileType.GRASS);

        if (laundryCollected >= LAUNDRY_REQUIRED) {
            completeMissingLaundryQuest();
            return;
        }

        startDialogue("", new String[] {
            "You collected a piece of laundry.",
            "Laundry collected: " + laundryCollected + "/" + LAUNDRY_REQUIRED
        }, GameState.TOWN);
        
    }
    
    
    //Collect Flowers
    private void startFlowerPickingMap() {

        currentMap = flowerFieldGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 1;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "A short walk beyond town, the forest opens into a quiet patch of wild grass.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "The flowers should grow around here. Look for the pink ones.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Understood. We're hunting pink flowers.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Picking. We're picking them.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Right. Tactical picking.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
    }
    
    //Collecting Flowers
    private void collectFlower() {

        if (!activeQuestName.equals("Flower Picking") || flowersCompleted) {
            startDialogue("", new String[] {
                "These flowers are pretty, but you do not need them right now."
            }, GameState.EXPLORATION);
            return;
        }

        flowersCollected++;

        currentMap.getTiles()[player.col][player.row] = new Tile(TileType.GRASS);

        if (flowersCollected >= FLOWERS_REQUIRED) {
            completeFlowerPickingQuest();
            return;
        }

        startDialogue("", new String[] {
            "You picked medicinal flowers.",
            "Flowers collected: " + flowersCollected + "/" + FLOWERS_REQUIRED
        }, GameState.EXPLORATION);
    }
    
    //Step 4
    private void enterSafehouse() {

        currentMap = safehouseGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 1;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "West of the old road, the trees open into a hidden camp.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Wait...this is it? The Bandit King's hideout?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "There are children here...", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Weapons down. No sudden moves.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Golden Sinner", "Please, If you're here to swing steel, turn around.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "We're here for answers.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Golden Sinner", "We all are... Come in.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);
        
    }
    
    //Step 5
    
    private void handleKingTentEvent() {

        if (inspectedKingTent) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Art", "The tent is empty now.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "But whatever happened here... it changed things.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        inspectedKingTent = true;
        pendingTaliConfrontation = true;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The largest tent sits apart from the rest of the camp.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Biggest tent. Has to be the King's.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Dean, lower your voice.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "No guards outside. That's strange.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Maybe The King is dramatic. Villains love dramatic timing.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Or maybe we shouldn't be standing here.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("", "Inside, there is no throne. No stolen crown. No pile of coin.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "Maps. Supply routes. Names of villages.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Some of these are circled out.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "That is not ominous of him at all.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "You three really are nosy.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("", "A tall and toned lady in her 20's appeared behind them. Her skin was the color of "
            		+ "a fair bronze. Her black hair fitted into a high pony tail. Her spear resting on her shoulder.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Ah. Dramatic timing. I called it. Wait a womens voice?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "Cael said three little rats were sniffing around my camp.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Art", "We're not here for the people in this camp.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "No? Just my tent, my maps, and my weapons?", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Penelope", "Your people attacked roads and villages.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("???", "My people were attacked first.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Penelope", "By who?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "By you.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Art", "Someone lied to you.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "Funny. I was about to say the same thing.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("???", "You came looking for The King?", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Dean", "I was expecting a bulky guy... with a nicer voice? Maybe with a cool crown?", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("???", "That better not be disappointment in those words.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Dean", "Not at all ma'am", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "I'm Tali Sin.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Tali", "And I am The King you seek.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
            new DialogueLine("Art", "Then we need answers.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "You won't be alive to hear them.", DialogueSide.RIGHT, DialogueFaction.ENEMY)
        }, GameState.EXPLORATION);
        
    }
    
    
    //Done Quest here
    //Chapter 1
    
    //Cellar Rat
    private void completeCellarRatsQuest() {

        if (cellarRatsCompleted) {
            return;
        }

        cellarRatsCompleted = true;
        activeQuestName = "";
        gold += 20;

        addBattleMessage("Cellar Rats completed!");
        addBattleMessage("Received 20 gold.");
        
        checkStarterJobProgress();
        
    }
    
    //Missing Laundry
    private void completeMissingLaundryQuest() {

        laundryCompleted = true;
        activeQuestName = "";
        gold += 10;

        updateTownQuestTiles();

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "You gathered the last piece of missing laundry.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "And thus the town's socks were saved.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "It was helpful, Dean.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Helpful is enough for now.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "Missing Laundry completed. Received 10 gold.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.TOWN);
        
        checkStarterJobProgress();
    }
    
    //Flowers
    private void completeFlowerPickingQuest() {

        flowersCompleted = true;
        activeQuestName = "";
        gold += 15;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "You gathered the last bundle of medicinal flowers.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "These should help the healer prepare more medicine.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "And not a single flower monster. Shame.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Maybe that is for the best.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "Flower Picking completed. Received 15 gold.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);
        
        checkStarterJobProgress();
    }
    
    //Are they completed? If so move on below
    
    private boolean areStarterJobsComplete() {
        return cellarRatsCompleted && laundryCompleted && flowersCompleted;
    }
    
    private void checkStarterJobProgress() {

    	if (!starterJobsComplete && areStarterJobsComplete()) {
            starterJobsComplete = true;
            pendingStarterJobsCompleteScene = true;
        }
    }
    
    
    
    //Chapter 1 Major Step
    private void startStarterJobsCompleteScene() {

    	banditQuestUnlocked = true;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "About a month passed.", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The work was not glorious. Rats, laundry, flowers, leaky roofs, missing tools, and one very angry goose.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "For the record, that goose was absolutely trained.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Dean, it was protecting its nest.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "No way! Someone trained that thing for attack purposes.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "People are starting to trust us. That matters.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Even if the jobs were small, they helped.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Small jobs. Big hearts. Such is life.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "Then, one morning, a new request appeared on the board. Unlike the others, this one was marked urgent.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.TOWN);
        
    }
    
    private void completeOldMillRoad() {

        if (oldMillRoadCompleted) {
            return;
        }

        oldMillRoadCompleted = true;

        if (checkmateStep < 4) {
            checkmateStep = 4;
        }

        activeQuestName = "";

        addBattleMessage("Old Mill Road cleared!");
        addBattleMessage("The Golden Sinners are targeting supply roads.");

        System.out.println("Old Mill Road complete! Checkmate step: " + checkmateStep);
        
    }
    
    //End of Tali Boss Fight
    private void completeTaliConfrontation() {

        if (taliConfrontationCompleted) {
            return;
        }

        taliConfrontationCompleted = true;

        if (checkmateStep < 6) {
            checkmateStep = 6;
        }

        taliTemporaryAlly = true;

        activeQuestName = "";

        updateOverworldQuestTiles();

        addBattleMessage("Tali defeated!");
        addBattleMessage("Cael's betrayal has been revealed.");
        addBattleMessage("Tali will fight with you for now.");

        System.out.println("Tali is now a temporary ally.");
        
    }
    
    //Cael Final Chapter Fight
    private void completeCaelBattle() {

        if (checkmateStep < 8) {
            checkmateStep = 8;
        }

        activeQuestName = "";

        recruitTali();

        pendingChapterOneEnding = true;

        updateOverworldQuestTiles();

        addBattleMessage("Cael defeated!");
        addBattleMessage("The Golden Sinners are broken.");
        addBattleMessage("Tali joined the party!");

        System.out.println("Cael battle complete! Checkmate step: " + checkmateStep);
        
    }
    
    
    //Chapter 1 end
    private void startChapterOneEnding() {
    	
    	pendingChapterOneCamp = true;

        currentMap = townGameMap;
        showMapTitle();
        currentState = GameState.TOWN;

        player.col = 5;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "By the time the party returned to the village, word had already reached the streets.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The road attacks had stopped. Supplies returned. Merchants spoke the party's name with relief.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The refugees of the Golden Sinners much to Tali's relief were offered permanent shelter at the village", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            
            new DialogueLine("Townsperson", "That's them! The ones who dealt with the Bandits!", DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Did you hear that? 'The ones.' That's practically a title.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Please do not make them regret thanking us.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "We only did what needed doing.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Classic hero answer. Very clean. Very humble. I give it an eight.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Village Elder", "You returned with a village breathing easier because of you.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "We had help.", DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("Tali", "Do not look at me like that. I am not part of the parade.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Too late. You are absolutely in the parade now.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Try putting flowers on me and I bite.", DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("", "That night, away from the noise and gratitude, the party made camp beyond the village lights.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.TOWN);
        
    }
    
    
    //Chapter 1 Camp end
    private void startChapterOneCampReflection() {
    	
    	pendingAdvanceToChapterTwo = true;
    	
    	openCampWithReturn(GameState.OVERWORLD, overworldGameMap, 2, 5);

        currentState = GameState.CAMP;
        campMenuIndex = 0;

        startDialogue(new DialogueLine[] {
            new DialogueLine("Dean", "So. We are officially heroes now, right?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "The village thanked us but not quite yet", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "It's close enough for tonight.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "It felt strange. Hearing people say our names like that.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Good strange or bad strange?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Both.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "People cheer when they are scared and someone else bleeds for them.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "That is one way to ruin a victory campfire.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "You wanted honest or cozy?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Maybe both matter.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "People needed help. We helped. But it does not mean everything is fixed.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "The crops are still failing.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Animals are still running from the woods.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "And whatever scared my people into following Cael did not start with Cael.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then tomorrow, we decide where to look next.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Tomorrow. Tonight, I am accepting that we are at least village-level heroes.", 
            		DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Village-level?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "It is a rank. I made it up just now.", DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.CAMP);
        
        
    }
    
    /*
     * CHAPTER 2 BEGINS HERE
     * !!!
     */
    
    private void startChapterTwoOpening() {

        chapterTwoStep = 1;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "Morning came quiet over the camp, but the road ahead did not feel as simple as it had the day before.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Dean", "So, where does a group of village-level heroes go after saving the roads?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Preferably somewhere people stop calling us heroes.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "You say that now, but the title grows on you.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Like mold.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Penelope", "The elder said the roads are safer, but not safe.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then we keep moving. If something else is spreading, we find where it starts.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("Silas", "A noble intention. Expensive, though.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "WOAH! Who are you and how long were you standing there?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "A small dwarvish man stands before them. The big bellied man had red hair and large moustache."
            		+ "he is wearing typical merchant coveralls.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Silas", "Long enough to hear the word 'heroes' used with confidence.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Tali", "That was his mistake, not ours.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            
            new DialogueLine("Silas", "Silas Vale. Merchant, collector, and occasional employer of capable people.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "What kind of work?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Silas", "Ruins west of here. Old stone. Older locks. I need guards while I retrieve a relic.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Penelope", "A relic?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Silas", "A harmless one, if handled by someone who knows its worth.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Dean", "That sentence had at least three suspicious parts.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Four.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Silas", "You may keep any ordinary salvage. I only require the relic. Payment upfront, half now.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Art", "We'll think about it.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Silas", "Of course. But ruins do not stay quiet forever.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The dwarven man waddles away with his carraige.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("William", "And neither do men who lie about them.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Great. New mysterious person.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "William Winters. And if Silas Vale sent you toward those ruins, "
            		+ "then you are already closer to danger than you understand.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "The man was deshveled and his mage like clothes were dirty. A tome on his hip."
            		+ "His dark hair fading with grey.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            
            new DialogueLine("William", "If you enter those ruins, do not touch anything sealed.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "That is extremely specific advice.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Specific advice is usually the useful kind.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Art", "You know something.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "I know enough to say you should be careful where it wakes.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Where it wakes?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "Ask me again if you survive Silas's job.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "I hate that answer.", DialogueSide.RIGHT, DialogueFaction.ALLY),
        }, GameState.CAMP);

        pendingRuinsJobUnlock = true;
        
    }
    
    
    private void unlockChapterTwoRuinsJob() {

        chapterTwoStep = 2;
        ruinsJobUnlocked = true;

        updateStoryWorldState();

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The ruins Silas mentioned have been marked on your map.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "I still don't trust the guy with the too-clean merchant smile.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Good. That means at least one thing is working in your head.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "William seemed worried.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then we go carefully. We get some answers before anyone gets hurt.",
                    DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.CAMP);
        
    }
    
    
    //Chapter 2 Step 1 Ruins
    private void enterChapterTwoRuins() {

        currentMap = chapterTwoRuinsGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 1;
        player.row = 8;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The ruins wait beyond the old road, half-buried beneath leaning stone and tangled roots.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Silas", "There you are. I was beginning to think caution had won.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Caution never wins. It just complains until we arrive.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "I do not complain.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Sometimes.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Enough. We go in, watch each other, and keep our eyes on Silas.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Silas", "Wise. Suspicion keeps the blood moving.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "I liked him more before he said that.",
                    DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
        
    }
    
    
    private boolean isGolemSealTrap() {
        return currentBattleScenario != null &&
               currentBattleScenario.getId().equals("golem_seal_trap");
    }
    
    private boolean isStoneGolem(BattleUnit unit) {
        return unit != null && unit.getName().equals("Stone Golem");
    }
    
    //Calls Stone Golem
    private BattleUnit getStoneGolem() {

        for (BattleUnit enemy : enemyUnits) {
            if (enemy != null && enemy.isAlive() && enemy.getName().equals("Stone Golem")) {
                return enemy;
            }
        }

        return null;
    }
    
    //Sequence of events for Golem trap
    private boolean handleGolemTrapTurnEvents() {

        if (!isGolemSealTrap()) {
            return false;
        }

        if (currentBattleTurn == 2 && !golemTurn2DialogueShown) {
            golemTurn2DialogueShown = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("Dean", "Okay, I just hit it and It did not care.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Gah, same here. My spear is hitting stone.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "Our weapons are barely scratching it.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "I'm trying to suppress it's magical aura... I..can't", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "So the plan is... to keep losing slowly?", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "The plan is stay alive and look for an opening. There has "
                		+ "gotta be a way. Hold on everyone!", DialogueSide.LEFT, DialogueFaction.ALLY)
            }, GameState.BATTLE);

            return true;
            
        }

        
        if (currentBattleTurn == 3 && !golemTurn3DialogueShown) {
            golemTurn3DialogueShown = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("Penelope", "I can't keep this up forever.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Can't die here...", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I would like to formally vote for not dying.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "The Creation keeps shielding me from the pulse.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Only you?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Art", "...Yeah.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Then unless that sword learns to share, we're going to need help.", 
                		DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.BATTLE);

            return true;
        }

        
        if (currentBattleTurn == 4 && !williamArrivedForGolem) {
            williamArrivedForGolem = true;
            pendingWilliamGolemRescue = true;

            startDialogue(new DialogueLine[] {
                new DialogueLine("", "A sharp crack of blue light cuts across the chamber.", DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("William", "Quickly step away from the center seal!", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "William? Oh good. The mysterious warning man came back.", DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("William", "Preferably before the guardian turns you into ash.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Penelope", "Can you stop it?", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("William", "No.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Bad start.", DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("William", "But I can break what is waking it.", DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.BATTLE);

            
            return true;
        }

        
        return false;
    }
    
    
    
    private void performWilliamGolemRescue() {

        BattleUnit william = createUnitFromId("william_guest", 4, 3, false);

        if (william != null) {
            playerBattleUnits.add(william);
            addBattleMessage("William Winters joined the battle!");
        }

        BattleUnit golem = getStoneGolem();

        if (golem != null && golem.isAlive()) {
            addBattleMessage("William casts Arcane Break!");
            golem.takeDamage(golem.getHp());
            addBattleMessage("The Stone Golem collapses!");
        }

        handleBattleVictory();
    }
    
    //Outside now
    private void drawRuinsExterior(Graphics g) {

        g.setColor(new Color(12, 18, 28));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Night sky / distant trees
        g.setColor(new Color(25, 45, 35));
        g.fillRect(0, mapHeight - 120, mapWidth, 120);

        // Ruins silhouette
        g.setColor(new Color(55, 55, 65));
        g.fillRect(120, mapHeight - 210, 80, 160);
        g.fillRect(210, mapHeight - 170, 180, 120);
        g.fillRect(410, mapHeight - 210, 70, 160);

        // Ruins doorway
        g.setColor(new Color(10, 10, 15));
        g.fillRect(260, mapHeight - 120, 70, 70);

        // Moon / pale light
        g.setColor(new Color(220, 220, 240));
        g.fillOval(60, 50, 45, 45);

        g.setColor(Color.WHITE);
        g.drawString("Outside the Ruins", 30, 30);
    }
    
    //Chapter 2 Part 4
    private void startWilliamRecruitmentScene() {

        recruitWilliam();

        pendingActOneEnding = true;
        showingRuinsExteriorScene = true;

        startDialogue(new DialogueLine[] {
        		
            new DialogueLine("", "The party stumbles out of the ruins as the last tremors fade beneath the stone.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "I vote we never take jobs from smiling merchants again.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "That was your first rule? Mine is to find Silas and break his teeth.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Can everyone please breathe before making threats?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "It's natural you feel that way. Threats can wait until after bleeding stops.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Art", "Huff...Silas escaped with the relic.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Yes. And if that relic is what I believe it is, he will search for more.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Wonderful. The evil thief has a shopping list.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("Art", "You knew the ruins were dangerous.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "I knew enough to be afraid of them.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "And enough to save us.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "Barely.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("", "William eye's the rusty sword on Art's holster.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Art", "You know something about The Sword don't you.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "I know it should not have answered you.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "But it did.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Yes. Which means either the world has become desperate, or you have.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "That was supposed to be reassuring?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "No. This is.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "Art, I do not know everything your sword is tied to.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "But I know this much... relics like that do not wake for nothing.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "If it chose your hand, then some good may still be possible.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "I'm not following?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Certain doom.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "I think that was encouraging. Maybe.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "It was the closest he has gotten.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("William", "Silas will not stop. If you mean to follow him, you will need someone who understands old magic.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then come with us.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "I was hoping you would say that after I saved your lives.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Narrator", "William Winters joined the party.",
                    DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.BATTLE);
    }
    
    //End of Act 1
    private void startActOneEnding() {
    	
    	pendingEndActOneTransition = true;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "Silas vanished with the relic before the ruins grew quiet again.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "The party left with more questions than answers, and one more companion than they had entered with.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Dean", "So. Bandits, lava, golems, creepy relics, suspicious merchants.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Mannnn, I miss the rats.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "I never thought I would agree with that.", DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Tali", "Whatever Silas stole, people will bleed for it.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "Likely.", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "You could have softened that.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Unlikely.", DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Art", "Then we follow him.", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Even if this is bigger than us?", DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Especially then.", DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("", "In Art's hand, the Rusty Creation stirred with pale light.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("William", "There is a name in old records. One most scholars dismiss as myth.", 
            		DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "What name?", DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Marrtyme.", DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("", "The road ahead no longer led toward village troubles or simple jobs.", 
            		DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Something old had begun to wake.", DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.BATTLE);
        
    }
    
    //Draws end of act one scenes
    private void drawActOneEnd(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        Font oldFont = g.getFont();

        g.setColor(Color.WHITE);
        g.setFont(oldFont.deriveFont(34f));

        String title = "End of Act One";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, screenHeight / 2 - 40);

        g.setFont(oldFont.deriveFont(16f));
        String subtitle = "Lost Time: Rewrite";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (screenWidth - subWidth) / 2, screenHeight / 2);

        String prompt = "Press ENTER to return to title.";
        int promptWidth = g.getFontMetrics().stringWidth(prompt);
        g.drawString(prompt, (screenWidth - promptWidth) / 2, screenHeight / 2 + 60);

        g.setFont(oldFont);
    }
    
    
    /*
     * Start of ACT 2!!
     * 
     */
    
    //Events List and Methods for Act 2 
    private void handleCarnalvalEvent(String eventId) {

        if (eventId == null) {
            showCarnalvalEmptyInteraction();
            return;
        }

        if (eventId.equals("carnalval_rules_sign")) {
            startDialogue(new DialogueLine[] {
                new DialogueLine("Narrator", "A painted sign stands beneath a string of golden lanterns.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Narrator", "WELCOME, HONORED GUESTS!",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Narrator", "Rule One: Enjoy yourself.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Narrator", "Rule Two: Do not damage the attractions.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Narrator", "Rule Three: Do not ask where the exits went.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Narrator", "Rule Four: The Ringmaster is always listening.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Dean", "I liked Rule One. Then it got worse every sentence.",
                        DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Rule Four is the only honest one.",
                        DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            return;
        }

        if (eventId.equals("carnalval_exit_loop")) {
            startDialogue(new DialogueLine[] {
            		
                new DialogueLine("", "The entrance gate still stands behind the party, red and gold beneath the lanterns.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("", "Beyond it should be the withered road.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("", "Instead, the path bends gently back toward the Main Carnival Grounds.",
                        DialogueSide.RIGHT, DialogueFaction.NPC),
                new DialogueLine("Penelope", "That road was straight when we came in.",
                        DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("William", "It still is. It is simply straight in a direction that no longer helps us.",
                        DialogueSide.RIGHT, DialogueFaction.ALLY),
                new DialogueLine("Dean", "I hate magic that understands comedy.",
                        DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Art", "So leaving is not simple.",
                        DialogueSide.LEFT, DialogueFaction.ALLY),
                new DialogueLine("Tali", "Good. Means we stop pretending this place is friendly.",
                        DialogueSide.RIGHT, DialogueFaction.ALLY)
            }, GameState.EXPLORATION);

            carnalvalExitDiscovered = true;
            return;
        }

        showCarnalvalEmptyInteraction();
    }
    
    
    private void showCarnalvalEmptyInteraction() {

        String message = "The music seems to follow your footsteps.";

        if (currentMap == carnalvalMainGameMap) {
            message = "Lanterns sway above the Main Grounds, though there is no wind.";
        }
        else if (currentMap == laughingLaneGameMap) {
            message = "Somewhere nearby, a guest laughs one beat too late.";
        }
        else if (currentMap == gildedMidwayGameMap) {
            message = "Gold-painted signs promise prizes no one seems willing to describe.";
        }
        else if (currentMap == performersRowGameMap) {
            message = "Behind the tents, quiet voices stop as soon as you listen.";
        }
        else if (currentMap == guestLodgingGameMap) {
            message = "The lodging tents are warm, clean, and much too prepared.";
        }
        else if (currentMap == mainStageGameMap) {
            message = "The stage lights wait in silence.";
        }

        startDialogue(new DialogueLine[] {
            new DialogueLine("", message, DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.EXPLORATION);
        
    }
    
    
    
    //SKIP End of Events List for Act 2
    
    private void startActTwo() {

        storyChapter = 3;
        chapterThreeStep = 0;

        pendingActTwoOpening = true;

        startStoryTransition("Several Months Later...");
        
    }
    
    //Chapter 3
    private void startChapterThreeOpening() {

        chapterThreeStep = 1;

        openCampWithReturn(GameState.OVERWORLD, overworldGameMap, 3, 5);

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "For months, the party followed every rumor of Silas Vale.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "They guarded caravans, cleared roads, chased false sightings, "
            		+ "and helped villages that seemed weaker by the week.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "They found wilted fields beneath grey rain. They found animals twisted into things they no longer recognized.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "They found stories of a smiling merchant. But never Silas himself.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Dean", "That makes four caravans saved, two bridges cleared, and one extremely rude weregoat defeated.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "It wasn't a goat.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "It had horns and hated me. Close enough.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("Penelope", "The caravan driver is still shaking when I left. He should be okay within the week.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "He had just been chased by antlered abomination. I would shake too.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "It was more than fear. He kept asking if the rain was watching him.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Art", "We helped them. But we are no closer to Silas.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Roads are getting worse. People are getting desperate. "
            		+ "That is usually when monsters start wearing more friendly faces.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "A deeply unpleasant image. Also, not inaccurate.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Dean", "So what is the scholarly answer? Ancient curse? Bad weather? Extremely committed mother natures revenge?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "I have a theory.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Is it comforting?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Not really.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("William", "Whatever Silas stole did not create this corruption alone. "
            		+ "It shaped something already leaking through.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Marrtyme.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Possibly. Or something stirred by the same wound.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Penelope", "Well, we keep arriving after people are already hurt. Is there anything we can do?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "We can keep moving until we arrive sooner.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "That's almost impossible.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Almost is generous.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "...We keep moving.",
                    DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.CAMP);

        pendingCorruptedRoadMission = true;
        
    }
    
    
    private void unlockCorruptedRoadMission() {

    	chapterThreeStep = 2;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "By morning, another report reached the camp.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Travelers had vanished beyond the eastern border road,"
            		+ " where the grass had turned gray and the trees no longer carried leaves.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Penelope", "Guys...that is farther than we have gone before.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Good. Means we are finally leaving the same dead ends behind.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "I liked some of those dead ends. One had decent soup.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("William", "The report mentioned music in the rain.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Music?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("William", "Possibly. Maybe they misheard.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Tali", "Sounds like bait.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Can we call it a lead instead? Bait makes me feel like the worm.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "If people are missing, we cannot ignore it.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Art", "Then we move east. Carefully.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("", "The party leaves the familiar roads behind and follows the corruption east.",
                    DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.CAMP);

        pendingMoveToActTwoWorld = true;
        
    }
    
    
    private void enterActTwoWorld() {

        currentMap = actTwoWorldGameMap;
        showMapTitle();
        currentState = GameState.OVERWORLD;

        player.col = 1;
        player.row = 8;

        movementLeft = maxMovement;

        updateActTwoWorldQuestTiles();

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The eastern roads stretch beneath a sky the color of old ash.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Dead grass bends without wind. Far ahead, the road cracks into the withered region.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "Well. This place has a welcoming amount of awful.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Keep your weapon close.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "And your expectations low.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "We find the missing travelers first.",
                    DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.OVERWORLD);
        
    }
    
    
    
    //Step 3
    private void startSilasTicketScene() {

        pendingSilasTicketScene = false;

        chapterThreeStep = 4;
        carnalvalUnlocked = true;

        updateActTwoWorldQuestTiles();

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "That night, the campfire burns blue.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "A thin melody drifts through the dead trees, bright and distant, like carnival music played underwater.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Penelope", "Does anyone else hear that?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "Please say no. I was hoping this was only a me problem.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("", "A black-and-gold ticket unfolds from the smoke and lands in Art's hand without burning.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Art", "It has our names on it.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Of course it does.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("", "To Art Forger and honored companions:",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Silas Vale welcomes you to the Carnalval of Desires.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Lodging provided. Answers available. Regrets optional. Departure negotiable.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),

            new DialogueLine("Dean", "Departure negotiable? I hate when that happens",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Burn it.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "I would advise against that.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "Why?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "Because I suspect we have no choice in this.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),

            new DialogueLine("Penelope", "He knows where we are.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then he wanted us to know he knows.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("Dean", "So we are not going, right?",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "We're absolutely going.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Dean", "I knew that. I just wanted to hear someone else make the bad decision first.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("William", "Silas has the relic. If the ticket is genuine, this is the first direct invitation he has given us.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "And if it is a trap?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "It is.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Then we walk in knowing it is one.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),

            new DialogueLine("", "By morning, a road waits where no road had been before.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "Far beyond the dead trees, red and gold lights shimmer in the gray mist.",
                    DialogueSide.RIGHT, DialogueFaction.NPC)
        }, GameState.OVERWORLD);
        
    }
    
    
    private void enterCarnalvalGate() {

        storyChapter = 4;
        chapterFourStep = 1;
        carnalvalEntered = true;

        currentMap = carnalvalMainGameMap;
        showMapTitle();
        currentState = GameState.EXPLORATION;

        player.col = 5;
        player.row = 8;
        movementLeft = maxMovement;

        startDialogue(new DialogueLine[] {
            new DialogueLine("", "The road bends toward lights that should not fit beneath the dead trees.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("", "One step carries the party beneath a red-and-gold archway. The music swells.",
                    DialogueSide.RIGHT, DialogueFaction.NPC),
            new DialogueLine("Dean", "I know this is bad, but I am a little curious.",
                    DialogueSide.LEFT, DialogueFaction.ALLY),
            new DialogueLine("Tali", "That is how people die by the way.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "The space feels wrong.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Penelope", "Wrong how?",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("William", "There is more carnival inside the gate than there is land outside of it.",
                    DialogueSide.RIGHT, DialogueFaction.ALLY),
            new DialogueLine("Art", "Stay close everyone.",
                    DialogueSide.LEFT, DialogueFaction.ALLY)
        }, GameState.EXPLORATION);
        
    }
    
    
    
    
    
    //END OF STORY Skip
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
    
    
    //NPC interaction more forgiving
    private NPC getNpcAt(int col, int row) {

        for (NPC npc : townNpcs) {
            if (npc.getCol() == col && npc.getRow() == row) {
                return npc;
            }
        }

        return null;
    }
    
    //NPC interaction 
    private void interactWithNpc(NPC npc) {

        if (npc == null) return;

        
        // multi-speaker towns person conversation tester
        if (npc.getName().equals("Townsperson")) {
        	
        	if (storyChapter == 0) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Townsperson", "You children should stay away from those ruins.", 
                    		DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("Art", "We were just looking around.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.TOWN);

                return;
            }

            if (storyChapter >= 1) {
                startDialogue(new DialogueLine[] {
                    new DialogueLine("Townsperson", "The crops have been failing lately.", DialogueSide.RIGHT, DialogueFaction.NPC),
                    new DialogueLine("Art", "Something feels wrong in Cerebella.", DialogueSide.LEFT, DialogueFaction.ALLY)
                }, GameState.TOWN);

                return;
            }
        }

        
        // Normal non-quest NPC dialogue
        if (!npc.hasQuest()) {
            startDialogue(npc.getName(), npc.getDefaultDialogue(), GameState.TOWN);
            return;
        }
        
        //Not selected Quest Yet      
        // Quest NPC dialogue
        if (npc.getQuestId().equals("bandit_quest")) {

            if (!banditQuestUnlocked) {
                startDialogue(npc.getName(), new String[] {
                    "You three have been helping around town lately.",
                    "Small work, maybe, but people have noticed.",
                    "Keep at it. Trust is earned one task at a time."
                }, GameState.TOWN);

                return;
            }

            if (!banditQuestAccepted) {
                acceptBanditQuest();

        	    startDialogue(new DialogueLine[] {
        	        new DialogueLine("Village Elder", "You saw the notice, then.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "The road attacks?", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "Yes. At first it was missing goods. Then broken wagons. "
        	        		+ "Now travelers have stopped coming altogether.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Penelope", "That is why the market has been so empty.", DialogueSide.RIGHT, DialogueFaction.ALLY),
        	        new DialogueLine("Dean", "Bandits picking on supply roads. Sounds like they need picking back.", 
        	        		DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "This is not like the goose, Dean.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Dean", "I know.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Dean", "...Mostly.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Art", "If people are getting hurt, we will look into it.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "Then start near the old forest road. That is where the last caravan vanished.",
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("", "The location has been marked on your map.", DialogueSide.RIGHT, DialogueFaction.NPC)
        	    }, GameState.TOWN);

        	    return;
        	}

        	if (isBanditQuestActive()) {
        	    startDialogue(new DialogueLine[] {
        	        new DialogueLine("Village Elder", "The old forest road is where you should begin.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "If the reports are true, the attacks are not random anymore.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "We will be careful.", DialogueSide.LEFT, DialogueFaction.ALLY)
        	    }, GameState.TOWN);

        	    return;
        	}
        	
        	//Finishing bandit ambush talk to elder
        	if (checkmateStep == 2 && banditQuestCompleted && !banditQuestRewardClaimed) {
        	    gold += 100;
        	    banditQuestRewardClaimed = true;
        	    
        	    if (checkmateStep < 3) {
        	        checkmateStep = 3;
        	    }
        	    
        	    updateOverworldQuestTiles();

        	    startDialogue(new DialogueLine[] {
        	        new DialogueLine("Village Elder", "You found their mark?", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "A gold coin split by a black line.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Penelope", "The merchants called them the Golden Sinners.", DialogueSide.RIGHT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "Then the rumors are true.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Dean", "Rumors are usually more fun when they are fake.", 
        	        		DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "They were once just scattered thieves. Now someone is organizing them.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "Someone called the King?", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "That is what they call their leader. No one seems to know his face.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Dean", "A mysterious Bandit King. I hate that the name is good.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "Take this for what you have done. But if you continue this path, be careful.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("", "Received 100 gold.", DialogueSide.RIGHT, DialogueFaction.NPC)
        	    }, GameState.TOWN);
        	    
        	    

        	    return;
        	}
        	
        	
        	//Checkmate step 4
        	
        	if (checkmateStep == 4) {
        		
        		safehouseUnlocked = true;
        		updateOverworldQuestTiles();
        		
        		
        	    startDialogue(new DialogueLine[] {
        	        new DialogueLine("Village Elder", "So it is true. They are not simply robbing travelers anymore.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "They said The King wanted the road cleared.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "Cleared...", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "That road brings grain, medicine, letters from sons and daughters who left home.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "It is not just dirt and wagon tracks. It is how villages like ours keep breathing.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Penelope", "Then they knew exactly what they were doing.", DialogueSide.RIGHT, DialogueFaction.ALLY),
        	        new DialogueLine("Dean", "Then we make them regret it.", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "The Golden Sinners were once a nuisance. Dangerous, yes, but scattered.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "Now they move like a fist. One name. One banner. One King.", DialogueSide.RIGHT, 
        	        		DialogueFaction.NPC),
        	        new DialogueLine("Art", "Where do we find him?", DialogueSide.LEFT, DialogueFaction.ALLY),
        	        new DialogueLine("Village Elder", "There are rumors of a camp west of the old road. If The King has men nearby, someone"
        	        		+ " there will know more.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "But listen to me. Do not underestimate them.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "They burned one storehouse already. If they decide on this village, "
        	        		+ "we may not get another warning.", DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "Then we go before they do.", DialogueSide.LEFT, DialogueFaction.ALLY)
        	    }, GameState.TOWN);

        	    return;
        	}

        	//Repeat Dialogue
        	if (banditQuestRewardClaimed) {
        	    startDialogue(new DialogueLine[] {
        	        new DialogueLine("Village Elder", "The Golden Sinners will not ignore your interference.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Village Elder", "If you keep following this trail, you may find more than road bandits.", 
        	        		DialogueSide.RIGHT, DialogueFaction.NPC),
        	        new DialogueLine("Art", "Thank you, we'll keep our eyes open.", DialogueSide.LEFT, DialogueFaction.ALLY)
        	    }, GameState.TOWN);

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
    //Highlights all targets and hover colors over selected target
    private void drawTargetHighlight(Graphics g) {
    	
    	if ((!battleTargetSelectOpen && !battleSkillTargetSelectOpen) || availableTargets.isEmpty()) {
            return;
        }

        for (int i = 0; i < availableTargets.size(); i++) {

            BattleUnit target = availableTargets.get(i);

            if (target == null || !target.isAlive()) {
                continue;
            }

            int x = target.getCol() * tileSize;
            int y = target.getRow() * tileSize;

            if (i == currentTargetIndex) {
                g.setColor(Color.YELLOW);
                g.drawRect(x, y, tileSize, tileSize);
                g.drawRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
            } else if (battleSkillTargetSelectOpen) {
                g.setColor(Color.MAGENTA);
                g.drawRect(x + 4, y + 4, tileSize - 8, tileSize - 8);
            } else {
                g.setColor(Color.ORANGE);
                g.drawRect(x + 4, y + 4, tileSize - 8, tileSize - 8);
            }
        }
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
    	    
    	    
    	    if (currentBattleScenario != null &&
        		    currentBattleScenario.getId().equals("forest_ambush")) {
        		    completeBanditQuest();
        		}
        	
        	if (currentBattleScenario != null &&
        		    currentBattleScenario.getId().equals("cellar_rats")) {

        		    completeCellarRatsQuest();
        		}
        	
        	if (currentBattleScenario != null &&
        		    currentBattleScenario.getId().equals("old_mill_road")) {
        		    completeOldMillRoad();
        		}
        	
        	if (currentBattleScenario != null &&
        		    currentBattleScenario.getId().equals("bandit_king_challenge")) {
        		    completeTaliConfrontation();
        		}
        	
        	
        	if (currentBattleScenario != null &&
        		    currentBattleScenario.getId().equals("cael_usurper")) {
        		    completeCaelBattle();
        		}
        	
    	    

    	    handleBattleVictory();
    	}
    }
    
    
    
  //Checks if the Survive turns battle as concluded its objective
    private void checkSurviveTurnsObjective() {
    	
    	//For Golem
    	if (isGolemSealTrap()) {
            return;
        }
    	
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

    	showingRuinsExteriorScene = false;

        if (battleReturnMap != null) {
            currentMap = battleReturnMap;
            showMapTitle();
            currentState = battleReturnState;
            player.col = battleReturnCol;
            player.row = battleReturnRow;
        } else {
            currentMap = overworldGameMap;
            showMapTitle();
            currentState = GameState.OVERWORLD;
            player.col = 2;
            player.row = 5;
        }

        pendingReturnToOverworldAfterDialogue = false;

        battleUnitSelected = false;
        selectedBattleUnit = null;
        battleActionMenuOpen = false;
        battleAttackPreviewOpen = false;
        battleSkillPreviewOpen = false;
        battleTargetSelectOpen = false;
        battleSkillTargetSelectOpen = false;
        battleHealTargetSelectOpen = false;
        battleHealPreviewOpen = false;
        battleZoomCombatOpen = false;

        updateStoryWorldState();

        battleReturnMap = null;

        repaint();
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
        		currentBattleScenario.getId().equals("golem_seal_trap")) {

        		currentMap = overworldGameMap;
        		showMapTitle();
        		currentState = GameState.OVERWORLD;
        		player.col = 6;
        		player.row = 8;
        		pendingReturnToOverworldAfterDialogue = false;
                pendingWilliamRecruitmentScene = true;
            }
        
        
        if (currentBattleScenario != null &&
        	    currentBattleScenario.getId().equals("withered_road")) {

        	    corruptedRoadCompleted = true;
        	    chapterThreeStep = 3;
        	    pendingSilasTicketScene = true;

        	    updateActTwoWorldQuestTiles();
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
        currentBattleTurn++;
        battlePhase = "PLAYER";
        addBattleMessage("Player Phase");
        showBattlePhaseBanner("Player Phase");

        checkReinforcements();

        for (BattleUnit unit : playerBattleUnits) {
            if (unit != null && unit.isAlive()) {
                unit.setHasMoved(false);
                unit.setHasActed(false);
            }
        }

        if (handleGolemTrapTurnEvents()) {
            return;
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

        if (allPlayerUnitsDefeated()) {
            triggerBattleDefeat();
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

            if (role == EnemyRole.STATIONARY) {
                handleStationaryEnemyTurn(enemy, target);
                
            } else if (role == EnemyRole.RANGED) {
                handleRangedEnemyTurn(enemy, target);
                
            } else {
                handleAggressiveEnemyTurn(enemy, target);
            }

            // IMPORTANT This will check after each enemy acts
            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
            }
        }

        // Only start player phase if someone is alive
        if (!allPlayerUnitsDefeated()) {
            startPlayerPhase();
        }
    }
    
    //Aggressive Enemy Trait
    private void handleAggressiveEnemyTurn(BattleUnit enemy, BattleUnit target) {

        // Attack immediately if already in range
    	if (isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
            }

            startBattlePause(45);
            return;
        }

        // Otherwise move first
        moveEnemyTowardTarget(enemy, target);

        //Check after if targets is there
        if (target.isAlive() && isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
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

            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
            }

            startBattlePause(45);
            return;
        }

        // Otherwise reposition
        moveRangedEnemyTowardTarget(enemy, target);

        //After moving, check again and shoot if now in range
        if (target.isAlive() && isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
            }
        }

        startBattlePause(45);
    }
    
    //Stationary Enemies like turrets
    private void handleStationaryEnemyTurn(BattleUnit enemy, BattleUnit target) {

        if (enemy == null || !enemy.isAlive()) {
            return;
        }

        if (isGolemSealTrap() && enemy.getName().equals("Stone Golem")) {
            performGolemPulse(enemy);
            startBattlePause(45);
            return;
        }

        if (target != null && isEnemyInRange(enemy, target)) {
            performAttack(enemy, target);

            if (!target.isAlive()) {
                addBattleMessage(target.getName() + " was defeated!");
            }

            if (allPlayerUnitsDefeated()) {
                triggerBattleDefeat();
                return;
            }

            startBattlePause(45);
        }
        
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
    
    
    //Goblem Specific Attack that targets all but Art for story reasons
    private void performGolemPulse(BattleUnit golem) {

        addBattleMessage(golem.getName() + " releases a seal pulse!");

        int pulseDamage = 3;

        for (BattleUnit unit : playerBattleUnits) {

            if (unit == null || !unit.isAlive()) {
                continue;
            }

            if (isArtUnit(unit) && hasCreationSword) {
                addBattleMessage(unit.getName() + " is protected by The Creation!");
                continue;
            }

            unit.takeDamage(pulseDamage);
            addBattleMessage(unit.getName() + " took " + pulseDamage + " damage!");

            if (!unit.isAlive()) {
                addBattleMessage(unit.getName() + " was defeated!");
            }
        }

        if (allPlayerUnitsDefeated()) {
            triggerBattleDefeat();
        }
        
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
    	
    	//For golem
    	if (isStoneGolem(defender)) {
            return false;
        }
    	
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
    
    
    
    //Dialogue prevents getting stuck 
    private void drawDialogue(Graphics g) {
    	
    	if (showingRuinsExteriorScene) {
    	    drawRuinsExterior(g);
    	    return;
    	}
    	
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
        else if (previousState == GameState.QUEST_BOARD) {
            drawQuestBoard(g);
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
    
    //Defeat State if you were to lose in combat
    //GG
    private void triggerDefeat(String message) {

        defeatMessage = message;
        defeatMenuIndex = 0;

        battleActionMenuOpen = false;
        battleAttackPreviewOpen = false;
        battleSkillPreviewOpen = false;
        battleTargetSelectOpen = false;
        battleSkillTargetSelectOpen = false;
        battleHealTargetSelectOpen = false;
        battleHealPreviewOpen = false;
        battleZoomCombatOpen = false;

        selectedBattleUnit = null;
        battleUnitSelected = false;

        selectedUnitStartCol = -1;
        selectedUnitStartRow = -1;

        currentState = GameState.DEFEAT;
        
    }
    
    private boolean allPlayerUnitsDefeated() {

        for (BattleUnit unit : playerBattleUnits) {
            if (unit != null && unit.isAlive()) {
                return false;
            }
        }

        return true;
    }
    
    private void triggerBattleDefeat() {

        if (currentBattleScenario != null &&
            currentBattleScenario.getId().equals("bandit_king_challenge")) {

            triggerDefeat("Tali's force overwhelmed the party...");
            return;
        }

        triggerDefeat("The party was defeated...");
    }
    
    //You lost gg
    
    private void drawDefeat(Graphics g) {

        Font originalFont = g.getFont();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        g.setColor(Color.WHITE);

        g.setFont(originalFont.deriveFont(36f));
        g.drawString("Defeat", screenWidth / 2 - 65, screenHeight / 2 - 100);

        g.setFont(originalFont.deriveFont(16f));
        g.drawString(defeatMessage, screenWidth / 2 - 120, screenHeight / 2 - 55);

        int optionY = screenHeight / 2;

        for (int i = 0; i < defeatOptions.length; i++) {

            if (i == defeatMenuIndex) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            String prefix = (i == defeatMenuIndex) ? "> " : "  ";
            g.drawString(prefix + defeatOptions[i], screenWidth / 2 - 90, optionY + (i * 30));
        }

        g.setColor(Color.WHITE);
        g.drawString("UP/DOWN select | ENTER confirm", screenWidth / 2 - 130, screenHeight / 2 + 100);

        g.setFont(originalFont);
        
    }
    
    
    private boolean canRetreatFromCurrentBattle() {

        if (currentBattleScenario == null) {
            return true;
        }

        String id = currentBattleScenario.getId();

        if (id.equals("bandit_king_challenge")) {
            return false;
        }

        if (id.equals("old_mill_road")) {
            return false;
        }

        if (id.equals("forest_ambush")) {
            return false;
        }

        return true;
        
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
            writer.write("currentMapName=" + getCurrentMapSaveName() + "\n");
            //prologue step
            writer.write("prologueStep=" + prologueStep + "\n");
            writer.write("hasCreationSword=" + hasCreationSword + "\n");
            writer.write("creationAwakened=" + creationAwakened + "\n");
            
            //Prologue Inspect Event
            writer.write("inspectedOldHeroesMural=" + inspectedOldHeroesMural + "\n");
            writer.write("inspectedWhiteBladeMural=" + inspectedWhiteBladeMural + "\n");
            writer.write("inspectedBrokenHourMural=" + inspectedBrokenHourMural + "\n");
            
            
            
            //Camp bond data
            writer.write("penelopeBond=" + penelopeBond + "\n");
            writer.write("deanBond=" + deanBond + "\n");
            writer.write("penelopeLastTalkedChapter=" + penelopeLastTalkedChapter + "\n");
            writer.write("deanLastTalkedChapter=" + deanLastTalkedChapter + "\n");
            writer.write("taliBond=" + taliBond + "\n");
            writer.write("taliLastTalkedChapter=" + taliLastTalkedChapter + "\n");
                     
            //Quest
            writer.write("banditQuestUnlocked=" + banditQuestUnlocked + "\n");
            writer.write("banditQuestAccepted=" + banditQuestAccepted + "\n");
            writer.write("banditQuestCompleted=" + banditQuestCompleted + "\n");
            writer.write("banditQuestRewardClaimed=" + banditQuestRewardClaimed + "\n");
            
            //Gets active quest
            writer.write("activeQuestName=" + activeQuestName + "\n");
            
            //QuestBoardChapter 1
            writer.write("cellarRatsCompleted=" + cellarRatsCompleted + "\n");
            writer.write("laundryCompleted=" + laundryCompleted + "\n");
            writer.write("flowersCompleted=" + flowersCompleted + "\n");
            writer.write("starterJobsComplete=" + starterJobsComplete + "\n");
            
            //Chapter 1 CheckMate Steps
            writer.write("checkmateStep=" + checkmateStep + "\n");
            
            writer.write("inspectedSafehouseChildren=" + inspectedSafehouseChildren + "\n");
            writer.write("inspectedSafehouseDoctor=" + inspectedSafehouseDoctor + "\n");
            writer.write("inspectedSafehouseSupplies=" + inspectedSafehouseSupplies + "\n");
            writer.write("inspectedSafehouseOrders=" + inspectedSafehouseOrders + "\n");
            
            writer.write("oldMillRoadCompleted=" + oldMillRoadCompleted + "\n");
            writer.write("safehouseUnlocked=" + safehouseUnlocked + "\n");
            
            writer.write("inspectedKingTent=" + inspectedKingTent + "\n");
            writer.write("taliConfrontationCompleted=" + taliConfrontationCompleted + "\n");
            
            writer.write("taliTemporaryAlly=" + taliTemporaryAlly + "\n");
            writer.write("taliRecruited=" + taliRecruited + "\n");
            
            /*
             * Chapter 2 Saves
             */
            writer.write("chapterTwoStep=" + chapterTwoStep + "\n");
            writer.write("ruinsJobUnlocked=" + ruinsJobUnlocked + "\n");
            
            writer.write("chapterTwoStep=" + chapterTwoStep + "\n");
            writer.write("ruinsJobUnlocked=" + ruinsJobUnlocked + "\n");
            
            writer.write("williamRecruited=" + williamRecruited + "\n");


            //ACT 2 CHAPTER 3
            
            writer.write("chapterThreeStep=" + chapterThreeStep + "\n");
            writer.write("corruptedRoadCompleted=" + corruptedRoadCompleted + "\n");
            writer.write("carnalvalUnlocked=" + carnalvalUnlocked + "\n");
            
            //Act 2 Chapter 4
            writer.write("chapterFourStep=" + chapterFourStep + "\n");
            writer.write("carnalvalEntered=" + carnalvalEntered + "\n");
            writer.write("pipIntroduced=" + pipIntroduced + "\n");
            writer.write("silasWelcomeSeen=" + silasWelcomeSeen + "\n");
            writer.write("carnalvalExitDiscovered=" + carnalvalExitDiscovered + "\n");
            writer.write("lodgingUnlocked=" + lodgingUnlocked + "\n");
            writer.write("mainStageUnlocked=" + mainStageUnlocked + "\n");
            writer.write("laughingLaneVisited=" + laughingLaneVisited + "\n");
            writer.write("gildedMidwayVisited=" + gildedMidwayVisited + "\n");
            writer.write("performersRowVisited=" + performersRowVisited + "\n");
            writer.write("guestLodgingVisited=" + guestLodgingVisited + "\n");
            
            
            
            

            //Deletes Old Hard code in favor of calling 
            writer.write("partyCount=" + partyMembers.size() + "\n");

            for (int i = 0; i < partyMembers.size(); i++) {
                writePartyMember(writer, partyMembers.get(i), i);
            }

            writer.close();

            System.out.println("Game saved.");
            showSystemMessage("Game saved.");

            } catch (IOException e) {
                System.out.println("Save failed.");
                showSystemMessage("Save failed.");
                e.printStackTrace();
            }
    }
    
    //Load game after Save to give function
    private void loadGame() {

        File file = new File(SAVE_FILE);
        String loadedMapName = "overworld";

        if (!file.exists()) {
            System.out.println("No save file found.");
            showSystemMessage("No save file found.");
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
                else if (key.equals("currentMapName")) {
                    loadedMapName = value;
                }
                else if (key.equals("prologueStep")) {
                    prologueStep = Integer.parseInt(value);
                }
                else if (key.equals("hasCreationSword")) {
                    hasCreationSword = Boolean.parseBoolean(value);
                }
                else if (key.equals("creationAwakened")) {
                    creationAwakened = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedOldHeroesMural")) {
                	inspectedOldHeroesMural = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedWhiteBladeMural")) {
                	inspectedWhiteBladeMural = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedBrokenHourMural")) {
                	inspectedBrokenHourMural = Boolean.parseBoolean(value);
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
                else if (key.equals("taliBond")) {
                    taliBond = Integer.parseInt(value);
                }
                else if (key.equals("taliLastTalkedChapter")) {
                    taliLastTalkedChapter = Integer.parseInt(value);
                }
                else if (key.equals("partyCount")) {
                    partyCount = Integer.parseInt(value);
                }
                //Active Quests
                
                else if (key.equals("activeQuestName")) {
                    activeQuestName = value;
                }
                
                else if (key.equals("banditQuestUnlocked")) {
                    banditQuestUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("banditQuestAccepted")) {
                    banditQuestAccepted = Boolean.parseBoolean(value);
                }
                else if (key.equals("banditQuestCompleted")) {
                    banditQuestCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("banditQuestRewardClaimed")) {
                    banditQuestRewardClaimed = Boolean.parseBoolean(value);
                }
                //Quest Board Chapter 1
                else if (key.equals("cellarRatsCompleted")) {
                    cellarRatsCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("laundryCompleted")) {
                    laundryCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("flowersCompleted")) {
                    flowersCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("starterJobsComplete")) {
                    starterJobsComplete = Boolean.parseBoolean(value);
                }
                else if (key.equals("checkmateStep")) {
                    checkmateStep = Integer.parseInt(value);
                }
                else if (key.equals("inspectedSafehouseChildren")) {
                    inspectedSafehouseChildren = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedSafehouseDoctor")) {
                    inspectedSafehouseDoctor = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedSafehouseSupplies")) {
                    inspectedSafehouseSupplies = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedSafehouseOrders")) {
                    inspectedSafehouseOrders = Boolean.parseBoolean(value);
                }
                else if (key.equals("oldMillRoadCompleted")) {
                    oldMillRoadCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("safehouseUnlocked")) {
                    safehouseUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("inspectedKingTent")) {
                    inspectedKingTent = Boolean.parseBoolean(value);
                }
                else if (key.equals("taliConfrontationCompleted")) {
                    taliConfrontationCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("taliTemporaryAlly")) {
                    taliTemporaryAlly = Boolean.parseBoolean(value);
                }
                else if (key.equals("taliRecruited")) {
                    taliRecruited = Boolean.parseBoolean(value);
                }
                //Chapter 2 Loads
                else if (key.equals("chapterTwoStep")) { 
                    chapterTwoStep = Integer.parseInt(value);
                }
                else if (key.equals("ruinsJobUnlocked")) {
                    ruinsJobUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("chapterTwoStep")) {
                    chapterTwoStep = Integer.parseInt(value);
                }
                else if (key.equals("ruinsJobUnlocked")) {
                    ruinsJobUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("williamRecruited")) {
                    williamRecruited = Boolean.parseBoolean(value);
                }
                //Act2Chapter3
                else if (key.equals("chapterThreeStep")) {
                    chapterThreeStep = Integer.parseInt(value);
                }
                else if (key.equals("corruptedRoadCompleted")) {
                    corruptedRoadCompleted = Boolean.parseBoolean(value);
                }
                else if (key.equals("carnalvalUnlocked")) {
                    carnalvalUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("chapterFourStep")) { //Chapter 4
                    chapterFourStep = Integer.parseInt(value);
                }
                else if (key.equals("carnalvalEntered")) {
                    carnalvalEntered = Boolean.parseBoolean(value);
                }
                else if (key.equals("pipIntroduced")) {
                    pipIntroduced = Boolean.parseBoolean(value);
                }
                else if (key.equals("silasWelcomeSeen")) {
                    silasWelcomeSeen = Boolean.parseBoolean(value);
                }
                else if (key.equals("carnalvalExitDiscovered")) {
                    carnalvalExitDiscovered = Boolean.parseBoolean(value);
                }
                else if (key.equals("lodgingUnlocked")) {
                    lodgingUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("mainStageUnlocked")) {
                    mainStageUnlocked = Boolean.parseBoolean(value);
                }
                else if (key.equals("laughingLaneVisited")) {
                    laughingLaneVisited = Boolean.parseBoolean(value);
                }
                else if (key.equals("gildedMidwayVisited")) {
                    gildedMidwayVisited = Boolean.parseBoolean(value);
                }
                else if (key.equals("performersRowVisited")) {
                    performersRowVisited = Boolean.parseBoolean(value);
                }
                else if (key.equals("guestLodgingVisited")) {
                    guestLodgingVisited = Boolean.parseBoolean(value);
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
            
            if (taliRecruited && getPartyMemberById("tali") == null) {
                taliRecruited = false;
                recruitTali();
            }
            
            if (williamRecruited && getPartyMemberById("william") == null) {
                williamRecruited = false;
                recruitWilliam();
            }
            
            currentMap = getMapBySaveName(loadedMapName);
            currentState = getGameStateForMap(currentMap);
            validateLoadedPlayerPosition();

            updateStoryWorldState();
            updateTownQuestTiles();
            updateActTwoWorldQuestTiles();

            showMapTitle();
            

            System.out.println("Game loaded.");
            showSystemMessage("Game loaded.");

        } catch (IOException e) {
            System.out.println("Load failed.");
            showSystemMessage("Load failed.");
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
    
    private boolean isArtUnit(BattleUnit unit) {
        return unit != null && unit.getName().contains("Art");
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
    
    private boolean isCarnalvalMap(GameMap map) {

        return map == carnalvalMainGameMap ||
               map == laughingLaneGameMap ||
               map == gildedMidwayGameMap ||
               map == performersRowGameMap ||
               map == guestLodgingGameMap ||
               map == mainStageGameMap;
    }
    
    private boolean canUseSaveAndLoad() {

        if (currentState == GameState.OVERWORLD ||
            currentState == GameState.TOWN ||
            currentState == GameState.EXPLORATION) {

            return true;
        }

        return currentState == GameState.CAMP && !campBondMenuOpen;
    }

    private void showSystemMessage(String message) {
        systemMessage = message;
        systemMessageTimer = SYSTEM_MESSAGE_DURATION;
    }

    private void drawSystemMessage(Graphics g) {

        if (systemMessageTimer <= 0 ||
            systemMessage == null ||
            systemMessage.isEmpty()) {

            return;
        }

        Font oldFont = g.getFont();
        Color oldColor = g.getColor();

        g.setFont(oldFont.deriveFont(Font.BOLD, 16f));

        int padding = 12;
        int textWidth = g.getFontMetrics().stringWidth(systemMessage);
        int boxWidth = textWidth + padding * 2;
        int boxHeight = 34;

        int x = (screenWidth - boxWidth) / 2;
        int y = 16;

        int alpha = 210;

        if (systemMessageTimer < 45) {
            alpha = Math.max(0, systemMessageTimer * 210 / 45);
        }

        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);

        g.setColor(
            new Color(
                255,
                255,
                255,
                Math.min(255, alpha + 40)
            )
        );

        g.drawRoundRect(x, y, boxWidth, boxHeight, 12, 12);
        g.drawString(systemMessage, x + padding, y + 23);

        g.setFont(oldFont);
        g.setColor(oldColor);
    }
    
    private String getCurrentMapSaveName() {

        if (currentMap == overworldGameMap) {
            return "overworld";
        }

        if (currentMap == townGameMap) {
            return "town";
        }

        if (currentMap == actTwoWorldGameMap) {
            return "act_two_world";
        }

        if (currentMap == carnalvalMainGameMap) {
            return "carnalval_main";
        }

        if (currentMap == laughingLaneGameMap) {
            return "laughing_lane";
        }

        if (currentMap == gildedMidwayGameMap) {
            return "gilded_midway";
        }

        if (currentMap == performersRowGameMap) {
            return "performers_row";
        }

        if (currentMap == guestLodgingGameMap) {
            return "guest_lodging";
        }

        if (currentMap == mainStageGameMap) {
            return "main_stage";
        }

        if (currentMap == safehouseGameMap) {
            return "safehouse";
        }

        if (currentMap == chapterTwoRuinsGameMap) {
            return "chapter_two_ruins";
        }

        if (currentMap == flowerFieldGameMap) {
            return "flower_field";
        }

        if (currentMap == prologueForestGameMap) {
            return "prologue_forest";
        }

        return "overworld";
    }
    
    //This categorizes maps helps the loading phase
    private GameState getGameStateForMap(GameMap map) {

        if (map == townGameMap) {
            return GameState.TOWN;
        }

        if (map == overworldGameMap || map == actTwoWorldGameMap) {
            return GameState.OVERWORLD;
        }

        return GameState.EXPLORATION;
    }
    
    //Position Check when loading
    private void validateLoadedPlayerPosition() {

        Tile[][] tiles = currentMap.getTiles();

        boolean outsideMap =
                player.col < 0 ||
                player.col >= tiles.length ||
                player.row < 0 ||
                player.row >= tiles[player.col].length;

        if (!outsideMap && tiles[player.col][player.row].isPassable()) {
            return;
        }

        for (int row = tiles[0].length - 1; row >= 0; row--) {
            for (int col = 0; col < tiles.length; col++) {

                if (tiles[col][row].isPassable()) {
                    player.col = col;
                    player.row = row;

                    System.out.println(
                        "Saved player position was invalid. Moved to a safe tile."
                    );

                    return;
                }
            }
        }

        currentMap = overworldGameMap;
        currentState = GameState.OVERWORLD;

        player.col = 3;
        player.row = 1;

        System.out.println(
            "Loaded map had no passable tile. Returned to the overworld."
        );
    }
    
    private GameMap getMapBySaveName(String mapName) {

        if (mapName.equals("overworld")) {
            return overworldGameMap;
        }

        if (mapName.equals("town")) {
            return townGameMap;
        }

        if (mapName.equals("act_two_world")) {
            return actTwoWorldGameMap;
        }

        if (mapName.equals("carnalval_main")) {
            return carnalvalMainGameMap;
        }

        if (mapName.equals("laughing_lane")) {
            return laughingLaneGameMap;
        }

        if (mapName.equals("gilded_midway")) {
            return gildedMidwayGameMap;
        }

        if (mapName.equals("performers_row")) {
            return performersRowGameMap;
        }

        if (mapName.equals("guest_lodging")) {
            return guestLodgingGameMap;
        }

        if (mapName.equals("main_stage")) {
            return mainStageGameMap;
        }

        if (mapName.equals("safehouse")) {
            return safehouseGameMap;
        }

        if (mapName.equals("chapter_two_ruins")) {
            return chapterTwoRuinsGameMap;
        }

        if (mapName.equals("flower_field")) {
            return flowerFieldGameMap;
        }

        if (mapName.equals("prologue_forest")) {
            return prologueForestGameMap;
        }

        return overworldGameMap;
    }
    
    //Debug for act skips
    private void prepareActTwoDebugStart() {

        // Clear active quest state
        activeQuestName = "";
        pendingQuestName = "";
        questConfirmOpen = false;

        // Prologue complete
        prologueStep = 5;
        hasCreationSword = true;
        creationAwakened = true;

        // Chapter 1 starter jobs complete
        cellarRatsCompleted = true;
        laundryCompleted = true;
        flowersCompleted = true;
        starterJobsComplete = true;

        // Bandit Trouble complete
        banditQuestUnlocked = true;
        banditQuestAccepted = false;
        banditQuestCompleted = true;
        banditQuestRewardClaimed = true;

        // Checkmate complete
        checkmateStep = 8;
        oldMillRoadCompleted = true;
        safehouseUnlocked = false;
        inspectedSafehouseChildren = true;
        inspectedSafehouseDoctor = true;
        inspectedSafehouseSupplies = true;
        inspectedSafehouseOrders = true;
        inspectedKingTent = true;
        taliConfrontationCompleted = true;
        taliTemporaryAlly = false;

        // Recruit Tali if missing
        recruitTali();

        // Chapter 2 complete enough for Act Two
        chapterTwoStep = 3;
        ruinsJobUnlocked = true;
        silasBetrayalTriggered = true;

        // Recruit William if missing
        recruitWilliam();

        // Reset Act Two progression
        chapterThreeStep = 0;
        corruptedRoadCompleted = false;
        carnalvalUnlocked = false;

        // Clear Act Two pending flags
        pendingActTwoOpening = false;
        pendingChapterThreeOpening = false;
        pendingCorruptedRoadMission = false;
        pendingSilasTicketScene = false;
        pendingMoveToActTwoWorld = false;

        // Rebuild/update maps
        generateActTwoWorld();
        updateStoryWorldState();

        System.out.println("Debug: Act Two start prepared.");
    }
    
    
    //keys need to be pressed for movement
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        
        //title controls
        if (currentState == GameState.TITLE) {

            if (code == KeyEvent.VK_UP) {
                titleMenuIndex--;

                if (titleMenuIndex < 0) {
                    titleMenuIndex = titleMenuOptions.length - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                titleMenuIndex++;

                if (titleMenuIndex >= titleMenuOptions.length) {
                    titleMenuIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {
                handleTitleMenuSelection();
                repaint();
                return;
            }

            return;
        }
        
        if (currentState == GameState.CONTROLS) {

            if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_ENTER) {
                currentState = GameState.TITLE;
                repaint();
                return;
            }

            return;
        }
        
        
        
        //no input on transitions
        if (storyTransitionTimer > 0) {
            return;
        }
        
        //Save
        if (code == KeyEvent.VK_S) {

            if (canUseSaveAndLoad()) {
                saveGame();
            } else {
                showSystemMessage(
                    "You cannot save during this screen."
                );
            }

            repaint();
            return;
        }

        //Load
        if (code == KeyEvent.VK_L) {

            if (canUseSaveAndLoad()) {
                loadGame();
            } else {
                showSystemMessage(
                    "You cannot load during this screen."
                );
            }

            repaint();
            return;
        }
        
        //DEbug code for buttons
        
        //Advances Chapters for testing
        if (code == KeyEvent.VK_C) {
            advanceStoryChapter(storyChapter + 1);

            if (storyChapter > 7) {
                storyChapter = 7;
            }

            repaint();
            return;
        }
        
        if (code == KeyEvent.VK_P) {
            startPrologue(); //Will be tied to new game unpon starting the game
            repaint();
            return;
        }
        

        
        //Exploration will delete the above later
        if (code == KeyEvent.VK_R) {
            currentMap = ruinsGameMap;
            showMapTitle();
            currentState = GameState.EXPLORATION;

            player.col = 1;
            player.row = 8;

            repaint();
            return;
        }
        
        //Skip to Act 1
        if (code == KeyEvent.VK_1) {
        	startChapterOne();
        	repaint();
        	return;
        }
        
        //Skip to Act 2 
        if (code == KeyEvent.VK_2) {
            prepareActTwoDebugStart();
            startActTwo();
            repaint();
            return;
        }
        
        //open status screen
        if (code == KeyEvent.VK_Q) {
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
        if (code == KeyEvent.VK_F) {
            if (currentState == GameState.OVERWORLD ||
                currentState == GameState.TOWN ||
                currentState == GameState.EXPLORATION) {

                openCamp();
                repaint();
                return;
            }
        }
        

        
        //Title placeholder
        if (currentState == GameState.ACT_ONE_END) {
            if (code == KeyEvent.VK_ENTER) {
                currentState = GameState.TITLE;
                titleMenuIndex = 0;
                repaint();
                return;
            }

            return;
        }

        if (currentState == GameState.DIALOGUE) {

            if (code == KeyEvent.VK_ENTER) {
                dialogueManager.nextLine();

                if (!dialogueManager.isActive()) {
                	
                	//Battle Library Dialogue
                	if (pendingScenarioIntroAfterQuestAccept != null) {

                	    BattleScenario scenario = pendingScenarioIntroAfterQuestAccept;
                	    pendingScenarioIntroAfterQuestAccept = null;

                	    if (scenario.getIntroDialogue() != null && scenario.getIntroDialogue().length > 0) {

                	        // After the intro dialogue finishes, THEN load the battle.
                	        pendingBattleScenario = scenario;
                	        startDialogue(scenario.getIntroDialogue(), GameState.QUEST_BOARD);

                	        repaint();
                	        return;

                	    } else {
                	        loadBattleScenario(scenario);
                	        repaint();
                	        return;
                	    }
                	}
                	
                	
                	//Post sword dialogue
                	if (pendingPrologueReturnHome) {
                	    pendingPrologueReturnHome = false;
                	    startPrologueReturnHome();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingPrologueChapterOne) {
                		pendingPrologueChapterOne = false;

                	    pendingChapterOneStart = true;
                	    startStoryTransition("Years Later...");

                	    repaint();
                	    return;
                	}
                	
                	//Recruitment Scene
                	if (pendingRecruitmentScene) {
                	    pendingRecruitmentScene = false;
                	    startRecruitmentRejectionScene();
                	    repaint();
                	    return;
                	}

                	//The Adventure begins scene
                	if (pendingAdventurerIdeaScene) {
                	    pendingAdventurerIdeaScene = false;
                	    startAdventurerIdeaScene();
                	    repaint();
                	    return;
                	}
                	
                	//Flowers
                	if (pendingFlowerFieldStart) {
                	    pendingFlowerFieldStart = false;
                	    startFlowerPickingMap();
                	    repaint();
                	    return;
                	}
                	
                	//Plays after Last Petty quest done
                	if (pendingStarterJobsCompleteScene) {
                	    pendingStarterJobsCompleteScene = false;
                	    startStarterJobsCompleteScene();
                	    repaint();
                	    return;
                	}
                	
                	//Tali Battle
                	if (pendingTaliConfrontation) {
                	    pendingTaliConfrontation = false;

                	    BattleScenario scenario = BattleScenarioLibrary.getScenario("bandit_king_challenge");
                	    loadBattleScenario(scenario);

                	    repaint();
                	    return;
                	}
                	
                	//Chapter End
                	if (pendingChapterOneEnding) {
                	    pendingChapterOneEnding = false;
                	    startChapterOneEnding();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingChapterOneCamp) {
                	    pendingChapterOneCamp = false;
                	    startChapterOneCampReflection();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingAdvanceToChapterTwo) {
                	    pendingAdvanceToChapterTwo = false;

                	    advanceStoryChapter(2);
                	    pendingChapterTwoOpening = true;

                	    currentState = GameState.CAMP;

                	    repaint();
                	    return;
                	}
                	
                	/*
                	 * Start Chapter 2
                	 */
                	if (pendingChapterTwoOpening) {
                	    pendingChapterTwoOpening = false;
                	    startChapterTwoOpening();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingRuinsJobUnlock) {
                	    pendingRuinsJobUnlock = false;
                	    unlockChapterTwoRuinsJob();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingSilasTrapBattle) {
                	    pendingSilasTrapBattle = false;

                	    BattleScenario scenario = BattleScenarioLibrary.getScenario("golem_seal_trap");

                	    if (scenario.getIntroDialogue() != null && scenario.getIntroDialogue().length > 0) {
                	        pendingBattleScenario = scenario;
                	        startDialogue(scenario.getIntroDialogue(), GameState.EXPLORATION);

                	        repaint();
                	        return;
                	    }

                	    loadBattleScenario(scenario);
                	    repaint();
                	    return;
                	}
                	
                	//William Saves Team
                	if (pendingWilliamGolemRescue) {
                	    pendingWilliamGolemRescue = false;
                	    performWilliamGolemRescue();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingWilliamRecruitmentScene) {
                	    pendingWilliamRecruitmentScene = false;
                	    startWilliamRecruitmentScene();
                	    repaint();
                	    return;
                	}
                	
                	//End of Act 1 Chapter 2s
                	if (pendingActOneEnding) {
                	    pendingActOneEnding = false;
                	    startActOneEnding();
                	    repaint();
                	    return;
                	}
                	
                	
                	if (pendingEndActOneTransition) {
                	    pendingEndActOneTransition = false;
                	    pendingReturnAfterActOne = true;
                	    startStoryTransition("End of Act One");
                	    repaint();
                	    return;
                	}
                	
                	//ACT 2 Chapter 3
                	
                	if (pendingCorruptedRoadMission) {
                	    pendingCorruptedRoadMission = false;
                	    unlockCorruptedRoadMission();
                	    repaint();
                	    return;
                	}
                	
                	if (pendingMoveToActTwoWorld) {
                	    pendingMoveToActTwoWorld = false;
                	    enterActTwoWorld();
                	    repaint();
                	    return;
                	}
                	
                	
                	
                	
                	
                	//Skip for Dialogue completion Block
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

                	    if (pendingSilasTicketScene) {
                	        startSilasTicketScene();
                	    }

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
                	
                	System.out.println("Leaving camp to state: " + campReturnState);

                	if (campReturnMap != null) {
                	    System.out.println("Leaving camp to map: " + campReturnMap.getMapName());
                	}
                	
                	//Just for the prologue
                	campBondMenuOpen = false;
                    campBondIndex = 0;

                    // Scripted prologue camp: leaving camp sends the party to the ruins
                    if (storyChapter == 0 && prologueStep == 1 && campReturnState == GameState.EXPLORATION) {
                        enterPrologueRuins();
                        repaint();
                        return;
                    }

                    if (campReturnMap != null) {
                        currentMap = campReturnMap;
                    }

                    currentState = campReturnState;
                    player.col = campReturnCol;
                    player.row = campReturnRow;

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
        
        //Defeat Screen Movement
        if (currentState == GameState.DEFEAT) {

            if (code == KeyEvent.VK_UP) {
                defeatMenuIndex--;

                if (defeatMenuIndex < 0) {
                    defeatMenuIndex = defeatOptions.length - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                defeatMenuIndex++;

                if (defeatMenuIndex >= defeatOptions.length) {
                    defeatMenuIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {

                String selected = defeatOptions[defeatMenuIndex];

                if (selected.equals("Retry Battle")) {
                    if (lastBattleScenario != null) {
                        loadBattleScenario(lastBattleScenario);
                    }

                    repaint();
                    return;
                }

                if (selected.equals("Return to Overworld")) {
                    currentMap = overworldGameMap;
                    showMapTitle();
                    currentState = GameState.OVERWORLD;

                    player.col = 3;
                    player.row = 1;

                    movementLeft = maxMovement;

                    repaint();
                    return;
                }
            }

            return;
        }
        
        //Quest Board currentState
        if (currentState == GameState.QUEST_BOARD) {
        	
        	//For quest selection screen
        	if (questConfirmOpen) {

        	    if (code == KeyEvent.VK_ESCAPE) {
        	        declineQuest();
        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN ||
        	        code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {

        	        questConfirmIndex = (questConfirmIndex == 0) ? 1 : 0;
        	        repaint();
        	        return;
        	    }

        	    if (code == KeyEvent.VK_ENTER) {

        	        if (questConfirmIndex == 0) {
        	            acceptQuest(pendingQuestName);
        	        } else {
        	            declineQuest();
        	        }

        	        repaint();
        	        return;
        	    }

        	    return;
        	}

        	
        	//Normal quest board handles
            if (code == KeyEvent.VK_ESCAPE) {
                currentState = GameState.TOWN;
                repaint();
                return;
            }

            if (code == KeyEvent.VK_UP) {
                questBoardIndex--;

                if (questBoardIndex < 0) {
                    questBoardIndex = questBoardOptions.length - 1;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_DOWN) {
                questBoardIndex++;

                if (questBoardIndex >= questBoardOptions.length) {
                    questBoardIndex = 0;
                }

                repaint();
                return;
            }

            if (code == KeyEvent.VK_ENTER) {
                handleQuestBoardSelection();
                repaint();
                return;
            }

            return;
        }
        
        

        //End of States
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
                showMapTitle();
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
        	            
        	            if (allPlayerUnitsDefeated()) {
        	                triggerDefeat("The party was defeated...");
        	                repaint();
        	                return;
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
        			
        		    if (!canRetreatFromCurrentBattle()) {
        		        addBattleMessage("You cannot retreat from this battle.");
        		        repaint();
        		        return;
        		    }

        		    currentMap = overworldGameMap;
        		    showMapTitle();
        		    currentState = GameState.OVERWORLD;

        		    player.col = 3;
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
        		
        		//Movement checker
        		if (battleUnitSelected &&
        			    selectedBattleUnit != null &&
        			    code == KeyEvent.VK_ENTER &&
        			    battleCursorCol == selectedBattleUnit.getCol() &&
        			    battleCursorRow == selectedBattleUnit.getRow() &&
        			    !battleActionMenuOpen &&
        			    !battleAttackPreviewOpen &&
        			    !battleTargetSelectOpen &&
        			    !battleSkillTargetSelectOpen) {

        			    battleActionMenuOpen = true;
        			    battleMenuIndex = 0;
        			    repaint();
        			    return;
        			}
        		
        		
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
