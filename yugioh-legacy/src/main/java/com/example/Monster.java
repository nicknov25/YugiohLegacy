package com.example;

import java.io.Serializable;
import java.sql.*;

public class Monster extends Card implements Serializable {
    private int attackPoints;
    private int defensePoints;
    private int level;
    private String element;
    private String type;
    private final int defaultAttackPoints;
    private final int defaultDefensePoints;
    private final int defaultLevel;
    private final String defaultElement;
    private final String defaultType;
    private boolean hasAttacked = false;
    private boolean canBeTributed;
    private boolean defaultCanBeTributed;

    public Monster(){
        super();
        attackPoints = 0;
        defensePoints = 0;
        level = 0;
        element = "NULL";
        type = "NULL";
        canBeTributed = true;
        defaultAttackPoints = 0;
        defaultDefensePoints = 0;
        defaultLevel = 0;
        defaultElement = element;
        defaultType = type;
        defaultCanBeTributed = true;
    }
    //Position will always be "NUll", "Attack", "Defense", or "Set"
    //Types of Monster Cards: "Normal", "Effect", and "Flip Effect"
    public Monster(String userID, String userName, String userDescription, boolean userHasEffect, int userAttack,
                   int userDefense, int userLevel, String userElement, String userType){
        super(userID, userName, userDescription, userHasEffect);
        attackPoints = userAttack;
        defensePoints = userDefense;
        level = userLevel;
        element = userElement;
        type = userType;
        canBeTributed = true;
        defaultAttackPoints = attackPoints;
        defaultDefensePoints = defensePoints;
        defaultLevel = level;
        defaultElement = element;
        defaultType = type;
        defaultCanBeTributed = true;
    }
    public Monster(String userId) throws ClassNotFoundException, SQLException {
        super(userId);
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards" , "root", "Classic Yugioh");
        Statement statement = connection.createStatement();
        ResultSet card = statement.executeQuery
                ("select atk, def, lvl, element, type from Monsters where id = '" + userId + "';");
        if(card.next()){
            attackPoints = card.getInt(1);
            defensePoints = card.getInt(2);
            level = card.getInt(3);
            element = card.getString(4);
            type = card.getString(5);
        }
        else{
            attackPoints = 0;
            defensePoints = 0;
            level = 0;
            element = "ERROR";
            type = "ERROR";
        }
        defaultAttackPoints = attackPoints;
        defaultDefensePoints = defensePoints;
        defaultLevel = level;
        defaultElement = element;
        defaultType = type;
        connection.close();
    }
    public Monster(Monster m){
        super(m.getId(), m.getName(), m.getDescription(), m.getHasEffect());
        attackPoints = m.getAttackPoints();
        defensePoints = m.getDefensePoints();
        level = m.getLevel();
        element = m.getElement();
        type = m.getType();
        defaultAttackPoints = m.getAttackPoints();
        defaultDefensePoints = m.getDefensePoints();
        defaultLevel = m.getLevel();
        defaultElement = m.getElement();
        defaultType = m.getType();
    }
    public void setHasAttacked(boolean bool){
        hasAttacked = bool;
    }
    public boolean getHasAttacked(){
        return hasAttacked;
    }
    public void setAttackPoints(int userAttack){
        attackPoints = userAttack;
    }
    public void raiseAttackPoints(int userAttack){
        attackPoints += userAttack;
    }
    public void lowerAttackPoints(int userAttack){
        attackPoints -= userAttack;
    }
    public int getAttackPoints(){
        return attackPoints;
    }
    public void setDefensePoints(int userDefense){
        defensePoints = userDefense;
    }
    public void raiseDefensePoints(int userDefense){
        defensePoints += userDefense;
    }
    public void lowerDefensePoints(int userDefense){
        defensePoints -= userDefense;
    }
    public int getDefensePoints(){
        return defensePoints;
    }
    public void setLevel(int userLevel){
        level = userLevel;
    }
    public void raiseLevel(int userLevel){
        level += userLevel;
    }
    public void lowerLevel(int userLevel){
        level -= userLevel;
    }
    public int getLevel(){
        return level;
    }
    public void setElement(String userElement){
        element = userElement;
    }
    public String getElement(){
        return element;
    }
    public void setType(String userType){
        type = userType;
    }
    public String getType(){
        return type;
    }
    public boolean getCanBeTributed(){
        return canBeTributed;
    }
    public void setCanBeTributed(boolean userValue){
        canBeTributed = userValue;
    }
    public void restoreDefaultAttack(){
        setAttackPoints(defaultAttackPoints);
    }
    public void restoreDefaultDefense(){
        setDefensePoints(defaultDefensePoints);
    }
    public void restoreDefaultLevel(){
        setLevel(defaultLevel);
    }
    public void restoreDefaultElement(){
        setElement(defaultElement);
    }
    public void restoreDefaultType(){
        setType(defaultType);
    }
    public void restoreDefaultCanBeTributed(){
        setCanBeTributed(defaultCanBeTributed);
    }
    public void setPositionAttack(){
        setPosition("Attack");
    }
    public void setPositionDefense(){
        setPosition("Defense");
    }

    @Override
    public void restoreAllDefaults() {
        super.restoreAllDefaults();
        restoreDefaultAttack();
        restoreDefaultDefense();
        restoreDefaultLevel();
        restoreDefaultElement();
        restoreDefaultType();
        restoreDefaultCanBeTributed();
    }

    @Override
    public String toString() {
        return super.toString() + "Element: " + getElement() + "\nType: " + getType() + "\nLevel " + getLevel() +
                "\nATK " + getAttackPoints() + "/DEF " + getDefensePoints() + "\nHas Effect?: " + getHasEffect() +
                "\nDescription/Effect:\n" + getDescription() + "\n";
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Monster monster = new Monster("16268841");
        System.out.println(monster);
    }
}
