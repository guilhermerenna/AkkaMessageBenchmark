package artificeCluster;


import AkkaMessageBenchmark.ArtificeActors.CactusActor;
import AkkaMessageBenchmark.ArtificeActors.CreatureActor;
import ArtificeMailbox.SenderMessage;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

//#backend
public class ArtificeBackend extends UntypedActor {
    private String name;
    private Router backendRouter;
    private int nCreatures;
    private int nCacti;
    private int countCreatures;
    private int countCacti;
    private ArrayList<Routee> random;
    // private ArrayList<Routee> creatures;
    // private ArrayList<Routee> cacti;
    private Router creatureRouter;
    private Router cactusRouter;
    private Router randomRouter = null;
    private String path;
    private String username;
    private String password;

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
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof List) {
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
                    if(countCacti == nCacti) randomRouter = new Router(new RandomRoutingLogic(), random);
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
                    if(countCreatures == nCreatures) randomRouter = new Router(new RandomRoutingLogic(), random);
                    // cactusRouter = new Router(new RandomRoutingLogic(), cacti);
                    getSender().tell("cactus created", getSelf());
                    getSender().tell("started", getSelf());
                } else {
                    System.err.println(this.name + ": new cactus request received, but world is full. Rejected!");
                }
            } else {
                System.err.println(this.name + ": Recebida mensagem: " + message);
            }

            //Codigo do fatorial
//            final Integer n = (Integer) message;
//            Future<BigInteger> f = future(new Callable<BigInteger>() {
//                public BigInteger call() {
//                    return factorial(n);
//                }
//            }, getContext().dispatcher());
//
//            Future<FactorialResult> result = f.map(
//                    new Mapper<BigInteger, FactorialResult>() {
//                        public FactorialResult apply(BigInteger factorial) {
//                            return new FactorialResult(n, factorial);
//                        }
//                    }, getContext().dispatcher());
//
//            pipe(result, getContext().dispatcher()).to(getSender());
        } else if(message instanceof SenderMessage) {
            if(randomRouter != null) {
                randomRouter.route(message, getSender());
            }
        } else {
            System.err.println(this.name + ": UNHANDLED MESSAGE RECEBIDA:"+message.toString());
            unhandled(message);
        }
    }
}
//#backend

