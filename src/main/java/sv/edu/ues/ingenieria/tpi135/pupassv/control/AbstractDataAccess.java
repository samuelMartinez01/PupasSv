/*
 * Clase abstracta que proporciona operaciones CRUD genéricas para entidades JPA.
 * @param <T> Tipo de la entidad gestionada.
 */
package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author samuel
 * @param <T>
 */
public abstract class AbstractDataAccess<T> {

    /**
     * Devuelve una instancia de EntityManager.
     * @return EntityManager para realizar operaciones de persistencia.
     */
    public abstract EntityManager getEntityManager();

    /**
     * Clase del tipo de dato gestionado.
     */
    Class tipoDato;

    /**
     * Constructor que inicializa el tipo de dato gestionado.
     * @param tipoDatos Clase del tipo de dato.
     */
    public AbstractDataAccess(Class tipoDatos) {
        this.tipoDato = tipoDatos;
    }

    /**
     * Crea y persiste una entidad en la base de datos.
     * @param entity Entidad a persistir.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     * @throws IllegalArgumentException Si la entidad es nula.
     */
    public void create(T entity) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;
        if (entity == null) {
            throw new IllegalArgumentException("Entidad no puede ser nula");
        }
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("EntityManager no disponible");
            }
            em.persist(entity);
            em.flush();
        } catch (PersistenceException pe) {
            // Captura específicamente errores de persistencia
            String errorMsg = "Error al persistir entidad: " + pe.getMessage();
            if (pe.getCause() != null) {
                errorMsg += " - Causa: " + pe.getCause().getMessage();
            }
            throw new IllegalStateException(errorMsg, pe);
        } catch (Exception ex) {
            // Captura otros errores genéricos
            String errorMsg = "Error inesperado al crear entidad: " + ex.getMessage();
            throw new IllegalStateException(errorMsg, ex);
        }
    }

    /**
     * Busca una entidad por su ID.
     * @param id Identificador de la entidad.
     * @return La entidad encontrada o null si no existe.
     * @throws IllegalArgumentException Si el ID es nulo.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     */
    public T findById(final Object id) throws IllegalStateException {
        EntityManager em = null;

        // Si el ID es nulo, retornar null directamente
        if (id == null) {
            return null;
        }

        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("EntityManager no disponible");
            }
            return (T) em.find(tipoDato, id);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al buscar el id en la entidad", ex);
        }
    }

    /**
     * Obtiene una lista de entidades dentro de un rango especificado.
     * @param first Índice del primer resultado.
     * @param max Número máximo de resultados.
     * @return Lista de entidades encontradas.
     * @throws IllegalArgumentException Si los parámetros son inválidos.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     */
    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException {
        EntityManager em = null;
        if (first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros no válidos");
        }
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("EntityManager no disponible");
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(tipoDato);
            Root<T> raiz = cq.from(tipoDato);
            cq.select(raiz);
            TypedQuery<T> q = em.createQuery(cq);
            q.setFirstResult(first);
            q.setMaxResults(max);
            return q.getResultList();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al obtener el rango de entidades", ex);
        }
    }

    /**
     * Actualiza una entidad existente.
     * @param registro Entidad a actualizar.
     * @return La entidad actualizada.
     * @throws IllegalArgumentException Si la entidad es nula.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     */
    public T update(T registro) throws IllegalArgumentException, IllegalStateException {
        EntityManager em = getEntityManager();

        if (registro == null) {
            throw new IllegalArgumentException("El registro es nulo");
        }
        try {
            return em.merge(registro);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al actualizar la entidad", ex);
        }
    }

    /**
     * Elimina una entidad de la base de datos.
     * @param id Entidad a eliminar por id.
     * @throws IllegalArgumentException Si la entidad es nula.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     */
    public void delete(Object id) {
        if (id == null || Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("Id no valido ");
        }
        EntityManager em = null;
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("No se pudo acceder al repositorio");
        }
        try {
            T registro = (T) em.find(tipoDato, id);
            if (registro == null) {
                throw new EntityNotFoundException("Id not found");
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(this.tipoDato);
            Root<T> raiz = cd.from(this.tipoDato);
            cd.where(cb.equal(raiz, registro));
            em.createQuery(cd).executeUpdate();
            return;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Cuenta el número total de entidades.
     * @return Número total de entidades.
     * @throws IllegalStateException Si no hay EntityManager disponible.
     */
    public Long count() throws IllegalStateException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("EntityManager no disponible");
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            cq.select(cb.count(cq.from(tipoDato)));
            return em.createQuery(cq).getSingleResult();
        } catch (Exception ex) {
            throw new IllegalStateException("Error al contar las entidades", ex);
        }
    }
}
