package Cluster;


import Artifice.Actors.CactusActor;
import Artifice.Actors.CreatureActor;
import Artifice.Mailbox.SenderMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RandomRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//#backend
public class ArtificeBackend extends UntypedActor {
    private String name;
    private Router backendRouter = null;
    private int nCreatures;
    private int nCacti;
    private int countCreatures;
    private int countCacti;
    private ArrayList<Routee> random;
    // private ArrayList<Routee> creatures;
    // private ArrayList<Routee> cacti;
    private Router creatureRouter;
    private Router cactusRouter;
    private Router internalRouter = null;
    private String path;
    private String username;
    private String password;
    protected Cluster cluster;
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

        this.nCreatures = nCreatures;
        this.nCacti = nCacti;
        this.path = path;
        this.username = username;
        this.password = password;
        this.countCreatures = 0;
        this.countCacti = 0;
        this.random = new ArrayList<Routee>();
        // this.creatures = new ArrayList<Routee>();
        // this.cacti= new ArrayList<Routee>();
        cluster = Cluster.get(context().system());
    }

    public void preStart() throws Exception {
        super.preStart();

        cluster.subscribe(self(), MemberUp.class);
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof List) {
            List<ActorRefRoutee> routeeList = new ArrayList<ActorRefRoutee>();
            for(ActorRef r : (List<ActorRef>) message) {
                routeeList.add(new ActorRefRoutee(r));
            }
            backendRouter = new Router(new RandomRoutingLogic(), ((List<Routee>) message));
        } else if (message instanceof String) {
            if(message.equals("new creature")) {
                if((++countCreatures) < nCreatures) {
                    System.err.println("Creating creature " + ("creature" + countCreatures) + " at " + this.path);
                    // creatures.add(new ActorRefRoutee(getContext().actorOf(Props.create(CreatureActor.class, (this.name + ".creature" + this.countCreatures), this.path, this.username, this.password).withMailbox("artificeMailbox"), ("creature" + this.countCreatures))));
                    random.add(new ActorRefRoutee(getContext().actorOf(Props.create(CreatureActor.class, (this.name+".creature"+this.countCreatures), this.path, this.username, this.password).withMailbox("artificeMailbox"), ("creature" + this.countCreatures))));
                    System.out.println(this.name + ": new creature. " + (nCreatures - countCreatures) + " remaining.");
                    getSender().tell("creature created", getSelf());
                } else if(++countCreatures == nCreatures) {
                    // creatureRouter.addRoutee(Props.create(...))
                    // creatures.add(new ActorRefRoutee(getContext().actorOf(Props.create(CreatureActor.class, (this.name+".creature"+this.countCreatures), this.path, this.username, this.password).withMailbox("artificeMailbox"), ("creature" + this.countCreatures))));
                    random.add(new ActorRefRoutee(getContext().actorOf(Props.create(CreatureActor.class, (this.name+".creature"+this.countCreatures), this.path, this.username, this.password).withMailbox("artificeMailbox"), ("creature" + this.countCreatures))));
                    System.out.println(this.name + ": new creature. " + countCreatures + " were created. Starting router...");
                    if(countCacti == nCacti) internalRouter = new Router(new RandomRoutingLogic(), random);
                    // creatureRouter = new Router(new RandomRoutingLogic(), creatures);
                    getSender().tell("creature created", getSelf());
                    getSender().tell("started", getSelf());
                } else {
                    System.err.println(this.name + ": new creature request received, but world is full. Rejected!");
                }
            } else if(message.equals("new cactus")) {
                if((++countCacti) < nCacti) {
                    // System.out.println("Creating cactus "+("cactus"+i)+" at "+this.path);
                    // cacti.add(new ActorRefRoutee(getContext().actorOf(Props.create(CactusActor.class, (this.name+".cactus"+countCacti), path, username, password).withMailbox("artificeMailbox"), ("cactus" + countCacti))));
                    random.add(new ActorRefRoutee(getContext().actorOf(Props.create(CactusActor.class, (this.name+".cactus"+countCacti), path, username, password).withMailbox("artificeMailbox"), ("cactus" + countCacti))));
                    System.out.println(this.name + ": new cactus. " + (nCacti - countCacti) + " remaining.");
                    getSender().tell("cactus created", getSelf());
                } else if(++countCacti == nCacti) {
                    // cactusRouter.addRoutee(Props.create(...))
                    // cacti.add(new ActorRefRoutee(getContext().actorOf(Props.create(CactusActor.class, (this.name+".cactus"+countCacti), path, username, password).withMailbox("artificeMailbox"), ("cactus" + countCacti))));
                    random.add(new ActorRefRoutee(getContext().actorOf(Props.create(CactusActor.class, (this.name + ".cactus" + countCacti), path, username, password).withMailbox("artificeMailbox"), ("cactus" + countCacti))));
                    System.out.println(this.name + ": new cactus. " + countCacti + " were created. Starting router...");
                    if(countCreatures == nCreatures) internalRouter = new Router(new RandomRoutingLogic(), random);
                    // cactusRouter = new Router(new RandomRoutingLogic(), cacti);
                    getSender().tell("cactus created", getSelf());
                    getSender().tell("started", getSelf());
                } else {
                    System.err.println(this.name + ": new cactus request received, but world is full. Rejected!");
                }
            } else if(message.equals("shutdown")) {
                context().system().shutdown();
            } else {
                System.err.println(this.name + ": Recebida mensagem: " + message);
            }

        } else if(message instanceof SenderMessage) {
            System.err.println("\n\n\n"+this.name+ ": Sender message recebida.");
            String senderString = getSender().toString().split("#")[0];
            if(senderString.endsWith("artificeBackend")) {
                System.err.println(this.name+ ": Encaminhando para roteador interno.");
                // internalRouter.route(message, getSender());
            } else {
                System.err.println(this.name+ ": recebido de "+senderString+". Encaminhando para roteador de backends.");
                // backendRouter.route(message, getSender());
            }
        } else if(message instanceof MemberUp) {
            System.err.println(this.name + ": recebido um member up.");
            MemberUp upEvent = (MemberUp) message;
            register(upEvent.member());
        } else if(message instanceof CurrentClusterState) {
            System.err.println(this.name + ": Cluster state foi recebido.");
            CurrentClusterState state = (CurrentClusterState) message;
            int i = 0;
            for (Member member : state.getMembers()) {
                System.out.println("Membro "+i); i++;
                if (member.status().equals(MemberStatus.up())) {
                    register(member);
                    System.err.println(this.name + ": Enviando registro para "+member.toString());
                }
            }
            log.info(this.name + ": lista de membros percorrida. "+i+" membros notificados.");
        } else {
            log.info(this.name + ": UNHANDLED MESSAGE RECEBIDA:"+message.toString());
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
}
//#backend

