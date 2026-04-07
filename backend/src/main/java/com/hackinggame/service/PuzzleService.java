package com.hackinggame.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PuzzleService {
    
    private final Random random = new Random();
    
    // Record to hold puzzle data
    public record Puzzle(String type, String question, String answer, int reward, String hint) {}
    
    /**
     * Generates a random puzzle based on difficulty level
     * @param difficulty 1 = Easy, 2 = Medium, 3 = Hard
     * @return Puzzle object with question, answer, and reward
     */
    public Puzzle generatePuzzle(int difficulty) {
        String[] types = {"CAESAR", "BINARY", "PATTERN", "LOGIC", "VIGENERE"};
        String type = types[random.nextInt(types.length)];
        
        return switch (type) {
            case "CAESAR" -> generateCaesarPuzzle(difficulty);
            case "BINARY" -> generateBinaryPuzzle(difficulty);
            case "PATTERN" -> generatePatternPuzzle(difficulty);
            case "LOGIC" -> generateLogicPuzzle(difficulty);
            case "VIGENERE" -> generateVigenerePuzzle(difficulty);
            default -> generateCaesarPuzzle(difficulty);
        };
    }
    
    /**
     * CAESAR CIPHER PUZZLE
     * Shift each letter by a certain number
     */
    private Puzzle generateCaesarPuzzle(int difficulty) {
        int shift;
        String original;
        String hint;
        int reward;
        
        switch (difficulty) {
            case 1: // Easy
                shift = 3;
                original = "HELLO";
                hint = "Hint: Caesar cipher with shift of " + shift;
                reward = 50;
                break;
            case 2: // Medium
                shift = 5;
                original = "CYBER";
                hint = "Hint: Caesar cipher with shift of " + shift;
                reward = 75;
                break;
            default: // Hard
                shift = 7;
                original = "ENCRYPTION";
                hint = "Hint: Caesar cipher with shift of " + shift;
                reward = 100;
                break;
        }
        
        String encrypted = caesarEncrypt(original, shift);
        String question = "🔐 CAESAR CIPHER CHALLENGE\n\n" +
                         "Decrypt this message:\n" +
                         "┌─────────────────────────────────┐\n" +
                         "│  " + encrypted + "  │\n" +
                         "└─────────────────────────────────┘\n\n" +
                         hint;
        
        return new Puzzle("CAESAR", question, original.toUpperCase(), reward, hint);
    }
    
    /**
     * BINARY CODE PUZZLE
     * Convert binary to text
     */
    private Puzzle generateBinaryPuzzle(int difficulty) {
        String word;
        String hint;
        int reward;
        
        switch (difficulty) {
            case 1: // Easy
                word = "HACK";
                hint = "Hint: 4 characters, each is 8 bits (01001000 = H)";
                reward = 60;
                break;
            case 2: // Medium
                word = "CODE";
                hint = "Hint: 4 characters, first letter C = 01000011";
                reward = 85;
                break;
            default: // Hard
                word = "SECURE";
                hint = "Hint: 6 characters, all uppercase ASCII";
                reward = 110;
                break;
        }
        
        String binary = word.chars()
            .mapToObj(c -> String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'))
            .collect(Collectors.joining(" "));
        
        String question = "💻 BINARY CODE BREAKING\n\n" +
                         "Convert this binary code to text:\n" +
                         "┌─────────────────────────────────────────┐\n" +
                         "│  " + binary + "  │\n" +
                         "└─────────────────────────────────────────┘\n\n" +
                         hint;
        
        return new Puzzle("BINARY", question, word.toUpperCase(), reward, hint);
    }
    
    /**
     * PATTERN RECOGNITION PUZZLE
     * Find the next number in sequence
     */
    private Puzzle generatePatternPuzzle(int difficulty) {
        String question;
        String answer;
        String hint;
        int reward;
        
        switch (difficulty) {
            case 1: // Easy - Arithmetic sequence
                question = "2, 4, 6, 8, ?";
                answer = "10";
                hint = "Hint: Add 2 each time";
                reward = 55;
                break;
            case 2: // Medium - Geometric sequence
                question = "3, 6, 12, 24, ?";
                answer = "48";
                hint = "Hint: Multiply by 2 each time";
                reward = 80;
                break;
            default: // Hard - Fibonacci sequence
                question = "1, 1, 2, 3, 5, 8, ?";
                answer = "13";
                hint = "Hint: Fibonacci sequence (add previous two numbers)";
                reward = 105;
                break;
        }
        
        String fullQuestion = "🧩 PATTERN RECOGNITION\n\n" +
                             "Find the next number in the sequence:\n" +
                             "┌─────────────────────────────────┐\n" +
                             "│  " + question + "  │\n" +
                             "└─────────────────────────────────┘\n\n" +
                             hint;
        
        return new Puzzle("PATTERN", fullQuestion, answer, reward, hint);
    }
    
    /**
     * LOGIC RIDDLE PUZZLE
     * Solve brain teasers
     */
    private Puzzle generateLogicPuzzle(int difficulty) {
        String question;
        String answer;
        String hint;
        int reward;
        
        switch (difficulty) {
            case 1: // Easy
                question = "I speak without a mouth and hear without ears.\n" +
                          "I have no body, but I come alive with wind.\n" +
                          "What am I?";
                answer = "ECHO";
                hint = "Hint: Think about sound reflecting";
                reward = 65;
                break;
            case 2: // Medium
                question = "The more you take, the more you leave behind.\n" +
                          "What am I?";
                answer = "FOOTSTEPS";
                hint = "Hint: Think about walking";
                reward = 90;
                break;
            default: // Hard
                question = "I have keys but no locks.\n" +
                          "I have space but no room.\n" +
                          "What am I?";
                answer = "KEYBOARD";
                hint = "Hint: Used for typing";
                reward = 115;
                break;
        }
        
        String fullQuestion = "🤔 LOGIC RIDDLE\n\n" +
                             "Solve this riddle:\n" +
                             "┌─────────────────────────────────┐\n" +
                             "│  " + question + "  │\n" +
                             "└─────────────────────────────────┘\n\n" +
                             hint;
        
        return new Puzzle("LOGIC", fullQuestion, answer.toUpperCase(), reward, hint);
    }
    
    /**
     * VIGENÈRE CIPHER PUZZLE
     * Decrypt using keyword
     */
    private Puzzle generateVigenerePuzzle(int difficulty) {
        String keyword;
        String message;
        String hint;
        int reward;
        
        switch (difficulty) {
            case 1: // Easy
                keyword = "KEY";
                message = "ATTACK";
                hint = "Hint: Keyword is 'KEY'";
                reward = 70;
                break;
            case 2: // Medium
                keyword = "HACK";
                message = "CYBER";
                hint = "Hint: Keyword is 'HACK'";
                reward = 95;
                break;
            default: // Hard
                keyword = "SECURITY";
                message = "ENCRYPTION";
                hint = "Hint: Keyword is 'SECURITY'";
                reward = 120;
                break;
        }
        
        String encrypted = vigenereEncrypt(message, keyword);
        
        String question = "🔑 VIGENÈRE CIPHER CHALLENGE\n\n" +
                         "Keyword: " + keyword + "\n\n" +
                         "Decrypt this message:\n" +
                         "┌─────────────────────────────────┐\n" +
                         "│  " + encrypted + "  │\n" +
                         "└─────────────────────────────────┘\n\n" +
                         hint;
        
        return new Puzzle("VIGENERE", question, message.toUpperCase(), reward, hint);
    }
    
    /**
     * Helper method for Caesar cipher encryption
     */
    private String caesarEncrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char) ((c - base + shift) % 26 + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Helper method for Caesar cipher decryption
     */
    public String caesarDecrypt(String text, int shift) {
        return caesarEncrypt(text, 26 - (shift % 26));
    }
    
    /**
     * Helper method for Vigenère cipher encryption
     */
    private String vigenereEncrypt(String text, String keyword) {
        StringBuilder result = new StringBuilder();
        keyword = keyword.toUpperCase();
        int keywordIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = keyword.charAt(keywordIndex % keyword.length()) - 'A';
                result.append((char) ((c - base + shift) % 26 + base));
                keywordIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Helper method for Vigenère cipher decryption
     */
    public String vigenereDecrypt(String text, String keyword) {
        StringBuilder result = new StringBuilder();
        keyword = keyword.toUpperCase();
        int keywordIndex = 0;
        
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = keyword.charAt(keywordIndex % keyword.length()) - 'A';
                result.append((char) ((c - base - shift + 26) % 26 + base));
                keywordIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * Verify if the answer is correct
     */
    public boolean verifyAnswer(Puzzle puzzle, String answer) {
        if (puzzle == null || answer == null) return false;
        return puzzle.answer().equalsIgnoreCase(answer.trim());
    }
    
    /**
     * Generate a custom puzzle for attacking another player
     */
    public Puzzle generateAttackPuzzle(int victimVaultLevel) {
        int difficulty = Math.min(3, victimVaultLevel);
        return generatePuzzle(difficulty);
    }
    
    /**
     * Get all puzzle types
     */
    public List<String> getPuzzleTypes() {
        return Arrays.asList("CAESAR", "BINARY", "PATTERN", "LOGIC", "VIGENERE");
    }
    
    /**
     * Get puzzle statistics
     */
    public Map<String, Object> getPuzzleStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTypes", 5);
        stats.put("difficultyLevels", 3);
        stats.put("maxReward", 120);
        stats.put("minReward", 50);
        return stats;
    }
    
    /**
     * Generate a hint for the current puzzle
     */
    public String generateHint(Puzzle puzzle) {
        if (puzzle == null) return "No puzzle available";
        
        return switch (puzzle.type()) {
            case "CAESAR" -> "Try shifting each letter backward by the shift amount. " + puzzle.hint();
            case "BINARY" -> "Convert each 8-bit binary group to its ASCII character. " + puzzle.hint();
            case "PATTERN" -> "Look for the mathematical relationship between numbers. " + puzzle.hint();
            case "LOGIC" -> "Think metaphorically about the description. " + puzzle.hint();
            case "VIGENERE" -> "Use the keyword to reverse the encryption. " + puzzle.hint();
            default -> puzzle.hint();
        };
    }
    
    /**
     * Bonus puzzle for high-level players (Level 5+)
     */
    public Puzzle generateBonusPuzzle() {
        String[] bonusPuzzles = {
            "What is the MD5 hash of 'password'? (Answer in hex)",
            "Decode Base64: SGV5LCB5b3UncmUgYSByZWFsIGhhY2tlciE=",
            "What port does HTTPS use?",
            "What does SQL stand for?"
        };
        
        String[] bonusAnswers = {
            "5F4DCC3B5AA765D61D8327DEB882CF99",
            "Hey, you're a real hacker!",
            "443",
            "STRUCTURED QUERY LANGUAGE"
        };
        
        int index = random.nextInt(bonusPuzzles.length);
        
        String question = "🌟 BONUS CHALLENGE 🌟\n\n" +
                         "Show your elite hacking skills!\n\n" +
                         bonusPuzzles[index];
        
        return new Puzzle("BONUS", question, bonusAnswers[index], 200, "Elite hacker challenge!");
    }
}