package uam.edu.ni.laboratorio_universidad.dao;

import uam.edu.ni.laboratorio_universidad.database.JPAUtil;
import uam.edu.ni.laboratorio_universidad.model.Equipo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class EquipoDAO {

    // Guardar equipo
    public void guardar(Equipo equipo) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            em.persist(equipo);

            tx.commit();

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }

    // Listar equipos
    public List<Equipo> listar() {
        return buscar(null, null);
    }

    // Buscar por nombre y tipo
    public List<Equipo> buscar(String nombre, Long idTipo) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            String jpql =
                    "SELECT e FROM Equipo e JOIN FETCH e.tipoEquipo WHERE 1=1";

            if (nombre != null && !nombre.isBlank()) {
                jpql += " AND LOWER(e.nombre) LIKE :nombre";
            }

            if (idTipo != null) {
                jpql += " AND e.tipoEquipo.idTipo = :idTipo";
            }

            jpql += " ORDER BY e.idEquipo";

            TypedQuery<Equipo> consulta =
                    em.createQuery(jpql, Equipo.class);

            if (nombre != null && !nombre.isBlank()) {
                consulta.setParameter(
                        "nombre",
                        "%" + nombre.trim().toLowerCase() + "%"
                );
            }

            if (idTipo != null) {
                consulta.setParameter("idTipo", idTipo);
            }

            return consulta.getResultList();

        } finally {
            em.close();
        }
    }

    // Actualizar equipo
    public boolean actualizar(Equipo equipo) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Equipo equipoBD = em.find(
                    Equipo.class,
                    equipo.getIdEquipo()
            );

            if (equipoBD == null) {
                tx.rollback();
                return false;
            }

            em.merge(equipo);

            tx.commit();
            return true;

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }

    // Eliminar equipo
    public boolean eliminar(Long id) {

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Equipo equipo = em.find(Equipo.class, id);

            if (equipo == null) {
                tx.rollback();
                return false;
            }

            em.remove(equipo);

            tx.commit();
            return true;

        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;

        } finally {
            em.close();
        }
    }
}

