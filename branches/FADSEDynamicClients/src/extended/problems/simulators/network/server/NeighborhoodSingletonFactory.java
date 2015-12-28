/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators.network.server;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.ini4j.Wini;

/**
 *
 * @author Radu
 */
public class NeighborhoodSingletonFactory {

    private NeighborhoodType type;
    private String path;

    public NeighborhoodSingletonFactory(NeighborhoodType _type, String _path){
        type = _type;
        path = _path;
    }

    public INeighborhood MakeInstance() throws ParserConfigurationException, Exception{
        if(type == null || path == null){
            throw new Exception("Type or path have not been initialized yet!");
        }
        if(type == NeighborhoodType.Static){
            return new Neighborhood(path);
        }
        return new DynamicNeighborhood(path);
    }

      public static void InitializeNeighborhood(File dir, String neighborConfig) throws IOException {
        Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
        String neighborhoodType = ini.get("Neighborhood", "type");
        NeighborhoodType type;
        if (neighborhoodType.compareTo("static") == 0) {
            type = NeighborhoodType.Static;
        } else {
            type = NeighborhoodType.Dynamic;
        }
        NeighborhoodSingletonFactory factory = new NeighborhoodSingletonFactory(type, neighborConfig);
        NeighborhoodSingletonWrapper.setFactory(factory);
    }
}
