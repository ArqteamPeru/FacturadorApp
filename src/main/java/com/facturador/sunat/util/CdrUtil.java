package com.facturador.sunat.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CdrUtil {

    /**
     * Extrae el contenido XML del CDR a partir del ZIP recibido desde SUNAT.
     *
     * @param zipBytes El contenido del ZIP (SunatResponse.getCdr())
     * @return El contenido del XML dentro del ZIP como String
     * @throws Exception si hay error al descomprimir o leer el contenido
     */
    public static String extraerXmlDesdeCdrZip(byte[] zipBytes) throws Exception {
        try (InputStream is = new ByteArrayInputStream(zipBytes);
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".xml")) {
                    // Leer contenido XML del ZIP
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = zis.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, len));
                    }

                    return sb.toString(); // Retorna el XML como String
                }
            }
        }

        throw new RuntimeException("No se encontró archivo XML en el CDR ZIP");
    }
}
