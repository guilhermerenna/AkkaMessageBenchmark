package Cluster;


import Artifice.Actors.CactusActor;
import Artifice.Actors.CreatureActor;
import Artifice.Actors.DBActor;
import Artifice.Mailbox.RoutedSenderMessage;
import Artifice.Mailbox.SenderMessage;
import Artifice.Mailbox.StampedSenderMessage;
import Cluster.Tools.StatisticsAnalyser;
import Cluster.message.CreationOrder;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RandomRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//#backend
public class ArtificeBackend extends UntypedActor {
    private String name;
    private ArrayList<Routee> internalRoutees;
    private Router internalRouter = null;
    private Router backendRouter = null;
    private String path;
    private String username;
    private String password;
    protected Cluster cluster;
    private int nCreatures;
    private int nCacti;
    private ActorRef dbActor;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public ArtificeBackend(String name, int nCreatures, int nCacti, String path, String username, String password) {
        String hostname = "";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }

        if(!hostname.equals("")) this.name = hostname + "." + name;
        else this.name = name;
        this.path = path;
        this.username = username;
        this.password = password;
        this.internalRoutees = new ArrayList<Routee>();
        this.nCacti = 0;
        this.nCreatures = 0;
        // this.creatures = new ArrayList<Routee>();
        // this.cacti= new ArrayList<Routee>();
        cluster = Cluster.get(context().system());
    }

    public void preStart() throws Exception {
        super.preStart();
        dbActor = getContext().actorOf(Props.create(DBActor.class, this.name + "\\dbactor", this.path, this.username, this.password));

        cluster.subscribe(self(), MemberUp.class);
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof List) {
            List<Routee> backendRouteeList = new ArrayList<Routee>();
            for(ActorRef r : (List<ActorRef>) message) {
                backendRouteeList.add(new ActorRefRoutee(r));
            }
            backendRouter = new Router(new RandomRoutingLogic(), ((List<Routee>) backendRouteeList));
            log.info(this.name + ": backendRouter iniciado! Testando...");
            backendRouter.route("teste", self());

        } else if (message instanceof CreationOrder) {
            CreationOrder order = (CreationOrder)message;

            //TODO: migrar nCacti para getNCacti (encapsular atributos eh boa pratica de programacao ;) )
            nCacti = order.nCacti;
            for(int i=0;i<order.nCacti;i++){
                internalRoutees.add(new ActorRefRoutee(getContext().actorOf(Props.create(CactusActor.class, ("cactus" +i), dbActor).withMailbox("artificeMailbox"), ("cactus" + i))));
            }
            nCreatures = order.nCreature;
            for(int j=0;j<order.nCreature;j++) {
                internalRoutees.add(new ActorRefRoutee(getContext().actorOf(Props.create(CreatureActor.class, ("creature" + j), dbActor).withMailbox("artificeMailbox"), ("creature" + j))));
            }
            internalRouter = new Router(new RandomRoutingLogic(), internalRoutees);
            System.out.println(this.name+": creation order completed");
            getSender().tell("ready", getSelf());

        } else if (message instanceof String) {

            if(message.equals("shutdown")) {
                log.info(this.name + ": ORDEM DE DESLIGAMENTO RECEBIDA");
                context().stop(self()); //não é necessario parar explicitamente os filhos
                //statistcs analyser passado para postStop


            } else if(message.equals("startSimulation")) {
                System.err.println(this.name + ": starting simulation!");
                log.info(this.name + ": starting simulation!");
                for(Routee ref : internalRoutees) {
                    ref.send("startSimulation",self());
                }
            } else {
                //internalRouter.Route(message, );
            }

        } else if(message instanceof SenderMessage) {
            String senderString = getSender().toString().split("#")[0];
            log.info(this.name + ": SenderMessage de " + senderString + ". Encaminhando para roteador de backends...");
            backendRouter.route(new RoutedSenderMessage((SenderMessage) message), self());

        } else if(message instanceof RoutedSenderMessage) {
            log.info("\n\n" + this.name + ": RoutedSenderMessage recebida.");
            SenderMessage sm = ((RoutedSenderMessage) message).getSenderMessage();
            StampedSenderMessage ssm = new StampedSenderMessage(sm.getSender(), sm.getStimulusValues(), sm.getSendingTime(), System.currentTimeMillis());
            internalRouter.route(ssm, getSender());

        } else if(message instanceof MemberUp) {
            System.err.println(this.name + ": recebido um member up.");
            MemberUp upEvent = (MemberUp) message;
            register(upEvent.member());

        } else if(message instanceof CurrentClusterState) {
            System.err.println(this.name + ": ClusterState foi recebido.");

        } else {
            log.info(this.name + ": UNHANDLED MESSAGE RECEBIDA de "+getSender().toString()+": "+message.toString());
            unhandled(message);
        }
    }

    void register(Member member) {
        if (member.hasRole("frontend")) {
            getContext().actorSelection(member.address() + "/user/artificeFrontend").tell(
                    "register", getSelf());
            System.err.println(this.name + ": Enviando requisicao de registro para frontend.");
            log.info(this.name + ": Enviando requisicao de registro para frontend.");
        }
    }

    public void postStop(){

        log.info(this.name + ": Finalizando a simulacao: iniciando STATISTICS ANALYSER");


        StatisticsAnalyser sa = new StatisticsAnalyser(this.name, this.path, this.username, this.password, this.nCreatures, this.nCacti);
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

        log.info(this.name + ": DESLIGANDO BACKEND");
        getContext().system().shutdown();
    }

}
//#backend


