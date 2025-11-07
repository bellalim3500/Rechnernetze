package client;

import java.io.*;
import java.net.*;

import coded.SimpleTextCodec;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.request.PinMessage;

class TCPClient {

    public static void main(String argv[]) throws Exception {


        String msgString;
        String encodedMsg;
        String encodedResponse;
        Message response;
        Message request;

        // create codec
        SimpleTextCodec codec = new SimpleTextCodec();

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

        // read message and create Message-Object which then is encoded
        System.out.println("Message?");
        //msgString = inFromUser.readLine();
        int pinInt = Integer.parseInt(inFromUser.readLine());
        
        request = new PinMessage(new MsgHeader(1, MsgType.PIN, "0", "0", System.currentTimeMillis()), pinInt); // Hardcoded for msgType.TEXT. TODO needs to be changed
        System.out.println("Message to decode:\n " + request);

        encodedMsg = codec.encode(request);
        System.out.println("Out to Server (encoded):\n" + encodedMsg);

        // send encoded String + '\n' to signal end of String
        outToServer.write(encodedMsg + "\n");
        outToServer.flush();

        // recieve and decode response from server
        encodedResponse=inFromServer.readLine();
        response=codec.decode(encodedResponse);

        System.out.println("In from Server:\n" + response.toString());

        clientSocket.close();

    }

    
}
