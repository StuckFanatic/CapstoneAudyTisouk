package adventuresInJava;

//Weapons of mass destruction
public class Weapon {

	private String name;
	private int minRange;
	private int maxRange;
	private int attackBonus;
	private int damageDiceCount;
	private int damageDiceSides;
	private int damageBonus;
	
	public Weapon(String name, int minRange, int maxRange, int attackBonus,
			int damageDiceCount, int damageDiceSides, int damageBonus) {
		
		this.name = name;
		this.minRange = minRange;
		this.maxRange = maxRange;
		this.attackBonus = attackBonus;
		this.damageDiceCount = damageDiceCount;
		this.damageDiceSides = damageDiceSides;
		this.damageBonus = damageBonus;
		
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getMinRange() {
		
		return minRange;
	}
	
	public int getMaxRange() {
		
		return maxRange;
	}
	
	public int getAttackBonus() {
		
		return attackBonus;
	}
	
	public int getDamageDiceCount() {
		
		return damageDiceCount;
	}
	
	public int getDamageDiceSides() {
		
		return damageDiceSides;
	}
	
	public int getDamageBonus() {
		
		return damageBonus;
	}
	
	
}
