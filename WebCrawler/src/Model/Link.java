package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that represents de <code>Edge<V, E></code> on graph.
 *
 * Class that represents de Edge on graph.
 *
 * @author BRKsCosta
 */
public class Link {

    private String linkName = "";
    private List<String> array = new ArrayList();

    /**
     * Build a new object
     *
     * @param name The URL from an website
     */
    public Link(String name) {
        this.linkName = name;
        array.add(this.linkName);
        removeDuplicates(linkName);
    }
    
    /**
     * This method remove duplicated links
     * @param name The concrete link
     */
    private void removeDuplicates(String name) {
        for (Iterator<String> iterator = array.iterator(); iterator.hasNext();) {
            String value = iterator.next();
            if (array.contains(value)) {
                iterator.remove();
            } else {
                this.linkName = name;
            }
        }
    }

    /**
     * Get the URL
     *
     * @return return the URL
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     * Set a new name of URL
     *
     * @param linkName The URL
     */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    /**
     * Format the URL
     *
     * @return URL formatted
     */
    @Override
    public String toString() {
        return "Link Name: " + linkName + "\n";
    }

}
