/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import Controller.HomeController;
import Model.Link;
import Model.WebCrawler;
import Model.WebCrawlerException;
import Model.WebPage;
import static Patterns.Factories.Factories.getAppStage;
import Patterns.Singleton.LoggerException;
import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graphview.*;
import java.io.IOException;
import java.util.Observable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import Patterns.Singleton.LoggerWriter;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.StageStyle;

/**
 * This class contains all implementation about the UI. Implement a behavioral 
 * of the view and a observer to keep updated when model is changed.
 * 
 * @see Model.WebCrawler
 * @see Views.IHomeOperations
 * 
 * @author BRKsCosta and danielcordeiro
 */
public class HomeView extends VBox implements Observer, IHomeOperations {

    // Enum for searchCriteria
    public enum StopCriteria {
        PAGES, DEPTH, ITERATIVE;
    }

    LoggerWriter logW = LoggerWriter.getInstance();
    //SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
    //SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();
    private SmartPlacementStrategy strategy;
    private SmartGraphPanel<WebPage, Link> graphView;

    //Menu  
    private MenuBar menuBar;
    private Menu menuFile;
    private Menu menuEdit;
    private Menu menuHelp;
    private MenuItem mFileItemExportFile;
    private MenuItem mFileItemImportFile;
    private MenuItem mFileItemExit;
    private MenuItem mEditUndo;
    private MenuItem mEditClearGraph;
    private MenuItem mEditRedo;
    private MenuItem mHelpAbout;
    private SeparatorMenuItem separatorMenu;
    private SeparatorMenuItem separatorEdit;

    //Actions left panel
    private Button btnStartCrawler;
    private TextField txtFieldURL;
    private TextField txtFieldNumPages;
    private RadioButton rdBtnBreadthFirst;
    private RadioButton rdBtnDepth;
    private RadioButton rdBtnIterative;
    private final Spinner spinner = new Spinner();

    //Layout
    private SplitPane splitPane;
    private AnchorPane anchorPaneLeft;
    private AnchorPane anchorPaneRigth;
    private HBox bottomHBox;

    //Labels
    private Label lblStatistics;
    private Label lblInfo;
    private Label lblCriteria;
    private Label lblAnotherThing;
    private Label lblWebCrawler;
    private Label lblNumPages;

    private static final String INITAL_VALUE = "10";
    private Scene scene;

    // Graph interface
    SmartGraphEdge<Link, WebPage> edgeClicked = null;
    SmartGraphVertex<WebPage> vertexClicked;
    private boolean inIterativeMode = false;

    public HomeView(WebCrawler model) {
        this.strategy = new SmartCircularSortedPlacementStrategy();
        this.graphView = new SmartGraphPanel<>(model.getGraph(), strategy);
        BorderPane window = new BorderPane(HomeView.this);
        window.setCenter(HomeView.this);
        this.scene = new Scene(window, 1500, 700);

        this.initializeComponents();

        update(model, null);

    }
    
    /**
     * Get SmartGraphPanel
     * @return A object SmartGraphPanel
     */
    public SmartGraphPanel<WebPage, Link> getGraphView() {
        return graphView;
    }
    
    /**
     * Set the SmartGraphPanel
     * @param graphView Object SmartGraphPanel
     */
    public void setGraphView(SmartGraphPanel<WebPage, Link> graphView) {
        this.graphView = graphView;
    }

    /**
     * Initialize all components present on UI.
     */
    private void initializeComponents() {
        //Set up menu bar
        this.menuFile = new Menu("File");
        this.mFileItemImportFile = new MenuItem("Import File");
        this.mFileItemExportFile = new MenuItem("Export File");
        this.mFileItemExit = new MenuItem("Exit");
        this.separatorMenu = new SeparatorMenuItem();

        this.menuFile.getItems().addAll(mFileItemExportFile, mFileItemImportFile,
                separatorMenu, mFileItemExit);

        this.menuEdit = new Menu("Edit");
        this.mEditUndo = new MenuItem("Undo");
        this.mEditRedo = new MenuItem("Redo");
        this.mEditClearGraph = new MenuItem("Clear Graph");
        this.separatorEdit = new SeparatorMenuItem();
        this.menuEdit.getItems().addAll(mEditUndo, mEditRedo, separatorEdit, mEditClearGraph);

        this.mHelpAbout = new MenuItem("About");
        this.menuHelp = new Menu("Help");
        this.menuHelp.getItems().add(mHelpAbout);

        this.menuBar = new MenuBar();
        this.menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);

        // Criteria type on left pane
        final ToggleGroup group = new ToggleGroup();
        this.lblCriteria = new Label("Modos Pesquisa");
        this.lblCriteria.setFont(new Font("Verdana", 16));
        this.lblCriteria.setPadding(new Insets(0, 0, 15, 0));
        lblCriteria.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(lblCriteria, 0.0);
        AnchorPane.setRightAnchor(lblCriteria, 0.0);
        lblCriteria.setAlignment(Pos.CENTER);

        this.txtFieldURL = new TextField();
        this.txtFieldURL.setText("http://www.brunomnsilva.com/sandbox/index.html");
        this.txtFieldURL.setId("textFieldSearchURL");
        this.txtFieldURL.setPrefSize(6, 15);

        this.btnStartCrawler = new Button("GO!");
        this.rdBtnBreadthFirst = new RadioButton("Largura");
        this.rdBtnBreadthFirst.setToggleGroup(group);
        this.rdBtnBreadthFirst.setSelected(true);

        this.lblNumPages = new Label("Número de páginas");
        this.txtFieldNumPages = new TextField();
        this.txtFieldNumPages.setId("textFieldNumPages");
        this.txtFieldNumPages.setMinSize(10, 10);
        this.rdBtnDepth = new RadioButton("Profundidade");
        this.rdBtnDepth.setToggleGroup(group);
        this.rdBtnDepth.setSelected(false);
        this.rdBtnIterative = new RadioButton("Iterativo");
        this.rdBtnIterative.setToggleGroup(group);
        this.rdBtnIterative.setSelected(false);

        //Left Layout
        this.anchorPaneLeft = new AnchorPane();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000,
                Integer.parseInt(INITAL_VALUE)));
        spinner.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(1));
        int row = 0;
        grid.add(new Label("Nº Pages: "), 0, row);
        grid.add(spinner, 1, row);

        VBox items = new VBox(lblCriteria, txtFieldURL, btnStartCrawler,
                rdBtnBreadthFirst, grid, rdBtnDepth, rdBtnIterative);
        items.setPadding(new Insets(5, 0, 0, 10));
        AnchorPane.setTopAnchor(items, 15.0);
        AnchorPane.setLeftAnchor(items, 10.0);
        AnchorPane.setRightAnchor(items, 10.0);
        this.anchorPaneLeft.getChildren().add(items);
        //END LEFT LAYOUT

        // Statistics on right pane
        this.lblStatistics = new Label("Estatísticas");
        this.anchorPaneRigth = new AnchorPane();
        VBox vboxChart = barChart();
        AnchorPane.setTopAnchor(vboxChart, 10.0);
        AnchorPane.setLeftAnchor(vboxChart, 10.0);
        AnchorPane.setRightAnchor(vboxChart, 80.0);
        AnchorPane.setBottomAnchor(vboxChart, 80.0);
        anchorPaneRigth.getChildren().addAll(vboxChart);

        //Center pane graph will shows here
        VBox boxScroll = new VBox();
        this.lblWebCrawler = new Label("Welcome to your WebCrawler Graph");
        this.lblWebCrawler.setFont(new Font("Verdana", 16));
        this.lblWebCrawler.setPadding(new Insets(0, 0, 15, 0));

        //Graph interface
        SmartGraphDemoContainer graphContainter = new SmartGraphDemoContainer(graphView);
        graphView.setAutomaticLayout(true);

        this.splitPane = new SplitPane();
        this.splitPane.setDividerPositions(0.5f, 1.3f, 0.4f);
        this.splitPane.getItems().addAll(anchorPaneLeft, graphContainter, anchorPaneRigth);

        //Config HBox Bootom
        Pane panelBottom = new Pane();
        panelBottom.setPadding(new Insets(0, 410, 0, 410));
        this.lblInfo = new Label("Bem-Vindo!");
        this.lblInfo.setTextFill(Paint.valueOf("#ff6961"));
        this.lblAnotherThing = new Label("Outra Coisa");
        this.bottomHBox = new HBox();
        HBox.setHgrow(panelBottom, Priority.ALWAYS);
        this.bottomHBox.getChildren().addAll(lblInfo, panelBottom /*, lblAnotherThing*/);
        bottomHBox.setStyle("-fx-background-color: #867B71;");

        /*Creates layout*/
        HomeView.setVgrow(splitPane, Priority.ALWAYS);
        this.getChildren().addAll(menuBar, splitPane, bottomHBox);
        getStylesheets().add(this.getClass().getResource("/Resources/css/styles.css").toExternalForm());
    }

    /**
     * This method build the bar chart
     *
     * @return Return the statistics on chart
     */
    private VBox barChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Devices");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Visits");
        BarChart barChart = new BarChart(xAxis, yAxis);
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("2014");
        dataSeries1.getData().add(new XYChart.Data("Desktop", 567));
        dataSeries1.getData().add(new XYChart.Data("Phone", 65));
        dataSeries1.getData().add(new XYChart.Data("Tablet", 23));
        barChart.getData().add(dataSeries1);
        VBox vbox = new VBox(barChart);
        return vbox;
    }

    // Observer method implemented
    @Override
    public void update(Observable o, Object o1) {

        WebCrawler obsModel = (WebCrawler) o; // Model

        if (o instanceof WebCrawler) {
            // Não será preciso fazer update se não houver páginas
            if (obsModel.countWebPages() > 0) {
                graphView.update();
            }

            if (obsModel.isFinished == true) {
                graphView.update();
                obsModel.isFinished = false;
            }
        }
    }
    
    // IHomeOperations methods
    @Override
    public String getInputURL() {
        return this.txtFieldURL.getText();
    }
    
    @Override
    public void importFile(HomeController controller) {
       FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Ficheiro");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Data File", "*.data"),
                new ExtensionFilter("Json File", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(getAppStage());
        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")
                    + 1, selectedFile.getName().length());
            controller.importFiles(fileExtension.toUpperCase());

        }
    }

    @Override
    public void exportFile(HomeController controller) {
        chooseFileType(controller);
    }

    @Override
    public void exitApp() {
        Platform.exit();
        System.out.println("Views.Home.exitApp()");
        System.exit(0);
    }

    @Override
    public void clearError() {
        this.lblInfo.setText("");
    }

    @Override
    public void showError(String errorMsg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Alerta");
        alert.setHeaderText("Oooops, vamos com calma..");
        alert.setContentText(errorMsg);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.showAndWait();
    }

    @Override
    public void setTriggersButtons(HomeController controller) {

        // Quit app
        this.mFileItemExit.setOnAction((ActionEvent event) -> {
            controller.exitApp();
        });

        // Apply DAO - TODO
        this.mFileItemExportFile.setOnAction((ActionEvent event) -> {
            exportFile(controller);
        });

        this.mFileItemImportFile.setOnAction((ActionEvent event) -> {
            importFile(controller);
        });

        // Apply Memento - Undo
        this.mEditUndo.setOnAction((ActionEvent event) -> {
            controller.undoAction();
        });

        this.mEditRedo.setOnAction((ActionEvent event) -> {
            redoGraph();
        });

        this.mEditClearGraph.setOnAction(((event) -> {
            controller.clearGraph();
            System.out.println("Clear graph");
        }));

        // Show the graph
        this.btnStartCrawler.setOnAction((ActionEvent t) -> {
            selectSearchType(controller);
        });
        
        // VISIT WEB PAGE
        this.graphView.setVertexDoubleClickAction(graphVertex -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());

            if(this.inIterativeMode){
                try {
                    controller.getModel().insertNewSubWebPageCrawler(graphVertex.getUnderlyingVertex());
                    controller.getCaretaker().requestSave();
                } catch (IOException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                controller.openWebPage(graphVertex.getUnderlyingVertex().element().getPersonalURL());
            }
            
            graphVertex.setStyle("-fx-fill: gold; -fx-stroke: brown;");
            graphView.update();
        });
        
        // Double click in one edge. 
        // It will have to toogle between edges. It can't be possible to have more than one clicked edge at the same time. 
        // Lets see, we can click in one, show description. If we click in another one the older one has to come back to the older color and we get the description and color of the new clicked one.
        // It can be possible to double click one edge, change the color and give the description and if we double click again it changes the color again to the older one and doens0t print it's description.
        this.graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("EDGE -1");
            //dynamically change the style when clicked
            if (this.edgeClicked != null){
                System.out.println("EDGE 0");
                // Check if the clicked edge is the same as the edge passed as an argument
                if (graphEdge.getUnderlyingEdge() == this.edgeClicked) {
                    // Change the color to the default
                    graphEdge.setStyle("-fx-stroke: #FF6D66; -fx-stroke-width: 2;");
                    this.edgeClicked = null;
                    
                    System.out.println("EDGE 1");
                } else {
                    // Change the color of the old edgeClicked to the default
                    this.edgeClicked.setStyle("-fx-stroke: #FF6D66; -fx-stroke-width: 2;");

                    // Assign to the edgeClicked a new value
                    this.edgeClicked = graphEdge;
                    this.edgeClicked.setStyle("-fx-stroke: black; -fx-stroke-width: 2;");
                    System.out.println("EDGE 2");
                }
            } else {
                // Assign to the edgeClicked a new value
                this.edgeClicked = graphEdge;
                graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 2;");
                System.out.println("EDGE 3");
            }

            // Refresh color's
            this.graphView.update();
        });

        // Try to catch the ENTER key event
        EventHandler<KeyEvent> enterKeyEventHandler;

        enterKeyEventHandler = (KeyEvent event) -> {
            // handle users "enter key event"
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    // yes, using exception for control is a bad solution ;-)
                    selectSearchType(controller);
                } catch (NumberFormatException e) {
                    // show message to user: "only numbers allowed"
                    // reset editor to INITAL_VALUE
                    spinner.getEditor().textProperty().set(INITAL_VALUE);

                }
            }
        };

        // note: use KeyEvent.KEY_PRESSED, because KeyEvent.KEY_TYPED is to late, spinners
        // SpinnerValueFactory reached new value before key released an SpinnerValueFactory will
        // throw an exception
        spinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);

    }
    
    /**
     * This method make redo action on graph
     * 
     */
    private void redoGraph() {
        // TODO
        System.out.println("Views.Home.redoGraph()");
    }
    
    /**
     * This method execute the search requested
     * @param controller The controller object
     * @throws LoggerException
     * @throws WebCrawlerException
     * @throws NumberFormatException 
     */
    private void selectSearchType(HomeController controller) throws
            LoggerException, WebCrawlerException, NumberFormatException {
        try {
            // Get the numPages
            int parseInt = Integer.parseInt(spinner.getEditor().textProperty().get());

            if (rdBtnBreadthFirst.isSelected()) {
                this.inIterativeMode = false;
                lblAnotherThing.setText("Selecionou BFS");
                controller.startSearch(HomeView.StopCriteria.PAGES, parseInt);
            } else if (rdBtnDepth.isSelected()) {
                this.inIterativeMode = false;
                lblAnotherThing.setText("Selecionou DFS");
                controller.startSearch(HomeView.StopCriteria.DEPTH, parseInt);
            } else {
                lblAnotherThing.setText("Selecionou Iterativo");
                this.inIterativeMode = true;
                controller.startSearch(HomeView.StopCriteria.ITERATIVE, parseInt);
            }
        } catch (IOException ex) {
            LoggerWriter.getInstance().writeToLog("Classe View btnStarcrawler: " + ex.getStackTrace()[0]);
        }
    }
    
    /**
     * Set the root webpage
     * @param p The concrete vertex
     */
    public void setColorRootPage(Vertex<WebPage> p) {
        System.out.println("root? " + p);
        System.out.println("graphview ? " + graphView.getStylableVertex(p));
        graphView.getStylableVertex(p).setStyle("-fx-fill: gold; -fx-stroke: brown;");
    }
    
    /**
     * Update the graph view
     */
    public void updateGraph() {
        graphView.update();
    }
    
    /**
     * Show errors along the application
     * @param msg The error message
     */
    public void showErrorStackTraceException(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Hmmm, algo não está bom");
        alert.initStyle(StageStyle.UTILITY);
        Exception ex = new WebCrawlerException(msg);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
        exitApp();
    }
    
    /**
     * Open the dialog to open the file
     * @param controller The controller object
     */
    private void chooseFileType(HomeController controller) {
        List<String> choices = new ArrayList<>();
        choices.add("DATA");
        choices.add("JSON");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("DATA", choices);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Exportar");
        dialog.setHeaderText("Por favor, selecione um formato.");
        dialog.setContentText("Formato:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            controller.exportFile(result.get());
        }
    }

    @Override
    public String toString() {
        return "View: " + HomeView.class;
    }
}