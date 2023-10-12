package com.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class SaveCardImages {
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
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        String imageUrl;
        String destinationFile;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards" , "root", "password goes here");
        // Create a statement
        Statement statement1 = connection.createStatement();
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();

        // Execute a statement
        ResultSet monsters = statement1.executeQuery("select cardImageURL from Monsters");
        ResultSet spells = statement2.executeQuery("select cardImageURL from Spells");
        ResultSet traps = statement3.executeQuery("select cardImageURL from Traps");
        // Iterate through the result and print the student names
        ArrayList<String> cards = new ArrayList<>();
        while(monsters.next()){
            cards.add(monsters.getString(1));
        }
        while(spells.next()){
            cards.add(spells.getString(1));
        }
        while(traps.next()){
            cards.add(traps.getString(1));
        }
        // Close the connection
        connection.close();
        for(int i = 0; i < cards.size(); i++){
            imageUrl =cards.get(i);
            destinationFile = imageUrl.replace("https://images.ygoprodeck.com/images/cards/", "");
            saveImage(imageUrl, destinationFile);
        }
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

}
