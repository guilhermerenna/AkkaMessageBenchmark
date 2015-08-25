package Cluster;

import Cluster.Tools.DataExtractor;
import Cluster.Tools.StatisticsAnalyser;
import Cluster.message.CreationOrder;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//#frontend
public class ArtificeFrontend extends UntypedActor {
    Cluster cluster = Cluster.get(context().system());
    private List<ActorRef> backends;
    final int nCreatures;
    final int nCacti;
    final int numBackends;
    final String name;
    static DataExtractor de;
    int backendIsReady;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public ArtificeFrontend(String name, int nCreatures, int nCacti, DataExtractor de) {
        backends = new ArrayList<ActorRef>();
        this.nCreatures = nCreatures;
        this.nCacti= nCacti;
        this.de = de;
        this.name = name;
        this.numBackends = de.getBackendNumber();
        this.backendIsReady = 0;
    }

    @Override
    public void preStart() {
        System.err.println(this.name + ": Frontend iniciando na URL "+getSelf().toString());
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof String) {
            if(message.equals("start")) {
                log.info(this.name + ": A enviar requisicao para " + backends.size() + " backends");
                for(ActorRef ref : backends) {
                    ref.tell(backends, self());
                    log.info(this.name+ ": Enviando requisicao ordem de criacao de "+nCreatures+" criaturas e "+nCacti+" cactos para backend.");
                    ref.tell(new CreationOrder(nCacti, nCreatures), getSelf());

                }
            } else if(message.equals("started")) {
                if(++backendIsReady == this.numBackends) {
                    sender().tell("startSimulation", self());
                    getContext().system().scheduler().scheduleOnce(Duration.create(de.getSimulationDuration(), TimeUnit.MILLISECONDS),getSelf(), "shutdown", getContext().dispatcher(), null);
                    System.err.println("SHUTDOWN AGENDADO");
                    log.info(this.name + "Agendando shutdown para daqui a "+(de.getSimulationDuration()/1000)+"s...");
                }
            } else if(message.equals("register")) {
                log.info(this.name + ": membro registrado: "+getSender().toString());
                getContext().watch(getSender());
                backends.add(sender());
                if(backends.size() >= numBackends) {
                    self().tell("start",self());
                }

            } else if(message.equals("shutdown")) {
                System.err.println("INICIANDO SHUTDOWN.");
                log.info(this.name + ": Recebida requisicao de shutdown. Desligando o sistema...");
                for(ActorRef ref : backends) {
                    ref.tell(message, self());
                }
                /* Thread one = new Thread() {
                    public void run() {

                    }
                };

                one.start();*/

                StatisticsAnalyser sa = new StatisticsAnalyser(de.getPath(), de.getUsername(), de.getPassword(), de.getCreatureNumber(), de.getCactiNumber());
                int total = -1;
                try {
                    total = sa.run();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (total != -1) System.out.println("TOTAL DE MENSAGENS: " + total);
                else {
                    System.err.println("Erro ao rodar Statistics Analyser.");
                }

                System.exit(0);

                context().system().shutdown();

            }
            ///// ---------------------------------- até aqui, VERIFICADO
            if(message.equals("creature created")) {
                log.info(this.name + ": new creature on " + getSender().path());
            }
        } else if (message instanceof ReceiveTimeout) {
            System.err.println(this.name + ": mensagem recebida: Timeout.");
            log.info("Timeout");

        } else if (message instanceof Terminated){
            Terminated t = (Terminated)message;
            backends.remove(t.actor());             //elimina o backend da lista de referencias

        } else {
            System.err.println(this.name + ": mensagem recebida: unhandled.");
            unhandled(message);
        }
    }

}

//#frontend
