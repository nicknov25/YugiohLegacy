package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Deck extends Card implements Serializable{
    private Stack<Card> myDeck;
    private final int maxSpecialSize = 15;
    private final int maxDeckSize = 60;
    private final int minDeckSize = 40;
    public Deck(){ myDeck = new Stack<>();}
    public Deck(Card userCard){
        myDeck = new Stack<>();
        addCard(userCard);
    }
    public boolean isCardInDeck(Card userCard){
        if(myDeck.search(userCard) == -1){
            return true;
        }
        return false;
    }
    public void addCard(Card userCard){
        if(myDeck.size() >= maxDeckSize){
            return;
        }
        else {
            myDeck.push(userCard);
            shuffleDeck();
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(myDeck);
    }
    public Card drawCard(){
        return myDeck.pop();
    }
    public Card[] viewWholeDeck(){
        Card[] cards = new Card[myDeck.size()];
        for(int i = 0; i < myDeck.size(); i++){
            cards[i] = myDeck.pop();
        }
        for(int i = 0; i < cards.length; i++){
            myDeck.push(cards[i]);
        }
        shuffleDeck();
        return cards;
    }
    public void emptyDeck(){
        for(int i = 0; i < myDeck.size(); i++){
            myDeck.pop();
        }
    }
    public boolean isValidSpecialDeck(){
        if(myDeck.size() <= maxSpecialSize){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isValidDeck(){
        if(myDeck.size() >= minDeckSize && myDeck.size() <= maxDeckSize){
            shuffleDeck();
            return true;
        }
        else {
            return false;
        }
    }
    public Card getSpecificCardFromDeck(Card userCard){
        ArrayList<Card> cardList = new ArrayList<>();
        for(int i = 0; i < myDeck.size(); i++){
            cardList.add(myDeck.pop());
        }
        Card c = cardList.get(cardList.indexOf(userCard));
        cardList.remove(userCard);
        for(int i = 0; i < cardList.size(); i++){
            myDeck.push(cardList.get(i));
        }
        shuffleDeck();
        return c;
    }
    public int cardsRemaining(){return myDeck.size();}
}
