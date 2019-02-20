import org.apache.log4j.Logger;

/**
 * 模拟日志产生
 */
public class LoggerGenerator {

    private static Logger logger = Logger.getLogger(LoggerGenerator.class);

    public static void main(String[] args) {

        int index = 0;
        while (true) {
            try {
                Thread.sleep(1000);
                logger.info("value : " + index++);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
