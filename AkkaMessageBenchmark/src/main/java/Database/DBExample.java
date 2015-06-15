package Database;

import AkkaMessageBenchmark.CactusActor;
import ArtificeMailbox.ReceiverMessage;
import akka.actor.*;

/**
 * Created by lsi on 15/06/15.
 */
public class DBExample {
    static final String dbName = "akkaTest";
    static final String host = "localhost";
    static final String port = "5432";


    public static void main(String[] args) {
        // String path = DBExample.host + ":" + DBExample.port + "/"+DBExample.dbName;
        ActorSystem system = ActorSystem.create("DBActorSystem");
        ActorRef dbActor = system.actorOf(
                Props.create(ActorDB.class));

        ActorRef sender = system.actorOf(Props.create(CactusActor.class), "sender");
        ActorRef receiver = system.actorOf(Props.create(CactusActor.class), "receiver");

        dbActor.tell("lalala",sender);

        //system.shutdown();
    }
}
