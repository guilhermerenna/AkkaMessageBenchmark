package AkkaMessageBenchmark;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 * Created by renna on 21/06/15.
 */
public class Main {
    static ArtificeApp app = new ArtificeApp();
    static StatisticsAnalyser stats = new StatisticsAnalyser();
    static DBCleaner cleaner = new DBCleaner();

    public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException, FileNotFoundException, UnsupportedEncodingException {

        // Cleans the message table
        cleaner.go();

        // Runs the app
        app.go();

        // Pulls the data from the table, computes the start time, latency and processing time, and displays on the screen
        // Saves the file
        stats.go();
    }
}
