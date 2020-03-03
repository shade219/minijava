package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ClassDeclSimple extends ClassDecl {
  public Identifier i;
  public VarDeclList vl;  
  public MethodDeclList ml;
 
  public ClassDeclSimple(Identifier ai, VarDeclList avl, MethodDeclList aml,
                         Location pos) {
    super(pos);
    i=ai; vl=avl; ml=aml;
    if (i != null) i.setParent(this);
    if (vl != null) vl.setParent(this);
    if (ml != null) ml.setParent(this);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Object accept(ObjectVisitor v) {
    return v.visit(this);
  }
}
