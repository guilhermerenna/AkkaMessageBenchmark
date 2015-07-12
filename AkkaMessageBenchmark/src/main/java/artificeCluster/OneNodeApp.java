package artificeCluster;

import AkkaMessageBenchmark.DBCleaner;
import AkkaMessageBenchmark.DataExtractor;
import AkkaMessageBenchmark.FrontendBenchmark;
import AkkaMessageBenchmark.StatisticsAnalyser;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;

public class OneNodeApp {

    public static void main(String[] args) throws InterruptedException {

        System.err.println("Verificando configurações para o banco no arquivo \"artifice.xml\"...");

        artificeCluster.DataExtractor de = new artificeCluster.DataExtractor("artifice.xml");

        System.err.println("Limpando banco de dados...");

        artificeCluster.DBCleaner dbcleaner = new artificeCluster.DBCleaner(de.getPath(), de.getUsername(), de.getPassword());

        System.err.println("Iniciando simulação em 3..2...1...");

        Thread.sleep(3000);

        // Iniciando 2 backends
        System.err.println("Iniciando Backend 1 em 2551.");
        ArtificeBackendMain.main(new String[]{"2551"});
        System.err.println("Iniciando Backend 2 em 2552.");
        ArtificeBackendMain.main(new String[]{"2552"});

        // Iniciando 1 frontend, e criando criaturas e cactus conforme parametros do arquivo artifice.xml
        System.err.println("Iniciando Frontend em random.");
        String[] params = {"0", de.getCreatureNumber().toString(), de.getCactiNumber().toString()};

        // ArtificeFrontendMain.main dispara exceção caso os parametros estejam incorretos (porta, #criaturas, #cactos)
        try {
            ArtificeFrontendMain.main(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("Cluster up!!");

        System.out.println("Finalizando simulação em 30 segundos...");
        Thread.sleep(15000);
        System.out.println("Finalizando simulação em 15 segundos...");
        Thread.sleep(5000);
        System.out.println("Finalizando simulação em 10 segundos...");
        Thread.sleep(5000);
        System.out.println("Finalizando simulação em 5 segundos...");
        Thread.sleep(5000);
        System.out.println("Finalizando simulação agora!");

        ArtificeFrontendMain.shutdown();

        Thread.sleep(3000);

        artificeCluster.StatisticsAnalyser sa = new artificeCluster.StatisticsAnalyser(de.getPath(), de.getUsername(), de.getPassword(), de.getCreatureNumber(), de.getCactiNumber());

    }
}
