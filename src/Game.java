public abstract class Game{
    
    protected String gameName;
    protected int currentScore;

    public abstract void startGame();

    public void endGame(){
        System.out.println(gameName + " ended." + "\n" + "Score: " + currentScore);
    }

}