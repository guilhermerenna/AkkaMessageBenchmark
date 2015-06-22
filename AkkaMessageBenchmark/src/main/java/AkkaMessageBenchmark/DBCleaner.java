package AkkaMessageBenchmark;

import java.sql.*;

/**
 * Created by lsi on 16/06/15.
 */
public class DBCleaner {
    private static final String username = "lsi";
    private static final String password = "win*c4s4)";
    private static Connection con;
    private static Statement stm;

    public static void run() throws InterruptedException, SQLException, ClassNotFoundException {

        // TODO: Create database and table if not exists

        /*
        * create database akkaartifice;
        *
        * create sequence message_id_seq;
        *
        * create table MESSAGE (
        * 	messageID integer primary key default nextval('message_id_seq'),
        * 	sender varchar(150),
        * 	receiver varchar(150),
        * 	sendingTime bigint,
        * 	receivingTime bigint,
        * 	dbTime bigint,
        * 	stimulusValue varchar(100)
        * );
        *
        * */
        System.out.println("Connecting to database...");
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://" + Frontend.path, username, password);
        stm = (Statement) con.createStatement();

        String query = "DELETE from message;";
        System.out.println("Cleaning message table...");
        stm = (Statement) con.createStatement(); // Persistent connection, so it can be accessed without closing the Resultset
        stm.execute(query);

        if (stm!=null && !stm.isClosed()) {
            stm.close();
        }
        if (con!= null && !con.isClosed()) {
            con.close();
        }
    }
}