package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Program extends ASTNode {
  public MainClass m;
  public ClassDeclList cl;

  public Program(MainClass am, ClassDeclList acl, Location pos) {
    super(pos);
    m=am; cl=acl; 
    if (m != null) m.setParent(this);
    if (cl != null) cl.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
