package AkkaMessageBenchmark;

import AkkaMessageBenchmark.ArtificeActors.CactusActor;
import AkkaMessageBenchmark.ArtificeActors.CreatureActor;
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
        ActorRef r;

        // Creating and adding cactus actors to router
        for(int i=0;i<ArtificeApp.nCacti;i++) {
            r = getContext().actorOf(Props.create(CactusActor.class, "cactus"+i).withMailbox("artificeMailbox"),"cactus"+i);
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }

        // Creating and adding creature actors to router
        for(int i=0;i<ArtificeApp.nCreatures;i++) {
            r = getContext().actorOf(Props.create(CreatureActor.class, "creature"+i).withMailbox("artificeMailbox"),"creature"+i);
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }

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
