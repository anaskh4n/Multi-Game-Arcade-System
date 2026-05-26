public class MastermindGame extends Game implements Scorable{
    
    String color [] = new String [4];
    String chose [] = {"R", "G", "B", "Y", "W", "P"};

    int attemptcount = 0;

    public void initialize(){
        for( int x = 0 ; x<4 ; x++){
            int index = (int)(Math.random()*(5+1));
            color[x] = chose[index];
        }
    }

    public MastermindGame(){
         startGame(); 
    }

    public String[] getSecretCode(){
        return color;
    }

    int [] checkGuess(String[] guess){

        attemptcount++;
        int result [] = new int [2];
        int ccount = 0; //color count
        int pos = 0;

        for(int x = 0 ; x<4 ; x++){
            if(guess[x].equals(color[x])){
                pos++;
            }
        }

        for(int x = 0 ; x<4 ; x++){
            if(guess[x].equals(color[x])) continue; // skip exact matches
            for(int y = 0 ; y<4 ; y++){
                if(x != y && guess[x].equals(color[y])){
                    ccount++;
                    break;
                }
            }
        }

        result[0] = pos;
        result[1] = ccount;

        // check if game lost
        if(attemptcount >= 10 && pos != 4){
            System.out.println("Game over! You lost.");
        }

        return result;

    }

    @Override
    public void startGame(){
        initialize();
        attemptcount = 0;
    }

    @Override
    public void saveScore(String username){
        System.out.println("Score saved for " + username);
    }

}
