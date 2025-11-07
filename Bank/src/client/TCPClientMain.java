package client;

import java.io.*;
import java.net.*;

import coded.SimpleTextCodec;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import messages.Text;

class TCPClient {

    public static void main(String argv[]) throws Exception {


        String msgString;
        String encodedMsg;
        String encodedResponse;
        Message response;
        Text msg;

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
        msgString = inFromUser.readLine();
        msg = new Text(new MsgHeader(1, MsgType.TEXT, "0", "0"), msgString); // Hardcoded for msgType.TEXT. TODO needs to be changed
        encodedMsg = codec.encode(msg);

        System.out.println("Out to Server:\n" + msg.toString());

        // send encoded String + '\n' to signal end of String
        outToServer.write(encodedMsg + '\n');
        outToServer.flush();

        // recieve and decode response from server
        encodedResponse=inFromServer.readLine();
        response=codec.decode(encodedResponse);

        System.out.println("In from Server:\n" + response.toString());

        clientSocket.close();

    }

    
}
