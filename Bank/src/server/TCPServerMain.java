package server;

import java.io.*;
import java.net.*;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.request.PinMessage;
import messages.response.PinOkMessage;
import coded.SimpleTextCodec;

public class TCPServerMain {
    public static void main(String argv[]) throws Exception {

        // hardcoded for MsgType.PIN
        // TODO cascading cases, that send, check and answer messages according to Client-Logic
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

                //usually you check if(line.isEmpty()) and break; but our messages are defined as header CRLF CRLF body 
                //which produces an empty line. This would cause the body not beeing read

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

            clientPin = ((PinMessage) clientMessage).pin();

            // logic to check pin

            response = new PinOkMessage(new MsgHeader(1, MsgType.PIN_OK, "0", "0", System.currentTimeMillis()));
            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + encodedResponse);

            outToClient.write(encodedResponse + '\n');
            outToClient.flush();

        }

    }

}
