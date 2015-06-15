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

    public static void main(String[] args) throws InterruptedException {

        // Override the configuration of the port
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + 2552).withFallback(
                ConfigFactory.load());

        ActorSystem _System = ActorSystem.create("ArtificeSystem", config);
        // ConfigFactory.load().getConfig("ArtificeRouter"));

        ActorRef backend1 = _System.actorOf(Props.create(Backend.class), "backend1");
        // ActorRef cactus = cactusSystem.actorOf(Props.create(CactusActor.class), "cactus1");

        Thread.sleep(500);

        for(int i=0;i<4;i++) {
            backend1.tell(new SenderMessage(backend1, backend1, "Stimulus values test", System.currentTimeMillis()), backend1);
            Thread.sleep(50);
        }

        Thread.sleep(1000);

        _System.shutdown();
    }
}
