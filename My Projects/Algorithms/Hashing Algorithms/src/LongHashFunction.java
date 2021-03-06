

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author hkanna
 */
public class LongHashFunction implements HashFunction<Long> {

    private final int[] primes;
    private final int count;
    private final Random random = new Random();
    private final static int MAX = 1000;

    public LongHashFunction(int n) {
        if (n < 0 || n == Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        count = n;
        this.primes = new int[n];
        for (int i = 0; i < primes.length; i++) {
            BigInteger big = new BigInteger(Integer.toString(random.nextInt(MAX)));
            primes[i] = big.nextProbablePrime().intValue();
            System.out.println("Prime multiplier " + (i + 1) + " " + primes[i]);
        }
    }
    /*
     * Returns hashcode integer.
     * parameter String and hash function number 1,2,3...
     */

    public int hashCode(Long key, int n) {
        String longString = key.toString();
        if (n < 1 || n > primes.length) {
            throw new IllegalArgumentException();
        }
        final int prime = primes[ n - 1];
        int hashCode = 0;

        for (int i = 0; i < longString.length(); i++) {
            hashCode = prime * hashCode + longString.charAt(i);
        }

        return hashCode;
    }

    public int number() {
        return count;
    }

    public void generateNew() {
        for (int i = 0; i < count; i++) {
            BigInteger big = new BigInteger(Integer.toString(random.nextInt()));
            primes[i] = big.nextProbablePrime().intValue();
        }
    }
}
