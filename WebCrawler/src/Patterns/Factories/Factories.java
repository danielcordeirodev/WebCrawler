/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.Factories;

import Controller.HomeController;
import Main.Main;
import Model.WebCrawler;
import Patterns.DAO.IWebCrawlerDAO;
import Patterns.DAO.WebCrawlerFile;
import Patterns.DAO.WebCrawlerJson;
import Patterns.Memento.CareTaker;
import Views.HomeView;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The class create a factory of any object type
 *
 * @author BRKsCosta and danielcordeiro
 */
public class Factories {

    private static HomeView view;
    private static HomeController controller;
    private static Stage stage;

    public static void showApp() {

        stage = new Stage(StageStyle.DECORATED);
        stage.sizeToScene();
        stage.setTitle("Web Crawler");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/Resources/images/icon.png")));
        stage.setScene(createMVCApp());
        stage.show();
        getView().getGraphView().init();
    }

    public static Stage getAppStage() {
        return stage;
    }

    private static Scene createMVCApp() {

        WebCrawler model = new WebCrawler();
        view = new HomeView(model);
        CareTaker careTaker = new CareTaker(model);
        controller = new HomeController(model, view, careTaker);

        return view.getScene();
    }

    public static IWebCrawlerDAO createFileType(String fileType, WebCrawler model) {

        switch (fileType) {
            case "DATA":
                return new WebCrawlerFile(model);
            case "JSON":
                return new WebCrawlerJson(model);
            default:
                getView().showError("Tipo de ficheiro não existente!");
        }
        return null;
    }

    public static HomeView getView() {
        return view;
    }

    public static HomeController getController() {
        return controller;
    }

}
