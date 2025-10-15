// src/main/java/mil/teng254/legacy/filter/JAXBContentResolver.java
package mil.teng254.legacy.filter;

import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Set;

@Provider
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
public class JAXBContentResolver implements ContextResolver<JAXBContext> {
    private static final String CORRECT_CLASS_NAME="com.sun.xml.bind.v2.runtime.JAXBContextImpl";
    private static final String[] packagesArray = new String[]{"mil.teng254.legacy.dto"};
    private final JAXBContext context;
    private final Set<Class<?>> allClasses;

    @SneakyThrows
    public JAXBContentResolver() {
        allClasses = findXmlRootClasses();
        if (this.allClasses.isEmpty()) {
            throw new RuntimeException("No classes with @XmlRootElement found in packages: "
                    + String.join(", ", packagesArray));
        }
        if (log.isDebugEnabled()) {
            ArrayList<String> classesList = new ArrayList<>(allClasses.size());
            for (Class<?> clazz : allClasses) {
                classesList.add(clazz.getCanonicalName());
            }
            log.debug(".ctor allClasses:[\n-{}\n]", String.join("\n-", classesList));
        }
        Class<?>[] allClassesArray = allClasses.toArray(new Class<?>[allClasses.size()]);
        context = JAXBContext.newInstance(allClassesArray);
        String contextName = context.getClass().getName();
        log.debug(".ctor JAXBContext-implementation={}",contextName);

        if (CORRECT_CLASS_NAME.equals(contextName)) {
            log.debug(".ctor JAXBContext correct");
        } else {
            log.error(".ctor JAXBContext ERROR: {}, expected {}", contextName,CORRECT_CLASS_NAME);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> findXmlRootClasses() {
        AnnotationScannerListener scanner = new AnnotationScannerListener(XmlRootElement.class);
        PackageNamesScanner pns = new PackageNamesScanner(packagesArray);
        pns.scan(scanner);
        return scanner.getAnnotatedClasses();
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        return allClasses.contains(type) ? context : null;
    }
}
