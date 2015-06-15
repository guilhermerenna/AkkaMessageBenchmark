package AkkaMessageBenchmark;

import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;
import Creature.EyeActor;
import Creature.MouthActor;
import Creature.NoseActor;
import Database.ActorDB;
import Stimuli.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class CreatureActor extends UntypedActor {
    private String nome;
    private final ActorRef mouth = getContext().actorOf(Props.create(MouthActor.class).withRouter(new RoundRobinRouter(5)), "mouth");
    private final ActorRef nose = getContext().actorOf(Props.create(NoseActor.class).withRouter(new RoundRobinRouter(5)), "nose");
    private final ActorRef eye = getContext().actorOf(Props.create(EyeActor.class).withRouter(new RoundRobinRouter(5)), "eye");

    private ActorRef dbActor;

    public CreatureActor(String nome) {
        this.nome = nome;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        dbActor = getContext().actorOf(Props.create(ActorDB.class, this.nome+"\\dbactor"));
    }

    @Override
    public void onReceive(Object arg0) throws Exception {

        // Mensagem a ser recebida
        if(arg0 instanceof ReceiverMessage) {
            System.out.println(this.nome+ ": ReceiverMessage received: "+((ReceiverMessage) arg0).toString());
            dbActor.tell(arg0, getSelf());
        }

//        // APENAS PARA TESTES!! SenderMessage deve ser tratada pela mailbox e convertida para ReceiverMessage
//        else if(arg0 instanceof SenderMessage)  {
//            System.out.println(this.nome+": SenderMessage received: "+((SenderMessage) arg0).toString());
//        }

        else if(arg0 instanceof String) {
            System.out.println("String message received: "+(String) arg0);
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
