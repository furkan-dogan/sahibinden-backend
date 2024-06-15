package com.sahibinden.codecase.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class BadWordUtil {
    private Set<String> badWords;

    public BadWordUtil() {
        badWords = new HashSet<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("Badwords.txt").getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsBadWord(String text) {
        for (String badWord : badWords) {
            if (text.toLowerCase().contains(badWord)) {
                return true;
            }
        }
        return false;
    }
}
