package Artifice.Actors;

import Artifice.Mailbox.ReceiverMessage;
import Artifice.Mailbox.SenderMessage;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CactusActor utiliza NECESSARIAMENTE a mailbox personalizada ArtificeMailbox.
 */
public class CactusActor extends ArtificeActor {

    public CactusActor(String name, String path, String username, String password) {
        // Actor name and Database username, password and path
        super(name, path, username, password);
        System.out.println(this.name + " constructor successfully called!");
    }

    /**
     * Evento disparado quando uma mensagem é recebida.
     * @param arg0 Mensagem do tipo OBJETO.
     * @throws Exception
     */
    @Override
    public void onReceive(Object arg0) throws Exception {
        if(arg0 instanceof List) {
            System.out.println("List received!! What do I do with it?!? ");
            // TODO: Implement changeState()
        } else if(arg0 instanceof SenderMessage) {
            ReceiverMessage msg = new ReceiverMessage(
                    getSender(),
                    getSelf(),
                    ((SenderMessage) arg0).getStimulusValues(),
                    ((SenderMessage) arg0).getSendingTime(),
                    System.currentTimeMillis()
            );
            System.out.println(this.name+": ReceiverMessage built!");
            dbActor.tell(msg,getSelf());
        } else if(arg0 instanceof String) {
            if (arg0.equals("startSimulation")) {

                // Scheduler para enviar mensagens "anycast" a cada 50ms
                System.err.println(this.name + "starting simulation!");
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(500, TimeUnit.MILLISECONDS),
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

                    context().parent().tell(new SenderMessage("Spike from " + this.name + "!!", System.currentTimeMillis()), getSelf());
                    System.out.println(this.name + ": Sending spike!");
                    getContext().system().scheduler().scheduleOnce(
                            Duration.create(500, TimeUnit.MILLISECONDS),
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
