package ArtificeClusterBroken;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.routing.RandomRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.List;

/**
 * Created by lsi on 09/07/15.
 */
public class ArtificeBackend extends UntypedActor {
    private ActorRef frontend;
    private String name;

    Cluster cluster = Cluster.get(getContext().system());

    Router router;

    public ArtificeBackend(String name) {
        this.name = name;
    }

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {

        System.out.println(this.name + ": Hello, world!");

        cluster.subscribe(getSelf(), ClusterEvent.MemberUp.class);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(this.name+": I was awakened.");
            e.printStackTrace();
        }
        System.out.println(this.name + ": creating reference for frontend.");
        this.frontend = context().actorFor("akka://ClusterSystem/user/frontend");
        System.out.println(this.name + ": sending join request to frontend");
        this.frontend.tell("backendregistration", getSelf());
        System.out.println(this.name + ": request sent!");
    }
    @Override
    public void onReceive(Object message) {
        if(message instanceof List) {
            router = new Router(new RandomRoutingLogic(), (List<Routee>) message);
        } else if (message instanceof String) {
            System.out.println(this.name+ ": string received: " + message);
        } else {
            unhandled(message);
        }
    }

    private void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    "BackendRegistration", getSelf());
    }

}
// #backend

