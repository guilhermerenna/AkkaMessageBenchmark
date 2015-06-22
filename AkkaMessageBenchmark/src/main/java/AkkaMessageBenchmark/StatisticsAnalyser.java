package AkkaMessageBenchmark;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static int totalMessages = 0;

    // Number of creatures per backend
    public static int nCreatures;
    // Number of cacti per backend
    public static int nCacti;
    // Scheduling delay time before sending the next message
    // public static int scheduling;

    public StatisticsAnalyser(int nCreatures, int nCacti) { //, int scheduling) {
        this.nCreatures = nCreatures;
        this.nCacti = nCacti;
        // this.scheduling = scheduling;
    }

    public static int run() throws InterruptedException, SQLException, ClassNotFoundException, IOException {
        System.out.println("Connecting to database...");
        Class.forName("org.postgresql.Driver");
        con = DriverManager.getConnection("jdbc:postgresql://" + Frontend.path, username, password);
        stm = (Statement) con.createStatement();

        String query = "SELECT * from message ORDER BY sendingtime;";
        System.out.println("Executing query...");
        stm = (Statement) con.createStatement(); //para ele poder ser executado várias vezes sem que feche o resultset
        ResultSet rs = stm.executeQuery(query);

        // Defines the name for the output, using the current timestamp
        String outputPath = new SimpleDateFormat("'output-age"+nCreatures+"cac"+nCacti+"-'yyyyMMddhhmm'.csv'").format(new java.util.Date());

        // Writes to a string the home directory from System (i.e. finds out which user folder it should use)
        outputPath = System.getProperty("user.home")+"/output/" + outputPath;

        // Creates the path based on the string created above
        Path path = Paths.get(outputPath);

        // Creates directory, if not exists
        Files.createDirectories(path.getParent());

        // Creates the pointer to the output file, and opens it
        PrintWriter writer = new PrintWriter(outputPath, "UTF-8");

        // Sets up the output format
        String outputDisplay = "#: %5d \tSent: %10d\t Sender: %s\tReceiver: %s\tLatency: %3d \tProcessing : %3d \tStimulus: %s\n";
        String headerFile = "Id, Sent, Sender, Receiver, Latency, Processing, Stimulus\n";
        String outputFile = "%5d, %10d, %s, %s, %3d, %3d, %s\n";

        // Saves header to file...
        writer.printf(headerFile);

        System.out.println("Iterating resultset...");
        rs.next();

        //Retrieve by column name
        int messageid  = rs.getInt("messageid");
        String sender = rs.getString("sender").split("/")[5];
        String receiver = rs.getString("receiver").split("/")[5];
        long sendingtime = rs.getLong("sendingtime");
        long start = sendingtime;
        long receivingtime = rs.getLong("receivingtime");
        long dbtime = rs.getLong("dbtime");
        String stimulus = rs.getString("stimulusvalue");

        // Processes first element, in order to get the first timestamp.
        // Display values on the screen
        System.out.printf(
                // format string:
                outputDisplay,

                // Id, Sent, Latency, Processing, Stimulus:
                messageid,
                (sendingtime - start),
                sender,
                receiver,
                (receivingtime - sendingtime),
                (dbtime - receivingtime),
                stimulus.toString()
        );

        totalMessages++;

        // Saves data to file...
        writer.printf(
                // format string:
                outputFile,

                // Id, Sent, Latency, Processing, Stimulus:
                messageid,
                (sendingtime - start),
                sender,
                receiver,
                (receivingtime - sendingtime),
                (dbtime - receivingtime),
                stimulus.toString());


        // Processes the remaining elements, and computes timestamps based on first timestamp
        while(rs.next()){
            //Retrieve by column name
            messageid = rs.getInt("messageid");

            sender = rs.getString("sender").split("/")[5];
            receiver = rs.getString("receiver").split("/")[5];
            sendingtime = rs.getLong("sendingtime");
            receivingtime = rs.getLong("receivingtime");
            dbtime = rs.getLong("dbtime");
            stimulus = rs.getString("stimulusvalue");

            // Display values on the screen
            System.out.printf(
                    // format string:
                    outputDisplay,

                    // Id, Sent, Latency, Processing, Stimulus:
                    messageid,
                    (sendingtime - start),
                    sender,
                    receiver,
                    (receivingtime - sendingtime),
                    (dbtime - receivingtime),
                    stimulus.toString()
            );

            // Saves data to file...
            writer.printf(
                    // format string:
                    outputFile,

                    // Id, Sent, Latency, Processing, Stimulus:
                    messageid,
                    (sendingtime - start),
                    sender,
                    receiver,
                    (receivingtime - sendingtime),
                    (dbtime - receivingtime),
                    stimulus.toString());
            totalMessages++;
        }

        writer.close();

        System.out.println("\n\nData saved to file " + outputPath);
        // System.out.println("Time: " + (new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())).toString());

        System.out.println(totalMessages + " messages sent.");

        if (rs!=null && !rs.isClosed()) {
            rs.close();
        }
        if (stm!=null && !stm.isClosed()) {
            stm.close();
        }
        if (con!= null && !con.isClosed()) {
            con.close();
        }

        return totalMessages;
    }
}