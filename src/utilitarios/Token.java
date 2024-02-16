
package utilitarios;

/**
 *
 * @author abmon
 */
public class Token {
    
      
    private String lexema;
    private String tipoDeToken;
    private String valor;
    private int numeroLinea;


    public Token() {
    }

    public Token(String lexema, String tipo, String valor, int linea) {
        this.lexema = lexema;
        this.tipoDeToken = tipo;
        this.valor = valor;
        this.numeroLinea = linea;
    }

         
    public String getLexema() {
        return lexema;
    }

    public String getTipoDeToken() {
        return tipoDeToken;
    }

    public String getValor() {
        return valor;
    }

    public int getLinea() {
        return numeroLinea;
    }

     @Override
    public String toString() {
    return getTipoDeToken()+ " " + getLexema() + " " + getValor() + " " + getLinea();
  }
}
