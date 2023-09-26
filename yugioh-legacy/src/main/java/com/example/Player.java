package com.example;

import java.io.Serializable;
import java.util.ArrayList;

public class Player extends Deck implements Serializable{
    private Deck deck;
    private Deck graveyard;
    private Deck banished;
    private Card fieldSpell;
    private Card[] monsters = new Card[5];
    private Card[] magics = new Card[5];
    private int lifePoints;
    public ArrayList<Card> hand = new ArrayList<>();

    public Player(){
        deck = new Deck();
        graveyard = new Deck();
        banished = new Deck();
        fieldSpell = new Spell();
        for (int i = 0; i < monsters.length; i++){
            monsters[i] = new Monster();
            magics[i] = new Spell();
        }
        lifePoints = 0;
    }
    public Player(Deck userDeck, int userLifePoints){
        deck = userDeck;
        graveyard = new Deck();
        banished = new Deck();
        fieldSpell = new Spell();
        for (int i = 0; i < monsters.length; i++){
            monsters[i] = new Monster();
            magics[i] = new Spell();
        }
        lifePoints = userLifePoints;
    }
    public Deck getDeck(){return deck;}
    public boolean monsterSpotAvailable(){
        for(int i = 0; i < monsters.length; i++){
            if(monsters[i].getId() == null){
                return true;
            }
        }
        return false;
    }
    public boolean magicSpotAvailable(){
        for(int i = 0; i < magics.length; i++){
            if(magics[i].getId() == null){
                return true;
            }
        }
        return false;
    }
    public boolean fieldSpellSpotAvailable(){
        if(fieldSpell.getId() == null){
            return true;
        }
        return false;
    }

    public int getLifePoints() {return lifePoints;}

    public void addLifePoints(int userLifePoints){
        lifePoints += userLifePoints;
    }
    public void subtractLifePoints(int userLifePoints){
        lifePoints -= userLifePoints;
        if(lifePoints <= 0){
            lifePoints = 0;
        }
    }
    public void addToHand(){
        hand.add(deck.drawCard());
    }
    public boolean isCardInHand(Card card){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i) == card){
                return true;
            }
        }
        return false;
    }
    public boolean isMonsterInHand(){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i) instanceof Monster){
                return true;
            }
        }
        return false;
    }
    public boolean isTrapInHand(){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i) instanceof Trap){
                return true;
            }
        }
        return false;
    }
    public boolean isSpellInHand(){
        for(int i = 0; i < hand.size(); i++){
            if(hand.get(i) instanceof Spell){
                return true;
            }
        }
        return false;
    }
    public Card selectCardFromHand(int handNum){
        return hand.get(handNum);
    }
    public void playCardFromHand(Card userCard, int fieldNum){
        if(hand.get(hand.indexOf(userCard)) instanceof Monster){
            monsters[fieldNum] = hand.get(hand.indexOf(userCard));
        }
        if(hand.get(hand.indexOf(userCard)) instanceof Trap){
            magics[fieldNum] = hand.get(hand.indexOf(userCard));
        }
        if(hand.get(hand.indexOf(userCard)) instanceof Spell){
            if(((Spell) hand.get(hand.indexOf(userCard))).getType().equals("Field")){
                fieldSpell = hand.get(hand.indexOf(userCard));
            }
            else{
                magics[fieldNum] = hand.get(hand.indexOf(userCard));
            }
        }
        hand.remove(userCard);
    }
    public void sendMonsterToGraveyard(int fieldNum){
        if(!monsters[fieldNum].getId().equals("NULL")){
            graveyard.addCard(monsters[fieldNum]);
            monsters[fieldNum] = new Monster();
        }
    }
    public void sendMagicToGraveyard(int fieldNum){
        if(!magics[fieldNum].getId().equals("NULL")){
            graveyard.addCard(magics[fieldNum]);
            magics[fieldNum] = new Spell();
        }
    }
    public void sendFieldSpellToGraveyard(){
        if(!fieldSpell.getId().equals("NULL")){
            graveyard.addCard(fieldSpell);
            fieldSpell = new Spell();
        }
    }
    public void banishMonster(int fieldNum){
        if (!monsters[fieldNum].getId().equals("NULL")){
            banished.addCard(monsters[fieldNum]);
            monsters[fieldNum] = new Monster();
        }
    }
    public void banishMagic(int fieldNum){
        if(!magics[fieldNum].getId().equals("NULL")){
            banished.addCard(magics[fieldNum]);
            magics[fieldNum] = new Spell();
        }
    }
    public void banishFieldSpell(){
        if(!fieldSpell.getId().equals("NULL")){
            banished.addCard(fieldSpell);
            fieldSpell = new Spell();
        }
    }
    public void normalSummonLv4Monster(Monster m, String position){
        if(monsterSpotAvailable()){
            if(m.getLevel() <= 4){
                for(int i = 0; i < monsters.length; i++){
                    if(monsters[i].getId() == null){
                        monsters[i] = m;
                        m.setPosition(position);
                        i = monsters.length;
                    }
                }
            }
        }
    }
    public void normalSummonLv6Monster(Monster m, int tribute, String position){
        if(m.getLevel() == 5 || m.getLevel() == 6){
            sendMonsterToGraveyard(tribute);
            for(int i = 0; i < monsters.length; i++){
                if(monsters[i].getId() == null){
                    monsters[i] = m;
                    m.setPosition(position);
                    i = monsters.length;
                }
            }
        }
    }
    public void normalSummonLv9Monster(Monster m, int tribute1, int tribute2, String position){
        if(m.getLevel() > 6 && m.getLevel() < 10){
            sendMonsterToGraveyard(tribute1);
            sendMonsterToGraveyard(tribute2);
            for(int i = 0; i < monsters.length; i++){
                if(monsters[i].getId() == null){
                    monsters[i] = m;
                    m.setPosition(position);
                    i = monsters.length;
                }
            }
        }
    }
    public void normalSummonLv10Monster(Monster m, int tribute1, int tribute2, int tribute3, String position){
        if(m.getLevel() <= 10){
            sendMonsterToGraveyard(tribute1);
            sendMonsterToGraveyard(tribute2);
            sendMonsterToGraveyard(tribute3);
            for(int i = 0; i < monsters.length; i++){
                if(monsters[i].getId() == null){
                    monsters[i] = m;
                    m.setPosition(position);
                    i = monsters.length;
                }
            }
        }
    }

}
