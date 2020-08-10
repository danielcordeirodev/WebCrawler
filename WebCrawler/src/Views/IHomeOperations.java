/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import Controller.HomeController;
import java.util.Observer;
/**
 *
 * @author BRKsCosta
 */
public interface IHomeOperations extends Observer {
    /**
     * Get input URL
     * @return The URL
     */
    String getInputURL();
    
    /**
     * Import a file
     * @param controller Controller object
     */
    void importFile(HomeController controller);
    
    /**
     * Export a file
     * @param controller Controller object
     */
    void exportFile(HomeController controller);
    /**
     * Exit the application
     */
    void exitApp();
    
    /**
     *  Clear errors
     */
    void clearError();
    
    /**
     * Show errors along the application run time 
     * @param errorMsg The concrete error message
     */
    void showError(String errorMsg);
    
    /**
     * Set triggers to all buttons 
     * @param controller The controller object
     */
    void setTriggersButtons(HomeController controller);
    
}
