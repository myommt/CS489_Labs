package com.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class EmployeeDAO {
    
    private EntityManagerFactory emf;
    
    public EmployeeDAO() {
        emf = Persistence.createEntityManagerFactory("employee-department-pu");
    }
    
    public void save(Employee employee) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(employee);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving employee", e);
        } finally {
            em.close();
        }
    }
    
    public Employee findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id", Employee.class);
            query.setParameter("id", id);
            List<Employee> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e LEFT JOIN FETCH e.department", Employee.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Employee> findByDepartment(Long departmentId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.department.id = :departmentId", Employee.class);
            query.setParameter("departmentId", departmentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Employee findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery(
                "SELECT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.email = :email", Employee.class);
            query.setParameter("email", email);
            List<Employee> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
    
    public void update(Employee employee) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(employee);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error updating employee", e);
        } finally {
            em.close();
        }
    }
    
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee employee = em.find(Employee.class, id);
            if (employee != null) {
                em.remove(employee);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error deleting employee", e);
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