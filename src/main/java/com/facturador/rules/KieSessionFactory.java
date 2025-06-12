package com.facturador.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class KieSessionFactory {

    private static final KieServices kieServices = KieServices.Factory.get();
    private static final KieContainer kieContainer = kieServices.getKieClasspathContainer();

    public static void applyEnrichRules(Object document) {
        KieSession kieSession = kieContainer.newKieSession("enrichRulesSession");
        if (kieSession == null) {
            throw new IllegalStateException("No se pudo crear la KieSession 'enrichRulesSession'. Verifica el nombre en kmodule.xml y que las reglas estén correctamente configuradas.");
        }
        try {
            kieSession.insert(document);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }

    public static void applySummaryRules(Object document) {
        KieSession kieSession = kieContainer.newKieSession("summaryRulesSession");
        if (kieSession == null) {
            throw new IllegalStateException("No se pudo crear la KieSession 'summaryRulesSession'. Verifica el nombre en kmodule.xml y que las reglas estén correctamente configuradas.");
        }
        try {
            kieSession.insert(document);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }

}
