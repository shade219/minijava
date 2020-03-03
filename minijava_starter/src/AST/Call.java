package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class Call extends Exp {
  public Exp e;
  public Identifier i;
  public ExpList el;
  
  public Call(Exp ae, Identifier ai, ExpList ael, Location pos) {
    super(pos);
    e=ae; i=ai; el=ael;
    if (e != null)  i.setParent(this);
    if (i != null)  e.setParent(this);
    if (el != null) el.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
