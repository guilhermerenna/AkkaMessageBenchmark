package AkkaMessageBenchmark;

import ArtificeMailbox.SenderMessage;
import Stimuli.TouchStimulusMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

//import akka.kernel.Bootable;

public class Frontend {
    // Number of creatures per backend
    public static int nCreatures;
    // Number of cacti per backend
    public static int nCacti;
    // Scheduling delay time before sending the next message
    // public static int scheduling;

    public Frontend(int nCreatures, int nCacti) { //, int scheduling) {
        this.nCreatures = nCreatures;
        this.nCacti = nCacti;
        // this.scheduling = scheduling;
    }

    public static final String path = "localhost:5432/akkaartifice";

    public void run() throws InterruptedException {
        // Override the configuration of the port
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + 2552).withFallback(
                ConfigFactory.load());

        ActorSystem _System = ActorSystem.create("ArtificeSystem", config);
        // ConfigFactory.load().getConfig("ArtificeRouter"));

        ActorRef backend1 = _System.actorOf(Props.create(Backend.class), "backend1");
        // ActorRef cactus = cactusSystem.actorOf(Props.create(CactusActor.class), "cactus1");

        // Sleep 500ms, so all actors will be up
        Thread.sleep(500);

        System.out.println("500ms have been passed. All actors should be starting by now...");


        // CURRENTLY NOT BEING USED: Messages sent by loop
        /* System.out.println("Sending " + nMessages + " messages.");
        for (int i = 0; i < nMessages; i++) {
            backend1.tell(new SenderMessage("Stimulus values test", System.currentTimeMillis()), backend1);
            // Thread.sleep(5);
        } */

        // Sleep 1000ms, so the messages can be sent across the actors
        Thread.sleep(1000);

        _System.shutdown();
    }
}
