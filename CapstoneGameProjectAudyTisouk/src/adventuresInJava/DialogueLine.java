package adventuresInJava;


//This will host lines of dialogue with the owner of the conversation
public class DialogueLine {

    private String speakerName;
    private String text;

    public DialogueLine(String speakerName, String text) {
        this.speakerName = speakerName;
        this.text = text;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getText() {
        return text;
    }
}
