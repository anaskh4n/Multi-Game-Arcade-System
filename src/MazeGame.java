public class MazeGame extends Game implements Scorable{

    // # is wall
    // ' ' is empty space
    // S is the starting point
    // E is the ending point
    // player can move in ' ' areas

    private char[][] easyMap;
    private char[][] hardMap;
    private char[][] chosen = new char[6][6];
    private String difficulty = "easy";
    private boolean exitReached;
    int totalMoves = 0;

    int playerRow;
    int playerCol;

    public void initialize(){
        
        easyMap = new char[][] {
            {'#','#','#','#','#','#'},
            {'#','S',' ',' ','#','#'},
            {'#',' ','#',' ','#','#'},
            {'#',' ',' ',' ',' ','#'},
            {'#',' ',' ','#','E','#'},
            {'#','#','#','#','#','#'}
        };

        // 2 hardcoded maps

        hardMap = new char[][] {
            {'#','#','#','#','#','#'},
            {'#','S','#','E',' ','#'},
            {'#',' ',' ','#',' ','#'},
            {'#',' ',' ','#',' ','#'},
            {'#','#',' ',' ',' ','#'},
            {'#','#','#','#','#','#'}
        };
    }

    public MazeGame() {
        initialize();
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public char[][] getBoard(){
        return chosen;
    }

    @Override
    public void startGame() {
        startGame(difficulty.equals("easy"));
    }

    public void startGame(boolean isEasy){
        exitReached = false;
        totalMoves = 0;
        currentScore = 0;

        if(isEasy){
            difficulty = "easy";
        } else {
            difficulty = "hard";
        }

        if(isEasy){
            for(int x = 0; x < 6; x++)
                for(int y = 0; y < 6; y++)
                    chosen[x][y] = easyMap[x][y];
        } else {
            for(int x = 0; x < 6; x++)
                for(int y = 0; y < 6; y++)
                    chosen[x][y] = hardMap[x][y];
        }

        chosen[1][1] = 'P';
        playerRow = 1;
        playerCol = 1;
    }

    public void move(String direction){
        if(direction.equals("UP")){
            if(chosen[playerRow-1][playerCol] == '#') return;
            chosen[playerRow][playerCol] = ' ';
            playerRow--;
            if(chosen[playerRow][playerCol] == 'E') exitReached = true;
            else chosen[playerRow][playerCol] = 'P';
            totalMoves++;
        }
        else if(direction.equals("DOWN")){
            if(chosen[playerRow+1][playerCol] == '#') return;
            chosen[playerRow][playerCol] = ' ';
            playerRow++;
            if(chosen[playerRow][playerCol] == 'E') exitReached = true;
            else chosen[playerRow][playerCol] = 'P';
            totalMoves++;
        }
        else if(direction.equals("RIGHT")){
            if(chosen[playerRow][playerCol+1] == '#') return;
            chosen[playerRow][playerCol] = ' ';
            playerCol++;
            if(chosen[playerRow][playerCol] == 'E') exitReached = true;
            else chosen[playerRow][playerCol] = 'P';
            totalMoves++;
        }
        else if(direction.equals("LEFT")){
            if(chosen[playerRow][playerCol-1] == '#') return;
            chosen[playerRow][playerCol] = ' ';
            playerCol--;
            if(chosen[playerRow][playerCol] == 'E') exitReached = true;
            else chosen[playerRow][playerCol] = 'P';
            totalMoves++;
        }
    }

    public int calculateScore(){
        int optimalMoves;
        if(difficulty.equals("easy")){
            optimalMoves = 10;
        } else {
            optimalMoves = 15;
        }

        int score;
        if(totalMoves <= optimalMoves){
            score = 500;
        } else {
            int penalty = (totalMoves - optimalMoves) * 5;
            if(500 - penalty < 50){
                score = 50;
            } else {
                score = 500 - penalty;
            }
        }
        return score;
    }

    boolean isExitReached(){
        return exitReached;
    }

    @Override
    public void saveScore(String username){
        System.out.println("Score saved for " + username);
    }
}