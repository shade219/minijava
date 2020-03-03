package AST.Visitor;

import AST.*;
import Symtab.*;

public class SemanticAnalysisVisitor implements ObjectVisitor {

	SymbolTable st = null;
	public int errors = 0;
	
	public void setSymtab(SymbolTable s)
	{
		st = s;
	}

	public SymbolTable getSymtab()
	{
		return st;
	}

	public void report_error(int line, String msg)
	{
		System.out.println(line+": "+msg);
		++errors;
	}
	
	public String checkIntType(Object o)
	{
		if ( o != null && o instanceof String && ((String)o).equals("int") )
		{
			return "int";
		}
		return null;
	}
	
	public Symbol getSymbol(ASTNode node) 
	{
		String name = null;
		
		if ( node == null ) {
			return null;
		}
		else if ( node instanceof IdentifierExp ) {
			IdentifierExp i = (IdentifierExp)node;
			name = i.s;
		}
		else if  ( node instanceof Identifier ) {
			Identifier i = (Identifier)node;
			name = i.s;		
		}
		
		// symbol was found
		Symbol s = st.lookupSymbol(name);	
		if ( s != null ) {
			return s;
		}

		// get the parent class
		ASTNode p = node;
		while ( p != null && !(p instanceof ClassDecl) ) {
			p = p.getParent();
		}

		// check if the parent class is extended
		if ( p != null && p instanceof ClassDeclExtends ) {
			ClassDeclExtends c = (ClassDeclExtends)p;
			ClassSymbol cs = (ClassSymbol)st.lookupSymbol(c.j.s);			
			if ( cs != null ) {
				return cs.getVariable(name);
			}
		}
		
		return null;
	}
	
	// Display added for toy example language. Not used in regular MiniJava
	public Object visit(Display n) {
		if ( n.e != null ) n.e.accept(this);
		return null;
	}
	
	// MainClass m;
	// ClassDeclList cl;
	public Object visit(Program n) {
		if ( n.m != null ) n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.get(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Object visit(MainClass n) {
		Object o = ( n.i1 != null ) ? n.i1.accept(this) : null;
		//n.i2.accept(this); // i2 is the String[] a argument declaration
		st = st.findScope(n.i1.toString());
		st = st.findScope("main");
		if ( n.s != null ) n.s.accept(this);
		st = st.exitScope();
		st = st.exitScope();
		return n.i1.toString();
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Object visit(ClassDeclSimple n) {
		Object o = ( n.i != null ) ? n.i.accept(this) : null;
		st = st.findScope(n.i.toString());				
		if ( n.vl != null ) {
			for (int i = 0; i < n.vl.size(); i++) {
				if ( n.vl.get(i) != null ) n.vl.get(i).accept(this);
			}
		}
		
		if ( n.ml != null ) {
			for (int i = 0; i < n.ml.size(); i++) {
				if ( n.ml.get(i) != null ) n.ml.get(i).accept(this);
			}
		}
		st = st.exitScope();
		return o;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Object visit(ClassDeclExtends n) {
		Object o = ( n.i != null ) ? n.i.accept(this) : null;
		if ( n.j != null ) n.j.accept(this);
		
		st = st.findScope(n.i.toString());		
		
		if ( n.vl != null ) {
			for (int i = 0; i < n.vl.size(); i++) {
				if ( n.vl.get(i) != null ) n.vl.get(i).accept(this);
			}
		}
		
		if ( n.ml != null ) {
			for (int i = 0; i < n.ml.size(); i++) {
				if ( n.ml.get(i) != null ) n.ml.get(i).accept(this);
			}
		}
		
		st = st.exitScope();
		return o;
	}

	// Type t;
	// Identifier i;
	public Object visit(VarDecl n) {
		//if ( n.i != null ) n.i.accept(this); // i is the variable name
		return ( n.t != null ) ? n.t.accept(this) : null;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Object visit(MethodDecl n) {
		Object o = ( n.t != null ) ? n.t.accept(this) : null;
		//if ( n.i != null ) n.i.accept(this);  // method name
		st = st.findScope(n.i.toString());		
		
		if ( n.fl != null ) {
			for (int i = 0; i < n.fl.size(); i++) {
				if ( n.fl.get(i) != null ) n.fl.get(i).accept(this);
			}
		}
		
		if ( n.vl != null ) {
			for (int i = 0; i < n.vl.size(); i++) {
				if ( n.vl.get(i) != null ) n.vl.get(i).accept(this);
			}
		}
		
		if ( n.sl != null ) {
			for (int i = 0; i < n.sl.size(); i++) {
				if ( n.sl.get(i) != null ) n.sl.get(i).accept(this);
			}
		}
		
		Object r = ( n.e != null ) ? n.e.accept(this) : null;
		if ( r != null && !r.equals(o) ) {
			report_error(n.getLineNo(), "Mismatched function call return type for \""+n.i.toString()+"\".");			
		}
		
		st = st.exitScope();
		
		return o;
	}

	// Type t;
	// Identifier i;
	public Object visit(Formal n) {
		//Object o1 = ( n.t != null ) ? n.t.accept(this) : null;
		//Object o2 = ( n.i != null ) ? n.i.accept(this) : null;
		return ( n.t != null ) ? n.t.accept(this) : null;
	}

	public Object visit(IntArrayType n) {
		return "int[]";
	}

	public Object visit(BooleanType n) {
		return "Boolean";
	}

	public Object visit(IntegerType n) {
		return "int";
	}

	// String s;
	public Object visit(IdentifierType n) {
		return n.s;
	}

	// StatementList sl;
	public Object visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Object visit(If n) {
		Object o = ( n.e != null ) ? n.e.accept(this) : null;
		Object o1 = ( n.s1 != null ) ? n.s1.accept(this) : null;
		Object o2 = (n.s2 != null) ? n.s2.accept(this) : null;

		if ( o == null || 
			!(o instanceof String) ||
			!((String)o).equals("Boolean") )
		{
			report_error(n.getLineNo(), "Expecting boolean type for if condition.");
		}

		return null;
	}

	// Exp e;
	// Statement s;
	public Object visit(While n) {
		Object o1 = ( n.e != null ) ? n.e.accept(this) : null;
		Object o2 = ( n.s != null ) ? n.s.accept(this) : null;
		
		if ( o1 == null || 
			!(o1 instanceof String) ||
			!((String)o1).equals("Boolean") )
		{
			report_error(n.getLineNo(), "Expecting boolean type for while loop condition.");
		}
		
		return null;
	}

	// Exp e;
	public Object visit(Print n) {
		Object o = ( n.e != null ) ? n.e.accept(this) : null;
		// TODO
		return "void";
	}

	// Identifier i;
	// Exp e;
	public Object visit(Assign n) {
		Object o1 = ( n.i != null ) ? n.i.accept(this) : null;
		Object o2 = ( n.e != null ) ? n.e.accept(this) : null;

		if ( o1 == null || o2 == null || !o1.equals(o2) ) 
		{
			report_error(n.getLineNo(), "Mismatched lhs & rhs in assignment statement.");
		}
		
		return o1;
	}

	// Identifier i;
	// Exp e1,e2;
	public Object  visit(ArrayAssign n) {
		// i[e1] = e2;
		Object o = ( n.i != null ) ? n.i.accept(this) : null;
		Object o1 = ( n.e1 != null ) ? n.e1.accept(this) : null;
		Object o2 = ( n.e2 != null ) ? n.e2.accept(this) : null;

		String t = (o != null && o instanceof String) ? (String)o : null;
		if ( t == null || t != "int[]" )
		{
			report_error(n.getLineNo(), "Undefined array type.");
		}
		else
		{
			t = t.replaceAll("\\[\\]", "");
		}
		
		if ( o1 == null || 
			!(o1 instanceof String) ||
			!((String)o1).equals("int") )
		{
			report_error(n.getLineNo(), "Expecting int type for array index.");
		}
		
		if ( t == null || o2 == null || !t.equals(o2) ) 
		{
			report_error(n.getLineNo(), "Mismatched lhs & rhs in array assignment statement.");
		}
		
		return o;
	}

	// Exp e1,e2;
	public Object visit(And n) {
		Object o1 = ( n.e1 != null ) ? n.e1.accept(this) : null;
		Object o2 = ( n.e2 != null ) ? n.e2.accept(this) : null;
		
		String s1 = (o1 != null && o1 instanceof String) ? (String)o1 : "";
		String s2 = (o2 != null && o2 instanceof String) ? (String)o2 : "";
		
		if ( s1 != s2 || s1 != "Boolean") {
			report_error(n.getLineNo(), "Expecting boolean operand types in and operation.");
			return null;
		}
		
		return s1;
	}

	// Exp e1,e2;
	public Object visit(LessThan n) {
		String s1 = checkIntType(( n.e1 != null ) ? n.e1.accept(this) : null);
		String s2 = checkIntType(( n.e2 != null ) ? n.e2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(n.getLineNo(), "Expecting int operand types in compare operation.");
			return null;
		}
		
		return "Boolean";
	}

	// Exp e1,e2;
	public Object visit(Plus n) {
		String s1 = checkIntType(( n.e1 != null ) ? n.e1.accept(this) : null);
		String s2 = checkIntType(( n.e2 != null ) ? n.e2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(n.getLineNo(), "Expecting int operand types in add operation.");
			return null;
		}
		
		return s1;
	}

	// Exp e1,e2;
	public Object visit(Minus n) {
		String s1 = checkIntType(( n.e1 != null ) ? n.e1.accept(this) : null);
		String s2 = checkIntType(( n.e2 != null ) ? n.e2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(n.getLineNo(), "Expecting int operand types in subtract operation.");
			return null;
		}
		
		return s1;
	}

	// Exp e1,e2;
	public Object visit(Times n) {
		String s1 = checkIntType(( n.e1 != null ) ? n.e1.accept(this) : null);
		String s2 = checkIntType(( n.e2 != null ) ? n.e2.accept(this) : null);
		if ( s1 != s2 || s1 != "int") {
			report_error(n.getLineNo(), "Expecting int operand types in multiply operation.");
			return null;
		}
		
		return s1;
	}

	// Exp e1[e2];
	public Object visit(ArrayLookup n) {
		Object o1 = ( n.e1 != null ) ? n.e1.accept(this) : null;
		
		String t = (o1 != null && o1 instanceof String) ? (String)o1 : null;
		if ( t == null )
		{
			report_error(n.getLineNo(), "Undefined array type.");
		}
		else
		{
			t = t.replaceAll("\\[\\]", "");
		}

		Object o2 = ( n.e2 != null ) ? n.e2.accept(this) : null;
		if ( o2 == null || 
			!(o2 instanceof String) ||
			!((String)o2).equals("int") )
		{
			report_error(n.getLineNo(), "Expecting int type for array index.");
		}
		
		return t;
	}

	// Exp e;
	public Object visit(ArrayLength n) {
		if ( n.e != null ) n.e.accept(this);
		return "int";
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Object visit(Call n) {
		
		// obj.func() -- method call from object type
		String c = (n.e != null) ? (String)n.e.accept(this) : "";
		if ( c != null && c != "" ) {
			Symbol s = st.lookupSymbol(c);
			if ( s == null || !(s instanceof ClassSymbol) ) {
				report_error(n.e.getLineNo(), "Class object \""+c+"\" is undefined.");
				return null;
			}

			ClassSymbol cs = (ClassSymbol)s;
			MethodSymbol ms = cs.getMethod(n.i.s);
			if ( ms != null ) {
				// call expression list
				if ( ms.getParameters().size() != n.el.size() ) {
					report_error(n.e.getLineNo(), "Function call \""+n.i.s+"\" expecting "+ms.getParameters().size()+" argument(s), but "+n.el.size()+" provided.");
				}
				else {
					for (int i = 0; i < n.el.size(); i++) {
						String argtype = ms.getParameters().get(i).getType();						
						String param = (String)n.el.get(i).accept(this);
						Symbol param_sym = ( param != null ) ? st.lookupSymbol(param) : null;

						if ( param_sym != null && param_sym instanceof ClassSymbol ) {
							String param_ext = param_sym.getType();
							if ( !param.equals(argtype) && !param_ext.equals(argtype) ) {
								report_error(n.e.getLineNo(), "Function call \""+n.i.s+"\" argument "+i+" expecting "+argtype+" type.");
							}
						}
						else if ( !param.equals(argtype) ) {
							report_error(n.e.getLineNo(), "Function call \""+n.i.s+"\" argument "+i+" expecting "+argtype+" type.");
						}		
					}
				}
				
				return ms.getType();
			}

			report_error(n.getLineNo(), "Function call \""+n.i.s+"\" is undefined.");
			return null;
		}
		
		// func() -- method call with no object
		//n.i.accept(this); i is the function name
		Symbol s = st.lookupSymbol(n.i.s);
		if ( s != null && s instanceof MethodSymbol ) {
			MethodSymbol ms = (MethodSymbol)s;
			
			// call expression list
			if ( ms.getParameters().size() != n.el.size() ) {
				report_error(n.e.getLineNo(), "Function call \""+n.i.s+"\" expecting "+ms.getParameters().size()+" arguments, but only "+n.el.size()+" were provided.");
			}
			else {
				for (int i = 0; i < n.el.size(); i++) {
					Object o = n.el.get(i).accept(this);
					String argtype = ms.getParameters().get(i).getType();
					if ( !o.equals(argtype) ) {
						report_error(n.e.getLineNo(), "Function call \""+n.i.s+"\" argument "+i+" expecting "+argtype+" type.");
					}
				}
			}
			
			return ms.getType();
		}

		report_error(n.getLineNo(), "Function call \""+n.i.s+"\" is undefined.");
		return null;
	}

	// int i;
	public Object visit(IntegerLiteral n) {
		return "int";
	}

	public Object visit(True n) {
		return "Boolean";
	}

	public Object visit(False n) {
		return "Boolean";
	}

	// String s;
	public Object visit(IdentifierExp n) {
		Symbol s = getSymbol(n);
		if ( s == null ) {
			report_error(n.getLineNo(), "Symbol \""+n.s+"\" has not been declared.");
			return null;
		}
		
		return s.getType();
	}

	public Object visit(This n) {
		
		// get the current scope
		ASTNode a = st.getScope();

		// Get the parent class
		while ( a != null && !(a instanceof ClassDecl) ) {
			a = a.getParent();
		}
		
		if ( a != null && a instanceof ClassDeclSimple ) {
			ClassDeclSimple c = (ClassDeclSimple)a;
			return c.i.toString();
		}
		else if ( a != null && a instanceof ClassDeclExtends ) {
			ClassDeclExtends c = (ClassDeclExtends)a;
			return c.i.toString();
		}
		
		report_error(n.getLineNo(), "Unable to determine \"this\" reference.");
		return null;
	}

	// Exp e;
	public Object visit(NewArray n) {
		// new int[e]
		Object o = ( n.e != null ) ? n.e.accept(this) : null;
		if ( o == null || !(o instanceof String) || !((String)o).equals("int") ) {
			report_error(n.getLineNo(), "New array expecting integer size.");
		}
		return "int[]";
	}

	// Identifier i;
	public Object visit(NewObject n) {
		Symbol s = getSymbol(n.i);
		if ( s == null ) {
			report_error(n.getLineNo(), "Object \""+n.i.s+"\" has not been declared.");
		}
		
		return n.i.s;
	}

	// Exp e;
	public Object visit(Not n) {
		return ( n.e != null ) ? n.e.accept(this) : null;
	}

	// String s;
	public Object visit(Identifier n) {
		Symbol s = getSymbol(n);
		if ( s == null ) {
			getSymbol(n);
			report_error(n.getLineNo(), "Symbol \""+n.s+"\" has not been declared.");
			return null;
		}
		return s.getType();
	}
}