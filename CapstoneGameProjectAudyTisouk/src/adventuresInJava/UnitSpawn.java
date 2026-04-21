package adventuresInJava;

//Controls spawn points for all units
public class UnitSpawn {

	private String unitId;
	private int col;
	private int row;
	private boolean enemy;
	
	public UnitSpawn(String unitId, int col, int row, boolean enemy) {
		
		this.unitId = unitId;
		this.col = col;
		this.row = row;
		this.enemy = enemy;
		
		
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
	
	
	
	
	
	
}
