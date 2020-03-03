package Symtab;

import AST.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class SymbolTable {

    private HashMap<String, Symbol> ctable, vtable, mtable;
    private HashMap<String, SymbolTable> children;
    private SymbolTable parent = null;
    private ASTNode scope = null;

    public SymbolTable() {
        ctable = new HashMap<String, Symbol>();
        mtable = new HashMap<String, Symbol>();
        vtable = new HashMap<String, Symbol>();
        children = new HashMap<String, SymbolTable>();
        parent = null;
        scope = null;
    }

    public SymbolTable(SymbolTable p, ASTNode n) {
        ctable = new HashMap<String, Symbol>();
        mtable = new HashMap<String, Symbol>();
        vtable = new HashMap<String, Symbol>();
        children = new HashMap<String, SymbolTable>();
        parent = p;
        scope = n;
    }
    
    public HashMap<String, Symbol> getClassTable() {
    	return ctable;    	
    }
    
    public HashMap<String, Symbol> getMethodTable() {
    	return mtable;    	
    }
    
    public HashMap<String, Symbol> getVarTable() {
    	return vtable;    	
    }
    
    public void addSymbol(Symbol s) {
    	if ( s instanceof ClassSymbol )
    		//if ( !ctable.containsKey(s.getName()) )
    			ctable.put(s.getName(), s);
    	else if ( s instanceof MethodSymbol )
    		//if ( !mtable.containsKey(s.getName()) )
    			mtable.put(s.getName(), s);
    	else if ( s instanceof VarSymbol )
    		//if ( !vtable.containsKey(s.getName()) )
				vtable.put(s.getName(), s);
    }

    Symbol getSymbol(String i) {
        Symbol s = vtable.get(i);
        if ( s == null )
        	s = mtable.get(i);
        if ( s == null )
        	s = ctable.get(i);
        return s;
    }

    public Symbol lookupSymbol(String i) {
        SymbolTable st = this;
        while ( st != null ) {
            Symbol s = st.getSymbol(i);
            if ( s != null ) return s;
            st = st.getParent();
        }
        return null;
    }

    public Symbol lookupSymbol(String name, String type) {
        SymbolTable st = this;
        while ( st != null ) {
            Symbol s = null; 
            if ( type == "VarSymbol" ) 
            	s = st.vtable.get(name);
            else if ( type == "ClassSymbol" )
            	s = st.ctable.get(name);
            else if ( type == "MethodSymbol" )
            	s = st.mtable.get(name);
            if ( s != null ) return s;
            st = st.getParent();
        }
        return null;
    }

    public SymbolTable enterScope(String i, ASTNode n) {
        SymbolTable st = this.getChild(i);
        if ( st == null ) {
            st = new SymbolTable(this, n);
            this.addChild(i, st);
        }
        return st;
    }

    public SymbolTable findScope(String i) {
        SymbolTable st = this.getChild(i);
        if ( st != null ) {
        	return st;
        }
        return this;
    }

    public SymbolTable exitScope() {
    	return this.getParent();
    }

    public void addChild(String i, SymbolTable st) {
        children.put(i, st);
        st.parent = this;
    }

    public SymbolTable getChild(String i) {
        return children.get(i);
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void setScope(ASTNode n) {
    	scope = n;
    }
    
    public ASTNode getScope() {
    	return scope;
    }
    
    public void printTabs(int tabs)
    {
        for ( int t=0; t<tabs*4; t++ ) {
        	System.out.print(" ");
        }
    }

    public void print(int level) {
        for ( Iterator i = ctable.keySet().iterator(); i.hasNext(); ) {
            String s = (String)i.next();
            Symbol sym = ctable.get(s);
            printTabs(level);
            System.out.println(sym.toString());
            SymbolTable child = children.get(s);
            if ( child != null ) {
            	child.print(level+1);
            }
        }
    	
        for ( Iterator<String> i = mtable.keySet().iterator(); i.hasNext(); ) {
            String s = (String)i.next();
            Symbol sym = mtable.get(s);
            printTabs(level);
            System.out.println(sym.toString());
            SymbolTable child = children.get(s);
            if ( child != null ) {
            	child.print(level+1);
            }
        }

        for ( Iterator<String> i = vtable.keySet().iterator(); i.hasNext(); ) {
            String s = (String)i.next();
            Symbol sym = vtable.get(s);
            printTabs(level);
            System.out.println(sym.toString());
        }
    }
}