package Artifice.Actors;

import Artifice.Mailbox.ReceiverMessage;
import Artifice.Mailbox.SenderMessage;
import Artifice.Mailbox.StampedSenderMessage;
import akka.actor.ActorRef;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CactusActor utiliza NECESSARIAMENTE a mailbox personalizada ArtificeMailbox.
 */
public class CactusActor extends ArtificeActor {

    public CactusActor(String name, ActorRef dbActor) {
        super(name, dbActor);
    }

    /**
     * Evento disparado quando uma mensagem é recebida.
     * @param message Mensagem do tipo OBJETO.
     * @throws Exception
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof List) {
            System.out.println("List received!! What do I do with it?!? ");
            // TODO: Implement changeState()
        } else if (message instanceof StampedSenderMessage) {
            ++messagesReceived;
            ReceiverMessage msg = new ReceiverMessage(
                    ((StampedSenderMessage) message).getSender(),
                    getSelf(),
                    ((StampedSenderMessage) message).getStimulusValues(),
                    ((StampedSenderMessage) message).getSendingTime(),
                    ((StampedSenderMessage) message).getReceivingTime(),
                    System.currentTimeMillis()
            );
            System.out.println(this.name+": ReceiverMessage built!");
            dbActor.tell(msg,getSelf());
        } else if(message instanceof String) {
            if (message.equals("startSimulation")) {
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
                System.out.println(this.name + ": String message received: " + (String) message);

                if (((String) message).equals("anycast")) {
                    messagesSent++;
                    context().parent().tell(new SenderMessage(getSelf(), "Spike from " + this.name + "!!", System.currentTimeMillis()), getSelf());
                    System.out.println(this.name + ": sending spike stimulus from " + getSelf().toString());
                    getContext().system().scheduler().scheduleOnce(
                            Duration.create(30, TimeUnit.MILLISECONDS),
                            getSelf(),
                            "anycast",
                            getContext().system().dispatcher(),
                            null
                    );
                }

            }
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
