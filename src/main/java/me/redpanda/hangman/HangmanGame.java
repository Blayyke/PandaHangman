package me.redpanda.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by blake on 2/25/2017.
 */
public class HangmanGame {
    private final String[] randomWords;
    private boolean isRunning = false;
    private String word;
    private int strikes = 0;
    private List<Character> guessedCharacters;
    private List<Character> charsInWord;

    /**
     * @param words An array of {@link String} words to choose the random word from when none are specified
     */
    public HangmanGame(String[] words) {
        if (words == null) throw new IllegalArgumentException("randomWords cannot be null!");
        if (words.length == 0) throw new IllegalArgumentException("randomWords must have atleast one string!");

        for (int i = 0; i < words.length; i++)
            words[i] = words[i].toLowerCase();

        this.randomWords = words;
    }

    /**
     * Start a game with a random word out of the list passed in the constructor
     *
     * @throws IllegalStateException if the game is already running
     */
    public void startGame() {
        startGame(null);
    }

    /**
     * Check if the game is currently running. This should be checked before calling most functions to prevent from throwing an {@link IllegalStateException}
     *
     * @return true if the game is started, false if otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Start a game with the specified word, or {@code null} for a random word.
     *
     * @param word The word to start the game with, or {@code null}
     * @throws IllegalStateException if the game is already running
     */
    public void startGame(String word) {
        if (isRunning())
            throw new IllegalStateException("Game is already running! Stop it first with HangmanGame#stopGame()");

        this.isRunning = true;
        guessedCharacters = new ArrayList<>();
        charsInWord = new ArrayList<>();
        strikes = 0;
        resetGame(word);
    }

    /**
     * Stop the current game.
     *
     * @throws IllegalStateException if the game is already stopped.
     */
    public void stopGame() {
        if (!isRunning())
            throw new IllegalStateException("Game is not running! Start it first with HangmanGame#startGame()");

        this.isRunning = false;
        this.guessedCharacters = null;
        this.charsInWord = null;
        this.strikes = 0;
        this.word = null;
    }

    /**
     * @param c The character to try and guess.
     * @return True if the character is in the word, false if otherwise.
     * @throws IllegalArgumentException if the character is already guessed.
     */
    public boolean setCharacterGuessed(char c) {
        checkState();

        String s = String.valueOf(c);
        c = s.toLowerCase().charAt(0);

        if (isGuessed(c))
            throw new IllegalArgumentException("Character \'" + c + "\' is already guessed!");

        if (!charsInWord.contains(c)) {
            strikes++;
            guessedCharacters.add(c);
            return false;
        }

        guessedCharacters.add(c);
        return true;
    }

    /**
     * Get the amount of incorrect guesses made. Can be used to implement game cancelling when X incorrect guesses are exceeded.
     *
     * @return The amount of incorrect letter guesses.
     * @throws IllegalStateException if the game is not currently running
     */
    public int getStrikes() {
        checkState();
        return strikes;
    }

    /**
     * Check whether or not all the letters in the word have been guessed.
     *
     * @return True if the word has been found, false if otherwise.
     * @throws IllegalStateException if the game is not currently running
     */
    public boolean isWordGuessed() {
        checkState();
        for (char c : charsInWord) if (!isGuessed(c)) return false;

        return true;
    }

    private void checkState() {
        if (!isRunning())
            throw new IllegalStateException("The game is not currently running!");
    }

    /**
     * Check if a specific character has been guessed already.
     *
     * @param c The character to check.
     * @return True if the character has already been guessed, false if otherwise.
     * @throws IllegalStateException if the game is not currently running
     */
    public boolean isGuessed(char c) {
        checkState();

        String s = String.valueOf(c);
        c = s.toLowerCase().charAt(0);
        return guessedCharacters.contains(c);
    }

    /**
     * Get the word which the game has been started with.
     *
     * @return The word being used in the current game.
     * @throws IllegalStateException if the game is not currently running
     */
    public String getWord() {
        checkState();
        return word;
    }

    private void setWord(String word) {
        if (!guessedCharacters.isEmpty())
            throw new IllegalStateException("Cannot set word whilst the game is in progress!");

        word = word.toLowerCase();

        for (char c : word.toCharArray())
            charsInWord.add(c);
        this.word = word;
    }

    private void setRandomWord() {
        setWord(randomWords[new Random().nextInt(randomWords.length)]);
    }

    /**
     * Return a list of all the guessed characters.
     *
     * @return A {@link List} of {@link Character}s which have been guessed
     * @throws IllegalStateException if the game is not currently running
     */
    public List<Character> getGuessedCharacters() {
        checkState();
        return guessedCharacters.subList(0, guessedCharacters.size());
    }

    private void resetGame(String word) {
        if (word == null) setRandomWord();
        else setWord(word);
    }

    /**
     * Get the progress string of the current game.
     *
     * @return The progress string, separated by ' '. Eg: (word is test) t _ s t, when t and s have been correctly guessed.
     * @throws IllegalStateException if the game is not currently running
     */
    public String getProgressString() {
        return getProgressString(" ");
    }

    /**
     * Get the progress string of the current game using a specified character separator.
     *
     * @param separator What to separate each character with.
     * @return The progress string. Eg: (word is test) t _ s t, when t and s have been correctly guessed.
     */
    public String getProgressString(String separator) {
        checkState();
        String underscores = "";
        for (Character charInWord : word.toCharArray()) {
            if (isGuessed(charInWord))
                underscores += charInWord + separator;
            else
                underscores += "_" + separator;
        }

        return underscores;
    }
}