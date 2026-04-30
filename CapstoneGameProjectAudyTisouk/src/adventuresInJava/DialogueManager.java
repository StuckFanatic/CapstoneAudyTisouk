package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;

public class DialogueManager {

	//Reads out current line
	private String[] lines;
	private int currentLine = 0;
	
	//could delete private String[] lines because we are now going to evolve our current line by line system to host more characters
	private DialogueLine[] dialogueLines;
	
	
	//Shows what is on the screen and next
	private String displayedText = "";
	private int charIndex = 0;

	//TF on if dialogue is used and how fast the message gets displayed
	private boolean active = false;
	private int textSpeed = 2; // Lower speed = faster?
	private int textTimer = 0;
	
	//Dialogue Speaker name
	private String speakerName = "";
	
	//Left and Right speaker portrait set up
	private String leftSpeakerName = "";
	private String rightSpeakerName = "";
	
	//Right now color in current speaks in blue and green to help visuals
	private DialogueFaction leftSpeakerFaction = DialogueFaction.ALLY;
	private DialogueFaction rightSpeakerFaction = DialogueFaction.NPC;
	
	
	
	// Starts a new dialogue scene with one speaker
	public void startDialogue(String speakerName, String[] lines) {

	    this.speakerName = speakerName;
	    this.lines = lines;
	    this.dialogueLines = null;
	    
	    //gets one speaker leaves right
	    leftSpeakerName = speakerName;
	    rightSpeakerName = "";
	    
	    leftSpeakerFaction = DialogueFaction.NPC;
	    rightSpeakerFaction = DialogueFaction.NPC;

	    currentLine = 0;
	    charIndex = 0;
	    textTimer = 0;
	    displayedText = "";
	    active = true;
	}

	// Starts a new dialogue scene with multiple speakers
	public void startDialogue(DialogueLine[] dialogueLines) {

	    this.dialogueLines = dialogueLines;
	    this.lines = null;
	    this.speakerName = "";

	    //gets two speaker leaves right for other PC while left is always leader
	    leftSpeakerName = "";
	    rightSpeakerName = "";
	    
	    leftSpeakerFaction = DialogueFaction.ALLY;
	    rightSpeakerFaction = DialogueFaction.NPC;
	    
	    currentLine = 0;
	    charIndex = 0;
	    textTimer = 0;
	    displayedText = "";
	    active = true;
	}
	
	//Updating the effect on each frame
	public void update() {

	    if (!active) return;

	    String currentText = getCurrentText();

	    if (charIndex < currentText.length()) {

	        textTimer++;

	        if (textTimer > textSpeed) {
	            displayedText += currentText.charAt(charIndex);
	            charIndex++;
	            textTimer = 0;
	        }
	    }
	}
	
	//Draws the dialogue in the box
	public void draw(Graphics g, int screenWidth, int screenHeight) {
		
	    if (!active) {
	        return;
	    }

	    String currentSpeaker = getCurrentSpeakerName();
	    String currentText = getCurrentText();
	    
	    DialogueSide currentSide = getCurrentSide();
	    
	    
	    DialogueFaction currentFaction = getCurrentFaction();

	    updateVisibleSpeakers(currentSpeaker, currentSide, currentFaction);
		
		
		int boxHeight = 120;
		int y = screenHeight - boxHeight - 20;
		
		int textX = 140;
		
		
		//Portrait for characters
		int portraitSize = 110;
		
		// This makes portraits rise above the dialogue box as like in fire emblem
		int portraitY = screenHeight - 190;

		int leftPortraitX = 45;
		int rightPortraitX = screenWidth - 45 - portraitSize;
		
		drawPortraitBox(
			    g,
			    leftSpeakerName,
			    leftSpeakerFaction,
			    leftPortraitX,
			    portraitY,
			    portraitSize,
			    currentSide == DialogueSide.LEFT
			);

		drawPortraitBox(
			    g,
			    rightSpeakerName,
			    rightSpeakerFaction,
			    rightPortraitX,
			    portraitY,
			    portraitSize,
			    currentSide == DialogueSide.RIGHT
			);
		
		
		//Box itself
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(40, y, screenWidth - 80, boxHeight);
		
		
		//Border of Box
		g.setColor(Color.WHITE);
		g.drawRect(40, y, screenWidth - 80, boxHeight);
		
		
		//Drawing the text, Once text is done show press enter to continue; Speaking
		g.drawString(displayedText, textX, y + 40);
		
		
		//Dialogue Name
		g.setColor(getFactionColor(currentFaction));
		g.drawString(currentSpeaker, textX, screenHeight - 120);
		
		g.setColor(Color.WHITE);
		
		
		if (charIndex >= currentText.length()) {
		    g.drawString("Press Enter...", screenWidth - 180, y + 90);
		}

	}
	

	//Moves on to the next line of text
	public void nextLine() {

	    String currentText = getCurrentText();

	    // If text is still typing, finish it instantly instead of moving on
	    if (charIndex < currentText.length()) {
	        displayedText = currentText;
	        charIndex = currentText.length();
	        return;
	    }

	    currentLine++;

	    if (dialogueLines != null) {
	        if (currentLine >= dialogueLines.length) {
	            active = false;
	            return;
	        }
	        
	    } else if (lines != null) {
	        if (currentLine >= lines.length) {
	            active = false;
	            return;
	        }
	        
	    } else {
	        active = false;
	        return;
	    }

	    // Reset typewriter for the next line
	    displayedText = "";
	    charIndex = 0;
	    textTimer = 0;
	    
	}
	
	
	//Check to make sure it is running
	public boolean isActive() {
		
		return active;
		
	}
	
	public String getSpeakerName() {
	    return speakerName;
	}
	
	//This will help sort portraits of characters
	private String getInitials(String name) {
	    if (name == null || name.isEmpty()) {
	        return "?";
	    }

	    String[] parts = name.trim().split(" ");
	    String initials = "";

	    for (String part : parts) {
	        if (!part.isEmpty()) {
	            initials += part.charAt(0);
	        }

	        if (initials.length() >= 2) {
	            break;
	        }
	    }

	    return initials.toUpperCase();
	}
	
	
	//Can now get more people in a conversation 
	private String getCurrentSpeakerName() {

	    if (dialogueLines != null && currentLine < dialogueLines.length) {
	        return dialogueLines[currentLine].getSpeakerName();
	    }

	    return speakerName;
	}
	

	private String getCurrentText() {

	    if (dialogueLines != null && currentLine < dialogueLines.length) {
	        return dialogueLines[currentLine].getText();
	    }

	    if (lines != null && currentLine < lines.length) {
	        return lines[currentLine];
	    }

	    return "";
	}
	
	//Will default to the left
	private DialogueSide getCurrentSide() {

	    if (dialogueLines != null && currentLine < dialogueLines.length) {
	        return dialogueLines[currentLine].getSide();
	    }

	    return DialogueSide.LEFT;
	}
	
	//Portrait will appear when speaking
	private void updateVisibleSpeakers(String currentSpeaker, DialogueSide currentSide, DialogueFaction faction) {

	    if (currentSide == DialogueSide.LEFT) {
	        leftSpeakerName = currentSpeaker;
	        leftSpeakerFaction = faction;
	        
	    } else {
	    	
	        rightSpeakerName = currentSpeaker;
	        rightSpeakerFaction = faction;
	    }
	}
	
	//bright active portrait and dim inactive portrait
	private void drawPortraitBox(Graphics g, String speaker, DialogueFaction faction,
            int x, int y, int size, boolean activeSpeaker) {

		if (speaker == null || speaker.isEmpty()) {
			return;
		}

		Color factionColor = getFactionColor(faction);

		if (activeSpeaker) {
			g.setColor(new Color(55, 55, 65));
		} else {
			
			g.setColor(new Color(35, 35, 40));
		}

		g.fillRect(x, y, size, size);

		// Portrait border
		if (activeSpeaker) {
			g.setColor(factionColor);
		} else {
			
			g.setColor(factionColor.darker());
		}

		g.drawRect(x, y, size, size);
		g.drawRect(x + 1, y + 1, size - 2, size - 2);

		// Placeholder initials
		String initials = getInitials(speaker);

		if (activeSpeaker) {
			g.setColor(factionColor);
		} else {
			
			g.setColor(factionColor.darker());
		}

		g.drawString(initials, x + (size / 2) - 12, y + (size / 2) + 5);
	}
	
	
	

	
	//Factions = color
	private DialogueFaction getCurrentFaction() {

	    if (dialogueLines != null && currentLine < dialogueLines.length) {
	        return dialogueLines[currentLine].getFaction();
	    }

	    return DialogueFaction.NPC;
	}
	
	//This is the colors that will be called
	private Color getFactionColor(DialogueFaction faction) {

	    if (faction == DialogueFaction.ALLY) {
	        return new Color(80, 160, 255); // blue players characters
	    }

	    if (faction == DialogueFaction.NPC) {
	        return new Color(80, 220, 120); // green non playable 
	    }

	    if (faction == DialogueFaction.ENEMY) {
	        return new Color(230, 80, 80); // red enemy
	    }

	    return Color.WHITE;
	}
	
}
