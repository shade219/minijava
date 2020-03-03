package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class VarDeclList extends ASTNode {
   private List<VarDecl> list;

   public VarDeclList(Location pos) {
      super(pos);
      list = new ArrayList<VarDecl>();
   }

   public void add(VarDecl n) {
      list.add(n);
      if (n != null) n.setParent(this);
   }

   public VarDecl get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
