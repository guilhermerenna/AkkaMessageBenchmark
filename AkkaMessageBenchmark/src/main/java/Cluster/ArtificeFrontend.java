package Cluster;

import Cluster.Tools.DataExtractor;
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
    Cluster cluster = Cluster.get(context().system());
    private List<ActorRef> backends;
    final int nCreatures;
    final int nCacti;
    final int numBackends;
    final int periodo;
    final String name;
    static DataExtractor de;
    int backendsReady;
    boolean simulating = false;

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public ArtificeFrontend(String name, int nCreatures, int nCacti, DataExtractor de) {
        backends = new ArrayList<ActorRef>();
        this.nCreatures = nCreatures;
        this.nCacti= nCacti;
        this.de = de;
        this.name = name;
        this.numBackends = de.getBackendNumber();
        this.periodo = de.getPeriod();
        this.backendsReady = 0;
    }

    @Override
    public void preStart() {
        System.err.println(this.name + ": Frontend iniciando na URL "+getSelf().toString());
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof String) {
            if(message.equals("start")) {
                simulating = true;
                log.info(this.name + ": A enviar requisicao para " + backends.size() + " backends");
                for(ActorRef ref : backends) {
                    ref.tell(backends, self());
                    log.info(this.name+ ": Enviando requisicao ordem de criacao de "+nCreatures+" criaturas e "+nCacti+" cactos para backend.");
                    ref.tell(new CreationOrder(nCacti, nCreatures, numBackends), getSelf());
                }

            } else if(message.equals("started")) {
                if(++backendsReady == this.numBackends) {
                    for(ActorRef r : backends) {
                        r.tell("startSimulation", self());
                    }
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
                simulating = false;
                log.info(this.name + ": INICIANDO SHUTDOWN: enviando ordem aos backends");
                for(ActorRef ref : backends) {
                    ref.tell(message, self());
                }
                log.info(this.name + ": ordens enviadas, desligando frontend");
                getContext().system().shutdown();

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
            //TODO conferir aqui
            if(simulating == true) {
                backends.remove(t.actor());             //elimina o backend da lista de referencias
            } else {
                backends.remove(0);                     //elimina um a um os backends até que todos tenham finalizado, e desliga o cluster
                if(backends.isEmpty()){
                    context().system().shutdown();
                }
            }

        } else {
            System.err.println(this.name + ": mensagem recebida: unhandled.");
            unhandled(message);
        }
    }

}

//#frontend
