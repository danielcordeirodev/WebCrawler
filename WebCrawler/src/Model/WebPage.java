package Model;

import static Patterns.Factories.Factories.*;
import Patterns.Singleton.LoggerWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.jsoup.HttpStatusException;

/**
 * Class that represents the <code>Vertex</code> on graph.
 *
 * @author BRKsCosta and danielcordeiro.
 *
 */
public class WebPage {

    private LoggerWriter logger = LoggerWriter.getInstance();
    private int depth = 0;
    private boolean isLastOfALevel = false;
    private String titleName = "";
    private String personalURL = "";
    private final Queue<Link> listIncidentsWebPages;
    private int statusCode;

    /**
     * Build a new object of this type
     *
     * @param url Base URL to search
     */
    // This constructor is used only for depth criteria build
    public WebPage(String url) {
        this.listIncidentsWebPages = new LinkedList<>();
        // Instantiate the personalUrl
        this.personalURL = url;
        try {
            // Create a connection to the url
            Connection connection = Jsoup.connect(this.personalURL).
                    userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").
                    timeout(10000);
            // Get the status code
            this.statusCode = this.getStatusCode();

            insertStatusCodeTitle(connection);
        } catch (IllegalArgumentException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
        }

    }

    // <editor-fold defaultstate="collapsed" desc=" Getters ">
    /**
     * This method get the depth
     *
     * @return A number
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * Get the if is the last of the level
     *
     * @return True or False
     */
    public boolean getIsLastOfALevel() {
        return this.isLastOfALevel;
    }

    /**
     * Get a name of the title
     *
     * @return Name of title
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * Get WebPage personal URL
     *
     * @return
     */
    public String getPersonalURL() {
        return personalURL;
    }

    /**
     * Get status code from WebPage
     *
     * @return Return the status code in a integer
     */
    public final int getStatusCode() {
        try {
            URL url = new URL(this.personalURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            
            return connection.getResponseCode();
        } catch (IOException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
        }
        return 0;
    }

    /**
     * Return all incidents WebPages in a queue {@link java.util.Queue}.
     *
     * @return A queue with all incident pages.
     */
    public Queue<Link> getListIncidentsWebPages() {
        return listIncidentsWebPages;
    }
    
    public int getNumberLinks() {
        return listIncidentsWebPages.size();
    }

// </editor-fold>
    
    /**
     * Return all incident pages connected to the current WebPage
     * 
     * @param personalLink The URL of the WebPage
     * @return A queue with all WebPages.
     * @throws WebCrawlerException
     * @throws IOException 
     */
    public Queue<Link> getAllIncidentWebPages(String personalLink) throws WebCrawlerException, IOException {
        
        try {
            //Check if page is not found
            if ("".equals(personalLink) || personalLink == null) {
                throw new WebCrawlerException("URL não pode ser vazio ou nulo");
            }

            Document doc = Jsoup.connect(personalLink).get();
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String href = link.attr("abs:href");
                String newHref = processLink(href, personalLink);
                Link newObjLink = new Link(newHref);
                listIncidentsWebPages.offer(newObjLink);
            }

            Set<Link> set = new HashSet(listIncidentsWebPages);
            listIncidentsWebPages.clear();
            listIncidentsWebPages.addAll(set);

            return listIncidentsWebPages;

        } catch (HttpStatusException ex) {
            if (ex.getStatusCode() == 404) {
                this.listIncidentsWebPages.offer(new Link(ex.getUrl()));
                logger.writeToLog(ex.getMessage());
                getView().showErrorStackTraceException(ex.getMessage());
            }
            return listIncidentsWebPages;
        }
    }
    
    /**
     * This method insert the title of the webpage in case if the response of
     * the page is 200 OK or 404 - Not Found.
     *
     * @param connection Receives object of the type Connection
     * {@link org.jsoup}
     */
    private void insertStatusCodeTitle(Connection connection) {
        try {
            switch (statusCode) {
                case 404:
                    this.statusCode = 404;
                    this.titleName = "404 - Page not found";
                    break;
                case 200:
                    this.titleName = connection.get().title();
                    this.statusCode = 200;
                    break;
                default:
                    this.titleName = Integer.toString(getStatusCode());
                    break;
            }
        } catch (IOException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Setters ">
// Setters
    /**
     * Set the a depth number
     *
     * @param depth A number
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Set with true or false if the last of level.
     *
     * @param isLastOfALevel True or False
     */
    public void setIsLastOfALevel(boolean isLastOfALevel) {
        this.isLastOfALevel = isLastOfALevel;
    }

    /**
     * Set the status code
     *
     * @param status A number
     */
    public void setStatusCode(int status) {
        this.statusCode = status;
    }

    /**
     * Set personal page URL
     *
     * @param personalURL The URL to search
     */
    public void setPersonalURL(String personalURL) {
        this.personalURL = personalURL;
    }

    /**
     * Set a title name
     *
     * @param titleName String title name to set
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Auxiliar Methods ">
    /**
     * Process different types of URL
     *
     * @param link The specific link
     * @param startURL The base site URL
     * @return return a processed link in <code>string</code>
     */
    private String processLink(String link, String startURL) {
        try {
            URL u = new URL(startURL);
            if (link.startsWith("./")) {
                link = link.substring(2, link.length());
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.startsWith("#")) {
                link = startURL + link;
            } else if (link.startsWith("javascript:")) {
                link = null;
            } else if (link.startsWith("../") || (!link.startsWith("http://") && !link.startsWith("https://"))) {
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            }
            return link;
        } catch (MalformedURLException ex) {
            logger.writeToLog(ex.getMessage());
            getView().showErrorStackTraceException(ex.getMessage());
            return null;
        }
    }

    /**
     * Just strip filename on link
     *
     * @param path The URL of a website
     * @return Position of filename
     */
    private String stripFilename(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos + 1);
    }

// </editor-fold>
    
    /**
     * To print WebPage object
     *
     * @return Formatted title name
     */
    @Override
    public String toString() {
        return "Title: " + titleName + " Code = " + this.statusCode + "\n";
    }

}
