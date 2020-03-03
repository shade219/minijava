package AST.Visitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import AST.*;
import Symtab.*;

public class CodeTranslateVisitor implements Visitor {

	SymbolTable st = null;
	public int errors = 0;

	private int stack_mem = 0;
	private boolean alignment = false;

	private HashMap<String, String> objectIDType = new HashMap<String, String>();
	public HashMap<VarSymbol, String> stack_table = new HashMap<VarSymbol, String>();
	public HashMap<VarSymbol, String> object_table = new HashMap<VarSymbol, String>();
	public int stack_pos = -8;
	public int obj_pos = 8;
	public String call_regs[] = {"%rsi", "%rdx", "%rcx", "%r8", "%r9"};
	public int args_pos = 0;
	public int labelCount = 1;

	public void setSymtab(SymbolTable s) { st = s; }

	public SymbolTable getSymtab() { return st; }

	public void report_error(int line, String msg) {
		System.out.println(line + ": " + msg);
		++errors;
	}

	public String getLabel(String class_name, String call_name) {
		String label = !class_name.isEmpty() ? class_name+"$"+call_name : call_name;
		String os = System.getProperty("os.name");
		if (os.contains("Windows") || os.contains("OS X")) {
			return "_"+label;
		}
		return label;
	}

	private void prologue() {
		pushq("%rbp");
		gen("movq", "%rsp", "%rbp");
	}

	private void epilogue() {
		gen("movq", "%rbp", "%rsp");
		popq("%rbp");
		gen("ret", "");
	}

	// Display added for toy example language. Not used in regular MiniJava
	public void visit(Display n) {
		n.e.accept(this);
	}

	// MainClass m;
	// ClassDeclList cl;
	public void visit(Program n) {
		System.out.println("\t" + ".text");
		String asmMainLabel = getLabel("","asm_main");
		System.out.println("\t" + ".globl" + "\t" + asmMainLabel);
		n.m.accept(this);
		if (n.cl != null){
			for (int i = 0; i < n.cl.size(); i++) {
				n.cl.get(i).accept(this);
			}
		}
	}

	// Identifier i1,i2;
	// Statement s;
	public void visit(MainClass n) {
		//n.i1.accept(this);
		//n.i2.accept(this);
		String mainLabel = getLabel("", "asm_main:");
		gen(mainLabel);
		prologue();
		n.s.accept(this);
		epilogue();
	}

	private void dispatchTable(String className, String parentName, int mem_needed){


		classConstructor(mem_needed, className, parentName);

		//get method list from class symbol
		Symbol tempSym = st.lookupSymbol(className);
		ArrayList<MethodSymbol> mSyms = ((ClassSymbol) tempSym).getMethods();
		int mCount = mSyms.size();

		gen("\t" + ".data");

		//create class label for vtabel
		String extClass;
		if (parentName.equals("0")){
			genTLabel(className, "0");
		}
		else {
			genTLabel(className, parentName + "$$");
		}
		gen("\t" + ".quad " + className + "$" + className);
		for(MethodSymbol ms : mSyms){
			String mName = ms.getName();
			gen("\t" + ".quad " + className + "$" + mName);
		}
	}

	//create the vtable label
	private void genTLabel(String i, String j){
		gen(i + "$$: .quad " + j);
	}

	//create class constructors called by new object creation
	private void classConstructor(int mem_size, String cName, String pName){
		gen(cName + "$" + cName + ":");
		prologue();

		pushq("%rdi");
		gen("movq", mem_size, "%rdi");

		align("%rax");
		String temp = getLabel("","mjcalloc");
		gen("call", temp);
		undo("%rdx");
		popq("%rdi");

		gen("leaq", cName + "$$" + "(%rip)", "%rdx");

		gen("movq", "%rdx", "(%rax)");

		epilogue();
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) {
		//n.i.accept(this);

		int mem_needed = n.vl.size();
		mem_needed += 8;

		st = st.enterScope(n.i.s, n);
		obj_pos = 8;
		for (int i = 0; i < n.vl.size(); i++) {
			VarDecl n1 = n.vl.get(i);
			String v1 = n1.i.s;
			Symbol sym = st.lookupSymbol(v1);
			if (sym != null && sym instanceof VarSymbol){
				VarSymbol vs = (VarSymbol)sym;
				String obj_loc = Integer.toString(obj_pos) + "(%rdi)";
				obj_pos += 8;
				object_table.put(vs, obj_loc);
			}
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
		dispatchTable(n.i.s,"0",mem_needed);
		st = st.exitScope();
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) {
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.get(i).accept(this);
		}
	}

	// Type t;
	// Identifier i;
	public void visit(VarDecl n) {
		//n.t.accept(this);
		String classname = "";
		if (n.t instanceof IdentifierType){
			classname = ((IdentifierType) n.t).s;
		}
		objectIDType.put(n.i.s,classname);
		//n.i.accept(this);

		//get current method scope to get method name
		/*
		ASTNode a1 = st.getScope();
		MethodDecl m1 = (MethodDecl) a1;
		String tableName = m1.i.s;
		*/
		Symbol sym = st.lookupSymbol(n.i.s);
		if (sym != null && sym instanceof VarSymbol){
			VarSymbol vs = (VarSymbol)sym;
			String stack_loc = Integer.toString(stack_pos) + "(%rbp)";
			stack_pos -= 8;
			stack_table.put(vs, stack_loc);
		}
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public void visit(MethodDecl n) {

		//get name of class that method belongs to
		ASTNode classSym = st.getScope();
		String classname = "";
		if (classSym instanceof ClassDeclSimple){
			classname = ((ClassDeclSimple) classSym).i.s;
		}
		if (classSym instanceof ClassDeclExtends){
			classname = ((ClassDeclExtends) classSym).i.s;
		}


		st = st.enterScope(n.i.s, n);

		int varSize = n.vl.size();
		int formalSize = n.fl.size();
		int totalSize = (8*(varSize+formalSize));

		gen(classname + "$" + n.i.s + ":");
		prologue();

		gen("subq", totalSize, "%rsp");
		//stack_mem += totalSize;
		//align("%rax");
		//gen("andq", 16, "%rsp");

		//n.t.accept(this);
		//n.i.accept(this);
		stack_table.clear();
		args_pos = 0;
		stack_pos = -8;
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.get(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.get(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}

		//undo("%rdx");
		//stack_mem -= totalSize;
		n.e.accept(this);
		epilogue();

		st = st.exitScope();
	}


	// Type t;
	// Identifier i;
	public void visit(Formal n) {
		//n.t.accept(this);
		//n.i.accept(this);
		Symbol sym = st.lookupSymbol(n.i.s);
		if (sym != null && sym instanceof VarSymbol){
			VarSymbol vs = (VarSymbol)sym;
			String stack_loc = Integer.toString(stack_pos) + "(%rbp)";
			stack_pos -= 8;
			stack_table.put(vs, stack_loc);
			gen("movq", call_regs[args_pos++], stack_loc);
		}
	}

	public void visit(IntArrayType n) {
	}

	public void visit(BooleanType n) {
	}

	public void visit(IntegerType n) {
	}

	// String s;
	public void visit(IdentifierType n) {
	}

	// StatementList sl;
	public void visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.get(i).accept(this);
		}
	}

	// Exp e;
	// Statement s1,s2;
	public void visit(If n) {
		//generate labels
		String else_ = "L"+labelCount;
		labelCount++;
		String done_ = "L"+labelCount;
		labelCount++;

		n.e.accept(this);
		gen("cmpq", 0, "%rax");
		gen("je", else_);

		n.s1.accept(this);
		gen("jmp", done_);

		gen(else_ + ":");
		n.s2.accept(this);

		gen(done_ + ":");
	}

	// Exp e;
	// Statement s;
	public void visit(While n) {
		String test_ = "L"+labelCount;
		labelCount++;
		String done_ = "L"+labelCount;
		labelCount++;

		gen(test_ + ":");
		n.e.accept(this);
		gen("cmpq", 0, "%rax");
		gen("je", done_);

		n.s.accept(this);
		gen("jmp", test_);

		gen(done_ + ":");
	}

	// Exp e;
	public void visit(Print n) {
		n.e.accept(this);
		pushq("%rdi");
		gen("movq", "%rax", "%rdi");
		align("%rax");
		String temp = getLabel("","put");
		gen("movq", "%rsp", "%r12");
		gen("andq $-16,%rsp");
		gen("call", temp);
		gen("movq", "%r12", "%rsp");
		undo("%rdx");
		popq("%rdi");
	}

	// Identifier i;
	// Exp e;
	public void visit(Assign n) {
		//n.i.accept(this);
		n.e.accept(this);
		VarSymbol vs1 = (VarSymbol)st.lookupSymbol(n.i.s);
		String location = stack_table.get(vs1);

		if(location == null){
			location = object_table.get(vs1);
			gen("movq", "%rax", location);
		}
		else{
			gen("movq", "%rax", location);
		}
	}

	// Identifier i;
	// Exp e1,e2;
	public void visit(ArrayAssign n) {
		//get array location from variable -> store in %rcx
		VarSymbol vs1 = (VarSymbol)st.lookupSymbol(n.i.s);
		String location = stack_table.get(vs1);

		if(location == null){
			location = object_table.get(vs1);
			gen("movq", location, "%rcx");
		}
		else{
			gen("movq", location, "%rcx");
		}
		//n.i.accept(this);


		n.e1.accept(this);
		pushq("%rax");
		n.e2.accept(this);
		popq("%rdx");

		//array location in %rcx
		//array index in %rdx
		//value for assignment in %rax

		gen("movq", "%rax", "8(%rcx,%rdx,8)");

	}

	// Exp e1,e2;
	public void visit(And n) {
		gen("movq", 0, "%r8");
		gen("movq", 1, "%r9");
		n.e1.accept(this);
		gen("movq", "%rax", "%r10");
		n.e2.accept(this);
		gen("movq", "%rax", "%rcx");
		gen("movq", "%r10", "%rdx");
		gen("movq", 0, "%rax");
		gen("cmpq", 1, "%rdx");
		gen("cmoveq", "%r9", "%rax");
		gen("cmpq", 0, "%rcx");
		gen("cmoveq", "%r8", "%rax");

	}

	// Exp e1,e2;
	public void visit(LessThan n) {
		gen("movq", 1, "%r9");
		n.e1.accept(this);
		gen("movq", "%rax", "%r11");
		n.e2.accept(this);
		gen("movq", "%r11", "%rdx");
		gen("movq", "%rax", "%rcx");
		gen("movq", 0, "%rax");
		gen("cmpq", "%rcx", "%rdx");
		gen("cmovlq", "%r9", "%rax");

	}

	// Exp e1,e2;
	public void visit(Plus n) {
		n.e1.accept(this);
		pushq("%rax");
		n.e2.accept(this);
		popq("%rdx");
		gen("addq", "%rdx", "%rax");
	}

	// Exp e1,e2;
	public void visit(Minus n) {
		n.e1.accept(this);
		pushq("%rax");
		n.e2.accept(this);
		popq("%rdx");
		gen("negq", "%rax");
		gen("addq", "%rdx", "%rax");
	}

	// Exp e1,e2;
	public void visit(Times n) {
		n.e1.accept(this);
		pushq("%rax");
		n.e2.accept(this);
		popq("%rdx");
		gen("imulq", "%rdx", "%rax");
	}

	// Exp e1,e2;
	public void visit(ArrayLookup n) {
		n.e1.accept(this);
		pushq("%rax");
		n.e2.accept(this);
		popq("%rdx");
		gen("movq", "8(%rdx,%rax,8)", "%rax");
	}

	// Exp e;
	public void visit(ArrayLength n) {
		n.e.accept(this);
		gen("movq", "(%rax)", "%rax");
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public void visit(Call n) {
		final String c_regs[] = {"%rsi","%rdx","%rcx","%r8","%r9"};
		//version for this -> get parent to get class name
		//if identifier -> get identifier name

		//save current "this"
		pushq("%rdi");

		n.e.accept(this);
		String classname = "";
		String methodname = "";
		String varname = "";
		if(n.e instanceof This){
			SymbolTable s = st.getParent();
			ASTNode classSym = s.getScope();
			if (classSym instanceof ClassDeclSimple){
				classname = ((ClassDeclSimple) classSym).i.s;
			}
			if (classSym instanceof ClassDeclExtends){
				classname = ((ClassDeclExtends) classSym).i.s;
			}
		}
		else if(n.e instanceof NewObject){
			classname = ((NewObject) n.e).i.s;
		}
		else{
			varname = ((IdentifierExp) n.e).s;
			classname = objectIDType.get(varname);
		}

		methodname = n.i.s;

		//if not "this" store obj reference in rdi
		if(!(n.e instanceof This)){
			gen("movq", "%rax", "%rdi");
		}

		//n.i.accept(this);

		for (int i = 0; i < n.el.size(); i++) {
			n.el.get(i).accept(this);
			gen("pushq", "%rax");
		}

		for (int i = n.el.size(); i >0; i--){
			gen("popq", c_regs[i-1]);
		}

		gen("call", classname + "$" + methodname);


		popq("%rdi");
	}

	/*
	public String getExpr(ASTNode n){
		if (n == null){
			return "";
		}
		else if (n instanceof IntegerLiteral){
			IntegerLiteral i = (IntegerLiteral)n;
			i.accept(this);
			return "%rax";
		}
		else if (n instanceof True){
			True i = (True)n;
			i.accept(this);
			return "%rax";
		}
		else if (n instanceof False){
			False i = (False)n;
			i.accept(this);
			return "%rax";
		}
		else if (n instanceof IdentifierExp){
			IdentifierExp i = (IdentifierExp)n;
			Symbol s = st.lookupSymbol(i.s);
		}
		else if (n instanceof Identifier){
			Identifier i = (Identifier)n;
			Symbol s = st.lookupSymbol(i.s);
		}
		else if (n instanceof ArrayLookup){
			ArrayLookup e = (ArrayLookup)n;
			e.accept(this);
			return "%rax";
		}
		else if (n instanceof Exp){
			Exp e = (Exp)n;
			e.accept(this);
			return "%rax";
		}
		report_error(n.getLineNo(), "Undefined Exp");
		return "";
	}
	*/

	// int i;
	public void visit(IntegerLiteral n) {
		gen("movq", n.i, "%rax");
	}

	public void visit(True n) {
		gen("movq", 1, "%rax");
	}

	public void visit(False n) {
		gen("movq", 0, "%rax");
	}

	// String s;
	public void visit(IdentifierExp n) {
		VarSymbol vs1 = (VarSymbol)st.lookupSymbol(n.s);
		String location = stack_table.get(vs1);

		//if null then ID refers to object field. get offset from objecttable
		if(location == null){
			location = object_table.get(vs1);
			gen("movq", location, "%rax");
		}
		else{
			gen("movq", location, "%rax");
		}
	}

	public void visit(This n) {
		gen("movq", "%rdi", "%rax");
	}

	// Exp e;
	public void visit(NewArray n) {
		n.e.accept(this);

		//store array length on stack
		pushq("%rax");
		//increment length by 1 to store length in first pos
		gen("incq", "%rax");

		gen("imulq", 8, "%rax");
		pushq("%rdi");
		gen("movq", "%rax", "%rdi");
		align("%rax");
		String temp = getLabel("","mjcalloc");
		gen("call", temp);
		undo("%rdx");
		popq("%rdi");

		popq("%rdx");
		gen("movq", "%rdx", "(%rax)");
	}

	// Identifier i;
	public void visit(NewObject n) {
		align("%rax");
		gen("call", n.i.s + "$" + n.i.s);
		undo("%rdx");
	}

	// Exp e;
	public void visit(Not n) {
		n.e.accept(this);
		gen("xorq", 1, "%rax");
	}

	// String s;
	public void visit(Identifier n) {
	}

	//Helper functions to generate code and allign stack
	private void align(String reg){
		if(stack_mem%16 != 0){
			gen("pushq", reg);
			stack_mem += 8;
			alignment = true;
		}
	}

	private void undo(String reg){
		if(alignment) {
			gen("popq", reg);
			stack_mem -= 8;
			alignment = false;
		}
	}


	private void pushq(String reg){
		gen("pushq", reg);
		stack_mem += 8;
	}

	private void popq(String reg){
		gen("popq", reg);
		stack_mem -= 8;
	}


	//Genrates the assembly code print instructions

	private void gen(String s) {
		System.out.println(s);
	}

	private void gen(String instruction, String i, String j){
		gen("\t" + instruction + " " + i + "," + j);
	}

	private void gen(String instruction, int constant, String j){
		gen("\t" + instruction + " " + "$" + constant + "," + j);
	}

	private void gen(String instruction, String i){
		gen("\t" + instruction + " " + i);
	}
}
