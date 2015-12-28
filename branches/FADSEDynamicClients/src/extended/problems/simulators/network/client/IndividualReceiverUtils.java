/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.ini4j.Wini;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Radu
 */
public class IndividualReceiverUtils {

    private int startPort;
    private int currentPort;
    private static IndividualReceiverUtils _instance;
    private String currentPortFile;

    public static IndividualReceiverUtils GetInstance() {
        if (_instance == null) {
            _instance = new IndividualReceiverUtils();
        }
        return _instance;
    }

    public IndividualReceiverUtils() {
        try {
            String currentdir = System.getProperty("user.dir");
            File dir = new File(currentdir);
            Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
            startPort = ini.get("Client", "listenPort", int.class);

            currentPortFile = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "currentPort.txt";

            // Create file
            FileWriter fstream = new FileWriter(currentPortFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("" + startPort);
            //Close the output stream
            out.flush();
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(IndividualReceiverUtils.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
    }

    public int GetNextPort() {
        BufferedReader in = null;
        BufferedWriter out = null;
        currentPort = startPort;
        try {
            in = new BufferedReader(new FileReader(currentPortFile));
            currentPort = Integer.parseInt(in.readLine());
            in.close();

            FileWriter fstream = new FileWriter(currentPortFile);
            out = new BufferedWriter(fstream);
            out.write(currentPort + 1);
            out.flush();
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(IndividualReceiverUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(IndividualReceiverUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return currentPort;
    }
}
