package first_package;

import java.util.*;

public class ConnectFourCLI {
    static Scanner scanner = new Scanner(System.in);
    static Constants cv = new Constants();
    static Searcher searcher = new Searcher();

    private static boolean is_terminal_node(Board board){
        int count=0;
        for(int i:board.get_legal_moves()){
            if(i==cv.no_move){
                count++;
            }
        }
        if (count==cv.board_columns)return true;
        return board.is_terminal_state(cv.red_player) || board.is_terminal_state(cv.yellow_player);
    }
    public static void run(Board board1,SearchInfo info){
        System.out.println("ConnectConnectFour Engine");
        System.out.println("type 'help' for commands\n");
        int depth = 5;
        int time = 5;
        info.post=true;
        info.mate_limit=false;
        int ai_side = cv.empty;

        while(true){
            if(is_terminal_node(board1)){

                board1.print_board();

                if(board1.is_terminal_state(cv.red_player))System.out.println("WINNER IS O");
                else if(board1.is_terminal_state(cv.yellow_player))System.out.println("WINNER IS X");
                else System.out.println("DRAWN");
                break;
            }
            if(board1.turn==ai_side){
                int move = searcher.iterative_deepening(board1,info,time,depth);
                board1.make_move(move);
                board1.ply=0;
                System.out.println("ai made="+move);
                continue;
            }

            System.out.print("=>");
            String input = scanner.nextLine();

            if(input.equals("help")){
                System.out.println("depth x - set ai depth to x");
                System.out.println("time x - set ai thinking time to x");
                System.out.println("view - view settings");
                System.out.println("print - print board");
                System.out.println("post - print computer thinking");
                System.out.println("nopost - don't print computer thinking");
                System.out.println("mate - stop search only if found mate");
                System.out.println("nomate - disable mate finder mode");
                System.out.println("hashtable x - set hashtable entries min 1000 max 3000000");
                System.out.println("clear - clear hashtable");
                System.out.println("evaltable x - set evaltable entries min 1000 max 3000000");
                System.out.println("cleareval - clear evaltable");
                System.out.println("evaluate - count estimated evaluation");
                System.out.println("go - let ai make a move");
                System.out.println("force - stop ai");
                System.out.println("undo - undo latest move");
                System.out.println("quit - quit program");
                System.out.println("for moves just type the columns eg. 0 or 1");
                System.out.println();
            }
            else if(input.startsWith("depth")){
                depth = Integer.parseInt(input.split(" ")[1]);
                if (depth > cv.max_depth || depth < 0){
                    depth= cv.max_depth;
                }
            }
            else if(input.equals("evaluate")){
                System.out.println("evaluation=" + Evaluation.evaluate_board(board1));
            }
            else if(input.equals("clear")){
                board1.hash_table.clear_hash_tables();
            }
            else if(input.startsWith("hashtable")){
                int entries = Integer.parseInt(input.split(" ")[1]);
                if(entries < 1000){
                    entries=1000;
                }
                if(entries > 3000000){
                    entries=3000000;
                }
                board1.hash_table.init_hash_tables(entries);
            }
            else if(input.equals("cleareval")){
                board1.eval_table.clear_eval_tables();
            }
            else if(input.startsWith("evaltable")){
                int entries = Integer.parseInt(input.split(" ")[1]);
                if(entries < 1000){
                    entries=1000;
                }
                if(entries > 3000000){
                    entries=3000000;
                }
                board1.eval_table.init_eval_tables(entries);
            }
            else if(input.equals("post")){
                info.post=true;
            }
            else if(input.equals("nopost")){
                info.post=false;
            }
            else if(input.equals("mate")){
                info.mate_limit=true;
            }
            else if(input.equals("nomate")){
                info.mate_limit=false;
            }
            else if(input.startsWith("time")){
                time = Integer.parseInt(input.split(" ")[1]);
                if (time < 0){
                    time=0;
                }
            }
            else if(input.equals("view")){
                System.out.println("depth="+depth);
                System.out.println("time="+time);
                System.out.println("mate mode="+info.mate_limit);
                System.out.println("post="+info.post);
            }
            else if(input.equals("print")){
                board1.print_board();
            }
            else if(input.equals("go")){
                ai_side=board1.turn;
            }
            else if(input.equals("force")){
                ai_side=cv.empty;
            }
            else if(input.equals("undo")){
                if(board1.his_ply > 0) {
                    board1.undo_move();
                    ai_side= cv.empty;
                }
            }
            else if(input.equals("quit")){
                System.exit(0);
            }
            else {
                int move;
                if(input.length()>1)continue;
                try {
                    move = Integer.parseInt(input);
                }catch (NumberFormatException e){
                    continue;
                }
                for(int i:board1.get_legal_moves()){
                    if(move==i){
                        board1.make_move(i);
                        board1.ply = 0;
                        System.out.println("move made="+i);
                    }
                }
            }
        }
    }

}
