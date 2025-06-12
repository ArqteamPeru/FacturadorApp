package rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DefaultKieSessionUtils {

    private static final KieContainer kc = KieServices.Factory.get()
            .newKieClasspathContainer();

    public static KieSession getEnrichRulesSession() {
        return kc.newKieSession("enrich-session");
    }

    public static KieSession getSummaryRulesSession() {
        return kc.newKieSession("summary-session");
    }
}
