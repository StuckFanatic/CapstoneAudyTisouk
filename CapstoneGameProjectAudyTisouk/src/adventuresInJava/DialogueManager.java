package adventuresInJava;

import java.awt.Color;
import java.awt.Graphics;

public class DialogueManager {

	//Reads out current line
	private String[] lines;
	private int currentLine;
	
	
	//Shows what is on the screen and next
	private String displayedText = "";
	private int charIndex = 0;

	//TF on if dialogue is used and how fast the message gets displayed
	private boolean active = false;
	private int textSpeed = 2; // Lower speed = faster?
	private int textTimer = 0;
	
	//Dialogue Speaker name
	private String speakerName = "";
	
	
	//Starts a new dialogue scene
	public void startDialogue(String speakerName, String[] dialogueLines) {
		
		this.speakerName = speakerName;
		this.lines = dialogueLines;
		currentLine = 0;
		charIndex = 0;
		displayedText = "";
		active = true;
		
		
	}
	
	//Updating the effect on each frame
	public void update() {
		
		if(!active) return;
		
		if(charIndex < lines[currentLine].length()) {
			
			textTimer++;
			//Reveals the next line in sequence
			if(textTimer > textSpeed) {
				
				displayedText += lines[currentLine].charAt(charIndex);
				charIndex++;
				textTimer = 0;
				
			}
			
		}
		
	}
	
	//Draws the dialogue in the box
	public void draw(Graphics g, int screenWidth, int screenHeight) {
		
		if(!active) return;
		
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
		String initials = getInitials(speakerName);

		g.setColor(Color.YELLOW);
		g.drawString(initials, portraitX + 28, portraitY + 45);
		
		//Dialogue Name
		g.setColor(Color.YELLOW);
		
		g.drawString(speakerName, textX, screenHeight - 120);

		g.setColor(Color.WHITE);
		
		if(charIndex >= lines[currentLine].length()) {
			g.drawString("Press Enter...", screenWidth - 180, y + 90);
		}

	}

	//Moves on to the next line of text
	public void nextLine() {
		
		if(!active) return;
		
		//If next is still typing then finish them instantly
		if(charIndex < lines[currentLine].length()) {
			displayedText = lines[currentLine];
			charIndex = lines[currentLine].length();
			return;
		}
		
		currentLine++;
		
		//Will close out dialogue once it is over
		if(currentLine >= lines.length) {
			
			//Closes out dialogue manager and resets it
			active = false;
			} else {
				displayedText = "";
				charIndex = 0;
			}
		
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
	
}
