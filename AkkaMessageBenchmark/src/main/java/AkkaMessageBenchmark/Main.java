package AkkaMessageBenchmark;

import org.postgresql.util.PSQLException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by renna on 21/06/15.
 */
public class Main {
    // public static final int

    /*public static final int nTotal = 2;
    // Number of creatures per backend
    public static final int nCreatures = nTotal;
    // Number of cacti per backend
    public static final int nCacti = nTotal;*/

    public static void main(String[] args) throws IOException {

        boolean flag = false;

        String result = "";
        String exception = "";
        //for(int j = 6; j >= 0; j--) {
            //int scheduling = (int) Math.pow(2, j);
            for (int i = 0; i < 6; i++) {
                int nTotal = (int) Math.pow(2, i);
                // Number of creatures per backend
                int nCreatures = nTotal;
                // Number of cacti per backend
                int nCacti = nTotal;

                DBCleaner cleaner = new DBCleaner();
                Frontend frontend = new Frontend(nCreatures, nCacti); //, scheduling);
                StatisticsAnalyser stats = new StatisticsAnalyser(nCreatures, nCacti); //, scheduling);

                // Cleans the message table
                try {
                    cleaner.run();
                } catch (InterruptedException e) {
                    exception += "Interrupted Exception\n";
                    flag = true;
                    e.printStackTrace();
                } catch (SQLException e) {
                    exception += "SQL Exception.\n";
                    flag = true;
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    exception += "ClassNotFound Exception\n";
                    flag = true;
                    e.printStackTrace();
                }

                // Runs the frontend
                try {
                    frontend.run();
                } catch (InterruptedException e) {
                    exception += "Interrupted Exception\n";
                    flag = true;
                    e.printStackTrace();
                }

                // Pulls the data from the table, computes the start time, latency and processing time, and displays on the screen
                // Saves the file
                try {
                    result = result + " 2^" + i + ": " + stats.run() + "\n";
                } catch (IOException e) {
                    System.err.println("Erro ao abrir o arquivo.");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    exception += "Interrupted Exception\n";
                    flag = true;
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                    exception += "Interrupted Exception\n";
                    flag = true;
                } catch (ClassNotFoundException e) {
                    exception += "Interrupted Exception\n";
                    flag = true;
                    e.printStackTrace();
                }

                if (flag = true) {
                    exception += "----- 2^" + i + "\n ";
                    flag = false;
                }
            }
            System.out.println("\n\n\n");
            // overall += "\n== \t2^"+j+" scheduling time.\n== ";
            // System.out.println("\t2^"+j+" scheduling time.");

            // overall += "\t\tResultado:\n"+result+"\n";
            System.out.println("\tResultado:\n"+result);

            // overall += "\tExceptions:\n";
            System.out.println("\tExceptions:");

            if(exception.equals("")) {
                // overall += exception + "\n";
                System.out.println(exception);
            } else {
                // overall += "No exceptions were caught.\n";
                System.out.print("No exceptions were caught.");
            }
        }
        /*System.out.println("\n\n\n\n\t\t-- OVERALL RESULTS --" + overall);

        // Defines the name for the output, using the current timestamp
        String outputPath = new SimpleDateFormat("'OVERALL_RESULTS-'yyyyMMddhhmm'.csv'").format(new java.util.Date());

        // Writes to a string the home directory from System (i.e. finds out which user folder it should use)
        outputPath = System.getProperty("user.home")+"/output/" + outputPath;

        // Creates the path based on the string created above
        Path path = Paths.get(outputPath);

        // Creates directory, if not exists
        Files.createDirectories(path.getParent());

        // Creates the pointer to the output file, and opens it
        PrintWriter writer = new PrintWriter(outputPath, "UTF-8");

        writer.write("		-- OVERALL RESULTS --" + overall);
        writer.close();*/
    //}
}
