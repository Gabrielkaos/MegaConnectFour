package first_package;

public class HashTables {

    int num_entries;
    PvEntry[] pv_table;

    HashTables(int num_entries){
        init_hash_tables(num_entries);
    }

    public void init_hash_tables(int num_entries){
        this.num_entries = num_entries;
        pv_table = new PvEntry[num_entries];
        for(int i=0;i<num_entries;++i){
            pv_table[i] = new PvEntry();
        }
        clear_hash_tables();
        System.out.println("HashTable initialized with "+num_entries+" entries");
    }

    public void clear_hash_tables(){
        for(int i=0;i<num_entries;++i){
            pv_table[i].move = 100;
            pv_table[i].score = 0;
            pv_table[i].depth = 0;
            pv_table[i].pos_key = 0;
            pv_table[i].flag = 0;
        }
    }

}
