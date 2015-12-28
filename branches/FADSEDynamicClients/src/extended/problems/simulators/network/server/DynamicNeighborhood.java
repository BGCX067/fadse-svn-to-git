/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Radu Chis
 */
public class DynamicNeighborhood implements INeighborhood{

    private String xmlFilepath;
    private LinkedList<Neighbor> neighbors;    

    public DynamicNeighborhood(String _xmlFilePath) throws ParserConfigurationException {
        xmlFilepath = _xmlFilePath;
        neighbors = new LinkedList<Neighbor>();
    }

    private DynamicNeighborhood(LinkedList<Neighbor> _neighbors) {
        neighbors = _neighbors;
    }  

    public int getSize() {
        return neighbors.size();
    }

    public LinkedList<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(LinkedList<Neighbor> neigh) {
        neighbors = neigh;
    }

    /**
     * discards the old instance, and builds a new one
     * USE CAREFULLY (if there are still objects referring the old instance bad things might happen)
     * @return the new list of neighbors
     * @throws ParserConfigurationException
     */
    public LinkedList<Neighbor> getRefreshedNeighbors() throws ParserConfigurationException {       
        LinkedList<Neighbor> goodNeighbors = new LinkedList<Neighbor>();
        for (Neighbor nb : NeighborhoodSingletonWrapper.getInstance().getNeighbors()) {
            try {
                Socket t = new Socket(nb.getIp(),nb.getPort());
                DataInputStream dis = new DataInputStream(t.getInputStream());
                PrintStream ps = new PrintStream(t.getOutputStream());
                ps.println("Hello");
                String str = dis.readLine();
                if (str.equals("Hello")) {
                   goodNeighbors.add(nb);
                } 
                t.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NeighborhoodSingletonWrapper.setInstance( new DynamicNeighborhood(goodNeighbors));
        return NeighborhoodSingletonWrapper.getInstance().getNeighbors();
    }

    public LinkedList<Neighbor> loadNeighbors(String xmlFile) throws ParserConfigurationException {
        LinkedList<Neighbor> ns = new LinkedList<Neighbor>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(xmlFile));
            doc.getDocumentElement().normalize();
            NodeList neighborsList = doc.getElementsByTagName("neighbor");
            for (int i = 0; i < neighborsList.getLength(); i++) {
                Neighbor n = new Neighbor();
                NamedNodeMap atributes = neighborsList.item(i).getAttributes();
                n.setIp(InetAddress.getByName(atributes.getNamedItem("ip").getNodeValue()));
                n.setNumberOfOcupiedSlots(0);
                n.setNumberOfSlots(Integer.parseInt(atributes.getNamedItem("availableSlots").getNodeValue()));
                n.setPort(Integer.parseInt(atributes.getNamedItem("listenPort").getNodeValue()));
                ns.add(n);
            }

        } catch (SAXException ex) {
            Logger.getLogger(DynamicNeighborhood.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DynamicNeighborhood.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ns;
    }

    public Neighbor getByIpAndPort(InetAddress inetAddress, int port) {
        Neighbor neighbor = null;
        for (Neighbor n : neighbors) {
            try {
                if (n.getIp().equals(inetAddress) && n.getPort() == port) {
                    neighbor = n;
                    break;
                }
            } catch (NullPointerException e) {
                if (inetAddress != null) {
                    System.out.println("Searching for: " + inetAddress.getCanonicalHostName() + ":" + port);
                } else {
                    System.out.println("Inet addres is null - happens if there was an error while comunicating with the client");
                }
                System.out.println("Neighbors size = " + neighbors.size());
                e.printStackTrace();
            }
        }
        return neighbor;
    }
}
