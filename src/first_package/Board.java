package first_package;

public class Board {

    Constants cv = new Constants();
    Functions hf = new Functions();

    int[] board;
    int ply;
    int his_ply;
    int turn;
    int[] history;
    long[][] piece_keys;
    long side_key;
    long pos_key;
    int[] num_piece;

    //hashtables
    EvalTables eval_table = new EvalTables(1000000);
    HashTables hash_table = new HashTables(2000000);
    int[] pv_array = new int[cv.board_columns*cv.board_rows];

    //search heuristics
    int[][] search_history = new int[3][cv.board_columns];
    int[][] search_killers = new int[2][cv.max_depth];

    Board(){
        init_hash_keys();
        init_board();
    }

    public boolean make_move(int col){
        int row = hf.get_next_row(board,col);
        if(row == cv.no_move){
            return false;
        }
        int index = hf.get_index(row,col);

        history[his_ply] = col;
        hash_pce(turn,index);
        add_piece(index);
        ply++;
        his_ply++;
        flip_side();
        hash_side();
        return true;
    }
    public void undo_move(){
        ply--;
        his_ply--;
        int col = history[his_ply];
        int row = hf.get_not_row(board,col);
        int index = hf.get_index(row,col);

        int piece = board[index];
        remove_piece(index);
        hash_pce(piece,index);
        flip_side();
        hash_side();

    }
    public int[] get_legal_moves(){
        int row;
        int[] moves = new int[cv.board_columns];
        for(int i = 0;i<cv.board_columns;++i){
            row = hf.get_next_row(board,i);
            if(row != cv.no_move){
                moves[i] = i;
                continue;
            }
            moves[i] = cv.no_move;
        }
        return moves;
    }
    private void add_piece(int index){
        board[index]=turn;
        num_piece[turn]++;
    }
    private void remove_piece(int index){
        num_piece[board[index]]--;
        board[index]= cv.empty;
    }
    public void init_board(){
        board = new int[cv.board_rows*cv.board_columns];
        ply = 0;
        his_ply = 0;

        turn = cv.red_player;
        history = new int[cv.board_columns*cv.board_rows];

        num_piece = new int[3];

        pos_key = generate_pos_key();
    }
    private void init_hash_keys(){
        piece_keys = new long[3][cv.board_columns*cv.board_rows];
        for(int i=0;i<3;++i){
            for(int sq=0;sq<cv.board_columns*cv.board_rows;++sq){
                piece_keys[i][sq]=hf.rand_64();
            }
        }
        side_key = hf.rand_64();
    }
    public long generate_pos_key(){
        long key = 0;
        int sq;
        for(sq = 0;sq<cv.board_rows*cv.board_columns;++sq){
            if(board[sq] != cv.empty){
                key ^= piece_keys[board[sq]][sq];
            }
        }

        if(turn== cv.red_player){
            key^=side_key;
        }
        return key;
    }
    private void hash_side(){
        pos_key^=side_key;
    }
    private void hash_pce(int color,int index){
        pos_key^=piece_keys[color][index];
    }
    private void flip_side(){
        turn = turn==cv.red_player ? cv.yellow_player : cv.red_player;
    }
    public boolean is_terminal_state(int player){
        if(num_piece[player]<4)return false;
        int element;
        int index;
        int i,j,s;
        int dir;
        int index2;
        int found_same;
        boolean breaker;
        for(index = (cv.board_columns*cv.board_rows) - 1;index>=0;--index){
            element = board[index];

            if(element==player){
                for(i=0;i<8;++i){
                    dir = cv.dirs[i];
                    index2=index;
                    found_same = 0;
                    for(j = 0;j<3;++j){
                        index2+=dir;
                        if(index2 < 0 || index2 >= (cv.board_rows* cv.board_columns))break;
                        if(board[index2]==player){
                            breaker = false;
                            if(dir == 1 || dir == -((cv.board_columns)-1) || dir == (cv.board_columns+1)){
                                for(s = 0;s<cv.board_rows;++s){
                                    if(cv.corners_left[s]==index2){
                                        breaker = true;
                                        break;
                                    }
                                }
                            }
                            else if(dir == -1 || dir == (cv.board_columns-1) || dir == -(cv.board_columns+1)){
                                for(s = 0;s<cv.board_rows;++s){
                                    if(cv.corners_right[s]==index2){
                                        breaker = true;
                                        break;
                                    }
                                }
                            }
                            if(breaker)break;
                            found_same++;
                        }else{
                            break;
                        }
                    }
                    if(found_same==3)return true;
                }
            }

        }
        return false;
    }
    public void print_board(){
        int index;
        for(int row = 0;row<cv.board_rows;++row){
            for(int col=0;col<cv.board_columns;++col){
                index = hf.get_index(row,col);
                if(board[index] == cv.red_player){
                    System.out.print("O ");
                }else if(board[index] == cv.yellow_player){
                    System.out.print("X ");
                }else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
