import Scanner.*;
import Parser.parser;
import Parser.sym;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.*;
import java.util.List;

import AST.*;
import AST.Program;
//import AST.Statement;
import AST.Visitor.PrettyPrintVisitor;
import AST.Visitor.SemanticAnalysisVisitor;
import AST.Visitor.SymTableVisitor;
import AST.Visitor.CodeTranslateVisitor;

public class MiniJava {

    public static void main(String [] args)
    {
    	int status = 0;
		String help = "Use:\n" +
				  	  "  java MiniJava -S <source_file>\n" +
				  	  "  java MiniJava -P <source_file>\n" +
				  	  "  java MiniJava -T <source_file>\n" +
				  	  "  java MiniJava -A <source_file>\n";

    	if ( args.length == 1 && (args[0].equals("-h") || args[0].equals("-H")) )
    	{
        	// print the help
        	System.out.println(help);
    	}
    	else if ( args.length == 2 && args[0].equals("-S") )
    	{
        	// run scanner on the source file
        	status = FileScanner( args[1] );
    	}
    	else if ( args.length == 2 && args[0].equals("-P") )
    	{
        	// run the parser on the source file
        	status = FileParser( args[1] );
    	}
    	else if ( args.length == 2 && args[0].equals("-T") )
    	{
        	// run the parser & generate symbol table
        	status = FileSymbolTable( args[1] );
    	}
    	else if ( args.length == 2 && args[0].equals("-A") )
    	{
        	// run the parser & semantic analysis on the source file
        	status = FileSemanticAnalysis( args[1] );
    	}
			else if ( args.length == 2 && args[0].equals("-C"))
			{
				//run the parser, generate symbol table & generate code
				status = FileCodeGen( args[1] );
			}
    	else
    	{
    		System.err.println("Invalid program arguments.\n" + help);
    		status = 1;
    	}
		System.exit(status);
   }

	public static int FileScanner(String source_file)
	{
		int errors = 0;

	    try {
	        // create a scanner on the input file
	        ComplexSymbolFactory sf = new ComplexSymbolFactory();
	        Reader in = new BufferedReader(new FileReader(source_file));
	        scanner s = new scanner(in, sf);
	        Symbol t = s.next_token();
	        while (t.sym != sym.EOF){
	        	if ( t.sym == sym.error ) ++errors;

	            // print each token that we scanned
        		System.out.print(s.symbolToString(t) + "\n");
	            t = s.next_token();
	        }

	        System.out.println("\nLexical analysis completed");
	        System.out.println(errors + " errors were found.");

	    } catch (Exception e) {
	        // yuck: some kind of error in the compiler implementation
	        // that we're not expecting (a bug!)
	        System.err.println("Unexpected internal compiler error: " +
	                    e.toString());
	        // print out a stack dump
	        e.printStackTrace();
	    }

	    return errors == 0 ? 0 : 1;
	}

	public static int FileParser(String source_file)
	{
		int errors = 0;

	    try {
	        // create a parser on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
	        Reader in = new BufferedReader(new FileReader(source_file));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
		    // replace p.parse() with p.debug_parse() in next line to see trace of
		    // parser shift/reduce actions during parse
            root = p.parse();
            Program program = (Program)root.value;
            program.accept( new PrettyPrintVisitor() );

	        System.out.println("\nParsing completed");
	        System.out.println(errors + " errors were found.");
	    } catch (Exception e) {
	        // yuck: some kind of error in the compiler implementation
	        // that we're not expecting (a bug!)
	        System.err.println("Unexpected internal compiler error: " +
	                    e.toString());
	        // print out a stack dump
	        e.printStackTrace();
	    }

	    return errors == 0 ? 0 : 1;
	}

	public static int FileSymbolTable(String source_file)
	{
		int errors = 0;

	    try {
	        // create a parser on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
	        Reader in = new BufferedReader(new FileReader(source_file));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
		    // replace p.parse() with p.debug_parse() in next line to see trace of
		    // parser shift/reduce actions during parse
            root = p.parse();
            Program program = (Program)root.value;
            //program.accept( new PrettyPrintVisitor() );

            SymTableVisitor st = new SymTableVisitor();
            program.accept( st );
            st.print();

	        System.out.println("\nParsing completed");
	        System.out.println(errors + " errors were found.");
	    } catch (Exception e) {
	        // yuck: some kind of error in the compiler implementation
	        // that we're not expecting (a bug!)
	        System.err.println("Unexpected internal compiler error: " +
	                    e.toString());
	        // print out a stack dump
	        e.printStackTrace();
	    }

	    return errors == 0 ? 0 : 1;
	}

	public static int FileSemanticAnalysis(String source_file)
	{
		int errors = 0;

	    try {
	        // create a parser on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
	        Reader in = new BufferedReader(new FileReader(source_file));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
		    // replace p.parse() with p.debug_parse() in next line to see trace of
		    // parser shift/reduce actions during parse
            root = p.parse();
            Program program = (Program)root.value;
            //program.accept( new PrettyPrintVisitor() );

            SymTableVisitor st = new SymTableVisitor();
            program.accept( st );
            //st.print();
            errors += st.errors;

            //if ( errors == 0 ) // comment out to exit on symbol table errors
            {
	            SemanticAnalysisVisitor sa = new SemanticAnalysisVisitor();
	            sa.setSymtab(st.getSymtab());
	            program.accept( sa );
	            errors += sa.errors;
            }

	        System.out.println("\nCompiler completed");
	        System.out.println(errors + " errors were found.");
	    } catch (Exception e) {
	        // yuck: some kind of error in the compiler implementation
	        // that we're not expecting (a bug!)
	        System.err.println("Unexpected internal compiler error: " +
	                    e.toString());
	        // print out a stack dump
	        e.printStackTrace();
	    }

	    return errors == 0 ? 0 : 1;
	}
	public static int FileCodeGen(String source_file)
	{
		int errors = 0;

	    try {
	        // create a parser on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
	        Reader in = new BufferedReader(new FileReader(source_file));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
		    // replace p.parse() with p.debug_parse() in next line to see trace of
		    // parser shift/reduce actions during parse
            root = p.parse();
            Program program = (Program)root.value;
            //program.accept( new PrettyPrintVisitor() );

            SymTableVisitor st = new SymTableVisitor();
            program.accept( st );
            //st.print();
            errors += st.errors;

            //if ( errors == 0 ) // comment out to exit on symbol table errors
            {
	            CodeTranslateVisitor ctv = new CodeTranslateVisitor();
	            ctv.setSymtab(st.getSymtab());
	            program.accept( ctv );
	            errors += ctv.errors;
            }

	        //System.out.println("\nCompiler completed");
	        //System.out.println(errors + " errors were found.");
	    } catch (Exception e) {
	        // yuck: some kind of error in the compiler implementation
	        // that we're not expecting (a bug!)
	        System.err.println("Unexpected internal compiler error: " +
	                    e.toString());
	        // print out a stack dump
	        e.printStackTrace();
	    }

	    return errors == 0 ? 0 : 1;
	}
}
