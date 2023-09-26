package com.example;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameCard extends ImageView{
    private Monster monster;
    private Spell spell;
    private Trap trap;
    private ImageView imageFront;
    private ImageView imageBack = new ImageView(new Image(new FileInputStream("images/back.jpg")));
    private ImageView shownSide =  imageFront;
    public GameCard() throws FileNotFoundException {
        monster = new Monster();
        spell = new Spell();
        trap = new Trap();
        imageBack.setFitWidth(59);
        imageBack.setFitHeight(86);
    }
    public GameCard(ImageView i, Monster m) throws FileNotFoundException {
        imageFront = i;
        monster = m;
        spell = new Spell();
        trap = new Trap();
    }
    public GameCard(ImageView i, Spell s) throws FileNotFoundException {
        imageFront = i;
        monster = new Monster();
        spell = s;
        trap = new Trap();
    }
    public GameCard(ImageView i, Trap t) throws FileNotFoundException {
        imageFront = i;
        monster = new Monster();
        spell = new Spell();
        trap = t;
    }
    public void setImageFront(ImageView i){
        imageFront = i;
    }
    public void setImageWidth(double x){
        imageFront.setFitWidth(x);
    }
    public void setImageHeight(double x){
        imageFront.setFitHeight(x);
    }
    public void setMonster(Monster m){
        monster = m;
    }
    public void setSpell(Spell s){
        spell = s;
    }
    public void setTrap(Trap t){
        trap = t;
    }
    public void showBack(){
        shownSide = imageBack;
    }
    public void showFront(){
        shownSide = imageFront;
    }

    public ImageView getImageFront() {
        return imageFront;
    }
    public ImageView getImageBack(){return imageBack;}
    public ImageView getShownSide(){return shownSide;}

    public Monster getMonster() {
        return monster;
    }

    public Spell getSpell() {
        return spell;
    }

    public Trap getTrap() {
        return trap;
    }
}
