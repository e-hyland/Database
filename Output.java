import java.util.*;
import java.io.*;

/* This class is in charge of outputting the database to the screen or writing
back to a file as required */

class Output{

  boolean print(Table t, String title, Map<String, Record> recs){
    System.out.println(title);
    if (recs.size()==0) return false;
    for (Record r: recs.values()){
      String[] row = r.getrecord();
      for (String cell: row){
        System.out.printf("%15s"+" | ", cell);
      }
      System.out.println();
    }
    System.out.println();
    return true;
  }

  boolean save(Table t, String title, PrintWriter output, Map<String, Record> recs){
    output.println(title);
    if (recs.size()==0) return false;
    write_tables(recs, output);
    return true;
  }

  private void write_tables(Map<String,Record> recs, PrintWriter output){
    for (Record r: recs.values()){
      String[] row = r.getrecord();
      for (int i=0;i<row.length;i++){
        output.print(row[i]+"--");
      }
      output.println();
    }
  }

  public void test(Table t, Record r){
    PrintWriter output=null;
    try { output = new PrintWriter("writeback.txt");
    } catch (Exception e){throw new Error(e);}
    t.insert(r, "id");
    assert(save(t, "Table One", output, t.getdata())==true);
    t.delete("id");
    assert(save(t, "Table One", output, t.getdata())==false);
    assert(print(t, "TableOne", t.getdata())==false);
    t.insert(r, "id");
    assert(print(t, "TableOne", t.getdata())==true);
  }

  public static void main (String[] args){
    Table t = new Table();
    Output o = new Output();
    String fields[]={"id", "name", "age"};
    Record r = new Record(fields);
    boolean testing = false;
    assert(testing=true);
      if (testing) {o.test(t, r);}
  }

}
