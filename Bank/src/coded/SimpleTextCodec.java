package coded;

import java.util.Arrays;

import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
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

    public String encode(Message msg) {
        String msgBody;
        String msgHeader = msg.header().toString();

        if (msg instanceof AbhebenReqMessage) {
            AbhebenReqMessage d = (AbhebenReqMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;

        } else if (msg instanceof ByeMessage) {
            ByeMessage d = (ByeMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof HelloMessage) {
            HelloMessage d = (HelloMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof KarteMessage) {
            KarteMessage d = (KarteMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof KontostandReqMessage) {
            KontostandReqMessage d = (KontostandReqMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof PinMessage) {
            PinMessage d = (PinMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof AbhebenOkMessage) {
            AbhebenOkMessage d = (AbhebenOkMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof ErrorMessage) {
            ErrorMessage d = (ErrorMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof KarteOkMessage) {
            KarteOkMessage d = (KarteOkMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof KontostandMessage) {
            KontostandMessage d = (KontostandMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        } else if (msg instanceof PinOkMessage) {
            PinOkMessage d = (PinOkMessage) msg;
            msgBody = d.toString();
            return msgHeader + "\r\n\r\n" + msgBody;
        }
        throw new IllegalArgumentException("Unsupported message type:" + msg.getClass());
    }

    public Message decode(StringBuilder sb) {

        String s = sb.toString();
        MsgHeader header;
        Message msg = null;
        String headerLines[], bodyLines[];

        String[] parts = s.split("\r\n\r\n"); // should be 2 (header SP body); split at \r\n\r\n
        headerLines = parts[0].split("\r\n"); // splits lines of header
        bodyLines = parts[1].split("\r\n"); // splits lines of body

        try {
            header = constructHeader(headerLines);
            msg = constructMessage(bodyLines, header);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    public MsgHeader constructHeader(String headerLines[]) throws Exception {

        int version = 0;
        MsgType type = null;
        String msgId = null;
        String correlationId = null;
        long timestampMillis = 0;
        MsgHeader header = null;

        for (String part : headerLines) {
            String[] headerFields = part.split(": ", 2); // from "Version: 1" to "Version" and "1" to isolate value
            if (headerFields.length != 2)
                continue;
            switch (headerFields[0]) {
                case "Version":
                    version = Integer.parseInt(headerFields[1]);
                    break;
                case "Type":
                    type = MsgType.valueOf(headerFields[1].toUpperCase());
                    break;
                case "MsgId":
                    msgId = headerFields[1];
                    break;
                case "CorrelationId":
                    correlationId = headerFields[1];
                    break;
                case "Timestamp in Millis":
                    timestampMillis = System.currentTimeMillis();
                    break;
                default:
                    throw new Exception("Unsupported Header-Field");
            }

        }
        header = new MsgHeader(version, type, msgId, correlationId, timestampMillis);
        return header;

    }

    public Message constructMessage(String bodyLines[], MsgHeader header) throws Exception {
        String card = null;
        double amount = 0;
        String text = null;
        int pin = 0;
        String reason = null;
        Message msg = null;
        String normalized = null;

        for (String part : bodyLines) {

            String command = part.split(" ")[0];
            String bodyFields[] = part.split(" ");
            bodyFields = Arrays.copyOfRange(bodyFields, 1, bodyFields.length); // remove first word

            switch (command) {
                case "ABHEBEN_REQ":
                    card = bodyFields[0];
                    normalized = bodyFields[1].replace(",",".");
                    amount = Double.parseDouble(normalized);
                    msg = new AbhebenReqMessage(header, card, amount);
                    break;
                case "BYE":
                    text = String.join(" ", bodyFields);
                    msg = new ByeMessage(header, text);
                    break;
                case "HELLO":
                    text = String.join(" ", bodyFields);
                    msg = new HelloMessage(header, text);
                    break;
                case "KARTE":
                    card = bodyFields[0];
                    msg = new KarteMessage(header, card);
                    break;
                case "KONTOSTAND_REQ":
                    card = bodyFields[0];
                    msg = new KontostandReqMessage(header, card);
                    break;
                case "PIN":
                    pin = Integer.parseInt(bodyFields[0]);
                    msg = new PinMessage(header, pin);
                    break;
                case "ABHEBEN_OK":
                    normalized = bodyFields[0].replace(",",".");
                    amount = Double.parseDouble(normalized);
                    msg = new AbhebenOkMessage(header, amount);
                    break;
                case "ERROR":
                    reason = String.join(" ", bodyFields);
                    msg = new ErrorMessage(header, reason);
                    break;
                case "KARTE_OK":
                    msg = new KarteOkMessage(header);
                    break;
                case "KONTOSTAND":
                    normalized = bodyFields[0].replace(",",".");
                    amount = Double.parseDouble(normalized);
                    msg = new KontostandMessage(header, amount);
                    break;
                case "PIN_OK":
                    msg = new PinOkMessage(header);
                    break;
                default:
                    throw new Exception("Unsupported Body-Field");
            }

        }

        return msg;

    }
}