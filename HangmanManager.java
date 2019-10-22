//Helen Yu
//CSE 143DL with I-Miao Chien
//Homework 4

import java.util.*;

// a class for the game Evil Hangman, which seems to the user to be working as
// the normal Hangman: computer chooses a word, and the user tries to guess it 
// letter by letter. However, in this case, the computer delays picking a word 
// until it is forced to. The computer is always considering a set of 
// words that could be the answer based on the same letter pattern guessed by 
// the user.
public class HangmanManager {
   private int guessesLeft;
   private int wordLength;
   private Map<String, Set<String>> wordsPattern;
   private Set<String> words;
   private Set<Character> guesses;
   private String pattern;
   
   // pre: the given length of the word should be greater than 0, if not
   //      throws an IllegalArgumentException;
   //      the maximum number of guesses should not be less than 0, if not
   //      throws an IllegalArgumentException.
   // post: reads in and records a dictionary of words, the length of the word 
   //       chosen by the player, and the maximum guesses the player gets, 
   //       initializing the state of the game;
   //       creates a set of words with the dictionary given, which holds all the 
   //       words from the dictionary and eliminating any duplicates.
   public HangmanManager(Collection<String> dictionary, int length, int max) {
      if (length < 1 || max < 0) {
         throw new IllegalArgumentException();
      }

      guessesLeft = max;
      wordLength = length;
      wordsPattern = new TreeMap<String, Set<String>>();
      words = new TreeSet<String>();
      guesses = new TreeSet<Character>();
      
      pattern = "";
      for (int i = 0; i < wordLength; i++) {
         pattern += "- ";
      }
      pattern.substring(0,pattern.length()-1);   
         
      for(String word: dictionary) {
         if (word.length() == length) {
            words.add(word);
         }
      }
      
      
   }
   
   // post: returns a set of words that is considered by the Hangman Manager.
   public Set<String> words() {
      return words;
   }
   
   // post: returns how many guesses are left;
   //       the number of guesses left will only be deducted when a wrong guess
   //       is made.
   public int guessesLeft() {
      return guessesLeft;
   }
   
   // post: returns the current set of letters that have been guessed by the 
   //       player.
   public Set<Character> guesses() {
      return guesses;
   }
   
   // pre: the set of words should not be empty, throw an 
   //      IllegalStateException if not.
   // post: returns the current pattern with correctly-guessed characters of 
   //       the word shown and dashes inplaces of not-yet-been-guessed 
   //       characters.
   //       This operation is fast that it stores the existing pattern and
   //       returns them directly instead of calculating the pattern every time
   //       when the method is called.
   public String pattern() {
      if (words.isEmpty()) {
         throw new IllegalStateException();
      }
      
      return pattern;
   }
   
   // pre: the number of guesses left should be greater than zero, if not
   //      throws an IllegalStateException;
   //      the set of words available for the Hangman Manager to consider 
   //      should not be empty, if not throws an IllegalStateException;
   //      if the previous two condiitons are met, the current guess made
   //      should not have been guessed before, if not throw an 
   //      IllegalArgumentException.
   // post: records the current guess made by the player; with the current 
   //       guess, decides which set of words to use to continue the game;
   //       the method returns the number of occurrences of the guessed
   //       letter in the new pattern and updates the number of guesses left.
   public int record(char guess) {
      if (guessesLeft < 1 || words.isEmpty()) {
         throw new IllegalStateException();
      }
      
      if (guesses.contains(guess)) {
         throw new IllegalArgumentException();
      }
      
      guesses.add(guess);
      int numOfOccurrences = 0;
      wordsPattern.clear();
      
      for (String word: words) {
         String curPattern = "";
         
         for (int i = 0; i<word.length(); i++){
            if (word.charAt(i) == guess) {
               curPattern += guess+" ";
            }
            else if (word.charAt(i) == pattern.charAt(2*i)){
               curPattern += pattern.charAt(2*i)+" ";
            }
            else {
               curPattern += "- ";
            }
         }
         curPattern = curPattern.substring(0,curPattern.length()-1);
         if (!wordsPattern.containsKey(curPattern)) {
            wordsPattern.put(curPattern, new TreeSet<String>());
         }
         wordsPattern.get(curPattern).add(word);
      }
      
      int maxSize = 0;
      Set<String> maxFam = new TreeSet<>();
      String nextPattern = "";
      for (String patterns: wordsPattern.keySet()){
         if (wordsPattern.get(patterns).size() > maxSize) {
            maxSize = wordsPattern.get(patterns).size();
            maxFam = wordsPattern.get(patterns);
            nextPattern = patterns;
         }
         else if (wordsPattern.get(patterns).size() == maxSize) {
            boolean found = false;
            while (!found){
               for (String a: wordsPattern.keySet()) {
                  if (a.equals(patterns)) {
                     maxFam = wordsPattern.get(patterns);
                     nextPattern = patterns;
                     found = true;
                  }
                  else if (a.equals(nextPattern)){
                     found = true;
                  }
               }
            }
         }
      } 
      
      if (nextPattern.indexOf(guess)<0) {
         guessesLeft--;
      }
      else {
         for (int n = 0; n < nextPattern.length(); n++) {
            if (nextPattern.charAt(n) == guess) {
               numOfOccurrences++;
            }
         }
      }

      words = maxFam;
      pattern = nextPattern;
      
      return numOfOccurrences;
   }
