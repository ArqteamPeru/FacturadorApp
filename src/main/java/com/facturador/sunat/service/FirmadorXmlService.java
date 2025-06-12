package com.facturador.sunat.service;

import com.facturador.modelo.Configuracion;
import com.facturador.sunat.util.ConfiguracionSunatUtil;

import io.github.project.openubl.xbuilder.signature.XMLSigner;
import io.github.project.openubl.xbuilder.signature.CertificateDetails;
import io.github.project.openubl.xbuilder.signature.CertificateDetailsFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Service
public class FirmadorXmlService {

    private final ConfiguracionSunatUtil configuracionSunatUtil;

    @Autowired
    public FirmadorXmlService(ConfiguracionSunatUtil configuracionSunatUtil) {
        this.configuracionSunatUtil = configuracionSunatUtil;
    }

    public byte[] firmarConConfiguracionActiva(String xmlString) {
        try {
            // 1. Leer configuración desde servicio inyectado
            Configuracion config = configuracionSunatUtil.obtenerConfiguracionActiva();
            File archivoCert = new File(config.getCertificadoPath());

            // 2. Cargar certificado
            FileInputStream inputStream = new FileInputStream(archivoCert);
            CertificateDetails certDetails = CertificateDetailsFactory.create(inputStream, config.getClaveCertificado());
            X509Certificate cert = certDetails.getX509Certificate();
            PrivateKey key = certDetails.getPrivateKey();

            // 3. Firmar XML
            Document firmado = XMLSigner.signXML(xmlString, "SignID", cert, key);

            // 4. Convertir Document a byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(firmado), new StreamResult(outputStream));

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al firmar el XML: " + e.getMessage(), e);
        }
    }
}
