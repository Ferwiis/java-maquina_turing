package modelo;

import vista.*;
import controlador.*;

public class Main {

    public static void main(String[] args) {
        VTuring vista = new VTuring();
        VFichero lectura = new VFichero();
        Controlador_Turing control = new Controlador_Turing(vista, lectura);
        control.iniciarVentanaPrincipal();
    }
}
