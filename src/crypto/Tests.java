package crypto;

import java.util.Date;

public class Tests {

    public static void RSAspeed() {
        System.out.println("---Beginning Speed Runs---");
        var date = new Date();
        var StartTime = date.getTime();
        System.out.print("Test Number:  ");
        long[] times = new long[51];
        times[0] = StartTime;

        for (int i = 1; i < 51; i++) {
            new Rsa().gen();
            System.out.print("\b\b" + ((String.valueOf(i).length() == 1) ? "0" + i : i));
            times[i] = date.getTime() - times[i - 1];
        }

        long total = 0;

        for (int i = 0; i < 50; i++) {
            total += times[i + 1];
        }

        double avg = total / 50.0;

        System.out.printf("Ran %d tests in %d seconds with an average time of %f milliseconds per test.%n", 50, date.getTime() - StartTime, avg);
    }

    public static void RSASize() {
        int[] bdarray = new int[5], bearray = new int[5], bnarray = new int[5];

        for (int i = 0; i < 5; i++) {
            var r = new Rsa();
            r.gen();
            bdarray[i] = r.d.toString().length();
            bearray[i] = r.e.toString().length();
            bnarray[i] = r.n.toString().length();
        }

        int oned = bdarray[0];
        int onee = bearray[0];
        int onen = bnarray[0];


        for (int i = 0; i < bdarray.length; i++) {
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
    public static void main(String[] args) {
        // var z = new RSA();
        // z.gen();
        // var k = z.encrypt("Loafer Arnav");
        // System.out.println(k);
        // System.out.println(z.decrypt(k));
        for (int i = 0; i < 10; i++) {
            var z = new Rsa();
            z.gen();
            z.packageKeys();
            System.out.println("----" + i + "----");
            System.out.println(z.privateKey);
            System.out.println(z.publicKey);
            // System.out.println(z.encrypt("NICE").length());
            // System.out.println(z.encrypt("Though I find Asciidoc allows for more complex document structure.").length());
            // System.out.println(z.encrypt("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOI SAID I WOULD CLOSE DISCORD AND FOCUSSSSSWait @3dcantaloupe do we have music bot? You said we do, and then you tried some stuff and nothing seemed to happen").length());
            // System.out.println(String.format("D length in is: %d while E length in is %d. In bits, D: %d, E: %d. Encrypting some text will lead to a length of %s", z.d.toString().length(), z.e.toString().length(), z.d.toString(2).length(), z.e.toString(2).length(), z.encrypt("TEST TEXT IS BEST HOHO").length()));
        }
        // var f = z.decrypt(k);
        // System.out.println(f);
        // z.decrypt()
        // System.out.println(z.encrypt("Test"));
        // System.out.println(z.decrypt(z.encrypt("Test")));
    }
}
