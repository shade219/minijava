package AST;

import AST.Visitor.ObjectVisitor;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ClassDeclExtends extends ClassDecl {
  public Identifier i;
  public Identifier j;
  public VarDeclList vl;  
  public MethodDeclList ml;
 
  public ClassDeclExtends(Identifier ai, Identifier aj, 
                          VarDeclList avl, MethodDeclList aml,
                          Location pos) {
    super(pos);
    i=ai; j=aj; vl=avl; ml=aml;
    if (i != null)  i.setParent(this);
    if (j != null)  j.setParent(this);
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
