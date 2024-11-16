package first_package;

public class PvFunctions {
    Constants cv = new Constants();

    public boolean move_exists(Board board,int move){

        for(int i:board.get_legal_moves()){
            if(i != cv.no_move && i==move){
                return true;
            }
        }

        return false;
    }
    public int get_pv_line(Board board,int depth){
        int move = probe_move(board.hash_table ,board.pos_key);
        int count = 0;

        while(move != cv.no_move && count<depth){
            if(move_exists(board,move)){
                board.make_move(move);
                board.pv_array[count++]=move;
            }else {
                break;
            }
            move = probe_move(board.hash_table ,board.pos_key);
        }

        while(board.ply > 0){
            board.undo_move();
        }

        return count;
    }
    public int probe_move(final HashTables hash_tables,long pos_key){
        int index = (int) (pos_key % hash_tables.num_entries);

        if(hash_tables.pv_table[index].pos_key==pos_key){
            return hash_tables.pv_table[index].move;
        }

        return cv.no_move;
    }

    public void save_pv(HashTables hash_tables,int flag,int score,int move,int depth,long pos_key,int ply){
        int index = (int) (pos_key % hash_tables.num_entries);

        score = score_to_pv(score,ply);

        hash_tables.pv_table[index].move = move;
        hash_tables.pv_table[index].pos_key = pos_key;
        hash_tables.pv_table[index].depth = depth;
        hash_tables.pv_table[index].score = score;
        hash_tables.pv_table[index].flag = flag;

    }
    public boolean load_pv(final HashTables hash_tables,PvEntry pv_info,long pos_key){
        int index = (int) (pos_key % hash_tables.num_entries);

        if(hash_tables.pv_table[index].pos_key == pos_key){
            pv_info.flag = hash_tables.pv_table[index].flag;
            pv_info.move = hash_tables.pv_table[index].move;
            pv_info.depth = hash_tables.pv_table[index].depth;
            pv_info.score = hash_tables.pv_table[index].score;
            return true;
        }

        return false;
    }

    public void save_eval(EvalTables eval_tables,int score,long pos_key){
        int index = (int) (pos_key % eval_tables.num_entries);

        eval_tables.eval_table[index].pos_key = pos_key;
        eval_tables.eval_table[index].score = score;

    }
    public int load_eval(final EvalTables eval_tables,long pos_key){
        int index = (int) (pos_key % eval_tables.num_entries);

        if(eval_tables.eval_table[index].pos_key == pos_key){
            return eval_tables.eval_table[index].score;
        }
        return cv.no_score;
    }

    public int score_to_pv(int score,int ply){
        if(score > cv.is_mate)score+=ply;
        else if(score < -cv.is_mate)score-=ply;

        return score;
    }
    public int score_from_pv(int score,int ply){
        if(score > cv.is_mate)score-=ply;
        else if(score < -cv.is_mate)score+=ply;

        return score;
    }


}
