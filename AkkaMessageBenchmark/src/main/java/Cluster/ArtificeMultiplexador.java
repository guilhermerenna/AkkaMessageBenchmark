package Cluster;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URL;

/**
 * Created by lsi on 11/08/15.
 */
public class ArtificeMultiplexador {
    private static Options options;
    /**
     * Initialize command line options
     */
    static {
        Option help = OptionBuilder
                .withDescription("Shows this help")
                .create("help");

        Option frontend = OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription("Starts a new node with the Artifice Backend Service on the specified port.")
                .create("frontend");

        Option backend = OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription("Starts a new node with the Artifice Frontend Service on the specified port.")
                .create("backend");

        options = new Options();
        options.addOption(help);
        options.addOption(backend);
        options.addOption(frontend);

    }

    public static void main(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("help")) {
            //TODO print help file
            System.exit(0);
        } else {
            if (commandLine.hasOption("frontend")) {
                String port = commandLine.getOptionValue("frontend");
                ArtificeFrontendMain.main(new String[]{port});
            } else if (commandLine.hasOption("backend")) {
                String port = commandLine.getOptionValue("backend");
                ArtificeBackendMain.main(new String[]{port});
            } else {
                System.err.println("ERROR! An option is required (frontend | backend | help).");
            }
        }
    }
}
