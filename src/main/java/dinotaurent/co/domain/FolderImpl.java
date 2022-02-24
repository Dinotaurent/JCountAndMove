package dinotaurent.co.domain;

import dinotaurent.co.test.Test;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.*;
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

        if (listadoEntrada == null || listadoEntrada.length == 0) {
            log.info("No hay documentos dentro de la ruta de entrada");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            log.info("Se encontraron: " + contadorEntrada + " documentos en la ruta");
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
        }
    }

    public void mover() {

        if (contadorEntrada > 5 && contadorTemp == 0) {
            log.info("Se ha detectado bloqueo, procedera a mover los documentos y reiniciar los servicios");

            for (var archivo : nombresArchivosEntrada) {
                Path documento = Path.of(PATH_ENTRADA + "\\" + archivo);
                Path destino = Path.of(PATH_TEMP + archivo);
//                System.out.println(documento);
                try {
                    Path mover = Files.move(documento, destino);
                    log.info("Se movio el archivo " + documento +" a la ruta: " + PATH_TEMP);
                } catch (Exception ex) {
                    log.error(ex);
                }
            }
        }

    }

}
