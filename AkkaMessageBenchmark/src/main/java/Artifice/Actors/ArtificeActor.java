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

    public ArtificeActor(String name, String path, String username, String password) {
        this.name = name;

        // Database username, password and path
        this.path = path;
        this.username = username;
        this.password = password;

        // System.out.println(this.name + ": creating dbactor with user "+this.username+" at db "+this.path);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        // Instancia DBActor
        dbActor = getContext().actorOf(Props.create(DBActor.class, this.name + "\\dbactor", this.path, this.username, this.password));

        /*/getContext().system().scheduler().scheduleOnce(
                Duration.create(500, TimeUnit.MILLISECONDS),
                getSelf(), "tick", getContext().dispatcher(), null);*/
    }
}
