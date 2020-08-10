/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

// My packages
import org.junit.Before;

/**
 *
 * @author BRKsCosta
 */
public class WebCrawlerTest {

    private WebCrawler webCrawlerInstance;
    
    String moodle = "https://moodle.ips.pt/1920/";
    WebPage web404;
    String testeWebCrawler = "http://www.brunomnsilva.com/sandbox/index.html";  
    @Before
    public void setUp() throws IOException, WebCrawlerException {
        //this.webCrawlerInstance = new WebCrawler(this.testeWebCrawler, 15, WebCrawler.StopCriteria.PAGES);
        // Test pages not found
        String page404 = "https://deviup.com.br:3001/api/lembrete/2";
        this.web404 = new WebPage(page404);
        webCrawlerInstance.getGraph().insertVertex(web404);
        //webCrawlerInstance.start();
    }

    /**
     * Test a page when is not found
     *
     * @throws IOException
     * @throws WebCrawlerException
     */
    @Test
    public void pageNotFound_nPaginasNaoEncontrada_adicionaPaginaNaoEncontrada() throws IOException, WebCrawlerException {
        assertEquals(1, webCrawlerInstance.getPagesNotFound(web404));
    }

    @Test
    public void countLinks_numeroTotalLinks_iteraListaLinks() throws IOException, WebCrawlerException {
        int result = webCrawlerInstance.countLinks();
        int expResult = 1;
        assertEquals(expResult, result);
    }

    @Test
    public void countWebPages_numeroTotalPaginas_iteraListaPaginas() {
        int result = webCrawlerInstance.countWebPages();
        int expResult = 4;
        assertEquals(expResult, result);
    }
}
