import java.util.*;
import java.io.*;

/* This class reads in the files containing the tables.
This class knows about all other classes, it is the control centre.
To run the program, enter a file path on the command line to the directory
where your tables are stored in .txt files. */

public class Database{
  private Map<String, Table> tables;

  Database(){
    tables = new HashMap<String, Table>();
  }

  private boolean readfile(File f){
    try {
      Scanner in = new Scanner(f);
      String title = in.nextLine();
      if (title.contains("--")) return false;//Ensures title exists before fields
      Table t = new Table();
      this.tables.put(title, t);//Put title of table in map of tables
      if (!t.fill_fields(in)) return false;//Insert records into table
      if (foreignkeys(t)) return true; //Check if any foreign keys exist
      in.close();
    } catch (FileNotFoundException e) { throw new Error(e); }
    return true;
  }
  //Select all .txt files in chosen directory
  private boolean choosefiles(String path){
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles==null) return false;

    for (int i = 0; i < listOfFiles.length; i++) {
      File file = listOfFiles[i];
      if (file.getName().endsWith(".txt")) {
        if (!readfile(file)) return false;
      }
    }
    return true;
  }

  private boolean foreignkeys(Table t){
    Map<Integer, String> foreignkeys = t.getfks();
    if (foreignkeys.size()==0) return true;
    for (Map.Entry<Integer, String> f: foreignkeys.entrySet()){
      String name = f.getValue().substring(3, f.getValue().length());
      if (this.tables.containsKey(name)) {//Check that table exists
        t.addColumn(name, f.getKey());
        Table ref = this.tables.get(name);//Get reference table
        if (ref.get_size()!=t.get_size()) return false;
        t.insertfk_data(name, ref, f);//Insert primary key data into table
      }
      else return false;
    }
    return true;
  }
  //Write data back to file
  private void save_data(String file, Output out){
   PrintWriter output = null;
   try {
     output = new PrintWriter(file);
     for (Map.Entry<String, Table> tabs: tables.entrySet()){
       String title = tabs.getKey();
       Table t = tabs.getValue();
       Map<String, Record> recs = t.getdata();
       out.save(t, title, output, recs);
     }
     output.close();
   } catch(FileNotFoundException e){throw new Error(e);}
  }

  private void print_data(Output out){
   for (Map.Entry<String, Table> tabs: tables.entrySet()){
     String title = tabs.getKey();
     Table t = tabs.getValue();
     Map<String, Record> recs = t.getdata();
     out.print(t, title, recs);
   }
  }

  public void test(){
   assert(choosefiles("/home/emily/Desktop/folder")==false);
   assert(choosefiles("/home/emily/Java/Wk5/Database")==true);
   File f = new File("readfile.txt");
   assert(readfile(f)==true);
   Table t = new Table();
   String[] cols = {"id{FK=TableOne}", "name", "age"};
   Record r = new Record(cols);
   assert(foreignkeys(t)==true);
   t.insert(r,"id");
   t.init_types(cols);
   assert(foreignkeys(t)==false);
   Table tab = new Table();
   String[] columns = {"id{FK=TableFour}", "name", "age"};
   Record rec = new Record(columns);
   tab.insert(rec,"id");
   tab.init_types(columns);
   assert(foreignkeys(tab)==false);
  }

  public static void main (String[] args){
   Output out = new Output();
   Database d = new Database();
   boolean success = false;
   boolean testing = false;
   assert(testing=true);
   if (testing) d.test();
   else {success = d.choosefiles(args[0]);}
   if (success){
     d.print_data(out);
     d.save_data("writeback.txt", out);
   }
  }
}
/*Below are the querying methods I wrote for my original extension. They will
no longer work (and are not at all tidy) as some of my methods have changed
but I have included them as it took a significant amount of time and effort
to program. */

   /*
   boolean addColumn(String words[]){
     if (words.length!=4) {
       System.err.println("Invalid query.");
       return false;
     }
     if (this.tables.containsKey(words[1])){
       Table t = this.tables.get(words[1]);
       t.addColumn(words[2], words[3]);
       return true;
     }
     else {
       System.err.println("Invalid table.");
       return false;
     }
   }

   boolean delete(String words[]){
     if (words.length!=)||wo {
       System.err.println("Invalid query.");
       return false;
     }
     if (words[1].equals("record")&&this.tables.containsKey(words[2])){
       Table t = this.tables.get(words[2]);
       t.delete(words[3]);
       return true;
     }
     if (words[1].equals("table")&&this.tables.containsKey(words[2])){
       Table t = this.tables.get(words[2]);
       if (t==null){
         System.err.println("Invalid table.");
         return false;
       }
       del_table(words[2],t);
       return true;
     }
     else{
       System.err.println("Invalid query.");
       return false;
     }
   }


   //clear operation for data structure, or delete altogether
   void del_table(String title, Table t){
     Map<String, Record> records = t.getdata();
     Iterator<Map.Entry<String,Record>> it = records.entrySet().iterator();
     while(it.hasNext()){
       Map.Entry<String, Record> entry = (Map.Entry<String, Record>) it.next();
       String k = entry.getKey();
       t.delete(k);
     }
     this.tables.remove(title);
   }

   boolean select(String words[]){
     if (words.length!=4) {
       System.err.println("Invalid query.");
       return false;
     }
     Table t=null;
     if (words[2].equals("from")){
       t = this.tables.get(words[3]);
       if (t==null){
         System.err.println("Invalid table.");
         return false;
       }
     }
     else {
       System.err.println("Invalid query.");
       return false;
     }
     Map<String, Record> recs = t.getdata();
     int i = t.get_key(words[1]);
     if (i==-1) {
       System.err.println("Invalid column.");
       return false;
     }
     for (Record r: recs.values()) System.out.println(t.get_col(i, r));
     return t*/
