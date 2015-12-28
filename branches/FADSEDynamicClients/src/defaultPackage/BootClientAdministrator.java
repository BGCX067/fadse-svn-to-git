package defaultPackage;

import extended.problems.simulators.network.client.IndividualReceiver;
import extended.problems.simulators.network.server.NeighborCreator;
import extended.problems.simulators.network.server.ServerNeighborCreator;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Wini;
 
/**
 *
 * @author Radu Chis
 */
public class BootClientAdministrator {

    public static void main(String[] args) {
        try {
            Thread createServerNeighborHood = new Thread(ServerNeighborCreator.GetInstance());
            createServerNeighborHood.start();

            Thread createNeighborHood = new Thread(new NeighborCreator());
            createNeighborHood.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
