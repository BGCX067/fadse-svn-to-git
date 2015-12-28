/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators.network.server;

import java.net.InetAddress;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Radu
 */
public interface INeighborhood {   

    public int getSize();

    public LinkedList<Neighbor> getNeighbors();

    public void setNeighbors(LinkedList<Neighbor> neighs);

    public LinkedList<Neighbor> getRefreshedNeighbors() throws ParserConfigurationException;        

    public LinkedList<Neighbor> loadNeighbors(String xmlFile) throws ParserConfigurationException;

    public Neighbor getByIpAndPort(InetAddress inetAddress, int port);
}
