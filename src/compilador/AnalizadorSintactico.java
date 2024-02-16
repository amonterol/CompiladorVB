/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import java.io.BufferedWriter;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.nio.charset.Charset;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilitarios.Token;
import utilitarios.Error;

/**
 *
 * @author abmon
 */
public class AnalizadorSintactico {

    private AnalizadorLexico lexico;
    private List<List<Token>> listaTokens;

    private static List<String> listaCodigoOriginal;

    private static List<String> nuevaLista = new ArrayList<>();
    private String nombreArchivo;
    private List<String> listaCodigoAnalizado = new ArrayList<>();
    private Error errores = new Error();

    public AnalizadorSintactico(AnalizadorLexico lexico) {
        this.lexico = lexico;

    }

    public void leyendoLexico() throws IOException {
        lexico.analizarPrograma();
        listaTokens = lexico.getListaDeTokens();
        listaCodigoOriginal = lexico.getListaCodigoOriginal();
        nombreArchivo = lexico.getArchivoConCodigoFuente();

        //mprimirListaUsandoFor(listaDeTokens);
        String nombreArchivo = getArchivoConCodigoFuente();
        System.out.println("\n\n<<< 57 Sintactico> CANTIDAD DE TOKENS DESDE EL SINTACTICO>>> " + listaTokens.size() + " \n\n");
        listaTokens.forEach((item) -> {
            item.forEach((tkn) -> {
                System.out.println(tkn.getLexema() + " " + tkn.getTipoDeToken() + " " + tkn.getValor() + " " + tkn.getLinea());
            });
            System.out.println("\n\n");
        });

        if (!listaCodigoOriginal.isEmpty()) {
            System.out.println("\n\n<<< 63 Sintactico> CANTIDAD DE INSTRUCCIONES EN listaCodigoOriginal>>> " + listaCodigoOriginal.size() + " \n\n");
            listaCodigoOriginal.forEach((item) -> {
                System.out.println(item);
            });
        } else {
            System.out.println("\n\n<<< 66 Sintactico> listaCodigoOriginal no contiene elementos " + " \n\n");
        }

        analizarTokens();

    }

    public void analizarTokens() {
        int numeroDeLinea = 1;
        String instruccion = "";
        Token tokenActual = new Token();
        Token tokenSiguiente = new Token();
        Token tokenSiguienteSiguiente = new Token();

        int[] existeComandoImports = new int[2];
        int[] existeComandoModule = new int[2];

        boolean existeEstructura_Module_EndModule = false;

        boolean existeComandoProcesadoDiferenteModule = false;
        int numeroLineaDelComandoProcesado = 0;

        System.out.println("\n \n\n Sintactico 67 - BORRAR>>ESTAMOS EN EL SINTACTICO \n \n \n");

        if (!listaTokens.isEmpty()) {
            //Verifica existencia de comando Imports
            existeComandoImports = verificarPresenciaComandoImports(listaTokens);
            existeComandoModule = verificarPresenciaComandoModule(listaTokens);
            existeEstructura_Module_EndModule = verificarEstructura_Module_EndModule(listaTokens);

            //Verifica estructura ModulePrograma y el respectivo End Module
            if (!existeEstructura_Module_EndModule) {
                registraErrorEnArchivoFinal(500, 0);
                //System.out.println("\nSintactico 75> Existe estructura Module Program End Module \n \n ");
            }
            int i = 0;
            for (int j = 0; j < listaTokens.size(); j++) {
                System.out.println("\n\n153 Sintactico:> tamanio listaTokens " + listaTokens.size() + " valor de j " + j + "\n\n");
                if (i < listaCodigoOriginal.size()) {
                    instruccion = listaCodigoOriginal.get(i);
                    //System.out.println("156 Sintactico:> " + this.listaCodigoAnalizado.size());
                    System.out.println("\n Sintactico 157.- BORRAR> ESTA ES LA INSTRUCCION A AGREGAR A listaCodigoAnalizado "
                            + instruccion + " en la linea " + (i + 1));
                    agregarInstruccionEnListaCodigoAnalizado(instruccion, i + 1);
                    ++i;
                }
                if (instruccion.length() > 90) {
                    registraErrorEnArchivoFinal(122, numeroDeLinea);
                    //System.out.println("163 Sintactico:> " + "Instruccion de mas de  90 caracteres");
                }

                List<Token> listaDeTokens = listaTokens.get(j);
                System.out.println("\n\n167 Sintactico:> tamanio listaDeTokens " + listaDeTokens.size() + " valor de j " + j + "\n\n");
                if (!listaDeTokens.isEmpty()) {
                    System.out.println("\n\n<<<CANTIDAD DE TOKENS DE LISTADETOKENS>>> " + listaDeTokens.size() + "\n\n");
                    listaDeTokens.forEach((item) -> {

                        System.out.println(item.getLexema() + " " + item.getTipoDeToken() + " " + item.getValor() + " " + item.getLinea());

                    });
                } else {
                    System.out.println("210 Analizador Sintactivco> La lista de tokens esta vacia ");
                }
                tokenActual = listaDeTokens.get(0);
                System.out.println("\n\n160 Sintactico:> " + tokenActual.getValor() + " valor de j " + j + "\n\n");
                int numeroLinea = tokenActual.getLinea();
                if (!tokenActual.getTipoDeToken().equals("LINEA_EN_BLANCO")) {

                    switch (tokenActual.getValor()) {
                        case "Imports":

                            switch (listaDeTokens.size()) {
                                case 1:
                                    registraErrorEnArchivoFinal(133, tokenActual.getLinea());
                                    break;
                                case 2:
                                    tokenSiguiente = listaDeTokens.get(1);
                                    if (!AnalizadorLexico.getPalabrasReservadas().containsKey(tokenSiguiente.getValor().trim().toLowerCase())) {
                                        registraErrorEnArchivoFinal(133, tokenActual.getLinea());
                                    }
                                    //System.out.println("175 Sintactico revisar identificador valido." + tokenSiguiente.getValor());
                                    break;
                                case 3:
                                    boolean existeTtkn1_3 = AnalizadorLexico.getPalabrasReservadas().containsKey(listaDeTokens.get(1).getValor().trim().toLowerCase());
                                    boolean existeTtkn2_3 = listaDeTokens.get(2).getTipoDeToken().equals("PUNTO");
                                    if ((existeTtkn1_3 && existeTtkn2_3)) {
                                        registraErrorEnArchivoFinal(136, tokenActual.getLinea());
                                    } else {
                                        registraErrorEnArchivoFinal(134, tokenActual.getLinea());
                                    }
                                    break;
                                case 4:

                                    boolean existeTokenAuxiliar1_4 = AnalizadorLexico.getPalabrasReservadas().containsKey(listaDeTokens.get(1).getValor().trim().toLowerCase());
                                    boolean existeTokenAuxiliar2_4 = listaDeTokens.get(2).getTipoDeToken().equals("PUNTO");
                                    boolean existeTokenAuxiliar3_4 = AnalizadorLexico.getPalabrasReservadas().containsKey(listaDeTokens.get(3).getValor().trim().toLowerCase());

                                    if (!(existeTokenAuxiliar1_4 && existeTokenAuxiliar2_4 && existeTokenAuxiliar3_4)) {
                                        registraErrorEnArchivoFinal(135, tokenActual.getLinea());
                                    }
                                    break;
                                default:
                                    registraErrorEnArchivoFinal(134, tokenActual.getLinea());
                                    break;
                            }

                            if ((existeComandoModule[0] == 1) && (existeComandoModule[1] < tokenActual.getLinea())) {
                                registraErrorEnArchivoFinal(131, tokenActual.getLinea());
                                System.out.println("\nSintactico 172 - BORRAR>> Entramos al switch");
                            } else if (existeComandoProcesadoDiferenteModule) {
                                registraErrorEnArchivoFinal(132, tokenActual.getLinea());
                            }
                            System.out.println("\nSintactico 177 - BORRAR>> Analizando token " + tokenActual.getValor() + " en la linea " + tokenActual.getLinea());

                            break;
                        case "Module":

                            switch (listaDeTokens.size()) {
                                case 1:
                                    registraErrorEnArchivoFinal(141, tokenActual.getLinea());
                                    break;
                                case 2:
                                    tokenSiguiente = listaDeTokens.get(1);
                                    System.out.println("175 Sintactico revisar identificador valido." + tokenSiguiente.getValor());
                                    break;
                                default:
                                    registraErrorEnArchivoFinal(142, tokenActual.getLinea());
                                    break;
                            }
                            break;
                        case "Dim":
                            System.out.println("257 Sintactico ENTRAMOS A  DIM.");
                            if ((existeComandoImports[0] == 1) && (existeComandoImports[1] > tokenActual.getLinea())) {
                                System.out.println("206 Sintactico existe comando imports en DIM.");
                                registraErrorEnArchivoFinal(156, tokenActual.getLinea());
                            }
                            if ((existeComandoModule[0] == 1) && (existeComandoModule[1] > tokenActual.getLinea())) {
                                System.out.println("210 Sintactico existe comando Module en DIM." + (existeComandoModule[0] == 1) + (existeComandoModule[1] > tokenActual.getLinea()));
                                registraErrorEnArchivoFinal(155, tokenActual.getLinea());
                            }

                            switch (listaDeTokens.size()) {
                                case 1:
                                    registraErrorEnArchivoFinal(150, tokenActual.getLinea());
                                    break;
                                case 2:
                                    tokenSiguiente = listaDeTokens.get(1);
                                    if (!tokenSiguiente.getTipoDeToken().equals("IDENTIFICADOR")) {//para revisar identificador invalido
                                        registraErrorEnArchivoFinal(151, tokenActual.getLinea());
                                        registraErrorEnArchivoFinal(152, tokenActual.getLinea());
                                    } else {
                                        registraErrorEnArchivoFinal(152, tokenActual.getLinea());
                                    }
                                    break;
                                case 3:
                                    boolean existeTokenIdentificador_3 = listaDeTokens.get(1).getTipoDeToken().equals("IDENTIFICADOR");
                                    if (!existeTokenIdentificador_3) {
                                        registraErrorEnArchivoFinal(151, tokenActual.getLinea());
                                        registraErrorEnArchivoFinal(154, tokenActual.getLinea());

                                    }
                                    boolean existeTokenAs_3 = listaDeTokens.get(2)
                                            .getTipoDeToken()
                                            .equals("PALABRA_RESERVADA") && listaDeTokens.get(2).getValor().equals("As");
                                    if (!existeTokenAs_3) {
                                        registraErrorEnArchivoFinal(152, tokenActual.getLinea());
                                    } else {
                                        registraErrorEnArchivoFinal(154, tokenActual.getLinea());
                                    }
                                    break;
                                case 4:
                                    boolean existeTokenIdentificador_4 = listaDeTokens.get(1).getTipoDeToken().equals("IDENTIFICADOR");
                                    if (!existeTokenIdentificador_4) {
                                        registraErrorEnArchivoFinal(151, tokenActual.getLinea());
                                    }
                                    boolean existeTokenAs_4 = listaDeTokens.get(2)
                                            .getTipoDeToken()
                                            .equals("PALABRA_RESERVADA") && listaDeTokens.get(2).getValor().equals("As");
                                    if (!existeTokenAs_4) {
                                        registraErrorEnArchivoFinal(152, tokenActual.getLinea());
                                    }

                                    boolean existeTokenTipoDeDato_4 = listaDeTokens.get(3)
                                            .getTipoDeToken()
                                            .equals("PALABRA_RESERVADA") && AnalizadorLexico.getTiposDeDatos().containsKey(listaDeTokens.get(3)
                                            .getValor().trim().toLowerCase());

                                    if (!existeTokenTipoDeDato_4) {
                                        registraErrorEnArchivoFinal(153, tokenActual.getLinea());
                                    }
                                    break;

                                default:
                                    registraErrorEnArchivoFinal(154, tokenActual.getLinea());
                                    break;
                            }
                            break;

                        case "Sub":
                            System.out.println("257 Sintactico ENTRAMOS A  SUB.");
                            if ((existeComandoImports[0] == 1) && (existeComandoImports[1] > tokenActual.getLinea())) {
                                System.out.println("275 Sintactico existe comando imports en SUB.");
                                registraErrorEnArchivoFinal(162, tokenActual.getLinea());
                            }
                            if ((existeComandoModule[0] == 1) && (existeComandoModule[1] > tokenActual.getLinea())) {
                                System.out.println("279 Sintactico existe comando Module en SUB." + (existeComandoModule[0] == 1) + (existeComandoModule[1] > tokenActual.getLinea()));
                                registraErrorEnArchivoFinal(161, tokenActual.getLinea());
                            }

                            switch (listaDeTokens.size()) {
                                case 1:
                                    registraErrorEnArchivoFinal(160, tokenActual.getLinea());
                                    break;
                                case 2:
                                    tokenSiguiente = listaDeTokens.get(1);
                                    boolean existeTokenIdentificador_2 = tokenSiguiente.getTipoDeToken().equals("IDENTIFICADOR");
                                    boolean existeIdentificadorMain_2 = AnalizadorLexico.getTiposDeDatos().containsKey(tokenSiguiente);

                                    if (!existeTokenIdentificador_2 && !existeIdentificadorMain_2) {//para revisar identificador invalido
                                        registraErrorEnArchivoFinal(160, tokenActual.getLinea());
                                    } else if (existeIdentificadorMain_2) {
                                        registraErrorEnArchivoFinal(164, tokenActual.getLinea());
                                    } else {
                                        registraErrorEnArchivoFinal(164, tokenActual.getLinea());
                                    }
                                    break;
                                case 3:
                                    tokenSiguiente = listaDeTokens.get(1);
                                    Token tokenParentesisIzquierdo_3 = listaDeTokens.get(2);

                                    boolean existeTokenIdentificador_3 = tokenSiguiente.getTipoDeToken().equals("IDENTIFICADOR");
                                    boolean existeIdentificadorMain_3 = tokenSiguiente.getValor().equals("Main");
                                    boolean existeParentesisIzquierdo_3 = tokenParentesisIzquierdo_3.getTipoDeToken().equals("PARENTESIS_IZQUIERDO");

                                    if (!existeTokenIdentificador_3 && !existeIdentificadorMain_3) {//para revisar identificador invalido
                                        registraErrorEnArchivoFinal(160, tokenActual.getLinea());
                                    }
                                    if (existeIdentificadorMain_3 && !existeParentesisIzquierdo_3) {
                                        registraErrorEnArchivoFinal(165, tokenActual.getLinea());
                                    }
                                    if (existeParentesisIzquierdo_3) {
                                        registraErrorEnArchivoFinal(166, tokenActual.getLinea());
                                    }
                                    break;

                                case 4:
                                    Token tokenSiguientePosicion1_4 = listaDeTokens.get(1);
                                    Token tokenSiguientePosicionDos_4 = listaDeTokens.get(2);
                                    

                                    boolean existeTokenIdentificador_4 = tokenSiguientePosicion1_4.getTipoDeToken().equals("IDENTIFICADOR");
                                    System.out.println("321 Sintactico> existe Main " + existeTokenIdentificador_4);
                                    boolean existeIdentificadorMain_4 = tokenSiguientePosicion1_4.getValor().equals("Main");
                                    System.out.println("323 Sintactico> existe Main " + existeIdentificadorMain_4);
                                    boolean existeParentesisIzquierdo_4 = tokenSiguientePosicionDos_4.getTipoDeToken().equals("PARENTESIS_IZQUIERDO");
                                    boolean existeParentesisDerecho_4 = false;

                                    if (!existeTokenIdentificador_4 && !existeIdentificadorMain_4) {//para revisar identificador invalido
                                        registraErrorEnArchivoFinal(160, tokenActual.getLinea());
                                    }
                                    if (existeIdentificadorMain_4 && !existeParentesisIzquierdo_4) {
                                        registraErrorEnArchivoFinal(165, tokenActual.getLinea());
                                    }
                                    if (existeParentesisIzquierdo_4) {
                                        int l = 0;
                                        while (l < listaDeTokens.size()) {
                                            Token tkn = listaDeTokens.get(l);
                                            if (tkn.getTipoDeToken().equals("PARENTESIS_DERECHO")) {
                                                existeParentesisDerecho_4 = true;
                                                break;
                                            }
                                            ++l;

                                        }
                                    }
                                    if (!existeParentesisDerecho_4) {
                                        registraErrorEnArchivoFinal(166, tokenActual.getLinea());
                                    }

                                    break;

                                default:
                                    registraErrorEnArchivoFinal(154, tokenActual.getLinea());
                                    break;
                            }
                            break;

                        default:
                            System.out.println("\nSintactico 180 - BORRAR>> Entramos al switch");
                            break;
                    }
                }
            }

        } else {
            System.out.println("\nSintactico 72> Lista  tokens esta vacia");
        }

        try {
            crearArchivoErrores(listaCodigoAnalizado, nombreArchivo);
        } catch (IOException ex) {
            Logger.getLogger(AnalizadorSintactico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void imprimirListaUsandoFor(List<List<Token>> lista) {
        System.out.println("\n \n LISTA DE TOKENS DESDE EL SINTACTICO \n \n");

        /*
        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                System.out.println(lista.get(i).getLexema() + " " + lista.get(i).getTipoDeToken()
                        + " " + lista.get(i).getValor() + " " + lista.get(i).getLinea());
            }
        } else {
            System.out.println("Analizador Sintactivco> La lista de tokens esta vacia ");
        }
         */
        if (!lista.isEmpty()) {
            System.out.println("\n\n<<<CANTIDAD DE TOKENS>>> " + listaTokens.size());
            listaTokens.forEach((item) -> {
                item.forEach((tkn) -> {
                    System.out.println(tkn.getLexema() + " " + tkn.getTipoDeToken() + " " + tkn.getValor() + " " + tkn.getLinea());
                });
            });
        } else {
            System.out.println("210 Analizador Sintactivco> La lista de tokens esta vacia ");
        }

    }

    public String getArchivoConCodigoFuente() {
        String archivo = lexico.getArchivoConCodigoFuente();
        System.out.println("Analizador Sintactico 59> " + archivo);
        String[] myArray = archivo.split("\\.");

        System.out.println("Analizador Sintactico 62> " + myArray.length);
        return myArray[0];
    }

    public void crearArchivoErrores(List<String> lista, String nombre) throws FileNotFoundException, IOException {

        String directorio = "C:\\Users\\abmon\\Desktop\\";
        String archivo = nombre + "-errores.txt";

        String ruta = directorio + archivo;

        //List<String> list = Arrays.asList("This is the first line", "This is the second line");
        //Ruta a MSW logo en ingles
        //String ruta = "C:\\Program Files (x86)\\Softronics\\Microsoft Windows Logo\\cuadrado-Hugo-Errores.txt";
        //USAMO RUTA AL ESCRITORIO POR LOS ESCRITURA DEBIDO A PERMISOS DE ADMINISTRADOR
        //String ruta = "C:\\Users\\PC\\Desktop\\cuadrado-Hugo-Errores.txt";
        Path path = Paths.get(ruta);
        try (BufferedWriter br = Files.newBufferedWriter(path,
                Charset.defaultCharset(), StandardOpenOption.CREATE)) {
            for (String line : lista) {
                br.write(line);
                br.newLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imprimirArchivoSalida(List<String> lista) {

        lista.forEach((item) -> {
            System.out.println(item.length());
        });

    }

    /*
       public void imprimirArchivoListaErrores(List<MiError> lista) {
        Iterator iterator = lista.iterator();

        while (iterator.hasNext()) {
            MiError e = new MiError();
            e = (MiError) iterator.next();
            System.out.println("Error:" + "Linea: " + e.getLinea() + "Texto: " + e.getError());
        }
    }
     */
    public void registraErrorEnArchivoFinal(int numeroDeError, int numeroDeLinea) {
        // System.out.println("Analizador Sintactico 183> Descripcion del esrror es " + this.errores.obtenerDescripcionDeError(numeroDeError));
        listaCodigoAnalizado.add(String.format("%14s", "Error ") + numeroDeError
                + ": " + this.errores.obtenerDescripcionDeError(numeroDeError)
                + String.format("[%s%d]", "Linea ", numeroDeLinea));
    }

    public void agregarInstruccionEnListaCodigoAnalizado(String instruccion, int numeroDeLinea) {
        String formatoLinea1 = String.format("%05d", numeroDeLinea);
        this.listaCodigoAnalizado.add(formatoLinea1 + " " + instruccion);
        //System.out.println("181 Sintactico:> " + listaCodigoAnalizado.size());

    }

    /*
    public int verificarPresenciaComandoModule(List<Token> lista) {

        int numeroLineaComandoModule = 0;

        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getTipoDeToken().equals("PALABRA_RESERVADA") && lista.get(i).getValor().equals("Module")) {
                    numeroLineaComandoModule = lista.get(i).getLinea();
                }
            }
        }

        return numeroLineaComandoModule;

    }

     */
    public int[] verificarPresenciaComandoModule(List<List<Token>> lista) {
        int[] existeComandoModule = new int[2];

        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                List<Token> tokens = lista.get(i);

                Token token = tokens.get(0);

                if (token.getTipoDeToken().equals("PALABRA_RESERVADA") && token.getValor().equals("Module")) {
                    existeComandoModule[0] = 1;
                    existeComandoModule[1] = token.getLinea();
                    System.out.println("331 Analizador Sintactico > Encontramos un Module " + token.getValor());
                    break;
                }

            }
        }
        return existeComandoModule;
    }

    public boolean verificarPresenciaComandoEndModule(List<List<Token>> lista) {

        boolean existeComando_End_Module = false;
        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                List<Token> tokens = lista.get(i);
                for (int j = 0; j < tokens.size(); j++) {
                    if (tokens.get(j).getTipoDeToken().equals("PALABRA_RESERVADA") && tokens.get(j).getValor().equals("End")) {
                        System.out.println("Analizador Sintactico 362> Encontramos un END " + i + " " + tokens.size());

                        if ((j + 1) < tokens.size()) {

                            String str = tokens.get(j + 1).getValor();
                            if (str.equals("Module")) {

                                existeComando_End_Module = true;
                                System.out.println("Analizador Sintactico 369> Se encontro MODULE DESPUES DE END " + j + " " + lista.size());
                                break;
                            }
                        } else {
                            System.out.println("Analizador Sintactico 375> No se encontro MODULE DESPUES DE END " + j + " " + lista.size());
                            break;
                        }
                    }
                }

            }
        }
        return existeComando_End_Module;

    }

    public boolean verificarEstructura_Module_EndModule(List<List<Token>> lista) {
        int[] existeComandoModule = verificarPresenciaComandoModule(lista);
        System.out.println("409 Sintactico>" + ((existeComandoModule[0] == 1) && verificarPresenciaComandoEndModule(lista)));
        return (existeComandoModule[0] == 1) && verificarPresenciaComandoEndModule(lista);
    }

    public int[] verificarPresenciaComandoImports(List<List<Token>> lista) {
        int[] existeImports = new int[2];

        if (!lista.isEmpty()) {
            for (int i = 0; i < lista.size(); i++) {
                List<Token> tokens = lista.get(i);

                Token token = tokens.get(0);

                if (token.getTipoDeToken().equals("PALABRA_RESERVADA") && token.getValor().equals("Imports")) {
                    existeImports[0] = 1;
                    existeImports[1] = token.getLinea();
                    System.out.println("534 Analizador Sintactico > Encontramos un Imports " + token.getValor());
                    break;
                }

            }
        }
        return existeImports;
    }
}
