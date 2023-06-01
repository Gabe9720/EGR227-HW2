import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HangmanGame {
    private static final String DICTIONARY_FILE = "dictionary.txt";

    private HangmanManager hangmanManager;
    private Scanner consoleScanner;
    private PrintWriter outFile;

    public HangmanGame(int maxWrongGuesses) throws FileNotFoundException {
        hangmanManager = createHangmanManager(maxWrongGuesses);
        consoleScanner = new Scanner(System.in);
        outFile = createOutputFile();
    }

    public void playGame() {
        printWelcomeMessage();

        while (hangmanManager.guessesLeft() > 0 && hangmanManager.pattern().contains("-")) {
            printGameState();
            char guess = promptForGuess();
            int numOccurrences = hangmanManager.record(guess);
            processGuessResult(guess, numOccurrences);
        }

        printGameResult();
    }

    private HangmanManager createHangmanManager(int maxWrongGuesses) {
        List<String> dictionary = readDictionaryFromFile(DICTIONARY_FILE);
        return new HangmanManager(dictionary, getRandomWordLength(dictionary), maxWrongGuesses);
    }

    private List<String> readDictionaryFromFile(String filename) {
        List<String> dictionary = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                dictionary.add(fileScanner.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary file not found.");
            System.exit(1);
        }
        return dictionary;
    }

    private int getRandomWordLength(List<String> dictionary) {
        return dictionary.get((int) (Math.random() * dictionary.size())).length();
    }

    private PrintWriter createOutputFile() {
        try {
            return new PrintWriter(new FileWriter("game_results.txt"));
        } catch (IOException e) {
            System.out.println("Failed to create the output file.");
            System.exit(1);
        }
        return null;
    }

    private void deleteOutputFile() {
        File outputFile = new File("game_results.txt");
        if (outputFile.exists()) {
            try {
                Files.delete(outputFile.toPath());
            } catch (IOException e) {
                System.out.println("Failed to delete the output file.");
            }
        }
    }

    private void printWelcomeMessage() {
        System.out.println("Welcome to Hangman Game!");
        System.out.println("Guess the word by entering letters.");
        System.out.println("You have " + hangmanManager.guessesLeft() + " wrong guesses remaining.");
        System.out.println("Let's begin!\n");
    }

    private void printGameState() {
        System.out.println("Guesses left: " + hangmanManager.guessesLeft());
        System.out.println("Guessed characters: " + hangmanManager.guesses());
        System.out.println("Current pattern: " + hangmanManager.pattern());
        System.out.print("Enter your guess: ");
    }

    private char promptForGuess() {
        String input = consoleScanner.nextLine().toLowerCase();
        while (input.isEmpty() || input.length() > 1 || !Character.isLetter(input.charAt(0))) {
            System.out.print("Invalid input. Enter a single letter: ");
            input = consoleScanner.nextLine().toLowerCase();
        }
        return input.charAt(0);
    }

    private void processGuessResult(char guess, int numOccurrences) {
        outFile.println("Your guess: " + guess);
        if (numOccurrences == 0) {
            outFile.println("Sorry, there are no " + guess + "'s");
        } else if (numOccurrences == 1) {
            outFile.println("Yes, there is one " + guess);
        } else {
            outFile.println("Yes, there are " + numOccurrences + " " + guess + "'s");
        }
        outFile.println();
    }

    private void printGameResult() {
        if (hangmanManager.pattern().contains("-")) {
            System.out.println("\nYou lost! The word was: " + getRandomWord(new ArrayList<>(hangmanManager.words())));
        } else {
            System.out.println("\nCongratulations! You won!");
            System.out.println("The word was: " + hangmanManager.pattern());
        }

        outFile.println("Game result: " + (hangmanManager.pattern().contains("-") ? "Loss" : "Win"));
        outFile.println("The word was: " + getRandomWord(new ArrayList<>(hangmanManager.words())));
        outFile.close();

        deleteOutputFile();

        System.out.println("Thanks for playing Hangman Game! Goodbye!");
    }


    private String getRandomWord(List<String> words) {
        return words.get((int) (Math.random() * words.size()));
    }

    public static void main(String[] args) throws FileNotFoundException{
        try {
            System.out.print("Enter the maximum number of wrong guesses: ");
            Scanner scanner = new Scanner(System.in);
            int maxWrongGuesses = scanner.nextInt();
            HangmanGame game = new HangmanGame(maxWrongGuesses);
            game.playGame();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to initialize the game.");
        }
    }
}
