package AkkaMessageBenchmark.ArtificeActors;

import AkkaMessageBenchmark.Frontend;
import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CactusActor utiliza NECESSARIAMENTE a mailbox personalizada ArtificeMailbox.
 */
public class CactusActor extends ArtificeActor {

    /**
     * Construtor
     * @param nome Nome do ator
     */
    public CactusActor(String nome) {
        super(nome);
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
            System.out.println(this.nome+": ReceiverMessage built!");
            dbActor.tell(msg,getSelf());
        } else if(arg0 instanceof String) {
            System.out.println(this.nome + ": String received: "+(String) arg0);

            if(((String) arg0).equals("anycast")) {
                // Re-Scheduler para enviar mensagens "anycast" recursivo
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(1, TimeUnit.NANOSECONDS),
                        getSelf(), "anycast", getContext().dispatcher(), null);

                context().parent().tell(new SenderMessage("Spike from "+this.nome+"!!", System.currentTimeMillis()),getSelf());
                System.out.println(this.nome+": Sending spike!");

            }
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
