package com.artemkaxboy.jground.spoj.acode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <a href="https://www.spoj.com/problems/ACODE/">The problem</a>
 */
public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String inputLine;
        while (!(inputLine = r.readLine()).equals("0")) {
            System.out.println(decode(inputLine));
        }
    }

    static long decode(String input) {
        return decodeCopilot(input);
    }

    static long decodeCopilot(String input) {
        long[] dp = new long[input.length() + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= input.length(); i++) {
            int current = Integer.parseInt(input.substring(i - 1, i));
            int previous = Integer.parseInt(input.substring(i - 2, i));
            if (current > 0) {
                dp[i] += dp[i - 1];
            }
            if (previous >= 10 && previous <= 26) {
                dp[i] += dp[i - 2];
            }
        }
        return dp[input.length()];
    }
}
