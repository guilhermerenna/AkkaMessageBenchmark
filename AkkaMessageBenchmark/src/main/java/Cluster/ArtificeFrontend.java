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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//#frontend
public class ArtificeFrontend extends UntypedActor {
    Cluster cluster;
    private List<ActorRef> backends;
    final int nCreatures;
    final int nCacti;
    final int numBackends;
    final int periodo;
    final String name;
    static DataExtractor de;
    int backendsReady;
    boolean simulating;
    boolean isUsingCluster;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // Construtor utilizado em simulações com o AKKA CLUSTER
    public ArtificeFrontend(String name, DataExtractor de) {
        this.cluster =  Cluster.get(context().system());
        this.isUsingCluster = true;
        backends = new ArrayList<ActorRef>();
        this.nCreatures = de.getCreatureNumber();
        this.nCacti= de.getCactiNumber();
        this.de = de;
        this.name = name;
        this.numBackends = de.getBackendNumber();
        this.periodo = de.getPeriod();
        this.backendsReady = 0;
        this.simulating = true;
    }

    // Construtor utilizado em simulações sem o AKKA CLUSTER
    public ArtificeFrontend(DataExtractor de) {
        // cluster =  Cluster.get(context().system());
        this.isUsingCluster = false;
        backends = new ArrayList<ActorRef>();
        this.nCreatures = de.getCreatureNumber();
        this.nCacti= de.getCactiNumber();
        this.de = de;
        this.name = "frontend";
        this.numBackends = de.getBackendNumber();
        this.periodo = de.getPeriod();
        this.backendsReady = 0;
        this.simulating = true;
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
                    log.info(this.name+ ": Enviando requisicao ordem de criacao de "+nCreatures+" criaturas e "+nCacti+" cactos para backend.");
                    ref.tell(new CreationOrder(nCacti, nCreatures, numBackends, periodo, backends), getSelf());
                }

            } else if(message.equals("started")) {
                if(++backendsReady == this.numBackends) {
                    for(ActorRef r : backends) {
                        r.tell("startSimulation", self());
                    }
                    getContext().system().scheduler().scheduleOnce(Duration.create(de.getSimulationDuration(), TimeUnit.MILLISECONDS),getSelf(), "shutdown", getContext().dispatcher(), null);
                    System.err.println("SHUTDOWN AGENDADO");
                    log.info(this.name + ": Agendando shutdown para daqui a "+(de.getSimulationDuration()/1000)+"s...");
                }
            } else if(message.equals("register")) {
                if(simulating) {
                    log.info(this.name + ": membro registrado: " + getSender().toString());
                    getContext().watch(getSender());
                    backends.add(sender());
                    if (backends.size() >= numBackends) {
                        self().tell("start", self());
                    }
                }
            } else if(message.equals("shutdown")) {
                simulating = false;
                log.info(this.name + ": TENTANDO SHUTDOWN: enviando ordem aos backends");
                if(backends.size() > 0) {
                    for(ActorRef ref : backends) {
                        log.info(this.name + ": enviando shutdown para "+ref.toString());
                        ref.tell(message, self());
                    }

                    log.info(this.name + ": Ordens enviadas. Aguardando resposta de " + backends.size()+" backends.");

                    getContext().system().scheduler().scheduleOnce(
                            Duration.create(500, TimeUnit.MILLISECONDS),
                            getSelf(),
                            "shutdown",
                            getContext().system().dispatcher(),
                            null
                    );
                }
            } else if(message.equals("exiting")) {
                backends.remove(sender());
                log.info(this.name + ": backend "+ sender().toString()+" saiu.");

                if(backends.size() == 0) {
                    if(isUsingCluster) log.info(this.name + ": todos os backends finalizaram! Fechando frontend.");
                    else {
                        System.out.println("Coletando estatisticas! ======================================");
                        StatisticsAnalyser sa = new StatisticsAnalyser(this.name, de.getPath(), de.getUsername(), de.getPassword(), de.getCreatureNumber(), de.getCactiNumber(), de.getBackendNumber(), de.getPeriod());
                    }
                    getContext().system().shutdown();
                }
            }
            ///// ---------------------------------- até aqui, VERIFICADO
            if(message.equals("creature created")) {
                log.info(this.name + ": new creature on " + getSender().path());
            }
        } else if (message instanceof ReceiveTimeout) {
            System.err.println(this.name + ": mensagem recebida: Timeout.");
            log.info("Timeout");

        }
        else if(message instanceof  Terminated) {
            log.info(this.name + ": recebi terminated de " + ((Terminated) message).actor().toString());
        }

/*        else if (message instanceof Terminated){
            Terminated t = (Terminated)message;
            //TODO conferir aqui
            if(simulating == true) {
                backends.remove(t.actor());             //elimina o backend da lista de referencias
            } else {
                backends.remove(0);                     //elimina um a um os backends até que todos tenham finalizado, e desliga o cluster
                if(backends.isEmpty()){
                    context().system().shutdown();
                }
            }

        } */
        else {
            System.err.println(this.name + ": mensagem recebida: unhandled.");
            unhandled(message);
        }
    }

}

//#frontend
