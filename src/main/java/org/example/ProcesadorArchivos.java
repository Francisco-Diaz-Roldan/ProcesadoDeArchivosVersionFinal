package org.example;

import java.io.*;
import java.util.ArrayList;

public class ProcesadorArchivos {

    public static void procesadorArchicos(String archivoCsv, String archivoPlantilla) {
        File archivo = new File(archivoCsv);

        try (BufferedReader brArchivo = new BufferedReader(new FileReader(archivo))) {
            String lecturaArchivo;
            while ((lecturaArchivo = brArchivo.readLine()) != null) {
                boolean datosCompletos = true; // Variable para controlar si todos los datos están completos

                String[] datosArchivo = lecturaArchivo.split(",");
                if (datosArchivo.length >= 5) {
                    ArrayList<String> plantillas = new ArrayList<>();
                    String id = datosArchivo[0];
                    String empresa = datosArchivo[1];
                    String ciudad = datosArchivo[2];
                    String email = datosArchivo[3];
                    String empleado = datosArchivo[4];

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
                                datosCompletos = false; // Si hay marcadores sin reemplazar, establece datosCompletos a false
                                break; // Sale del bucle si falta algún valor
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

                        System.out.println("Creada la carpeta 'salida' y el archivo 'correoBienvenida-" + id + ".txt'");
                    } else {
                        System.err.println("Error: Faltan datos en la línea: " + lecturaArchivo + " del archivo data.csv");
                    }
                } else {
                    System.err.println("Error: Faltan datos en la línea: " + lecturaArchivo + " del archivo data.csv");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de obtención de datos " + e);
        }
    }
}
