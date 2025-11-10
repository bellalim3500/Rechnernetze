package coded;

import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.request.HelloMessage;

public class UnitTest {
    // unit test for codec
    // creates a test MsgHeader and Message of the specified msgType,
    // encodes it and checks wether the original msg and the en- and decoded
    // versions are equal

    public static void main(String argv[]){
        StringBuilder stringBuilder = new StringBuilder();
        SimpleTextCodec codec = new SimpleTextCodec();
        MsgHeader msgHeader = new MsgHeader(101, MsgType.HELLO, "mathi", "mathi", System.currentTimeMillis());
        HelloMessage msg = new HelloMessage(msgHeader, "Hello Test");
        Message decoded;

        System.out.println(msg);
        System.out.println(msg.toString());

        String encoded = codec.encode(msg);
        System.out.println(encoded);

        stringBuilder.append(encoded).append("\r\n");

        decoded = codec.decode(stringBuilder);
        System.out.println(decoded);

        if (msg.toString().equals(decoded.toString())) { // It's necessary to use toString(). I dont't know why since
                                                         // msg and msg.toString() print exactly the same

                                                         //Failing because of timestamp
            System.out.printf("test passed");
        } else {
            System.out.printf("test failed");
        }
    }

    
}
