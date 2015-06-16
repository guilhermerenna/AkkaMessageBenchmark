package AkkaMessageBenchmark;

import ArtificeMailbox.ReceiverMessage;
import Database.DBActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.List;

/**
 * CactusActor utiliza NECESSARIAMENTE a mailbox personalizada ArtificeMailbox.
 */
public class CactusActor extends UntypedActor {
    private String nome;
    private ActorRef dbActor;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        dbActor = getContext().actorOf(Props.create(DBActor.class, this.nome+"\\dbactor"));
    }

    /**
     * Construtor
     * @param nome Nome do ator
     */
    public CactusActor(String nome) {
        this.nome = nome;
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
        } else if(arg0 instanceof ReceiverMessage) {
            System.out.println(this.nome+": ReceiverMessage received: "+((ReceiverMessage) arg0).toString());
            dbActor.tell(arg0,getSelf());

            //TODO: gravar no banco
        } else if(arg0 instanceof String) {
            System.out.println("String message received: "+(String) arg0);
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
