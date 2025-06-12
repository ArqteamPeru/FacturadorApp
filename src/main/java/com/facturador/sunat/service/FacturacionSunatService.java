package com.facturador.sunat.service;

import com.facturador.modelo.Configuracion;
import com.facturador.sunat.dto.ComprobanteDTO;
import com.facturador.sunat.dto.RespuestaSunatDTO;
import com.facturador.sunat.util.*;

import io.github.project.openubl.xsender.company.CompanyCredentials;
import io.github.project.openubl.xsender.company.CompanyURLs;
import io.github.project.openubl.xsender.files.BillServiceXMLFileAnalyzer;
import io.github.project.openubl.xsender.files.ZipFile;
import io.github.project.openubl.xsender.models.SunatResponse;
import io.github.project.openubl.xsender.sunat.BillServiceDestination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
public class FacturacionSunatService {

    private final GeneradorXmlService generadorXmlService;
    private final FirmadorXmlService firmadorXmlService;
    private final ConfiguracionSunatUtil configuracionSunatUtil;
    private final EnvioSunatUtil envioSunatUtil;

    @Autowired
    public FacturacionSunatService(
            GeneradorXmlService generadorXmlService,
            FirmadorXmlService firmadorXmlService,
            ConfiguracionSunatUtil configuracionSunatUtil,
            EnvioSunatUtil envioSunatUtil
    ) {
        this.generadorXmlService = generadorXmlService;
        this.firmadorXmlService = firmadorXmlService;
        this.configuracionSunatUtil = configuracionSunatUtil;
        this.envioSunatUtil = envioSunatUtil;
    }

    public RespuestaSunatDTO procesar(ComprobanteDTO dto) {
        RespuestaSunatDTO respuesta = new RespuestaSunatDTO();

        try {
            Configuracion config = configuracionSunatUtil.obtenerConfiguracionActiva();

            // Generar XML desde DTO
            RespuestaSunatDTO resultadoXml = generadorXmlService.generarXml(dto);
            if (!resultadoXml.isExito()) return resultadoXml;

            String nombreArchivoXml = resultadoXml.getNombreArchivoXml();
            byte[] xmlBytes = Base64.getDecoder().decode(resultadoXml.getXmlBase64());
            String xmlString = new String(xmlBytes, StandardCharsets.UTF_8);

            // Firmar XML
            byte[] xmlFirmado = firmadorXmlService.firmarConConfiguracionActiva(xmlString);

            // Guardar temporalmente
            Path xmlPath = Files.createTempFile(nombreArchivoXml.replace(".xml", ""), ".xml");
            Files.write(xmlPath, xmlFirmado);

            // Preparar envío
            CompanyURLs urls = CompanyURLs.builder()
                    .invoice("https://e-beta.sunat.gob.pe/ol-ti-itcpfegem-beta/billService")
                    .build();

            BillServiceXMLFileAnalyzer analyzer = new BillServiceXMLFileAnalyzer(xmlPath, urls);
            ZipFile zipFile = analyzer.getZipFile();
            BillServiceDestination envio = analyzer.getSendFileDestination();
            BillServiceDestination consultaTicket = analyzer.getVerifyTicketDestination();

            CompanyCredentials credentials = CompanyCredentials.builder()
                    .username(config.getUsuarioSunat())
                    .password(config.getClaveSunat())
                    .build();

            // Enviar
            SunatResponse sunatResponse = envioSunatUtil.enviarConPosibleConsultaTicket(
                    zipFile, envio, consultaTicket, credentials
            );

            // Procesar respuesta
            String xmlCdr = CdrUtil.extraerXmlDesdeCdrZip(sunatResponse.getSunat().getCdr());
            String codRespuesta = CdrParserUtil.obtenerCodigoRespuesta(xmlCdr);
            String descRespuesta = CdrParserUtil.obtenerDescripcionRespuesta(xmlCdr);
            String interpretacion = SunatRespuestaTipo.interpretar(codRespuesta);

            respuesta.setExito(true);
            respuesta.setMensaje("SUNAT respondió correctamente.");
            respuesta.setCdrXml(xmlCdr);
            respuesta.setCodRespuesta(codRespuesta);
            respuesta.setDescripcionSunat(interpretacion + ": " + descRespuesta);

            // Guardar ZIP firmado en base64
            byte[] zipBytes = zipFile.getFile();
            String zipBase64 = Base64.getEncoder().encodeToString(zipBytes);
            respuesta.setZipFirmadoBase64(zipBase64);

            // Guardar CDR ZIP en base64
            byte[] cdrZipBytes = sunatResponse.getSunat().getCdr();
            String cdrBase64 = Base64.getEncoder().encodeToString(cdrZipBytes);
            respuesta.setZipCDRBase64(cdrBase64);

            // Limpiar archivo temporal
            Files.deleteIfExists(xmlPath);

        } catch (Exception e) {
            respuesta.setExito(false);
            respuesta.setMensaje("Error al enviar a SUNAT: " + e.getMessage());
            e.printStackTrace();
        }

        return respuesta;
    }
}
