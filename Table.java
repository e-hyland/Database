import java.util.*;
import java.io.*;

/*This class builds the tables from the file and is in charge of all logic
associated with changing a table or fetching data from a table. */

class Table{
  private Map<String, Record> records;
  private String[] type;
  private int pk;
  private Map<Integer, String> fks;

  Table(){
    this.fks = new HashMap<Integer, String>();
    this.records = new LinkedHashMap<String, Record>();
    this.pk = -1;
  }
  //Fill table with relevant records and add an integer primary key.
  boolean fill_fields(Scanner in){
    String s = in.nextLine();
    String fields[] = s.split("--");
    this.pk = indexpk(fields);//Check pk exists and get index
    if (this.pk==-1) return false;
    Record r = new Record(fields);
    insert(r, fields[this.pk]); //Insert field names as first record in table
    init_types(fields); //Insert types of fields into array
    if (insert_records(in)) return true;
    return false;
  }

  /*When a foreign key exists, this function will insert the primary key data
  from the reference table into the foreign key column of the original table*/
  void insertfk_data(String name, Table ref, Map.Entry<Integer, String> f){
    Map<String, Record> recs = ref.getdata();
    Iterator it = recs.entrySet().iterator();
    boolean isFirst = true;
    for (String key: this.records.keySet()){//Iterate through original table
      Map.Entry ent = (Map.Entry)it.next();//Iterate through reference table
      if ((!isFirst)){ //Ensures the column names are not overwritten
        Record r = (Record)ent.getValue();
        update(key, f.getKey(), r.get(ref.getpk()));//Insert new data
      }
      else isFirst=false;
    }
  }
  //Store the types in an array for reference for all other records
  void init_types(String fields[]){
    this.type = new String[fields.length];
    for (int i=0;i<fields.length;i++){
      this.type[i] = set_types(fields[i]);
      if (this.type[i].contains("FK")) this.fks.put(i, type[i]);
    }
  }

  private int indexpk(String fields[]){
    for(int i=0;i<fields.length;i++){
      if (fields[i].contains("{PK}")) return i;
    }
    return -1;
  }

  private boolean insert_records(Scanner in){
    while (in.hasNextLine()) {
      String row = in.nextLine();
      String data[] = row.split("--");
      for (int i=0;i<data.length;i++){
        if (!CheckType(data[i], this.type[i])) return false;
      }
      Record r = new Record(data);
      if (uniquepk(this.pk, data[this.pk])) insert(r, data[this.pk]);
      else return false;//If primary key is not unique, stop program.
    }
    return true;
  }

  private boolean uniquepk(int index, String str){
    for (Map.Entry<String, Record> recs: this.records.entrySet()){
      Record r = recs.getValue();
      if (r.get(index).equals(str)) return false;
    }
    return true;
  }
  //Add a new column to all records in table
  boolean addColumn(String name, int index){
    boolean isFirst = true;
    Map.Entry<String, Record> recs=this.records.entrySet().iterator().next();
    Record rec = recs.getValue();
    if (index>rec.row_length()) return false;
    for (Record r: this.records.values()){
      if (!isFirst) r.addCell(" ", index);//Insert empty cell
      else isFirst=false;
    }
    return true;
  }
  //Isolate type between "{}" and return type
  String set_types(String s){
    if (s.contains("{")&&s.contains("}")){
      int begin = s.indexOf('{');
      int end = s.indexOf('}');
      String type = s.substring(begin+1, end);
      return type;
    }
    else return "";
  }

  boolean CheckType(String s, String type){
    if (type.equals("STR")){
      if (s.matches("[A-Za-z0-9]+")) return true;
    }
    if (type.equals("INT")){
        if (s.matches("[0-9]+")) return true;
    }
    if (type.contains("FK")) return true;
    if (type.equals("PK")) return true;
    return false;
  }

  Record select(String key){
    Record r = this.records.get(key);
    return r;
  }

  void insert(Record r, String key){
    if (this.records.size()!=0){
      for (int i=0;i<r.row_length();i++){
        String s = r.get(i);
        if (i==this.pk) uniquepk(i, s);
        CheckType(s, this.type[i]);
      }
    }
    this.records.put(key, r);
  }

  void delete(String key){
    this.records.remove(key);
  }

  boolean update(String key, int col, String data){
    Record r = this.records.get(key);
    if (col >= r.row_length()) return false;
    if (col==this.pk) uniquepk(col, data);
    if (!CheckType(data, this.type[col])) return false;
    r.insert(data, col);
    return true;
  }

  Map<String, Record> getdata(){
    return this.records;
  }

  Map<Integer, String> getfks(){
    return this.fks;
  }

  int getpk(){
    return this.pk;
  }

  int get_size(){
    return this.records.size();
  }

  public void test(Table t, Record r, String fields[]){
    String[] types = new String[3];
    for (int i=0;i<3;i++)types[i]="{STR}";
    init_types(types);
    assert(this.type[0].equals("STR"));
    assert(this.type.length==3);
    t.insert(r,"id");
    assert(select("id")==r);
    t.delete("id");
    assert(select("id")==null);
    t.insert(r,"id");
    assert(t.update("id", 1, "firstname")==true);
    assert(t.update("id", 4, "firstname")==false);
    assert(t.get_size()==1);
    assert(t.getdata()==this.records);
    assert(t.getfks()==this.fks);
    assert(t.getpk()==this.pk);
    assert(t.addColumn("pet", 3)==true);
    assert(t.addColumn("lastname", 6)==false);
    assert(t.indexpk(fields)==3);
    fields[3] = "uniqueid";
    assert(t.indexpk(fields)==-1);
    try {
      File f = new File("nopk.txt");
      Scanner in = new Scanner(f);
      Table t1 = new Table();
      assert(t1.fill_fields(in)==false);
      File f2 = new File("pk.txt");
      Scanner s = new Scanner(f2);
      Table t2 = new Table();
      assert(t2.fill_fields(s)==true);
      assert(t2.insert_records(s)==true);
      File file = new File("uniquepk.txt");
      Scanner str = new Scanner(file);
      Table t3 = new Table();
      assert(t3.fill_fields(str)==false);
      assert(t3.uniquepk(0, "4")==true);
      assert(t3.uniquepk(0, "3")==false);
    } catch(FileNotFoundException e){throw new Error(e);}
    assert(set_types("name{STR}").equals("STR"));
    assert(set_types("name{PK}").equals("PK"));
    assert(set_types("name{TEST}").equals("TEST"));
    assert(set_types("name{TEST").equals(""));
    assert(set_types("nameTEST{").equals(""));
    assert(set_types("nameTEST}").equals(""));
    assert(CheckType("emily", "STR")==true);
    assert(CheckType("emily5", "STR")==true);
    assert(CheckType("emily*", "STR")==false);
    assert(CheckType("7862", "INT")==true);
    assert(CheckType("-7862", "INT")==false);
    assert(CheckType("*3726jh", "PK")==true);
    assert(CheckType("*3726jh", "FK")==true);
  }

  public static void main (String[] args){
     Table t = new Table();
     String fields[]={"id", "name", "age", "uniqueid{PK}"};
     Record r = new Record(fields);
     boolean testing = false;
     assert(testing=true);
     if (testing) {t.test(t, r, fields);}
  }
}
