package coded;

import messages.Data;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.Text;

public final class SimpleTextCodec {
    String s;

    public String encode(Message msg) {
        if (msg instanceof Data) {
            Data d = (Data) msg;
            s = String.format("type=%s;value=%.3f;unit=%s;version=%d",
                    d.header().type(),
                    d.value(),
                    d.unit(),
                    d.header().version());

            return s;
        } else if (msg instanceof Text) {
            Text t = (Text) msg;
            s = String.format("type=%s;version=%d;msgId=%s;correlationId=%s;text=%s",
                    t.header().type(),

                    t.header().version(),
                    t.header().msgId(),
                    t.header().correlationId(),
                    t.text());

            return s;
        }
        throw new IllegalArgumentException("Unsupported message type:" + msg.getClass());
    }

    public Message decode(String s) {

        String[] parts = s.split(";");
        String type = null, unit = null, msgId=null , correlationId=null, text = null;
        double value = 0;
        int version =0 ;
        

        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length != 2)
                continue;
            switch (kv[0]) {
                case "type":
                    type = kv[1];
                    break;
                case "correlationId":
                    correlationId = kv[1];
                    break;
                case "value":
                    value = Double.parseDouble(kv[1]);
                    break;
                case "unit":
                    unit = kv[1];
                    break;
                case "msgId":
                    msgId = kv[1];
                    break;
                case "version":
                    version = Integer.parseInt(kv[1]);
                    break;
                case "text":
                    text = kv[1];
                    break;
                default:
                    break;
            }

        }
        MsgHeader h = new MsgHeader(version, MsgType.TEXT, msgId, correlationId);

        if (type.equals("DATA")) {
            return new Data(h, value, unit);
        } else if (type.equals("TEXT")) {
            return new Text(h, text);
        }
        throw new IllegalArgumentException("Unknown type: " + type);
    }

}