package AkkaMessageBenchmark.ArtificeActors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by renna on 21/06/15.
 */
public  abstract class ArtificeActor extends UntypedActor {
    protected String nome;
    protected ActorRef dbActor;

    public ArtificeActor(String nome) {
        this.nome = nome;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        // Instancia DBActor
        dbActor = getContext().actorOf(Props.create(DBActor.class, this.nome + "\\dbactor"));

        /*/getContext().system().scheduler().scheduleOnce(
                Duration.create(500, TimeUnit.MILLISECONDS),
                getSelf(), "tick", getContext().dispatcher(), null);*/

        // Scheduler para enviar mensagens "anycast" a cada 50ms
        getContext().system().scheduler().schedule(
                Duration.Zero(),
                Duration.create(50, TimeUnit.MILLISECONDS),
                getSelf(),
                "anycast",
                getContext().system().dispatcher(),
                null
        );
    }
}
