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
	
	
	
	// Starts a new dialogue scene with one speaker
	public void startDialogue(String speakerName, String[] lines) {

	    this.speakerName = speakerName;
	    this.lines = lines;
	    this.dialogueLines = null;

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
		
		
		int boxHeight = 120;
		int y = screenHeight - boxHeight - 20;
		
		
		//Portrait for characters
		int portraitX = 40;
		int portraitY = screenHeight - 135;
		int portraitSize = 80;
		
		
		//Portrait moves text over to the right
		int textX = portraitX + portraitSize + 25;
		
		
		//Box itself
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(40, y, screenWidth - 80, boxHeight);
		
		
		//Border of Box
		g.setColor(Color.WHITE);
		g.drawRect(40, y, screenWidth - 80, boxHeight);
		
		
		//Drawing the text, Once text is done show press enter to continue
		g.drawString(displayedText, textX, y + 40 );
		
		
		//Portrait
		g.setColor(new Color(35, 35, 35));
		g.fillRect(portraitX, portraitY, portraitSize, portraitSize);

		g.setColor(Color.WHITE);
		g.drawRect(portraitX, portraitY, portraitSize, portraitSize);
		
		
		//For now gets the initials of the speaker so we can use it as a placeholder
		String initials = getInitials(currentSpeaker);

		g.setColor(Color.YELLOW);
		g.drawString(initials, portraitX + 28, portraitY + 45);
		
		
		//Dialogue Name
		g.setColor(Color.YELLOW);
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
	
	
	
}
