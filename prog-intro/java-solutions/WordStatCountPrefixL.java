import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class WordStatCountPrefixL {
    public static void main(String[] args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
            StringBuilder wordBuilder = new StringBuilder();
            String[] words = new String[1];
            int[] wordCount = new int[1];
            String wordString;
            String letter;
            String tempStr;
            int lastWordIndex = 0;
            int symbol;
            int tempNum;
            boolean ifInArray;
            while (!((symbol = reader.read()) == -1)) {
                letter = String.valueOf((char) symbol).toLowerCase();
                if (Character.isLetter(symbol) | (Character.DASH_PUNCTUATION == Character.getType(symbol)) | Objects.equals(letter, "'")) {
                    wordBuilder.append(letter);
                } else if (wordBuilder.length() < 3) {
                    wordBuilder.setLength(0);
                } else if (!(wordBuilder.isEmpty())) {
                    wordString = String.valueOf(wordBuilder).substring(0,3);
                    ifInArray = false;
                    for (int i = 0; i < words.length; i++) {
                        if (wordString.equals(words[i])) {
                            wordCount[i]++;
                            ifInArray = true;
                            break;
                        }
                    }
                    if (!ifInArray) {
                        if (lastWordIndex == words.length) {
                            words = Arrays.copyOf(words, words.length * 2);
                            wordCount = Arrays.copyOf(wordCount, wordCount.length * 2);
                        }
                        words[lastWordIndex] = wordString;
                        wordCount[lastWordIndex] = 1;
                        lastWordIndex++;
                    }
                    wordBuilder.setLength(0);
                }
            }
            words = Arrays.copyOf(words, lastWordIndex);
            wordCount = Arrays.copyOf(wordCount, lastWordIndex);
            for (int i = 0; i < wordCount.length; i++) {
                for (int j = 0; j < wordCount.length - 1; j++) {
                    if (wordCount[j] > wordCount[j + 1]) {
                        tempNum = wordCount[j];
                        wordCount[j] = wordCount[j + 1];
                        wordCount[j + 1] = tempNum;
                        tempStr = words[j];
                        words[j] = words[j + 1];
                        words[j + 1] = tempStr;
                    }
                }
            }
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));
                for (int j = 0; j < words.length; j++) {
                        writer.write(words[j] + " " + wordCount[j]);
                        writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error when writing to a file:" + e.getMessage());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error file not found:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error when writing to a file:" + e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error when working with the file:" + e.getMessage());
            }
        }
    }
}