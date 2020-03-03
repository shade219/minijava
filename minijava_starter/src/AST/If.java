package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class If extends Statement {
  public Exp e;
  public Statement s1,s2;

  public If(Exp ae, Statement as1, Statement as2, Location pos) {
    super(pos);
    e=ae; s1=as1; s2=as2;
    if (e != null) e.setParent(this);
    if (s1 != null) s1.setParent(this);
    if (s2 != null) s2.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}

