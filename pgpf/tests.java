package pgpf;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class tests {

    public static void RSAspeed(){
        System.out.println("---Beginning Speed Runs---");
        var date = new Date();
        var StartTime = date.getTime();
        System.out.print("Test Number:  ");
        long[] times = new long[51];
        times[0] = StartTime;
        
        for (int i = 1; i < 51; i ++){
            new RSA().gen();
            System.out.print("\b\b"+((String.valueOf(i).length() == 1) ? "0"+i : i));
            times[i] = date.getTime() - times[i-1];
        }

        long total = 0;

        for (int i = 0; i < 50; i++){
            total += times[i+1];
        }

        float avg = (total)/50;

        System.out.println(String.format("Ran %d tests in %d seconds with an average time of %f milliseconds per test.", 50, date.getTime()-StartTime, avg));
    }

    public static void RSASize(){
        int[] bdarray = new int[5];
        int[] bearray = new int[5];
        int[] bnarray = new int[5];
        
        for (int i = 0; i < 5; i ++){
            var r = new RSA();
            r.gen();
            bdarray[i] = r.d.toString().length();
            bearray[i] = r.e.toString().length();
            bnarray[i] = r.n.toString().length();
        }

        int oned = bdarray[0];
        int onee = bearray[0];
        int onen = bnarray[0];


        for (int i = 0; i < bdarray.length; i++){
            // if (bdarray[i] != oned){
            //     System.out.println(String.format("At Index %d of d, size was %d not %d.", i, bdarray[i], oned));
            // }
            // if (bearray[i] != onee){
            //     System.out.println(String.format("At Index %d of e, size was %d not %d.", i, bearray[i], onee));
            // }
            // if (bnarray[i] != onen){
            //     System.out.println(String.format("At Index %d of n, size was %d not %d.", i, bnarray[i], onen));
            // }
            System.out.println(bearray[i]);
        }

    }

    // Run tests
    public static void main(String[] args){
        RSASize();
    }
}
