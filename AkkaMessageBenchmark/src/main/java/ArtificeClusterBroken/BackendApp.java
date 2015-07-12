package ArtificeClusterBroken;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by lsi on 09/07/15.
 */
public class BackendApp {
    static ActorRef backend;
    public static void main(String[] args) throws InterruptedException {
        // Override the configuration of the port when specified as program argument
        final String port = args.length > 0 ? args[0] : "2552";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
                withFallback(ConfigFactory.load("artifice"));

        System.err.println("Criando actorSystem em backend.");
        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        System.err.println("Criando ator em backend.");
        backend = system.actorOf(Props.create(ArtificeBackend.class, "backend"), ("backend"+port));

        Thread.sleep(5000);

        system.shutdown();
    }

}
