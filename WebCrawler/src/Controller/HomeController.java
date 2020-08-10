/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Patterns.Memento.CareTaker;
import Model.WebCrawlerException;
import Model.WebCrawler;
import Model.WebPage;
import Patterns.DAO.IWebCrawlerDAO;
import static Patterns.Factories.Factories.createFileType;
import Views.*;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This class is responsible to manager all the business logic
 *
 * @author BRKsCosta and danielcordeiro
 */
public class HomeController {

    private final HomeView view;
    private final WebCrawler model;
    private final CareTaker caretaker;

    @SuppressWarnings("LeakingThisInConstructor")
    public HomeController(WebCrawler model, HomeView view, CareTaker caretaker) {
        this.view = view;
        this.model = model;
        this.caretaker = caretaker;

        this.view.setTriggersButtons(this);
        this.model.addObserver(this.view); // Subscribe the model
    }

    /**
     * Return the class view instance
     *
     * @return HomeView object
     */
    public HomeView getView() {
        return view;
    }

    /**
     * Return the model instance
     *
     * @return WebCrawler object
     */
    public WebCrawler getModel() {
        return model;
    }

    /**
     * Return the CareTakes class to manage undo.
     *
     * @return CareTaker Object
     */
    public CareTaker getCaretaker() {
        return caretaker;
    }

    /**
     * Start the search of the WebPage
     *
     * @param criteria The type of stop criteria
     * @param numCriteria Number of max pages to generate
     * @throws WebCrawlerException
     * @throws IOException
     */
    public void startSearch(HomeView.StopCriteria criteria, int numCriteria)
            throws WebCrawlerException, IOException {

        // First clear all the graph
        clearGraph();

        // Init the WebCrawler
        this.model.buildWebCrawler(criteria, numCriteria, view.getInputURL());

        this.caretaker.requestSave();
    }

    /**
     * Exit the application
     */
    public void exitApp() {
        this.view.exitApp();
    }

    /**
     * Get the root webpage
     *
     * @return Vertex element
     */
    public Vertex<WebPage> getRootPage() {
        return model.getRootWebPage();
    }

    /**
     * Clear errors
     */
    public void clearErrors() {
        this.view.clearError();
    }

    /**
     * Import files
     */
    public void importFiles(String fileType) {

        IWebCrawlerDAO dao = createFileType(fileType, model);
        dao.loadFile();
    }

    /**
     * Exports the chosen file type.
     *
     * @param fileType Type of the file
     */
    public void exportFile(String fileType) {

        // Choose the file to create
        IWebCrawlerDAO dao = createFileType(fileType, model);
        dao.saveAll();

    }

    /**
     * Undo in interactive mode
     */
    public void undoAction() {
        // Checks if we have saved webcrawler's
        if (!caretaker.canUndo()) {
            view.showError("Sem undos disponíveis.");
        }
        caretaker.requestRestore();
    }

    /**
     * Open the a web page on browser
     *
     * @param url
     */
    public void openWebPage(String url) {

        try {

            Desktop.getDesktop().browse(new URL(url).toURI());

        } catch (MalformedURLException ex) {
            model.getLogger().writeToLog(ex.getMessage());
            view.showErrorStackTraceException(ex.getMessage());
        } catch (URISyntaxException | IOException ex) {
            model.getLogger().writeToLog(ex.getMessage());
            view.showErrorStackTraceException(ex.getMessage());
        }
    }

    /**
     * Clear the graph
     */
    public void clearGraph() {
        model.clearGraph();
    }

    /**
     * Inserts a new page from parent
     *
     * @param subRoot The parent
     * @throws IOException
     */
    public void insertNewSubRoot(SmartGraphVertex<WebPage> subRoot) throws IOException {
        model.insertNewSubWebPageCrawler(subRoot.getUnderlyingVertex());
    }

    @Override
    public String toString() {
        return "Home Controller: \n CareTaker: " + this.caretaker;
    }

}
