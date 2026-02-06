/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yoga
 */
public class FindDescriptionTest {
    
    public FindDescriptionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of finddescription method, of class FindDescription.
     */
    @Test
    public void testFinddescription() {
        System.out.println("finddescription");
        FindDescription instance = new FindDescription();
//        instance.findDescription();
//708688004|Percutaneous fine needle aspiration biopsy of kidney using imaging guidance (procedure)
        String fsn = instance.getFSN(708688004);
        System.out.println("Query result FSN: " + fsn); 
        String answer ="Percutaneous fine needle aspiration biopsy of kidney using imaging guidance (procedure)";
        assertEquals(fsn, answer);
    }
    
}
