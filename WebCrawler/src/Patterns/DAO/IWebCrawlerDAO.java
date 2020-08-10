/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.DAO;

import Model.Link;
import Model.WebPage;
import com.brunomnsilva.smartgraph.graph.Edge;
import java.util.List;

/**
 *
 * @author BRKsCosta
 */
public interface IWebCrawlerDAO {

    /**
     * Saves the book in the current persistence storage.
     *
     */
    public void saveAll();

    /**
     * Retrieves all books from current storage persistence.
     *
     * @return All pages present inserted on graph
     */
    public List<Edge<Link, WebPage>> readAll();
    
    /**
     * Read the file
     */
    public void loadFile();
}
