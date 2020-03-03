package Symtab;

import AST.*;
import java.util.ArrayList;

public class MethodSymbol extends Symbol
{
    private ArrayList<Symbol> parameters;

    public MethodSymbol(String n, String t)
    {
    	super(n, t);
        parameters = new ArrayList<Symbol>();
    }

    public Symbol copy() {
    	MethodSymbol m = new MethodSymbol(name, type);
        for(int i=0;i<parameters.size();i++)
        {
        	VarSymbol vs = (VarSymbol)parameters.get(i);
            m.addParameter(vs.copy());
        }
  
        return m;
    }
    
    public void addParameter(Symbol p) {
        for(int i=0;i<parameters.size();i++) {
        	if ( parameters.get(i) == p || parameters.get(i).getName() == p.getName() ) {
        		return;
        	}
        }
        parameters.add(p);
    }

    public ArrayList<Symbol> getParameters() {
        return parameters;
    }
    
    public String toString() {
        String s = "";
        for ( int i=0; i<parameters.size(); i++ ) 
        {
        	s += ((i > 0) ? ", " : "") + parameters.get(i);
        }

        return "{METHOD}" + type + " " + name + "(" + s + ")";
    }
    
    public boolean equals(Symbol s) {
    	if ( s == null || !(s instanceof MethodSymbol) )
    		return false;
    	
    	MethodSymbol ms = (MethodSymbol)s;    	
    	return ms.toString().equals(this.toString());
    }
}