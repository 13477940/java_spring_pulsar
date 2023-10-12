package framework.web.session;

import framework.web.session.service.SessionCounter;
import framework.web.session.service.SessionServiceStatic;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class AppUserSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        SessionCounter.addCount(event.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        SessionCounter.delCount(event.getSession());
        SessionServiceStatic.getInstance().removeUser(event.getSession());
    }

}
