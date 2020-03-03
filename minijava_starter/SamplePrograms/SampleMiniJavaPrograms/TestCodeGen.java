class TestCodeGen{
  public static void main(String[] args){
    System.out.println(new TS().Test(5));
  }
}

class TS{
  int y;
  int[] z;
  public int Test(int a){
    int i;
    y = 2;
    z = new int[2];
    z[0] = 0;
    z[1] = 1;
    y = z[0];
    System.out.println(y);
    y = z[1];
    System.out.println(y);
    return 9;
  }
}
