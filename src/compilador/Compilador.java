/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package compilador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Compilador {
      
    
    public static void main(String[] args) {
      
        AnalizadorLexico lexico =  new AnalizadorLexico(args);
        AnalizadorSintactico sintactico = new AnalizadorSintactico(lexico);
        
        try {           
            sintactico.leyendoLexico();
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
  
}
