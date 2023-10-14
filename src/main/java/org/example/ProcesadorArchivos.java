package org.example;

import java.io.*;
import java.util.ArrayList;

/**
 * Esta clase contiene varios métodos con los que es posible crear una carpeta "salida" que crea documentos
 * "correoBienvenida.txt" a partir de unos datos sacdos de "data.csv" y empleando una plantilla "template.txt".
 * Además, comprueba que no falten archivos o datos en los mismos.
 */

public class ProcesadorArchivos {

    /***
     *Permite procesar los archivos "data.csv" y "template.txt" para generar los nuevos archivos de texto
     * "correoBienvenida.txt" que aparecerán en la carpeta "salida".
     *
     * @param archivoCsv        Se trata de la ruta al archivo de entrada "data.csv".
     * @param archivoPlantilla  Se trata de la ruta al archivo de plantilla "template.txt".
     */
    public static void procesadorArchivos(String archivoCsv, String archivoPlantilla) {
        File archivo = new File(archivoCsv);
        File plantilla = new File(archivoPlantilla);

        // Compruebo que existe el archivo data.csv
        if (!archivo.exists()) {
            System.err.println("Error: archivo data.csv no encontrado.");
            return; // Manejo el error y salgo del método en caso de que no exista
        }

        // Compruebo que existe el archivo  template.txt
        if (!plantilla.exists()) {
            System.err.println("Error: archivo template.txt no encontrado.");
            return; // Manejo el error y salgo del método en caso de que no exista
        }

        boolean carpetaCreada = false;
        crearCarpetaSalida(); // Llamo a la función que crea la carpeta salida

        try (BufferedReader brArchivo = new BufferedReader(new FileReader(archivo))) {
            String lecturaArchivo;
            int linea = 0; // Creo una variable para calcular después el número de la línea del archivo
            while ((lecturaArchivo = brArchivo.readLine()) != null) {
                linea++;
                procesarLinea(lecturaArchivo, linea, archivoPlantilla, carpetaCreada);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de obtención de datos " + e);
        }

        //LLamo a la función que crea los correos de salida con los datos ya cambiados
        //imprimirArchivosCorreoBienvenida();
    }

    private static void procesarLinea(String linea, int numeroLinea, String archivoPlantilla, boolean carpetaCreada) {
        boolean todosDatos = true; // Creo una variable para compribar que están todos los datos
        String[] datosArchivo = linea.split(",");
        // Creo un ArrayList en el que voy añadiendo los datos que faltan en el data.csv
        ArrayList<String> datosFaltantes = new ArrayList<>();

        //Voy asignanco valores a los datos que han sido separados mediante comas
        if (datosArchivo.length >= 5) {
            String id = datosArchivo[0];
            String empresa = datosArchivo[1];
            String ciudad = datosArchivo[2];
            String email = datosArchivo[3];
            String empleado = datosArchivo[4];

            if (id.isEmpty()) datosFaltantes.add("id");
            if (empresa.isEmpty()) datosFaltantes.add("empresa");
            if (ciudad.isEmpty()) datosFaltantes.add("ciudad");
            if (email.isEmpty()) datosFaltantes.add("email");
            if (empleado.isEmpty()) datosFaltantes.add("empleado");

            if (datosFaltantes.isEmpty()) {
                ArrayList<String> plantillas = cargarPlantillas(archivoPlantilla, ciudad, email, empresa, empleado,
                        todosDatos);

                if (todosDatos) {
                    escribirCorreoBienvenida(id, plantillas);
                } else {
                    imprimirErrorDatosFaltantes(numeroLinea, datosFaltantes);
                }
            } else {
                imprimirErrorDatosFaltantes(numeroLinea, datosFaltantes);
            }
        } else {
            imprimirErrorDatosFaltantes(numeroLinea, null);
        }
    }

    private static ArrayList<String> cargarPlantillas(String archivoPlantilla, String ciudad, String email,
                                                      String empresa, String empleado, boolean todosDatos) {
        ArrayList<String> plantillas = new ArrayList<>();
        try (BufferedReader brPlantilla = new BufferedReader(new FileReader(archivoPlantilla))) {
            String lecturaPlantilla;
            while ((lecturaPlantilla = brPlantilla.readLine()) != null) {
                //Sustituimos los %%n%% por los valores almacenados anteriormente
                lecturaPlantilla = lecturaPlantilla.replace("%%2%%", ciudad);
                lecturaPlantilla = lecturaPlantilla.replace("%%3%%", email);
                lecturaPlantilla = lecturaPlantilla.replace("%%4%%", empresa);
                lecturaPlantilla = lecturaPlantilla.replace("%%5%%", empleado);
                plantillas.add(lecturaPlantilla + "\n");

                if (lecturaPlantilla.contains("%%")) {
                    todosDatos = false; // Si quedan cosas sin reemplazar en la plantilla, establece todosDatos a false
                    break; // El pograma ale del bucle si falta algo
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de la plantilla " + e);
        }
        return plantillas;
    }

    private static void crearCarpetaSalida() {//Creo la carpeta salida en caso de que no exista
        File salida = new File("salida");
        salida.mkdir();
        System.out.println("Se ha creado correctamente la carpeta salida:");
    }

    //Creo los archivos con la información sustituida de las plantillas en la carpeta salida y lo imprimo por pantalla
    private static void escribirCorreoBienvenida(String id, ArrayList<String> plantillas) {
        try (BufferedWriter correoBienvenida = new BufferedWriter(new FileWriter("salida/correoBienvenida-"
                + id + ".txt"))) {
            for (String nuevaPlantilla : plantillas) {
                correoBienvenida.write(nuevaPlantilla);
            }
            System.out.println("Se ha creado correctamente el correoBienvenida-" + id + ".txt");
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo de salida " + e);
        }
    }

    //Para control de errores
    //Imprimo por pantalla un mensaje de error indicando los datos que faltan en data.csv  y la línea en la que faltan
    private static void imprimirErrorDatosFaltantes(int numeroLinea, ArrayList<String> datosFaltantes) {
        String mensajeError = "Error: Faltan datos en la línea " + numeroLinea;
        if (datosFaltantes != null) {
            mensajeError += ", falta: " + String.join(", ", datosFaltantes);
        }
        System.err.println(mensajeError + " en el archivo data.csv");
    }

    //Imprimo el contenido de las plantillas con la información sustituida por consola
    /*private static void imprimirArchivosCorreoBienvenida() {
        File salida = new File("salida");
        if (salida.exists() && salida.isDirectory()) {
            File[] archivosCorreoBienvenida = salida.listFiles((dir, name) -> name.startsWith("correoBienvenida-")
                    && name.endsWith(".txt"));
            /*for (File archivoCorreo : archivosCorreoBienvenida) {
                 try (BufferedReader brCorreo = new BufferedReader(new FileReader(archivoCorreo))) {
                   String linea;
                    while ((linea = brCorreo.readLine()) != null) {
                        System.out.println(linea);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error al leer el archivo de salida " + e);
                }
            }
        }
    }*/
}
