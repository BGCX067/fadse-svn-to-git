/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import knowledge.fuzzyrepresentation.FuzzyParameter;
import knowledge.fuzzyrepresentation.FuzzyRule;
import knowledge.fuzzyrepresentation.LltInterval;

/**
 *
 * @author jahrralf
 */
public class Fuzzyfier {

    public static void main(String[] args) {
        System.out.println("Hello :) Task of this class: convert intervals to fuzzy rules.");
        System.out.println("");


        HashMap<String, FuzzyParameter> paras = new HashMap<        String, FuzzyParameter>();

        FuzzyParameter para_0 = new FuzzyParameter("PARA0", 4, 32, false);
        paras.put("para_0", para_0);

        FuzzyParameter para_1 = new FuzzyParameter("PARA1", 4, 31, false);
        paras.put("para_1", para_1);

        FuzzyParameter para_2 = new FuzzyParameter("PARA2", 0, 6, true);
        paras.put("para_2", para_2);

        FuzzyParameter para_3 = new FuzzyParameter("PARA3", 2, 4, false);
        paras.put("para_3", para_3);

        FuzzyParameter para_4 = new FuzzyParameter("PARA4", 5, 13, false);
        paras.put("para_4", para_4);

        FuzzyParameter para_5 = new FuzzyParameter("PARA5", 0, 7, false);
        paras.put("para_5", para_5);

        ArrayList<FuzzyRule> rules = new ArrayList<FuzzyRule>();

        FuzzyRule rule_1 = new FuzzyRule();
        rule_1.addCondition(para_1.getLltInterval(LltInterval.MIN, 14));
        rule_1.addCondition(para_2.getLltInterval(4,5));
        rule_1.consequence = para_5.getLltInterval(LltInterval.MIN, 5);
        rules.add(rule_1);

        FuzzyRule rule_2 = new FuzzyRule();
        rule_2.addCondition(para_1.getLltInterval(LltInterval.MIN, 20));
        rule_2.addCondition(para_2.getLltInterval(5,LltInterval.MAX));
        rule_2.consequence = para_5.getLltInterval(LltInterval.MIN, 5);
        rules.add(rule_2);

      
        // For Completeness
        
         FuzzyRule correct_para_1 = new FuzzyRule(true);
        correct_para_1.addCondition(para_1.getLltInterval(21, LltInterval.MAX));
        correct_para_1.consequence = para_1.getLltInterval(LltInterval.MIN, 21);
        rules.add(correct_para_1);

        FuzzyRule correct_para_2 = new FuzzyRule(true);
        correct_para_2.addCondition(para_2.getLltInterval(LltInterval.MIN, 3));
        correct_para_2.consequence = para_2.getLltInterval(3, LltInterval.MAX);
        rules.add(correct_para_2);

         FuzzyRule correct_para_5 = new FuzzyRule(true);
        correct_para_5.addCondition(para_5.getLltInterval(6, LltInterval.MAX));
        correct_para_5.consequence = para_5.getLltInterval(LltInterval.MIN, 5);
        rules.add(correct_para_5);
        

        System.out.println("// Block definition (there may be more than one block per file)");
        System.out.println("// " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        System.out.println("FUNCTION_BLOCK gap");
        System.out.println("");

        System.out.println("// Define input variables");
        System.out.println("VAR_INPUT");
        for(FuzzyParameter para : paras.values()) {
            if(para.classes.size() > 0) {
                System.out.println("    " + para.getName() + " : REAL;");
            }
        }
        System.out.println("END_VAR");
        System.out.println("");

        System.out.println("// Define output variables");
        System.out.println("VAR_OUTPUT");
        for(FuzzyParameter para : paras.values()) {
            if(para.classes.size() > 0) {
                System.out.println("    OUT" + para.getName() + " : REAL;");
            }
        }
        System.out.println("END_VAR");
        System.out.println("");

        for (FuzzyParameter para : paras.values()) {
            if(para.classes.size() > 0) {
                System.out.println(para.getFuzzify());
                System.out.println("");
                System.out.println(para.getDefuzzify());
                System.out.println("");
            }
        }

        System.out.println("RULEBLOCK No1");
        System.out.println("    // Use 'min' for 'and' (also implicit use 'max'");
        System.out.println("    // for 'or' to fulfill DeMorgan's Law)");
        System.out.println("    AND : MIN;");
        System.out.println("    // Use 'min' activation method");
        System.out.println("    ACT : MIN;");
        System.out.println("    // Use 'max' accumulation method");
        System.out.println("    ACCU : MAX;");

        for (FuzzyRule rule : rules) {
            for(FuzzyRule my_rule : rule.getRelatedRules())
                System.out.println(my_rule.getFuzzyRule());
        }
        System.out.println("END_RULEBLOCK");

        System.out.println("END_FUNCTION_BLOCK");

        for(FuzzyParameter para : paras.values()) {
            if(para.classes.size() > 0) {
                para.getUncoveredLltIntervals();
            }
            // System.out.println("Uncovered for " + para.getName() + ": " + para.getUncoveredLltInterval());
        }
    }
}


