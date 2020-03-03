package Symtab;

import AST.*;
import java.util.ArrayList;

public class ClassSymbol extends Symbol
{
    private ArrayList<MethodSymbol> methods;
    private ArrayList<VarSymbol> variables;

    public ClassSymbol(String n)
    {
        super(n, "");
        methods = new ArrayList<MethodSymbol>();
        variables = new ArrayList<VarSymbol>();
    }

    public ClassSymbol(String n, String e)
    {
        super(n, e);
        methods = new ArrayList<MethodSymbol>();
        variables = new ArrayList<VarSymbol>();
    }
    
    public Symbol copy() {
    	ClassSymbol c = new ClassSymbol(name, type);
        for(int i=0;i<variables.size();i++)
        {
            c.addVariable((VarSymbol)variables.get(i).copy());
        }
  
        for(int i=0;i<methods.size();i++)
        {
            c.addMethod((MethodSymbol)methods.get(i).copy());
        }
        return c;
    }

    public void addMethod(MethodSymbol m) {
        for(int i=0;i<methods.size();i++) {
        	if ( methods.get(i) == m || methods.get(i).getName() == m.getName() ) {
        		return;
        	}
        }

        methods.add(m);
    }

    public void addVariable(VarSymbol v) {
        for(int i=0;i<variables.size();i++) {
        	if ( variables.get(i) == v || variables.get(i).getName() == v.getName() ) {
        		return;
        	}
        }
        variables.add(v);
    }
    
    public MethodSymbol getMethod(String n) {
        for(int i=0;i<methods.size();i++) {
        	MethodSymbol ms = methods.get(i);
        	if ( ms.getName().equals(n) ) {
        		return ms;
        	}
        }
    	return null;
    }

    public ArrayList<MethodSymbol> getMethods() {
        return methods;
    }

    public VarSymbol getVariable(String n) {
        for(int i=0;i<variables.size();i++) {
        	VarSymbol vs = variables.get(i);
        	if ( vs.getName().equals(n) ) {
        		return vs;
        	}
        }
    	return null;
    }

    public ArrayList<VarSymbol> getVariables() {
        return variables;
    }

    public void extendsClass(ClassSymbol c) {
        type = c.name;
        methods.addAll(c.methods);
        variables.addAll(c.variables);
    }

    public String toString() {
        String c = "{CLASS}";
        if(type != null && !type.isEmpty() )
            c += "class " + name + " extends " + type;
        else
            c += "class " + name;

        if ( variables.size() > 0 )
        {
	        c += "\n    Variables:";
	        for(int i=0;i<variables.size();i++)
	        {
	            c += "\n        "+variables.get(i).toString() ;
	        }
        }
        
        if ( methods.size() > 0 )
        {
	        c += "\n    Methods:";
	        for(int i=0;i<methods.size();i++)
	        {
	            c += "\n        "+methods.get(i).toString();
	        }
        }
        return c;
    }
    
    public boolean equals(Symbol s) {
    	if ( s == null || !(s instanceof ClassSymbol) )
    		return false;
    	
    	ClassSymbol cs = (ClassSymbol)s;    	
    	return cs.toString().equals(this.toString());
    }    
}