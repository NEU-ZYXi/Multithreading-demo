import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main() throws InterruptedException {
        BigInteger ans = calculateResult(new BigInteger("2000"),new BigInteger("2000"),new BigInteger("2000"),new BigInteger("2000"));
        System.out.println(ans);
    }

        public static BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) throws InterruptedException {
            BigInteger result = BigInteger.ZERO;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
            List<PowerCalculatingThread> threads = new ArrayList<>();

            threads.add(new PowerCalculatingThread(base1, power1));
            threads.add(new PowerCalculatingThread(base2, power2));
            for (PowerCalculatingThread thread : threads) {
                thread.setDaemon(true);
                thread.join();
            }
            for (PowerCalculatingThread thread : threads) {
                thread.start();
            }
            for (PowerCalculatingThread thread : threads) {
                result = result.add(thread.getResult());
            }
            return result;
        }

        private static class PowerCalculatingThread extends Thread {
            private BigInteger result = BigInteger.ONE;
            private BigInteger base;
            private BigInteger power;

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
            }

            public BigInteger getResult() { return result; }
        }
    }

