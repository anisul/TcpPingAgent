import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TcpPingAgent extends Agent {
    Socket server = null;
    String targethost = "35.180.88.76";
    BufferedReader in;
    PrintWriter out;
    int port = 1234;

    protected void setup() {
        //Take host and port from argument
        //Object[] args = getArguments();
        //targethost = (String) args[0];
        //port = Integer.parseInt((String) args[1]);

        //Define agent
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Smith");
        sd.setName(getName());
        sd.addOntologies("SmithAgent");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.err.println(getLocalName()
                    + " registration with DF did not succeed. Reason: "
                    + e.getMessage());
            doDelete();
        }
        addBehaviour(new SendRequest());
    }

    public class SendRequest extends OneShotBehaviour {
        @Override
        public void action() {
            try {
                //Open socket to TCP server
                System.out.println("INFO:\t Opening socket to "+ targethost +":"+ port);
                server = new Socket(targethost, port);
                System.out.println("INFO:\t Successfully opened socket to "+ targethost +":"+ port);

                //write on the output stream (count of element in fibonacci series)
                out = new PrintWriter(server.getOutputStream(), true);
                out.println(Integer.parseInt("105"));
                out.flush();
                System.out.println("INFO:\t Successfully communicated with TCP server at "+ targethost +":"+ port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}