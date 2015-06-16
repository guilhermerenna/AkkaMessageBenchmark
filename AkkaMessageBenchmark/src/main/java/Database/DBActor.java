package Database;

import AkkaMessageBenchmark.ArtificeApp;
import ArtificeMailbox.ReceiverMessage;
import akka.actor.UntypedActor;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Ronaldo Drummond and Bruno Maciel on 10/06/15.
 */

public class DBActor extends UntypedActor {

    private Connection con;
    private Statement stm;
    private String name;
    private static final String username = "lsi";
    private static final String password = "win*c4s4)";

    public DBActor(String name) {
        this.name = name;
    }

    /**
     * Inicialização do DBActor sem nome.
     */
    public DBActor() {
        // Inicialização do DBActor sem nome.
    }

    @Override
    public void preStart() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://"+ ArtificeApp.path,username,password);
            stm = (Statement) con.createStatement();
        } catch(SQLException exception) {
            System.err.println("Erro ao estabelecer conexão com o BD!");
            exception.printStackTrace();
        } catch (ClassNotFoundException exception) {
            System.err.println("Erro ao estabelecer conexão com o BD!");
            exception.printStackTrace();
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof ReceiverMessage) {
            // System.out.println("Recebi estimulo. Gravando no banco...");
            insertDB(generateQuery((ReceiverMessage)o));
        } else {
            System.out.println("DBActor says: Message received is not a ReceiverMessage!");
        }
    }

    private String generateQuery(ReceiverMessage msg) {
        return "insert into MESSAGE(sender,receiver,sendingTime,receivingTime,stimulusValue,dbTime) "
               + "values ('"+msg.getSender().path().toString() +"', "
               + "'"+msg.getReceiver().path().toString() +"', "
                + String.valueOf(msg.getSendingTime()) +", "
                + String.valueOf(msg.getReceivingTime()) +", "
                + "'"+msg.getStimulusValues() +"', "
                + String.valueOf(System.currentTimeMillis())
               + ")";
    }

    private void insertDB (String query) {
        try {
            stm = (Statement) con.createStatement(); //para ele poder ser executado várias vezes sem que feche o resultset
            stm.execute(query);
        } catch(SQLException exception) {
            System.err.println("Erro ao inserir no BD!");
            exception.printStackTrace();
        }
    }

    @Override
    public void postStop() {
        try {
            if (con!= null && !con.isClosed()) {
                con.close();
            }
            if (stm!=null && !stm.isClosed()) {
                stm.close();
            }
        } catch(SQLException exception) {
            System.err.println("Erro ao fechar conexão com o BD!");
            exception.printStackTrace();
        }
    }
}
