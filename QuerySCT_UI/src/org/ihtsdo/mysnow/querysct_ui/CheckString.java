/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

/**
 *
 * @author yoga
 */
public class CheckString {
    public static boolean isLong(String st){
        try{
            long l = Long.parseLong(st);
        }catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }
    
}
