package ArtificeClusterBroken;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.routing.Router;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by lsi on 09/07/15.
 */
public class FrontendApp {
    Router router;
    static ActorRef frontend;

    public static void main(String[] args) throws InterruptedException {
        // starting 1 frontend node

        final String port = args.length > 0 ? args[0] : "0";

        final Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.parseString(
                "akka.cluster.roles = [frontend]").withFallback(
                ConfigFactory.load("artifice")));

        final ActorSystem system = ActorSystem.create("ClusterSystem", config);


        System.err.println("Sistema iniciado." +
                "Atividades começam assim que 2 backends estiverem no cluster.");

        // Registra novo membro
        Cluster.get(system).registerOnMemberUp(new Runnable() {
            public void run() {
                system.actorOf(Props.create(ArtificeFrontend.class, "frontend", 2, true),
                        "frontend");
            }
        });
    }
}
