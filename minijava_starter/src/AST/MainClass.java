package AST;

import AST.Visitor.Visitor;
import AST.Visitor.ObjectVisitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class MainClass extends ASTNode{
  public Identifier i1,i2;
  public Statement s;

  public MainClass(Identifier ai1, Identifier ai2, Statement as,
                   Location pos) {
    super(pos);
    i1=ai1; i2=ai2; s=as;
    if (i1 != null) i1.setParent(this);
    if (i2 != null) i2.setParent(this);
    if ( s != null)  s.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}

