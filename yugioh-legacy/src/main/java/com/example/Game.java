package com.example;

import java.io.Serializable;

public class Game extends Player implements Effect, Serializable{
    int userTurnCount;
    int opponentTurnCount;
    //CURRENTPHASE: 0 = Standby Phase, 1 = Draw Phase, 2 = Main Phase 1, 3 = Battle Phase, 4 = Main Phase 2, 5 = End Phase.
    int currentPhase;
    boolean isUserTurn;
    Player user;
    Player opponent;

    public Game(){
        user = new Player();
        opponent = new Player();
        userTurnCount = 0;
        opponentTurnCount = 0;
        currentPhase = 0;
        isUserTurn = false;
    }
    public Game(Player p1, Player p2){
        user = p1;
        opponent = p2;
        userTurnCount = 0;
        opponentTurnCount = 0;
        currentPhase = 0;
        isUserTurn = false;
        user.shuffleDeck();
        opponent.shuffleDeck();
        for(int i = 0; i <= 5; i++){
            user.addToHand();
            opponent.addToHand();
        }
    }
    //True indicates a player has won and goes second; False indicates a tie and a replay is needed;
    // 1 = rock, 2 = paper, 3 = scissors
    public boolean rockPaperScissors(int userChoice, int opponentChoice){
        if(userChoice == opponentChoice){
            return false;
        }
        if((userChoice==1&&opponentChoice==2)||(userChoice==2&&opponentChoice==3)||(userChoice==3&&opponentChoice==1)){
            isUserTurn = true;
            return true;
        }
        if((userChoice==2&&opponentChoice==1)||(userChoice==3&&opponentChoice==2)||(userChoice==1&&opponentChoice==3)){
            isUserTurn = false;
            return true;
        }
        return false;
    }
    public void startNextTurn(){
        if(isUserTurn){
            userTurnCount++;
            isUserTurn = false;
        }
        else{
            opponentTurnCount++;
            isUserTurn = true;
        }
    }
    public void startNextPhase(){
        if(currentPhase <= 3){
            currentPhase++;
        }
        if(currentPhase == 4){
            currentPhase = 0;
            startNextTurn();
        }
    }
    public int getCurrentPhase(){
        return currentPhase;
    }
    public void drawPhase(){
        if(isUserTurn){
            user.addToHand();
        }
        else{
            opponent.addToHand();
        }
    }

    @Override
    public void useEffect(Card userCard) {
        if(userCard instanceof Monster) {
            //Summoner Monk
            if (userCard.getId().equals("423585")) {
                if(userCard.getCardTurnCounter() == 0 && user.isSpellInHand());
            }
        }
    }

    @Override
    public void passiveEffect(Card userCard) {
        if(userCard instanceof Monster) {
            //Summoner Monk
            if (userCard.getId().equals("423585")) {
                ((Monster) userCard).setCanBeTributed(false);
                ((Monster) userCard).setPositionDefense();
            }
        }
    }
}
