package first_package;

import java.security.SecureRandom;

public class Functions {

    Constants cv = new Constants();

    public long rand_64(){
        SecureRandom secure_random = new SecureRandom();
        return Math.abs(secure_random.nextLong());
    }
    public int opposite_of(int turn){
        return turn==cv.red_player ? cv.yellow_player : cv.red_player;
    }
    public int get_index(int row,int col){
        return col + row * cv.board_columns;
    }
    public int get_next_row(final int[] board,int col){
        int index;
        for(int i = cv.board_rows - 1;i>=0;--i){
            index = get_index(i,col);
            if(board[index]==cv.empty){
                return i;
            }
        }
        return cv.no_move;
    }
    public int get_not_row(final int[] board,int col){
        int index;
        for(int i = 0;i<cv.board_rows;++i){
            index = get_index(i,col);
            if(board[index] != cv.empty){
                return i;
            }
        }
        return cv.no_move;
    }

}
