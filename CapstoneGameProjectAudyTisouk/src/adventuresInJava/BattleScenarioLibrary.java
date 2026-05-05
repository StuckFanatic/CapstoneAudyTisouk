package adventuresInJava;

import java.util.ArrayList;
import java.util.List;

//This will be the host for all maps in code
public class BattleScenarioLibrary {

	public static BattleScenario getScenario(String scenarioId) {
		
		if (scenarioId.equals("bandit_field")) {
			return createBanditField();
		}
		
		if (scenarioId.equals("forest_ambush")) {
		    return createForestAmbush();
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
				null //Normal Combat No pre-dialogue
				
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

	    return new BattleScenario(
	        "forest_ambush",
	        "Forest Ambush",
	        layout,
	        ObjectiveType.DEFEAT_ALL,
	        0,
	        playerSpawns,
	        enemySpawns,
	        reinforcements,
	        introDialogue
	    );
	}
	
	
}
