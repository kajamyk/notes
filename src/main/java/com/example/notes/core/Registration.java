package com.example.notes.core;

import com.example.notes.infra.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class Registration {
    @Autowired
    UserService userService;

    public boolean arePasswordsMatching(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }

    public boolean isPasswordEntropySufficient(String password) {
        return shannonEntropy(password) > 4;
    }

    public boolean isPasswordLengthSufficient(String password) {
        return password.length() > 8;
    }

    public boolean isUserNameCorrect(String userName) {
        for (int i = 0; i < userName.length(); i++) {
            if (!isCharCorrect(userName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isCharCorrect(char c) {
        if (c == '_') {
            return true;
        }
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        return c >= '0' && c <= '9';
    }

    private double shannonEntropy(String password) {
        double entropy = 0;
        ArrayList<CharFreq> freqs = new ArrayList<>();
        String[] split = password.split("");
        for (String s : split) {
            boolean flag = true;
            for (CharFreq cf : freqs) {
                if (cf.s.equals(s)) {
                    flag = false;
                    cf.count++;
                    break;
                }
                flag = true;
            }
            if (flag) {
                freqs.add(new CharFreq(s));
            }
        }

        for (CharFreq cf : freqs) {
            int freq = cf.count;
            if (freq == 0) {
                continue;
            }

            double c = (double) freq / password.length();
            entropy -= log2(Math.pow(c, c));
        }
        return entropy;
    }

    private static class CharFreq {
        public final String s;
        public int count = 1;

        public CharFreq(String in) {
            this.s = in;
        }
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
