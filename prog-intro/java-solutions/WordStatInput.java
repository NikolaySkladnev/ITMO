import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class WordStatInput {
    public static void main(String[] args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
            StringBuilder wordBuilder = new StringBuilder();
            String[] words = new String[1];
            int[] wordCount = new int[1];
            int lastWordIndex = 0;
            boolean ifInArray;
            String wordString;
            String letter;
            int symbol;
            while (!((symbol = reader.read()) == -1)) {
                letter = String.valueOf((char) symbol).toLowerCase();
                if (Character.isLetter(symbol) | (Character.DASH_PUNCTUATION == Character.getType(symbol)) | Objects.equals(letter, "'")) {
                    wordBuilder.append(letter);
                } else if (!(wordBuilder.isEmpty())) {
                    wordString = String.valueOf(wordBuilder);
                    ifInArray = false;
                    for (int i = 0; i < words.length; i++) {
                        if (Objects.equals(words[i], wordString)) {
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
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));
                for (int j = 0; j < words.length; j++) {
                    writer.write(words[j] + " " + wordCount[j] + "\n");
                }
            } catch (IOException e) {
                System.out.println("Ошибка записи в файл");
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Ошибка при работе с файлами");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Ошибка при работе с файлами");
            }
        }
    }
}