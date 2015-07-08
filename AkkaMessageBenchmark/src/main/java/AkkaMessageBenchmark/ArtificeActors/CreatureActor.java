package AkkaMessageBenchmark.ArtificeActors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;
import Creature.*;
import ArtificeMailbox.*;

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
        if (arg0 instanceof SenderMessage) {
            ReceiverMessage msg = new ReceiverMessage(
                    getSender(),
                    getSelf(),
                    ((SenderMessage) arg0).getStimulusValues(),
                    ((SenderMessage) arg0).getSendingTime(),
                    System.currentTimeMillis()
            );
            System.out.println(this.name + ": ReceiverMessage built!");
            dbActor.tell(msg, getSelf());
        }

//        // APENAS PARA TESTES!! SenderMessage deve ser tratada pela mailbox e convertida para ReceiverMessage
//        else if(arg0 instanceof SenderMessage)  {
//            System.out.println(this.name+": SenderMessage received: "+((SenderMessage) arg0).toString());
//        }

        else if (arg0 instanceof String) {
            System.out.println(this.name + ": String message received: " + (String) arg0);

            if (((String) arg0).equals("anycast")) {
                /*getContext().system().scheduler().scheduleOnce(
                        Duration.create(1000, TimeUnit.MILLISECONDS),
                        getSelf(), "tick", getContext().dispatcher(), null);*/

                context().parent().tell(new SenderMessage("Touch from "+this.name+"!!", System.currentTimeMillis()), getSelf());
                System.out.println(this.name + ": sending touch stimulus!");

            } else System.out.println(this.name + ": String recebida: " + (String) arg0);
        } else {
            throw new Exception("Message type not supported.");
        }
    }

}
