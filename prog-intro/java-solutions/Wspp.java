import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Wspp {
    public static void main(String[] args) {
        try {
            OwnScanner sc = new OwnScanner(new FileInputStream(args[0]));
            try {
                StringBuilder wordBuilder = new StringBuilder();
                LinkedHashMap<String, int[]> words = new LinkedHashMap<>();
                String wordString;
                char letter;
                int[] temp;
                int positionOfWord = 0;
                while (sc.hasNextSym()) {
                    letter = sc.nextSym();
                    if ((Character.DASH_PUNCTUATION == Character.getType(letter)) | (Character.isLetter(letter) | Objects.equals(String.valueOf(letter), "'"))) {
                        wordBuilder.append(letter);
                    } else {
                        if (!wordBuilder.isEmpty()) {
                            wordString = String.valueOf(wordBuilder).toLowerCase();
                            positionOfWord++;
                            if (words.containsKey(wordString)) {
                                temp = words.get(wordString);
                                if (temp[0] == temp.length) {
                                    temp = Arrays.copyOf(temp, temp.length * 2);
                                }
                                temp[temp[0]++] = positionOfWord;
                                temp[1]++;
                                words.put(wordString, temp);
                            } else {
                                temp = new int[]{3, 1, positionOfWord};
                                words.put(wordString, temp);
                            }
                        }
                        wordBuilder.setLength(0);
                    }
                }
                for (String keyWord : words.keySet()) {
                    temp = words.get(keyWord);
                    temp = Arrays.copyOf(temp, temp[0]);
                    words.put(keyWord, temp);
                }
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))){
                    for (String keyWord : words.keySet()){
                        writer.write(keyWord + " ");
                        for (int i = 1; i < words.get(keyWord).length; i++) {
                            if (i + 1 == words.get(keyWord).length) {
                                writer.write(String.valueOf(words.get(keyWord)[i]));
                            } else {
                                writer.write(words.get(keyWord)[i] + " ");
                            }
                        }
                        writer.newLine();
                    }
                } catch (IOException e){
                    System.out.println("Ошибка при записи в файл:" + e);
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}