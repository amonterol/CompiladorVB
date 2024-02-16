/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import utilitarios.Token;
import utilitarios.Error;

public class AnalizadorLexico {

    //Se usa para construir el archivo de salida cuando se ha terminado el análisis léxico
    private String[] args = null;

    private static String archivoAdjunto = "";
    private static String archivoConCodigoFuente = "";
    private static String rutaCodigoFuente = "C:\\Users\\abmon\\Desktop\\";
    private static boolean existeArchivoConExtensionVB = false;

    private static List<String> listaCodigoOriginal = new ArrayList<>();

    private static List<String> listaCodigoAnalizado;
    private Error errores = new utilitarios.Error();

    private static List<Token> listaDeTokens;
    private static List<List<Token>> listaTokens = new ArrayList<List<Token>>();

    private enum TipoDeToken {
        PALABRA_RESERVADA,
        STRING,
        NUMERO_ENTERO,
        NUMERO_REAL,
        IDENTIFICADOR,
        MULTIPLICACION,
        DIVISION,
        SUMA,
        RESTA,
        MENOR_QUE,
        MENOR_O_IGUAL_QUE,
        MAYOR_QUE,
        MAYOR_O_IGUAL_QUE,
        IGUAL,
        DIFERENTE,
        DECLARACION_DE_TIPO,
        ASIGNACION,
        COMENTARIO,
        INICIO_COMENTARIO,
        FIN_COMENTARIO,
        PUNTO_Y_COMA,
        COMA,
        PUNTO,
        PARENTESIS_IZQUIERDO,
        PARENTESIS_DERECHO,
        LLAVE_IZQUIERDO,
        LLAVE_DERECHO,
        LINEA_EN_BLANCO,
        IMPORTS, MODULE, END, SUB, MAIN, WHILE, TRY, CATCH, AS, DIM, BOOLEAN, NOT, IF, THEN, ELSE,
        DESCONOCIDO

    }

    private static final Map<String, String> palabrasReservadas;
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("as", "As");
        palabrasReservadas.put("boolean", "boolean");
        palabrasReservadas.put("catch", "Catch");
        palabrasReservadas.put("console", "Console");
        palabrasReservadas.put("dim", "Dim");
        palabrasReservadas.put("else", "else");
        palabrasReservadas.put("end", "End");
        palabrasReservadas.put("false", "false");
        palabrasReservadas.put("if", "If");
        palabrasReservadas.put("imports", "Imports");
        palabrasReservadas.put("io", "IO");
        palabrasReservadas.put("main", "Main");
        palabrasReservadas.put("module", "Module");
        palabrasReservadas.put("new", "New");
        palabrasReservadas.put("not", "Not");
        palabrasReservadas.put("readline", "ReadLine");
        palabrasReservadas.put("streamReader", "StreamReader");
        palabrasReservadas.put("string", "String");
        palabrasReservadas.put("sub", "Sub");
        palabrasReservadas.put("system", "System");
        palabrasReservadas.put("then", "then");
        palabrasReservadas.put("true", "true");
        palabrasReservadas.put("try", "Try");
        palabrasReservadas.put("while", "While");
        palabrasReservadas.put("writeline", "WriteLine");

    }

    private static final Map<String, String> tiposDeDatos;
    static {

        tiposDeDatos = new HashMap<>();
        tiposDeDatos.put("boolean", "boolean");
        tiposDeDatos.put("byte", "Byte");
        tiposDeDatos.put("char", "Char");
        tiposDeDatos.put("fecha", "Fecha");
        tiposDeDatos.put("decimal", "Decimal");
        tiposDeDatos.put("double", "Double");
        tiposDeDatos.put("definido", "Definido por usuario");
        tiposDeDatos.put("entero", "Entero");
        tiposDeDatos.put("long", "Long");
        tiposDeDatos.put("object", "Object");
        tiposDeDatos.put("short", "Short");
        tiposDeDatos.put("single", "Single");
        tiposDeDatos.put("string", "String");
        tiposDeDatos.put("uinteger", "UInteger");
        tiposDeDatos.put("ulong", "ULong");
        tiposDeDatos.put("ushort", "UShort");

    }

    public static String nombreDePrograma = " ";
    public static boolean nombreDeProgramaEsIdentificadorValido = true;

    public static boolean comandoDeVisualBasicEncontrado = false;
    public static int lineasConComandosVisualBasicEncontradas = 0;

    public static boolean comandoMODULE = false;
    public static int lineasConComandoMODULE = 0;

    public static boolean comandoEND_MODULE = false;
    public static int lineasConComandoEND_MODULE = 0;

    public static boolean comandoSUB = false;
    public static int lineasConComandoEND_SUB = 0;

    public AnalizadorLexico(String[] args) {
        this.args = args;
    }

    public void analizarPrograma() {
        /*
        String archivoAdjunto = "";
        String archivoConCodigoFuente = "";
        String rutaCodigoFuente = "C:\\Users\\abmon\\Desktop\\";
        boolean existeArchivoConExtensionVB = false;
        List<String> listaCodigoOriginal = new ArrayList<>();
         */
        //Almacena el numero de linea actual que se esta leyendo.
        int numeroDeLineaActual = 1;
        //Almacena la posicion de cursor en una linea especifica
        int posicion = 0;

        //Almacena cada comentario encontador en el analizis lexico hasta se
        //se convierta en un Token.
        boolean comentarioEncontrado = false;
        String comentario = null;

        //HashMap<Integer, String> palabrasReservadas = new HashMap();
        //Identificador nombrePrograma = null;
        //Valida si se adjunta un único archivo con código fuente al programa
        archivoAdjunto = validarExistenciaArchivoInicial(this.args);

        //Valida que la extensión del archivo sea .vb
        existeArchivoConExtensionVB = validarExtensionArchivoInicial(archivoAdjunto);

        //Determina la ruta del archivo con el código fuente y lee el archivo
        if (existeArchivoConExtensionVB) {
            archivoConCodigoFuente = rutaCodigoFuente + archivoAdjunto;

            try {
                listaCodigoOriginal = leerContenidoArchivoTexto(archivoConCodigoFuente);
            } catch (IOException ex) {
                Logger.getLogger(AnalizadorLexico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //BORRAR SOLO PARA PRUEBAS
        //System.out.println("\n0.- INICIO ESTE ES EL ARCHIVO CONVERITIDO A LISTA DE LINEAS"); //BORRAR
        imprimirContenidoArchivoTexto(listaCodigoOriginal); //BORRAR
        //System.out.println("\n0.- FIN ESTE ES EL ARCHIVO CONVERITIDO A LISTA DE LINEAS"); //BORRAR

        try {
            if (!listaCodigoOriginal.isEmpty()) {
                //System.out.println("\n1.- codigo fuente no esta vacio"); //BORRAR

                //System.out.println("\n1.- INICIO codigo fuente"); //BORRAR
                imprimirContenidoArchivoTexto(listaCodigoOriginal); //BORRAR
                //System.out.println("\n1.- FIN codigo fuente"); //BORRAR

                for (String lineaDeCodigo : listaCodigoOriginal) {

                    //System.out.println("\n2.- INICIO LINEA DE CODIGO"); //BORRAR
                    //System.out.println(lineaDeCodigo); //BORRAR
                    //System.out.println("\n2.- FIN LINEA DE CODIGO"); //BORRAR
                    listaDeTokens = new ArrayList<>();
                    if (lineaDeCodigo.isBlank() || lineaDeCodigo.isEmpty()) {
                        System.out.println("\n3.- ES UN NUEVA LINEA DE CODIGO ESTA EN BLANCO " + lineaDeCodigo); //BORRAR
                        //agregarNuevoToken("Linea en blanco", AnalizadorLexico.TipoDeToken.LINEA_EN_BLANCO.toString(), null, numeroDeLineaActual);
                        agregarNuevoToken("linea en blanco", TipoDeToken.LINEA_EN_BLANCO.toString(), null, numeroDeLineaActual);
                        listaTokens.add(listaDeTokens);
                    } else {
                        // System.out.println("\n4.-.- BORRAR> INICIO LINEA DE CODIGO CONVERTIDA A CARACTERES  " + lineaDeCodigo);
                        char[] arregloCaracteres = lineaDeCodigo.toCharArray();
                        // iterar sobre la array `char[]` usando for-loop mejorado
                        for (char ch : arregloCaracteres) {
                            System.out.print(ch);
                            System.out.print(" ");
                        }
                        System.out.println(" ");

                        identificandoTokens(arregloCaracteres, numeroDeLineaActual);
                        listaTokens.add(listaDeTokens);
                        // numeroDeLineaActual++;
                        // System.out.println("\n4.-.- BORRAR> FIN LINEA DE CODIGO CONVERTIDA A CARACTERES  " + lineaDeCodigo);
                    }
                    ++numeroDeLineaActual; //Aumenta con cada linea que es analizada
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("No hay lineas que leer en el archivo de codigo fuente" + ex);
        }

        /*
        System.out.println("\n\n<<<CANTIDAD DE TOKENS>>> " + listaDeTokens.size());
        listaDeTokens.forEach((item) -> {
            System.out.println(item.getLexema() + " " + item.getTipoDeToken() + " " + item.getValor() + " " + item.getLinea());
        });
         */
        System.out.println("\n\n<<< 218 Lexico> CANTIDAD DE TOKENS DES EL LEXICO>>> " + listaTokens.size());
        listaTokens.forEach((item) -> {
            item.forEach((tkn) -> {
                System.out.println(tkn.getLexema() + " " + tkn.getTipoDeToken() + " " + tkn.getValor() + " " + tkn.getLinea());
            });
            System.out.println("\n\n");
        });

    }

    public String validarExistenciaArchivoInicial(String[] args) {
        if (args.length == 0) {

            JOptionPane.showMessageDialog(null, errores.obtenerDescripcionDeError(100), "Falta archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println(errores.obtenerDescripcionDeError(100));
            System.exit(0);

        } else if (args.length > 1) {
            JOptionPane.showMessageDialog(null, errores.obtenerDescripcionDeError(101), "Cantidad Archivos", JOptionPane.WARNING_MESSAGE);
            System.out.println(errores.obtenerDescripcionDeError(101));
            System.exit(0);
            return "";

        } else if (args.length == 1) {
            System.out.println(" BORRAR: Este es el archivo " + args[0].toUpperCase());
            return args[0].toUpperCase();

            //nombrePrograma = new Identificador(archivoConCodigoFuente.substring(0, archivoConCodigoFuente.indexOf('.')));
            //System.out.println(" BORRAR: ESTE ES EL NOMBRE DEL PROGRAM SI SU EXTENSION " + nombrePrograma.getIdentificador() + " " + archivoConCodigoFuente.indexOf('.'));
        }
        return "";

    }

    public boolean validarExtensionArchivoInicial(String archivoInicial) {
        if (!archivoInicial.endsWith(".VB")) {
            JOptionPane.showMessageDialog(null, errores.obtenerDescripcionDeError(102), "Extension archivo", JOptionPane.WARNING_MESSAGE);
            System.out.println(errores.obtenerDescripcionDeError(102));
            System.exit(0);

        }
        return true;

    }

    //Lee el archivo con código fuente y convierte cada linea en una entrada de una lista.
    public List leerContenidoArchivoTexto(String path) throws IOException {

        List<String> contenido = new ArrayList<>();
        if (!path.isEmpty()) {
            Stream<String> stream = Files.lines(Paths.get(path));
            contenido = stream
                    .collect(Collectors.toList());
        } else {
            JOptionPane.showMessageDialog(null, "El programa fuente proporcionado + "
                    + " no esta en el directorio C:\\Users\\abmon\\Desktop\\",
                    "Falta archivo", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        return contenido;
    }

    //Crea un archivo de texto a partir del contenido de la lista con el contenido del archivo con código fuente
    public static void escribirContenidoEnArchivoTexto(List<String> lista, String ruta) {

        Path path = Paths.get(ruta);
        try (BufferedWriter br = Files.newBufferedWriter(path,
                Charset.defaultCharset(), StandardOpenOption.CREATE)) {
            for (String elemento : lista) {
                br.write(elemento);
                br.newLine();
            }
            br.close();
        } catch (Exception ex) {
            System.out.format("I/O error: %s%n", ex);
        }

    }

    //Imprime el contenido de la lista con las líneas de código
    public static void imprimirContenidoArchivoTexto(List listaDeLineas) {
        System.out.println("BORRAR - ESTA ES LA LISTA DEL CONTENIDO DEL ARCHIVO" + "\n");

        listaDeLineas.forEach(linea -> {
            System.out.println(linea);
        });

    }

    public static void identificandoTokens(char[] arregloCaracteres, int numeroLinea) {
        char caracterActual = ' ';
        char caracterSiguiente = ' ';
        String identificador = "";
        String str = "";
        String comentario = "";
        String numero = "";

        for (int i = 0; i < arregloCaracteres.length; i++) {

            caracterActual = arregloCaracteres[i];
            switch (caracterActual) {
                //Comentarios
                case '\'': // ver si se puede manejar no como tokens es decir de otra forma
                    /*
                    if (!esFinalLinea(arregloCaracteres, i)) {
                        comentario = comentario.trim() + caracterActual;
                        caracterSiguiente = arregloCaracteres[++i];
                        while (i < arregloCaracteres.length) {
                            caracterSiguiente = arregloCaracteres[i];
                            comentario = comentario + caracterSiguiente;
                            ++i;
                        }
                        caracterSiguiente = ' ';
                        caracterActual = ' ';
                    }
                     */
                    while (!esFinalLinea(arregloCaracteres, i)) {

                        caracterActual = arregloCaracteres[i];
                        comentario = comentario + caracterActual;
                        //System.out.println("322 AnalisisLexico> Valor de i = " + i + " " + arregloCaracteres.length + " " + esFinalLinea(arregloCaracteres, i)+ "\n");//BORRAR
                        ++i;
                    }

                    agregarNuevoToken(comentario, TipoDeToken.COMENTARIO.toString(), comentario.trim(), numeroLinea);
                    comentario = " ";
                    --i;
                    break;

                //Agrupación
                case '(':
                    agregarNuevoToken(null, TipoDeToken.PARENTESIS_IZQUIERDO.toString(), null, numeroLinea);
                    break;
                case ')':
                    agregarNuevoToken(null, TipoDeToken.PARENTESIS_DERECHO.toString(), null, numeroLinea);
                    break;
                case '{':
                    agregarNuevoToken(null, TipoDeToken.LLAVE_IZQUIERDO.toString(), null, numeroLinea);
                    ;
                    break;
                case '}':
                    agregarNuevoToken(null, TipoDeToken.LLAVE_DERECHO.toString(), null, numeroLinea);
                    ;
                    break;

                //OPERADOR COMA    
                case ',':
                    agregarNuevoToken(null, TipoDeToken.COMA.toString(), null, numeroLinea);
                    break;

                //OPERADOR PUNTO
                case '.':
                    agregarNuevoToken("Punto", TipoDeToken.PUNTO.toString(), String.valueOf(caracterActual), numeroLinea);
                    break;

                //TERMINACION DE INSTRUCCION
                case ';':
                    agregarNuevoToken(null, TipoDeToken.PUNTO_Y_COMA.toString(), null, numeroLinea);
                    break;

                //OPERADORES ARITMETICOS    
                case '-':
                    agregarNuevoToken(null, TipoDeToken.RESTA.toString(), null, numeroLinea);
                    break;
                case '+':
                    agregarNuevoToken(null, TipoDeToken.SUMA.toString(), null, numeroLinea);
                    break;
                case '*':
                    agregarNuevoToken(null, TipoDeToken.MULTIPLICACION.toString(), null, numeroLinea);
                    break;
                case '/':
                    agregarNuevoToken(null, TipoDeToken.DIVISION.toString(), null, numeroLinea);
                    break;

                // OPERADORES DE COMPARACION
                case '=':
                    agregarNuevoToken("Asignacion", TipoDeToken.ASIGNACION.toString(), String.valueOf(caracterActual), numeroLinea);
                    break;

                case '<':

                    if (arregloCaracteres.length < i) {
                        caracterSiguiente = arregloCaracteres[i++];
                        switch (caracterSiguiente) {
                            case '>':
                                agregarNuevoToken(null, TipoDeToken.DIFERENTE.toString(), null, numeroLinea);
                                break;
                            case '=':
                                agregarNuevoToken(null, TipoDeToken.MENOR_O_IGUAL_QUE.toString(), null, numeroLinea);
                                break;
                            case ' ':
                                agregarNuevoToken(null, TipoDeToken.MENOR_QUE.toString(), null, numeroLinea);
                                break;
                            default:
                                break;
                        }
                    }
                    break;

                case '>':
                    if (arregloCaracteres.length < i) {
                        caracterSiguiente = arregloCaracteres[i++];
                        switch (caracterSiguiente) {
                            case '=':
                                agregarNuevoToken(null, TipoDeToken.MAYOR_O_IGUAL_QUE.toString(), null, numeroLinea);
                                break;
                            case ' ':
                                agregarNuevoToken(null, TipoDeToken.MAYOR_QUE.toString(), null, numeroLinea);
                                break;
                            default:
                                break;
                        }
                    }
                    break;

                case '"':

                    if (i < arregloCaracteres.length) {
                        //str = str.trim() + caracterActual;
                        //caracterSiguiente = arregloCaracteres[++i];
                        while (i < arregloCaracteres.length) {
                            caracterSiguiente = arregloCaracteres[i];
                            if (caracterSiguiente != '"' && caracterSiguiente != ')') {

                                str = str + caracterSiguiente;
                                ++i;
                            } else if (caracterSiguiente == '"') {
                                str = str + caracterSiguiente;
                                ++i;
                            } else if (caracterSiguiente == ')') {
                                //str = str.trim();
                                break;
                            }

                        }
                        caracterSiguiente = ' ';
                        caracterActual = ' ';
                    }
                    --i;
                    agregarNuevoToken("String", TipoDeToken.STRING.toString(), str.trim(), numeroLinea);

                    str = " ";
                    break;

                // SE IGNORAN LOS CARACTERES EN BLANCO
                case ' ':
                case '\r':
                case '\t':
                    break;

                default:

                    if (isDigit(caracterActual)) {
                        boolean decimal = false;
                        numero = numero.trim() + caracterActual;
                        System.out.println("5 ESTE ES NUMERO " + numero.trim() + " " + i);
                        caracterSiguiente = arregloCaracteres[++i];
                        if (i < arregloCaracteres.length) {
                            while (i < arregloCaracteres.length && caracterSiguiente != ' ') {
                                caracterSiguiente = arregloCaracteres[i];
                                if (isDigit(caracterSiguiente)) {
                                    numero = numero + caracterSiguiente;

                                } else if (caracterSiguiente == '.') {
                                    numero = numero + caracterSiguiente;
                                    decimal = true;

                                } else {
                                    numero = " ";
                                    break;
                                }

                                ++i;
                                System.out.println("5 ESTE ES NUMERO " + numero.trim() + " " + i);

                            }
                            if (decimal) {
                                agregarNuevoToken("Numero", TipoDeToken.NUMERO_REAL.toString(), numero.trim(), numeroLinea);
                            } else {
                                agregarNuevoToken("Numero", TipoDeToken.NUMERO_ENTERO.toString(), numero.trim(), numeroLinea);
                            }
                            caracterSiguiente = ' ';
                            caracterActual = ' ';
                        }
                        System.out.println("4 ESTE ES NUMERO " + numero.trim() + " " + i);
                        --i;
                        numero = " ";
                        break;
                    } else if (isAlpha(caracterActual)) {
                        //System.out.println("1 ESTE ES EL IDENTIFICADOR " + caracterActual);
                        //identificador += String.valueOf(caracterActual).trim();
                        //identificador = identificador.trim() + caracterActual;
                        //System.out.println("2 ESTE ES EL IDENTIFICADOR " + identificador);
                        /*
                        if (i < arregloCaracteres.length) {
                            caracterSiguiente = arregloCaracteres[++i];
                            //System.out.println("3 ESTE ES EL IDENTIFICADOR " + caracterSiguiente);
                            while (i < arregloCaracteres.length && caracterSiguiente != ' ') {

                                caracterSiguiente = arregloCaracteres[i];
                                if (isAlphaNumeric(caracterSiguiente)) {
                                    identificador += caracterSiguiente;
                                    ++i;
                                } else {
                                    agregarNuevoToken("Identificador", TipoDeToken.IDENTIFICADOR.toString(), identificador.trim(), numeroLinea);
                                    identificador = " ";
                                    break;
                                }

                            }
                            caracterSiguiente = ' ';
                            caracterActual = ' ';
                        }

                         */
                        while (!esFinalLinea(arregloCaracteres, i) && caracterActual != ' ') {

                            caracterActual = arregloCaracteres[i];
                            if (caracterActual == '.' || caracterActual == '(' || caracterActual == ')') {
                                break;
                            }

                            identificador = identificador + caracterActual;
                            //System.out.println("322 AnalisisLexico> Valor de i = " + i + " " + arregloCaracteres.length + " " + esFinalLinea(arregloCaracteres, i)+ "\n");//BORRAR
                            ++i;
                        }

                        if (palabrasReservadas.containsKey(identificador.trim().toLowerCase())) {
                            System.out.println("517 Analisis sintactico> Encontro palabra reservada");
                            agregarNuevoToken("Palabra Reservada", TipoDeToken.PALABRA_RESERVADA.toString(), palabrasReservadas.get(identificador.trim().toLowerCase()).trim(), numeroLinea);
                        } else {
                            System.out.println("520 Analisis sintactico> No Encontro palabra reservada");
                            agregarNuevoToken("Identificador", TipoDeToken.IDENTIFICADOR.toString(), identificador.trim(), numeroLinea);
                        }

                        //System.out.println("4 ESTE ES EL IDENTIFICADOR " + identificador.trim() + " " + i);
                        --i;
                        identificador = " ";
                        break;

                    } else {
                        agregarNuevoToken("Desconocido", TipoDeToken.DESCONOCIDO.toString(), String.valueOf(caracterActual), numeroLinea);
                        break;
                    }

            }

        }
    }

    public static void agregarNuevoToken(String nombreToken, String tipoDeToken, String valor, int numeroLinea) {
        Token nuevoToken = new Token(nombreToken, tipoDeToken, valor, numeroLinea);

        listaDeTokens.add(nuevoToken);
    }

    private static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    public void emitirMensajeDeError(int numeroDeError, int numeroDeLinea) {
        listaCodigoAnalizado.add(String.format("%14s", "Error ") + numeroDeError
                + ": " + errores.obtenerDescripcionDeError(numeroDeError)
                + String.format("[%s%d]", "Linea ", numeroDeLinea));
    }

    public void emitirMensajeDeAdvertencia(int numeroDeLinea) {
        listaCodigoAnalizado.add(String.format("%61s", "Advertencia: comando no es soportado por esta versión.")
                + String.format("[%s%d]", "Linea ", numeroDeLinea));
    }

    public void incluirLineaEnArchivoFinal(String instruccion, int numeroDeLinea) {
        String formatoLinea1 = String.format("%05d", numeroDeLinea);
        listaCodigoAnalizado.add(formatoLinea1 + " " + instruccion);
    }

    public List<String> getListaCodigoOriginal() {
        return AnalizadorLexico.listaCodigoOriginal;
    }

    public static Map<String, String> getPalabrasReservadas() {
        return palabrasReservadas;
    }

    public static Map<String, String> getTiposDeDatos() {
        return tiposDeDatos;
    }

    

    public List<List<Token>> getListaDeTokens() {
        return AnalizadorLexico.listaTokens;
    }

    public String getArchivoConCodigoFuente() {
        return AnalizadorLexico.archivoAdjunto;
    }

    public List<String> getListaCodigoRevisado() {
        return AnalizadorLexico.listaCodigoAnalizado;
    }

    public static boolean esFinalLinea(char[] arregloCaracteres, int contador) {
        return contador >= arregloCaracteres.length;
    }

}
