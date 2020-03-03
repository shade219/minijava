package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ClassDeclList extends ASTNode{
   private List<ClassDecl> list;

   public ClassDeclList(Location pos) {
      super(pos);
      list = new ArrayList<ClassDecl>();
   }

   public void add(ClassDecl n) {
      list.add(n);
      if (n != null) n.setParent(this);
   }

   public ClassDecl get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
