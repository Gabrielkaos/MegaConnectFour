package first_package;

public class EvalTables {

    int num_entries;
    EvalEntry[] eval_table;

    EvalTables(int num_entries){
        init_eval_tables(num_entries);
    }

    public void init_eval_tables(int num_entries){
        this.num_entries = num_entries;
        eval_table = new EvalEntry[num_entries];
        for(int i=0;i<num_entries;++i){
            eval_table[i] = new EvalEntry();
        }
        clear_eval_tables();
        System.out.println("EvalTable initialized with "+num_entries+" entries");
    }

    public void clear_eval_tables(){
        for(int i=0;i<num_entries;++i){
            eval_table[i].score = 0;
            eval_table[i].pos_key = 0;
        }
    }

}
