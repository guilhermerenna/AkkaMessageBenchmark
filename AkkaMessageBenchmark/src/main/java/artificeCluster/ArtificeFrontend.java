package artificeCluster;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

//#frontend
public class ArtificeFrontend extends UntypedActor {
    final int nCreatures;
    final int nCacti;
    final boolean repeat;
    final String name;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(),
            "artificeBackendRouter");

    public ArtificeFrontend(String name, int nCreatures, int nCacti, boolean repeat) {
        this.nCreatures = nCreatures;
        this.nCacti= nCacti;
        this.repeat = repeat;
        this.name = name;
    }

    @Override
    public void preStart() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendJobs();
        // getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof String) {
            if(message.equals("creature created")) {
                log.info(this.name + ": new creature on " + getSender().path());
            } else if(message.equals("started")) {
                log.info(this.name + ": Ready for clustering: " + getSender().path());
            }
//            else if(message.equals("shutdown")) {
//                getContext().parent().tell(new PoisonPill(), getSelf());
//            }
        } else if (message instanceof ReceiveTimeout) {
            System.err.println(this.name + ": mensagem recebida: Timeout.");
            log.info("Timeout");
            sendJobs();

        } else {
            System.err.println(this.name + ": mensagem recebida: unhandled.");
            unhandled(message);
        }
    }

    void sendJobs() {
        log.info("Enviando requisicoes de criacao de [{}] criaturas.", nCreatures);
        for (int n = 1; n <= nCreatures; n++) {
            System.err.println(this.name + ": solicitando criacao de criaturas aos backends.");
            backend.tell("new creature", getSelf());
        }
        log.info("Enviando requisicoes de criacao de [{}] cactos.", nCacti);
        for (int n = 1; n <= nCacti; n++) {
            System.err.println(this.name + ": solicitando criacao de cactos aos backends.");
            backend.tell("new cactus", getSelf());
        }
    }

}

//#frontend
