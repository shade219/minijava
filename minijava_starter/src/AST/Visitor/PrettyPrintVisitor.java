package AST.Visitor;

import AST.*;


// Sample print visitor from MiniJava web site.

public class PrettyPrintVisitor implements Visitor {

	int tabs = 0;

	public void incTab() {
		tabs += 1;
	}

	public void decTab() {
		tabs -= 1;
		if (tabs < 0)
			tabs = 0;
	}

	public void printTab() {
		String spaces = "";
		for (int i = 0; i < tabs * 4; i++)
			spaces += " ";
		System.out.print(spaces);
	}

	// Display added for toy example language. Not used in regular MiniJava
	public void visit(Display n) {
		printTab();
		System.out.print("display ");
		n.e.accept(this);
		System.out.println(";");
	}

	// MainClass m;
	// ClassDeclList cl;
	public void visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			System.out.println();
			n.cl.get(i).accept(this);
		}
	}

	// Identifier i1,i2;
	// Statement s;
	public void visit(MainClass n) {
		printTab();
		System.out.print("class ");
		n.i1.accept(this);
		System.out.println(" {");
		incTab();
		printTab();
		System.out.print("public static void main (String [] ");
		n.i2.accept(this);
		System.out.println(") {");
		incTab();
		n.s.accept(this);
		decTab();
		printTab();
		System.out.println("}");
		decTab();
		printTab();
		System.out.println("}");
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) {
		printTab();
		System.out.print("class ");
		n.i.accept(this);
		System.out.println(" { ");
		incTab();
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
		decTab();
		System.out.println();
		printTab();
		System.out.println("}");
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) {
		System.out.print("class ");
		n.i.accept(this);
		System.out.println(" extends ");
		n.j.accept(this);
		System.out.println(" { ");
		incTab();
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		System.out.println();
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
		decTab();
		System.out.println();
		printTab();
		System.out.println("}");
	}

	// Type t;
	// Identifier i;
	public void visit(VarDecl n) {
		printTab();
		n.t.accept(this);
		System.out.print(" ");
		n.i.accept(this);
		System.out.println(";");
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public void visit(MethodDecl n) {
		System.out.println();
		printTab();
		System.out.print("public ");
		n.t.accept(this);
		System.out.print(" ");
		n.i.accept(this);
		System.out.print(" (");
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.get(i).accept(this);
			if (i + 1 < n.fl.size()) {
				System.out.print(", ");
			}
		}
		System.out.println(") { ");
		incTab();
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		System.out.println();
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		System.out.println();
		printTab();
		System.out.print("return ");
		n.e.accept(this);
		System.out.println(";");
		decTab();
		printTab();
		System.out.println("}");
	}

	// Type t;
	// Identifier i;
	public void visit(Formal n) {
		n.t.accept(this);
		System.out.print(" ");
		n.i.accept(this);
	}

	public void visit(IntArrayType n) {
		System.out.print("int []");
	}

	public void visit(BooleanType n) {
		System.out.print("boolean");
	}

	public void visit(IntegerType n) {
		System.out.print("int");
	}

	// String s;
	public void visit(IdentifierType n) {
		System.out.print(n.s);
	}

	// StatementList sl;
	public void visit(Block n) {
		System.out.println("{ ");
		incTab();
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
		decTab();
		printTab();
		System.out.println("}");
	}

	// Exp e;
	// Statement s1,s2;
	public void visit(If n) {
		printTab();
		System.out.print("if (");
		n.e.accept(this);
		System.out.print(") ");
		if ((n.s1 instanceof Block) == false) {
			System.out.println();
			incTab();
		}
		n.s1.accept(this);
		if ((n.s1 instanceof Block) == false) {
			decTab();
		}
		printTab();
		System.out.print("else ");
		if ((n.s2 instanceof Block) == false) {
			System.out.println();
			incTab();
		}
		n.s2.accept(this);
		if ((n.s2 instanceof Block) == false) {
			decTab();
		}
	}

	// Exp e;
	// Statement s;
	public void visit(While n) {
		printTab();
		System.out.print("while (");
		n.e.accept(this);
		System.out.print(") ");
		n.s.accept(this);
	}

	// Exp e;
	public void visit(Print n) {
		printTab();
		System.out.print("System.out.println(");
		n.e.accept(this);
		System.out.println(");");
	}

	// Identifier i;
	// Exp e;
	public void visit(Assign n) {
		printTab();
		n.i.accept(this);
		System.out.print(" = ");
		n.e.accept(this);
		System.out.println(";");
	}

	// Identifier i;
	// Exp e1,e2;
	public void visit(ArrayAssign n) {
		printTab();
		n.i.accept(this);
		System.out.print("[");
		n.e1.accept(this);
		System.out.print("] = ");
		n.e2.accept(this);
		System.out.println(";");
	}

	// Exp e1,e2;
	public void visit(And n) {
		System.out.print("(");
		n.e1.accept(this);
		System.out.print(" && ");
		n.e2.accept(this);
		System.out.print(")");
	}

	// Exp e1,e2;
	public void visit(LessThan n) {
		System.out.print("(");
		n.e1.accept(this);
		System.out.print(" < ");
		n.e2.accept(this);
		System.out.print(")");
	}

	// Exp e1,e2;
	public void visit(Plus n) {
		System.out.print("(");
		n.e1.accept(this);
		System.out.print(" + ");
		n.e2.accept(this);
		System.out.print(")");
	}

	// Exp e1,e2;
	public void visit(Minus n) {
		System.out.print("(");
		n.e1.accept(this);
		System.out.print(" - ");
		n.e2.accept(this);
		System.out.print(")");
	}

	// Exp e1,e2;
	public void visit(Times n) {
		System.out.print("(");
		n.e1.accept(this);
		System.out.print(" * ");
		n.e2.accept(this);
		System.out.print(")");
	}

	// Exp e1,e2;
	public void visit(ArrayLookup n) {
		n.e1.accept(this);
		System.out.print("[");
		n.e2.accept(this);
		System.out.print("]");
	}

	// Exp e;
	public void visit(ArrayLength n) {
		n.e.accept(this);
		System.out.print(".length");
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public void visit(Call n) {
		n.e.accept(this);
		System.out.print(".");
		n.i.accept(this);
		System.out.print("(");
		for (int i = 0; i < n.el.size(); i++) {
			n.el.get(i).accept(this);
			if (i + 1 < n.el.size()) {
				System.out.print(", ");
			}
		}
		System.out.print(")");
	}

	// int i;
	public void visit(IntegerLiteral n) {
		System.out.print(n.i);
	}

	public void visit(True n) {
		System.out.print("true");
	}

	public void visit(False n) {
		System.out.print("false");
	}

	// String s;
	public void visit(IdentifierExp n) {
		System.out.print(n.s);
	}

	public void visit(This n) {
		System.out.print("this");
	}

	// Exp e;
	public void visit(NewArray n) {
		System.out.print("new int [");
		n.e.accept(this);
		System.out.print("]");
	}

	// Identifier i;
	public void visit(NewObject n) {
		System.out.print("new ");
		System.out.print(n.i.s);
		System.out.print("()");
	}

	// Exp e;
	public void visit(Not n) {
		System.out.print("!");
		n.e.accept(this);
	}

	// String s;
	public void visit(Identifier n) {
		System.out.print(n.s);
	}
}
