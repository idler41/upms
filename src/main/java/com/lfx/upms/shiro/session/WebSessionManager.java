package com.lfx.upms.shiro.session;

import com.lfx.upms.util.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-03-22 09:07
 */
@Slf4j
public class WebSessionManager extends DefaultWebSessionManager {

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);

        if (sessionId == null) {
            log.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a " +
                    "session could not be found.", sessionKey);
            return null;
        }
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();

            // read session from request
            if (request != null) {
                Object sessionObj = request.getAttribute(sessionId.toString());
                if (sessionObj != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("read session from request");
                    }
                    return (Session) sessionObj;
                }
            }
        }

        Session s = retrieveSessionFromDataSource(sessionId);
        if (s == null) {
            //session ID was provided, meaning one is expected to be found, but we couldn't find one:
            String msg = "Could not find session with ID [" + sessionId + "]";
            throw new UnknownSessionException(msg);
        }
        if (request != null) {
            request.setAttribute(sessionId.toString(), s);
            if (request.getAttribute(WebConstants.CURRENT_USER_ID) == null) {
                request.setAttribute(WebConstants.CURRENT_USER_ID, s.getAttribute(WebConstants.CURRENT_USER_ID));
            }
        }
        return s;
    }

}
