/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.Stategy;

import Model.Link;
import Model.WebCrawler;
import Model.WebCrawlerException;
import Model.WebPage;
import Patterns.Singleton.LoggerWriter;
import com.brunomnsilva.smartgraph.graph.Vertex;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is a strategy to search all pages in breadth-first.
 *
 * @author BRKsCosta and danielcordeiro
 */
public class SearchPages implements ISearchCriteria {

    private WebCrawler model;
    private int countHttpsLinks = 0;
    private int countPageNotFound = 0;
    private LoggerWriter logW = LoggerWriter.getInstance();

    public SearchPages(WebCrawler model) {
        this.model = model;
    }

    @Override
    public Iterable<WebPage> searchPages(WebPage webPage)
            throws WebCrawlerException {

        try {
            // Contar numero de WebPages contadas
            int countMaxVisitedPage = 0;

            Queue<WebPage> webPagesToVisit = new LinkedList<>();

            if (this.model.getNumCriteria() == 0) {
                return this.model.getPagesList();
            }

            if (this.model.checkIfHasWebPage(webPage) == false) {
                // Insert the webPage in the graph
                this.model.getGraph().insertVertex(webPage);
                logW.writeToLog(webPage.getTitleName() + " | "
                        + webPage.getPersonalURL() + " | " + webPage.getTitleName()
                        + " | " + webPage.getNumberLinks());
            }

            webPagesToVisit.add(webPage);
            this.model.getPagesList().add(webPage);

            // Increment countMaxVisitedPage by 1
            countMaxVisitedPage++;
            this.countHttpsLinks = this.model.countHttpsProtocols(webPage.getPersonalURL());
            this.countPageNotFound = this.model.getPagesNotFound(webPage);

            while (!webPagesToVisit.isEmpty()) {
                WebPage visitedWebPage = webPagesToVisit.poll();
                System.out.println("Link da página root: " + visitedWebPage.getPersonalURL() + "\nIncident WebPages:\n[");

                // Get all incident links in the visited WebPage -> This will always come in random order... and only for those who aren't 404 page not found TODO
                Queue<Link> allIncidentWebLinks;
                if (visitedWebPage.getStatusCode() == 404) {
                    allIncidentWebLinks = new LinkedList();
                } else {
                    allIncidentWebLinks = visitedWebPage.getAllIncidentWebPages(visitedWebPage.getPersonalURL());
                }

                for (Link link : allIncidentWebLinks) {

                    if (countMaxVisitedPage == this.model.getNumCriteria()) {
                        return this.model.getPagesList();
                    }

                    countHttpsLinks += this.model.countHttpsProtocols(link.getLinkName());
                    
                    // This WebPage can already exists with that link
                    Vertex<WebPage> vertexWebPageFound = this.model.getEqualWebPageVertex(link.getLinkName());

                    // Check if it exists already a WebPage with that link
                    if(vertexWebPageFound != null){
                        // Insert a new Link between WebPagess
                        this.model.getGraph().insertEdge(visitedWebPage, vertexWebPageFound.element(), link);
                    }else{
                        // Insert a new WebPage in the graph
                        WebPage webPageInserting = new WebPage(link.getLinkName());
                        this.model.getGraph().insertVertex(webPageInserting);
                        this.model.getPagesList().add(webPageInserting);
                        webPagesToVisit.add(webPageInserting);
                        
                        // Insert a new Link between WebPages
                        this.model.getGraph().insertEdge(visitedWebPage, webPageInserting, link);

                        logW.writeToLog(webPageInserting.getTitleName() + " | "
                            + webPageInserting.getPersonalURL() + " | " + visitedWebPage.getTitleName()
                            + " | " + this.model.getGraph().incidentEdges(this.model.getEqualWebPageVertex(webPageInserting.getPersonalURL())).size());

                        // Increment countMaxVisitedPage by 1
                        countMaxVisitedPage++;
                        
                        System.out.println("Link da sub-página: " + webPageInserting.getPersonalURL());
                        
                        countPageNotFound += this.model.getPagesNotFound(webPageInserting);
                    }
                }
                System.out.println("]\n");
            }

            return model.getPagesList();
        } catch (IOException ex) {
            model.getLogger().writeToLog("Error Search Pages algorithm: " + ex.getMessage());
        }
        return null;
    }

}
