package servlets.filter;

import exceptions.BusinessException;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateSessionManager;
import util.HibernateUtil;

import java.io.IOException;

@Slf4j
public class TransactionFilter implements Filter {

    private SessionFactory sessionFactory;

    @Override
    public void init(FilterConfig filterConfig) {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException {
        
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            HibernateSessionManager.setSession(session);

            chain.doFilter(request, response);

            if (transaction != null && transaction.isActive()) {
                transaction.commit();
            }
        } catch (BusinessException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.warn("A business exception occurred, rolling back transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("A critical error occurred, rolling back transaction", e);
            throw new ServletException("A critical error occurred during the transaction.", e);
        } finally {
            if (session != null) {
                HibernateSessionManager.clearSession();
                session.close();
            }
        }
    }
}
