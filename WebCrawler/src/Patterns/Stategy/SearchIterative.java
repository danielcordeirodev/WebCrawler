/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.Stategy;

import Model.Link;
import Model.WebCrawler;
import Model.WebPage;
import Patterns.Singleton.LoggerWriter;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *  This class is a strategy to search pages by interactive mode
 * 
 * @author BRKsCosta and danielcordeiro
 */
public class SearchIterative implements ISearchCriteria {

    private WebCrawler model;
    private int countHttpsLinks = 0;
    private int countPageNotFound = 0;
    private LoggerWriter logW = LoggerWriter.getInstance();
    
    public SearchIterative(WebCrawler model) {
        this.model = model;
    }

    @Override
    public Iterable<WebPage> searchPages(WebPage webPage) {
        try {
            // For the memento
            this.model.setSubRootWebPageChoosed(webPage);
            
            this.countHttpsLinks = this.model.countHttpsProtocols(webPage.getPersonalURL());
            this.countPageNotFound = this.model.getPagesNotFound(webPage);
            
            System.out.println("Link da página subRoot: " + webPage.getPersonalURL() + "\nIncident WebPages:\n[");

            // Get all incident links in the visited WebPage -> This will always come in random order... and only for those who aren't 404 page not found TODO
            Queue<Link> allIncidentWebLinks;
            
            if(webPage.getStatusCode() == 404){
                this.model.getWebPagesNotFound().add(webPage);
                allIncidentWebLinks = new LinkedList();
            }else{
                allIncidentWebLinks = webPage.getAllIncidentWebPages(webPage.getPersonalURL());
            }

            for (Link link : allIncidentWebLinks) {
                
                countHttpsLinks += this.model.countHttpsProtocols(link.getLinkName());
                
                // This WebPage can already exists with that link
                Vertex<WebPage> vertexWebPageFound = this.model.getEqualWebPageVertex(link.getLinkName());
                
                // Check if it exists already a WebPage with that link
                if(vertexWebPageFound != null){
                    // Insert a new Link between WebPages
                    this.model.getGraph().insertEdge(webPage, vertexWebPageFound.element(), link);
                }else{
                    // Insert a new WebPage in the graph
                    WebPage webPageInserting = new WebPage(link.getLinkName());
                    this.model.getGraph().insertVertex(webPageInserting);
                    this.model.getPagesList().add(webPageInserting);
                    this.model.getGraph().insertEdge(webPage, webPageInserting, link); 
                  
                    System.out.println("Link da sub-página: " + webPageInserting.getPersonalURL());
                    
                    countPageNotFound += this.model.getPagesNotFound(webPageInserting);
                }
            }
            System.out.println("]\n");
            
            return this.model.getPagesList();
        } catch (IOException ex) {
            this.model.getLogger().writeToLog("Error Search Pages algorithm: " + ex.getMessage());
        }
        return null;
    }
}