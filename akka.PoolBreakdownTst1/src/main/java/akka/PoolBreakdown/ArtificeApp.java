package akka.PoolBreakdown;

import Creature.nervousSystem.electricalSignallingSystem.electricalStimulus.Stimulus;
import Creature.nervousSystem.electricalSignallingSystem.electricalStimulus.TouchStimulus;
import Stimuli.TouchStimulusMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RandomRouter;
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

        backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        /*backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        backend1.tell(new TouchStimulusMessage("toquei"),backend1);
//        backend1.tell(new TouchStimulusMessage("toquei"),backend1);*/


    }
}
