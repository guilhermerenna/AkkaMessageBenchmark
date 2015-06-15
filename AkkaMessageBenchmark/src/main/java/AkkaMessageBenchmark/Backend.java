package AkkaMessageBenchmark;

import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;
import Stimuli.StimulusMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RandomRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsi on 12/06/15.
 */
public class Backend extends UntypedActor {
    Router router; {
        List<Routee> routees = new ArrayList<Routee>();
        ActorRef r = getContext().actorOf(Props.create(CactusActor.class,"cactus1").withMailbox("artificeMailbox"));
        getContext().watch(r);
        routees.add(new ActorRefRoutee(r));
        r = getContext().actorOf(Props.create(CreatureActor.class,"creature1").withMailbox("artificeMailbox"));
        getContext().watch(r);
        routees.add(new ActorRefRoutee(r));
        r = getContext().actorOf(Props.create(CactusActor.class,"cactus2").withMailbox("artificeMailbox"));
        getContext().watch(r);
        routees.add(new ActorRefRoutee(r));
        r = getContext().actorOf(Props.create(CreatureActor.class,"creature2").withMailbox("artificeMailbox"));
        getContext().watch(r);
        routees.add(new ActorRefRoutee(r));

        router = new Router(new RandomRoutingLogic(), routees);
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof StimulusMessage) {
            // System.out.println("Forwarding StimulusMessage...");
            router.route(o, getSender());
        } else if(o instanceof SenderMessage) {
            router.route(o, getSender());
            // System.out.println("Forwarding Stimulus from Sender...");

        } else if (o instanceof String) {
            System.out.println("String message received: " + ((String) o));
        } else if (o instanceof List) {
            // System.out.println("List received. Forwarding...");
            router.route(o, getSender());
        }
    }
}
