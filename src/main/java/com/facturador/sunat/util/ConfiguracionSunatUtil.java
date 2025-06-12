package com.facturador.sunat.util;

import com.facturador.database.ConfiguracionDAO;
import com.facturador.modelo.Configuracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionSunatUtil {

    private final ConfiguracionDAO configuracionDAO;

    @Autowired
    public ConfiguracionSunatUtil(ConfiguracionDAO configuracionDAO) {
        this.configuracionDAO = configuracionDAO;
    }

    public Configuracion obtenerConfiguracionActiva() {
        Configuracion config = configuracionDAO.obtener();

        if (config == null) {
            throw new RuntimeException("No se encontró configuración activa de SUNAT.");
        }

        return config;
    }
}
