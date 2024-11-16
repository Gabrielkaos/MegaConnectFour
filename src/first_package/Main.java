package first_package;

//TODO find bugs

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        SearchInfo info = new SearchInfo();

        ConnectFourCLI.run(board,info);

    }
}
