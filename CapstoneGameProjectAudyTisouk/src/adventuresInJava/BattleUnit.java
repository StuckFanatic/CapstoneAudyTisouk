package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;

//This class will track unit movement and position and any other combat related actions
public class BattleUnit {

	private String name;
	private int col;
	private int row;
	private boolean enemy;
	private boolean hasMoved;
	private boolean hasActed;
	
	private int hp;
	private int maxHp;
	private CharacterClass characterClass;
	
	private UnitStats stats;
	
	private Weapon weapon;
	
	public BattleUnit(String name, int col, int row, boolean enemy, Weapon weapon, CharacterClass characterClass, UnitStats stats) {
		
		this.name = name;
		this.col = col;
		this.row = row;
		this.enemy = enemy; //This will detect friend from foe
		this.weapon = weapon;
		this.characterClass = characterClass;
		this.stats = stats;
		
		this.hasMoved = false;
		this.hasActed = false;
		
		this.maxHp = stats.getMaxHp();
		this.hp = stats.getMaxHp();
		
	}
	
	public void draw(Graphics g, int tileSize) {
		
		if (enemy) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.CYAN);
		}
		
		g.fillOval(col * tileSize + 8, row * tileSize + 8, tileSize - 16, tileSize - 16);
	}
	
	
	public String getName() {
		
		return name;
	}
	
	public int getCol() {
		
		return col;
	}
	
	public int getRow() {
		
		return row;
	}
	
	public void setPosition(int col, int row) {
		
		this.col = col;
		this.row = row;
		
	}
	
	public boolean isEnemy() {
		
		return enemy;
	}
	
	public Weapon getWeapon() {
		
		return weapon;
	}
	
	public CharacterClass getCharacterClass() {
		
		return characterClass;
	}
	
	public UnitStats getStats() {
		
		return stats;
	}
	
	public boolean hasMoved() {
			
		return hasMoved;
	}
	
	public void setHasMoved(boolean hasMoved) {
		
		this.hasMoved = hasMoved;
	}
	
	public boolean hasActed() {
		
		return hasActed;
	}
	
	public void setHasActed(boolean hasActed) {
		
		this.hasActed = hasActed;
	}
	
	public void resetTurn() {
		
		hasMoved = false;
		hasActed = false;
	}
	
	public int getHp() {
		
		return hp;
	}
	
	public int getMaxHp() {
		
		return maxHp;
	}
	
	public void setHp(int hp) {
		
		this.hp = hp;
	}
	
	public void takeDamage(int damage) {
		
		hp -= damage;
		if (hp < 0) {
			hp = 0;
		}
	}
	
	public boolean isAlive() {
		
		return hp > 0;
	}
	
	public int getArmorClass() {
		
		return 10 + stats.getDefense() + (stats.getSpeed() / 4);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
