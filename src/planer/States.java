/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Domen
 */
public class States {
    public int parent;
    public int n; 
    public Operators operator;
    List<State> precondition, current;
    List<Operators> nextOperations;
    
    public States(Operators op, int Parent, int N,  List<State> prec,  List<State> curr, List<Operators> next)
    {
        operator = op;
        parent = Parent;
        n = N;
        precondition = new ArrayList<>(prec);
        current = new ArrayList<>(curr);
        nextOperations = new ArrayList<>(next);
    }
}
