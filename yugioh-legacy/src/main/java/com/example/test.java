package com.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        StackPane root = new StackPane();
        root.getChildren().add(new Label("This is window 1"));
        Scene mainScene = new Scene(root, 800, 600);

        Stage secondStage = new Stage();
        StackPane secondRoot = new StackPane();
        secondRoot.getChildren().add(new Label("This is window 2"));
        Scene secondScene = new Scene(secondRoot, 400, 300);

        secondStage.setScene(secondScene);
        secondStage.show();

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
