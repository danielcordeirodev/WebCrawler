package Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.brunomnsilva.smartgraph.graph.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import Patterns.Singleton.LoggerWriter;
import java.util.Date;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import Patterns.Memento.IMemento;
import Patterns.Memento.IOriginator;
import Patterns.Stategy.ISearchCriteria;
import Patterns.Stategy.SearchDepth;
import Patterns.Stategy.SearchIterative;
import Patterns.Stategy.SearchPages;
import Views.HomeView.StopCriteria;
import java.util.LinkedList;

@SuppressWarnings("null")
/**
 * Model to be created to build de graph Link <code>Vertex</code> WebPage and
 * <code>Edge</code> Use the methods setChanged() and notifyObservers()
 *
 * @author BRKsCosta and danielcordeiro
 */
public class WebCrawler extends Observable implements IOriginator, Serializable {

    private final LoggerWriter logger = LoggerWriter.getInstance();

    private ISearchCriteria searchCriteria;

    // Pertinent variables to the DiGraph structure:
    private WebPage rootWebPage;

    // Iterative variables
    private WebPage subRootWebPageChoosed;
    private List<WebPage> webPagesNotFound;

    private WebPage previousSubRootWebPageChoosed; // Still seing if it is needed

    private StopCriteria stopCriteriaChoosed; // PAGES, DEPTH, ITERATIVE.
    private final List<WebPage> pagesList = new ArrayList<>();

    private final Graph<WebPage, Link> graph;

    // Statistics
    private int countHttpsLinks = 0;
    private int countPageNotFound = 0;
    private int numCriteria = 0;
    public boolean isFinished = false;

    public WebCrawler(Graph graph) {
        this.graph = graph;
    }
    
    public WebCrawler() {
        this(new MyDigraph<>());
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters ">
    /**
     * Get a instance of the logger
     *
     * @return A Logger object
     */
    public LoggerWriter getLogger() {
        return logger;
    }

    /**
     * Get all links
     *
     * @return A collection of edges
     */
    public List<Edge<Link, WebPage>> getAllLinks() {
        return (List<Edge<Link, WebPage>>) graph.edges();
    }

    /**
     * Get page list
     *
     * @return A list of pages
     */
    public List<WebPage> getPagesList() {
        return pagesList;
    }

    /**
     * Get the number of pages
     *
     * @return A number
     */
    public int getNumCriteria() {
        return numCriteria;
    }

    /**
     * Get HTTPS links
     *
     * @return A number
     */
    public int getCountHttpsLinks() {
        return countHttpsLinks;
    }

    /**
     * Get count of pages not found
     *
     * @return A number
     */
    public int getCountPageNotFound() {
        return countPageNotFound;
    }

    /**
     * Get stop criteria
     *
     * @return
     */
    public StopCriteria getStopCriteriaChoosed() {
        return stopCriteriaChoosed;
    }

    /**
     * Get root web page
     *
     * @return The root web page
     */
    public Vertex<WebPage> getRootWebPage() {
        
        for (Vertex<WebPage> v : graph.vertices()) {
            if (v.element().equals(this.rootWebPage)) {
                return v;
            }
        }
        return null;
    }
    
    public Graph<WebPage, Link> getGraph() {
        return this.graph;
    }
    
    public List<WebPage> getWebPagesNotFound() {
        return webPagesNotFound;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Setters ">
    /**
     * Set the number of web pages to how criteria
     *
     * @param numCriteria A number
     */
    public void setNumCriteria(int numCriteria) {
        this.numCriteria = numCriteria;
    }

    /**
     * Set sub root pages choosed
     *
     * @param subRootWebPageChoosed A WebPage object
     */
    public void setSubRootWebPageChoosed(WebPage subRootWebPageChoosed) {
        this.subRootWebPageChoosed = subRootWebPageChoosed;
    }

    /**
     * Set the count of HTTPS pages
     *
     * @param countHttpsLinks A number
     */
    public void setCountHttpsLinks(int countHttpsLinks) {
        this.countHttpsLinks = countHttpsLinks;
    }

    /**
     * Set the pages not found
     *
     * @param countPageNotFound A number
     */
    public void setCountPageNotFound(int countPageNotFound) {
        this.countPageNotFound = countPageNotFound;
    }

    /**
     * Set a stop criteria
     *
     * @param stopCriteriaChoosed A ENUM of the type StopCriteria
     */
    public void setStopCriteriaChoosed(StopCriteria stopCriteriaChoosed) {
        this.stopCriteriaChoosed = stopCriteriaChoosed;
    }

    /**
     * Set the root webpage
     *
     * @param rootWebPage A object of the type webpage
     */
    public void setRootWebPage(WebPage rootWebPage) {
        this.rootWebPage = rootWebPage;
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    /**
     * Clear the all elements in the graph.
     */
    public void clearGraph() {
        
        this.graph.edges().forEach((edge) -> {
            this.graph.removeEdge(edge);
        });
        
        this.graph.vertices().forEach((webPage) -> {
            this.graph.removeVertex(webPage);
        });
        
        this.isFinished = true;
        
        setChanged();
        notifyObservers();
    }

    /**
     * This method is responsible to select a type of search to the WebPages
     *
     * @param criteria The type of search
     * @param numCriteria The stop criteria
     * @param inputUrl The page to search
     */
    public void buildWebCrawler(StopCriteria criteria, int numCriteria, String inputUrl) {

        // Assign values
        this.numCriteria = numCriteria;
        
        if (this.numCriteria != 0) {
            this.rootWebPage = new WebPage(inputUrl);
            this.graph.insertVertex(this.rootWebPage);
        }
        
        switch (criteria) {
            case PAGES:
                this.searchCriteria = new SearchPages(this);
                break;
            case DEPTH:
                this.searchCriteria = new SearchDepth(this);
                break;
            default: // Iterative
                this.searchCriteria = new SearchIterative(this);
                break;
        }
        
        this.webPagesNotFound = new LinkedList<>();
        
        this.searchPagesAndPrint(this.rootWebPage);
    }

    /**
     * The aux method to iterative mode that insert the new web pages when the
     * user click.
     *
     * @param subRoot The parent node
     * @throws IOException
     */
    public void insertNewSubWebPageCrawler(Vertex<WebPage> subRoot) throws IOException {
        
        this.searchPagesAndPrint(subRoot.element());
        
        setChanged();
        notifyObservers();
    }

    /**
     * This method helps to see the pages that was generated.
     *
     * @param rootWebPage A object of the type WebPage
     */
    private void searchPagesAndPrint(WebPage rootWebPage) {
        Iterable<WebPage> it = searchCriteria.searchPages(rootWebPage);
        
        print("\n ========= Estatísticas ========= \n");
        print(" »»»»» Páginas Visitadas (%d) ««««« \n\n %s", this.countWebPages(), it);
        print(" »»»»» Páginas não encontradas (%d) «««««", this.countPageNotFound);
        print(" »»»»» Ligações HTTPS (%d) «««««", this.countHttpsLinks);
        print(" »»»»» Ligações entre páginas (%d) «««««", this.countLinks());
        
        setChanged();
        notifyObservers();
    }
    
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    /**
     * Count HTTPS protocols
     *
     * @param startURL site URL
     * @return Number of pages founded
     * @throws MalformedURLException
     */
    public int countHttpsProtocols(String startURL) throws MalformedURLException {
        int count = 0;
        URL u = new URL(startURL);
        if (u.getProtocol().equals("https")) {
            count++;
        }
        return count;
    }

    /**
     * Count number of pages not found
     *
     * @param myWebPage
     * @return Counter of pages
     */
    public int getPagesNotFound(WebPage myWebPage) {
        
        if (myWebPage.getStatusCode() == 404) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks if exists already a webPage like the parameter inside the webPage
     *
     * @param webPage we want to check
     * @return if exists the webPage
     */
    public boolean checkIfHasWebPage(WebPage webPage) {
        for (Vertex<WebPage> page : graph.vertices()) {
            if (page.element() == webPage) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get if the link is equal the link web page
     *
     * @param linkName
     * @return
     */
    public Vertex<WebPage> getEqualWebPageVertex(String linkName) {
        for (Vertex<WebPage> page : graph.vertices()) {
            if (page.element().getPersonalURL().equals(linkName)) {
                return page;
            }
        }
        return null;
    }

    /**
     * Counter of links
     *
     * @return Number of links (Edges)
     */
    public int countLinks() {
        return graph.numEdges();
    }

    /**
     * Count titles from a specific website
     *
     * @return Number of titles (Vertex)
     */
    public int countWebPages() {
        
        return graph.numVertices();
    }
    
    @Override
    public IMemento save() {
        // Creates a new private Memento Object and returns it
        try {
            return new WebCrawlerMemento(this.graph, this.webPagesNotFound, this.rootWebPage);
        } catch (IOException ex) {
            Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public void restore(IMemento savedState) {
        
        WebCrawlerMemento save = (WebCrawlerMemento) savedState;
        
        this.clearGraph();
        
        for (Vertex<WebPage> webPage : save.getGraph().vertices()) {
            System.out.println("Vertex a inserir -> " + webPage.element().getPersonalURL());
            this.graph.insertVertex(webPage.element());
        }
        
        for (Edge<Link, WebPage> edge : save.getGraph().edges()) {
            System.out.println("Edge a inserir -> " + edge.element().getLinkName());
            this.graph.insertEdge(edge.vertices()[0], edge.vertices()[1], edge.element());
        }
        
        this.isFinished = true; // Just for testing, TODO
        
        setChanged();
        notifyObservers();
        
    }

    // </editor-fold>

    /**
     * Private class to implement the memento
     */
    private class WebCrawlerMemento implements IMemento {

        private WebPage rootWebPage;
        private final Graph<WebPage, Link> graph;
        private final Date createdAt;
        private List<WebPage> webPagesNotFound;

        public WebCrawlerMemento(Graph<WebPage, Link> graph, List<WebPage> webPagesNotFound, WebPage rootWebPage) throws IOException {
     
            this.graph = new MyDigraph<>();
            
            graph.vertices().forEach((vertex) -> {
                this.graph.insertVertex(vertex.element());
            });
            
            graph.edges().forEach((edge) -> {
                this.graph.insertEdge(edge.vertices()[0], edge.vertices()[1], edge.element());
            });

            this.webPagesNotFound = new LinkedList<>(webPagesNotFound);
            this.rootWebPage = rootWebPage;
            this.createdAt = new Date();
        }

        // Getters
        /**
         * Get the root web page
         *
         * @return
         */
        public WebPage getRootWebPage() {
            return this.rootWebPage;
        }

        public List<WebPage> getPagesNotFound(){
            return this.webPagesNotFound;
        }

        /**
         * Get the create date
         *
         * @return
         */
        public Date getCreatedAt() {
            return this.createdAt;
        }
        
        public Graph<WebPage, Link> getGraph(){
            return this.graph;
        }

        @Override
        public String getDescription() {
            return String.format("WebCrawler Memento created at %s with the "
                    + "vertex %s", createdAt.toString(),
                    this.rootWebPage.getPersonalURL());
        }
    }

}
