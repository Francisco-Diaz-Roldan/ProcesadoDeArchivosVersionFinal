package org.example;

import java.io.*;
import java.util.ArrayList;

public class ProcesadorArchivos {

    public static void procesadorArchivos(String archivoCsv, String archivoPlantilla) {
        File archivo = new File(archivoCsv);

        boolean carpetaCreada = false;

        try (BufferedReader brArchivo = new BufferedReader(new FileReader(archivo))) {
            String lecturaArchivo;
            int linea = 0; // Variable para el número de línea
            while ((lecturaArchivo = brArchivo.readLine()) != null) {
                linea++; // Incrementa el número de la línea

                boolean todosDatos = true; // Variable para controlar si todos los datos están completos
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

                    if (elementosFaltantes.isEmpty()) {
                        ArrayList<String> plantillas = new ArrayList<>();
                        try (BufferedReader brPlantilla = new BufferedReader(new FileReader(archivoPlantilla))) {
                            String lecturaPlantilla;
                            while ((lecturaPlantilla = brPlantilla.readLine()) != null) {
                                lecturaPlantilla = lecturaPlantilla.replace("%%2%%", ciudad);
                                lecturaPlantilla = lecturaPlantilla.replace("%%3%%", email);
                                lecturaPlantilla = lecturaPlantilla.replace("%%4%%", empresa);
                                lecturaPlantilla = lecturaPlantilla.replace("%%5%%", empleado);

                                plantillas.add(lecturaPlantilla + "\n");
                                if (lecturaPlantilla.contains("%%")) {
                                    todosDatos = false; // Si hay cosas sin reemplazar, establece datosCompletos a false
                                    break; // Sale del bucle si falta algo
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error al leer el archivo de la plantilla " + e);
                        }

                        if (todosDatos && !carpetaCreada) {
                            File salida = new File("salida");
                            salida.mkdir();
                            System.out.println("Se ha creado correctamente la carpeta salida:");
                            carpetaCreada = true; // Marca que la carpeta se ha creado
                        }

                        if (todosDatos) {
                            try (BufferedWriter correoBienvenida = new BufferedWriter(new FileWriter("salida/correoBienvenida-" + id + ".txt"))) {
                                for (String nuevaPlantilla : plantillas) {
                                    correoBienvenida.write(nuevaPlantilla);
                                }
                                System.out.println("Se ha creado correctamente el correoBienvenida-" + id + ".txt");
                            } catch (IOException e) {
                                throw new RuntimeException("Error al escribir el archivo de salida " + e);
                            }
                        }
                    } else {
                        String mensajeError = "Error: Faltan datos en la línea " + linea + ", falta añadir: ";
                        mensajeError += String.join(", ", elementosFaltantes);
                        System.err.println(mensajeError + " en el archivo data.csv");
                    }
                } else {
                    System.err.println("Error: Faltan datos en la línea " + linea + " en el archivo data.csv");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de obtención de datos " + e);
        }

        // Ahora, iteramos a través de los archivos correoBienvenida-*.txt y los imprimimos
        File salida = new File("salida");
        if (salida.exists() && salida.isDirectory()) {
            File[] archivosCorreoBienvenida = salida.listFiles((dir, name) -> name.startsWith("correoBienvenida-") && name.endsWith(".txt"));
            for (File archivoCorreo : archivosCorreoBienvenida) {
                //System.out.println("Contenido de " + archivoCorreo.getName() + ":");
                try (BufferedReader brCorreo = new BufferedReader(new FileReader(archivoCorreo))) {
                    String linea;
                    while ((linea = brCorreo.readLine()) != null) {
                        //System.out.println(linea);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error al leer el archivo de salida " + e);
                }
            }
        }
    }
}