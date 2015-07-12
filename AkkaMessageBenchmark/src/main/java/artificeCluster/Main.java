package artificeCluster;

import AkkaMessageBenchmark.DBCleaner;
import AkkaMessageBenchmark.DataExtractor;
import AkkaMessageBenchmark.FrontendBenchmark;
import AkkaMessageBenchmark.StatisticsAnalyser;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by lsi on 10/07/15.
 */
public class Main {
    public static void main (String[] args) throws IOException {

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

            DataExtractor de = new DataExtractor("artifice.xml");

            DBCleaner cleaner = new DBCleaner(de.getPath(), de.getUsername(), de.getPassword());
            FrontendBenchmark frontend = new FrontendBenchmark(de.getPath(), de.getUsername(), de.getPassword(), nCreatures, nCacti); //, scheduling);

            StatisticsAnalyser stats = new StatisticsAnalyser(de.getPath(), de.getUsername(), de.getPassword(), nCreatures, nCacti); //, scheduling);

            // Cleans the message table
            try {
                cleaner.run();
            } catch (Exception e) {
                exception += "Exception! \n";
                flag = true;
                e.printStackTrace();
                System.err.println("\nErro ao estabelecer conexão! \nOs parametros do banco (usuario, senha, caminho para o banco, tabela, etc.) estão corretos?\nConfira o arquivo \"artifice.xml\".\n");
                throw new IOException("Impossivel rodar simulacao.");
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
}
