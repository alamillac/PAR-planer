/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;

/**
 *
 * @author Domen
 */
public class State {
    public String name,value1,value2;
    
    public State(String NAME, String VALUE_1, String VALUE_2)
    {
        name = NAME;
        value1 = VALUE_1;
        value2 = VALUE_2;
    }
    
    public State(String NAME, String VALUE_1)
    {
        name = NAME;
        value1 = VALUE_1;
        value2 = null;
    }
    
    public State(String NAME)
    {
        name = NAME;
        value1 = null;
        value2 = null;
    }
    
    public State(State st)
    {
        name = st.name;
        value1 = st.value1;
        value2 = st.value2;
    }
    
    public boolean eq (State st)
    {
        if(name.equals(st.name))
        {
            if((value1==null)&&(st.value1==null))
            {
                return true;
            }
            else
            {
                if((name.equals(st.name))&&(value1.equals(st.value1)))
                {
                    if((value2==null)&&(st.value2==null))
                    {
                        return true;
                    }
                    else
                    {
                        if((name.equals(st.name))&&(value1.equals(st.value1))&&(value2.equals(st.value2)))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
}
