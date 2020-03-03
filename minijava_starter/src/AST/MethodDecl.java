package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class MethodDecl extends ASTNode {
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public StatementList sl;
  public Exp e;

  public MethodDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl, 
                    StatementList asl, Exp ae, Location pos) {
    super(pos);
    t=at; i=ai; fl=afl; vl=avl; sl=asl; e=ae;
    if (t != null) t.setParent(this);
    if (i != null) i.setParent(this);
    if (fl != null) fl.setParent(this);
    if (vl != null) vl.setParent(this);
    if (sl != null) sl.setParent(this);
    if (e != null) e.setParent(this);
  }
 
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
