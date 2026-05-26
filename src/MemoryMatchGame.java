public class MemoryMatchGame extends Game implements Scorable{

    final int row = 4;
    final int col = 4;
    
    int card [][] = new int [row][col];
    int [] random = {1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8};

    // already initialized as false, all indexes
    boolean [][] flipped = new boolean[row][col];
    int flippedCount = 0;

    
    public MemoryMatchGame(){ 
        startGame(); 
    }

    public void initialize() {
        // shuffling
        for(int i = 0 ; i < 16 ; i++){
            int j = (int)(Math.random()*(i+1));
            int temp = random[i];
            random[i] = random[j];
            random[j] = temp;
        }

        //assigning the values to the card array
        int index = 0;
        for(int i = 0 ; i < row ; i++){
            for(int j = 0 ; j < col ; j++){
                card[i][j] = random[index++];
            }
        }
    }    

    public void flipCard(int r, int c){
        if(flipped[r][c]) return;

        flipped[r][c] = true;
        flippedCount++;
        if(flippedCount %2 == 0){
            currentScore++;
        }
    }

    public boolean isMatch(int r1, int c1, int r2, int c2){
        if(card[r1][c1] == card[r2][c2]){
            return true;
        }
        else{ return false;}

    }

    

    public int getValue(int r, int c){
        return card[r][c];
    }

    public void unflipCard(int r, int c){
        flipped[r][c] = false;
        flippedCount--;
    }

    public boolean allMatched(){
        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
                if(!flipped[i][j]) return false;
        return true;
    }

    @Override
    public void startGame(){

        flipped = new boolean[row][col];
        flippedCount = 0;
        currentScore = 0;

        // shuffling
        for(int i = 0 ; i < 16 ; i++){
            int j = (int)(Math.random()*(i+1));
            int temp = random[i];
            random[i] = random[j];
            random[j] = temp;
        }

        //assigning the values to the card array
        int index = 0;
        for(int i = 0 ; i < row ; i++){
            for(int j = 0 ; j < col ; j++){
                card[i][j] = random[index++];
            }
        }
    }

    @Override
    public void saveScore(String username){
        System.out.println("Score saved for " + username);
    }

    
}