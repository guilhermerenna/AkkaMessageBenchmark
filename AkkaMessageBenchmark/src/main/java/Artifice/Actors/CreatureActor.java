package Artifice.Actors;

import Artifice.Mailbox.ReceiverMessage;
import Artifice.Mailbox.SenderMessage;
import Artifice.Mailbox.StampedSenderMessage;
import Artifice.Creature.EyeActor;
import Artifice.Creature.MouthActor;
import Artifice.Creature.NoseActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CreatureActor extends ArtificeActor {
    private final ActorRef mouth = getContext().actorOf(Props.create(MouthActor.class).withRouter(new RoundRobinRouter(5)), "mouth");
    private final ActorRef nose = getContext().actorOf(Props.create(NoseActor.class).withRouter(new RoundRobinRouter(5)), "nose");
    private final ActorRef eye = getContext().actorOf(Props.create(EyeActor.class).withRouter(new RoundRobinRouter(5)), "eye");

    public CreatureActor(String name, String path, String username, String password) {
        // Actor name and Database username, password and path
        super(name, path, username, password);
    }

    @Override
    public void onReceive(Object arg0) throws Exception {

        // Mensagem a ser recebida
        if (arg0 instanceof StampedSenderMessage) {
            ReceiverMessage msg = new ReceiverMessage(
                    ((StampedSenderMessage) arg0).getSender(),
                    getSelf(),
                    ((StampedSenderMessage) arg0).getStimulusValues(),
                    ((StampedSenderMessage) arg0).getSendingTime(),
                    ((StampedSenderMessage) arg0).getReceivingTime()
            );
            System.out.println(this.name + ": ReceiverMessage built!");
            dbActor.tell(msg, getSelf());
        } else if (arg0 instanceof String) {
            if (arg0.equals("startSimulation")) {

                // Scheduler para enviar mensagens "anycast" a cada 50ms
                System.err.println(this.name + "starting simulation!");
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(10, TimeUnit.MILLISECONDS),
                        getSelf(),
                        "anycast",
                        getContext().system().dispatcher(),
                        null
                );
            } else {
                System.out.println(this.name + ": String message received: " + (String) arg0);

                if (((String) arg0).equals("anycast")) {
                    /*getContext().system().scheduler().scheduleOnce(
                            Duration.create(1000, TimeUnit.MILLISECONDS),
                            getSelf(), "tick", getContext().dispatcher(), null);*/

                    context().parent().tell(new SenderMessage(getSelf(),"Touch from " + this.name + "!!", System.currentTimeMillis()), getSelf());
                    System.out.println(this.name + ": sending touch stimulus from " + getSelf().toString());

                    getContext().system().scheduler().scheduleOnce(
                            Duration.create(10, TimeUnit.MILLISECONDS),
                            getSelf(),
                            "anycast",
                            getContext().system().dispatcher(),
                            null
                    );

                } else System.out.println(this.name + ": String recebida: " + (String) arg0);
            }
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
