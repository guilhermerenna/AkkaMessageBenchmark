package artificeCluster;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.*;

/**
 * Created by renna on 08/07/15.
 */
public class DataExtractor {
    private static String path;
    private static String username;
    private static String password;
    private static int creatureNumber;
    private static int cactiNumber;

    public DataExtractor(String path) {
        try {

            File artificeConf = new File(path);

            // InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/artifice.xml");

            /*if(null != is) {
                fileReader = new InputStreamReader(is);
            }*/

            /*InputStream is = this.getClass().getResourceAsStream(path);

            if(null != is) {
                artificeConf = new InputStreamReader(is);
            }*/

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(path);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Encontrados dados do elemento " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("database");

            //for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(0);

            // System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                DataExtractor.path = eElement.getElementsByTagName("path").item(0).getTextContent();
                DataExtractor.username = eElement.getElementsByTagName("username").item(0).getTextContent();
                DataExtractor.password = eElement.getElementsByTagName("password").item(0).getTextContent();
                DataExtractor.creatureNumber = Integer.parseInt(eElement.getElementsByTagName("creatures").item(0).getTextContent());
                DataExtractor.cactiNumber = Integer.parseInt(eElement.getElementsByTagName("cacti").item(0).getTextContent());
                System.out.println("Username: " + DataExtractor.username);
                System.out.println("Password: " + DataExtractor.password.replaceAll(".", "*"));
                System.out.println("# creatures: " + DataExtractor.creatureNumber);
                System.out.println("# cacti: " + DataExtractor.cactiNumber);
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

    public static Integer getCreatureNumber() {
        return creatureNumber;
    }

    public static Integer getCactiNumber() {
        return cactiNumber;
    }
}
