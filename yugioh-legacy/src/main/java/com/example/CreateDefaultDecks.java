package com.example;

import java.sql.*;
import java.util.ArrayList;

public class CreateDefaultDecks {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String m = "name = 'Breaker the Magical Warrior' or name = 'Buster Blader' or name = 'Dark Magician Girl' or name = 'Dark Magician' or name = 'Defender, the Magical Knight' or name = 'Skilled Dark Magician' or name = 'Sorcerer of Dark Magic' or name = 'Gagaga Magician' or name = 'Magician of Black Chaos' or name = 'Summoner Monk' or name = 'Silent Magician LV4' or name = 'Old Vindictive Magician' or name = 'Silent Magician LV8' or name = 'Endymion The Master Magician' or name = 'Dark Paladin';";
        String s = "name = 'Dark Magic Attack' or name = 'Magical Dimension' or name = 'Mystical Space Typhoon' or name = 'Polymerization' or name = 'Thousand Knives' or name = 'Level Lifter' or name = 'Dark Magic Curtain' or name = 'Black Magic Ritual' or name = 'Emblem of Dragon Destroyer';";
        String t = "name = 'Call of The Haunted' or name = 'Raigeki Break' or name = 'Black Illusion' or name = 'Torrential Tribute' or name = 'Magic Cylinder' or name = 'Sakuretsu Armor' or name = 'Mirror Force';";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards" , "root", "password goes here");
        // Create a statement
        Statement statement1 = connection.createStatement();
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();

        // Execute a statement
        ResultSet monsters = statement1.executeQuery("select id from Monsters where " + m);
        ResultSet spells = statement2.executeQuery("select id from Spells where " + s);
        ResultSet traps = statement3.executeQuery("select id from Traps where " + t);
        // Iterate through the result and print the student names
        ArrayList<String> mList = new ArrayList<>();
        ArrayList<String> sList = new ArrayList<>();
        ArrayList<String> tList = new ArrayList<>();
        while(monsters.next()){
            mList.add(monsters.getString(1));
        }
        while(spells.next()){
            sList.add(spells.getString(1));
        }
        while(traps.next()){
            tList.add(traps.getString(1));
        }
        // Close the connection
        connection.close();
        System.out.println("Monsters:\n" + mList);
        System.out.println("Spells:\n" + sList);
        System.out.println("Traps:\n" + tList);
    }
}
