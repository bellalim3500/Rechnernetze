package server;

import java.io.*;
import java.net.*;
import messages.Message;
import messages.MsgHeader;
import messages.MsgType;
import coded.SimpleTextCodec;
import messages.Text;

public class TCPServerMain {
    public static void main(String argv[]) throws Exception {
        String clientEncoded;
        String clientSentence;
        String capitalizedSentence;
        String encodedResponse;
        SimpleTextCodec codec = new SimpleTextCodec();
        Message clientMessage;
        Text response;

        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Warte auf Client...");

        while (true) {

            Socket connectionSocket = welcomeSocket.accept();
            System.out.printf("Client connected: %s%n", connectionSocket.getRemoteSocketAddress());

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
                    connectionSocket.getInputStream()));

            BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(
                    connectionSocket.getOutputStream()));

            clientEncoded = inFromClient.readLine();

            clientMessage = codec.decode(clientEncoded);
            clientSentence = ((Text) clientMessage).text();

            System.out.println("In from client:\n" + clientMessage.toString());

            capitalizedSentence = clientSentence.toUpperCase();
            response = new Text(new MsgHeader(1, MsgType.TEXT, "0", "0"), capitalizedSentence);
            encodedResponse = codec.encode(response);

            System.out.println("Out to Client:\n" + response);

            outToClient.write(encodedResponse +'\n');
            outToClient.flush();

        }
    }

}
