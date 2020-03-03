package AST;

import java.util.List;

import AST.Visitor.ObjectVisitor;

import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class ExpList extends ASTNode{
   private List<Exp> list;

   public ExpList(Location pos) {
      super(pos);
      list = new ArrayList<Exp>();
   }

   public void add(Exp n) {
      list.add(n);
      if (n != null) n.setParent(this);
   }

   public Exp get(int i)  { 
      return list.get(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
