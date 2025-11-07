package coded;

import messages.Data;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.Text;
import messages.request.AbhebenReqMessage;
import messages.request.ByeMessage;
import messages.request.HelloMessage;
import messages.request.KarteMessage;
import messages.request.KontostandReqMessage;
import messages.request.PinMessage;
import messages.response.AbhebenOkMessage;
import messages.response.ErrorMessage;
import messages.response.KarteOkMessage;
import messages.response.KontostandMessage;
import messages.response.PinOkMessage;

public final class SimpleTextCodec {
    private String body;
    private String header;
    private String encodedMessage;

    public String encode(Message msg) {

        header = msg.header().toString();

        if (msg instanceof AbhebenReqMessage) {
            AbhebenReqMessage d = (AbhebenReqMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;

        } else if (msg instanceof ByeMessage) {
            ByeMessage d = (ByeMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof HelloMessage) {
            HelloMessage d = (HelloMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof KarteMessage) {
            KarteMessage d = (KarteMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof KontostandReqMessage) {
            KontostandReqMessage d = (KontostandReqMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        }else if (msg instanceof PinMessage) {
            PinMessage d = (PinMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof AbhebenOkMessage) {
            AbhebenOkMessage d = (AbhebenOkMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof ErrorMessage) {
            ErrorMessage d = (ErrorMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof KarteOkMessage) {
            KarteOkMessage d = (KarteOkMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        }else if (msg instanceof KontostandMessage) {
            KontostandMessage d = (KontostandMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        } else if (msg instanceof PinOkMessage) {
            PinOkMessage d = (PinOkMessage) msg;
            body = d.toString();
            return encodedMessage = header + body;
        }
        throw new IllegalArgumentException("Unsupported message type:" + msg.getClass());
    }

    public Message decode(String s) {

        String[] parts = s.split(";");
        String type = null, unit = null, msgId = null, correlationId = null, text = null;
        double value = 0;
        int version = 0;

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