/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import static junit.framework.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author BRKsCosta
 */
public class WebPageTest {

    private WebPage wepageInstance;
    String moodle = "https://moodle.ips.pt/1920/";

    @Before
    public void setUp() throws IOException {
        this.wepageInstance = new WebPage(this.moodle);
    }

    /**
     * Test of getTitleName method, of class WebPage.
     */
    @Test
    public void getTitleName_tituloPagina_obterTitulo() {
        System.out.println("getTitleName");
        String expResult = "Moodle do Instituto Politécnico de Setúbal";
        assertEquals(expResult, wepageInstance.getTitleName());
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setPersonalURL method, of class WebPage.
     */
    @Test
    public void setPersonalURL_URLMudado_mudarURL() {
        System.out.println("setPersonalURL");
        String expResult = this.moodle;
        wepageInstance.setPersonalURL(this.moodle);
        assertEquals(expResult, this.moodle);
    }

    /**
     * Test of getPersonalURL method, of class WebPage.
     */
    @Test
    public void getPersonalURL_obterUrl_inserirURL() {
        System.out.println("getPersonalURL");
        String expResult = this.moodle;
        assertEquals(expResult, wepageInstance.getPersonalURL());
    }
    

    /**
     * Test of getStatusCode method, of class WebPage.
     */
    @Test
    public void getStatusCode_codigo200OK_obterStatusCode() throws Exception {
        System.out.println("getStatusCode");
        int expResult = 200;
        int result = wepageInstance.getStatusCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTitleName method, of class WebPage.
     */
    @Test
    public void setTitleName_tituloMudado_mudarTituloPagina() {
        System.out.println("setTitleName");
        String expResult = "Teste";
        wepageInstance.setTitleName("Teste");
        assertEquals(expResult, "Teste");
    }

    /**
     * Test of getAllIncidentWebPages method, of class WebPage.
     */
    @Test
    public void getAllIncidentWebPages_listaLinksPagina_obterTodosLinks() throws Exception {
        System.out.println("getAllIncidentWebPages");
        Queue<Link> expResult = new LinkedList();
        Queue<Link> result = wepageInstance.getAllIncidentWebPages(this.moodle);
        expResult.addAll(result);
        assertEquals(expResult, result);
    }

}
