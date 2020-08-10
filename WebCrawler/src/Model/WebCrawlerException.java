/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 * This throw exception in run time
 * @author brkscosta and danielcordeiro
 */
public class WebCrawlerException extends RuntimeException {

    public WebCrawlerException(String string) {
        super(string);
    }
}
