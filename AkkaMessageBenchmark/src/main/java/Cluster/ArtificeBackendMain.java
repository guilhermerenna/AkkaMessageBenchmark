package Cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import artificeCluster.ArtificeBackend;
import artificeCluster.DataExtractor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ArtificeBackendMain {
    private static DataExtractor de = new DataExtractor("artifice.xml");

    public static void main(String[] args) {
        // Override the configuration of the port when specified as program argument
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
                withFallback(ConfigFactory.load("artificeCluster"));

        System.err.println("Criando actorSystem em backend na porta " + port);
        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        System.err.println("Criando ator em backend.");
        system.actorOf(Props.create(ArtificeBackend.class, ("backend" + port), 10, 10, de.getPath(), de.getUsername(), de.getPassword()), "artificeBackend");

        // METRICS LISTENER: Desativado para remover as mensagens de LOG desnecessarias
        // System.err.println("Criando ator metricsListener em backend.");
        // system.actorOf(Props.create(MetricsListener.class), "metricsListener");

        // Thread.yield();
        // system.shutdown();


    }

}
