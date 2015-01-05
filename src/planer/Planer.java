/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Domen
 */
public class Planer {
    
    public static int table_size, ind = 0, n = 0, deep = 0;
    public static List<State> initialState, goalState;
    public static List<Box> blocks;
    public static List<Operators> operator, unknown;
    public static List<States> ok, to_check;
    public static PrintWriter writer;
    public static boolean solution = false;
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        set_operators();
        String text_info = read_from_file("C:/planer/input.txt");
        get_info_from_text(text_info);
        
        for(State st : initialState) // elements on table in start position 
        {
            if("ONTABLE".equals(st.name))
            {
                n++;
            }
        }
        
        String fileName = "C:/planer/infoLog.txt";
        
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Planer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        writer.println("Depth:" + Integer.toString(deep));
        
        ArrayList<Operators> op = get_next_operators((ArrayList<State>) initialState, new ArrayList<>()); // get next opperators for first node 
        States first = new States(null, 0, n, new ArrayList<>(),initialState,  op);
        
        ok = new ArrayList<>();
        to_check = new ArrayList<>();
        ok.add(first);
        deep++;
        to_check.addAll(get_new_states(ok.get(0), ind, deep));
        ind++;
        
        while(!solution)
        {
            check();
            int a= 0;
            deep++;
            
            while(ind<ok.size())
            {
                to_check.addAll(get_new_states(ok.get(ind), ind, deep));
                ind++;
            }
            
            if(a==2)
            {
                break;
            }
                
        }
        
        writer.close();
    }
    
    public static void check()
    {
        int i = to_check.size()-1;
        while(i!=-1)
        {
            if(states_equal((ArrayList<State>) to_check.get(i).current, (ArrayList<State>) to_check.get(i).precondition))
            {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                printSolution(to_check.get(i));
                solution = true;
            }
            
            ok.add(to_check.get(i));
            to_check.remove(i);
                
            i--;
        }
       
    }
    
    public static ArrayList<States> get_new_states(States st, int parent, int depth)
    {
        States tmp = null;
        ArrayList<State> prec = new ArrayList<>();
        ArrayList<State> curr = new ArrayList<>();
        ArrayList<States> tmp_list = new ArrayList<>();
        ArrayList<Operators> op_list = new ArrayList<>();
        int current_table_size = 0;
        
        for(Operators opp : st.nextOperations)
        {
            writer.println("---------------------------------------------------------------");
            writer.println();
            writer.println("\n\rDepth:" + Integer.toString(depth));
            printOperator(opp);
            current_table_size = st.n;
            
            if(get_preconditions(opp, current_table_size)!=null)//we can return null list if block x is smaller than block y
            {
                
                prec = get_preconditions(opp, current_table_size);
                curr = get_current_from_parent(st.current, st.precondition); // everytime new because we chech regression and if is annything true we remove it from the list 
                
                if(opp.name.equals("PUTDOWN"))
                {
                   current_table_size++;
                }
                
                if(regression(opp, prec, curr))
                {

                    tmp = new States(opp, parent, current_table_size, prec, curr,  get_next_operators(curr, prec));

                    tmp_list.add(tmp);
                    System.out.println("true: ADD NEW");

                }
                else
                {
                    writer.println("                                                                    FALSE");
                }
            }
        }
        
        return tmp_list;  
    }
    
    public static boolean  regression(Operators opp, ArrayList<State> precondition, ArrayList<State> current) // we chech is state is ok
    {
        check_add_list(opp, current);
        if(!check_Del_list(opp, current)){
            System.out.println("FALSE");
            return false; 
        }
        
        if(!checkImpossible(precondition, current)){
            return false; 
        }
        
        for(States st : ok)
        {
            List<State> newList = new ArrayList<State>(st.precondition);
            newList.addAll(st.current);
        
            if(states_equal2(precondition, current, newList)) // if state is equal we return false
            {
                writer.println("Two equal states");
                return false; 
            }
        }
        
        return true;
    }
   
    public static boolean checkImpossible(ArrayList<State> precondition, ArrayList<State> current)
    {
        for(State cr : current)
        {
            for(State prec : precondition)
            {
                if(prec.name.equals("EMPTYHAND")&&cr.name.equals("EMPTYHAND"))
                {
                    writer.println("Double EMPTYHAND ->  FALSE");
                    System.out.println("Double emptyhand");
                    return false;
                }
                if(prec.name.equals("HOLDING")&&cr.name.equals("HOLDING"))
                {
                    writer.println("Double HOLDING ->  FALSE");
                    System.out.println("Can't holding two things");
                    return false;
                }
                if(prec.name.equals("EMPTYHAND")&&cr.name.equals("HOLDING"))
                {
                    writer.println("Can't be EMPTYHAND and HOLDING something");
                    System.out.println("Can't be EMPTYHAND and HOLDING something");
                    return false;
                }
                if(prec.name.equals("HOLDING")&&cr.name.equals("ON")&&prec.value1.equals(cr.value2))
                {
                    writer.println("Can't HOLDING block when somenthing is on block");
                    System.out.println("Can't holding something when somenthing is on block ");
                    return false;
                }
                
                if(prec.name.equals("ON")&&cr.name.equals("ON")&&prec.value2.equals(cr.value2))
                {
                    writer.println("Can't have two different blocks on the same ");
                    System.out.println("Can't have two different blocks on the same ");
                    return false;
                }
            }
        }
        return true; 
    }
    
    public static boolean states_equal2(List<State> precondition, List<State> current, List<State> ok_state)
    {
        List<State> newList = new ArrayList<State>(precondition);
        newList.addAll(current);
        
        boolean match = false;
        for(State cr : newList)
        {
            for(State goal : ok_state)
            {
                if(cr.eq(goal)){match = true;}    
            }
            
            if(match == false) // if one state is not equal in the final state means that is not equal
            {
                return false;
            }else{
                match = false;
            }
        }
        return true; 
    }
    
     public static boolean states_equal(List<State> precondition, List<State> current)
    {
        List<State> newList = new ArrayList<State>(precondition);
        boolean ok = true;
        
        for(State st : current) // we make sure that we dont have duplicats in the current and precondition 
        {
            for (State tm : newList)
            {
                if(tm.eq(st)){ok = false; break;}
            }
            
            if(ok)
            {
                newList.add(st);
            }
            ok = true;
        }
        //newList.addAll(current);
        
        if(deep==4)
        {
            int a = 2;
        }
        boolean match = false;
        for(State cr : newList)
        {
            for(State goal : goalState)
            {
                if(cr.eq(goal)){match = true;}    
            }
            
            if(match == false) // if one state is not equal in the final state means that is not equal
            {
                return false;
            }else{
                match = false;
            }
        }
        
        return true; 
    }
    
    public static boolean check_Del_list(Operators opp, ArrayList<State> current)
    {
        
        for(State st : current)
        {
            if("PICKUP".equals(opp.name)){
                   if((st.name.equals("HOLDING"))&&(st.value1.equals(opp.value1)))
                    {
                        writer.println("HOLDING(" + st.value1 +") opperator delet list ->  FALSE");
                        System.out.println("state HOLDING is in operator del list "); 
                        return false;
                    }
            }
            if("PUTDOWN".equals(opp.name))
            {
                 if((st.name.equals("ONTABLE"))&&(st.value1.equals(opp.value1)))
                    {
                        writer.println("ONTABLE(" + st.value1 +") opperator delet list ->  FALSE");
                        System.out.println("state ontable is in operator del list  "); 
                        return false;
                    }
                    if(st.name.equals("EMPTYHAND"))
                    {
                        writer.println("EMPTYHAND opperator delet list ->  FALSE");
                        System.out.println("state emptyhand is in operator del list  "); 
                        return false;
                    }
            }
            if("UNSTACK".equals(opp.name))
            {
                 if((st.name.equals("HOLDING"))&&(st.value1.equals(opp.value1)))
                {
                    writer.println("HOLDING(" + st.value1 +") opperator delet list ->  FALSE");
                    System.out.println("state HOLDING x is in operator del list "); 
                    return false;
                }
                if((st.name.equals("CLEAR"))&&(st.value1.equals(opp.value2)))
                {
                    writer.println("CLEAR(" + st.value1 +") opperator delet list ->  FALSE");
                    System.out.println(" state CLEAR Y is in operator del list" );
                    return false;
                }
                
                
            }
            if("STACK".equals(opp.name)){
                 if((st.name.equals("ON"))&&(st.value1.equals(opp.value1))&&(st.value2.equals(opp.value2)))
                {
                    writer.println("ON(" + st.value1 +"),("+ st.value2 +") opperator delet list ->  FALSE");
                    System.out.println("state ON x,y  is in operator del list "); 
                    return false;
                }
                if(st.name.equals("EMPTYHAND"))
                {
                    writer.println("EMPTYHAND opperator delet list ->  FALSE");
                    System.out.println(" state emptyhand is in operator del list" );
                    return false;
                }
            }
            
        }
        return true; 
    }
    
    public static void check_add_list(Operators opp, ArrayList<State> current)
    {
        List<Integer> index_to_remove = new ArrayList<>();
        
        for(int i = 0; i < current.size(); i++)
        {
            
            if("PICKUP".equals(opp.name)){
                if((current.get(i).name.equals("HOLDING"))&&(current.get(i).value1.equals(opp.value1)))
                {
                    writer.println("HOLDING(" + current.get(i).value1 +")->  TRUE");
                    //System.out.println("HOLDING(" + current.get(i).value1 +")->  TRUE"); 
                    index_to_remove.add(i);
                }
            }
            if("PUTDOWN".equals(opp.name)){
                if((current.get(i).name.equals("ONTABLE"))&&(current.get(i).value1.equals(opp.value1)))
                {
                    //System.out.println("ONTABLE on add list, true"); 
                    writer.println("ONTABLE(" + current.get(i).value1 +")->  TRUE");
                    index_to_remove.add(i);
                }
                if(current.get(i).name.equals("EMPTYHAND"))
                {
                    writer.println("EMPTYHAND -> TRUE"); 
                    index_to_remove.add(i);
                }
            }
            if("UNSTACK".equals(opp.name)){
                if((current.get(i).name.equals("HOLDING"))&&(current.get(i).value1.equals(opp.value1)))
                {
                    writer.println("HOLDING(" + current.get(i).value1 +")->  TRUE");
                    index_to_remove.add(i);
                }
                if((current.get(i).name.equals("CLEAR"))&&(current.get(i).value1.equals(opp.value2)))
                {
                    writer.println("CLEAR(" + current.get(i).value1 +")->  TRUE"); //clear Y
                    index_to_remove.add(i);
                }
            }
            if("STACK".equals(opp.name)){
                if((current.get(i).name.equals("ON"))&&(current.get(i).value1.equals(opp.value1))&&(current.get(i).value2.equals(opp.value2)))
               {
                   //System.out.println("ON x y on add list, true"); 
                   writer.println("ON(" + current.get(i).value1 +"),("+current.get(i).value2 +") ->  TRUE");
                   index_to_remove.add(i);
               }
               if(current.get(i).name.equals("EMPTYHAND"))
               {
                   writer.println("EMPTYHAND -> TRUE"); 
                   index_to_remove.add(i);
               }
            }
        }
        
        if(index_to_remove.size()>1)
        {
        Collections.sort(index_to_remove ,Collections.reverseOrder()); // we remove highers index first
        for(int i = 0; i < index_to_remove.size(); i++)
        {
            current.remove((int)index_to_remove.get(i));
        }
        }else{
            current.remove((int)index_to_remove.get(0));
        }
        
        
        
    }
    
    public static ArrayList<State> get_current_from_parent(List<State> current, List<State> prec)
    {
        writer.println("Current state:");
        Boolean ok = true;
        
        List<State> tmp = new ArrayList<>();
        for (State current1 : current)
        {
            printStete(current1);
            tmp.add(new State(current1));
        }
        for (State prec1 : prec)
        {
            printStete(prec1);
            for (State tm : tmp)
            {
                if(tm.eq(prec1)){ok = false; break;}
            }
            
            if(ok)
            {
                tmp.add(new State(prec1));
            }
            ok = true;
        }
        writer.println();
        return (ArrayList<State>) tmp;
    }
    
    public static ArrayList<State> get_preconditions(Operators op, int n) // we get new preconditions from operators 
    {
        ArrayList<State> tmp = new ArrayList<>();
        
        writer.println();
        writer.println("Preconditions:");
        
        if("PUTDOWN".equals(op.name))
        {
            if(n<table_size)
            {
                tmp.add(new State("HOLDING", op.value1));
                writer.println("HOLDING("+op.value1+")");
                n++; // we add one to curent table ocupation 
            }
            else
            {
                //write to file that we can't put more blocks on table
                tmp = null;
            }
        }
        
        if("STACK".equals(op.name))
        {
           if(get_block_size(op.value1)<=get_block_size(op.value2))
           {
               tmp.add(new State("HOLDING", op.value1));
               writer.println("HOLDING("+op.value1+")");
               tmp.add(new State("CLEAR", op.value2));
               writer.println("CLEAR("+op.value2+")");
           }
           else
           {
               //block x is bigger than y 
               writer.write("Block "+op.value1+" is bigger than "+ op.value2);
               tmp = null;
           }
            
        }
            
        if("PICKUP".equals(op.name))
        {
            tmp.add(new State("ONTABLE", op.value1));
            tmp.add(new State("CLEAR", op.value1));
            tmp.add(new State("EMPTYHAND"));
            
            writer.println("ONTABLE("+op.value1+")");
            writer.println("CLEAR("+op.value1+")");
            writer.println("EMPTYHAND");
            
        }
        
        if("UNSTACK".equals(op.name))
        {
            tmp.add(new State("ON", op.value1, op.value2));
            writer.println("ON("+op.value1+","+op.value2+")");
            tmp.add(new State("CLEAR", op.value1));
            writer.println("CLEAR("+op.value1+")");
            tmp.add(new State("EMPTYHAND"));
            writer.println("EMPTYHAND");
        }
            
        writer.println();
        
        return tmp;
    }
    
    public static ArrayList<Operators> get_next_operators(ArrayList<State> current, ArrayList<State> prec)
    {
        List<Operators> tmp = new ArrayList<>();
        Operators a;
        unknown = new ArrayList<>();
        for (State current1 : current)
        {
            a = get_operator_from_add_list(current1);
            if(a!= null)
            {
            tmp.add(a);
            }
        }
        
        for (State prec1 : prec)
        {
            a = get_operator_from_add_list(prec1);
            if(a!= null)
            {
            tmp.add(a);
            }
        }
        
        for(Operators op : unknown) // we add all the operators where don't know both blocks 
        {
            tmp.add(op);
        }
        
        return (ArrayList<Operators>) tmp;
    }
    
    public static Operators get_operator_from_add_list(State current1)
    {
            if("ONTABLE".equals(current1.name))
            {
                 return (new Operators("PUTDOWN", current1.value1, null, null, null));
            }
            
            if("CLEAR".equals(current1.name))
            {
                for(Box bx : blocks)
                {
                    if(!current1.value1.equals(bx.name)) //we don't know which is first block so we add all of the possibilities 
                    {
                        unknown.add(new Operators("UNSTACK", bx.name, current1.value1, null, null, null));
                    }
                }
                 return null;
            }
            
            if("ON".equals(current1.name))
            {
                 return (new Operators("STACK", current1.value1, current1.value2, null, null, null));
            }
            
            if("HOLDING".equals(current1.name)) //for holding we have two possibilities P.U. and UNS
            {
                    for(Box bx : blocks)
                    {
                        if(!current1.value1.equals(bx.name)) //we don't know which is second block so we add all of the possibilities 
                        {
                            unknown.add(new Operators("UNSTACK", current1.value1, bx.name,  null, null, null));
                        }
                    }
                    return (new Operators("PICKUP", current1.value1, null, null, null));
            }
            return null;
    }
    
    public static String read_from_file(String path)
    {
        String info = "";
        File file = new File(path);
        
        try (FileReader reader = new FileReader(file)) {
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            info = new String(chars);
        }
        catch (IOException e) {
        }
        
        System.out.println(info);
        return info;
    }
    
    public static void get_info_from_text(String text)
    {
        int GS_Index = 0; 
        String [] tmp = text.split("\n");
        
        // parse size of table
        table_size = Integer.parseInt(tmp[0].substring(tmp[0].indexOf("=")+1, tmp[0].indexOf("\r"))); 
        
        // parse bloks 
        tmp[1] = tmp[1].substring(tmp[1].indexOf("=")+1, tmp[1].indexOf(";"));
        String[] blk_tmp = tmp[1].split(",");
        blocks = new ArrayList<>();
        for (String blk_tmp1 : blk_tmp) {
            blocks.add(new Box(0, blk_tmp1));
        }
        
        //parse initial state
        initialState = new ArrayList<>();
        tmp[2] = tmp[2].substring(tmp[2].indexOf("=")+1, tmp[2].length()-1); //get string without "InitalState" text
        for(int i = 2; i < tmp.length; i++)
        {
            if(tmp[i].contains("GoalState")){GS_Index = i; break;} // parse till this line
            String [] tmparr = tmp[i].split(";");
            for(String tmparr1 : tmparr)
            {
                if(!"\r".equals(tmparr1))
                {
                    
                    initialState.add(from_txt_to_state(tmparr1));
                }
            }
        }
        
        
        //parse goal state
        goalState = new ArrayList<>();
        tmp[GS_Index] = tmp[GS_Index].substring(tmp[GS_Index].indexOf("=")+1, tmp[GS_Index].length()-1); //get string without "GoalState" text
        for(int i = GS_Index; i < tmp.length; i++)
        {
            String [] tmparr = tmp[i].split(";");
            for(String tmparr1 : tmparr)
            {
                if(!"\r".equals(tmparr1))
                {
                    goalState.add(from_txt_to_state(tmparr1));
                }
            }
        }
        
        set_blocks(); // set the size of blocks after we know initial state
    }
    
    public static State from_txt_to_state(String st)
    {
        
        String name = null, val1 = null, val2 = null;
        
        if(st.contains("("))  
            {
                name = st.substring(0, st.indexOf("(")); //get name
            }else
            {
                name = st;
            }
        
        try{
            st = st.substring(st.indexOf("(")+1, st.indexOf(")"));
            if(st.contains(","))
            {
                String[] tmp = st.split(",");
                val1 = tmp[0];
                val2 = tmp[1];
            }else
            {
                val1 = st;
            }
        }catch(Exception e){}
            
        return new State(name, val1, val2);
    }
    
    public static void set_operators()
    {
        operator = new ArrayList<>();
        List<State> Precondition = new ArrayList<>();
        List<State> Eliminate = new ArrayList<>();
        List<State> Add = new ArrayList<>();
        
        //----------pick up (x)
        Precondition.add(new State("UsedColsNum".toUpperCase(), "n"));
        Precondition.add(new State("OnTable".toUpperCase(), "x"));
        Precondition.add(new State("Clear".toUpperCase(), "x"));
        Precondition.add(new State("EmptyHand".toUpperCase()));
        
        Eliminate.add(new State("UsedColsNum".toUpperCase(), "n"));
        Eliminate.add(new State("OnTable".toUpperCase(), "x"));
        Eliminate.add(new State("EmptyHand".toUpperCase()));
        
        Add.add(new State("UsedColsNum".toUpperCase(), "n-1"));
        Add.add(new State("Holding".toUpperCase(), "x"));
        
        operator.add(new Operators("PickUp".toUpperCase(), "x", Precondition, Eliminate, Add));
        //----------pick up (x)
        
        Precondition = new ArrayList<>();
        Eliminate = new ArrayList<>();
        Add = new ArrayList<>();
        
        //----------PutDown (x)
        Precondition.add(new State("UsedColsNum".toUpperCase(), "n"));
        Precondition.add(new State("Holding".toUpperCase(), "x"));
        
        Eliminate.add(new State("UsedColsNum".toUpperCase(), "n"));
        Eliminate.add(new State("Holding".toUpperCase(), "x"));
        
        Add.add(new State("UsedColsNum".toUpperCase(), "n+1"));
        Add.add(new State("EmptyHand".toUpperCase()));
        Add.add(new State("OnTable".toUpperCase(), "x"));
        
        operator.add(new Operators("PutDown".toUpperCase(), "x", Precondition, Eliminate, Add));
        //----------PutDown (x)
        
        
        Precondition = new ArrayList<>();
        Eliminate = new ArrayList<>();
        Add = new ArrayList<>();
        
        //----------Unstack (x,y)
        Precondition.add(new State("On".toUpperCase(), "x", "y"));
        Precondition.add(new State("Clear".toUpperCase(), "x"));
        Precondition.add(new State("EmptyHand".toUpperCase()));
        
        Eliminate.add(new State("On".toUpperCase(), "x", "y"));
        Eliminate.add(new State("EmptyHand".toUpperCase()));
        
        Add.add(new State("Clear".toUpperCase(), "y"));
        Add.add(new State("Holding".toUpperCase(), "x"));
        
        operator.add(new Operators("Unstack".toUpperCase(), "x", "y", Precondition, Eliminate, Add));
        //----------Unstack (x,y)
        
        Precondition = new ArrayList<>();
        Eliminate = new ArrayList<>();
        Add = new ArrayList<>();
        
        //----------Stack (x,y)
        Precondition.add(new State("Size".toUpperCase(), "x", "a"));
        Precondition.add(new State("Size".toUpperCase(), "y", "b"));
        Precondition.add(new State("Holding".toUpperCase(), "x"));
        Precondition.add(new State("Clear".toUpperCase(), "y"));
        
        Eliminate.add(new State("Holding".toUpperCase(), "x"));
        Eliminate.add(new State("Clear".toUpperCase(), "y"));
        
        
        Add.add(new State("On".toUpperCase(), "x", "y"));
        Add.add(new State("EmptyHand".toUpperCase()));
        
        operator.add(new Operators("Stack".toUpperCase(), "x", "y", Precondition, Eliminate, Add));
        //----------Stack (x,y)
        
        
    }
    
    public static void set_blocks()
    {
        for (State st : initialState)
        {
            if("SIZE".equals(st.name))
            {
                for(Box bx : blocks)
                {
                    if(bx.name.equals(st.value1))
                    {
                        bx.size = Integer.parseInt(st.value2); // set size to each box
                    }
                }
            }
        }
    }
    
    public static int get_block_size(String name)
    {
        int tmp = 0; 
        for(Box bx : blocks)
                {
                    if(bx.name.equals(name))
                    {
                        tmp = bx.size;
                    }
                }
        return tmp;
    }
    
    public static void printSolution(States s)
    {
        int parent = s.parent;
        
        writer.println("-----------SOLUTION------------------------------------------------------------------------------------------------------------------------------------");
        
        printOperator(s.operator);
        while(parent!=0)
        {
            printOperator(ok.get(parent).operator);
            parent = ok.get(parent).parent;
        }
        
    }
    
    public static void printOperator(Operators st)
    {
        if(st.name.equals("PICKUP")||st.name.equals("PUTDOWN")) // because they have just one value
        {
            writer.println(st.name+"("+st.value1+")");
        }
        if(st.name.equals("STACK")||st.name.equals("UNSTACK")) // because they have two values
        {
            writer.println(st.name+"("+st.value1+","+st.value2+")");
        }
    }
    
    public static void printStete(State st)
    {
        if(st.name.equals("ONTABLE")||st.name.equals("CLEAR")||st.name.equals("HOLDING")) // because they have just one value
        {
            writer.println(st.name+"("+st.value1+")");
        }
        if(st.name.equals("EMPTYHAND")) 
        {
            writer.println("EMPTYHAND");
        }
        if(st.name.equals("ON")||st.name.equals("SIZE")) // because they have two values
        {
            writer.println(st.name+"("+st.value1+","+st.value2+")");
        }
    }
}
