package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public final class MTuring {

    private final String CUADRO_BLANCO = "□";
    private char[] ConjQ;
    private char[] ConjSigma;
    private char[] ConjCinta;
    private char[] ConjEstadosFinales;
    private String SimboloBlanco;
    private List<String> delta = new ArrayList<>();
    private String estado_inicial;
    private String textoFichero;
    private String[][] matriz_transiciones = null;
    private File ruta;
    private String estados;
    private StringBuilder cadena_modificada;
    private String impresor;

    public MTuring(File r) {
        try {
            leerTupla(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImpresor() {
        return this.impresor;
    }

    public StringBuilder getCadenaModificada() {
        return cadena_modificada;
    }

    private void leerTupla(File r) throws FileNotFoundException, IOException {
        ruta = new File(r.getAbsolutePath());
        FileReader leerFichero = new FileReader(ruta);
        try (BufferedReader bufferLectura = new BufferedReader(leerFichero)) {
            String cadenaElement;
            int linea = 0;
            while ((textoFichero = bufferLectura.readLine()) != null) {
                if (textoFichero.matches("#.*")) {
                } else if (textoFichero.matches("\b*")) {
                } else {
                    if (linea >= 2) { //
                        String[] separarEspaciosDelta = textoFichero.split(" ");
                        for (String espacio : separarEspaciosDelta) {
                            delta.add(espacio);
                        }
                    } else {
                        String[] separarEspacios = textoFichero.split(" ");
                        for (String espacio : separarEspacios) {
                            if (espacio.matches("#.*")) {
                                break;
                            }
                        }
                        if (linea == 0) {
                            for (int i = 0; i < separarEspacios.length; i++) {
                                cadenaElement = "";
                                cadenaElement = separarEspacios[i];
                                cadenaElement = ajustarCaracteres(cadenaElement, i);
                                switch (i) {
                                    case 0: //
                                        ConjQ = cadenaElement.toCharArray();
                                        matriz_transiciones = new String[ConjQ.length][ConjQ.length];
                                        estados = ConjQ.toString();
                                        break;
                                    case 1:
                                        estado_inicial = cadenaElement;
                                        break;
                                    case 2:
                                        ConjSigma = cadenaElement.toCharArray();
                                        break;
                                    case 3:
                                        ConjCinta = cadenaElement.toCharArray();
                                        break;
                                    case 4:
                                        ConjEstadosFinales = cadenaElement.toCharArray();
                                        break;
                                    case 5:
                                        SimboloBlanco = cadenaElement;
                                        break;
                                }
                            }
                        }

                    }
                    linea++;
                }

            }
        }
        asignarTransiciones();
    }

    private String ajustarCaracteres(String cadena, int indice) {
        cadena = cadena.replace("{", "");
        cadena = cadena.replace("}", "");
        cadena = cadena.replace(",", "");
        switch (indice) {
            case 0:
                cadena = cadena.replace("(", "");
                break;
            case 5:
                cadena = cadena.replace(")", "");
                break;
        }
        return cadena;
    }

    private boolean validarNumero(String cadena) {
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException e) {
            resultado = false;
        }
        return resultado;
    }

    private void asignarTransiciones() {
        int fila = 0, columna = 0;
        String f, c, transiciones_concatenadas = null;
        for (int i = 0; i < delta.size(); i++) {
            String cadena = delta.get(i);
            if (cadena.endsWith(":")) {
                transiciones_concatenadas = "";
                for (int j = 0; j < cadena.length() - 1; j++) {
                    if (cadena.substring(j, j + 1).equals(",")) {
                        f = cadena.substring(0, j);
                        c = cadena.substring(j + 1, cadena.length() - 1);
                        if ((validarNumero(f) == true) && (validarNumero(c) == true)) {
                            fila = Integer.parseInt(f);
                            columna = Integer.parseInt(c);
                            transiciones_concatenadas += cadena + " ";
                            break;
                        }
                    }
                }
            } else if (cadena.endsWith(";")) {
                transiciones_concatenadas += cadena + " ";
                matriz_transiciones[fila][columna] = transiciones_concatenadas;
            }
        }
    }

    private String establecerEspaciosEnBlanco() {
        String cuadros = "";
        for (int i = 0; i < 2; i++) {
            cuadros = cuadros.concat(CUADRO_BLANCO);
        }
        return cuadros;
    }

    private boolean verificarEstadoFinal(int estadoActual) {
        for (int i = 0; i < ConjEstadosFinales.length; i++) {
            if (estadoActual == Character.getNumericValue(ConjEstadosFinales[i])) {
                return true;
            }
        }
        return false;
    }

    private void generarImpresion(int iteracion, char caracter_lectura, int estadoActual, char caracter_modificado, String[] transiciones, int transicion) {
        impresor += "  |                   " + iteracion + "                 |                        " + caracter_lectura + "                        |                  " + estadoActual + "                   |                          " + caracter_modificado + "                             |                 " + transiciones[transicion] + "              |\n";
    }

    public boolean generarComputos(String cadena_entrada) {
        impresor = "";
        cadena_modificada = null;
        int estadoActual = Integer.parseInt(estado_inicial), estadoDirigido = 0, nTrans, iteracion = 0;
        String transicionesNoEjecutadas;
        boolean warning = false, exit;
        String espaciosBlanco = establecerEspaciosEnBlanco();
        cadena_modificada = new StringBuilder(espaciosBlanco + cadena_entrada + espaciosBlanco);
        int k = espaciosBlanco.length();
        impresor += "  |             Iteración           |          Caracter en lectura         |              Estado             |            Caracter modificado            |            Transiciones       |\n";
        if (cadena_entrada.isBlank()) {
            cadena_modificada.append(CUADRO_BLANCO);
        }
        while (true) {
            if (!warning) {
                char caracter_lectura = cadena_modificada.charAt(k);
                for (int i = estadoActual; i < matriz_transiciones.length; i++) {
                    exit = false;
                    transicionesNoEjecutadas = null;
                    nTrans = 0;
                    for (int j = estadoDirigido; j < matriz_transiciones.length; j++) {
                        if (matriz_transiciones[i][j] != null) {
                            String[] transiciones = matriz_transiciones[i][j].split(" ");
                            if (i == estadoActual) {
                                nTrans += transiciones.length - 1;
                                int transicion = 1;
                                while ((!exit) && (transicion < transiciones.length)) {
                                    String funcion = transiciones[transicion];
                                    if (funcion.lastIndexOf(SimboloBlanco) != -1) {
                                        funcion = funcion.replace(SimboloBlanco, CUADRO_BLANCO);
                                    }
                                    if (caracter_lectura == funcion.charAt(0)) {
                                        funcion = funcion.substring(2);
                                        cadena_modificada.setCharAt(k, funcion.charAt(0));
                                        if (Character.toString(funcion.charAt(2)).equals("R")) {
                                            k++;
                                        } else if (Character.toString(funcion.charAt(2)).equals("L")) {
                                            k--;
                                        }
                                        if ((k == 0) || (k == cadena_modificada.length() - 1)) {
                                            cadena_modificada.reverse();
                                            cadena_modificada.append(CUADRO_BLANCO);
                                            cadena_modificada.reverse();
                                            cadena_modificada.append(CUADRO_BLANCO);
                                            System.out.println(cadena_modificada);
                                            k++;
                                        }
                                        i = j;
                                        j = 0;
                                        estadoActual = i;
                                        estadoDirigido = j;
                                        exit = true;
                                        generarImpresion(++iteracion, caracter_lectura, estadoActual, funcion.charAt(0), transiciones, transicion);
                                    } else {
                                        transicionesNoEjecutadas += transiciones[transicion] + " ";
                                    }
                                    transicion++;
                                }
                            } else {
                                exit = true;
                            }
                        }
                        if (exit) {
                            break;
                        }
                    }
                    if (transicionesNoEjecutadas != null) {
                        String[] fallidas = transicionesNoEjecutadas.split(" ");
                        if (fallidas.length == nTrans) {
                            exit = true;
                            warning = true;
                        }
                    }
                    if (exit) {
                        break;
                    }
                }
            }
            if (warning) {
                return false;
            } else if (verificarEstadoFinal(estadoActual)) {
                return true;
            }

        }
    }
}
