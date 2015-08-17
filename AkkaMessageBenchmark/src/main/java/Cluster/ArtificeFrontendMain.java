package Cluster;

import Cluster.Tools.DBCleaner;
import Cluster.Tools.DataExtractor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.sql.SQLException;

public class ArtificeFrontendMain {
    private static ActorSystem system;
    static int nCreatures;
    static int nCacti;
    static DataExtractor de = new DataExtractor("artifice.xml");

    public static void main(String[] args) throws IOException {

        int port = 0;

        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
            System.err.println("Iniciando frontend na porta "+port);
        }
        else System.err.println("Iniciando frontend em porta aleatoria.");

        System.err.println("Limpando banco de dados...");

        DBCleaner dbcleaner = new DBCleaner(de.getPath(), de.getUsername(), de.getPassword());

        try {
            dbcleaner.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]").
                        withFallback(ConfigFactory.load("artificeCluster")));

        system = ActorSystem.create("ClusterSystem", config);
        system.log().info(
                "Artifice will start when the minimum backend members number is reached.");
        //#registerOnUp
        System.err.println("Frontend: registrando...");



        Cluster.get(system).registerOnMemberUp(new Runnable() {
            public void run() {
                System.err.println("Frontend: running!");
                ActorRef frontend = system.actorOf(Props.create(ArtificeFrontend.class, "ArtificeFrontend", de.getCreatureNumber(), de.getCactiNumber(), de),
                        "artificeFrontend");
                System.err.println("Frontend: router registrado!");

            }
        });
        //#registerOnUp
    }

}
