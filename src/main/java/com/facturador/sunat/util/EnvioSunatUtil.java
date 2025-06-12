package com.facturador.sunat.util;

import com.facturador.modelo.Configuracion;
import io.github.project.openubl.xsender.Constants;
import io.github.project.openubl.xsender.camel.StandaloneCamel;
import io.github.project.openubl.xsender.camel.utils.CamelData;
import io.github.project.openubl.xsender.camel.utils.CamelUtils;
import io.github.project.openubl.xsender.company.CompanyCredentials;
import io.github.project.openubl.xsender.files.ZipFile;
import io.github.project.openubl.xsender.models.SunatResponse;
import io.github.project.openubl.xsender.sunat.BillServiceDestination;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
public class EnvioSunatUtil {

    public SunatResponse enviarConPosibleConsultaTicket(
            ZipFile zipFile,
            BillServiceDestination envioDestino,
            BillServiceDestination ticketDestino,
            CompanyCredentials credentials
    ) throws Exception {

        // 1. Acceder a CamelContext ya levantado por XSender
        CamelContext camelContext = StandaloneCamel.getInstance()
                .getMainCamel()
                .getCamelContext();

        ProducerTemplate producer = camelContext.createProducerTemplate();

        // 2. Preparar datos del envío principal
        CamelData camelData = CamelUtils.getBillServiceCamelData(zipFile, envioDestino, credentials);

        SunatResponse response = producer.requestBodyAndHeaders(
                Constants.XSENDER_BILL_SERVICE_URI,
                camelData.getBody(),
                camelData.getHeaders(),
                SunatResponse.class
        );

        // 3. Verificar si se requiere ticket
        if (response.getSunat() != null && response.getSunat().getTicket() != null) {
            String ticket = response.getSunat().getTicket();

            CamelData camelTicket = CamelUtils.getBillServiceCamelData(ticket, ticketDestino, credentials);

            SunatResponse ticketResponse = producer.requestBodyAndHeaders(
                    Constants.XSENDER_BILL_SERVICE_URI,
                    camelTicket.getBody(),
                    camelTicket.getHeaders(),
                    SunatResponse.class
            );

            return ticketResponse;
        }

        // 4. Retornar respuesta directa si no hay ticket
        return response;
    }
}
