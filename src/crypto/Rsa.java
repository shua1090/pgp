package crypto;

import javax.annotation.processing.SupportedAnnotationTypes;

import jdk.jfr.Description;

import java.io.NotActiveException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64.*;
import java.util.*;

public class Rsa {
    BigInteger e; // Public Key
    BigInteger d; // Private Key
    BigInteger n;
    String publicKey;
    String privateKey;

    public String b64encode(String str) {
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public String b64decode(String str) {
        Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(str), StandardCharsets.UTF_8);
    }

    @Deprecated
    private String pad(String str, int length){
        if (str.length() < length){
            while (str.length() < length)
                str = "0" + str;
        } else {
            throw new RuntimeException("Dang");
        }
        return str;
    }

    @Deprecated
    public String encrypt(String bar) {
        var l = bar.split("");
        System.out.println(l.length);
        String f = "";
        for (String str : l) {
//        var f = new BigInteger(str. getBytes(StandardCharsets.UTF_8)).modPow(this.e, this.n);
            f += pad(new BigInteger(str.getBytes(StandardCharsets.UTF_8)).modPow(this.e, this.n).toString(), 1235);
//            System.out.println("-"+f.length());
//            System.out.println(pad(new BigInteger(str.getBytes(StandardCharsets.UTF_8)).modPow(this.e, this.n).toString(), 1234).length());
//            System.out.println(f.length());
        }
        System.out.println("Length: "+f.length());
        return b64encode(f);
    }


    public String fencrypt(String str) {
        var f = new BigInteger(str.getBytes(StandardCharsets.UTF_8)).modPow(this.e, this.n);
//        System.out.println("-"+f.toString().length());
        return b64encode(f.toString());
    }

    @Deprecated
    public String decrypt(String str) {
        String originalText = "";
        String decodedString = b64decode(str);
        if (decodedString.length() % 1235 != 0)
            throw new RuntimeException();
        for (int i = 0; i < decodedString.length() / 1235; i++){
            var k = decodedString.substring(1235*i, 1235*(i+1));
            originalText += new String(new BigInteger(k).modPow(this.d, this.n).toByteArray(), StandardCharsets.UTF_8);
        }
        return originalText;
    }

    public String fdecrypt(String zed) {
        var k = new BigInteger(b64decode(zed)).modPow(this.d, this.n);
        return new String(k.toByteArray(), StandardCharsets.UTF_8);
    }

    public void unpackagePrivate() {

    }

    public void unpackagePublic() {

    }

    // Base 64 Encoding and Size Encoding
    public void packageKeys() {

        String tempPublicKey;
        String tempPrivateKey;

        int eLength = e.toString().length();
        int dLength = d.toString().length();

        tempPublicKey = b64encode(e.toString() + n.toString());
        tempPrivateKey = b64encode(d.toString() + n.toString());

        publicKey = "---" + " Public Key " + Integer.toHexString(eLength) + " ---\n" + tempPublicKey + "\n" + "--- End Public Key ---";
        privateKey = "---" + " Private Key " + Integer.toHexString(dLength) + " ---\n" + tempPrivateKey + "\n" + "--- End Private Key ---";
    }

    // Extended Euclidean Algorithm
    public BigInteger inverse(BigInteger a, BigInteger b) {
        BigInteger inv = BigInteger.ZERO;
        BigInteger q, r, r1 = a, r2 = b, t, t1 = BigInteger.ZERO, t2 = BigInteger.ONE;
        while (r2.compareTo(BigInteger.ZERO) > 0) {
            q = r1.divide(r2);
            r = r1.subtract((q.multiply(r2)));
            r1 = r2;
            r2 = r;
            t = t1.subtract((q.multiply(t2)));
            t1 = t2;
            t2 = t;
        }
        if (r1.equals(BigInteger.ONE)) {
            inv = t1;
        }
        if (inv.compareTo(BigInteger.ZERO) > 0) {
            inv = inv.add(a);
        }
        return inv;
    }

    // Partial Pseudo-Random Prime Number Generation
    public BigInteger largePrime(int size) {
        BigInteger longVal = BigInteger.valueOf(Long.MAX_VALUE);
        Random rand = new Random();
        BigInteger result;
        byte[] byteArray = new byte[size + 1];
        rand.nextBytes(byteArray);
        byteArray[0] = 0;
        result = new BigInteger(byteArray);
        // result = result.nextProbablePrime();
        // FIXME: Pure random - Better but takes longer time. Still deciding.
        do {
            rand.nextBytes(byteArray);
            byteArray[0] = 0;
            result = new BigInteger(byteArray).nextProbablePrime();
        } while (result.compareTo(longVal) != 1 || !IsProbablyPrime(result) || !(result.toString(2).length() == size * 8));
        return result;
    }

    public BigInteger[] twoPrimeGen(int size){
        BigInteger f = largePrime(size);
        BigInteger k = largePrime(size);
        return new BigInteger[]{f, k};
    }

    public BigInteger nphi(BigInteger[] arr){
        this.n = arr[0].multiply(arr[1]);
        var phi = (arr[0].subtract(BigInteger.valueOf(1)).multiply(arr[1].subtract(BigInteger.valueOf(1))));
        return phi;
    }

    public void edcalc(BigInteger phi, BigInteger[] b, int size){
        BigInteger temp;
        do {
            temp = largePrime(size);
        } while (!(temp.gcd(phi).compareTo(BigInteger.ONE) == 0) || (temp.compareTo(b[0]) != -1 || temp.compareTo(b[1]) != -1));
        this.e = temp;
        this.d = inverse(phi, e);
    }

    public void gen(int size) {
        BigInteger phi;
        do {
            var b = twoPrimeGen(size);
            phi = nphi(b);
            edcalc(phi, b,size);
        } while (this.fencrypt("TEST").length() != 1644 || !(this.e.multiply(this.d).mod(phi).equals(BigInteger.ONE)));
//        BigInteger f = largePrime(256);
//        // System.out.println(f.toString());
//        BigInteger k = largePrime(256);
//        // System.out.println(k.toString());
//        this.n = f.multiply(k);
//        var phi = (f.subtract(BigInteger.valueOf(1)).multiply(k.subtract(BigInteger.valueOf(1))));
//        // System.out.println(phi.toString().length());
//        BigInteger temp;
//        do {
//            temp = largePrime(256);
//        } while (!(temp.gcd(phi).compareTo(BigInteger.ONE) == 0) || (temp.compareTo(f) != -1 || temp.compareTo(k) != -1));
//        this.e = temp;
//        this.d = inverse(phi, e);
        // System.out.println("Expected answer: 1; Actual answer: "+((this.e.multiply(this.d)).mod(phi))); //<- Sanity Check
    }

    // Rabin-Miller Primality
    public static boolean IsProbablyPrime(BigInteger value) {
        int witnesses = 10;
        if (value.compareTo(BigInteger.ONE) < 0)
            return false;

        BigInteger d = value.subtract(BigInteger.ONE);
        int s = 0;

        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s += 1;
        }

        byte[] bytes = new byte[value.toByteArray().length];
        BigInteger a;
        var Gen = new Random();

        for (int i = 0; i < witnesses; i++) {
            do {
                Gen.nextBytes(bytes);

                a = new BigInteger(bytes);
            }
            while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(value.subtract(BigInteger.TWO)) > 0);

            BigInteger x = a.modPow(d, value);
            if (x.equals(BigInteger.ONE) || x.equals(value.subtract(BigInteger.ONE)))
                continue;

            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, value);

                if (x.equals(BigInteger.ONE))
                    return false;
                if (x.equals(value.subtract(BigInteger.ONE)))
                    break;
            }

            if (!x.equals(value.subtract(BigInteger.ONE)))
                return false;
        }

        return true;
    }
}
