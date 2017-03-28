import java.util.*;
import java.io.*;

/* This class is in charge of all logic associated with records. */

class Record {
  private String[] cells;

  Record(String[] data){
    this.cells = data;
  }

  String[] getrecord(){
    return this.cells;
  }

  boolean addCell(String column, int index){
    if (index>row_length()) return false;
    this.cells = Arrays.copyOf(this.cells, this.cells.length+1);
    for (int i=this.cells.length-1;i>index-1;i--){
      this.cells[i]=this.cells[i-1];
    }
    this.cells[index]=column;
    return true;
  }

  int row_length(){
    return this.cells.length;
  }

  String get(int i){
    if (i>=this.cells.length) return "null";
    return this.cells[i];
  }

  int index(String field){
    for (int i=0;i<this.cells.length;i++){
      if (this.cells[i].equals(field)) return i;
    }
    return (-1);
  }

  boolean insert(String data, int i){
    if (i>=this.cells.length) return false;
    this.cells[i]=data;
    return true;
  }

  public void test(){
     assert(get(1)=="bc234");
     assert(get(0)=="ab123");
     assert(get(3)=="null");
     assert(row_length()==3);
     assert(insert("gh567", 2)==true);
     assert(insert("gh567", 3)==false);
     insert("hello", 1);
     assert(cells[1]=="hello");
     assert(index("ab123")==0);
     assert(index("783624")==-1);
     assert(getrecord()==this.cells);
     assert(addCell("java", 3)==true);
     assert(this.cells[3]=="java");
     assert(addCell("java", 5)==false);
  }

  public static void main (String[] args){
     String[] cells = {"ab123", "bc234", "cd345"};
     Record r = new Record(cells);
     boolean testing = false;
     assert(testing=true);
     if (testing) {r.test();}
  }
}
