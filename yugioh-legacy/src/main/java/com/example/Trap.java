package com.example;

import java.io.Serializable;
import java.sql.*;

public class Trap extends Card implements Serializable {
    private String element;
    private String type;
    private final String defaultElement;
    private final String defaultType;
    private boolean canBeTributed;
    private boolean defaultCanBeTributed;
    public Trap(){
        super();
        element = "Trap";
        type = "Normal";
        canBeTributed = false;
        defaultElement = element;
        defaultType = type;
        defaultCanBeTributed = false;
    }
    //Position will always be "NUll", "Set", or "Active"
    //Types of Trap Cards: "Counter", "Continuous", "Normal", "Field", and "Equip"
    public Trap(String userID, String userName, String userDescription, String userType){
        super(userID, userName, userDescription, true);
        element = "Trap";
        type = userType;
        canBeTributed = false;
        defaultElement = element;
        defaultType = type;
        defaultCanBeTributed = false;
    }
    public Trap(String userId) throws ClassNotFoundException, SQLException {
        super(userId);
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards" , "root", "Classic Yugioh");
        Statement statement = connection.createStatement();
        ResultSet card = statement.executeQuery
                ("select element, type from Traps where id = '" + userId + "';");
        if(card.next()) {
            element = card.getString(1);
            type = card.getString(2);
        }
        else{
            element = "ERROR";
            type = "ERROR";
        }
        defaultElement = element;
        defaultType = type;
        connection.close();
    }
    public Trap(Trap t){
        super(t.getId(), t.getName(), t.getDescription(), t.getHasEffect());
        element = t.getElement();
        type = t.getType();
        defaultElement = t.getElement();
        defaultType = t.getType();
    }
    public String getElement() {
        return element;
    }
    public void setElement(String userElement) {
        element = userElement;
    }
    public String getType(){
        return type;
    }
    public void setType(String userType){
        type = userType;
    }
    public void restoreDefaultElement(){
        setElement(defaultElement);
    }
    public void restoreDefaultType(){
        setType(defaultType);
    }
    public void ActivateCard(){
        setPosition("Active");
    }
    public boolean getCanBeTributed(){
        return canBeTributed;
    }
    public void setCanBeTributed(boolean userValue){
        canBeTributed = userValue;
    }
    public void restoreDefaultCanBeTributed(){
        setCanBeTributed(defaultCanBeTributed);
    }
    @Override
    public void restoreAllDefaults() {
        super.restoreAllDefaults();
        restoreDefaultElement();
        restoreDefaultType();
        restoreDefaultCanBeTributed();
    }

    @Override
    public String toString() {
        return super.toString() + "Element: " + getElement() + "\nType: " + getType() + "\nEffect:\n" + getDescription() + "\n";
    }
}
