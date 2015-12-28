/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package environment.rule;

import environment.parameters.Parameter;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Horia
 */
public interface Rule extends Serializable{
    public boolean validate(Parameter[] parameters);
    
}
