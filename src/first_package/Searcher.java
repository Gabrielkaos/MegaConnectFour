package first_package;


public class Searcher {
    Constants cv = new Constants();
    PvFunctions tt = new PvFunctions();
    Functions hf = new Functions();

    private void order_moves(Move[] moves, int move_num){
        Move temp;
        int bestScore=0;
        int bestNum=move_num;
        int index;

        for(index=move_num;index<cv.board_columns;++index){
            if(moves[index].score>bestScore){
                bestScore=moves[index].score;
                bestNum=index;
            }
        }

        temp=moves[move_num];
        moves[move_num] = moves[bestNum];
        moves[bestNum] = temp;
    }
    private Move[] score_moves(final Board board,int[] legal_moves,int pv_move){
        Move[] moves = new Move[cv.board_columns];

        for(int i=0;i<cv.board_columns;++i){
            Move j = new Move();
            if(legal_moves[i] != cv.no_move){
                if(legal_moves[i]==pv_move){
                    j.col=legal_moves[i];
                    j.score=2000000;
                    moves[i]=j;
                }else if(legal_moves[i]==board.search_killers[0][board.ply]){
                    j.col=legal_moves[i];
                    j.score=1000000;
                    moves[i]=j;
                }else if(legal_moves[i]==board.search_killers[1][board.ply]){
                    j.col=legal_moves[i];
                    j.score=900000;
                    moves[i]=j;
                }else{
                    j.col = legal_moves[i];
                    j.score = board.search_history[board.turn][legal_moves[i]];
                    j.score -= Math.abs(cv.board_columns/2 - j.col);
                    moves[i] = j;
                }
                continue;
            }
            j.col=legal_moves[i];
            j.score=0;
            moves[i]=j;
        }

        return moves;
    }
    private void should_we_stop(SearchInfo info){
        if((info.mate_limit && info.nodes>=100000000) || (!info.mate_limit && info.stop_time != 0 &&
        ((System.currentTimeMillis()/1000)-info.start_time)>=info.stop_time)){
            info.stop_search=true;
        }
    }
    private void init_searcher(Board board,SearchInfo info,int stop_time){

        for(int i=0;i<3;++i){
            for(int j=0;j<cv.board_columns;++j){
                board.search_history[i][j]=0;
            }
        }

        for(int i=0;i<2;++i){
            for(int j=0;j<cv.max_depth;++j){
                board.search_killers[i][j] = cv.no_move;
            }
        }

        board.ply = 0;
        info.nodes=0;
        info.stop_search=false;
        info.start_time = (int) (System.currentTimeMillis()/1000);
        info.stop_time = stop_time;
    }
    private int fix_scores(int score){
        if(score >= cv.is_mate){
            score = ((cv.infinite - score + 1) / 2);
        }else if(score <= -cv.is_mate){
            score = (-(score + cv.infinite) / 2);
        }
        return score;
    }

    private int alpha_beta(Board board,SearchInfo info,int depth,int alpha,int beta){
        boolean root_node = board.ply==0;
        boolean pv_node = alpha != (beta - 1);

        if(depth<=0)return Evaluation.evaluate_board(board);

        if((info.nodes & 1023)==0)should_we_stop(info);
        info.nodes++;

        if(!root_node){
            int r_alpha = Math.max(alpha , -cv.infinite + board.ply);
            int r_beta = Math.min(beta , cv.infinite - board.ply - 1);
            if(r_alpha>=r_beta)return r_alpha;
        }

        PvEntry pv_info = new PvEntry();
        boolean is_hit = tt.load_pv(board.hash_table,pv_info,board.pos_key);

        if(is_hit){
            if(pv_info.depth >= depth && !pv_node){
                pv_info.score = tt.score_from_pv(pv_info.score,board.ply);
                if(pv_info.flag==cv.hf_exact ||
                        (pv_info.flag== cv.hf_alpha && pv_info.score <= alpha) ||
                        (pv_info.flag== cv.hf_beta && pv_info.score >= beta)){
                    return pv_info.score;
                }
            }
        }

        int[] legal_moves = board.get_legal_moves();

        if(board.is_terminal_state(board.turn)){
            return cv.infinite - board.ply;
        }else if(board.is_terminal_state(hf.opposite_of(board.turn))){
            return -cv.infinite + board.ply;
        }else if(legal_moves.length==0){
            return 0;
        }

        Move[] moves = score_moves(board,legal_moves,pv_info.move);

        int old_alpha = alpha;
        int best_move = cv.no_move;
        int best_score = -cv.infinite;
        int legal = 0;
        boolean found_pv = false;
        int score;
        int move;

        for(int i=0;i<cv.board_columns;++i){
            order_moves(moves,i);
            move = moves[i].col;

            if(move==cv.no_move)continue;
            if(!board.make_move(move)){
                continue;
            }

            if(found_pv){
                score = -alpha_beta(board,info,depth-1,-alpha - 1,-alpha);
                if(score > alpha && score < beta){
                    score = -alpha_beta(board,info,depth-1,-beta,-alpha);
                }
            }else {
                score = -alpha_beta(board,info,depth-1,-beta,-alpha);
            }

            legal++;
            board.undo_move();

            if(info.stop_search)return 0;

            if(score > best_score){
                best_score = score;
                best_move = move;

                if(score > alpha){
                    if(score >= beta){
                        board.search_killers[1][board.ply]=board.search_killers[0][board.ply];
                        board.search_killers[0][board.ply]=move;
                        tt.save_pv(board.hash_table,cv.hf_beta,beta,move,depth,board.pos_key,board.ply);
                        return beta;
                    }
                    board.search_history[board.turn][move]+=depth;
                    alpha = score;
                    found_pv = true;
                }

            }

        }

        if(legal==0){
            return 0;
        }

        if(old_alpha != alpha) tt.save_pv(board.hash_table, cv.hf_exact , best_score,best_move,depth,board.pos_key,board.ply);
        else tt.save_pv(board.hash_table, cv.hf_alpha , alpha,best_move,depth,board.pos_key,board.ply);

        return alpha;
    }
    public int iterative_deepening(Board board,SearchInfo info,int stop_time,int depth){
        init_searcher(board,info,stop_time);

        int best_move = cv.no_move;
        int best_score;
        boolean already_mate;

        for(int current_depth=1;current_depth<=cv.max_depth;++current_depth){

            best_score = alpha_beta(board,info,current_depth,-cv.infinite , cv.infinite);

            if(info.stop_search)break;

            int count = tt.get_pv_line(board,current_depth);
            best_move = board.pv_array[0];

            if(info.post) {
                already_mate = Math.abs(best_score) >= cv.is_mate;
                String score = already_mate ? " mate=" + fix_scores(best_score) : " score=" + best_score;
                System.out.print("depth=" + current_depth + score + " nodes=" + info.nodes);
                System.out.print(" pv=");
                for (int i = 0; i < count; ++i) {
                    System.out.print(board.pv_array[i] + " ");
                }
                System.out.println();
            }else{
                if((current_depth % 2)==0){
                    System.out.println("calculated "+(current_depth / 2)+" moves ahead");
                }
            }

            if(info.mate_limit && Math.abs(best_score) >= cv.is_mate){
                break;
            }

            if (!info.mate_limit && info.stop_time == 0 && current_depth>=depth){
                break;
            }
        }
        if(!info.post) System.out.println("bestmove="+best_move);
        return best_move;
    }


}
