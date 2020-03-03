package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class NewObject extends Exp {
  public Identifier i;
  
  public NewObject(Identifier ai, Location pos) {
    super(pos);
    i=ai;
    if (i != null) i.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
