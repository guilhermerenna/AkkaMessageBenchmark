package AkkaMessageBenchmark;

import ArtificeMailbox.SenderMessage;
import Stimuli.TouchStimulusMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

//import akka.kernel.Bootable;

public class ArtificeApp {
    // Number of messages sent over the whole system
    private static final int nMessages = 20;
    // Number of creatures per backend
    public static final int nCreatures = 10;
    // Number of cacti per backend
    public static final int nCacti = 10;

    public static final String path = "localhost:5432/akkaartifice";

    public void go() throws InterruptedException {
        // Override the configuration of the port
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + 2552).withFallback(
                ConfigFactory.load());

        ActorSystem _System = ActorSystem.create("ArtificeSystem", config);
        // ConfigFactory.load().getConfig("ArtificeRouter"));

        ActorRef backend1 = _System.actorOf(Props.create(Backend.class), "backend1");
        // ActorRef cactus = cactusSystem.actorOf(Props.create(CactusActor.class), "cactus1");

        Thread.sleep(500);

        System.out.println("Sending " + nMessages + " messages.");

        /* for (int i = 0; i < nMessages; i++) {
            backend1.tell(new SenderMessage("Stimulus values test", System.currentTimeMillis()), backend1);
            // Thread.sleep(5);
        } */

        Thread.sleep(1000);

        _System.shutdown();
    }
}
