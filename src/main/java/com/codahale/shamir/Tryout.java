package com.codahale.shamir;

import java.security.SecureRandom;
import java.util.Map;

public class Tryout {
    public static void main(String[] args) throws Exception {
        Scheme scheme = new Scheme(new SecureRandom(), 5, 3);
        Map<Integer, byte[]> split = scheme.split("hello".getBytes("UTF-8"));
        split.forEach((n, share) -> {
            System.out.printf("%d-", n);
            for (byte b: share)
                System.out.printf("%02x", b);
            System.out.println("\n");
        });

        byte[] joined = scheme.join(split);
        System.out.printf("Joined to: %s\n\n", new String(joined, "UTF-8"));

        Map<Integer, byte[]> example = Map.of(
                5, java.util.HexFormat.of().parseHex("c8d84a8f1d"),
                2, java.util.HexFormat.of().parseHex("c869462a01"),
                3, java.util.HexFormat.of().parseHex("a811c02627")
        );
        byte[] exampleJoined = scheme.join(example);
        System.out.printf("Example joined to: %s\n\n", new String(exampleJoined, "UTF-8"));
    }
}
