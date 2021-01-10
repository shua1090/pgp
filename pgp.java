import java.math.*;
import java.util.Random;
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
class RSA{
    BigInteger e; // Public Key
    BigInteger d; // Private Key
    BigInteger n; 
    RSA(){
        setup();
    }

    private void setup(){
        BigInteger f = largePrime(256);
        BigInteger k = largePrime(256);
        this.n = f.multiply(k);
        var phi = (f.subtract(BigInteger.valueOf(1)).multiply(k.subtract(BigInteger.valueOf(1))));
        BigInteger temp;
        do{
            temp = largePrime(250);
        } while (!(temp.gcd(phi).compareTo(BigInteger.ONE) == 0)|| (temp.compareTo(f) != -1 || temp.compareTo(k) != -1));
        this.e = temp;
        this.d = inverse(phi, e);
        System.out.println("Expected answer: 1; Actual answer: "+((this.e.multiply(this.d)).mod(phi)));
    }

    // Extended Euclidean Algorithm
    BigInteger inverse(BigInteger a, BigInteger b){
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

    private BigInteger largePrime(int size){
        BigInteger longVal = BigInteger.valueOf(Long.MAX_VALUE);
        Random rand = new Random();
        BigInteger result;
        byte[] byteArray = new byte[size];
        rand.nextBytes(byteArray);
        byteArray[0] = 0;
        result = new BigInteger(byteArray);
        result = result.nextProbablePrime();
        // Pure random
        // do{
            // rand.nextBytes(byteArray);
            // result = new BigInteger(byteArray);
        // } while (result.compareTo(longVal) != 1 || !result.isProbablePrime(1));
        return result;
    }

    public void gen(){
    }
}

class keygen{
    int encryptionNum;
    keygen(String encryption){
        switch (encryption){
            case "rsa":
                this.encryptionNum = 1;
                break;
            case "des":
                this.encryptionNum = 2;
                break;
            default:
                this.encryptionNum = 0;
                break;
        }
    }


    public void generate(){
        if (encryptionNum == 1){
            var r = new RSA();
        }
    }
}

public class pgp{   
    public static void main(String[] args){
        for (int i = 0; i < 10; i++){
            long t = new Date().getTime();
            new keygen("rsa").generate();
            System.out.println(String.format("%d time has passed.", new Date().getTime()-t));
        }
    }
}