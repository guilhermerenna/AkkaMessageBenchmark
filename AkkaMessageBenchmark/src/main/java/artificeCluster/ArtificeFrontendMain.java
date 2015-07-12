package artificeCluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class ArtificeFrontendMain {
    private static ActorSystem system;
    static int nCreatures = 0;
    static int nCacti = 0;

    public static void main(String[] args) throws IOException {

        if (args.length >= 3) {
            nCreatures = Integer.parseInt(args[1]);
            nCacti = Integer.parseInt(args[2]);
        } else {
            throw new IOException("Parametros insuficientes! Forneça a porta, numero de criaturas e numero de cactos da simulação.");
        }

        final Config config = ConfigFactory.parseString(
                "akka.cluster.roles = [frontend]").withFallback(
                ConfigFactory.load("artificeCluster"));

        ArtificeFrontendMain.system = ActorSystem.create("ClusterSystem", config);
        system.log().info(
                "Factorials will start when 2 backend members in the cluster.");
        //#registerOnUp
        System.err.println("Frontend: registrando...");
        Cluster.get(system).registerOnMemberUp(new Runnable() {
            public void run() {
                System.err.println("Frontend: running!");
                ActorRef frontend = system.actorOf(Props.create(ArtificeFrontend.class, "ArtificeFrontend", ArtificeFrontendMain.nCreatures, ArtificeFrontendMain.nCacti, true),
                        "artificeFrontend");
                System.err.println("Frontend: router registrado!");
            }
        });
        //#registerOnUp

    }

    public static void shutdown() {
        ArtificeFrontendMain.system.shutdown();
    }

}
