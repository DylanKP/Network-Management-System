import java.util.logging.*;

/**
 * This class does the logging of the actions performed by the
 * system. 
 */
public class LoggingManager {
    private static LoggingManager instance;
    private Logger logger;

    private LoggingManager() {
        logger = Logger.getLogger(LoggingManager.class.getName());
        logger.setUseParentHandlers(false);

        if (logger.getHandlers().length == 0) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            try {
                FileHandler fileHandler = new FileHandler("log/log.txt", true); // Append to the same file
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "An error occurred. Failed to create log.txt: " + e.getMessage());
            }
        }
    }


    public static synchronized LoggingManager getInstance() {
        if (instance == null) {
            instance = new LoggingManager();
        }
        return instance;
    }

    public void logEvent(Level level, String message) {
        logger.log(level, message);
    }

    public void close() {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
        }
    }
}

