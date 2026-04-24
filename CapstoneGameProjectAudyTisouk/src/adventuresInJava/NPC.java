package adventuresInJava;

//NPC Class to host many different characters
public class NPC {

	private String name;
	private int col;
	private int row;
	private String questId;
	
	private String[] defaultDialogue;
	private String[] questNotStartedDialogue;
	private String[] questActiveDialogue;
	private String[] questCompletedDialogue;
	
	
	public NPC(String name, int col, int row, String questId, 
		 String[] defaultDialogue,
		 String[] questNotStartedDialogue,
		 String[] questActiveDialogue,
		 String[] questCompletedDialogue) {
		
		
		this.name = name;
		this.col = col;
		this.row = row;
		this.questId = questId;
		this.defaultDialogue = defaultDialogue;
		this.questNotStartedDialogue = questNotStartedDialogue;
		this.questActiveDialogue = questActiveDialogue;
		this.questCompletedDialogue = questCompletedDialogue;
		
	}
	
	public String getName() {
		
		return name;
	}
	
	public int getCol () {
		
		return col;
	}
	
	public int getRow() {
		
		return row;
	}
	
	public String getQuestId() {
		
		return questId;
	}
	
	public boolean hasQuest() {
		
		return questId != null && !questId.isEmpty();
	}
	
	public String[] getDefaultDialogue() {
		
		return defaultDialogue;
	}
	
	public String[] getQuestNotStartedDialogue() {
		
		return questNotStartedDialogue;
	}
	
	public String[] getQuestActiveDialogue() {
		
		return questActiveDialogue;
	}
	
	public String[] getQuestCompletedDialogue() {
		
		return questCompletedDialogue;
	}
	
	
}
