package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Formal extends ASTNode{
  public Type t;
  public Identifier i;
 
  public Formal(Type at, Identifier ai, Location pos) {
    super(pos);
    t=at; i=ai;
    if (t != null) t.setParent(this);
    if (i != null) i.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
