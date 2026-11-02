
package com.collegemate.backend.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimilarityEngine {

    public double calculateSimilarity(
            List<String> question1,
            List<String> question2) {

        if (question1 == null || question2 == null
                || question1.isEmpty() || question2.isEmpty()) {
            return 0.0;
        }

        Set<String> set1 = new HashSet<>(question1);
        Set<String> set2 = new HashSet<>(question2);

        int matches = 0;

        for (String word1 : set1) {

            for (String word2 : set2) {

                if (word1.equals(word2)
                        || wordSimilarity(word1, word2) >= 0.70) {

                    matches++;
                    break;
                }
            }
        }

        int totalWords = Math.max(set1.size(), set2.size());

        return (double) matches / totalWords;
    }

    private double wordSimilarity(String word1, String word2) {

        int distance = levenshteinDistance(word1, word2);

        int maxLength = Math.max(word1.length(), word2.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return 1.0 - (double) distance / maxLength;
    }

    private int levenshteinDistance(String s1, String s2) {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {

            for (int j = 1; j <= s2.length(); j++) {

                int cost = s1.charAt(i - 1) == s2.charAt(j - 1)
                        ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
