package it.cnr.ilc.lexolite;

import it.cnr.ilc.lexolite.controller.AccountControllerToolbar;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oakgen
 */
@WebFilter(urlPatterns = {"/faces/*", "/servlet/*"})
public class LexOliteFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AccountControllerToolbar.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        File logFile;
        String logFilePath = null;
        if (logFilePath != null) {
            logFile = new File(logFilePath);
        } else {
            logFile = new File(filterConfig.getServletContext().getRealPath("/"));
            logFile = new File(logFile.getParentFile().getParentFile(), "logs/lexolite.log");
        }
        /*     PatternLayout layout = new PatternLayout();
        String conversionPattern = "%d %p %m\n";
        layout.setConversionPattern(conversionPattern);
        DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
        rollingAppender.setFile(logFile.getAbsolutePath());
        rollingAppender.setDatePattern("'.'yyyy-MM-dd");
        rollingAppender.setLayout(layout);
        rollingAppender.activateOptions();
        Logger logger = Logger.getLogger("lexolite");
        logger.setLevel(Level.INFO);
        logger.addAppender(rollingAppender);*/
        logger.info("LexO-lite start");
        System.out.println("log file " + logFile);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HibernateUtil.getSession().beginTransaction();
            enableFilters(HibernateUtil.getSession());
            chain.doFilter(request, response);
            try {
                HibernateUtil.getSession().getTransaction().commit();
            } catch (Exception e) {
                logger.error("On get hibernate session", e);
            }
        } catch (Throwable ex) {
            logger.error("doFilter()", ex);
            try {
                HibernateUtil.getSession().getTransaction().rollback();
            } catch (Exception e) {
            }
            ex.printStackTrace(new PrintStream(response.getOutputStream()));
        } finally {
            try {
                HibernateUtil.getSession().close();
            } catch (Exception e) {
            }
        }
    }

    private void enableFilters(Session session) {
        session.enableFilter("status");
    }

    @Override
    public void destroy() {
    }
}
