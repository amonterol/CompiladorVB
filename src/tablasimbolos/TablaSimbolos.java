/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tablasimbolos;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author abmon
 * 
 * Map<String, PalabrasReservadas> palabrasReservadas;

    static {

        palabrasReservadas = new HashMap<>();
 */
public class TablaSimbolos {
    private  Map<Map,String> tablaSimbolosActual = null;
    private TablaSimbolos tablaSimbolosAnterior;
    
    public TablaSimbolos(TablaSimbolos tablaAnterior){
        this.tablaSimbolosActual = new HashMap<>();
        this.tablaSimbolosAnterior = tablaAnterior;
    }

    public Map<Map, String> getTablaSimbolosActual() {
        return tablaSimbolosActual;
    }

    public void setTablaSimbolosActual(Map<Map, String> tablaSimbolosActual) {
        this.tablaSimbolosActual = tablaSimbolosActual;
    }

    public TablaSimbolos getTablaSimbolosAnterior() {
        return tablaSimbolosAnterior;
    }

    public void setTablaSimbolosAnterior(TablaSimbolos tablaSimbolosAnterior) {
        this.tablaSimbolosAnterior = tablaSimbolosAnterior;
    }
    
    
}
