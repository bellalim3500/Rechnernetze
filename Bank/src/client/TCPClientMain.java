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

class TCPClient {
    final static int MAXPINCOUNT = 3;
    // final static int MINPIN = 4;
    // final static int MAXPIN = 6;
    // final static int MINCARD = 6;
    // final static int MAXCARD = 20;

    public static void main(String argv[]) throws Exception {

        String userInput;
        String msgString;
        String encodedMsg;
        String responseString;
        String menuInput;
        String cardNo;
        Message response;
        Message request;
        int pin;
        int pinCount = 0;
        SimpleTextCodec codec = new SimpleTextCodec(); // create codec
        BufferedReader inFromUser;
        BufferedWriter outToServer;
        BufferedReader inFromServer;
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

            // send encoded String + '\n' to signal end of String
            outToServer.write(encodedMsg + "\n");
            outToServer.flush();
            System.out.println("CardNo sent:\n" + encodedMsg);

            // read response
            // TODO decode is not working yet, since it's only reading the first line and
            // then stops
            responseString = inFromServer.readLine(); // probably won't work because more than one line
            response = codec.decode(responseString);

            // Check if cardNo-Response is !OK, if so print ErrorMessage and break, if not
            // code continues TODO wrap everything with while?
            if (!response.header().type().equals("KARTE_OK")) {
                System.out.println(response);

                // TODO decide if we want to send a ByeMessage or just close socket and delete
                // ByeMessage completely -> less work

                clientSocket.close();
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
                // TODO

            } while (response.header().type().equals("ERROR") && pinCount < MAXPINCOUNT);

            // if pin is entered wrong 3 times print ErrorMessage and break, if not code
            // continues
            if (pinCount == MAXPINCOUNT) {

                System.out.println(response);
                clientSocket.close();
                System.out.println("Connection closed");
                break;
            }

            // present menu Options, read menuInput from user
            do {
                System.err.println("What do you want to do? < balance | withdrawal | quit > ");
                menuInput = inFromUser.readLine();

                // switch different menuOptions

                switch (menuInput) {
                    case "balance":

                        request = new KontostandReqMessage(
                                new MsgHeader(1, MsgType.PIN, "1", "1", System.currentTimeMillis()),
                                cardNo);
                        encodedMsg = codec.encode(request);
                        outToServer.write(encodedMsg + "\n");
                        outToServer.flush();
                        System.out.println("Request sent:\n" + encodedMsg);

                        // TODO response

                        break; //break so menu question is asked again. should return to beginning of do{}

                    case "withdrawal":

                        System.out.println("Amount? (00.00)");
                        double reqAmount = Double.parseDouble(inFromUser.readLine());

                        request = new AbhebenReqMessage(
                                new MsgHeader(1, MsgType.PIN, "1", "1", System.currentTimeMillis()),
                                cardNo, reqAmount);
                        encodedMsg = codec.encode(request);
                        outToServer.write(encodedMsg + "\n");
                        outToServer.flush();
                        System.out.println("Withdrawal-Request sent:\n" + encodedMsg);

                        // TODO response

                        // TODO switch-logic for ABHEBEN_OK and ERROR Response

                        break;

                    default:
                        break;
                }
            } while (!menuInput.toLowerCase().equals("balance") || !menuInput.toLowerCase().equals("withdrawal"));
            // not checking for "quit" so loop can be exited if menuInput="quit"

            // create byemessage
            request = new ByeMessage(new MsgHeader(1, MsgType.BYE, "1", "1", System.currentTimeMillis()), "");
            encodedMsg = codec.encode(request);
            outToServer.write(encodedMsg + "\n");
            outToServer.flush();
            System.out.println("Bye-Message sent:\n" + encodedMsg);
            clientSocket.close();
            

        }
    }

}
