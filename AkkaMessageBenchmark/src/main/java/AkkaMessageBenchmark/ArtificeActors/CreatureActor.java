package AkkaMessageBenchmark.ArtificeActors;

import ArtificeMailbox.ReceiverMessage;
import ArtificeMailbox.SenderMessage;
import Creature.EyeActor;
import Creature.MouthActor;
import Creature.NoseActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;

public class CreatureActor extends ArtificeActor {
    private final ActorRef mouth = getContext().actorOf(Props.create(MouthActor.class).withRouter(new RoundRobinRouter(5)), "mouth");
    private final ActorRef nose = getContext().actorOf(Props.create(NoseActor.class).withRouter(new RoundRobinRouter(5)), "nose");
    private final ActorRef eye = getContext().actorOf(Props.create(EyeActor.class).withRouter(new RoundRobinRouter(5)), "eye");

    public CreatureActor(String nome) {
        super(nome);
    }

    @Override
    public void onReceive(Object arg0) throws Exception {

        // Mensagem a ser recebida
        if (arg0 instanceof SenderMessage) {
            ReceiverMessage msg = new ReceiverMessage(
                    getSender(),
                    getSelf(),
                    ((SenderMessage) arg0).getStimulusValues(),
                    ((SenderMessage) arg0).getSendingTime(),
                    System.currentTimeMillis()
            );
            System.out.println(this.nome + ": ReceiverMessage built!");
            dbActor.tell(msg, getSelf());
        }

//        // APENAS PARA TESTES!! SenderMessage deve ser tratada pela mailbox e convertida para ReceiverMessage
//        else if(arg0 instanceof SenderMessage)  {
//            System.out.println(this.nome+": SenderMessage received: "+((SenderMessage) arg0).toString());
//        }

        else if (arg0 instanceof String) {
            System.out.println(this.nome + ": String message received: " + (String) arg0);

            if (((String) arg0).equals("anycast")) {
                /*getContext().system().scheduler().scheduleOnce(
                        Duration.create(1000, TimeUnit.MILLISECONDS),
                        getSelf(), "tick", getContext().dispatcher(), null);*/

                context().parent().tell(new SenderMessage("Touch from "+this.nome+"!!", System.currentTimeMillis()), getSelf());
                System.out.println(this.nome + ": sending touch stimulus!");

            } else System.out.println(this.nome + ": String recebida: " + (String) arg0);
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
