package messages;

public class Text implements Message{

    MsgHeader h; 
    String text;
    public Text(MsgHeader h, String text) {
        this.h = h;
        this.text = text;
    }
    public MsgHeader header() {
        return h;
    }
    public String text() {
        return text;
    }

    @Override
    public String toString(){
        return "Header: " + h.toString() + "\nText = " + text;

    }

    
    
}
