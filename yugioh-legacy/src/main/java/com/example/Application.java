package com.example;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class Application extends javafx.application.Application {
    //False when opponent's turn, True when player's turn
    private final boolean[] playerTurn = {true};
    //Keeps track of current phase internally
    //0 = SP, 1 = DP,  2 = MP1, 3 = BP, 4 = MP2, 5 = EP
    private final int[] currentPhase = {2};
    //checks if a monster was played by whoever's turn it is
    private final boolean[] wasMonsterPlayed = {false};
    private VBox lifePoints = new VBox();
    private HBox playerHand = new HBox();
    private HBox opponentHand = new HBox();
    private Label playerLifePoints = new Label();
    private Label opponentLifePoints = new Label();
    private StackPane playerGraveyard = new StackPane();
    private StackPane playerDeck = new StackPane();
    private StackPane playerFieldSpell = new StackPane();
    private StackPane playerBanished = new StackPane();
    private StackPane playerMonster1 = new StackPane();
    private StackPane playerMonster2 = new StackPane();
    private StackPane playerMonster3 = new StackPane();
    private StackPane playerMonster4 = new StackPane();
    private StackPane playerMonster5 = new StackPane();
    private StackPane playerSpell1 = new StackPane();
    private StackPane playerSpell2 = new StackPane();
    private StackPane playerSpell3 = new StackPane();
    private StackPane playerSpell4 = new StackPane();
    private StackPane playerSpell5 = new StackPane();
    private StackPane opponentGraveyard = new StackPane();
    private StackPane opponentDeck = new StackPane();
    private StackPane opponentFieldSpell = new StackPane();
    private StackPane opponentBanished = new StackPane();
    private StackPane opponentMonster1 = new StackPane();
    private StackPane opponentMonster2 = new StackPane();
    private StackPane opponentMonster3 = new StackPane();
    private StackPane opponentMonster4 = new StackPane();
    private StackPane opponentMonster5 = new StackPane();
    private StackPane opponentSpell1 = new StackPane();
    private StackPane opponentSpell2 = new StackPane();
    private StackPane opponentSpell3 = new StackPane();
    private StackPane opponentSpell4 = new StackPane();
    private StackPane opponentSpell5 = new StackPane();
    private ScrollPane deckScroll1 = new ScrollPane();
    private ScrollPane deckScroll2 = new ScrollPane();
    private ScrollPane deckScroll3 = new ScrollPane();
    private Label deckStatus = new Label();
    //Keeps track of the row for deck builder and deck selector.
    private final int[] deckRow = {-1};
    //Keeps track of the column for deck builder and deck selector.
    private final int[] deckColumn = {0};
    //The starting Life Points for a match (always 8000 for now. In future versions will switch between 2000. 4000, and 8000
    private int startingLP;
    //Used to keep track of monsters being added to a deck in deckBuilder
    private ArrayList<String> newDeckM = new ArrayList<>();
    //Used to keep track of spells being added to a deck in deckBuilder
    private ArrayList<String> newDeckS = new ArrayList<>();
    //Used to keep track of traps being added to a deck in deckBuilder
    private ArrayList<String> newDeckT = new ArrayList<>();
    private GridPane userDeck = new GridPane();
    private GridPane cardPane = new GridPane();
    private Button monsters = new Button("Monsters");
    private Button spells = new Button("Spells");
    private Button traps = new Button("Traps");
    private Button clearDeck = new Button("Clear Deck");
    private Button saveDeck = new Button("Save Deck");
    private TextField deckName = new TextField();
    private TextField searchCards = new TextField();
    private Button search = new Button("Search");
    private HBox deckButtons = new HBox();
    //Holds all cards searched for in the current search in deck builder
    private ArrayList<Card> searchedCards = new ArrayList<>();
    //Holds all monsters in the database
    private ArrayList<Card> implementedCardsM = new ArrayList<>();
    //Holds all spells in the database
    private ArrayList<Card> implementedCardsS = new ArrayList<>();
    //Holds all traps in the database
    private ArrayList<Card> implementedCardsT = new ArrayList<>();
    //Holds all the cards in a deck to be shown in DeckSelector
    private ArrayList<Card> deckView = new ArrayList<>();
    private BackgroundImage backgroundImage = new BackgroundImage(new Image(new FileInputStream(
            "images/Background.jpg")), null, null, null,
            null);
    private Background background = new Background(backgroundImage);
    private ImageView cardFront;
    private ImageView cardBack = new ImageView(new Image(new FileInputStream("images/back.jpg")));
    private Image gameCardBack = new Image(new FileInputStream("images/back.jpg"));
    private AccountHandler account = new AccountHandler();
    private TextField tfUserName = new TextField();
    private PasswordField tfPassword = new PasswordField();
    private Label status = new Label();
    private Button btLogin = new Button("Log In");
    private Button btNewAct = new Button("Create Account");
    private Label un = new Label("Username:  ");
    private Label pw = new Label("Password:  ");
    private Label name = new Label();
    private Label description = new Label();
    private Label element = new Label();
    private Label type = new Label();
    private Label atkDefLv = new Label();
    private Button startGame = new Button("Play A Game");
    private Button deckSelector = new Button("Choose Your Deck");
    private Button deckBuilder = new Button("Build Your Deck");
    private Button viewStats = new Button("View Stats");
    private Button logOut = new Button("Log Out");
    private Button returnToMenu = new Button("Main Menu");
    private Label stats = new Label();
    private GridPane cardInfo = new GridPane();
    private int wins;
    private int losses;
    private int currentDeck;
    private Client client;
    private Socket socket;
    public Application() throws FileNotFoundException{}
    private Scene ConnectingScreen(Stage stage) throws IOException{
        socket = new Socket("localhost", 1234);
        client = new Client(socket, account.username);
        final boolean[] connected = {false};
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(background);
        final String[] connectingText = {"SEARCHING FOR A GAME PLEASE WAIT"};
        Label connecting = new Label(connectingText[0]);
        borderPane.setCenter(connecting);
        Platform.runLater(()->{
            while(true){
                connecting.setText(connectingText[0]);
            }
        });
        new Thread(() -> {
            while(!connected[0]){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(connecting.getText().equals("SEARCHING FOR A GAME PLEASE WAIT") ||
                        connecting.getText().equals("SEARCHING FOR A GAME PLEASE WAIT.") ||
                        connecting.getText().equals("SEARCHING FOR A GAME PLEASE WAIT..")){
                    connectingText[0] += ".";
                }
                else{
                    connectingText[0] = "SEARCHING FOR A GAME PLEASE WAIT";
                }
            }
        }).start();
        while (client.listenForMessage() == null){
            connecting.setText(connectingText[0]);
        }
        if(client.listenForMessage() != null && client.listenForMessage().equals("Start")){
            connected[0] = true;
            try {
                stage.setScene(GameScreen(stage));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new Scene(borderPane, 1388, 750);
    }
    private Scene DeckSelector(Stage stage) throws SQLException, ClassNotFoundException, FileNotFoundException {
        BorderPane borderPane = new BorderPane();
        ScrollPane scrollPane1 = new ScrollPane();
        ScrollPane scrollPane2 = new ScrollPane();
        ScrollPane scrollPane3 = new ScrollPane();
        borderPane.setBackground(background);
        scrollPane1.setBackground(background);
        scrollPane2.setBackground(background);
        scrollPane3.setBackground(background);
        cardPane.setBackground(background);
        scrollPane3.setContent(cardInfo);
        cardPane.getChildren().clear();
        scrollPane2.setContent(cardPane);
        cardPane.setHgap(10);
        cardPane.setVgap(10);
        HBox hBox = new HBox();
        hBox.setBackground(background);
        scrollPane1.setContent(hBox);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        ArrayList<Integer> decks;
        decks = account.getUserDecks();
        Label deckInfo = new Label("Current Deck:  " + account.getDeckName(currentDeck));
        borderPane.setBackground(background);
        borderPane.setTop(scrollPane1);
        borderPane.setCenter(scrollPane2);
        borderPane.setRight(scrollPane3);
        hBox.getChildren().add(returnToMenu);
        deckInfo.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        deckInfo.setTextFill(Color.DARKBLUE);
        hBox.getChildren().add(deckInfo);
        showDeck(currentDeck);
        //Creates a button for each deck held in the account
        for(int i = 0; i < decks.size(); i++){
            Button deck = new Button();
            deck.setText(i + " " + account.getDeckName(decks.get(i)));
            hBox.getChildren().add(deck);
            //Clicking on any deck button will show all the cards in the deck within the gridPane below
            deck.setOnAction(e->{
                try {
                    String[] s = deck.getText().split(" ");
                    currentDeck = decks.get(Integer.parseInt(s[0]));
                    account.setCurrentDeck(currentDeck);
                    deckInfo.setText("Current Deck:  " + account.getDeckName(currentDeck));
                    showDeck(currentDeck);
                } catch (ClassNotFoundException | SQLException | FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        returnToMenu.setOnAction(e->{
            stage.setScene(MainMenu(stage));
        });
        return new Scene(borderPane, 1388, 750);
    }
    private Scene StartScreen(Stage stage){
        name.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        description.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        element.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        type.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        atkDefLv.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        name.setTextFill(Color.DARKBLUE);
        description.setTextFill(Color.DARKBLUE);
        element.setTextFill(Color.DARKBLUE);
        type.setTextFill(Color.DARKBLUE);
        atkDefLv.setTextFill(Color.DARKBLUE);
        name.setText("N/A");
        description.setText("N/A");
        element.setText("N/A");
        type.setText("N/A");
        atkDefLv.setText("N/A");
        un.setTextFill(Color.BLACK);
        un.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        pw.setTextFill(Color.BLACK);
        pw.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        status.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        GridPane pane1 = new GridPane();
        GridPane pane2 = new GridPane();
        GridPane pane3 = new GridPane();
        GridPane mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        pane1.add(un, 0, 0);
        pane1.add(tfUserName, 1, 0);
        pane1.add(pw, 0, 1);
        pane1.add(tfPassword, 1, 1);
        pane2.add(btLogin, 0, 0);
        pane2.add(btNewAct, 1, 0);
        pane3.add(status, 0, 0);
        pane2.setHgap(65);
        mainPane.add(pane1, 0, 0);
        mainPane.add(pane2, 0, 1);
        mainPane.add(pane3, 0, 2);
        mainPane.setBackground(background);
        btLogin.setOnAction(e->{
            if(tfUserName.getText().equals("") && tfPassword.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Please Enter a Username and Password");
            }
            else if(tfUserName.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Username Field Left Blank!");
            }
            else if(tfPassword.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Password Field Left Blank!");
            }
            else{
                try {
                    account.login(tfUserName.getText().toLowerCase(), PasswordHash.hashPassword(tfPassword.getText()));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                if (account.loggedIn) {
                    try {
                        currentDeck = account.getCurrentDeckNumber();
                    } catch (ClassNotFoundException | SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    stage.setScene(MainMenu(stage));
                } else {
                    status.setTextFill(Color.RED);
                    status.setText("Username Or Password Incorrect!\nTry Again.");
                }
            }
            tfPassword.clear();
            tfUserName.clear();
        });
        btNewAct.setOnAction(e->{
            if(tfUserName.getText().equals("") && tfPassword.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Please Enter a Username and Password");
            }
            else if(tfUserName.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Username Field Left Blank!");
            }
            else if(tfPassword.getText().equals("")){
                status.setTextFill(Color.RED);
                status.setText("Password Field Left Blank!");
            }
            else {
                try {
                    account.createAccount(tfUserName.getText().toLowerCase(),
                            PasswordHash.hashPassword(tfPassword.getText()));

                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
                if (account.loggedIn) {
                    try {
                        currentDeck = account.getCurrentDeckNumber();
                    } catch (ClassNotFoundException | SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    stage.setScene(MainMenu(stage));
                } else {
                    status.setTextFill(Color.RED);
                    status.setText("Username Already Exists!\nTry Another Name.");
                }
            }
            tfPassword.clear();
            tfUserName.clear();
        });
        return new Scene(mainPane, 1388, 750);
    }
    private Scene DeckBuilder(Stage stage) throws SQLException, ClassNotFoundException, FileNotFoundException {
        BorderPane deckBorder = new BorderPane();
        VBox bottomVBox = new VBox();
        searchCards.setPromptText("Enter part of a card's name here! (Letters, Numbers, and Spaces only!");
        bottomVBox.getChildren().addAll(searchCards, search);
        bottomVBox.setSpacing(20);
        bottomVBox.setBackground(background);
        deckBorder.setBottom(bottomVBox);
        deckStatus.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        deckStatus.setTextFill(Color.BLACK);
        deckStatus.setText("Create Your Deck");
        userDeck.getChildren().clear();
        cardPane.getChildren().clear();
        userDeck.setBackground(background);
        cardPane.setBackground(background);
        cardPane.setHgap(10);
        deckScroll2.setBackground(background);
        deckScroll1.setBackground(background);
        deckScroll3.setBackground(background);
        cardPane.setVgap(10);
        getAllMonsters();
        getAllSpells();
        getAllTraps();
        showMonsters();
        deckBorder.setBackground(background);
        deckButtons.setSpacing(40);
        deckButtons.getChildren().clear();
        deckButtons.getChildren().add(returnToMenu);
        deckButtons.getChildren().add(monsters);
        deckButtons.getChildren().add(spells);
        deckButtons.getChildren().add(traps);
        deckButtons.getChildren().add(saveDeck);
        deckButtons.getChildren().add(clearDeck);
        deckButtons.getChildren().add(deckName);
        deckButtons.getChildren().add(deckStatus);
        deckName.setPromptText("Enter Your Deck Name");
        deckBorder.setTop(deckButtons);
        deckScroll3.setContent(userDeck);
        deckScroll2.setContent(cardInfo);
        deckBorder.setLeft(deckScroll3);
        deckBorder.setRight(deckScroll2);
        deckScroll1.setContent(cardPane);
        deckBorder.setCenter(deckScroll1);
        monsters.setOnAction(e->{
            try {
                showMonsters();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        spells.setOnAction(e->{
            try {
                showSpells();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        traps.setOnAction(e->{
            try {
                showTraps();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        search.setOnAction(e->{
            boolean goodName = false;

            char[] chars = searchCards.getText().toCharArray();
            for(int i = 0; i < searchCards.getText().length(); i++) {
                if (Character.isLetterOrDigit(chars[i]) || chars[i] == ' ') {
                    goodName = true;
                } else {
                    goodName = false;
                    break;
                }
            }
            if(!goodName){
                searchCards.clear();
                searchCards.setPromptText("PLEASE ONLY USE LETTERS, NUMBERS, AND/OR SPACES!");
            }
            else{
                try {
                    getSearchedCards(searchCards.getText());
                    showSearchedCards();
                } catch (FileNotFoundException | ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        saveDeck.setOnAction(e->{
            char[] chars = deckName.getText().toCharArray();
            boolean goodName = false;
            boolean goodDeck = false;
            for(int i = 0; i < deckName.getText().length(); i++){
                if(Character.isLetterOrDigit(chars[i]) || chars[i] == ' '){
                    goodName = true;
                }
                else{
                    goodName = false;
                    break;
                }
            }
            if(newDeckM.size() > 0 && newDeckT.size() > 0 && newDeckS.size() > 0 && (newDeckM.size() + newDeckS.size() +
                    newDeckT.size()) >=  40 && (newDeckM.size() + newDeckS.size() + newDeckT.size()) <= 60){
                goodDeck = true;
            }
            if(!goodName){
                deckStatus.setTextFill(Color.RED);
                deckStatus.setText("INVALID NAME! Please only A-Z, 0-9 and Spaces");
            }
            else if(!goodDeck){
                deckStatus.setTextFill(Color.RED);
                deckStatus.setText("INVALID DECK! A deck contains 40-60 cards and 1 of each!");
            }
            else {
                try {
                    account.createNewDeck(deckName.getText(), newDeckM, newDeckS, newDeckT);
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
                deckStatus.setTextFill(Color.GREEN);
                deckStatus.setText("Deck Successfully Saved!");
            }
        });
        clearDeck.setOnAction(e->{
            newDeckM.clear();
            newDeckS.clear();
            newDeckT.clear();
            userDeck.getChildren().clear();
            deckColumn[0] = 0;
            deckRow[0] = -1;
            deckStatus.setTextFill(Color.BLACK);
            deckStatus.setText("Create Your Deck");
        });
        returnToMenu.setOnAction(e->{
            newDeckM.clear();
            newDeckS.clear();
            newDeckT.clear();
            userDeck.getChildren().clear();
            deckColumn[0] = 0;
            deckRow[0] = -1;
            stage.setScene(MainMenu(stage));
        });
        return new Scene(deckBorder, 1388, 750);
    }
    private Scene GameScreen(Stage stage) throws SQLException, ClassNotFoundException {
        Player player = new Player(createPlayerDeck(), 8000);
        Player opponent = new Player(createOpponentDeck(), 8000);
        BorderPane endScreen = new BorderPane();
        Stage popUp = new Stage();
        Label label = new Label();
        VBox vBox = new VBox();
        startingLP = player.getLifePoints();
        BorderPane gamePane = new BorderPane();
        //False when opponent's turn, True when player's turn
        playerTurn[0] = true;
        //Keeps track of current phase internally
        //0 = SP, 1 = DP,  2 = MP1, 3 = BP, 4 = MP2, 5 = EP
        currentPhase[0] = 2;
        wasMonsterPlayed[0] = false;
        final int[] playerTurnCount = {0};
        final int[] opponentTurnCount = {0};
        HBox phaseTracker = new HBox();
        Button standbyPhase = new Button("SP");
        Button drawPhase = new Button("DP");
        Button mainPhase1 = new Button("MP1");
        Button battlePhase = new Button("BP");
        Button mainPhase2 = new Button("MP2");
        Button endPhase = new Button("EP");
        standbyPhase.setDisable(true);
        drawPhase.setDisable(true);
        mainPhase1.setDisable(false);
        battlePhase.setDisable(true);
        mainPhase2.setDisable(true);
        endPhase.setDisable(true);
        phaseTracker.setSpacing(70);
        phaseTracker.getChildren().addAll(standbyPhase, drawPhase, mainPhase1, battlePhase, mainPhase2, endPhase);
        lifePoints.getChildren().clear();
        playerHand.getChildren().clear();
        opponentHand.getChildren().clear();
        playerGraveyard.getChildren().clear();
        playerDeck.getChildren().clear();
        playerFieldSpell.getChildren().clear();
        playerBanished.getChildren().clear();
        playerMonster1.getChildren().clear();
        playerMonster2.getChildren().clear();
        playerMonster3.getChildren().clear();
        playerMonster4.getChildren().clear();
        playerMonster5.getChildren().clear();
        playerSpell1.getChildren().clear();
        playerSpell2.getChildren().clear();
        playerSpell3.getChildren().clear();
        playerSpell4.getChildren().clear();
        playerSpell5.getChildren().clear();
        opponentGraveyard.getChildren().clear();
        opponentDeck.getChildren().clear();
        opponentFieldSpell.getChildren().clear();
        opponentBanished.getChildren().clear();
        opponentMonster1.getChildren().clear();
        opponentMonster2.getChildren().clear();
        opponentMonster3.getChildren().clear();
        opponentMonster4.getChildren().clear();
        opponentMonster5.getChildren().clear();
        opponentSpell1.getChildren().clear();
        opponentSpell2.getChildren().clear();
        opponentSpell3.getChildren().clear();
        opponentSpell4.getChildren().clear();
        opponentSpell5.getChildren().clear();
        Button nextPhase = new Button("Next Phase");
        Button endTurn = new Button("End Turn");
        HBox buttons = new HBox();
        opponentLifePoints.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        opponentLifePoints.setTextFill(Color.DARKBLUE);
        opponentLifePoints.setText("Opponent's Life Points:\n" + opponent.getLifePoints() + " / " + startingLP);
        playerLifePoints.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        playerLifePoints.setTextFill(Color.DARKBLUE);
        playerLifePoints.setText(account.getUsername() + "'s Life Points:\n" + player.getLifePoints() + " / " + startingLP);
        lifePoints.getChildren().add(opponentLifePoints);
        lifePoints.getChildren().add(buttons);
        lifePoints.getChildren().add(playerLifePoints);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(returnToMenu, nextPhase, endTurn);
        gamePane.setBackground(background);
        playerHand.setBackground(background);
        opponentHand.setBackground(background);
        ScrollPane scrollPane1 = new ScrollPane();
        ScrollPane scrollPane2 = new ScrollPane();
        ScrollPane scrollPane3 = new ScrollPane();
        scrollPane1.setBackground(background);
        scrollPane2.setBackground(background);
        scrollPane3.setBackground(background);
        scrollPane1.setContent(cardInfo);
        scrollPane2.setContent(playerHand);
        scrollPane3.setContent(opponentHand);
        gamePane.setBottom(scrollPane2);
        gamePane.setTop(scrollPane3);
        gamePane.setRight(scrollPane1);
        gamePane.setLeft(lifePoints);
        lifePoints.setAlignment(Pos.CENTER_LEFT);
        setStackPaneRectangleInfo(playerGraveyard);
        setStackPaneRectangleInfo(playerDeck);
        setStackPaneRectangleInfo(playerFieldSpell);
        setStackPaneRectangleInfo(playerBanished);
        setStackPaneRectangleInfo(playerMonster1);
        setStackPaneRectangleInfo(playerMonster2);
        setStackPaneRectangleInfo(playerMonster3);
        setStackPaneRectangleInfo(playerMonster4);
        setStackPaneRectangleInfo(playerMonster5);
        setStackPaneRectangleInfo(playerSpell1);
        setStackPaneRectangleInfo(playerSpell2);
        setStackPaneRectangleInfo(playerSpell3);
        setStackPaneRectangleInfo(playerSpell4);
        setStackPaneRectangleInfo(playerSpell5);
        setStackPaneRectangleInfo(opponentGraveyard);
        setStackPaneRectangleInfo(opponentDeck);
        setStackPaneRectangleInfo(opponentFieldSpell);
        setStackPaneRectangleInfo(opponentBanished);
        setStackPaneRectangleInfo(opponentMonster1);
        setStackPaneRectangleInfo(opponentMonster2);
        setStackPaneRectangleInfo(opponentMonster3);
        setStackPaneRectangleInfo(opponentMonster4);
        setStackPaneRectangleInfo(opponentMonster5);
        setStackPaneRectangleInfo(opponentSpell1);
        setStackPaneRectangleInfo(opponentSpell2);
        setStackPaneRectangleInfo(opponentSpell3);
        setStackPaneRectangleInfo(opponentSpell4);
        setStackPaneRectangleInfo(opponentSpell5);
        playerGraveyard.getChildren().add(new Label("Graveyard\nZone"));
        playerDeck.getChildren().add(new Label("Deck\nZone"));
        playerFieldSpell.getChildren().add(new Label("Field Spell\nZone"));
        playerBanished.getChildren().add(new Label("Banished\nZone"));
        playerMonster1.getChildren().add(new Label("Monster\nZone"));
        playerMonster2.getChildren().add(new Label("Monster\nZone"));
        playerMonster3.getChildren().add(new Label("Monster\nZone"));
        playerMonster4.getChildren().add(new Label("Monster\nZone"));
        playerMonster5.getChildren().add(new Label("Monster\nZone"));
        playerSpell1.getChildren().add(new Label("Spell/Trap\nZone"));
        playerSpell2.getChildren().add(new Label("Spell/Trap\nZone"));
        playerSpell3.getChildren().add(new Label("Spell/Trap\nZone"));
        playerSpell4.getChildren().add(new Label("Spell/Trap\nZone"));
        playerSpell5.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentGraveyard.getChildren().add(new Label("Graveyard\nZone"));
        opponentDeck.getChildren().add(new Label("Deck\nZone"));
        opponentFieldSpell.getChildren().add(new Label("Field Spell\nZone"));
        opponentBanished.getChildren().add(new Label("Banished\nZone"));
        opponentMonster1.getChildren().add(new Label("Monster\nZone"));
        opponentMonster2.getChildren().add(new Label("Monster\nZone"));
        opponentMonster3.getChildren().add(new Label("Monster\nZone"));
        opponentMonster4.getChildren().add(new Label("Monster\nZone"));
        opponentMonster5.getChildren().add(new Label("Monster\nZone"));
        opponentSpell1.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentSpell2.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentSpell3.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentSpell4.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentSpell5.getChildren().add(new Label("Spell/Trap\nZone"));
        opponentDeck.getChildren().add(new ImageView(gameCardBack));
        playerDeck.getChildren().add(new ImageView(gameCardBack));
        if(playerDeck.getChildren().get(2) instanceof ImageView){
            ((ImageView) playerDeck.getChildren().get(2)).setFitWidth(59);
            ((ImageView) playerDeck.getChildren().get(2)).setFitHeight(86);
        }
        if(opponentDeck.getChildren().get(2) instanceof ImageView){
            ((ImageView) opponentDeck.getChildren().get(2)).setFitWidth(59);
            ((ImageView) opponentDeck.getChildren().get(2)).setFitHeight(86);
        }
        VBox opponentVBox = new VBox();
        VBox playerVBox = new VBox();
        HBox opponentTop = new HBox();
        HBox opponentBottom = new HBox();
        HBox userTop = new HBox();
        HBox userBottom = new HBox();
        opponentVBox.getChildren().addAll(opponentTop, opponentBottom);
        playerVBox.getChildren().addAll(userTop, userBottom);
        opponentTop.getChildren().addAll(opponentDeck, opponentSpell5, opponentSpell4, opponentSpell3, opponentSpell2,
                opponentSpell1, opponentBanished);
        opponentBottom.getChildren().addAll(opponentGraveyard, opponentMonster5, opponentMonster4, opponentMonster3,
                opponentMonster2, opponentMonster1, opponentFieldSpell);
        userTop.getChildren().addAll(playerFieldSpell, playerMonster1, playerMonster2, playerMonster3, playerMonster4,
                playerMonster5, playerGraveyard);
        userBottom.getChildren().addAll(playerBanished, playerSpell1, playerSpell2, playerSpell3, playerSpell4, playerSpell5,
                playerDeck);
        BorderPane gameBoard = new BorderPane();
        phaseTracker.setAlignment(Pos.CENTER);
        gamePane.setCenter(gameBoard);
        gameBoard.setTop(opponentVBox);
        gameBoard.setBottom(playerVBox);
        gameBoard.setCenter(phaseTracker);
        opponentVBox.setAlignment(Pos.TOP_CENTER);
        playerVBox.setAlignment(Pos.BOTTOM_CENTER);
        opponentTop.setAlignment(Pos.TOP_CENTER);
        userTop.setAlignment(Pos.BOTTOM_CENTER);
        opponentBottom.setAlignment(Pos.TOP_CENTER);
        userBottom.setAlignment(Pos.BOTTOM_CENTER);
        endScreen.setCenter(vBox);
        endScreen.setBackground(background);
        vBox.getChildren().addAll(label, returnToMenu);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.DARKBLUE);
        vBox.setAlignment(Pos.CENTER);
        nextPhase.setOnAction(e->{
            if(player.getLifePoints() == 0){
                try {
                    account.addLoss();
                    label.setText("You Lost!\nBetter Luck Next Time!");
                    Scene scene2 = new Scene(endScreen, 400, 300);
                    popUp.setScene(scene2);
                    popUp.show();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if(opponent.getLifePoints() == 0){
                try {
                    account.addWin();
                    label.setText("Congrats!\nYou Won!!!");
                    Scene scene2 = new Scene(endScreen, 400, 300);
                    popUp.setScene(scene2);
                    popUp.show();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //0 = SP, 1 = DP,  2 = MP1, 3 = BP, 4 = MP2, 5 = EP
            else {
                if (currentPhase[0] == 0) {
                    currentPhase[0]++;
                    standbyPhase.setDisable(true);
                    drawPhase.setDisable(false);
                    if(playerTurn[0]){
                        drawPlayerCard(player);
                    }
                    else{
                        drawOpponentCard(opponent);
                    }
                } else if (currentPhase[0] == 1) {
                    currentPhase[0]++;
                    drawPhase.setDisable(true);
                    mainPhase1.setDisable(false);
                } else if (currentPhase[0] == 2) {
                    currentPhase[0]++;
                    if (opponentTurnCount[0] + playerTurnCount[0] == 0) {
                        currentPhase[0]++;
                        mainPhase1.setDisable(true);
                        mainPhase2.setDisable(false);
                    } else {
                        mainPhase1.setDisable(true);
                        battlePhase.setDisable(false);
                    }
                } else if (currentPhase[0] == 3) {
                    currentPhase[0]++;
                    battlePhase.setDisable(true);
                    mainPhase2.setDisable(false);
                } else if (currentPhase[0] == 4) {
                    if(playerMonster1.getChildren().size() >= 3) {
                        ((GameCard) playerMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster2.getChildren().size() >= 3) {
                        ((GameCard) playerMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster3.getChildren().size() >= 3) {
                        ((GameCard) playerMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster4.getChildren().size() >= 3) {
                        ((GameCard) playerMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster5.getChildren().size() >= 3) {
                        ((GameCard) playerMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster1.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster2.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster3.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster4.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster5.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    wasMonsterPlayed[0] = false;
                    currentPhase[0]++;
                    mainPhase2.setDisable(true);
                    endPhase.setDisable(false);
                    if (playerTurn[0]) {
                        playerTurn[0] = false;
                        playerTurnCount[0]++;
                    } else {
                        playerTurn[0] = true;
                        opponentTurnCount[0]++;
                    }
                    currentPhase[0] = 0;
                    endPhase.setDisable(true);
                    standbyPhase.setDisable(false);
                    currentPhase[0] = 1;
                    standbyPhase.setDisable(true);
                    drawPhase.setDisable(false);
                    if(playerTurn[0]){
                        drawPlayerCard(player);
                    }
                    else{
                        drawOpponentCard(opponent);
                    }
                    currentPhase[0] = 2;
                    drawPhase.setDisable(true);
                    mainPhase1.setDisable(false);

                } else {
                    if(playerMonster1.getChildren().size() >= 3) {
                        ((GameCard) playerMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster2.getChildren().size() >= 3) {
                        ((GameCard) playerMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster3.getChildren().size() >= 3) {
                        ((GameCard) playerMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster4.getChildren().size() >= 3) {
                        ((GameCard) playerMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(playerMonster5.getChildren().size() >= 3) {
                        ((GameCard) playerMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster1.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster2.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster3.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster4.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    if(opponentMonster5.getChildren().size() >= 3) {
                        ((GameCard) opponentMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                    }
                    wasMonsterPlayed[0] = false;
                    if (playerTurn[0]) {
                        playerTurn[0] = false;
                        playerTurnCount[0]++;
                    } else {
                        playerTurn[0] = true;
                        opponentTurnCount[0]++;
                    }
                    currentPhase[0] = 0;
                    endPhase.setDisable(true);
                    standbyPhase.setDisable(false);
                }
            }
        });
        endTurn.setOnAction(e->{
            if(player.getLifePoints() == 0){
                try {
                    account.addLoss();
                    label.setText("You Lost!\nBetter Luck Next Time!");
                    Scene scene2 = new Scene(endScreen, 400, 300);
                    popUp.setScene(scene2);
                    popUp.show();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if(opponent.getLifePoints() == 0){
                try {
                    account.addWin();
                    label.setText("Congrats!\nYou Won!!!");
                    Scene scene2 = new Scene(endScreen, 400, 300);
                    popUp.setScene(scene2);
                    popUp.show();
                } catch (ClassNotFoundException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else {
                if(playerMonster1.getChildren().size() >= 3) {
                    ((GameCard) playerMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(playerMonster2.getChildren().size() >= 3) {
                    ((GameCard) playerMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(playerMonster3.getChildren().size() >= 3) {
                    ((GameCard) playerMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(playerMonster4.getChildren().size() >= 3) {
                    ((GameCard) playerMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(playerMonster5.getChildren().size() >= 3) {
                    ((GameCard) playerMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(opponentMonster1.getChildren().size() >= 3) {
                    ((GameCard) opponentMonster1.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(opponentMonster2.getChildren().size() >= 3) {
                    ((GameCard) opponentMonster2.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(opponentMonster3.getChildren().size() >= 3) {
                    ((GameCard) opponentMonster3.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(opponentMonster4.getChildren().size() >= 3) {
                    ((GameCard) opponentMonster4.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                if(opponentMonster5.getChildren().size() >= 3) {
                    ((GameCard) opponentMonster5.getChildren().get(2)).getMonster().setHasAttacked(false);
                }
                wasMonsterPlayed[0] = false;
                if (playerTurn[0]) {
                    playerTurn[0] = false;
                    playerTurnCount[0]++;
                } else {
                    playerTurn[0] = true;
                    opponentTurnCount[0]++;
                }
                currentPhase[0] = 0;
                standbyPhase.setDisable(false);
                drawPhase.setDisable(true);
                mainPhase1.setDisable(true);
                battlePhase.setDisable(true);
                mainPhase2.setDisable(true);
                endPhase.setDisable(true);
            }
        });
        returnToMenu.setOnAction(e->{
            popUp.close();
            stage.setScene(MainMenu(stage));
        });
        playerMonster1.setOnMouseClicked(e -> attackPlayerMonster(playerMonster1, player, opponent));
        playerMonster2.setOnMouseClicked(e -> attackPlayerMonster(playerMonster2, player, opponent));
        playerMonster3.setOnMouseClicked(e -> attackPlayerMonster(playerMonster3, player, opponent));
        playerMonster4.setOnMouseClicked(e -> attackPlayerMonster(playerMonster4, player, opponent));
        playerMonster5.setOnMouseClicked(e -> attackPlayerMonster(playerMonster5, player, opponent));
        opponentMonster1.setOnMouseClicked(e -> attackOpponentMonster(opponentMonster1, player, opponent));
        opponentMonster2.setOnMouseClicked(e -> attackOpponentMonster(opponentMonster2, player, opponent));
        opponentMonster3.setOnMouseClicked(e -> attackOpponentMonster(opponentMonster3, player, opponent));
        opponentMonster4.setOnMouseClicked(e -> attackOpponentMonster(opponentMonster4, player, opponent));
        opponentMonster5.setOnMouseClicked(e -> attackOpponentMonster(opponentMonster5, player, opponent));
        for(int i = 0; i < 5; i++){
            drawOpponentCard(opponent);
            drawPlayerCard(player);
        }
        return new Scene(gamePane, 1388, 750);
    }
    
    private Scene MainMenu(Stage stage){
        cardInfo.setBackground(background);
        GridPane gridPane = new GridPane();
        gridPane.setBackground(background);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.add(startGame, 0, 0);
        gridPane.add(deckBuilder, 0, 1);
        gridPane.add(deckSelector, 0, 2);
        gridPane.add(viewStats, 0, 3);
        gridPane.add(logOut, 0, 4);
        startGame.setOnAction(e->{
            try {
                setCardInfo("back");
                stage.setScene(GameScreen(stage));
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        deckBuilder.setOnAction(e->{
            try {
                setCardInfo("back");
                stage.setScene(DeckBuilder(stage));
            } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        logOut.setOnAction(e->{
            account.logOut();
            stage.setScene(StartScreen(stage));
            status.setTextFill(Color.DARKBLUE);
            status.setText("Logged Out Successfully!");
        });
        viewStats.setOnAction(e->{
            try {
                wins = account.getWins();
                losses = account.getLosses();
                currentDeck = account.getCurrentDeckNumber();
                stage.setScene(StatsScreen(stage));
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        deckSelector.setOnAction(e->{
            try {
                currentDeck = account.getCurrentDeckNumber();
                stage.setScene(DeckSelector(stage));
            } catch (SQLException | ClassNotFoundException | FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        return new Scene(gridPane, 1388, 750);
    }
    private Scene StatsScreen(Stage stage) throws SQLException, ClassNotFoundException {
        BorderPane mainPane = new BorderPane();
        mainPane.setBackground(background);
        mainPane.setCenter(stats);
        mainPane.setTop(returnToMenu);
        stats.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        stats.setTextFill(Color.DARKBLUE);
        stats.setText("Current Number of Wins:  " + wins  + "\nCurrent Number of Losses:  " + losses +
                "\nCurrent Deck:  " + account.getDeckName(currentDeck));
        returnToMenu.setOnAction(e->{
            stage.setScene(MainMenu(stage));
        });
        return new Scene(mainPane, 1388, 750);
    }
    @Override
    public void start(Stage stage){
        cardInfo.setAlignment(Pos.TOP_CENTER);
        cardBack.setFitWidth(224);
        cardBack.setFitHeight(344);
        name.setText("N/A");
        description.setText("N/A");
        element.setText("N/A");
        type.setText("N/A");
        atkDefLv.setText("N/A");
        cardInfo.add(cardBack, 0, 0);
        cardInfo.add(name, 0, 1);
        cardInfo.add(atkDefLv, 0, 2);
        cardInfo.add(element, 0, 3);
        cardInfo.add(type, 0, 4);
        cardInfo.add(description, 0, 5);
        stage.setTitle("Yugioh Legacy");
        stage.setScene(StartScreen(stage));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
    private void setCardInfo(String cardId) throws SQLException, ClassNotFoundException, FileNotFoundException {
        if(cardId.equals("back")){
            cardBack.setFitWidth(224);
            cardBack.setFitHeight(344);
            name.setText("N/A");
            description.setText("N/A");
            element.setText("N/A");
            type.setText("N/A");
            atkDefLv.setText("N/A");
        }
        else {
            String cardType = getCardType(cardId);
            cardFront = new ImageView(new Image(new FileInputStream("images/" + cardId + ".jpg")));
            cardFront.setFitWidth(224);
            cardFront.setFitHeight(344);
            cardInfo.add(cardFront, 0, 0);
            if (cardType.equals("Spell")) {
                Spell spell = new Spell(cardId);
                name.setText("Card Name:\n" + spell.getName());
                description.setText("Description:\n" + spell.getDescription());
                element.setText("Element:  " + spell.getElement());
                type.setText("Type:  " + spell.getType());
                atkDefLv.setText("N/A");
            } else if (cardType.equals("Trap")) {
                Trap trap = new Trap(cardId);
                name.setText("Card Name:\n" + trap.getName());
                description.setText("Description:\n" + trap.getDescription());
                element.setText("Element:  " + trap.getElement());
                type.setText("Type:  " + trap.getType());
                atkDefLv.setText("N/A");
            } else if (cardType.equals("Monster")) {
                Monster monster = new Monster(cardId);
                name.setText("Card Name:\n" + monster.getName());
                description.setText("Description:\n" + monster.getDescription());
                element.setText("Element:  " + monster.getElement());
                type.setText("Type:  " + monster.getType());
                atkDefLv.setText("ATK/DEF:  " + monster.getAttackPoints() + "/" + monster.getDefensePoints() + "  Level:  "
                        + monster.getLevel());
            } else {
                name.setText("ERROR");
            }
        }
    }
    private String getCardType(String cardId) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "password goes here");
        Statement statement1 = connection.createStatement();
        ResultSet spell = statement1.executeQuery("select element from Spells where id = " + cardId + ";");
        if(spell.next()){
            if(spell.getString(1).equals("Spell Card")) {
                return "Spell";
            }
        }
        Statement statement2 = connection.createStatement();
        ResultSet trap = statement2.executeQuery("select element from Traps where id = " + cardId + ";");
        if(trap.next()){
            if(trap.getString(1).equals("Trap Card")) {
                return "Trap";
            }
        }
        return "Monster";
    }
    private void getAllMonsters() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "password goes here");
        Statement statement1 = connection.createStatement();
        ResultSet monster = statement1.executeQuery("select id from Monsters;");
        while(monster.next()){
            implementedCardsM.add(new Monster(monster.getString(1)));
            monster.next();
        }

    }
    private void getAllSpells() throws ClassNotFoundException, SQLException {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3006/cards", "root", "password goes here");
            Statement statement1 = connection.createStatement();
            ResultSet spell = statement1.executeQuery("select id from Spells;");
            while (spell.next()){
                implementedCardsS.add(new Spell(spell.getString(1)));
                spell.next();
            }

    }
    private void getAllTraps() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Connect to a database
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "password goes here");
        Statement statement1 = connection.createStatement();
        ResultSet trap = statement1.executeQuery("select id from Traps;");
        while (trap.next()){
            implementedCardsT.add(new Trap(trap.getString(1)));
            trap.next();
        }
    }
    private void getSearchedCards(String name) throws ClassNotFoundException, SQLException {
        searchedCards.clear();
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3006/cards", "root", "password goes here");
        Statement statement1 = connection.createStatement();
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();
        ResultSet tempMonsters = statement1.executeQuery("select id from Monsters where name like '%"+name+"%';");
        ResultSet tempSpells = statement2.executeQuery("select id from Spells where name like '%"+name+"%';");
        ResultSet tempTraps = statement3.executeQuery("select id from Traps where name like '%"+name+"%';");
        while(tempMonsters.next()){
            searchedCards.add(new Monster(tempMonsters.getString(1)));
            tempMonsters.next();
        }
        while(tempSpells.next()){
            searchedCards.add(new Spell(tempSpells.getString(1)));
            tempSpells.next();
        }
        while(tempTraps.next()){
            searchedCards.add(new Trap(tempTraps.getString(1)));
            tempTraps.next();
        }
    }
    private void showSearchedCards() throws FileNotFoundException{
        ArrayList<String> m = new ArrayList<>();
        ArrayList<String> s = new ArrayList<>();
        ArrayList<String> t = new ArrayList<>();
        for(int i = 0; i < implementedCardsM.size(); i++){
            m.add(implementedCardsM.get(i).getId());
        }
        for(int i = 0; i < implementedCardsS.size(); i++){
            s.add(implementedCardsS.get(i).getId());
        }
        for(int i = 0; i < implementedCardsT.size(); i++){
            t.add(implementedCardsT.get(i).getId());
        }
        cardPane.getChildren().clear();
        int row = -1;
        int column = 0;
        for(int i = 0; i < searchedCards.size(); i++){
            if(row >= 5){
                column++;
                row = 0;
            }
            else{
                row++;
            }
            ImageView image = new ImageView();
            image.setId(searchedCards.get(i).getId());
            image.setFitHeight(172);
            image.setFitWidth(112);
            image.setImage(new Image(new FileInputStream("images/" + searchedCards.get(i).getId() + ".jpg")));
            cardPane.add(image, row, column);
            image.setOnMouseEntered(e->{
                try {
                    setCardInfo(image.getId());
                } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            image.setOnMouseClicked(e->{
                if(m.contains(image.getId())){
                    int repeatCards = 0;
                    for (int j = 0; j < newDeckM.size(); j++) {
                        if (newDeckM.get(j).equals(image.getId())) {
                            repeatCards++;
                        }
                    }
                    if (repeatCards < 3 && newDeckM.size() < 60) {
                        ImageView newImage = new ImageView();
                        newImage.setFitHeight(172);
                        newImage.setFitWidth(112);
                        if (deckRow[0] >= 2) {
                            deckColumn[0]++;
                            deckRow[0] = 0;
                        } else {
                            deckRow[0]++;
                        }
                        newDeckM.add(image.getId());
                        try {
                            newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                            newImage.setId(image.getId());
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        userDeck.add(newImage, deckRow[0], deckColumn[0]);
                        newImage.setOnMouseEntered(f -> {
                            try {
                                setCardInfo(newImage.getId());
                            } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
                else if(s.contains(image.getId())){
                    int repeatCards = 0;
                    for (int j = 0; j < newDeckS.size(); j++) {
                        if(newDeckS.get(j).equals(image.getId())) {
                            repeatCards++;
                        }
                    }
                    if (repeatCards < 3 && newDeckS.size() < 60) {
                        ImageView newImage = new ImageView();
                        newImage.setFitHeight(172);
                        newImage.setFitWidth(112);
                        if (deckRow[0] >= 2) {
                            deckColumn[0]++;
                            deckRow[0] = 0;
                        } else {
                            deckRow[0]++;
                        }
                        newDeckS.add(image.getId());
                        try {
                            newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                            newImage.setId(image.getId());
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        userDeck.add(newImage, deckRow[0], deckColumn[0]);
                        newImage.setOnMouseEntered(f -> {
                            try {
                                setCardInfo(newImage.getId());
                            } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
                else if(t.contains(image.getId())){
                    int repeatCards = 0;
                    for (int j = 0; j < newDeckT.size(); j++) {
                        if(newDeckT.get(j).equals(image.getId())) {
                            repeatCards++;
                        }
                    }
                    if (repeatCards < 3 && newDeckT.size() < 60) {
                        ImageView newImage = new ImageView();
                        newImage.setFitHeight(172);
                        newImage.setFitWidth(112);
                        if (deckRow[0] >= 2) {
                            deckColumn[0]++;
                            deckRow[0] = 0;
                        } else {
                            deckRow[0]++;
                        }
                        newDeckT.add(image.getId());
                        try {
                            newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                            newImage.setId(image.getId());
                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        userDeck.add(newImage, deckRow[0], deckColumn[0]);
                        newImage.setOnMouseEntered(f -> {
                            try {
                                setCardInfo(newImage.getId());
                            } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
            });
        }
    }
    private void showMonsters() throws FileNotFoundException {
        cardPane.getChildren().clear();
        int row = -1;
        int column = 0;
        for(int i = 0; i < implementedCardsM.size(); i++){
            if(row >= 5){
                column++;
                row = 0;
            }
            else{
                row++;
            }
            ImageView image = new ImageView();
            image.setId(implementedCardsM.get(i).getId());
            image.setFitHeight(172);
            image.setFitWidth(112);
            image.setImage(new Image(new FileInputStream("images/" + implementedCardsM.get(i).getId() + ".jpg")));
            cardPane.add(image, row, column);
            image.setOnMouseEntered(e->{
                try {
                    setCardInfo(image.getId());
                } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            image.setOnMouseClicked(e->{
                int repeatCards = 0;
                for(int j = 0; j < newDeckM.size(); j++){
                    if(newDeckM.get(j).equals(image.getId())){
                        repeatCards++;
                    }
                }
                if(repeatCards < 3 && newDeckM.size() < 60){
                    ImageView newImage = new ImageView();
                    newImage.setFitHeight(172);
                    newImage.setFitWidth(112);
                    if(deckRow[0] >= 2){
                        deckColumn[0]++;
                        deckRow[0] = 0;
                    }
                    else{
                        deckRow[0]++;
                    }
                    newDeckM.add(image.getId());
                    try {
                        newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                        newImage.setId(image.getId());
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    userDeck.add(newImage, deckRow[0], deckColumn[0]);
                    newImage.setOnMouseEntered(f->{
                        try {
                            setCardInfo(newImage.getId());
                        } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            });
        }
    }
    private void showSpells() throws FileNotFoundException {
        cardPane.getChildren().clear();
        int row = -1;
        int column = 0;
        for(int i = 0; i < implementedCardsS.size(); i++){
            if(row >= 5){
                column++;
                row = 0;
            }
            else{
                row++;
            }
            ImageView image = new ImageView();
            image.setId(implementedCardsS.get(i).getId());
            image.setFitHeight(172);
            image.setFitWidth(112);
            image.setImage(new Image(new FileInputStream("images/" + implementedCardsS.get(i).getId() + ".jpg")));
            cardPane.add(image, row, column);
            image.setOnMouseEntered(e->{
                try {
                    setCardInfo(image.getId());
                } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            image.setOnMouseClicked(e->{
                int repeatCards = 0;
                for(int j = 0; j < newDeckS.size(); j++){
                    if(newDeckS.get(j).equals(image.getId())){
                        repeatCards++;
                    }
                }
                if(repeatCards < 3 && newDeckS.size() < 60){
                    ImageView newImage = new ImageView();
                    newImage.setFitHeight(172);
                    newImage.setFitWidth(112);
                    if(deckRow[0] >= 2){
                        deckColumn[0]++;
                        deckRow[0] = 0;
                    }
                    else{
                        deckRow[0]++;
                    }
                    newDeckS.add(image.getId());
                    try {
                        newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                        newImage.setId(image.getId());
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    userDeck.add(newImage, deckRow[0], deckColumn[0]);
                    newImage.setOnMouseEntered(f->{
                        try {
                            setCardInfo(newImage.getId());
                        } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            });
        }
    }
    private void showTraps() throws FileNotFoundException {
        cardPane.getChildren().clear();
        int row = -1;
        int column = 0;
        for(int i = 0; i < implementedCardsT.size(); i++){
            if(row >= 5){
                column++;
                row = 0;
            }
            else{
                row++;
            }
            ImageView image = new ImageView();
            image.setId(implementedCardsT.get(i).getId());
            image.setFitHeight(172);
            image.setFitWidth(112);
            image.setImage(new Image(new FileInputStream("images/" + implementedCardsT.get(i).getId() + ".jpg")));
            cardPane.add(image, row, column);
            image.setOnMouseEntered(e->{
                try {
                    setCardInfo(image.getId());
                } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            image.setOnMouseClicked(e->{
                int repeatCards = 0;
                for(int j = 0; j < newDeckT.size(); j++){
                    if(newDeckT.get(j).equals(image.getId())){
                        repeatCards++;
                    }
                }
                if(repeatCards < 3 && newDeckT.size() < 60){
                    ImageView newImage = new ImageView();
                    newImage.setFitHeight(172);
                    newImage.setFitWidth(112);
                    if(deckRow[0] >= 2){
                        deckColumn[0]++;
                        deckRow[0] = 0;
                    }
                    else{
                        deckRow[0]++;
                    }
                    newDeckT.add(image.getId());
                    try {
                        newImage.setImage(new Image(new FileInputStream("images/" + image.getId() + ".jpg")));
                        newImage.setId(image.getId());
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    userDeck.add(newImage, deckRow[0], deckColumn[0]);
                    newImage.setOnMouseEntered(f->{
                        try {
                            setCardInfo(newImage.getId());
                        } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            });
        }
    }
    private void showDeck(int deckNum) throws FileNotFoundException, SQLException, ClassNotFoundException {
        ArrayList<String> tempDeckM = new ArrayList<>(account.getDeckMonsters(deckNum));
        ArrayList<String> tempDeckS = new ArrayList<>(account.getDeckSpells(deckNum));
        ArrayList<String> tempDeckT = new ArrayList<>(account.getDeckTraps(deckNum));
        deckView.clear();
        cardPane.getChildren().clear();
        for(int i = 0; i < tempDeckM.size(); i++){
            deckView.add(new Monster(tempDeckM.get(i)));
        }
        for(int i = 0; i < tempDeckS.size(); i++){
            deckView.add(new Spell(tempDeckS.get(i)));
        }
        for(int i = 0; i < tempDeckT.size(); i++){
            deckView.add(new Trap(tempDeckT.get(i)));
        }
        int row = -1;
        int column = 0;
        for(int i = 0; i < deckView.size(); i++){
            if(row >= 8){
                column++;
                row = 0;
            }
            else{
                row++;
            }
            ImageView image = new ImageView();
            image.setFitHeight(172);
            image.setFitWidth(112);
            image.setImage(new Image(new FileInputStream("images/" + deckView.get(i).getId() + ".jpg")));
            image.setId(deckView.get(i).getId());
            cardPane.add(image, row, column);
            image.setOnMouseEntered(e->{
                try {
                    setCardInfo(image.getId());
                } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
    private void setStackPaneRectangleInfo(StackPane s){
        Rectangle r = new Rectangle();
        r.setWidth(59);
        r.setHeight(86);
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.DARKBLUE);
        s.getChildren().add(r);
    }
    private Deck createPlayerDeck() throws SQLException, ClassNotFoundException {
        Deck d = new Deck();
        ArrayList<String> monster = account.getDeckMonsters(currentDeck);
        ArrayList<String> spell = account.getDeckSpells(currentDeck);
        ArrayList<String> trap = account.getDeckTraps(currentDeck);
        for(int i = 0; i < monster.size(); i++){
            d.addCard(new Monster(monster.get(i)));
        }
        for(int i = 0; i < spell.size(); i++){
            d.addCard(new Spell(spell.get(i)));
        }
        for(int i = 0; i < trap.size(); i++){
            d.addCard(new Trap(trap.get(i)));
        }
        d.shuffleDeck();
        return d;
    }
    private Deck createOpponentDeck() throws SQLException, ClassNotFoundException {
        Deck d = new Deck();
        ArrayList<String> monster;
        ArrayList<String> spell;
        ArrayList<String> trap;
        if(currentDeck == 1) {
            monster = account.getDeckMonsters(2);
            spell = account.getDeckSpells(2);
            trap = account.getDeckTraps(2);
        }
        else{
            monster = account.getDeckMonsters(1);
            spell = account.getDeckSpells(1);
            trap = account.getDeckTraps(1);
        }
        for(int i = 0; i < monster.size(); i++){
            d.addCard(new Monster(monster.get(i)));
        }
        for(int i = 0; i < spell.size(); i++){
            d.addCard(new Spell(spell.get(i)));
        }
        for(int i = 0; i < trap.size(); i++){
            d.addCard(new Trap(trap.get(i)));
        }
        d.shuffleDeck();
        return d;
    }
    private boolean playerHasMonsters(){
        if(playerMonster1.getChildren().size() == 4 || playerMonster2.getChildren().size() == 4 ||
                playerMonster3.getChildren().size() == 4 || playerMonster4.getChildren().size() == 4 ||
                playerMonster5.getChildren().size() == 4){
            return true;
        }
        else{return false;}
    }
    private boolean opponentHasMonsters(){
        if(opponentMonster1.getChildren().size() == 4 || opponentMonster2.getChildren().size() == 4 ||
                opponentMonster3.getChildren().size() == 4 || opponentMonster4.getChildren().size() == 4 ||
                opponentMonster5.getChildren().size() == 4){
            return true;
        }
        else{
            return false;
        }
    }
    private void updatePlayerLP(int currentLP, int startingLP){
        playerLifePoints.setText(account.getUsername() + "'s Life Points:\n" + currentLP + " / " + startingLP);
    }
    private void updateOpponentLP(int currentLP, int startingLP){
        opponentLifePoints.setText("Opponent's Life Points:\n" + currentLP + " / " + startingLP);
    }
    private void drawPlayerCard(Player player){
        if(player.getDeck().cardsRemaining() == 1){
            playerDeck.getChildren().remove(2);
        }
        if(player.getDeck().cardsRemaining() > 0) {
            player.addToHand();
            try {
                GameCard card = new GameCard();
                ImageView image = new ImageView(new Image(new FileInputStream("images/" +
                        player.selectCardFromHand(player.hand.size()-1).getId() + ".jpg")));
                image.setId(player.selectCardFromHand(player.hand.size()-1).getId());
                image.setFitWidth(118);
                image.setFitHeight(172);
                card.setImageFront(image);
                if(player.selectCardFromHand(player.hand.size()-1) instanceof Monster){
                    card.setMonster((Monster) player.selectCardFromHand(player.hand.size()-1));
                }
                else if(player.selectCardFromHand(player.hand.size()-1) instanceof Spell){
                    card.setSpell((Spell) player.selectCardFromHand(player.hand.size()-1));
                }
                else{
                    card.setTrap((Trap) player.selectCardFromHand(player.hand.size()-1));
                }
                playerHand.getChildren().add(card.getImageFront());
                card.getImageFront().setOnMouseEntered(f->{
                    try {
                        setCardInfo(card.getImageFront().getId());
                    } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                card.getImageFront().setOnMouseClicked(f->{
                    if(playerTurn[0] && (currentPhase[0] == 2 || currentPhase[0] == 4)){
                        if (!card.getMonster().getId().equals("NULL") && player.isCardInHand(card.getMonster()) &&
                                !wasMonsterPlayed[0]){
                            if (playerMonster1.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerMonster1.getChildren().add(card);
                                playerMonster1.getChildren().add(image);
                                player.playCardFromHand(card.getMonster(), 0);
                                wasMonsterPlayed[0] = true;

                            } else if (playerMonster2.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerMonster2.getChildren().add(card);
                                playerMonster2.getChildren().add(image);
                                player.playCardFromHand(card.getMonster(), 1);
                                wasMonsterPlayed[0] = true;
                            } else if (playerMonster3.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerMonster3.getChildren().add(card);
                                playerMonster3.getChildren().add(image);
                                player.playCardFromHand(card.getMonster(), 2);
                                wasMonsterPlayed[0] = true;
                            } else if (playerMonster4.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerMonster4.getChildren().add(card);
                                playerMonster4.getChildren().add(image);
                                player.playCardFromHand(card.getMonster(), 3);
                                wasMonsterPlayed[0] = true;
                            } else if (playerMonster5.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerMonster5.getChildren().add(card);
                                playerMonster5.getChildren().add(image);
                                player.playCardFromHand(card.getMonster(), 4);
                                wasMonsterPlayed[0] = true;
                            }
                        } else if (!card.getTrap().getId().equals("NULL") && player.isCardInHand(card.getTrap())) {
                            if (playerSpell1.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerSpell1.getChildren().add(card);
                                playerSpell1.getChildren().add(image);
                                player.playCardFromHand(card.getTrap(), 0);

                            } else if (playerSpell2.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerSpell2.getChildren().add(card);
                                playerSpell2.getChildren().add(image);
                                player.playCardFromHand(card.getTrap(), 1);
                            } else if (playerSpell3.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerSpell3.getChildren().add(card);
                                playerSpell3.getChildren().add(image);
                                player.playCardFromHand(card.getTrap(), 2);
                            } else if (playerSpell4.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerSpell4.getChildren().add(card);
                                playerSpell4.getChildren().add(image);
                                player.playCardFromHand(card.getTrap(), 3);
                            } else if (playerSpell5.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                playerSpell5.getChildren().add(card);
                                playerSpell5.getChildren().add(image);
                                player.playCardFromHand(card.getTrap(), 4);
                            }
                        } else if (!card.getSpell().getId().equals("NULL") && player.isCardInHand(card.getSpell())) {
                            if (card.getSpell().getType().equals("Field")) {
                                if (playerFieldSpell.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerFieldSpell.getChildren().add(card);
                                    playerFieldSpell.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 0);
                                }
                            } else {
                                if (playerSpell1.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerSpell1.getChildren().add(card);
                                    playerSpell1.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 0);

                                } else if (playerSpell2.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerSpell2.getChildren().add(card);
                                    playerSpell2.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 1);
                                } else if (playerSpell3.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerSpell3.getChildren().add(card);
                                    playerSpell3.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 2);
                                } else if (playerSpell4.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerSpell4.getChildren().add(card);
                                    playerSpell4.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 3);
                                } else if (playerSpell5.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    playerSpell5.getChildren().add(card);
                                    playerSpell5.getChildren().add(image);
                                    player.playCardFromHand(card.getSpell(), 4);
                                }
                            }
                        }
                    }
                });
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void drawOpponentCard(Player opponent){
        if(opponent.getDeck().cardsRemaining() == 1){
            opponentDeck.getChildren().remove(2);
        }
        if(opponent.getDeck().cardsRemaining() > 0) {
            opponent.addToHand();
            try {
                GameCard card = new GameCard();
                ImageView image = new ImageView(new Image(new FileInputStream("images/" +
                        opponent.selectCardFromHand(opponent.hand.size()-1).getId() + ".jpg")));
                image.setId(opponent.selectCardFromHand(opponent.hand.size()-1).getId());
                image.setFitWidth(118);
                image.setFitHeight(172);
                card.setImageFront(image);
                if(opponent.selectCardFromHand(opponent.hand.size()-1) instanceof Monster){
                    card.setMonster((Monster) opponent.selectCardFromHand(opponent.hand.size()-1));
                }
                else if(opponent.selectCardFromHand(opponent.hand.size()-1) instanceof Spell){
                    card.setSpell((Spell) opponent.selectCardFromHand(opponent.hand.size()-1));
                }
                else{
                    card.setTrap((Trap) opponent.selectCardFromHand(opponent.hand.size()-1));
                }
                opponentHand.getChildren().add(card.getImageFront());
                card.getImageFront().setOnMouseEntered(f->{
                    try {
                        setCardInfo(card.getImageFront().getId());
                    } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                card.getImageFront().setOnMouseClicked(f->{
                    if(!playerTurn[0] && (currentPhase[0] == 2 || currentPhase[0] == 4)) {
                        if (!card.getMonster().getId().equals("NULL") && opponent.isCardInHand(card.getMonster()) &&
                                !wasMonsterPlayed[0]) {
                            if (opponentMonster1.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentMonster1.getChildren().add(card);
                                opponentMonster1.getChildren().add(image);
                                opponent.playCardFromHand(card.getMonster(), 0);
                                wasMonsterPlayed[0] = true;
                            } else if (opponentMonster2.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentMonster2.getChildren().add(card);
                                opponentMonster2.getChildren().add(image);
                                opponent.playCardFromHand(card.getMonster(), 1);
                                wasMonsterPlayed[0] = true;
                            } else if (opponentMonster3.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentMonster3.getChildren().add(card);
                                opponentMonster3.getChildren().add(image);
                                opponent.playCardFromHand(card.getMonster(), 2);
                                wasMonsterPlayed[0] = true;
                            } else if (opponentMonster4.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentMonster4.getChildren().add(card);
                                opponentMonster4.getChildren().add(image);
                                opponent.playCardFromHand(card.getMonster(), 3);
                                wasMonsterPlayed[0] = true;
                            } else if (opponentMonster5.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentMonster5.getChildren().add(card);
                                opponentMonster5.getChildren().add(image);
                                opponent.playCardFromHand(card.getMonster(), 4);
                                wasMonsterPlayed[0] = true;
                            }
                        } else if (!card.getTrap().getId().equals("NULL") && opponent.isCardInHand(card.getTrap())) {
                            if (opponentSpell1.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentSpell1.getChildren().add(card);
                                opponentSpell1.getChildren().add(image);
                                opponent.playCardFromHand(card.getTrap(), 0);

                            } else if (opponentSpell2.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentSpell2.getChildren().add(card);
                                opponentSpell2.getChildren().add(image);
                                opponent.playCardFromHand(card.getTrap(), 1);
                            } else if (opponentSpell3.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentSpell3.getChildren().add(card);
                                opponentSpell3.getChildren().add(image);
                                opponent.playCardFromHand(card.getTrap(), 2);
                            } else if (opponentSpell4.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentSpell4.getChildren().add(card);
                                opponentSpell4.getChildren().add(image);
                                opponent.playCardFromHand(card.getTrap(), 3);
                            } else if (opponentSpell5.getChildren().size() <= 2) {
                                image.setFitWidth(59);
                                image.setFitHeight(86);
                                opponentSpell5.getChildren().add(card);
                                opponentSpell5.getChildren().add(image);
                                opponent.playCardFromHand(card.getTrap(), 4);
                            }
                        } else if (!card.getSpell().getId().equals("NULL") && opponent.isCardInHand(card.getSpell())) {
                            if (card.getSpell().getType().equals("Field")) {
                                if (opponentFieldSpell.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentFieldSpell.getChildren().add(card);
                                    opponentFieldSpell.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 0);
                                }
                            } else {
                                if (opponentSpell1.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentSpell1.getChildren().add(card);
                                    opponentSpell1.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 0);

                                } else if (opponentSpell2.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentSpell2.getChildren().add(card);
                                    opponentSpell2.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 1);
                                } else if (opponentSpell3.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentSpell3.getChildren().add(card);
                                    opponentSpell3.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 2);
                                } else if (opponentSpell4.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentSpell4.getChildren().add(card);
                                    opponentSpell4.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 3);
                                } else if (opponentSpell5.getChildren().size() <= 2) {
                                    image.setFitWidth(59);
                                    image.setFitHeight(86);
                                    opponentSpell5.getChildren().add(card);
                                    opponentSpell5.getChildren().add(image);
                                    opponent.playCardFromHand(card.getSpell(), 4);
                                }
                            }
                        }
                    }
                });
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private void attackPlayerMonster(StackPane playerMonster, Player player, Player opponent){
        if(playerMonster.getChildren().size() >= 3) {
            if (playerTurn[0] && currentPhase[0] == 3 && !(((GameCard) playerMonster.getChildren().get(2)).getMonster().getHasAttacked())) {
                if (player.getLifePoints() != 0 || opponent.getLifePoints() != 0) {
                    if (playerMonster.getChildren().size() == 4) {
                        int attackPower = ((GameCard) playerMonster.getChildren().get(2)).getMonster().getAttackPoints();
                        if (!opponentHasMonsters()) {
                            opponent.subtractLifePoints(attackPower);
                            updateOpponentLP(opponent.getLifePoints(), startingLP);
                            ((GameCard) playerMonster.getChildren().get(2)).getMonster().setHasAttacked(true);
                        } else {
                            Rectangle r = new Rectangle();
                            r.setWidth(59);
                            r.setHeight(86);
                            r.setFill(Color.DARKBLUE);
                            r.setOpacity(0.5);
                            playerMonster.getChildren().add(r);
                        }
                    }
                }
            } else if (!playerTurn[0] && currentPhase[0] == 3) {
                int playerAtk = ((GameCard) playerMonster.getChildren().get(2)).getMonster().getAttackPoints();
                ;
                int opponentAtk = 0;
                if (opponentMonster1.getChildren().size() == 5) {
                    opponentMonster1.getChildren().remove(4);
                    opponentAtk = ((GameCard) opponentMonster1.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster1.getChildren().get(3), opponentMonster1.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster.getChildren().get(3), playerMonster.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                        ((GameCard) opponentMonster1.getChildren().get(2)).getMonster().setHasAttacked(true);
                    }
                } else if (opponentMonster2.getChildren().size() == 5) {
                    opponentMonster2.getChildren().remove(4);
                    opponentAtk = ((GameCard) opponentMonster2.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster2.getChildren().get(3), opponentMonster2.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster.getChildren().get(3), playerMonster.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                        ((GameCard) opponentMonster2.getChildren().get(2)).getMonster().setHasAttacked(true);
                    }
                } else if (opponentMonster3.getChildren().size() == 5) {
                    opponentMonster3.getChildren().remove(4);
                    opponentAtk = ((GameCard) opponentMonster3.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster3.getChildren().get(3), opponentMonster3.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster.getChildren().get(3), playerMonster.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                        ((GameCard) opponentMonster3.getChildren().get(2)).getMonster().setHasAttacked(true);
                    }
                } else if (opponentMonster4.getChildren().size() == 5) {
                    opponentMonster4.getChildren().remove(4);
                    opponentAtk = ((GameCard) opponentMonster4.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster4.getChildren().get(3), opponentMonster4.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster.getChildren().get(3), playerMonster.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                        ((GameCard) opponentMonster4.getChildren().get(2)).getMonster().setHasAttacked(true);
                    }
                } else if (opponentMonster5.getChildren().size() == 5) {
                    opponentMonster5.getChildren().remove(4);
                    opponentAtk = ((GameCard) opponentMonster5.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster5.getChildren().get(3), opponentMonster5.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster.getChildren().get(3), playerMonster.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                        ((GameCard) opponentMonster5.getChildren().get(2)).getMonster().setHasAttacked(true);
                    }
                }
            }
        }
    }
    private void attackOpponentMonster(StackPane opponentMonster, Player player, Player opponent) {
        if (opponentMonster.getChildren().size() >= 3) {
            if (!playerTurn[0] && currentPhase[0] == 3 && !(((GameCard) opponentMonster.getChildren().get(2)).getMonster().getHasAttacked())) {
                if (player.getLifePoints() != 0 || opponent.getLifePoints() != 0) {
                    if (opponentMonster.getChildren().size() == 4) {
                        int attackPower = ((GameCard) opponentMonster.getChildren().get(2)).getMonster().getAttackPoints();
                        if (!playerHasMonsters()) {
                            player.subtractLifePoints(attackPower);
                            updatePlayerLP(player.getLifePoints(), startingLP);
                            ((GameCard) opponentMonster.getChildren().get(2)).getMonster().setHasAttacked(true);
                        } else {
                            Rectangle r = new Rectangle();
                            r.setWidth(59);
                            r.setHeight(86);
                            r.setFill(Color.DARKBLUE);
                            r.setOpacity(0.5);
                            opponentMonster.getChildren().add(r);
                        }
                    }
                }
            } else if (playerTurn[0] && currentPhase[0] == 3) {
                int playerAtk = 0;
                int opponentAtk = ((GameCard) opponentMonster.getChildren().get(2)).getMonster().getAttackPoints();
                if (playerMonster1.getChildren().size() == 5) {
                    playerMonster1.getChildren().remove(4);
                    playerAtk = ((GameCard) playerMonster1.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster.getChildren().get(3), opponentMonster.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                        ((GameCard) playerMonster1.getChildren().get(2)).getMonster().setHasAttacked(true);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster1.getChildren().get(3), playerMonster1.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                    }
                } else if (playerMonster2.getChildren().size() == 5) {
                    playerMonster2.getChildren().remove(4);
                    playerAtk = ((GameCard) playerMonster2.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster.getChildren().get(3), opponentMonster.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                        ((GameCard) playerMonster2.getChildren().get(2)).getMonster().setHasAttacked(true);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster2.getChildren().get(3), playerMonster2.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                    }
                } else if (playerMonster3.getChildren().size() == 5) {
                    playerMonster3.getChildren().remove(4);
                    playerAtk = ((GameCard) playerMonster3.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster.getChildren().get(3), opponentMonster.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                        ((GameCard) playerMonster3.getChildren().get(2)).getMonster().setHasAttacked(true);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster3.getChildren().get(3), playerMonster3.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                    }
                } else if (playerMonster4.getChildren().size() == 5) {
                    playerMonster4.getChildren().remove(4);
                    playerAtk = ((GameCard) playerMonster4.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster.getChildren().get(3), opponentMonster.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                        ((GameCard) playerMonster4.getChildren().get(2)).getMonster().setHasAttacked(true);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster4.getChildren().get(3), playerMonster4.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                    }
                } else if (playerMonster5.getChildren().size() == 5) {
                    playerMonster5.getChildren().remove(4);
                    playerAtk = ((GameCard) playerMonster5.getChildren().get(2)).getMonster().getAttackPoints();
                    if (playerAtk > opponentAtk) {
                        opponentGraveyard.getChildren().addAll(opponentMonster.getChildren().get(3), opponentMonster.getChildren().get(2));
                        playerAtk = playerAtk - opponentAtk;
                        opponent.subtractLifePoints(playerAtk);
                        updateOpponentLP(opponent.getLifePoints(), startingLP);
                        ((GameCard) playerMonster5.getChildren().get(2)).getMonster().setHasAttacked(true);
                    } else if (opponentAtk > playerAtk) {
                        playerGraveyard.getChildren().addAll(playerMonster5.getChildren().get(3), playerMonster5.getChildren().get(2));
                        opponentAtk = opponentAtk - playerAtk;
                        player.subtractLifePoints(opponentAtk);
                        updatePlayerLP(player.getLifePoints(), startingLP);
                    }
                }
            }
        }
    }
}
