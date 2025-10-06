package com.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DepartmentDAO {
    
    private EntityManagerFactory emf;
    
    public DepartmentDAO() {
        emf = Persistence.createEntityManagerFactory("employee-department-pu");
    }
    
    public void save(Department department) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(department);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving department", e);
        } finally {
            em.close();
        }
    }
    
    public Department findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Department> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery("SELECT d FROM Department d", Department.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Department findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Department> query = em.createQuery(
                "SELECT d FROM Department d WHERE d.name = :name", Department.class);
            query.setParameter("name", name);
            List<Department> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    
    public void update(Department department) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(department);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating department", e);
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Department department = em.find(Department.class, id);
            if (department != null) {
                em.remove(department);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting department", e);
        } finally {
            em.close();
        }
    }
    
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}