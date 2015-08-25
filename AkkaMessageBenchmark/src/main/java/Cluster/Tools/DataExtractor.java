package Cluster.Tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by renna on 08/07/15.
 */
public class DataExtractor {
    private static String path;
    private static String username;
    private static String password;
    private static int creatureNumber;
    private static int cactiNumber;
    private static int backendNumber;
    private static int simulationDuration;
    private static String interfaceRede;
    private static String hosts;

    public DataExtractor(String argPath) {
        try {

            File artificeConf = new File(argPath);

            // InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/artifice.xml");

            /*if(null != is) {
                fileReader = new InputStreamReader(is);
            }*/

            /*InputStream is = this.getClass().getResourceAsStream(argPath);

            if(null != is) {
                artificeConf = new InputStreamReader(is);
            }*/

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(argPath);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Encontrados dados do elemento " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("parameters");

            //for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(0);

            // System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                path = eElement.getElementsByTagName("path").item(0).getTextContent();
                username = eElement.getElementsByTagName("username").item(0).getTextContent();
                password = eElement.getElementsByTagName("password").item(0).getTextContent();
                creatureNumber = Integer.parseInt(eElement.getElementsByTagName("creatures").item(0).getTextContent());
                cactiNumber = Integer.parseInt(eElement.getElementsByTagName("cacti").item(0).getTextContent());
                backendNumber = Integer.parseInt(eElement.getElementsByTagName("backendNumber").item(0).getTextContent());
                simulationDuration = Integer.parseInt(eElement.getElementsByTagName("simulationDuration").item(0).getTextContent());
                interfaceRede = eElement.getElementsByTagName("interfaceRede").item(0).getTextContent();
                hosts = eElement.getElementsByTagName("hosts").item(0).getTextContent();
                System.out.println("Database: " + path);
                System.out.println("Username: " + username);
                System.out.println("Password: " + password.replaceAll(".", "*"));
                System.out.println("# creatures: " + creatureNumber);
                System.out.println("# cacti: " + cactiNumber);
                System.out.println("# backends: " + backendNumber);
                System.out.println("Duration: " + (simulationDuration/1000)+ "s");
                System.out.println("Hosts: \n" + hosts);
            }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Integer getCreatureNumber() {
        return creatureNumber;
    }

    public Integer getCactiNumber() {
        return cactiNumber;
    }

    public String getInterfaceRede() {
        return interfaceRede;
    }

    public String getHosts() {
        return hosts;
    }

    public int getBackendNumber() { return backendNumber; }

    public int getSimulationDuration() { return simulationDuration; }

}