package org.example;

import static org.example.ProcesadorArchivos.procesadorArchivos;

public class Main {
    public static void main(String[] args) {

        //Se llama al método procesadorArchivos con los nombres de los archivos necesarios para ejecutarlo
        procesadorArchivos("data.csv","template.txt");
    }

}