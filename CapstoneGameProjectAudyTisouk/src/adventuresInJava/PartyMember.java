package adventuresInJava;

import java.util.ArrayList;
import java.util.List;

//Allows more easier access to switching and making party
public class PartyMember {

	//Stats and Numbers for Members
	private String id;
    private String name;

    private int level;
    private int experience;

    private UnitStats stats;
    private GrowthRates growthRates;
    private CharacterClass characterClass;
    private Weapon weapon;
    private List<Weapon> weapons;
    private Weapon equippedWeapon;

    private String skillName;
    

    
    public PartyMember(String id, String name, int level, int experience,
            UnitStats stats, GrowthRates growthRates,
            CharacterClass characterClass, Weapon weapon,
            String skillName) {
    	
    	this.id = id;
        this.name = name;
        
        this.level = level;
        this.experience = experience;
        
        this.stats = stats;
        this.growthRates = growthRates;
        this.characterClass = characterClass;
        this.weapons = new ArrayList<>();
        this.weapons.add(weapon);
        this.equippedWeapon = weapon;
        
        this.skillName = skillName;
    	
    	
    	
    }
    
    
    public String getId() {
        return id;
    }
    
    
    public String getName() {
        return name;
    }

    
    public int getLevel() {
        return level;
    }

    
    public int getExperience() {
        return experience;
    }

    
    public UnitStats getStats() {
        return stats;
    }

    
    public GrowthRates getGrowthRates() {
        return growthRates;
    }

    
    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    
    public Weapon getWeapon() {
        return equippedWeapon;
    }
    
    public List<Weapon> getWeapons() {
        return weapons;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void equipWeapon(Weapon weapon) {
        if (weapons.contains(weapon)) {
            equippedWeapon = weapon;
        }
    }

    public void addWeapon(Weapon weapon) {
        if (weapon == null) {
            return;
        }

        if (!hasWeapon(weapon.getId())) {
            weapons.add(weapon);
        }
    }
    
    public boolean hasWeapon(String weaponId) {
        for (Weapon weapon : weapons) {
            if (weapon.getId().equals(weaponId)) {
                return true;
            }
        }

        return false;
    }
    
    public void clearWeapons() {
        weapons.clear();
        equippedWeapon = null;
    }
    

    public String getSkillName() {
        return skillName;
    }
    

    public void setLevel(int level) {
        this.level = level;
    }
    

    public void setExperience(int experience) {
        this.experience = experience;
    }
    

    public void setStats(UnitStats stats) {
        this.stats = stats;
    }
    

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }
    

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
    
    
    
    
	
	
	
}
