package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Display extends Statement {
  public Exp e;

  public Display(Exp re, Location pos) {
    super(pos);
    e=re; 
    if (e != null) e.setParent(this);
  }
  
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}

