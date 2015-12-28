/*
 * This file is part of the FADSE tool.
 * 
 *   Authors: Horia Andrei Calborean {horia.calborean at ulbsibiu.ro}
 *   Copyright (c) 2009-2011
 *   All rights reserved.
 * 
 *   Redistribution and use in source and binary forms, with or without modification,
 *   are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * 
 *   The names of its contributors NOT may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *   ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *   OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package extended.problems.simulators.unimap;

import environment.Individual;
import environment.Objective;
import extended.problems.simulators.SimulatorBase;
import extended.problems.simulators.SimulatorOutputParser;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author Horia Calborean
 */
public class UniMapOutputParser extends SimulatorOutputParser {

    public static final String OBJECTIVE_1 = "Obj1";
    public static final String OBJECTIVE_2 = "Obj2";

    public UniMapOutputParser(SimulatorBase simulator) {
        super(simulator);
        this.defaultDelimiter = "=";
    }

    @Override
    protected LinkedList<String> getRealSimulatorObjective(String objectiveName) {
        return super.getRealSimulatorObjective(objectiveName);
    }

    @Override
    public LinkedList<Objective> getResults(Individual individual) {
        this.file = new File(this.simulator.getSimulatorOutputFile());
        // Process the file and find some objectives => can be found in this.results
        this.processFile(individual);
        // The return object
        LinkedList<Objective> finalResults = new LinkedList<Objective>();

        // Go through all the objectives and copy them to the return-object finalResults
        try {
            for (Objective obj : this.currentObjectives) {
                String key = obj.getName();

                // First handle the complex objectives (the ones that need aditional computation)
                if (key.equals(OBJECTIVE_1)) {
                    double OBJ1 = 0;//TODO get the value
                    obj.setValue(OBJ1);
                } else if (key.equals(OBJECTIVE_2)) {
                    double OBJ2 = 0;//TODO get the value
                    obj.setValue(OBJ2);
                } else {
                    // It is a default/simple objective corresponding to a single line
                    if (this.results.containsKey(key) && this.results.get(key) != null) {
                        obj.setValue(this.results.get(key));
                        System.out.println("Found value for " + key + ": " + this.results.get(key));
                    } else {
                        individual.markAsInfeasibleAndSetBadValuesForObjectives("Objective " + key + " cannot be found (not existent or null): " + this.results);
                        setWorstObjectives(finalResults);
                        break;
                    }
                }

                finalResults.add(obj);
                System.out.println("Final Results after adding " + obj.getValue() + " for " + obj.getName() + ": " + finalResults);
            }
        } catch (Exception ex) {
            System.out.println("Error while calculating Objective: " + ex.getMessage());
            individual.markAsInfeasibleAndSetBadValuesForObjectives("Error calculating objective: " + ex.getMessage());
            setWorstObjectives(finalResults);
        }

        // Check if one of the values if MAX, then set as infeasible
        for (Objective item : finalResults) {
            if (item.getValue() == Double.MAX_VALUE) {
                individual.markAsInfeasibleAndSetBadValuesForObjectives("one of the objectives is Double.MAX_VALUE: " + finalResults);
                setWorstObjectives(finalResults);
                break;
            }
        }
        // If infeasible, then set all values to max.
        if (!individual.isFeasible()) {
            // Set all the objectives to the max available value...
            System.out.println("Individual is infeasible - clear objectives.");
            setWorstObjectives(finalResults);
        }

        System.out.println("I calculated as results: " + finalResults);

        return finalResults;
    }

    @Override
    protected void processLine(String textLine, int lineNumber) {
        super.processLine(textLine, lineNumber);
    }
}
