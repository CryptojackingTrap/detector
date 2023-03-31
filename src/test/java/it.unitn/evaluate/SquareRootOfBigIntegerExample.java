package it.unitn.evaluate;

/*
 Find Square Root of BigInteger Example
 This Java example shows how to find square root of BigInteger using
 NEWTON's method.
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;

public class SquareRootOfBigIntegerExample {

    /**
     * from http://www.java-examples.com/find-square-root-biginteger-example
     *
     * @param //my2P100000
     * @return
     */
    public static BigDecimal squareRootOfBigInteger(String input) {
        BigInteger my2P100000 = new BigInteger(input);
        String mys = my2P100000 + "";
        String n = (mys);
        int firsttime = 0;

        BigDecimal myNumber = new BigDecimal(n);
        BigDecimal g = new BigDecimal("1");
        BigDecimal my2 = new BigDecimal("2");
        BigDecimal epsilon = new BigDecimal("0.0000000001");

        BigDecimal nByg = myNumber.divide(g, 9, BigDecimal.ROUND_FLOOR);

        // Get the value of n/g
        BigDecimal nBygPlusg = nByg.add(g);

        // Get the value of "n/g + g
        BigDecimal nBygPlusgHalf = nBygPlusg.divide(my2, 9,
                BigDecimal.ROUND_FLOOR);

        // Get the value of (n/g + g)/2
        BigDecimal saveg = nBygPlusgHalf;
        firsttime = 99;

        do {
            g = nBygPlusgHalf;
            nByg = myNumber.divide(g, 9, BigDecimal.ROUND_FLOOR);
            nBygPlusg = nByg.add(g);
            nBygPlusgHalf = nBygPlusg.divide(my2, 9, BigDecimal.ROUND_FLOOR);
            BigDecimal savegdiff = saveg.subtract(nBygPlusgHalf);

            if (savegdiff.compareTo(epsilon) == -1) {
                firsttime = 0;
            } else {
                saveg = nBygPlusgHalf;
            }

        } while (firsttime > 1);

        return saveg;

    }

    public static BigDecimal approximateCombination(Integer n, Integer k) {
        if (k > n / 2) {
            k = n - k;
        }


        Integer n_k = n - k;

        //using last solution in: https://www.quora.com/What-are-some-efficient-algorithms-to-compute-nCr
        BigDecimal bigN = new BigDecimal(n + "");
        BigDecimal bigK = new BigDecimal(k + "");
        BigDecimal bign_k = new BigDecimal(n_k + "");

        BigDecimal a = bigN.pow(n);
        System.out.println((new Date()).toString() + "a: " + a.toString());
        BigDecimal b = squareRootOfBigInteger(n + "");
        System.out.println((new Date()).toString() + "b: " + b.toString());


        BigDecimal c = bigK.pow(k);
        System.out.println((new Date()).toString() + "c: " + c.toString());
        BigDecimal d = squareRootOfBigInteger(k + "");
        System.out.println((new Date()).toString() + "d: " + d.toString());

        BigDecimal e = bign_k.pow(n_k);
        System.out.println((new Date()).toString() + "e: " + e.toString());
        BigDecimal f = squareRootOfBigInteger(bign_k + "");
        System.out.println((new Date()).toString() + "f: " + f.toString());

        BigDecimal g = new BigDecimal(Math.pow(Math.PI * 2, 0.5));
        System.out.println((new Date()).toString() + "g: " + g.toString());
        //
        BigDecimal h = a.multiply(b);
        System.out.println((new Date()).toString() + "h: " + h.toString());
        BigDecimal i = c.multiply(d).multiply(e).multiply(f).multiply(g);
        System.out.println((new Date()).toString() + "i: " + i.toString());
        //
        MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
        BigDecimal j = h.divide(i, mc);
        System.out.println((new Date()).toString() + "j: " + j.toString());

        return j;
    }

    /**
     * for very big n and very small k
     *
     * @param n
     * @param k
     * @return
     */
    public static BigDecimal myCombination(int n, int k) {
        if (k > n / 2) {
            k = n - k;
        }

        BigDecimal result = new BigDecimal(1);
        for (int i = 0; i < k; i++) {
            result = result.multiply(new BigDecimal(n - i));
//			result *=  (n-i);
        }
        BigDecimal temp = new BigDecimal(1);
        for (int i = 1; i <= k; i++) {
            temp = temp.multiply(new BigDecimal(i));
//			temp *= i; 
        }
        result = result.divide(temp);
//		result = result / temp;
        return result;
    }

    public static double factoriel(int n) {
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static BigDecimal getFalsePositive(int BC, int HC_e) {
        BigDecimal a = myCombination(BC, 36 * HC_e);
        BigDecimal b = new BigDecimal(Math.pow(factoriel(36), HC_e));
        BigDecimal d = new BigDecimal(8 * BC);
//		BigDecimal c = BigDecimalMath.pow(new BigDecimal(2), d);
        BigDecimal c = new BigDecimal(2).pow(36 * HC_e);

//		BigDecimal c = new BigDecimal(Math.pow(2, 8* BC));
        BigDecimal result = a.multiply(b).divide(c);
        return result;
    }

    public static void main(String[] args) {
//		Integer n = 9422315;
//		Integer k = 144;
//		Integer n = 11;
//		Integer k = 10;
		
		/*Integer n = 3000000;
		Integer k = 3;
		
		
		System.out.println((new Date() ).toString()+"start");
		BigDecimal combination = combination(n, k);
		System.out.println((new Date() ).toString()+"combination:"+combination.toString());*/
		
		/*double a_1 = (306882*306881*306880*20288)/(6*Math.pow(2, 224));
		double a_2 = (613831*613830*613829*613828*613827*613826*613825*613824*613823)*(405842*405841*405840)/((9*8*7*6*5*4*3*2)*6*3*Math.pow(2, 224));
		
		System.out.println(a_1);
		System.out.println(a_2);*/
        double BC = 57967.95439739414 * 20 * (0.102270218 + 2 * 0.012535344 + 4 * 0.350057228 + 8 * 0.529457697 + 16 * 0.003299458 + 32 * 0.000687026 + 64 * 0.001693029);
        int HC_e = 3;
        System.out.println(getFalsePositive((int) BC, HC_e));

    }

}
