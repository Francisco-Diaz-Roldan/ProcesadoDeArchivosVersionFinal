package org.example;

import static org.example.ProcesadorArchivos.procesadorArchivos;
/**
 * Esta clase que contiene el método principal "main" que comienza la ejecución del programa.
 */
public class Main {

    /***
     * Se trata del método principal que da comienzo al programa de procesado de archivos.
     *
     * @param args           Hace referencia a los argumentos de la línea de comandos, no se utilizan en esta ocasión.
     */

    public static void main(String[] args) {
        //Se llama al método procesadorArchivos con los nombres de los archivos necesarios para ejecutarlo
        procesadorArchivos("data.csv","template.txt");
    }

}