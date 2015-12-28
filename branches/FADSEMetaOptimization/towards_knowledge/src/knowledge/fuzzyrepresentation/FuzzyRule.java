/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge.fuzzyrepresentation;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 *
 * @author jahrralf
 */
public class FuzzyRule {

    private boolean reorder_denied = false;
    private static int counter = 0;
    private int ID = FuzzyRule.counter++;
    public LinkedHashSet<LltInterval> conditions = new LinkedHashSet<LltInterval>();
    public LltInterval consequence = null;

    public FuzzyRule() {
        new FuzzyRule(false);
    }

    public FuzzyRule(boolean reorder_denied) {
        this.reorder_denied = reorder_denied;
    }

    public void addCondition(LltInterval interval) {
        conditions.add(interval);
    }

    public String getFuzzyRule() {
        String result = "    Rule " + this.ID + " : IF ";

        String conditions_string = "";
        int i = 0;
        for (LltInterval item : conditions) {
            conditions_string += item.parameter.getName() + " IS " + item.getClassName();
            i++;
            if (i < conditions.size()) {
                conditions_string += " AND ";
            }
        }
        result += conditions_string;

        result += " THEN ";
        result += "out" + getConsequence().parameter.getName();
        result += " IS ";
        result += getConsequence().getClassName();
        result += ";";

        return result;
    }

    public ArrayList<FuzzyRule> getRelatedRules() {
        ArrayList<FuzzyRule> result = new ArrayList<FuzzyRule>();

        if (reorder_denied) {
            result.add(this);
        } else {
            LltInterval[] intervals = new LltInterval[this.conditions.size() + 1];
            int index = 0;
            for (LltInterval interval : this.conditions) {
                intervals[index] = interval;
                index++;
            }

            intervals[index] = this.getConsequence();

            for (int i = 0; i < intervals.length; i++) {
                FuzzyRule rule = new FuzzyRule();
                for (int j = 0; j < intervals.length; j++) {

                    if (i == j) {
                        rule.consequence = intervals[j];
                    } else {
                        rule.addCondition(intervals[j]);
                    }

                }
                result.add(rule);
            }


            /* for (int ignore_me = 0; ignore_me < intervals.length; ignore_me++) {
            for (int i = 0; i < intervals.length; i++) {
            FuzzyRule rule = new FuzzyRule();
            for (int j = 0; j < intervals.length; j++) {
            if (j == ignore_me) {
            continue;
            }

            if (i == j) {
            rule.consequence = intervals[j];
            } else {
            rule.addCondition(intervals[j]);
            }
            }
            if(rule.consequence != null)
            result.add(rule);
            }
            } */

        }

        return result;
    }

    /**
     * @return the consequence
     */
    public LltInterval getConsequence() {
        if(this.consequence == null) {
            this.consequence = this.conditions.iterator().next();
            this.conditions.remove(this.consequence);
        }

        return consequence;
    }
}
