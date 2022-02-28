package dinotaurent.co.domain;

import dinotaurent.co.test.Test;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

/**
 *
 * @author dandazme
 */
public class FolderImpl implements IFolder {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Test.class);

    private static final String PATH_ENTRADA = "C:\\Users\\danie\\Desktop\\prueba";
    private static final String PATH_TEMP = "C:\\Users\\danie\\Desktop\\prueba\\temp\\";
    public int contadorEntrada = 0;
    public int contadorTemp = 0;
    public File[] listadoEntrada;
    public File[] listadoTemp;
    public ArrayList<String> nombresArchivosEntrada = new ArrayList<String>();
    public ArrayList<String> nombresArchivosTemp = new ArrayList<String>();
    public ArrayList<String> fechasArchivos = new ArrayList<String>();
    public ArrayList<String> dosificador = new ArrayList<String>();
    public boolean movido = false;
    File pathEntrada = new File(PATH_ENTRADA);
    File pathTemp = new File(PATH_TEMP);
    BasicFileAttributes attrs;

    FileFilter filtro = (File file) -> !file.isHidden() && file.getName().endsWith(".txt");

    @Override

    public void contar() {
        listadoEntrada = pathEntrada.listFiles(filtro);
        listadoTemp = pathTemp.listFiles(filtro);
        contadorEntrada = listadoEntrada.length;
        contadorTemp = listadoTemp.length;
//     System.out.println(contadorTemp);

        if (listadoEntrada != null || listadoEntrada.length != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            log.info("Se encontraron: " + contadorEntrada + " documentos en la ruta de entrada");
            for (File archivo : listadoEntrada) {
                try {
                    attrs = Files.readAttributes(archivo.toPath(), BasicFileAttributes.class);
                    FileTime time = attrs.creationTime();
                    nombresArchivosEntrada.add(archivo.getName());
                    fechasArchivos.add(sdf.format(new Date(time.toMillis())));

                } catch (IOException ex) {
                    log.error(ex);
                }
            }
        }

        if (listadoTemp != null || listadoTemp.length != 0) {
            for (File archivo : listadoTemp) {
                try {
                    attrs = Files.readAttributes(archivo.toPath(), BasicFileAttributes.class);
                    nombresArchivosTemp.add(archivo.getName());

//                    System.out.println(nombresArchivosTemp);
                } catch (IOException ex) {
                    log.error(ex);
                }
            }

            if (!nombresArchivosTemp.isEmpty()) {
                if (nombresArchivosTemp.size() < 5) {
                    for (int i = 0; dosificador.size() == nombresArchivosTemp.size(); i++) {
                        dosificador.add(nombresArchivosTemp.get(i));
                    }
                } else if (nombresArchivosTemp.size() >= 5) {
                    for (int i = 0; dosificador.size() < 5; i++) {
                        dosificador.add(nombresArchivosTemp.get(i));
                    }
                }
            }
        }
        mover();
    }

    public void mover() {

        if (contadorEntrada > 5 && contadorTemp == 0) {
            System.out.println("Entro en la secuencia: A");
            log.info("Se ha detectado bloqueo, procedera a mover los documentos y reiniciar los servicios");

            for (var archivo : nombresArchivosEntrada) {
                Path documento = Path.of(PATH_ENTRADA + "\\" + archivo);
                Path destino = Path.of(PATH_TEMP + archivo);
//                System.out.println(documento);
                try {
                    Path mover = Files.move(documento, destino);
                    log.info("Se movio el archivo " + documento + " a la ruta: " + PATH_TEMP);
                } catch (Exception ex) {
                    log.error(ex);
                }
            }
            try {
                Thread.sleep(3000);
                String[] cmd = {"sc.exe stop ServicioTestJavaX", "sc.exe config \"ServicioTestJavaX\" obj= \".\\usuario2\" password= \",,41qw96\"", "sc.exe start ServicioTestJavaX"};

                for (var i : cmd) {
                    Runtime.getRuntime().exec(i);
                    Thread.sleep(800);
                }

                log.info("Se han reiniciado los servicios correctamente!!");
            } catch (IOException | InterruptedException ex) {
                log.error(ex);
            }
            contar();
        } else if (contadorEntrada == 0 && contadorTemp > 0) {
            System.out.println("Entro en la secuencia: B");
            log.info("Se procedera a dosificar los documentos a la ruta de entrada");

//            System.out.println(dosificador.size());
//            System.out.println(dosificador);
            for (var archivo : dosificador) {
                Path documento = Path.of(PATH_TEMP + archivo);
                Path destino = Path.of(PATH_ENTRADA + "\\" + archivo);

                try {
                    Path mover = Files.move(documento, destino);
                    log.info("Se movio el archivo " + documento + " a la ruta: " + PATH_ENTRADA);
                    movido = true;
                } catch (Exception ex) {
                    log.error(ex);
                }
            }
            dosificador.clear();
            nombresArchivosTemp.clear();
            contar();

        } else if (contadorEntrada == 0 && contadorTemp == 0 || contadorEntrada == 5 && contadorTemp == 0) {
            System.out.println("Entro en la secuencia: C");
            log.info("No se detectaron bloqueos ni documentos pendientes, se volvera a ejecutar dentro de 10 segundos");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
            contar();
        } else if (contadorEntrada <= 5 && contadorTemp > 0) {
            System.out.println("Entre en la secuencia: E");
            log.info("Aun no se han evacuado los documentos dosificados, se volvera a ejecutar dentro de 5 segundos");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
            contar();
        }

    }

}
