package Artifice.Actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by renna on 21/06/15.
 */
public  abstract class ArtificeActor extends UntypedActor {
    protected ActorRef dbActor;
    protected String name;
    private String path;
    private String username;
    private String password;
    protected int messagesSent;
    protected int messagesReceived;

    public ArtificeActor(String name, ActorRef dbActor) {
        this.name = name;

        // Database username, password and path
        this.messagesSent = 0;
        this.messagesReceived = 0;
        this.dbActor = dbActor;

        // System.out.println(this.name + ": creating dbactor with user "+this.username+" at db "+this.path);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();



        /*/getContext().system().scheduler().scheduleOnce(
                Duration.create(500, TimeUnit.MILLISECONDS),
                getSelf(), "tick", getContext().dispatcher(), null);*/
    }

    public void postStop() {
        System.out.println(this.name + " -- Sent:\t" + messagesSent + "\tReceived:\t" + messagesReceived + "\tDelta:\t" + (messagesReceived - messagesSent));
    }
}
