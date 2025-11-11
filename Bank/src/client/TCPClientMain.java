package client;

import java.io.*;
import java.net.*;

import coded.SimpleTextCodec;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.request.AbhebenReqMessage;
import messages.request.ByeMessage;
import messages.request.KarteMessage;
import messages.request.KontostandReqMessage;
import messages.request.PinMessage;
import messages.request.QuitReq;

// TODO the message classes should only return their own toString not the headers as well
class TCPClient {
    public static void main(String argv[]) throws Exception {
        final int MAXPINCOUNT = 3;

        String encodedMsg, responseString, menuInput, cardNo;
        StringBuilder stringBuilder = new StringBuilder();
        Message response, request;
        int pin;
        int pinCount = 0;
        SimpleTextCodec codec = new SimpleTextCodec(); // create codec
        BufferedReader inFromUser;
        BufferedWriter outToServer;
        BufferedReader inFromServer;
        boolean headerDone = false;
        Socket clientSocket = new Socket("localhost", 6789);
        System.out.println("Client connected end with \"END\"");

        // user input
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        // user output to server
        outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        // server input to user
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (clientSocket.isConnected()) {

            // send CardNo to Bank/Server

            System.out.println("CardoNo?\n");
            cardNo = inFromUser.readLine();

            // build Message Object and encode
            request = new KarteMessage(new MsgHeader(1, MsgType.KARTE, "1", "1", System.currentTimeMillis()), cardNo);
            encodedMsg = codec.encode(request);

            // send encoded String + '\r\n' to signal end of String
            outToServer.write(encodedMsg + "\r\n");
            outToServer.flush();
            System.out.println("CardNo sent:\n" + encodedMsg);

            // read response
            while ((responseString = inFromServer.readLine()) != null) {
                if (responseString.isEmpty()) {
                    headerDone = true;
                    break;
                }
                stringBuilder.append(responseString).append("\r\n");
            }

            if (!headerDone) {
                System.out.println("Server disconnected before completing header");
            }

            String bodyLine;

            while ((bodyLine = inFromServer.readLine()) != null && !bodyLine.isEmpty()) {
                stringBuilder.append("\r\n");
                stringBuilder.append(bodyLine).append("\r\n");
            }

            response = codec.decode(stringBuilder);

            // Check if cardNo-Response is !OK, if so print ErrorMessage and break, if not
            // code continues TODO wrap everything with while?
            if (response.header().type() == MsgType.KARTE) {
                System.out.println(response);
            } else {
                clientSocket.close();
                System.out.println(response);
                System.out.println("Connection closed");
                break;
            }

            // ask for pin, create msg and send TODO probably more elegant to write
            // send()-method here. Problem are the various data types
            // loop for incorrect pin
            do {

                System.out.println("Pin?");
                pin = Integer.parseInt(inFromUser.readLine());
                request = new PinMessage(new MsgHeader(1, MsgType.PIN, "1", "1", System.currentTimeMillis()), pin);
                encodedMsg = codec.encode(request);
                outToServer.write(encodedMsg + "\n");
                outToServer.flush();
                System.out.println("Pin sent:\n" + encodedMsg);
                // increase pinCount to limit to three attempts
                pinCount++;

                // read and decode answer
                stringBuilder.setLength(0);
                responseString = null;
                bodyLine = null;

                // read response
                while ((responseString = inFromServer.readLine()) != null) {
                    if (responseString.isEmpty()) {
                        headerDone = true;
                        break;
                    }
                    stringBuilder.append(responseString).append("\r\n");
                }

                if (!headerDone) {
                    System.out.println("Server disconnected before completing header");
                }

                while ((bodyLine = inFromServer.readLine()) != null && !bodyLine.isEmpty()) {
                    stringBuilder.append("\r\n");
                    stringBuilder.append(bodyLine).append("\r\n");
                }

                response = codec.decode(stringBuilder);

            } while (response.header().type().equals(MsgType.ERROR) && pinCount < MAXPINCOUNT);

            // TODO make sure that pin MAXINCOUNT works
            // if pin is entered wrong 3 times print ErrorMessage and break, if not code
            // continues
            if (pinCount == MAXPINCOUNT) {

                System.out.println(response);
                clientSocket.close();
                System.out.println("Connection closed");
                break;
            } else {
                System.out.println(response);
            }

            // present menu Options, read menuInput from user
            do {

                do {
                    System.err.println("What do you want to do? < balance | withdrawal | quit > ");
                    menuInput = inFromUser.readLine();
                } while (!(menuInput.toLowerCase().equals("balance") || menuInput.toLowerCase().equals("withdrawal")
                        || menuInput.toLowerCase().equals("quit")));

                // switch different menuOptions

                switch (menuInput) {
                    case "balance":

                        request = new KontostandReqMessage(
                                new MsgHeader(1, MsgType.KONTOSTAND_REQ, "1", "1", System.currentTimeMillis()),
                                cardNo);

                        break; // break so menu question is asked again. should return to beginning of do{}

                    case "withdrawal":

                        System.out.println("Amount? (00.00)");
                        double reqAmount = Double.parseDouble(inFromUser.readLine());

                        request = new AbhebenReqMessage(
                                new MsgHeader(1, MsgType.ABHEBEN_REQ, "1", "1", System.currentTimeMillis()),
                                cardNo, reqAmount);

                        // if it is an error the error will be printed and still exit switch but without
                        break;
                    case "quit":
                        request = new QuitReq(new MsgHeader(1, MsgType.QUIT_REQ, "1", "1", System.currentTimeMillis()));
                        break;
                    default:
                        break;
                }

                encodedMsg = codec.encode(request);
                outToServer.write(encodedMsg + "\n");
                outToServer.flush();
                System.out.println("Request sent:\n" + encodedMsg);

                // read and decode answer
                stringBuilder.setLength(0);
                responseString = null;
                bodyLine = null;

                // read response
                while ((responseString = inFromServer.readLine()) != null) {
                    if (responseString.isEmpty()) {
                        headerDone = true;
                        break;
                    }
                    stringBuilder.append(responseString).append("\r\n");
                }

                if (!headerDone) {
                    System.out.println("Server disconnected before completing header");
                }

                while ((bodyLine = inFromServer.readLine()) != null && !bodyLine.isEmpty()) {
                    stringBuilder.append("\r\n");
                    stringBuilder.append(bodyLine).append("\r\n");
                }

                response = codec.decode(stringBuilder);

                System.out.println(response);
            } while (!(menuInput.toLowerCase().equals("quit")));

            // TODO decide if we want to send a ByeMessage or just close socket and delete
            // ByeMessage completely -> less work
            // create byemessage
            request = new ByeMessage(new MsgHeader(1, MsgType.BYE, "1", "1", System.currentTimeMillis()), "");
            encodedMsg = codec.encode(request);
            outToServer.write(encodedMsg + "\n");
            outToServer.flush();
            System.out.println("Bye-Message sent:\n" + encodedMsg);
            clientSocket.close();
            break;

        }
    }

}
