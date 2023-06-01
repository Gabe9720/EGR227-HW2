import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.*;     // JUnit tools

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;     // Collections
import java.io.*;       // File access

/**
 * Contains testing code for the public functions of the class HangmanManager
 * @Author Christopher Nugent, Mikyung Han for EGR221 Solutions
 * @Version Jan 2017 (Updated on 1/29/2017)
 */
public class HangmanGameTest {

    private static final String EXPECTED_TEMPLATE =
            "C:\\Users\\jwoma\\IdeaProjects\\EGR227-HW2\\hw-2-starter-main\\expected_output\\%s\\%s";
    private static final String ACTUAL_TEMPLATE =
            "C:\\Users\\jwoma\\IdeaProjects\\EGR227-HW2\\game_results.txt";
    private static final String COMMON_FILE_NAME_TEMPLATE =
            "log%d-%s-%s.txt";

    private static void testAgainstFiles(int testId, int dicId, String dicStr, String bStr) {
        String commonFileName = String.format(COMMON_FILE_NAME_TEMPLATE,
                testId, dicStr, bStr);
        compareFiles(String.format(EXPECTED_TEMPLATE, "dictionary"+dicId, commonFileName),
                String.format(ACTUAL_TEMPLATE, commonFileName));
    }

    /**
     * Tests log1-dic2-true.txt
     */
    @Test
    public void testHangmanGameOutput() throws IOException {
        // Set up the test
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Create an instance of HangmanGame and run the game
        Game("4 7 e o t f c n d h g");

        // Restore the standard output
        System.setOut(System.out);

        // Read the expected output from a file
        String expectedOutput = new String(Files.readAllBytes(Paths.get("C:\\Users\\jwoma\\IdeaProjects\\EGR227-HW2\\hw-2-starter-main\\expected_output\\dictionary2\\log1-dic2-true.txt")));

        // Get the actual output from the HangmanGame
        String actualOutput = outputStream.toString();

        // Compare the expected and actual output
        assertEquals(expectedOutput, actualOutput);
    }
    private static void compareFiles(
            String expectedOutputFilePath,
            String actualOutputFilePath) {
        String expected = dumpFileContentsToString(expectedOutputFilePath, true);
        try {
            HangmanGame.main(null);
        }catch(FileNotFoundException e){
            Assert.fail("FileNotFound exception should not happen");
        }catch(IOException e){
            Assert.fail("IOException should not happen");
        }

        String actual = dumpFileContentsToString(actualOutputFilePath, false);
        Assert.assertEquals("output must match expected value", expected, actual);
    }


    private static String dumpFileContentsToString(String filePath, boolean cleanLF) {
        try {
            String str = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            if(cleanLF) {
                return str.replaceAll("\n", System.lineSeparator());
            }else{
                return str;
            }
        } catch (IOException e) {
            Assert.fail("Could not load file: " + filePath);
            return null;
        }
    }


    /* Loads the words in dictionary2.txt, all of which have a length of 4 */
    private Set<String> getDictionary() {
        try {
            Scanner fileScanner = new Scanner(new File("C:\\Users\\jwoma\\IdeaProjects\\EGR227-HW2\\hw-2-starter-main\\dictionary2.txt"));
            Set<String> dictionary = new HashSet<>();
            while(fileScanner.hasNext()) {
                dictionary.add(fileScanner.next());
            }
            return dictionary;
        } catch(FileNotFoundException e) {
            Assert.fail("Something went wrong.");      //Something went wrong
        }
        /* Should never be reached. */
        return new HashSet<>();
    }

    /*
     * Helper for constructorTest()
     * Asserts failure if running the constructor with the given parameters throws an exception.
     */
    private void constructorTestHelper(Set<String> dictionary, int length, int max) {
        try {
            new HangmanManager(dictionary, length, max);
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    /*
     * Helper for constructorFailTest()
     * Asserts failure if running the constructor with the given parameters does not throw an exception.
     */
    private void constructorFailHelper(Set<String> d, int l, int m) {
        try {
            new HangmanManager(d, l, m);
            Assert.fail("An exception was not thrown.");
        } catch (RuntimeException e) {/* Intentionally blank */}
    }

    /* Creates HangmanManager with only "zzzz" in the dictionary */
    private HangmanManager getDummyManager(int max) {
        Set<String> d = new HashSet<>();
        d.add("zzzz");
        return new HangmanManager(d, 4, max);
    }

    /* Tests successful runs of the constructor */
    @Test
    public void constructorTest() {
        Set<String> d = getDictionary();
        constructorTestHelper(d, 4, 2);
        constructorTestHelper(d, 4, 1);
        constructorTestHelper(d, 4, 9999);
        constructorTestHelper(d, 1, 0);
    }

    /* Tests unsuccessful runs of the constructor */
    @Test
    public void constructorFailTest() {
        Set<String> d = getDictionary();
        constructorFailHelper(d, 0, 0);         // length must be 1 or greater
        constructorFailHelper(d, 4, -1);        // max guesses must be 0 or greater
        constructorFailHelper(d, -1, 3);        // length must be 1 or greater
        constructorFailHelper(null, 10, 10);    // nullPointerException should be thrown
    }

    /* Checks for deep copy of the game dictionary with the words() method */
    @Test
    public void wordsTest() {
        Set<String> d = getDictionary();
        HangmanManager h = new HangmanManager(d, 4, 10);
        Assert.assertNotSame("Deep copy was not made.", h.words(), d);
        d.add("asdf");
        Assert.assertNotSame("Deep copy was not made.", h.words(), d);
    }

    /* Checks that the number of guesses remaining decreases correctly */
    @Test
    public void guessesLeftTest() {
        HangmanManager h = getDummyManager(10);
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 10);
        h.record('a');
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 9);
        h.record('b');
        h.record('c');
        Assert.assertEquals("Guesses left did not match the expected value.", h.guessesLeft(), 7);
    }

    /* Checks that guessed characters are added to the list of guessed characters, and that a deep copy is made */
    @Test
    public void guessesTest() {
        HangmanManager h = getDummyManager(10);
        h.record('a');
        h.record('b');
        h.record('c');
        h.record('d');
        h.record('e');
        Set<Character> set = h.guesses();
        Assert.assertNotSame("A deep copy was not made.", set, h.guesses());
        if (set.size() != 5 || !set.contains('a') || !set.contains('b') || !set.contains('c')
                || !set.contains('d') || !set.contains('e')) {
            Assert.fail("Content of guesses is not the letters guessed.");
        }
    }

    /* Checks that the content of the current dictionary matches what is expected as letters are guessed */
    @Test
    public void recordTest() {
        HangmanManager h = new HangmanManager(getDictionary(), 4, 10);
        h.record('e');
        Set<String> s = new HashSet<>();
        s.add("ally");
        s.add("cool");
        s.add("good");
        Assert.assertEquals("Content of remaining words did not match expected value.", h.words(), s);
        h.record('o');
        s.remove("ally");
        Assert.assertEquals("Content of remaining words did not match expected value.", h.words(), s);
    }

    /* Checks that exceptions are thrown when a letter is reguessed and when no guesses remain */
    @Test
    public void recordFailTest() {
        HangmanManager h = getDummyManager(2);
        h.record('a');          // 1 guess left
        try {                   // a already guessed, throw exception
            h.record('a');
            Assert.fail();
        } catch (IllegalArgumentException e) {}
        h.record('b');          // 0 guesses left
        try {
            h.record('c');
            Assert.fail();
        } catch (IllegalStateException e) {}
    }

    /* Checks that the pattern matches what is expected */
    @Test
    public void patternTest() {
        HangmanManager h = new HangmanManager(getDictionary(), 4, 10);
        Assert.assertEquals(h.pattern(), "- - - -");
        int numOccur = h.record('e');
        Assert.assertEquals(h.pattern(), "- - - -");
        Assert.assertEquals(0, numOccur);
        Assert.assertEquals("With wrong guess, guessesLeft should decrease by 1", 9, h.guessesLeft());

        int numOccur2 = h.record('o');
        Assert.assertEquals(h.pattern(), "- o o -");
        Assert.assertEquals(2, numOccur2);
        Assert.assertEquals("With correct guess, guessesLeft shouldn't decrease" , 9, h.guessesLeft());
    }

    /* Checks that the pattern matches what is expected */
    @Test
    public void patternNegativeTest() {
        HangmanManager h = new HangmanManager(new LinkedList<>(), 4, 10);
        try{
            h.pattern();
            Assert.fail("Must throw IllegalStateException");
        }catch(IllegalStateException e){
        }
    }
    public static void Game(String necessary) {
        try {
            // Compile the HangmanGame code
            ProcessBuilder compilerProcessBuilder = new ProcessBuilder("javac", "C:\\Users\\jwoma\\IdeaProjects\\EGR227-HW2\\hw-2-starter-main\\src\\HangmanGame.java");
            Process compilerProcess = compilerProcessBuilder.start();
            int compilerExitCode = compilerProcess.waitFor();

            if (compilerExitCode == 0) {
                // Run the HangmanGame code
                ProcessBuilder gameProcessBuilder = new ProcessBuilder("java", "HangmanGame");
                Process gameProcess = gameProcessBuilder.start();

                // Provide input to the HangmanGame
                BufferedWriter gameInputWriter = new BufferedWriter(new OutputStreamWriter(gameProcess.getOutputStream()));
                gameInputWriter.write(necessary);
                gameInputWriter.flush();

                // Read the output of the HangmanGame
                BufferedReader gameOutputReader = new BufferedReader(new InputStreamReader(gameProcess.getInputStream()));
                String line;
                while ((line = gameOutputReader.readLine()) != null) {
                    System.out.println(line);
                }

                // Wait for the HangmanGame process to finish
                int gameExitCode = gameProcess.waitFor();
                System.out.println("HangmanGame exited with code: " + gameExitCode);
            } else {
                System.out.println("Failed to compile HangmanGame.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
