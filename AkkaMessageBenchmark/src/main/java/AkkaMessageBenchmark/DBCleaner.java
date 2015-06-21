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

    public static void go() throws InterruptedException, SQLException, ClassNotFoundException {
        System.out.println("Connecting to database...");
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://" + ArtificeApp.path, username, password);
        stm = (Statement) con.createStatement();

        String query = "DELETE from message;";
        System.out.println("Cleaning message table...");
        stm = (Statement) con.createStatement(); //para ele poder ser executado várias vezes sem que feche o resultset
        stm.execute(query);

        if (stm!=null && !stm.isClosed()) {
            stm.close();
        }
        if (con!= null && !con.isClosed()) {
            con.close();
        }
    }
}