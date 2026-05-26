public class TicTacToeGame extends Game implements Scorable{

    final int row=3,col=3;
    
    char[][] board = new char[row][col];

    public TicTacToeGame(){
        startGame();
    }

    public boolean makeMove(int r, int c, char player){
        if(board[r][c] != '-') return false; // cell already taken
        board[r][c] = player;
        currentScore++;
        return true;
    }

    public char[][] getBoard(){
        return board;
    }

    public char checkWinner(){
        boolean flag = false;
        char result = '-';

        for( int x = 0 ; x<row ; x++){ // checking row condition
                if( (board[x][0]=='X' && board[x][1]=='X' && board[x][2]=='X') ){
                    flag = true;    result = 'X';
                }
                else if( !(flag) && (board[x][0]=='O' && board[x][1]=='O' && board[x][2]=='O') ){
                    flag = true;    result = 'O';
                }
                if(flag) { break; }
        }

        if(result == '-'){
            for( int y = 0 ; y<col ; y++){ // checking column condition
                    if( (board[0][y]=='X' && board[1][y]=='X' && board[2][y]=='X') ){
                        flag = true;    result = 'X';
                    }
                    else if( !(flag) && (board[0][y]=='O' && board[1][y]=='O' && board[2][y]=='O') ){
                        flag = true;    result = 'O';
                    }
                    if(flag) { break; }
            }
            
            if(!flag){  // diagnol conditions (from top left to bottom right)
                if( (board[0][0]=='X' && board[1][1]=='X' && board[2][2]=='X') ){
                        flag = true;    result = 'X';
                    }
                    else if( !(flag) && (board[0][0]=='O' && board[1][1]=='O' && board[2][2]=='O') ){
                        flag = true;    result = 'O';
                    }
            }
            
            if(!flag){  // diagonal conditions (from top right to bottom left)
                if( (board[0][2]=='X' && board[1][1]=='X' && board[2][0]=='X') ){
                        flag = true;    result = 'X';
                    }
                    else if( !(flag) && (board[0][2]=='O' && board[1][1]=='O' && board[2][0]=='O') ){
                        flag = true;    result = 'O';
                    }
            }
            if(!flag){  // draw condition
                boolean check = false;
                for (int x = 0 ; x<row ; x++ ){
                    for(int y = 0 ; y<col ; y++ ){
                        if (board[x][y] =='-'){
                            check = true;   // can not be a draw then
                        }
                        if(check) break;
                    }
                    if(check) break;
                }
                if(!check) result = 'D';
            }

        }
        return result;
    }

    @Override
    public void startGame(){
        for(int x = 0 ; x < row ; x++ ){
            for(int y = 0 ; y < col ; y++){
                board[x][y] = '-';
            }
        }
    }

    @Override
    public void saveScore(String username){
        System.out.println("Score saved for " + username);
    }
    
    public void aiMove(){
        boolean moved = false;
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (board[i][j] == '-') {
                    makeMove(i, j, 'O');
                    moved = true; 
                    break;
                }
            }
            if (moved){ break ;}
        }
    }
}