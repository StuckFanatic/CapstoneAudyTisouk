package adventuresInJava;

public class ShopItem {
	
	private String weaponId;
    private String displayName;
    private int price;

    public ShopItem(String weaponId, String displayName, int price) {
        this.weaponId = weaponId;
        this.displayName = displayName;
        this.price = price;
        
    }

    public String getWeaponId() {
        return weaponId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }
    
    
    
    
}
