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
import coded.SimpleTextCodec;

public class TCPServerMain {
    public static void main(String argv[]) throws Exception {
        // TODO make sure that PIN INVALID and KARTE INVALID work
        final int MINCARD = 6;
        final int MAXCARD = 20;
        final int MINPIN = 4;
        final int MAXPIN = 6;

        // hardcoded for MsgType.PIN
        // TODO cascading cases, that send, check and answer messages according to
        // Client-Logic
        // TODO according to protocol-timeline e.g. only if(PIN_OK) its possible to send
        // KontostandMessage
        String clientEncoded, encodedResponse;
        StringBuilder clientEncodedBuilder;
        int clientPin;
        SimpleTextCodec codec = new SimpleTextCodec();
        Message clientMessage, response;
        boolean headerDone = false;

        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Warte auf Client...");

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            System.out.printf("Client connected: %s%n", connectionSocket.getRemoteSocketAddress());

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
                    connectionSocket.getInputStream()));

            BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(
                    connectionSocket.getOutputStream()));

            clientEncodedBuilder = new StringBuilder();
            String line;
            while ((line = inFromClient.readLine()) != null) {

                // usually you check if(line.isEmpty()) and break; but our messages are defined
                // as header CRLF CRLF body
                // which produces an empty line. This would cause the body not beeing read

                if (line.isEmpty()) {
                    headerDone = true;
                    break;
                }
                clientEncodedBuilder.append(line).append("\r\n");
            }

            if (!headerDone) {
                System.out.println("Client disconnected before completing header");
            }

            String bodyLine;

            // read body
            while ((bodyLine = inFromClient.readLine()) != null && !bodyLine.isEmpty()) {
                clientEncodedBuilder.append("\r\n");
                clientEncodedBuilder.append(bodyLine).append("\r\n");
            }

            clientEncoded = clientEncodedBuilder.toString();

            System.out.println("In from Client (encoded):\n" + clientEncoded);

            clientMessage = codec.decode(clientEncodedBuilder);
            System.out.println("In from Client (decoded):\n" + clientMessage);

            // is Karte Okay?
            int cardLength = ((KarteMessage) clientMessage).card().length();
            if (cardLength >= MINCARD && cardLength <= MAXCARD) {
                response = new KarteOkMessage(new MsgHeader(1, MsgType.KARTE, "1", "1", System.currentTimeMillis()));
            } else {
                response = new ErrorMessage(new MsgHeader(1, MsgType.ERROR, "0", "0", System.currentTimeMillis()), "KARTE INVALID");
            }

            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);
            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

            // Reading in from client TODO should be in a method
            clientEncodedBuilder.setLength(0);
            line = null;
            bodyLine = null;

            while ((line = inFromClient.readLine()) != null) {

                if (line.isEmpty()) {
                    headerDone = true;
                    break;
                }
                clientEncodedBuilder.append(line).append("\r\n");
            }

            if (!headerDone) {
                System.out.println("Client disconnected before completing header");
            }

            while ((bodyLine = inFromClient.readLine()) != null && !bodyLine.isEmpty()) {
                clientEncodedBuilder.append("\r\n");
                clientEncodedBuilder.append(bodyLine).append("\r\n");
            }

            clientEncoded = clientEncodedBuilder.toString();

            System.out.println("In from Client (encoded):\n" + clientEncoded);

            clientMessage = codec.decode(clientEncodedBuilder);

            // create client pin
            clientPin = ((PinMessage) clientMessage).pin();

            // check pin
            int pinLength = ((PinMessage) clientMessage).pinLength();
            if (pinLength >= MINPIN && pinLength <= MAXCARD) {
                response = new PinOkMessage(new MsgHeader(1, MsgType.PIN_OK, "0", "0", System.currentTimeMillis()));
            } else {
                response = new ErrorMessage(new MsgHeader(1, MsgType.ERROR, "0", "0", System.currentTimeMillis()), "PIN INVALID");
            }

            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);

            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

            // read in Balance or Withdrawal message
            // Reading in from client TODO should be in a method
            clientEncodedBuilder.setLength(0);
            line = null;
            bodyLine = null;

            while ((line = inFromClient.readLine()) != null) {

                if (line.isEmpty()) {
                    headerDone = true;
                    break;
                }
                clientEncodedBuilder.append(line).append("\r\n");
            }

            if (!headerDone) {
                System.out.println("Client disconnected before completing header");
            }

            while ((bodyLine = inFromClient.readLine()) != null && !bodyLine.isEmpty()) {
                clientEncodedBuilder.append("\r\n");
                clientEncodedBuilder.append(bodyLine).append("\r\n");
            }

            clientEncoded = clientEncodedBuilder.toString();

            System.out.println("In from Client (encoded):\n" + clientEncoded);

            clientMessage = codec.decode(clientEncodedBuilder);
            
            // Check which type of message was sent and create a response message based on that
            if (clientMessage.header().type() == MsgType.KONTOSTAND_REQ) {
                response = new KontostandMessage(new MsgHeader(1, MsgType.KONTOSTAND, "0", "0", System.currentTimeMillis()), 1300);
            } else if (clientMessage.header().type() == MsgType.ABHEBEN_REQ) {
                // would technically need logic for checking if the konto has enough money
                response = new AbhebenOkMessage(new MsgHeader(1, MsgType.ABHEBEN_OK, "0", "0", System.currentTimeMillis()), ((AbhebenReqMessage)clientMessage).amount());
            }

            // encode and send out message to client
            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);

            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

        }

    }

}
