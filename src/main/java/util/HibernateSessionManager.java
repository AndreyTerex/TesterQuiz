package util;

import org.hibernate.Session;

public class HibernateSessionManager {

    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    public static Session getSession() {
        Session session = sessionThreadLocal.get();
        if (session == null) {
            throw new IllegalStateException("Session not found.");
        }
        return session;
    }

    public static void setSession(Session session) {
        sessionThreadLocal.set(session);
    }

    public static void clearSession() {
        sessionThreadLocal.remove();
    }
}
