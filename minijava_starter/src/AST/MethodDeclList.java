package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class MethodDeclList extends ASTNode {
   private List<MethodDecl> list;

   public MethodDeclList(Location pos) {
      super(pos);
      list = new ArrayList<MethodDecl>();
   }

   public void add(MethodDecl n) {
      list.add(n);
      if (n != null) n.setParent(this);
   }

   public MethodDecl get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
