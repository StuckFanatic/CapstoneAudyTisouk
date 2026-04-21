package adventuresInJava;

//Reinforcements
public class ReinforcementSpawn {

	private int turn;
	private String unitId;
	private int col;
	private int row;
	private boolean enemy;
	private boolean spawned;
	
	
	public ReinforcementSpawn(int turn, String unitId, int col, int row, boolean enemy) {
		
		this.turn = turn;
		this.unitId = unitId;
		this.col = col;
		this.row = row;;
		this.enemy = enemy;
		this.spawned = false;
	}
	
	public int getTurn() {
		
		return turn;
		
	}
	
	public String getUnitId() {
		
		return unitId;
		
	}
	
	public int getCol() {
		
		return col;
		
	}
	
	public int getRow() {
		
		return row;
		
	}
	
	public boolean isEnemy() {
		
		return enemy;
		
	}
	
	public boolean hasSpawned() {
		
		return spawned;
		
	}
	
	public void setSpawned(boolean spawned) {
		
		this.spawned = spawned;
		
	}
	
	
	
	
}
