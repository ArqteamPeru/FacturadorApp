package com.facturador.util;

import java.io.IOException;
import java.nio.file.*;

public class ArchivoSunatUtil {

    private static final Path RUTA_BASE = Paths.get("sunat/envios");

    // Guarda archivo en la ruta base por defecto: sunat/envios
    public static void guardarArchivo(byte[] contenido, String nombreArchivo) throws IOException {
        Files.createDirectories(RUTA_BASE);
        Path rutaArchivo = RUTA_BASE.resolve(nombreArchivo);
        Files.write(rutaArchivo, contenido, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("✅ Archivo guardado: " + rutaArchivo.toAbsolutePath());
    }

    // 🔄 NUEVO: Guarda archivo en una subcarpeta específica, como por fecha
    public static void guardarArchivoEnRuta(byte[] contenido, String rutaCarpeta, String nombreArchivo) throws IOException {
        Path carpeta = Paths.get(rutaCarpeta);
        Files.createDirectories(carpeta);
        Path archivoFinal = carpeta.resolve(nombreArchivo);
        Files.write(archivoFinal, contenido, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("✅ Archivo guardado: " + archivoFinal.toAbsolutePath());
    }

    // Utilidad para construir ruta completa
    public static Path obtenerRuta(String nombreArchivo) {
        return RUTA_BASE.resolve(nombreArchivo);
    }
}
