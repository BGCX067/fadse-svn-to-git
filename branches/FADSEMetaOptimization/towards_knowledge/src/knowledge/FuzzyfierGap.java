/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import knowledge.fuzzyrepresentation.FuzzyParameter;
import knowledge.fuzzyrepresentation.FuzzyRule;
import knowledge.fuzzyrepresentation.LltInterval;

/**
 *
 * @author jahrralf
 */
public class FuzzyfierGap {

    public static void main(String[] args) {
        System.out.println("Hello :) Task of this class: convert intervals to fuzzy rules.");
        System.out.println("");


        ArrayList<FuzzyParameter> paras = new ArrayList<FuzzyParameter>();

        FuzzyParameter para_1 = new FuzzyParameter("n_columns", 4, 31, false);
        paras.add(para_1);

        FuzzyParameter para_2 = new FuzzyParameter("n_layers", 0, 6, true);
        paras.add(para_2);

        FuzzyParameter para_4 = new FuzzyParameter("c_sets", 5, 13, true);
        paras.add(para_4);

        FuzzyParameter para_5 = new FuzzyParameter("c_lines", 0, 7, true);
        paras.add(para_5);

        ArrayList<FuzzyRule> rules = new ArrayList<FuzzyRule>();

        FuzzyRule rule_1 = new FuzzyRule();
        rule_1.addCondition(para_1.getLltInterval(LltInterval.MIN, 20));
        rule_1.addCondition(para_2.getLltInterval(3, LltInterval.MAX));
        rule_1.addCondition(para_4.getLltInterval(8, LltInterval.MAX));
        rule_1.consequence = para_5.getLltInterval(LltInterval.MIN, 4);
        rules.add(rule_1);

        FuzzyRule rule_2 = new FuzzyRule();
        rule_2.addCondition(para_1.getLltInterval(LltInterval.MIN, 20));
        rule_2.addCondition(para_2.getLltInterval(5, LltInterval.MAX));
        rule_2.addCondition(para_4.getLltInterval(11, LltInterval.MAX));
        rule_2.consequence = para_5.getLltInterval(LltInterval.MIN, 1);
        rules.add(rule_2);

        FuzzyRule rule_3 = new FuzzyRule();
        rule_3.addCondition(para_1.getLltInterval(LltInterval.MIN, 20));
        rule_3.addCondition(para_2.getLltInterval(3, LltInterval.MAX));
        rule_3.addCondition(para_4.getLltInterval(7, 8));
        rule_3.consequence = para_5.getLltInterval(1, 4);
        rules.add(rule_3);

        FuzzyRule rule_4 = new FuzzyRule();
        rule_4.addCondition(para_1.getLltInterval(LltInterval.MIN, 8));
        rule_4.addCondition(para_2.getLltInterval(LltInterval.MIN, 3));
        rule_4.addCondition(para_4.getLltInterval(7, 10));
        rule_4.consequence = para_5.getLltInterval(0, 1);
        rules.add(rule_4);

        FuzzyRule rule_5 = new FuzzyRule();
        rule_5.addCondition(para_1.getLltInterval(11, 18));
        rule_5.addCondition(para_2.getLltInterval(3, 5));
        rule_5.addCondition(para_4.getLltInterval(11, LltInterval.MAX));
        rule_5.consequence = para_5.getLltInterval(LltInterval.MIN, 2);
        rules.add(rule_5);

       
        // For Completeness
        
        FuzzyRule correct_para_1 = new FuzzyRule(true);
        correct_para_1.addCondition(para_1.getLltInterval(21, LltInterval.MAX));
        correct_para_1.consequence = para_1.getLltInterval(LltInterval.MIN, 21);
        rules.add(correct_para_1);

        FuzzyRule correct_para_4 = new FuzzyRule(true);
        correct_para_4.addCondition(para_4.getLltInterval(LltInterval.MIN, 7));
        correct_para_4.consequence = para_4.getLltInterval(7, LltInterval.MAX);
        rules.add(correct_para_4);

        FuzzyRule correct_para_5 = new FuzzyRule(true);
        correct_para_5.addCondition(para_5.getLltInterval(4, LltInterval.MAX));
        correct_para_5.consequence = para_5.getLltInterval(LltInterval.MIN, 4);
        rules.add(correct_para_5);
        

        System.out.println("// Block definition (there may be more than one block per file)");
        System.out.println("// " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        System.out.println("FUNCTION_BLOCK gap");
        System.out.println("");

        System.out.println("// Define input variables");
        System.out.println("VAR_INPUT");
        for(FuzzyParameter para : paras) {
            System.out.println("    " + para.getName() + " : REAL;");
        }
        System.out.println("END_VAR");
        System.out.println("");

        System.out.println("// Define output variables");
        System.out.println("VAR_OUTPUT");
        for(FuzzyParameter para : paras) {
            System.out.println("    out" + para.getName() + " : REAL;");
        }
        System.out.println("END_VAR");
        System.out.println("");


        for (FuzzyParameter para : paras) {
            System.out.println(para.getFuzzify());
            System.out.println("");
            System.out.println(para.getDefuzzify());
            System.out.println("");
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

        for(FuzzyParameter para : paras) {
             para.getUncoveredLltIntervals();
            // System.out.println("Uncovered for " + para.getName() + ": " + para.getUncoveredLltInterval());
        }
    }
}


