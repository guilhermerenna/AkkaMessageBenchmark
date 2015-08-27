package Artifice.Actors;

import Artifice.Mailbox.ReceiverMessage;
import akka.actor.UntypedActor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ronaldo Drummond and Bruno Maciel on 10/06/15.
 */

public class DBActor extends UntypedActor {

    private Connection con;
    private Statement stm;
    private String name;
    private String path;
    private String username;
    private String password;

    public DBActor(String name, String path, String username, String password) {
        this.name = name;

        // Database username, password and path
        this.path = path;
        this.username = username;
        this.password = password;

        // System.err.println(this.name+" found:\nPath " + this.path + "Username " + this.username + "\nPassword "+this.password);
    }

    @Override
    public void preStart() {
        try {
            System.err.println(this.name + ": DBActor iniciando...");
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://"+ this.path, this.username,this.password);
            stm = (Statement) con.createStatement();
            System.err.println(this.name + ": DBActor iniciado com sucesso!");
        } catch(SQLException exception) {
            System.err.println(this.name + ": Erro ao estabelecer conexão com o BD: " + exception.getMessage() + "\n");
        } catch (ClassNotFoundException exception) {
            System.err.println(this.name + ": Erro ao estabelecer conexão com o exit BD!");
            exception.printStackTrace();
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof ReceiverMessage) {
            System.out.println(this.name + ": Recebi estimulo. Gravando no banco:\n"+o.toString());
            insertDB(generateQuery((ReceiverMessage) o));
        } else {
            System.out.println(this.name + ": Message received is not a ReceiverMessage!");
        }
    }

    private String generateQuery(ReceiverMessage msg) {
        return "insert into MESSAGE(sender,receiver,sendingTime,receivingTime,stimulusValue,dbTime) "
               + "values ('"+msg.getSender().path() +"', "
               + "'"+msg.getReceiver().path() +"', "
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
            System.err.println(this.name + ": Erro ao inserir no BD!");
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
            System.err.println(this.name + ": Erro ao fechar conexão com o BD!");
            exception.printStackTrace();
        }
    }
}
