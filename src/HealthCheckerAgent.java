import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HealthCheckerAgent extends Agent {
    Socket server = null;
    String targethost = "35.180.88.76";
    BufferedReader in;
    PrintWriter out;
    int port = 1234;
    long tickInterval = 1000;

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
        addBehaviour(new SendRequest(this, tickInterval));
    }

    public class SendRequest extends TickerBehaviour {
        long currentTick = 0;
        public SendRequest(Agent a, long period) {
            super(a, period);
            currentTick = period;
        }

        @Override
        protected void onTick() {
            Health h =Health.getInstance();
            System.out.println(h.getHealth());
        }
    }
}
