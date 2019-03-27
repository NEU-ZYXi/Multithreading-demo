import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // threadCreation1();
        // threadCreation2();

//        Thread thread = new Thread(new BlockingTask());
//        thread.start();

        // use interrupt() to terminate threads immediately which catches InterruptedException
        // thread.interrupt();

        // Daemon Thread: background threads that do not prevent the application from exiting if main thread terminates
        // Thread thread = new Thread(new LongComputationTask(new BigInteger("200000"), new BigInteger("10000000")));
//        thread.setDaemon(true);
//        thread.start();

        // for methods do not respond to interrupt(), manually check if the current thread is interrupted after calling interrupt()
        // thread.interrupt();
        App app = new App();
        app.main();
    }

    public static void threadCreation1() throws InterruptedException {

        // 1. implement Runnable interface to create threads
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // code that will run in a new thread
                System.out.println("We are in thread: " + Thread.currentThread().getName());
                System.out.println("Current thread priority: " + Thread.currentThread().getPriority());

                throw new RuntimeException("internal error");
            }
        });

        // set name
        thread.setName("My New Thread");
        // set priority
        thread.setPriority(Thread.MAX_PRIORITY);
        // set error handler
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("An error happened in thread " + t.getName() + ", error is " + e.getMessage());
            }
        });

        System.out.println("We are in thread: " + Thread.currentThread().getName() + " before starting a new thread");

        // instruct JVM to start a new thread and pass it into OS
        thread.start();

        System.out.println("We are in thread: " + Thread.currentThread().getName() + " after starting a new thread");

        Thread.sleep(10000);
    }

    public static void threadCreation2() {

        // 2. extend Thread class and override run() method to create new threads
        Thread thread = new NewThread();

        thread.start();
    }

    private static class NewThread extends Thread {

        @Override
        public void run() {
            System.out.println("We are in thread: " + this.getName());
        }
    }

    public static class BlockingTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Exiting blocking thread");
            }
        }
    }

    public static class LongComputationTask implements Runnable {

        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + "=" + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger ans = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
//                if (Thread.currentThread().isInterrupted()) {
//                    System.out.println("Computation interrupted");
//                    return BigInteger.ZERO;
//                }
                ans = ans.multiply(base);
            }
            return ans;
        }
    }

    public static class App {

        public  void main() throws InterruptedException {
            BigInteger ans = calculateResult(new BigInteger("200"), new BigInteger("2"),
                    new BigInteger("2"),new BigInteger("200"));
            System.out.println(ans);
        }

        public  BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) throws InterruptedException {
            BigInteger result = BigInteger.ZERO;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */


            PowerCalculatingThread t1 = new PowerCalculatingThread(base1, power1);
            PowerCalculatingThread t2 = new PowerCalculatingThread(base2, power2);
            t1.start();
            t2.start();
            try {
                t1.join(2000);
                t2.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (t1.isFinished && t2.isFinished) {
                result = t1.getResult().add(t2.getResult());
            } else {
                System.out.println("Timed Out");
            }
            return result;
        }

        private  class PowerCalculatingThread extends Thread {
            private BigInteger result = BigInteger.ONE;
            private BigInteger base;
            private BigInteger power;
            private boolean isFinished = false;

            public PowerCalculatingThread(BigInteger base, BigInteger power) {
                this.base = base;
                this.power = power;
            }

            @Override
            public void run() {
           /*
           Implement the calculation of result = base ^ power
           */
                for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                    result = result.multiply(base);
                }
                this.isFinished = true;
            }

            public BigInteger getResult() { return result; }
        }
    }
}
