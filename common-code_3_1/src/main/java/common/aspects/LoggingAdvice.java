package common.aspects;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

/**
 * General purpose advice for aspects needing specific logging. Tacky, but still useful!
 *
 * User: djnorth
 * Date: 17/07/2012
 * Time: 17:29
 */
public class LoggingAdvice {

    /**
     * Logger to use
     */
    private final Logger logger;

    /**
     * Log level to use
     */
    private Level level;


    /**
     * Default constructor setting logger name to "rewards"
     */
    public LoggingAdvice() {
        this("rewards", Level.DEBUG);
    }


    /**
     * Constructor taking logger name and level
     *
     * @param loggerName
     * @param level
     */
    public LoggingAdvice(String loggerName, Level level) {
        this.logger = Logger.getLogger(loggerName);
        setLevel(level);
    }


    /**
     * Setter to override log level (default DEBUG)
     *
     * @param level
     */
    public void setLevel(Level level) {
        this.level = level;
    }


    /**
     * log method
     *
     * @param pjp
     */
    public Object logCall(ProceedingJoinPoint pjp) throws Throwable {

        Object returnValue = null;
        try {
            final StringBuffer message = new StringBuffer("invoked for target:");
            message.append(pjp.getTarget()).append('.').append(pjp.getSignature().toString());
            message.append(":args=").append(Arrays.toString(pjp.getArgs()));
            logger.log(level, message);
            returnValue = pjp.proceed();
            logger.log(level, "target:" + pjp.getTarget() + " returned:" + returnValue);
        } catch (Throwable t) {
            logger.log(level, "target:" + pjp.getTarget() + " exception", t);
            throw t;
        }

        return returnValue;
    }
}
