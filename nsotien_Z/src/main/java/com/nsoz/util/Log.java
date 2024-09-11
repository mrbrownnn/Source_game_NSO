
package com.nsoz.util;

import com.nsoz.server.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Admin
 */
public class Log {

    private static final Logger LOG = LogManager.getLogger(Log.class);

    // Info Level Logs
    public static void info(String message) {
        LOG.info(message);
    }

    public static void info(Object object) {
        LOG.info(object);
    }

    // Warn Level Logs
    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void warn(Object object) {
        LOG.warn(object);
    }

    // Error Level Logs
    public static void error(String message) {
        LOG.error(message);
    }

    public static void error(Object object) {
        LOG.error(object);
    }

    // Fatal Level Logs
    public static void fatal(String message) {
        LOG.fatal(message);
    }

    // Debug Level Logs
    public static void debug(String message) {
        if (Config.getInstance().isShowLog()) {
            LOG.debug(message);
        }
    }

    public static void debug(Object object) {
        if (Config.getInstance().isShowLog()) {
            LOG.debug(object);
        }
    }

    public static void error(String message, Throwable throwable) {
        LOG.error(message, throwable);
    }

    public static void log(Priority priority, Object message) {
        LOG.log(priority, message);
    }

}
