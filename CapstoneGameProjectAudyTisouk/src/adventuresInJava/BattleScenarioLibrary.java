package adventuresInJava;

import java.util.ArrayList;
import java.util.List;

//This will be the host for all maps in code
public class BattleScenarioLibrary {

	public static BattleScenario getScenario(String scenarioId) {
		
		if (scenarioId.equals("prologue_ruins")) {
		    return createPrologueRuins();
		}
		
		if (scenarioId.equals("bandit_field")) {
			return createBanditField();
		}
		
		if (scenarioId.equals("forest_ambush")) {
		    return createForestAmbush();
		}
		
		if (scenarioId.equals("cellar_rats")) {
		    return createCellarRats();
		}
		
		if (scenarioId.equals("old_mill_road")) {
		    return createOldMillRoad();
		}
		
		if (scenarioId.equals("bandit_king_challenge")) {
		    return createBanditKingChallenge();
		}
		
		if (scenarioId.equals("cael_usurper")) {
		    return createCaelUsurper();
		}
		
		if (scenarioId.equals("golem_seal_trap")) {
		    return createGolemSealTrap();
		}
		
		return createBanditField();
		
	}
	
	//Bandit Field Scenario
	private static BattleScenario createBanditField() {
		
		int[][] layout = {
				
				{0,0,0,4,4,4,0,0,0,0},
				{0,3,3,0,0,0,0,3,3,0},
				{0,3,0,0,0,0,0,0,3,0},
				{0,0,0,0,5,5,0,0,0,0},
				{0,0,0,5,1,1,5,0,0,0},
				{0,0,0,5,1,1,5,0,0,0},
				{0,0,0,0,5,5,0,0,0,0},
				{0,3,0,0,0,0,0,0,3,0},
				{0,3,3,0,0,0,0,3,3,0},
				{0,0,0,4,4,4,0,0,0,0}	
		};
		
		List<UnitSpawn> playerSpawns = new ArrayList<>();
		playerSpawns.add(new UnitSpawn("leader", 1, 1, false));
		playerSpawns.add(new UnitSpawn("archer_ally", 2, 1, false));
		playerSpawns.add(new UnitSpawn("mage", 3, 1, false));
		
		List<UnitSpawn> enemySpawns = new ArrayList<>();
		enemySpawns.add(new UnitSpawn("hunter", 6, 6, true));
		enemySpawns.add(new UnitSpawn("hunter", 7, 4, true));
		
		List<ReinforcementSpawn> reinforcements = new ArrayList<>();
		reinforcements.add(new ReinforcementSpawn(3, "bandit", 8, 1, true));
		reinforcements.add(new ReinforcementSpawn(3, "bandit", 8, 2, true));
		
		//id, name, layout, objectiveType, surviveTurnTarget, playerSpawns, enemySpawns, reinforcements;
		return new BattleScenario(
				"bandit_field",
				"Bandit Field",
				layout,
				ObjectiveType.DEFEAT_ALL,
				0,
				playerSpawns,
				enemySpawns,
				reinforcements,
				null, //Normal Combat No pre-dialogue
				null // after combat talk none for normal combat
				
		);
		
	}
	
	//Bandit Ambush Scenario
	private static BattleScenario createForestAmbush() {

	    int[][] layout = {
	        {3,3,3,3,3,3,3,3,3,3},
	        {3,0,0,0,0,0,0,0,0,3},
	        {3,0,3,3,0,0,3,3,0,3},
	        {3,0,3,0,0,0,0,3,0,3},
	        {3,0,0,0,4,4,0,0,0,3},
	        {3,0,0,0,4,4,0,0,0,3},
	        {3,0,3,0,0,0,0,3,0,3},
	        {3,0,3,3,0,0,3,3,0,3},
	        {3,0,0,0,0,0,0,0,0,3},
	        {3,3,3,3,3,3,3,3,3,3}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 4, 5, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 5, 4, false));
	    playerSpawns.add(new UnitSpawn("mage", 4, 4, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("hunter", 7, 7, true));
	    enemySpawns.add(new UnitSpawn("hunter", 2, 7, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();
	    reinforcements.add(new ReinforcementSpawn(3, "hunter", 9, 0, true));
	    reinforcements.add(new ReinforcementSpawn(3, "hunter", 0, 0, true));
	    
	    //Combat PreDialogue
	    DialogueLine[] introDialogue = new DialogueLine[] {
	    	    new DialogueLine("Art", "Something feels wrong.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Dean", "The forest is too quiet.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "You picked the wrong road, travelers!", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Art", "Weapons ready!", DialogueSide.LEFT, DialogueFaction.ALLY)
	    	};
	    //Combat Post Battle
	    DialogueLine[] outroDialogue = new DialogueLine[] {
	    	    new DialogueLine("Dean", "Okay. Those were definitely not random road thugs.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Penelope", "They have the same marking on their gear.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	    	    new DialogueLine("Art", "A gold coin split by a black line...", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Dean", "That sounds like a gang symbol.", 
	    	    		DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Penelope", "One of the merchants mentioned a name before. The Golden Sinners. That should be them.", 
	    	    		DialogueSide.RIGHT, DialogueFaction.ALLY),
	    	    new DialogueLine("Art", "Then this is not the end of it.", DialogueSide.LEFT, DialogueFaction.ALLY)
	    	};

	    return new BattleScenario(
	        "forest_ambush",
	        "Forest Ambush",
	        layout,
	        ObjectiveType.DEFEAT_ALL,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	}
	
	//Chapter 0 : Prologue/ Tutorial?
	private static BattleScenario createPrologueRuins() {

	    int[][] layout = {
	        {2,2,2,2,2,2,2,2,2,2},
	        {2,0,0,0,0,0,0,0,0,2},
	        {2,0,3,3,0,0,3,3,0,2},
	        {2,0,3,0,0,0,0,3,0,2},
	        {2,0,0,0,4,4,0,0,0,2},
	        {2,0,0,0,4,4,0,0,0,2},
	        {2,0,3,0,0,0,0,3,0,2},
	        {2,0,3,3,0,0,3,3,0,2},
	        {2,0,0,0,0,0,0,0,0,2},
	        {2,2,2,2,2,2,2,2,2,2}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 1, 8, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 2, 8, false));
	    playerSpawns.add(new UnitSpawn("mage", 3, 8, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();

	    DialogueLine[] introDialogue = new DialogueLine[] {
	        new DialogueLine("Dean", "I told you this place was real!", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "Dean, we shouldn't be here right now.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "It's fine Penelope. We'll just look around. Then we leave alright?", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "Fine. But if there is treasure, I saw it first.", DialogueSide.LEFT, DialogueFaction.ALLY)
	    };

	    DialogueLine[] outroDialogue = new DialogueLine[] {
	        new DialogueLine("Dean", "Woah, is Is that a... a sword? I bet you wouldn't touch it Art.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "It doesn't look safe guys... Hey Art! Don't actually touch it!", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "I only want to see it closer...", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Narrator", "A white light bursts through the ruins.", DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Narrator", "For a moment, time itself seems to stop.", DialogueSide.RIGHT, DialogueFaction.NPC)
	    };

	    return new BattleScenario(
	        "prologue_ruins",
	        "Prologue Ruins",
	        layout,
	        ObjectiveType.REACH_TILE,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	}
	
	
	//Prologue Rat Celler Quest Board
	private static BattleScenario createCellarRats() {

	    int[][] layout = {
	        {1,1,1,1,1,1,1,1,1,1},
	        {1,0,0,0,0,0,0,0,0,1},
	        {1,0,3,0,0,0,0,3,0,1},
	        {1,0,0,0,4,4,0,0,0,1},
	        {1,0,0,0,4,4,0,0,0,1},
	        {1,0,0,0,0,0,0,0,0,1},
	        {1,0,3,0,0,0,0,3,0,1},
	        {1,0,0,0,0,0,0,0,0,1},
	        {1,0,0,0,0,0,0,0,0,1},
	        {1,1,1,1,1,1,1,1,1,1}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 1, 8, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 2, 8, false));
	    playerSpawns.add(new UnitSpawn("mage", 3, 8, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("rat", 6, 3, true));
	    enemySpawns.add(new UnitSpawn("rat", 7, 5, true));
	    enemySpawns.add(new UnitSpawn("rat", 5, 6, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();

	    DialogueLine[] introDialogue = new DialogueLine[] {
	    		new DialogueLine("", "Later, beneath the tavern...", DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Dean", "So this is it. Our first official adventurer job.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "It smells awful down here.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "The owner said the rats were getting bold.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "Bold rats. Great. My legend starts with me dead in a basement.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "Please do NOT call this a legend when we tell people.", DialogueSide.RIGHT, DialogueFaction.ALLY)
	    };

	    DialogueLine[] outroDialogue = new DialogueLine[] {
	        new DialogueLine("Dean", "And just like that, the basement is saved.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "From rats.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "From BOLD rats.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "It was not glamorous, but it helped someone.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "That should count.", DialogueSide.RIGHT, DialogueFaction.ALLY)
	    };

	    return new BattleScenario(
	    	    "cellar_rats",
	    	    "Cellar Rats",
	    	    layout,
	    	    ObjectiveType.DEFEAT_ALL,
	    	    0,
	    	    playerSpawns,
	    	    enemySpawns,
	    	    reinforcements,
	    	    introDialogue,
	    	    outroDialogue
	    	);
	    
	}
	
	//Checkmate step 3
	private static BattleScenario createOldMillRoad() {

	    int[][] layout = {
	        {1,1,1,1,1,1,1,1,1,1},
	        {1,0,0,4,4,4,0,0,3,1},
	        {1,0,3,0,0,4,0,3,0,1},
	        {1,0,0,0,0,4,0,0,0,1},
	        {1,5,5,0,0,4,0,2,0,1},
	        {1,5,1,1,0,4,0,2,0,1},
	        {1,5,5,0,0,4,0,0,0,1},
	        {1,0,0,0,3,4,0,3,0,1},
	        {1,0,3,0,0,4,0,0,0,1},
	        {1,1,1,1,1,1,1,1,1,1}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 3, 8, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 2, 8, false));
	    playerSpawns.add(new UnitSpawn("mage", 1, 8, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("bandit", 7, 2, true));
	    enemySpawns.add(new UnitSpawn("hunter", 8, 4, true));
	    enemySpawns.add(new UnitSpawn("bandit", 6, 6, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();
	    reinforcements.add(new ReinforcementSpawn(3, "bandit", 8, 1, true));

	    DialogueLine[] introDialogue = new DialogueLine[] {
	        new DialogueLine("", "The old mill road sits quiet under a grey sky and setting sun.", DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Penelope", "This road should have more travelers.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "Maybe they heard we were coming and cleared the way.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "No. Something scared them off.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "There are wagon tracks near the mill.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "And fresh bootprints. Stay ready.", DialogueSide.LEFT, DialogueFaction.ALLY)
	    };

	    
	    DialogueLine[] outroDialogue = new DialogueLine[] {
	    	    new DialogueLine("Bandit", "Wait! Wait, don't swing!", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Dean", "That depends. Are you about to say something useful?", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "The King wanted the road cleared. That's all I know.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Art", "Cleared? You attacked travelers.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "Anyone walking that road was warned. The Golden Sinners own it now.", 
	    	    		DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Penelope", "You hurt people who had nothing to do with this.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "The King says the roads feed the towns. Break the roads, towns kneel. We have to follow all of his"
	    	    		+ " orders", 
	    	    		DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Dean", "That's not a king. That's a coward with a nickname.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Art", "Where is he?", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "No one just finds him. The King finds who matters.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("", "The bandit says nothing more, but the name hangs heavy in the air. As they bring him back to village", 
	    	    		DialogueSide.RIGHT, DialogueFaction.NPC)
	    	};
	    

	    return new BattleScenario(
	        "old_mill_road",
	        "Old Mill Road",
	        layout,
	        ObjectiveType.DEFEAT_ALL,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	    
	}
	
	
	//Major Tali Sin Fight
	private static BattleScenario createBanditKingChallenge() {

	    int[][] layout = {
	        {1,1,1,1,1,1,1,1,1,1},
	        {1,0,0,3,0,0,0,3,0,1},
	        {1,0,3,0,0,4,0,0,0,1},
	        {1,0,0,0,4,4,4,0,0,1},
	        {1,3,0,0,0,0,0,0,3,1},
	        {1,0,0,4,4,4,0,0,0,1},
	        {1,0,3,0,0,0,0,3,0,1},
	        {1,0,0,0,0,4,0,0,0,1},
	        {1,0,0,3,0,0,0,3,0,1},
	        {1,1,1,1,1,1,1,1,1,1}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 5, 7, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 5, 8, false));
	    playerSpawns.add(new UnitSpawn("mage", 4, 8, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("tali_boss", 5, 1, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();

	    DialogueLine[] introDialogue = new DialogueLine[] {
	        new DialogueLine("Tali", "Here I am.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Dean", "Still processing that part.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "We do not want to hurt the people in your camp.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "Then you should have stayed out of it.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Art", "We need the truth.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "Truth will be revealed after steel.", DialogueSide.RIGHT, DialogueFaction.ENEMY)
	    };

	    DialogueLine[] outroDialogue = new DialogueLine[] {
	        new DialogueLine("Tali", "Enough.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Dean", "That means we win, right?", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "It means I am still deciding whether you are stupid or lucky.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Art", "You said Cael told you we attacked your people.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "He informed enough for me to come defend my camp.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Penelope", "Then listen to us too. The raids on villages, the road attacks, the burned storehouse...",
	        		DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "You lie. I ordered no such raids on villages.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Art", "What?", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("", "Before Tali can answer, shouting rises from deeper in the camp.", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Injured Golden Sinner", "Tali! The eastern stores are gone!", DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Tali", "Gone?", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Injured Golden Sinner", "Coin, grain, medicine. Cael's men took all of it. Attacked us too", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Tali", "...Cael.", DialogueSide.RIGHT, DialogueFaction.ENEMY)
	    };

	    return new BattleScenario(
	        "bandit_king_challenge",
	        "The Bandit King",
	        layout,
	        ObjectiveType.DEFEAT_ALL,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	    
	}
	
	
	private static BattleScenario createCaelUsurper() {

	    int[][] layout = {
	        {1,1,1,1,1,1,1,1,1,1},
	        {1,0,0,3,0,0,0,3,0,1},
	        {1,0,3,0,0,4,0,0,0,1},
	        {1,0,0,0,4,4,4,0,0,1},
	        {1,3,0,0,0,0,0,0,3,1},
	        {1,0,0,4,4,4,0,0,0,1},
	        {1,0,3,0,0,0,0,3,0,1},
	        {1,0,0,0,0,4,0,0,0,1},
	        {1,0,0,3,0,0,0,3,0,1},
	        {1,1,1,1,1,1,1,1,1,1}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 1, 8, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 2, 8, false));
	    playerSpawns.add(new UnitSpawn("mage", 3, 8, false));

	    // Tali joins this fight as a temporary ally
	    playerSpawns.add(new UnitSpawn("tali_guest", 4, 8, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("cael_boss", 7, 2, true));
	    enemySpawns.add(new UnitSpawn("bandit", 6, 3, true));
	    enemySpawns.add(new UnitSpawn("hunter", 8, 4, true));
	    enemySpawns.add(new UnitSpawn("bandit", 7, 5, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();

	    DialogueLine[] introDialogue = new DialogueLine[] {
	        new DialogueLine("", "The trail leads to an old toll road where stolen supplies are being loaded onto wagons.", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Tali", "Cael!", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "Tali. You should be resting after your embarrassing little loss.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Tali", "You took the eastern stores.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "I took what the Golden Sinners needed.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Penelope", "Medicine and grain from your own camp?", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "From mouths that only consume. I have plans beyond feeding strays.",
	        		DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Dean", "Wow. I already hate him.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "You used Tali's name to raid towns.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "Her name opened doors. Fear kept them open.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Tali", "You turned my people into your knife.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "No. I gave them teeth. To survive is nothing more than grasping at straws."
	        		+ " Being strong means we grow.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Tali", "I'll kill you right now.", DialogueSide.LEFT, DialogueFaction.ALLY)
	    };

	    DialogueLine[] outroDialogue = new DialogueLine[] {
	    		new DialogueLine("", "The last strike lays Cael into the ground bleeding. Any remaining bandits scatter.", 
		        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Cael", "Gah...You think killing me fixes the problem?", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Cael", "The people need real leadership because we were tired of being weak.", 
	        		DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Cael", "All those dirty nobles... we needed to kill them", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Cael", "Our weak won't help us win.", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("Tali", "All we wanted was somewhere to live in and you're taking that from us", 
	        		DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Cael", "Because I wanted to give us more... I was willing to risk it...", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	        new DialogueLine("", "Cael succumbs to his injuries...", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Art", "Enough of this.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "The supplies are still here. We can return what was stolen.", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "And maybe keep the bandits from reorganizing into... whatever this was.", 
	        		DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "I started this mess.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "Then help us clean it up.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "...Fine. Until the Golden Sinners stop bleeding people dry, I am with you.", 
	        		DialogueSide.LEFT, DialogueFaction.ALLY)
	    };

	    return new BattleScenario(
	        "cael_usurper",
	        "Cael's Betrayal",
	        layout,
	        ObjectiveType.DEFEAT_ALL,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	    
	}
	
	//Golem Trap
	private static BattleScenario createGolemSealTrap() {

	    int[][] layout = {
	        {1,1,1,1,1,1,1,1,1,1},
	        {1,0,0,6,0,6,0,0,0,1},
	        {1,0,6,6,0,0,6,0,0,1},
	        {1,6,6,0,0,0,6,6,6,1},
	        {1,0,0,0,0,0,6,0,0,1},
	        {1,0,0,6,0,6,6,0,0,1},
	        {1,6,6,6,0,0,6,6,6,1},
	        {1,6,0,6,6,0,6,6,6,1},
	        {1,6,6,6,0,0,6,0,0,1},
	        {1,1,1,1,1,1,1,1,1,1}
	    };

	    List<UnitSpawn> playerSpawns = new ArrayList<>();
	    playerSpawns.add(new UnitSpawn("leader", 4, 8, false));
	    playerSpawns.add(new UnitSpawn("tali", 8, 1, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 1, 1, false));
	    playerSpawns.add(new UnitSpawn("mage", 8, 4, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("stone_golem", 4, 1, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();

	    DialogueLine[] introDialogue = new DialogueLine[] {
	        new DialogueLine("", "The exit seals behind the party as molten light pours from cracks in the walls.", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Dean", "Lava. That is fricking lava. Why is it lava?", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "We're separated!", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "Less yelling, more trying not to die!", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "Everyone stay on your platform!", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Dean", "Reeeally? I was just about to go turn myself into charcoal.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("", "Stone grinds against stone. A guardian rises from the center seal.", 
	        		DialogueSide.RIGHT, DialogueFaction.NPC)
	    };

	    DialogueLine[] outroDialogue = new DialogueLine[] {
	        new DialogueLine("", "The lava slows. The seal cracks open with a final shudder.", DialogueSide.RIGHT, DialogueFaction.NPC),
	        new DialogueLine("Dean", "I vote we never take jobs from smiling merchants again.", DialogueSide.LEFT, DialogueFaction.ALLY),
	        new DialogueLine("Tali", "That was your first rule? Mine is to find and kill that Dwarf", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Penelope", "Is everyone still standing?", DialogueSide.RIGHT, DialogueFaction.ALLY),
	        new DialogueLine("Art", "Barely.", DialogueSide.LEFT, DialogueFaction.ALLY)
	    };

	    return new BattleScenario(
	        "golem_seal_trap",
	        "The Broken Seal",
	        layout,
	        ObjectiveType.SURVIVE_TURNS,
	        4,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue,
	        outroDialogue
	    );
	}
	
	
	
	
	
}
