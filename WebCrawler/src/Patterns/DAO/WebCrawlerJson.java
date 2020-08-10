/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.DAO;

import Model.Link;
import Model.WebCrawler;
import Model.WebPage;
import static Patterns.Factories.Factories.*;
import Patterns.Singleton.LoggerWriter;
import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BRKsCosta
 */
public class WebCrawlerJson implements IWebCrawlerDAO {

    public static final String FILENAME = "Webcrawler.json";
    private LoggerWriter logger = LoggerWriter.getInstance();
    private List<Edge<Link, WebPage>> inMemory;
    private Map<String, List<String>> edgesMap;

    public WebCrawlerJson(WebCrawler model) {
        this.inMemory = model.getAllLinks();
        this.edgesMap = new HashMap<>();
    }

    @Override
    public void saveAll() {
        for (int i = 0; i < inMemory.size(); i++) {
            Edge<Link, WebPage> edge = inMemory.get(i);
            String key = edge.element().getLinkName();
            List<String> value = new ArrayList<>();

            for (Vertex<WebPage> vertice : edge.vertices()) {
                value.add(vertice.element().toString());
                edgesMap.put(key, value);
            }

        }
        this.saveFile();
    }
    
    /**
     * Save to a file
     */
    private void saveFile() {

        try {
            FileOutputStream fileOut
                    = new FileOutputStream(FILENAME);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(edgesMap);

            out.close();

            fileOut.close();
        } catch (IOException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
            Logger.getLogger(WebCrawlerJson.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void loadFile() {
        try {

            File f = new File(FILENAME);
            if (!f.exists()) {
                edgesMap = new HashMap();
                return;
            }

            FileInputStream fileIn
                    = new FileInputStream(FILENAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            edgesMap = (Map<String, List<String>>) in.readObject();
            in.close();

            fileIn.close();

        } catch (IOException | ClassNotFoundException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
        }
    }

    @Override
    public List<Edge<Link, WebPage>> readAll() {
        return inMemory;
    }

}
