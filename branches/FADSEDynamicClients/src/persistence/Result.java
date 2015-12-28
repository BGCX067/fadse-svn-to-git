/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import environment.Individual;
import environment.Objective;
import environment.document.InputDocument;
import environment.parameters.Parameter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andrei
 */
public class Result {

    /**TODO do something about the synchronized. Is it really necessary?
     * Save a result in the database
     * @param simulator
     * @param ind
     */
    public synchronized static void insertResult(InputDocument doc, Individual ind, String fileContents) {
//        System.out.println("************************ PERSISTENCE: insertResult");

        String simulatorName = doc.getSimulatorName();
        if (doc.getSimulatorParameter("realSimulator") != null) {
            simulatorName = doc.getSimulatorParameter("realSimulator");
        }

        try {
            String sql_statement =
                    "INSERT INTO TBL_SIMULATION "
                    + "(SIMULATOR_NAME, PARAMETER_STRING, PARAMETER_STRING_HASH, OUTPUT_FILE, FEASIBLE ) values ("
                    + "'" + simulatorName + "', "
                    + "'" + createParameterString(ind) + "',"
                    + "MD5('" + createParameterString(ind) + "'),"
                    + "COMPRESS('" + fileContents.replace("'", "''") +  "'), "
                    + (ind.isFeasible()?1:0)
                    + ")";

            // Connect
            DatabaseConnector.getInstance().connect();

            int simulation_id = DatabaseConnector.getInstance().executeUpdate(sql_statement);

            for (Objective o : ind.getEnvironment().getInputDocument().getObjectives().values()) {
                sql_statement =
                        "INSERT INTO TBL_RESULT "
                        + "(SIMULATION_ID, NAME, VALUE) values ("
                        + simulation_id + ", "
                        + "'" + o.getName() + "', "
                        + o.getValue()
                        + ")";

                DatabaseConnector.getInstance().executeUpdate(sql_statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Disconnect
                DatabaseConnector.getInstance().disconnect();
            } catch (Exception e) {
                System.out.println("Exception while closing DB-connection: " + e.getMessage());
            }
        }
    }

    /** Formats parameters of an individual as a String */
    public static String createParameterString(Individual ind) {
        String paramString = "";

        List<String> lst = new ArrayList<String>();

        // Add benchmark as parameter
        lst.add("benchmark=" + ind.getBenchmark());

        // Add all the other parameters
        for (Parameter p : ind.getParameters()) {
            lst.add(p.getName() + "=" + p.getValue());
        }

        Collections.sort(lst);
        String pre = "";
        for (String s : lst) {
            paramString += pre + s;
            pre = "|";
        }

        return paramString;
    }

    /**TODO do something about the synchronized. Is it really necessary?
     * Gets a list of objectives for a specific simulation.
     * @param simulator
     * @param ind
     * @return the array with found objectives. Empty if none
     */
    public synchronized static LinkedList<Objective> getObjectives(InputDocument doc, Individual ind) {
//        System.out.println("************************ PERSISTENCE: getObjectives");
        LinkedList<Objective> objectives = new LinkedList<Objective>();
        List<Objective> indObjectives = ind.getObjectives();

        String parameterString = createParameterString(ind);
        String sql_statement =
                " SELECT TR.id, TR.name, TR.value FROM TBL_SIMULATION as TS INNER JOIN TBL_RESULT AS TR "
                + " ON TR.simulation_id = TS.id "
                + " WHERE TS.PARAMETER_STRING like '" + parameterString + "' AND "
                + " TS.SIMULATOR_NAME like '" + doc.getSimulatorName() + "'";

        try {
            // Connect
            DatabaseConnector.getInstance().connect();

            ResultSet results = DatabaseConnector.getInstance().executeQuery(sql_statement);
            while (results.next()) {
                int id = results.getInt(1);
                String name = results.getString(2);
                String value = results.getString(3);

                for (Map.Entry entry : doc.getObjectives().entrySet()) {
                    //System.out.println(id + "\t\t" + o.getName());
                    Objective o = (Objective) entry.getValue();
                    if (o.getName().equals(name)) {
                        try {
                            o.setValue(Double.parseDouble(value));
                            objectives.add(o);
                        } catch (NumberFormatException nex) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Disconnect
                DatabaseConnector.getInstance().disconnect();
            } catch (Exception e) {
                System.out.println("Exception while closing DB-connection: " + e.getMessage());
            }
        }

//        System.out.println("OBJ:" + objectives);

        return objectives;
    }

    /** TODO do something about the synchronized. Is it really necessary?
     * Gets the output file saved for a specific simulation
     * @param simulator
     * @param ind
     * @return the array with found objectives. Empty if none
     */
    public synchronized static String getTextResuls(InputDocument doc, Individual ind) {
//        System.out.println("************************ PERSISTENCE: getTextResuls");
        String fileTextContent = null;

        try {
            String parameterString = createParameterString(ind);

            String sql_statement =
                    " SELECT UNCOMPRESS(output_file) FROM TBL_SIMULATION AS TS"
                    + " WHERE TS.PARAMETER_STRING_HASH = MD5('" + parameterString + "') AND "
                    + " TS.SIMULATOR_NAME like '" + doc.getSimulatorName() + "'";

            // Connect
            DatabaseConnector.getInstance().connect();

            ResultSet results = DatabaseConnector.getInstance().executeQuery(sql_statement);

            if (results.next()) {
                fileTextContent = results.getString(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Disconnect
                DatabaseConnector.getInstance().disconnect();
            } catch (Exception e) {
                System.out.println("Exception while closing DB-connection: " + e.getMessage());
            }
        }
        if (fileTextContent != null && fileTextContent.equals("")) {
            fileTextContent = null;
        }
        return fileTextContent;
    }
/**
 *
 * @param ind
 * @return  return value &lt; 0 not in the database
 *      return value =0 infeasible
 *      return value &gt; 0 feasible
 */
    public synchronized static int isFeasible(Individual ind) {
        int feasible = -1;

        try {
            String parameterString = createParameterString(ind);

            String sql_statement =
                    " SELECT feasible FROM TBL_SIMULATION AS TS"
                    + " WHERE TS.PARAMETER_STRING_HASH = MD5('" + parameterString + "') AND "
                    + " TS.SIMULATOR_NAME like '" + ind.getEnvironment().getInputDocument().getSimulatorName() + "'";

            // Connect
            DatabaseConnector.getInstance().connect();

            ResultSet results = DatabaseConnector.getInstance().executeQuery(sql_statement);

            if (results.next()) {
                feasible = results.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Disconnect
                DatabaseConnector.getInstance().disconnect();
            } catch (Exception e) {
                System.out.println("Exception while closing DB-connection: " + e.getMessage());
            }
        }
        return feasible;
    }
}
