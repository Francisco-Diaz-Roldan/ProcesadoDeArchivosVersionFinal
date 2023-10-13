package org.example;

import static org.example.ProcesadorArchivos.procesadorArchicos;

public class Main {
    public static void main(String[] args) {
        procesadorArchicos("data.csv", "template.txt");
    }
}