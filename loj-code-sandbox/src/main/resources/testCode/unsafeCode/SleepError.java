

/**
 * 无限睡眠（阻塞程序执行）
 */
public class Main {
    public static void main(String[] args) {
        Long ONE_HOUR = 60 * 60 * 1000L;
        try {
            Thread.sleep(ONE_HOUR);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("睡完啦");
    }
}
