package com.example;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

public abstract class Card implements Serializable{
    private String id;
    private int cardTurnCounter;
    private int counter;
    private int defaultCounter;
    private String name;
    private String description;
    private boolean hasEffect;
    private final String defaultId;
    private final String defaultName;
    private final String defaultDescription;
    private final boolean defaultHasEffect;
    private int defaultCardTurnCounter;
    private String position;
    private boolean isFaceDown;
    public Card(){
        id = "NULL";
        name = "NULL";
        description = "NULL";
        hasEffect = false;
        counter = 0;
        defaultCounter = 0;
        defaultId = id;
        defaultName = name;
        defaultDescription = description;
        defaultHasEffect = false;
        position = "NULL";
        isFaceDown = false;
        cardTurnCounter = 0;
        defaultCardTurnCounter = 0;
    }
    public Card(String userID, String userName, String userDescription, boolean userHasEffect){
        id = userID;
        name = userName;
        description = userDescription;
        hasEffect = userHasEffect;
        counter = 0;
        defaultCounter = 0;
        defaultId = id;
        defaultName = name;
        defaultDescription = description;
        defaultHasEffect = hasEffect;
        position = "NULL";
        isFaceDown = false;
        cardTurnCounter = 0;
        defaultCardTurnCounter = 0;
    }
    public Card(String userId) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
        Statement statement = connection.createStatement();
        ResultSet card;
        if(this instanceof Monster){
            card = statement.executeQuery
                    ("select name, description, hasEffect from Monsters where id = " + userId + ";");
        }
        else if(this instanceof Spell){
            card = statement.executeQuery
                    ("select name, description, hasEffect from Spells where id = " + userId + ";");
        }
        else if(this instanceof Trap){
            card = statement.executeQuery
                    ("select name, description, hasEffect from Traps where id = " + userId + ";");
        }
        else{
            card = statement.executeQuery("null");
        }
        id = userId;
        if(card.next()){
            name = removeDuplicates(card.getString(1));
            description = removeDuplicates(card.getString(2));
            hasEffect = card.getBoolean(3);
        }
        else{
            name = "ERROR";
            description = "ERROR";
            hasEffect = false;
        }
        defaultId = id;
        defaultName = name;
        defaultDescription = description;
        defaultHasEffect = hasEffect;
        position = "NULL";
        connection.close();
    }
    public Card(Card c){
        id = c.getId();
        name = c.getName();
        description = c.getDescription();
        hasEffect = c.getHasEffect();
        defaultId = c.getId();
        defaultName = c.getName();
        defaultDescription = c.getDescription();
        defaultHasEffect = c.hasEffect;
        position = "NULL";
    }
    public void setId(String userID){
        id = userID;
    }
    public String getId(){
        return id;
    }
    public void setName(String userName){
        name = userName;
    }
    public String getName(){
        return name;
    }
    public void setDescription(String userDescription){
        description = userDescription;
    }
    public String getDescription(){
        return description;
    }
    public void setCardTurnCounter(int userCounter){
        cardTurnCounter = userCounter;
    }
    public void addOneToCardTurnCounter(){
        cardTurnCounter++;
    }
    public void subtractOneFromCardTurnCounter(){
        cardTurnCounter--;
    }

    public int getCardTurnCounter() {
        return cardTurnCounter;
    }

    public void setCounter(int userCounter){
        counter = userCounter;
    }
    public void addOneToCounter(){
        counter++;
    }
    public void subtractOneFromCounter(){
        counter--;
    }
    public int getCounter(){
        return counter;
    }
    public void setDefaultCounter(int userCounter){
        defaultCounter = userCounter;
    }
    public boolean getIsFaceDown(){return isFaceDown;}
    public void setPosition(String userPosition){
        position = userPosition;
    }
    public String getPosition(){
        return position;
    }
    public void nullifyCard(){
        setPosition("NULL");
    }
    public void setCard(){
        position = "Set";
    }
    public void setHasEffect(boolean userHasEffect){
        hasEffect = userHasEffect;
    }
    public boolean getHasEffect(){
        return hasEffect;
    }
    public void restoreDefaultId(){
        setId(defaultId);
    }
    public void restoreDefaultName(){
        setName(defaultName);
    }
    public void restoreDefaultDescription(){
        setDescription(defaultDescription);
    }
    public void restoreDefaultHasEffect(){
        setHasEffect(defaultHasEffect);
    }
    public void restoreDefaultCounter(){
        setCounter(defaultCounter);
    }
    public void restoreDefaultCardTurnCounter(){setCardTurnCounter(defaultCardTurnCounter);}
    public void restoreAllDefaults(){
        restoreDefaultId();
        restoreDefaultName();
        restoreDefaultDescription();
        restoreDefaultHasEffect();
        restoreDefaultCounter();
    }
    @Override
    public String toString() {
        return "CARD #" + getId() + "\nName: " + getName() + "\n";
    }
    private String removeDuplicates(String s){
        char[] chars = s.toCharArray();
        ArrayList<Character> newChars = new ArrayList<>();
        for(int i = 0; i < chars.length; i++){
            if(chars[i] == '\'' || chars[i] == '"'){
                if(i != chars.length-1 && chars[i] == chars[i+1]){
                    i++;
                }
            }
            newChars.add(chars[i]);
        }
        int count = 0;
        for(int i = 0; i < newChars.size(); i++){
            if(count >= 20 && newChars.get(i) == ' '){
                newChars.set(i, '\n');
                count = 0;
            }
            count++;
        }
        char[] finalChars = new char[newChars.size()];
        for(int i = 0; i < newChars.size(); i++){
            finalChars[i] = newChars.get(i);
        }
        return String.valueOf(finalChars);
    }
}
