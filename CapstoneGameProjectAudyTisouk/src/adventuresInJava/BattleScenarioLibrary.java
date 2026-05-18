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
	    playerSpawns.add(new UnitSpawn("leader", 1, 1, false));
	    playerSpawns.add(new UnitSpawn("archer_ally", 2, 1, false));
	    playerSpawns.add(new UnitSpawn("mage", 3, 1, false));

	    List<UnitSpawn> enemySpawns = new ArrayList<>();
	    enemySpawns.add(new UnitSpawn("hunter", 7, 2, true));
	    enemySpawns.add(new UnitSpawn("hunter", 7, 7, true));

	    List<ReinforcementSpawn> reinforcements = new ArrayList<>();
	    reinforcements.add(new ReinforcementSpawn(3, "hunter", 8, 1, true));
	    
	    //Combat PreDialogue
	    DialogueLine[] introDialogue = new DialogueLine[] {
	    	    new DialogueLine("Leader", "Something feels wrong.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Archer Ally", "The forest is too quiet.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Bandit", "You picked the wrong road, travelers!", DialogueSide.RIGHT, DialogueFaction.ENEMY),
	    	    new DialogueLine("Leader", "Weapons ready!", DialogueSide.LEFT, DialogueFaction.ALLY)
	    	};
	    //Combat Post Battle
	    DialogueLine[] outroDialogue = new DialogueLine[] {
	    	    new DialogueLine("Archer Ally", "That was too close.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Leader", "But the road is safe now.", DialogueSide.LEFT, DialogueFaction.ALLY),
	    	    new DialogueLine("Leader", "Let's report back to the elder.", DialogueSide.LEFT, DialogueFaction.ALLY)
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
	
	
	
	
}
