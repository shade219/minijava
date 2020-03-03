package Symtab;

import AST.*;

public class VarSymbol extends Symbol {
	public VarSymbol(String n, String t) {
		super(n, t);
	}
	
	public String toString() {
		return "{VAR}" + getType() + " " + getName();
	}
	
	public Symbol copy() {
		return new VarSymbol(name, type);
	}
	
    public boolean equals(Symbol s) {
    	if ( s == null || !(s instanceof VarSymbol) )
    		return false;
    	
    	VarSymbol vs = (VarSymbol)s;    	
    	return vs.toString().equals(this.toString());
    }
}