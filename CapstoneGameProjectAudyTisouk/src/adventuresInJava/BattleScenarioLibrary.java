package adventuresInJava;

import java.util.ArrayList;
import java.util.List;

//This will be the host for all maps in code
public class BattleScenarioLibrary {

	public static BattleScenario getScenario(String scenarioId) {
		
		if (scenarioId.equals("bandit_field")) {
			return createBanditField();
		}
		
		return createBanditField();
		
	}
	
	//Bandit Field
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
		enemySpawns.add(new UnitSpawn("bandit", 6, 6, true));
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
				reinforcements
				
		);
	}
	
	
}
