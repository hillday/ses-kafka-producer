package software.amazon.samples.kafka.lambda;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NumUtil {

    private static long tmpID = 0;
    private static final long LOCK_TIME = 1;
    private static final long INCREASE_STEP = 1;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
    private static final Lock LOCK = new ReentrantLock();


    public static long nextPkId() {
        //当前：（年、月、日、时、分、秒、毫秒）
        long timeCount;
        try {
            if (LOCK.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
                timeCount = Long.parseLong(sdf.format(new Date()));
                try {
                    if (tmpID < timeCount) {
                        tmpID = timeCount;
                    } else {
                        tmpID += INCREASE_STEP;
                        timeCount = tmpID;
                    }
                    int random = (int)(Math.random()*10000)+1;
                    return timeCount + random;
                } finally {
                    LOCK.unlock();
                }
            } else {
                return nextPkId();
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
}
