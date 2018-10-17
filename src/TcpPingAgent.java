import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.StringACLCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpPingAgent extends Agent {
    private static final int TCP_SERVER_PORT = 1234;
    ServerSocket server = null;
    Socket socket = null;
    BufferedReader in;
    PrintWriter out;
    int value = 0;

    //Call to Agent constructor
    public TcpPingAgent(){
        super();
    }

    protected void setup() {
        System.out.println("INFO:\t Agent started.");
        try {
            //Create server socket
            server = new ServerSocket(TCP_SERVER_PORT);
            socket = server.accept();
            System.out.println("INFO:\t Connection to TCP server established.");

            // Create writer and reader to send and receive data
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class TcpCommunicator extends SimpleBehaviour {

        @Override
        public void action() {
            //TODO: Send message and receive message
            String messageContentTest = "" + value;
            System.out.println("Message sent to TCP Server: " + messageContentTest);

            // Get the answer and display it
            String msgReceived;
            msgReceived = callTcpServer(messageContentTest);
            System.out.println("Message received from TCP Server: " + msgReceived);

            // Increment the test variable
            value++;
        }

        @Override
        public boolean done() {
            return false;
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("INFO:\t Agent being taken down.");
        //Close writer and socket
        try {
            out.close();
            in.close();
            socket.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //super.takeDown();
    }

    public String callTcpServer(String messageContent) {
        ACLMessage message;
        String tcpAnswer = "";

        while (!tcpAnswer.equals(messageContent)) {
            String acknowledgement = "";

            while (!acknowledgement.equals("ok")) {
                // Send Message to TCP server
                message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("localhost:" + TCP_SERVER_PORT, AID.ISGUID));
                message.setContent(messageContent);

                // Encode message to send as an ACL Message
                StringACLCodec codec = new StringACLCodec(in, out);
                codec.write(message);
                out.flush();

                // Wait for ACK message
                try
                {
                    acknowledgement = in.readLine().toString();
                    in.reset();
                    System.out.println("ack = " + acknowledgement);
                } catch (IOException e) {
                    //TODO: handle exception
                }

                // Wait for its answer
                try
                {
                    while (!in.ready()) {}
                    tcpAnswer = tcpAnswer + in.readLine().toString();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return tcpAnswer;
    }
}
