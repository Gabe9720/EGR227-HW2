import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HangmanManager {
    private Set<String> answerSet;
    private int targetWordLength;
    private int maxWrongGuesses;
    private SortedSet<Character> guessedChars;
    private String pattern;

    public HangmanManager(Collection<String> dictionary, int length, int max) {
        // Validate the input parameters
        if (length < 1 || max < 0) {
            throw new IllegalArgumentException("Invalid input: length and max must be positive.");
        }

        // Initialize the state of the game
        this.answerSet = new HashSet<>();
        for (String word : dictionary) {
            if (word.length() == length) {
                answerSet.add(word);
            }
        }

        this.targetWordLength = length;
        this.maxWrongGuesses = max;
        this.guessedChars = new TreeSet<>();
        this.pattern = "-";
        for (int i = 1; i < length; i++) {
            pattern += " -";
        }
    }

    public Set<String> words() {
        return answerSet;
    }

    public int guessesLeft() {
        return maxWrongGuesses;
    }

    public SortedSet<Character> guesses() {
        return guessedChars;
    }

    public String pattern() {
        if (answerSet.isEmpty()) {
            throw new IllegalStateException("No words in the dictionary.");
        }
        return pattern;
    }

    public int record(char guess) {
        if (answerSet.isEmpty()) {
            throw new IllegalStateException("The set of possible answers is empty.");
        } else if (guessesLeft() < 1) {
            throw new IllegalStateException("No guesses remaining.");
        } else if (guessedChars.contains(guess)) {
            throw new IllegalArgumentException(guess + " has already been guessed.");
        }

        guessedChars.add(guess);

        Map<String, Set<String>> answerMap = buildAnswerMap(guess);
        int numOccurrences = updateAnswerInfo(answerMap, guess);

        if (numOccurrences == 0) {
            decreaseGuessesLeft();
        }
        return numOccurrences;
    }

    private String buildKey(String word, char guess) {
        StringBuilder sb = new StringBuilder(pattern);
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                sb.setCharAt(i * 2, guess);
            }
        }
        return sb.toString();
    }

    private Map<String, Set<String>> buildAnswerMap(char guess) {
        Map<String, Set<String>> answerMap = new TreeMap<>();
        for (String word : answerSet) {
            String key = buildKey(word, guess);
            Set<String> value;
            if (!answerMap.containsKey(key)) {
                value = new TreeSet<>();
            } else {
                value = answerMap.get(key);
            }
            value.add(word);
            answerMap.put(key, value);
        }
        return answerMap;
    }

    private int updateAnswerInfo(Map<String, Set<String>> answerMap, char guess) {
        String bestKey = findKey(answerMap);
        answerSet = answerMap.get(bestKey);
        int numOccurrences = calculateOccurrences(pattern, bestKey);
        pattern = bestKey;
        return numOccurrences;
    }

    private static int calculateOccurrences(String str1, String str2) {
        int count = 0;
        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    private String findKey(Map<String, Set<String>> answerMap) {
        String Key = answerMap.keySet().iterator().next();
        for (String key : answerMap.keySet()) {
            if (answerMap.get(key).size() > answerMap.get(Key).size()) {
                Key = key;
            }
        }
        return Key;
    }

    private void decreaseGuessesLeft() {
        maxWrongGuesses--;
    }
}

