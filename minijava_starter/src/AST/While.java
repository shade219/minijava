package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class While extends Statement {
  public Exp e;
  public Statement s;

  public While(Exp ae, Statement as, Location pos) {
    super(pos);
    e=ae; s=as; 
    if (e != null) e.setParent(this);
    if (s != null) s.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}

