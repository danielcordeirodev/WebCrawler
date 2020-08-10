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
 * This class is a strategy to search pages by interactive mode
 * 
 * @author BRKsCosta
 */
public class SearchDepth implements ISearchCriteria {

    private WebCrawler model;
    private LoggerWriter logW = LoggerWriter.getInstance();
    
    public SearchDepth(WebCrawler model) {
        this.model = model;
    }

    @Override
    public Iterable<WebPage> searchPages(WebPage webPage) {
        try {
            
            int countLevelReached = -1;
            int countHttpsLinks = 0;
            int countPageNotFound = 0;
            
            Queue<WebPage> webPagesToVisit = new LinkedList<>();
            
            if (this.model.getNumCriteria() == -1) {
                return this.model.getPagesList();
            }
            
            // First of all set the depth level 1 to the webPage root and set the attribute that it is the last WebPage of the level 1, Obvious, it is the root!!
            webPage.setDepth(0);
            webPage.setIsLastOfALevel(true);
            
            if (this.model.checkIfHasWebPage(webPage) == false) {
                // Insert the webPage in the graph
                this.model.getGraph().insertVertex(webPage);
                logW.writeToLog(webPage.getTitleName() + " | "
                        + webPage.getPersonalURL() + " | " + webPage.getTitleName()
                        + " | " + webPage.getNumberLinks());
            }
            
            countLevelReached++;
            
            webPagesToVisit.add(webPage);
            this.model.getPagesList().add(webPage);
            
            countHttpsLinks = this.model.countHttpsProtocols(webPage.getPersonalURL());
            countPageNotFound = this.model.getPagesNotFound(webPage);
            
            while (!webPagesToVisit.isEmpty()){
                
                // Check if it is still in the wanted level 
                if (countLevelReached == this.model.getNumCriteria()) {
                        return this.model.getPagesList();
                }
                
                // Picks a WebPage to visit
                WebPage visitedWebPage = webPagesToVisit.poll();
                System.out.println("Link da página root: " + visitedWebPage.getPersonalURL() + "\nIncident WebPages:\n[");
                
                // Get all incident links in the visited WebPage -> This will always come in random order... and only for those who aren't 404 page not found TODO
                Queue<Link> allIncidentWebLinks;
                if(visitedWebPage.getStatusCode() == 404){
                    this.model.getWebPagesNotFound().add(webPage);
                    allIncidentWebLinks = new LinkedList();
                }else{
                    allIncidentWebLinks = visitedWebPage.getAllIncidentWebPages(visitedWebPage.getPersonalURL());
                }
                
                for (Link link : allIncidentWebLinks){
                    // This WebPage can already exists with that link
                    Vertex<WebPage> vertexWebPageFound = this.model.getEqualWebPageVertex(link.getLinkName());
                    
                    if(vertexWebPageFound != null){
                        // Remove this link from the list
                        allIncidentWebLinks.remove(link);
                        // Insert a new Link between WebPages
                        this.model.getGraph().insertEdge(visitedWebPage, vertexWebPageFound.element(), link);
                    }
                }
                
                // This variable will help us to know when it reaches to the last element from the queue
                int incidentLinksAdded = 0;
                
                for (Link link : allIncidentWebLinks){
                    
                    // Insert a new WebPage in the graph
                    WebPage webPageInserting = new WebPage(link.getLinkName());
                    webPageInserting.setDepth(countLevelReached + 1);
                    
                    // Conditional Struture to check if it is visiting the last WebPage of the previous level AND the webPageInserting is the last incident of that last WebPage
                    if(visitedWebPage.getIsLastOfALevel() == true && incidentLinksAdded == allIncidentWebLinks.size() - 1){
                        webPageInserting.setIsLastOfALevel(true);
                        countLevelReached++; // finished the BFS of this level
                    }
                    
                    this.model.getGraph().insertVertex(webPageInserting);
                    this.model.getPagesList().add(webPageInserting);
                    webPagesToVisit.add(webPageInserting);
                    incidentLinksAdded++;
                    
                    countHttpsLinks += this.model.countHttpsProtocols(link.getLinkName());
                    countPageNotFound += this.model.getPagesNotFound(webPageInserting);
                    
                    System.out.println("Link da sub-página: " + webPageInserting.getPersonalURL());
                    
                    // Insert a new Link between WebPages
                    this.model.getGraph().insertEdge(visitedWebPage, webPageInserting, link);
                    logW.writeToLog(webPageInserting.getTitleName() + " | "
                            + webPageInserting.getPersonalURL() + " | " + visitedWebPage.getTitleName()
                            + " | " + this.model.getGraph().incidentEdges(this.model.getEqualWebPageVertex(webPageInserting.getPersonalURL())).size());
                }
                System.out.println("]\n");
            }

            return this.model.getPagesList();
        } catch (IOException ex) {
            this.model.getLogger().writeToLog("Error Search Pages algorithm: " + ex.getMessage());
        }
        return null;
            
    }
}
