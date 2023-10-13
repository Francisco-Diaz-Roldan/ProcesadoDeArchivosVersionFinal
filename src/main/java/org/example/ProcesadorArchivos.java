package org.example;

import java.io.*;
import java.util.ArrayList;

public class ProcesadorArchivos {

    public static void procesadorArchicos(String archivoCsv, String archivoPlantilla) {
        File archivo = new File(archivoCsv);

        try (BufferedReader brArchivo = new BufferedReader(new FileReader(archivo))) {
            String lecturaArchivo;
            int numeroLinea = 0; // Variable para el número de línea
            while ((lecturaArchivo = brArchivo.readLine()) != null) {
                numeroLinea++; // Incrementa el número de la línea

                boolean datosCompletos = true; // Variable para controlar si todos los datos están completos
                String[] datosArchivo = lecturaArchivo.split(",");
                ArrayList<String> elementosFaltantes = new ArrayList<>();

                if (datosArchivo.length >= 5) {
                    String id = datosArchivo[0];
                    String empresa = datosArchivo[1];
                    String ciudad = datosArchivo[2];
                    String email = datosArchivo[3];
                    String empleado = datosArchivo[4];

                    if (id.isEmpty()) elementosFaltantes.add("id");
                    if (empresa.isEmpty()) elementosFaltantes.add("empresa");
                    if (ciudad.isEmpty()) elementosFaltantes.add("ciudad");
                    if (email.isEmpty()) elementosFaltantes.add("email");
                    if (empleado.isEmpty()) elementosFaltantes.add("empleado");

                    if (!elementosFaltantes.isEmpty()) {
                        datosCompletos = false;
                    }

                    ArrayList<String> plantillas = new ArrayList<>();
                    try (BufferedReader brPlantilla = new BufferedReader(new FileReader(archivoPlantilla))) {
                        String lecturaPlantilla;
                        while ((lecturaPlantilla = brPlantilla.readLine()) != null) {
                            lecturaPlantilla = lecturaPlantilla.replace("%%2%%", ciudad);
                            lecturaPlantilla = lecturaPlantilla.replace("%%3%%", email);
                            lecturaPlantilla = lecturaPlantilla.replace("%%4%%", empresa);
                            lecturaPlantilla = lecturaPlantilla.replace("%%5%%", empleado);

                            plantillas.add(lecturaPlantilla + "\n");
                            System.out.println(lecturaPlantilla);

                            if (lecturaPlantilla.contains("%%")) {
                                datosCompletos = false; // Si hay cosas sin reemplazar, establece datosCompletos a false
                                break; // Sale del bucle si falta algo
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error al leer el archivo de la plantilla " + e);
                    }

                    if (datosCompletos) {
                        File salida = new File("salida");
                        salida.mkdir();

                        try (BufferedWriter correoBienvenida = new BufferedWriter(new FileWriter("salida/correoBienvenida-" + id + ".txt"))) {
                            for (String nuevaPlantilla : plantillas) {
                                correoBienvenida.write(nuevaPlantilla);
                                correoBienvenida.flush();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error al escribir el archivo de salida " + e);
                        }
                        System.out.println("\nCreada la carpeta 'salida' y el archivo 'correoBienvenida-" + id + ".txt'");
                    } else {
                        String mensajeError = "Error: Faltan datos en la línea " + numeroLinea + ", falta ";
                        mensajeError += String.join(", ", elementosFaltantes);
                        System.err.println(mensajeError + " del archivo data.csv");
                    }
                } else {
                    System.err.println("Error: Faltan datos en la línea " + numeroLinea + " del archivo data.csv");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de obtención de datos " + e);
        }
    }
}
