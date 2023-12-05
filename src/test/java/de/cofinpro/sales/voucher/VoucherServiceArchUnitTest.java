package de.cofinpro.sales.voucher;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class VoucherServiceArchUnitTest {

    private static final String MAIN_PACKAGE = "de.cofinpro.sales.voucher";

    private static final String SERVICE_LAYER = "Service";
    private static final String SERVICE_PACKAGE = "..service..";

    private static final String CONTROLLER_LAYER = "Controller";
    private static final String CONTROLLER_PACKAGE = "..controller..";

    private static final String MODEL_PACKAGE = "..model..";

    private static final String PERSISTENCE_LAYER = "Persistence";
    private static final String PERSISTENCE_PACKAGE = "..persistence..";

    private static final String SECURITY_LAYER = "Security";
    private static final String SECURITY_PACKAGE = "..security..";

    private static final String DOMAIN_PACKAGE = "..domain..";

    private JavaClasses importedClasses;

    @BeforeEach
    void setUp() {
        importedClasses = new ClassFileImporter().importPackages(MAIN_PACKAGE);
    }

    @Test
    @DisplayName("should assert that class in service package are only accessed by package controller or service")
    void serviceAccessRole() {

        ArchRule rule = classes()
                .that().resideInAPackage(SERVICE_PACKAGE)
                .should().onlyBeAccessed().byAnyPackage(CONTROLLER_PACKAGE, SERVICE_PACKAGE, SECURITY_PACKAGE)
                .andShould().haveSimpleNameContaining(SERVICE_LAYER);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("should assert that classes in exception package are only accessed by packages handler and service")
    void exceptionPackage() {

        ArchRule rule = classes()
                .that().resideInAPackage("..exception..")
                .should().onlyBeAccessed().byAnyPackage("..handler..", SERVICE_PACKAGE)
                .andShould().haveSimpleNameEndingWith("Exception");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("should assert controller implementation to match naming convention and access relevant classes")
    void controllerPackage() {
        ArchRule rule = classes().that().resideInAPackage(CONTROLLER_PACKAGE)
                .should().haveSimpleNameEndingWith(CONTROLLER_LAYER)
                .andShould().beAnnotatedWith(RestController.class)
                .andShould().accessClassesThat().resideInAnyPackage(SERVICE_PACKAGE, DOMAIN_PACKAGE, MODEL_PACKAGE);
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("should assert service implementation to match naming convention")
    void serviceImlPackage() {
        ArchRule rule = classes()
                .that().resideInAPackage("..service.impl..")
                .should().beAnnotatedWith(Service.class)
                .andShould().haveSimpleNameContaining(SERVICE_LAYER);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("should assert domain and model layer do not access high level layer")
    void domainAndModelPackages() {
        noClasses().that()
                .resideInAnyPackage(DOMAIN_PACKAGE, MODEL_PACKAGE)
                .should().accessClassesThat()
                .resideInAnyPackage(CONTROLLER_PACKAGE, SERVICE_PACKAGE, PERSISTENCE_PACKAGE).check(importedClasses);
    }

    @Test
    @DisplayName("should assert classes from persistence package to match naming convention")
    void persistencePackages() {

        noClasses().that()
                .resideInAPackage(PERSISTENCE_PACKAGE)
                .should().beInterfaces()
                .andShould().beAssignableTo(CrudRepository.class)
                .andShould().beAnnotatedWith(Repository.class)
                .andShould().accessClassesThat().resideInAnyPackage(SERVICE_PACKAGE, DOMAIN_PACKAGE, SECURITY_PACKAGE)
                .check(importedClasses);
    }

    @Test
    @DisplayName("should not find circular dependencies in packages")
    void cycle() {
        slices().matching(MAIN_PACKAGE.concat(".(*)..")).should().beFreeOfCycles().check(importedClasses);
    }

    @Test
    @DisplayName("should assert layer accessibility to be valid")
    void layerChecks() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer(SECURITY_LAYER).definedBy(SECURITY_PACKAGE)
                .layer(CONTROLLER_LAYER).definedBy(CONTROLLER_PACKAGE)
                .layer(SERVICE_LAYER).definedBy(SERVICE_PACKAGE)
                .layer(PERSISTENCE_LAYER).definedBy(PERSISTENCE_PACKAGE)

                .whereLayer(CONTROLLER_LAYER).mayNotBeAccessedByAnyLayer()
                .whereLayer(SERVICE_LAYER).mayOnlyBeAccessedByLayers(CONTROLLER_LAYER, SECURITY_LAYER)
                .whereLayer(PERSISTENCE_LAYER).mayOnlyBeAccessedByLayers(SERVICE_LAYER)
                .check(importedClasses);
    }
}
