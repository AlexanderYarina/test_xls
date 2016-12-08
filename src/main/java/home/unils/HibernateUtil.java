package home.unils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Created by Alex on 07.12.2016.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private static void buildSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure("/hibernate.cfg.xml");
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) buildSessionFactory();
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) getSessionFactory().close();
    }

}

