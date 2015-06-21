package AkkaMessageBenchmark.ArtificeActors;

import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;

import java.util.List;

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
            System.out.println(this.nome + ": String message received: "+(String) arg0);

            if(((String) arg0).equals("anycast")) {
                /*getContext().system().scheduler().scheduleOnce(
                        Duration.create(1000, TimeUnit.MILLISECONDS),
                        getSelf(), "tick", getContext().dispatcher(), null);*/

                context().parent().tell(new SenderMessage("Spike from "+this.nome+"!!", System.currentTimeMillis()),getSelf());
                System.out.println(this.nome+": Sending spike!");

            }
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
