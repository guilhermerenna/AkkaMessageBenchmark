package Cluster;

import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import artificeCluster.DataExtractor;
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
    final boolean repeat;
    final String name;
    static DataExtractor de = new DataExtractor("artifice.xml");

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    //ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "artificeBackendRouter");

    public ArtificeFrontend(String name, int nCreatures, int nCacti, boolean repeat) {
        backends = new ArrayList<ActorRef>();
        this.nCreatures = nCreatures;
        this.nCacti= nCacti;
        this.repeat = repeat;
        this.name = name;
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
                    // SENDING CREATURES AND CACTI CREATION REQUESTS
                    for (int n = 1; n <= nCreatures; n++) {
                        log.info(this.name+ ": Enviando requisicao de nova criatura para backend.");
                        ref.tell("new creature", ActorRef.noSender());
                    }
                    for (int n = 1; n <= nCacti; n++) ref.tell("new cactus", ActorRef.noSender());
                }
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(10000, TimeUnit.MILLISECONDS),
                        getSelf(), "shutdown", getContext().dispatcher(), null);
                System.err.println("SHUTDOWN AGENDADO");
                log.info(this.name + "Agendando shutdown para daqui a 20s...");
            } else if(message.equals("register")) {
                log.info(this.name + ": membro registrado");
                getContext().watch(getSender());
                backends.add(sender());
                if(backends.size() == 2) {
                    self().tell("start",self());
                }

            } else if(message.equals("shutdown")) {
                System.err.println("INICIANDO SHUTDOWN.");
                log.info(this.name + ": Recebida requisicao de shutdown. Desligando o sistema...");
                for(ActorRef ref : backends) {
                    ref.tell(message, self());
                }
                //self().tell(PoisonPill.getInstance(), ActorRef.noSender());
                context().system().shutdown();
                runStatsAnalyser();
            }
            ///// ---------------------------------- até aqui, VERIFICADO
            if(message.equals("creature created")) {
                log.info(this.name + ": new creature on " + getSender().path());
            } else if(message.equals("started")) {
                log.info(this.name + ": Ready for clustering: " + getSender().path());
            }
        } else if (message instanceof ReceiveTimeout) {
            System.err.println(this.name + ": mensagem recebida: Timeout.");
            log.info("Timeout");

        } else {
            System.err.println(this.name + ": mensagem recebida: unhandled.");
            unhandled(message);
        }
    }

    private void runStatsAnalyser() {
        artificeCluster.StatisticsAnalyser sa = new artificeCluster.StatisticsAnalyser(de.getPath(), de.getUsername(), de.getPassword(), de.getCreatureNumber(), de.getCactiNumber());
        int total = -1;
        try {
            total = sa.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    }

}

//#frontend
