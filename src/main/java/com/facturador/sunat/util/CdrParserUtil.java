package com.facturador.sunat.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class CdrParserUtil {

    public static String obtenerCodigoRespuesta(String xmlCdr) throws Exception {
        Document document = parse(xmlCdr);
        return document.getElementsByTagName("cbc:ResponseCode")
                .item(0)
                .getTextContent()
                .trim();
    }

    public static String obtenerDescripcionRespuesta(String xmlCdr) throws Exception {
        Document document = parse(xmlCdr);
        return document.getElementsByTagName("cbc:Description")
                .item(0)
                .getTextContent()
                .trim();
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }
}
