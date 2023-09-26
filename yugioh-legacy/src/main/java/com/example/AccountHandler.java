package com.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AccountHandler{
    String username, password;
    int wins, losses, currentDeck;
    boolean loggedIn;

    public AccountHandler(String un, String pw) {
        username = un;
        password = pw;
        loggedIn = false;
    }

    public AccountHandler() {
        username = "";
        password = "";
        loggedIn = false;
    }

    public void login(String un, String pw) throws SQLException, ClassNotFoundException {
        username = un;
        password = pw;
        login();
    }
    public void logOut(){
        username = "";
        password = "";
        loggedIn = false;
    }
    public String getUsername(){return username;}

    public void login() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
        Statement statement = connection.createStatement();
        ResultSet account = statement.executeQuery("select username, wins, losses from Accounts where username = '" + username +
                "' and password = '" + password + "';");

        if (account.next() && account.getString(1).equals(username)) {
            loggedIn = true;
            wins = account.getInt(2);
            losses = account.getInt(3);
        }
    }

    public void createAccount(String un, String pw) throws ClassNotFoundException, SQLException {
        username = un;
        password = pw;
        createAccount();
    }

    public void createAccount() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
        Statement statement = connection.createStatement();
        ResultSet account = statement.executeQuery("select username from Accounts where username = '" + username + "';");
        if (account.next()) {
            loggedIn = false;
        } else {
            Statement newStatement = connection.createStatement();
            String sql = "insert into Accounts VALUES ('" + username + "', '" + password + "', 0, 0, '1/2/', 1);";
            newStatement.executeUpdate(sql);
            wins = 0;
            losses = 0;
            loggedIn = true;
        }
    }

    public int getWins() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            int wins = 0;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select wins from Accounts where username = '" + username +
                    "' and password = '" + password + "';");
            if(account.next()) {
                wins = account.getInt(1);
                return wins;
            }
        }
        return 0;
    }

    public int getLosses() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select losses from Accounts where username = '" + username +
                    "' and password = '" + password + "';");
            while (account.next()) {
                losses = account.getInt(1);
                return losses;
            }
        }
        return 0;
    }

    public void addWin() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            wins = wins + 1;
            Statement statement = connection.createStatement();
            statement.executeUpdate("update Accounts set wins = " + wins + " where username = '" + username +
                    "' and password = '" + password + "';");
        }
    }

    public void addLoss() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            losses = losses + 1;
            Statement statement = connection.createStatement();
            statement.executeUpdate("update Accounts set losses = " + losses + " where username = '" + username +
                    "' and password = '" + password + "';");
        }
    }

    public ArrayList<String> getDeckNames() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            String[] tempDecks = new String[277];
            ArrayList<String> userDecks = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select decks from Accounts where username = '" + username +
                    "' and password = '" + password + "';");
            while (account.next()) {
                tempDecks = account.getString(1).split("/");
            }
            for (int i = 0; i < tempDecks.length; i++) {
                statement = connection.createStatement();
                account = statement.executeQuery("select name from Decks where id = " + i + ";");
                if (account.next()) {
                    userDecks.add(account.getString(1));
                }
            }
            return userDecks;
        }
        return null;
    }

    public int getCurrentDeckNumber() throws ClassNotFoundException, SQLException {
        if (loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select currentDeck from Accounts where username = '" + username +
                    "' and password = '" + password + "';");
            while(account.next()) {
                currentDeck = account.getInt(1);
            }
            return currentDeck;
        }
        return 0;
    }

    public String getDeckName(int deckNum) throws ClassNotFoundException, SQLException {
        if(loggedIn){
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select name from Decks, Accounts where Accounts.username = '" +
                    username + "' and " + deckNum + " = Decks.id;");
            while(account.next()) {
                return account.getString(1);
            }
        }
        return null;
    }
    public ArrayList<Integer> getUserDecks() throws ClassNotFoundException, SQLException {
        if(loggedIn){
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select decks from Decks, Accounts where Accounts.username = '" +
                    username + "' and Accounts.currentDeck = Decks.id;");
            if(account.next()) {
                String[] tempDecks = account.getString(1).split("/");
                ArrayList<Integer> decks = new ArrayList<>();
                for(int i = 0; i < tempDecks.length; i++){
                    decks.add(Integer.parseInt(tempDecks[i]));
                }
                return decks;
            }
        }
        return null;
    }
    public void setCurrentDeck(int deckNum) throws ClassNotFoundException, SQLException {
        if(loggedIn) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            statement.executeUpdate("update Accounts set currentDeck = " + deckNum + " where username = '" +
                    username + "';");
        }
    }
    public void createNewDeck(String name, ArrayList<String> monsters, ArrayList<String> spells, ArrayList<String> traps)
            throws ClassNotFoundException, SQLException {
        if(loggedIn){
            String monsterDeck = "";
            String spellDeck = "";
            String trapDeck = "";
            for(int i = 0; i < monsters.size(); i++){
             monsterDeck = monsterDeck + monsters.get(i) + "/";
            }
            for(int i = 0; i < spells.size(); i++){
                spellDeck = spellDeck + spells.get(i) + "/";
            }
            for(int i = 0; i < traps.size(); i++){
                trapDeck = trapDeck + traps.get(i) + "/";
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into Decks VALUES (NULL, '" + name + "', '" + monsterDeck + "', '" +
                    spellDeck + "', '" + trapDeck + "');");
            Statement newStatement = connection.createStatement();
            ResultSet resultSet = newStatement.executeQuery("select id from Decks where name = '" + name +"' and " +
                    "monster = '" + monsterDeck + "' and spell = '" + spellDeck + "' and trap = '" + trapDeck +"';");
            ArrayList<Integer> userDecks = getUserDecks();
            String newDeckSet = "";
            for(int i = 0; i < userDecks.size(); i++){
                newDeckSet = newDeckSet + userDecks.get(i) + "/";
            }
            if(resultSet.next()){
                newDeckSet = newDeckSet + resultSet.getString(1) + "/";
                Statement finalStatement = connection.createStatement();
                finalStatement.executeUpdate("update Accounts set decks = '" + newDeckSet + "' where username = '" + username +
                        "' and password = '" + password + "';");
            }
        }
    }

    public ArrayList<String> getDeckMonsters(int deckNum) throws ClassNotFoundException, SQLException {
        if(loggedIn){
            ArrayList<String> monsters = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select monster from Decks where id = " + deckNum + ";");
            if(account.next()) {
                String[] temp = account.getString(1).split("/");
                monsters.addAll(Arrays.asList(temp));
            }
            return monsters;
        }
        return null;
    }
    public ArrayList<String> getDeckSpells(int deckNum) throws ClassNotFoundException, SQLException {
        if(loggedIn){
            ArrayList<String> spells = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select spell from Decks where id = " + deckNum + ";");
            if(account.next()) {
                String[] temp = account.getString(1).split("/");
                spells.addAll(Arrays.asList(temp));
            }
            return spells;
        }
        return null;
    }
    public ArrayList<String> getDeckTraps(int deckNum) throws ClassNotFoundException, SQLException {
        if(loggedIn){
            ArrayList<String> traps = new ArrayList<>();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "Classic Yugioh");
            Statement statement = connection.createStatement();
            ResultSet account = statement.executeQuery("select trap from Decks where id = " + deckNum + ";");
            if(account.next()) {
                String[] temp = account.getString(1).split("/");
                traps.addAll(Arrays.asList(temp));
            }
            return traps;
        }
        return null;
    }
}
