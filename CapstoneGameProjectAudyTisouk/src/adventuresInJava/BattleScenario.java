package adventuresInJava;

import java.util.ArrayList;
import java.util.List;


//This class will help with making new maps for different quests, interactions and others
public class BattleScenario {

	private String id;
	private String name;
	private int[][] layout;
	private ObjectiveType objectiveType;
	private int surviveTurnTarget;
	
	private List<UnitSpawn> playerSpawns;
	private List<UnitSpawn> enemySpawns;
	private List<ReinforcementSpawn> reinforcements;
	
	//Introduction Dialogue or not
	private DialogueLine[] introDialogue;
	
	public BattleScenario(String id, String name, int[][] layout,
							ObjectiveType objectiveType, int surviveTurnTarget,
							List<UnitSpawn> playerSpawns,
							List<UnitSpawn> enemySpawns,
							List<ReinforcementSpawn> reinforcements,
							DialogueLine[] introDialogue) {
		
		this.id = id;
		this.name = name;
		this.layout = layout;
		this.objectiveType = objectiveType;
		this.surviveTurnTarget = surviveTurnTarget;
		this.playerSpawns = playerSpawns;
		this.enemySpawns = enemySpawns;
		this.reinforcements = reinforcements;
		this.introDialogue = introDialogue;
		
		
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int[][] getLayout() {
		return layout;
	}
	
	public ObjectiveType getObjectiveType() {
		return objectiveType;
	}
	
	public int getSurviveTurnTarget() {
		return surviveTurnTarget;
	}
	
	public List<UnitSpawn> getPlayerSpawns() {
		return playerSpawns;
	}
	
	public List<UnitSpawn> getEnemySpawns() {
		return enemySpawns;
	}
	
	public List<ReinforcementSpawn> getReinforcement() {
		return reinforcements;
	}
	
	public DialogueLine[] getIntroDialogue() {
	    return introDialogue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
