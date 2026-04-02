package adventuresInJava;

//Different Units will have different abilities and parameters on how they play
public class CharacterClass {

	private String name;
	private int maxHp;
	private int armorClass;
	private int movementRange;
	
	public CharacterClass(String name, int maxHp, int armorClass, int movementRange) {
		
		this.name = name;
		this.maxHp = maxHp;
		this.armorClass = armorClass;
		this.movementRange = movementRange;
		
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getMaxHp() {
		
		return maxHp;
	}
	
	public int getArmorClass() {
		
		return armorClass;
	}
	
	public int getMovementRange() {
		
		return movementRange;
	}
	
	
	
}
