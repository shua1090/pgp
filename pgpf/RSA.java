package pgpf;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64.*;
import java.util.*;
/*
    Provides encryption services for text
    Copyright (C) 2021 Shynn Lawrence

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
public class RSA{
    BigInteger e; // Public Key
    BigInteger d; // Private Key
    BigInteger n; 
    String publicKey;
    String privateKey;
    RSA(){
    }

    public String b64encode(String str){
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String b64decode(String str){
        Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(str), StandardCharsets.UTF_8);
    }

    public String encrypt(String str){
        var f = new BigInteger(str.getBytes(StandardCharsets.UTF_8)).modPow(this.e, this.n);
        return b64encode(f.toString());
    }

    public String decrypt(String zed){
        var k = new BigInteger(b64decode(zed)).modPow(this.d, this.n);
        return new String(k.toByteArray(), StandardCharsets.UTF_8);
    }

    public void unpackagePrivate(){

    }

    public void unpackagePublic(){

    }

    // Base 64 Encoding and Size Encoding
    public void packageKeys(){

        String tempPublicKey = "";
        String tempPrivateKey = "";

        int eLength = e.toString().length();
        int dLength = d.toString().length();

        tempPublicKey = b64encode(e.toString() + n.toString());
        tempPrivateKey = b64encode(d.toString() + n.toString());

        publicKey  = "---" +  " Public Key " + Integer.toHexString(eLength) + " ---\n" +  tempPublicKey + "\n" + "--- End Public Key ---" ;
        privateKey = "---" + " Private Key " + Integer.toHexString(dLength) + " ---\n" + tempPrivateKey + "\n" + "--- End Private Key ---";
    }

    // Extended Euclidean Algorithm
    public BigInteger inverse(BigInteger a, BigInteger b){
        BigInteger inv = BigInteger.ZERO;
        BigInteger q, r, r1 = a, r2 = b, t, t1 = BigInteger.ZERO, t2 = BigInteger.ONE;
        while (r2.compareTo(BigInteger.ZERO) == 1){
            q = r1.divide(r2);
            r = r1.subtract((q.multiply(r2)));
            r1 = r2;
            r2 = r;
            t = t1.subtract((q.multiply(t2)));
            t1 = t2;
            t2 = t;
        }
        if (r1.equals(BigInteger.ONE)){
            inv = t1;
        }
        if (inv.compareTo(BigInteger.ZERO) == 1){
            inv = inv.add(a);
        }
        return inv;
    }

    // Partial Pseudo-Random Prime Number Generation
    public BigInteger largePrime(int size){
        BigInteger longVal = BigInteger.valueOf(Long.MAX_VALUE);
        Random rand = new Random();
        BigInteger result;
        byte[] byteArray = new byte[size+1];
        rand.nextBytes(byteArray);
        byteArray[0] = 0;
        result = new BigInteger(byteArray);
        // result = result.nextProbablePrime();
        // FIXME: Pure random - Better but takes longer time. Still deciding.
        do{
            rand.nextBytes(byteArray);
            byteArray[0] = 0;
            result = new BigInteger(byteArray).nextProbablePrime();
        } while (result.compareTo(longVal) != 1 || !IsProbablyPrime(result) || !(result.toString(2).length() == (int) (size)*8));
        return result;
    }

    public void gen(){
        BigInteger f = largePrime(256);
        // System.out.println(f.toString());
        BigInteger k = largePrime(256);
        // System.out.println(k.toString());
        this.n = f.multiply(k);
        var phi = (f.subtract(BigInteger.valueOf(1)).multiply(k.subtract(BigInteger.valueOf(1))));
        // System.out.println(phi.toString().length());
        BigInteger temp;
        do{
            temp = largePrime(256);
        } while (!(temp.gcd(phi).compareTo(BigInteger.ONE) == 0)|| (temp.compareTo(f) != -1 || temp.compareTo(k) != -1));
        this.e = temp;
        this.d = inverse(phi, e);
        // System.out.println("Expected answer: 1; Actual answer: "+((this.e.multiply(this.d)).mod(phi))); //<- Sanity Check
    }

    // Rabin-Miller Primalty
    public static boolean IsProbablyPrime(BigInteger value) {
        int witnesses = 10;
        if (value.compareTo(BigInteger.ONE) == -1)
            return false;

        if (witnesses <= 0)
            witnesses = 10;

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
            while (a.compareTo(BigInteger.TWO) == -1 || a.compareTo(value.subtract(BigInteger.TWO)) == 1);

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