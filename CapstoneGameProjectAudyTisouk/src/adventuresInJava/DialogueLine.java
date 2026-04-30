package adventuresInJava;


//This will host lines of dialogue with the owner of the conversation
public class DialogueLine {

    private String speakerName;
    private String text;
    private DialogueSide side;
    
    //Color helps with visuals
    private DialogueFaction faction;

    public DialogueLine(String speakerName, String text, DialogueSide side, DialogueFaction faction) {
        this.speakerName = speakerName;
        this.text = text;
        this.side = side;
        this.faction = faction;
    }

    public DialogueLine(String speakerName, String text, DialogueSide side) {
        this(speakerName, text, side, DialogueFaction.ALLY);
    }

    public DialogueLine(String speakerName, String text) {
        this(speakerName, text, DialogueSide.LEFT, DialogueFaction.ALLY);
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getText() {
        return text;
    }

    public DialogueSide getSide() {
        return side;
    }

    public DialogueFaction getFaction() {
        return faction;
    }
}
