package adventuresInJava;

//Unit Stat Block
public class UnitStats {

	private int maxHp;
	private int strength;
	private int magic;
	private int skill;
	private int speed;
	private int luck;
	private int defense;
	private int resistance;
	private int movement;
	
	
	
	public UnitStats(int maxHp, int strength, int magic, int skill, int speed, int luck, int defense, int resistance, int movement) {
		
		this.maxHp = maxHp;
		this.strength = strength;
		this.magic = magic;
		this.skill = skill;
		this.speed = speed;
		this.luck = luck;
		this.defense = defense;
		this.resistance = resistance;
		this.movement = movement;
		
	}
	//Getters
	
	public int getMaxHp() {
		
		return maxHp;
	}
	
	public int getStrength() {
		
		return strength;
	}
	
	public int getMagic() {
		
		return magic;
	}
	
	public int getSkill() {
		
		return skill;
	}
	
	public int getSpeed() {
		
		return speed;
	}
	
	public int getLuck() {
		
		return luck;
	}
	
	public int getDefense() {
		
		return defense;
	}
	
	public int getResistance() {
		
		return resistance;
	}
	
	public int getMovement() {
		
		return movement;
	}
	
	//Setters
	
	public void setMaxHp(int maxHp) {
	    this.maxHp = maxHp;
	}

	public void setStrength(int strength) {
	    this.strength = strength;
	}

	public void setMagic(int magic) {
	    this.magic = magic;
	}

	public void setSkill(int skill) {
	    this.skill = skill;
	}

	public void setSpeed(int speed) {
	    this.speed = speed;
	}

	public void setLuck(int luck) {
	    this.luck = luck;
	}

	public void setDefense(int defense) {
	    this.defense = defense;
	}

	public void setResistance(int resistance) {
	    this.resistance = resistance;
	}

	public void setMovement(int movement) {
	    this.movement = movement;
	}
	
}
