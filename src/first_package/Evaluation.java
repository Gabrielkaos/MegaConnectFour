package first_package;

//we treat red player as white

public class Evaluation {

    static Constants cv = new Constants();
    static Functions hf = new Functions();
    static PvFunctions tt = new PvFunctions();

    private static final int men3 = 15;
    private static final int men2 = 10;
    private static final int men4 = 200;
//    private static final int center_score = 15;

    private static int eval_window(final Board board,int row,int col,int inc_row,int inc_col) {
        int i;
        int index;
        int[] window = new int[4];
        for (i = 0; i < 4; ++i) {
            index = hf.get_index(row , col);
            window[i] = board.board[index];

            row += inc_row;
            col += inc_col;
        }
        int score = 0;
        int red_found=0;
        int yellow_found=0;

        //red
        for (i = 0; i < 4; ++i) {
            if (window[i] == cv.red_player) {
                red_found++;
            } else if (window[i] == cv.yellow_player) {
                red_found=0;
                break;
            }
        }
        //yellow
        if(red_found==0) {
            for (i = 0; i < 4; ++i) {
                if (window[i] == cv.yellow_player) {
                    yellow_found++;
                } else if (window[i] == cv.red_player) {
                    yellow_found = 0;
                    break;
                }
            }
        }

        if(red_found==4){
            score+=men4;
        }else if(red_found==3){
            score+=men3;
        }else if(red_found==2){
            score+=men2;
        }

        if(yellow_found==4){
            score-=men4;
        }else if(yellow_found==3){
            score-=men3;
        }else if(yellow_found==2){
            score-=men2;
        }

        return score;
    }
    public static int evaluate_board(Board board){

        int tt_score = tt.load_eval(board.eval_table,board.pos_key);
        if(tt_score != cv.no_score){
            if(board.turn == cv.yellow_player)return -tt_score;
            return tt_score;
        }

        int score = 0;
        int row;
        int col;

        //vertical
        for(row=0;row<cv.board_rows-3;++row){
            for(col=0;col<cv.board_columns;++col){
                score+=eval_window(board,row,col,1,0);
            }
        }

        //horizontal
        for(row=0;row<cv.board_rows;++row){
            for(col=0;col<cv.board_columns-3;++col){
                score+=eval_window(board,row,col,0,1);
            }
        }

        //diagonal slope positive
        for(row=3;row<cv.board_rows;++row){
            for(col=0;col<cv.board_columns-4;++col){
                score+=eval_window(board,row,col,-1,1);
            }
        }

        //diagonal slope negative
        for(row=0;row<cv.board_rows-3;++row){
            for(col=0;col<cv.board_columns-3;++col){
                score+=eval_window(board,row,col,1,1);
            }
        }

//        for(row=0;row<cv.board_rows;++row){
//            if(board.board[cv.centers[row]]==cv.red_player){
//                score+=center_score;
//            }else if(board.board[cv.centers[row]]==cv.yellow_player){
//                score-=center_score;
//            }
//        }

        tt.save_eval(board.eval_table,score,board.pos_key);

        if(board.turn==cv.yellow_player)return -score;
        return score;
    }

}
