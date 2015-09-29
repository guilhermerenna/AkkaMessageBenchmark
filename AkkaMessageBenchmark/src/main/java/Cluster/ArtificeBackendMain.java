package Cluster;

import Cluster.Tools.DBCleaner;
import Cluster.Tools.DataExtractor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;

public class ArtificeBackendMain {
    private static DataExtractor de = new DataExtractor("artifice.xml");

    public static void main(String[] args) {

        // Extrai o endereço IP da porta especificada no arquivo artifice.xml
        Enumeration<InetAddress> eInterface = null;
        try {
            NetworkInterface networkinterface = NetworkInterface.getByName(de.getInterfaceRede());
            if(networkinterface == null){
                System.err.println("A interface de rede utilizada não foi reconhecida! Verifique os parametros arquivo artifice.xml.");
                System.exit(1);
            }
            eInterface = networkinterface.getInetAddresses();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        InetAddress currentAddress = null;
        String ip = null;

        while(eInterface.hasMoreElements())
        {
            currentAddress = eInterface.nextElement();
            if(currentAddress instanceof Inet4Address)
            {
                ip = currentAddress.toString();

                // Obtendo endereço ip sem o caracter "/", que vem no inicio da expressao
                ip = ip.split("/")[1];
                System.out.println("interface de rede: " + de.getInterfaceRede() + "\nendereco ip: " + ip);
                break;
            }
        }

        String port = "2551";

        // Override the configuration of the port when specified as program argument
        try {
            port = args[0];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro!\nA porta onde sera executado o frontend deve ser passada como parametro!");
            throw new ArrayIndexOutOfBoundsException("Parametro \"porta\" esta faltando!");
        }

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
                withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ip)).
                withFallback(ConfigFactory.parseString("akka.cluster.seed-nodes=" + de.getHosts())).
          /*TODO: testar eliminar estas linhas*/      withFallback(ConfigFactory.parseString("akka.cluster.min-nr-of-members=" + (de.getBackendNumber()+1))).
                withFallback(ConfigFactory.parseString("akka.cluster.role.backend.min-nr-of-members=" + de.getBackendNumber())).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
                withFallback(ConfigFactory.load("artificeCluster"));

        System.err.println("porta:" + port);
        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        System.err.println("Criando ator em backend.");
        system.actorOf(Props.create(ArtificeBackend.class, ("backend" + port), de.getCreatureNumber(), de.getCactiNumber(), de.getPath(), de.getUsername(), de.getPassword(), de.getPeriod()), "artificeBackend");


        // METRICS LISTENER: Desativado para remover as mensagens de LOG desnecessarias
        // System.err.println("Criando ator metricsListener em backend.");
        // system.actorOf(Props.create(MetricsListener.class), "metricsListener");

        // Thread.yield();
        // system.shutdown();


    }

}
