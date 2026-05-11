package adventuresInJava;

//Different Units will have different abilities and parameters on how they play
public class CharacterClass {

	private String name;
	private int maxHp;
	private int armorClass;
	private int movementRange;
	
	private WeaponType[] allowedWeaponTypes;
	
	public CharacterClass(String name, int maxHp, int armorClass, int movementRange, WeaponType[] allowedWeaponTypes) {
		
		this.name = name;
		this.maxHp = maxHp;
		this.armorClass = armorClass;
		this.movementRange = movementRange;
		this.allowedWeaponTypes = allowedWeaponTypes;
		
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
	
	public boolean canUseWeaponType(WeaponType weaponType) {

	    if (allowedWeaponTypes == null) {
	        return false;
	    }

	    for (WeaponType type : allowedWeaponTypes) {
	        if (type == weaponType) {
	            return true;
	        }
	    }

	    return false;
	}
	
	public WeaponType[] getAllowedWeaponTypes() {
	    return allowedWeaponTypes;
	}
	
	
	
}
