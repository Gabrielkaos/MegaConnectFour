package first_package;

public class Constants {

    final int board_rows = 8;
    final int board_columns = 9;

    final int red_player = 1;
    final int yellow_player = 2;
    final int empty = 0;

    final int no_move = 100;
    final int[] dirs = {-1,1,-board_columns,board_columns,-(board_columns-1),(board_columns-1),-(board_columns+1),(board_columns+1)};
    final int[] corners_right = {(board_columns)-1,(board_columns*2)-1,(board_columns*3)-1,(board_columns*4)-1,(board_columns*5)-1,(board_columns*6)-1,(board_columns*7)-1,(board_columns*8)-1};
    final int[] corners_left = {0,(board_columns),(board_columns*2),(board_columns*3),(board_columns*4),(board_columns*5),(board_columns*6),(board_columns*7),(board_columns*8)};
    final int[] centers = {(board_columns/2),(board_columns/2)+board_columns,(board_columns/2)+(board_columns*2),(board_columns/2)+(board_columns*3),(board_columns/2)+(board_columns*4),(board_columns/2)+(board_columns*5),(board_columns/2)+(board_columns*6),(board_columns/2)+(board_columns*7)};

    final int hf_beta = 1;
    final int hf_alpha = 2;
    final int hf_exact = 3;

    final int infinite = 40000;
    final int max_depth = board_columns*board_rows;
    final int is_mate = infinite - max_depth;

    final int no_score = infinite + 1;
}
