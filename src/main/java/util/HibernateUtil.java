package util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

@Slf4j
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static void init() {
        try {
            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();

            Metadata metadata = new MetadataSources(standardRegistry)
                    .getMetadataBuilder()
                    .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();

            log.info("Hibernate SessionFactory has been initialized by the ContextListener from hibernate.cfg.xml.");

        } catch (Throwable ex) {
            log.error("Initial SessionFactory creation failed. Check if hibernate.cfg.xml is in the classpath.", ex);
            throw new RuntimeException("Failed to init SessionFactory", ex);
        }
    }

    private HibernateUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory has not been initialized. Please call HibernateUtil.init() from your ContextListener first.");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("Hibernate SessionFactory has been shut down.");
        }
    }
}
