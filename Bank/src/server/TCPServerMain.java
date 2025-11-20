package server;

import java.io.*;
import java.net.*;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.request.AbhebenReqMessage;
import messages.request.KarteMessage;
import messages.request.PinMessage;
import messages.response.AbhebenOkMessage;
import messages.response.ErrorMessage;
import messages.response.KarteOkMessage;
import messages.response.KontostandMessage;
import messages.response.PinOkMessage;
import messages.response.Quit;
import coded.SimpleTextCodec;

public class TCPServerMain {
    public static void main(String argv[]) throws Exception {
        // TODO make sure that PIN INVALID and KARTE INVALID work
        final int MINCARD = 6;
        final int MAXCARD = 20;
        final int MINPIN = 4;
        final int MAXPIN = 6;

        String encodedResponse;

        SimpleTextCodec codec = new SimpleTextCodec();
        Message clientMessage, response;
        boolean headerDone = false;

        ServerSocket welcomeSocket = new ServerSocket(6789);

        outer: while (true) {
            System.out.println("Warte auf Client...");

            Socket connectionSocket = welcomeSocket.accept();
            System.out.printf("Client connected: %s%n", connectionSocket.getRemoteSocketAddress());

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
                    connectionSocket.getInputStream()));

            BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(
                    connectionSocket.getOutputStream()));

            clientMessage = readResponse(inFromClient, codec);
            System.out.println("In from Client (decoded):\n" + clientMessage);

            // is Karte Okay?
            int cardLength = ((KarteMessage) clientMessage).card().length();
            if (cardLength >= MINCARD && cardLength <= MAXCARD) {
                response = new KarteOkMessage(new MsgHeader(1, MsgType.KARTE, "1", "1", System.currentTimeMillis()));
            } else {
                response = new ErrorMessage(new MsgHeader(1, MsgType.ERROR, "0", "0", System.currentTimeMillis()),
                        "KARTE INVALID");
                encodedResponse = codec.encode(response);

                System.out.println("Out to Client:\n" + encodedResponse);
                outToClient.write(encodedResponse + '\n');
                outToClient.flush();

                outToClient.close();
                inFromClient.close();
                connectionSocket.close();
                continue outer;
            }

            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);
            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

            // Reading in from client
            clientMessage = readResponse(inFromClient, codec);
            // create client pin

            // check pin
            int pinLength = ((PinMessage) clientMessage).pinLength();
            if (pinLength >= MINPIN && pinLength <= MAXPIN) {
                response = new PinOkMessage(new MsgHeader(1, MsgType.PIN_OK, "0", "0", System.currentTimeMillis()));
            } else {
                response = new ErrorMessage(new MsgHeader(1, MsgType.ERROR, "0", "0", System.currentTimeMillis()),
                        "PIN INVALID");
            }

            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);

            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

            do {

                clientMessage = readResponse(inFromClient, codec);

                // Check which type of message was sent and create a response message based on
                // that
                if (clientMessage.header().type() == MsgType.KONTOSTAND_REQ) {
                    response = new KontostandMessage(
                            new MsgHeader(1, MsgType.KONTOSTAND, "0", "0", System.currentTimeMillis()), 1300);
                } else if (clientMessage.header().type() == MsgType.ABHEBEN_REQ) {
                    // would technically need logic for checking if the konto has enough money
                    response = new AbhebenOkMessage(
                            new MsgHeader(1, MsgType.ABHEBEN_OK, "0", "0", System.currentTimeMillis()),
                            ((AbhebenReqMessage) clientMessage).amount());
                } else if (clientMessage.header().type() == MsgType.QUIT_REQ) {
                    response = new Quit(new MsgHeader(1, MsgType.QUIT, "0", "0", System.currentTimeMillis()));
                }

                // encode and send out message to client
                encodedResponse = codec.encode(response);

                System.out.println("Out to Client:\n" + encodedResponse);

                outToClient.write(encodedResponse + '\n');
                outToClient.flush();

            } while (!(response instanceof Quit));

        }

    }

    private static Message readResponse(BufferedReader inFromClient, SimpleTextCodec codec) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String responseString;
        boolean headerDone = false;

        
        while ((responseString = inFromClient.readLine()) != null) {
            if (responseString.isEmpty()) {
                headerDone = true;
                break;
            }
            stringBuilder.append(responseString).append("\r\n");
        }

        if (!headerDone) {
            System.out.println("[WARN] Server disconnected before completing header");
        }

        String bodyLine;
        while ((bodyLine = inFromClient.readLine()) != null && !bodyLine.isEmpty()) {
            stringBuilder.append("\r\n");
            stringBuilder.append(bodyLine).append("\r\n");
        }

        System.out.println(stringBuilder);

        return codec.decode(stringBuilder);

    }

}
