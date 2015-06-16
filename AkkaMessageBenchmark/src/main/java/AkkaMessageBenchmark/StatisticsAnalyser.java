package AkkaMessageBenchmark;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Created by lsi on 16/06/15.
 */
public class StatisticsAnalyser {
    private static final String username = "lsi";
    private static final String password = "win*c4s4)";
    private static Connection con;
    private static Statement stm;

    public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException {
        System.out.println("Connecting to database...");
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://" + ArtificeApp.path, username, password);
        stm = (Statement) con.createStatement();

        String query = "SELECT * from message;";
        System.out.println("Executing query...");
        stm = (Statement) con.createStatement(); //para ele poder ser executado várias vezes sem que feche o resultset
        ResultSet rs = stm.executeQuery(query);

        System.out.println("Iterating resultset...");
        rs.next();
        //Retrieve by column name
        int messageid  = rs.getInt("messageid");
        // String sender = rs.getString("sender");
        // String receiver = rs.getString("receiver");
        long sendingtime = rs.getLong("sendingtime");
        long start = sendingtime;
        long receivingtime = rs.getLong("receivingtime");
        long dbtime = rs.getLong("dbtime");
        String stimulus = rs.getString("stimulusvalue");

        //Display values
        System.out.print("#: " + messageid);
        // System.out.print("Sender: ");// + sender);
        // System.out.print(", Receiver: "); //+ receiver);
        System.out.printf("\tSent: %10d", (sendingtime - start));
        System.out.printf("\tLatency: %3d", (receivingtime - sendingtime));
        System.out.printf("\tProcessing : %3d", (dbtime - receivingtime));
        System.out.print("\tStimulus: " + stimulus.toString());
        System.out.println();

        while(rs.next()){
            //Retrieve by column name
            messageid = rs.getInt("messageid");
            // sender = rs.getString("sender");
            // receiver = rs.getString("receiver");
            sendingtime = rs.getLong("sendingtime");
            receivingtime = rs.getLong("receivingtime");
            dbtime = rs.getLong("dbtime");
            stimulus = rs.getString("stimulusvalue");

            // Display values
            System.out.print("#: " + messageid);
            // System.out.print("Sender: " + sender);
            // System.out.print(", Receiver: "); //+ receiver);
            System.out.printf("\tSent: %10d" , (sendingtime-start));
            System.out.printf("\tLatency: %3d", (receivingtime - sendingtime));
            System.out.printf("\tProcessing : %3d", (dbtime - receivingtime));
            System.out.print("\tStimulus: " + stimulus.toString());
            System.out.println();
        }


        if (rs!=null && !rs.isClosed()) {
            rs.close();
        }
        if (stm!=null && !stm.isClosed()) {
            stm.close();
        }
        if (con!= null && !con.isClosed()) {
            con.close();
        }
    }
}