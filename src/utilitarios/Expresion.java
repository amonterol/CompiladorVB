/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilitarios;

/**
 *
 * @author abmon
 */
public class Expresion {

    protected Expresion izquierda;
    protected Token operador;
    protected Expresion derecha;

    public Expresion(Expresion izquierda, Token operador, Expresion derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    
    public Expresion getIzquierda() {
        return izquierda;
    }

    public void setIzquierda(Expresion izquierda) {
        this.izquierda = izquierda;
    }

    public Token getOperador() {
        return operador;
    }

    public void setOperador(Token operador) {
        this.operador = operador;
    }

    public Expresion getDerecha() {
        return derecha;
    }

    public void setDerecha(Expresion derecha) {
        this.derecha = derecha;
    }

}
