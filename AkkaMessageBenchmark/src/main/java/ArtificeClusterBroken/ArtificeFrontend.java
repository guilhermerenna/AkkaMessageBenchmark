package ArtificeClusterBroken;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.*;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsi on 09/07/15.
 */
public class ArtificeFrontend extends UntypedActor {
    final int nBackends;
    final boolean repeat;
    final String name;
    int countBackends;
    Router randomBackendRouter;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    List<Routee> routees = new ArrayList<Routee>();
    // List<ActorRef> backends = new ArrayList<ActorRef>();

    public ArtificeFrontend(String name, int nBackends, boolean repeat) {
        this.name = name;
        this.nBackends = nBackends;
        this.repeat = repeat;
        this.countBackends = 0;
    }

    @Override
    public void preStart() {
        //todo
        System.err.println("Frontend address: "+this.getSelf().path());
        getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
    }

    @Override
    /**
     * Possible messages received:
     * - Backend join request
     * - New creature request
     * - New cactus request
     */
    public void onReceive(Object message) {
        if (message.equals("backendregistration")) {
            // Checks if all backends have entered
            if(--countBackends == -1) {
                System.out.println("The backend "+getSender().path()+ " has sent a join request, but the cluster is full.\n" +
                        "Request refused.");
                getSender().tell("full", this.getSelf());
            }
            else if((--countBackends) > 0) {

                // If there are more to go, add this and wait for the next ones...
                routees.add(new ActorRefRoutee(getSender()));
                System.out.println(countBackends + " backends have registered. " + (this.nBackends - this.countBackends) + " remaining.");
            } else {
                // If this is the last one, add this...
                routees.add(new ActorRefRoutee(getSender()));

                System.out.println(countBackends + " backends have registered. \nCreating broadcast router.");

                // And setup the broadcast router.
                randomBackendRouter = new Router(new BroadcastRoutingLogic(), routees);

                System.out.println("Broadcast router created.\nSending notifications for the cluster.");
                randomBackendRouter.route(routees, getSelf());
                System.out.println("Activites should be started by now.");

            }
        } else if(message.equals("new creature")){
            // todo
            randomBackendRouter.route(message, getSender());
        } else if(message.equals("new cactus")){
            // todo
            randomBackendRouter.route(message, getSender());
        } else {
            System.err.println(this.name + ": a mensagem recebida não é suportada: \n"+message.toString());
        }
    }
}
