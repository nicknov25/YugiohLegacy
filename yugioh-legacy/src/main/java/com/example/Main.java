package com.example;

import java.sql.*;
import java.util.ArrayList;

public class Main {
    static String removeDuplicates(String s){
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
            if(count >= 70 && newChars.get(i) == ' '){
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
    public static void main(String[] args) throws SQLException, ClassNotFoundException{
        // Load the JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Driver loaded");
        // Connect to a database
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards" , "root", "Classic Yugioh");
        System.out.println("Database connected");
        // Create a statement
        Statement statement1 = connection.createStatement();
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();


        // Execute a statement
        ResultSet monsters = statement1.executeQuery
                ("select id, name, description, hasEffect, atk, def, lvl, element, type, implemented from Monsters");
        ResultSet traps = statement2.executeQuery
                ("select id, name, description, type from Traps");
        ResultSet spells = statement3.executeQuery
                ("select id, name, description, type from Spells");

        // Iterate through the result and print the student names
        ArrayList<Card> implementedCards = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        String id;
        String name;
        String description;
        boolean hasEffect;
        int atk;
        int def;
        int lvl;
        String element;
        String type;
        while (monsters.next()){
            id = monsters.getString(1);
            name = removeDuplicates(monsters.getString(2));
            description = removeDuplicates(monsters.getString(3));
            hasEffect = monsters.getBoolean(4);
            atk = monsters.getInt(5);
            def = monsters.getInt(6);
            lvl = monsters.getInt(7);
            element = monsters.getString(8);
            type = monsters.getString(9);
            if(monsters.getBoolean(10)){
                implementedCards.add(new Monster(id, name, description, hasEffect, atk, def, lvl, element, type));
                cards.add(new Monster(id, name, description, hasEffect, atk, def, lvl, element, type));
            }
            else{
                cards.add(new Monster(id, name, description, hasEffect, atk, def, lvl, element, type));
            }
        }
        while (spells.next()){
            id = spells.getString(1);
            name = removeDuplicates(spells.getString(2));
            description = removeDuplicates(spells.getString(3));
            type = spells.getString(4);
            cards.add(new Spell(id, name, description, type));
        }
        while (traps.next()){
            id = traps.getString(1);
            name = removeDuplicates(traps.getString(2));
            description = removeDuplicates(traps.getString(3));
            type = traps.getString(4);
            cards.add(new Trap(id, name, description, type));
        }
        // Close the connection
        connection.close();
        for(int i = 0; i < cards.size(); i++){
            System.out.println(cards.get(i).toString());
        }
    }
}