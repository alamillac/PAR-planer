/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import java.util.List;

/**
 *
 * @author Domen
 */
public class Operators {
    public String name,value1,value2;
    public List<State> Preconditions, Eliminate, Add;
    
    public Operators(String NAME, String VALUE_1, String VALUE_2, List<State> PREC, List<State> ELI, List<State> ADD)
    {
        name = NAME;
        value1 = VALUE_1;
        value2 = VALUE_2;
        Preconditions = PREC;
        Eliminate = ELI;
        Add = ADD;
    }
    
    public Operators(String NAME, String VALUE_1, List<State> PREC, List<State> ELI, List<State> ADD)
    {
        name = NAME;
        value1 = VALUE_1;
        value2 = null;
        Preconditions = PREC;
        Eliminate = ELI;
        Add = ADD;
    }
    
    
}
