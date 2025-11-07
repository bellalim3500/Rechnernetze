package messages;

public final class Data implements Message {
    
    private final MsgHeader header;
    private final double value;
    private final String unit;
    
    

    public Data(MsgHeader header, double value, String unit) {
        this.header = header;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public MsgHeader header() {
        return header;
    }


    public double value() {
        return value;
    }

    public String unit() {
        return unit;
    }

    
    @Override 
    public String toString(){
        return "Data[value=" + value +", unit= " + unit +"]";
    }
}
