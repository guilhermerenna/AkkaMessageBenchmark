package Artifice.Actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.Router;
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
    protected int periodo;
    protected Router backendRouter;

    public ArtificeActor(String name, String path, String username, String password, int periodo, Router backendRouter) {
        this.name = name;

        // Database username, password and path
        this.path = path;
        this.username = username;
        this.password = password;
        this.messagesSent = 0;
        this.messagesReceived = 0;
        this.periodo = periodo;

        this.backendRouter = backendRouter;

        // System.out.println(this.name + ": creating dbactor with user "+this.username+" at db "+this.path);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        dbActor = getContext().actorOf(Props.create(DBActor.class, this.name + "\\dbactor", this.path, this.username, this.password));

    }

    public void postStop() {
        System.out.println(this.name + " -- Period:\t"+this.periodo+"\tSent:\t" + messagesSent + "\tReceived:\t" + messagesReceived + "\tDelta:\t" + (messagesReceived - messagesSent));
    }
}
