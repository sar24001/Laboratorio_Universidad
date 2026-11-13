package uam.edu.ni.laboratorio_universidad.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory emf;

    public static void iniciar() {
        if (emf == null) {

            String password = System.getenv("DB_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new RuntimeException(
                        "La variable DB_PASSWORD no está configurada."
                );
            }

            Map<String, Object> propiedades = new HashMap<>();
            propiedades.put(
                    "jakarta.persistence.jdbc.password",
                    password
            );

            emf = Persistence.createEntityManagerFactory(
                    "bd_laboratorio_orm",
                    propiedades
            );
        }
    }

    public static EntityManager getEntityManager() {
        iniciar();
        return emf.createEntityManager();
    }

    public static void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}

