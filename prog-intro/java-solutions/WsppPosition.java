import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppPosition {
    public static void main(String[] args) {
        try {
            OwnScanner sc = new OwnScanner(new FileInputStream(args[0]));
            try {
                StringBuilder wordBuilder = new StringBuilder();
                Map<String, ArrayList<Integer>> words = new LinkedHashMap<>();
                List<Integer> countWordsInLine = new ArrayList<>();
                ArrayList<Integer> temp;
                countWordsInLine.add(1);
                String wordString;
                char letter;
                int countLine = 1;
                int currentLine = 0;
                int indexLineSep = 0;
                int positionOfWord = 0;
                while (sc.hasNextSym()) {
                    letter = sc.nextSym();
                    if (indexLineSep < System.lineSeparator().length()) {
                        if (Objects.equals(letter, System.lineSeparator().charAt(indexLineSep))) {
                            indexLineSep++;
                        } else {
                            indexLineSep = 0;
                        }
                    } else {
                        indexLineSep = 0;
                        positionOfWord = 0;
                        countWordsInLine.add(1);
                        countLine++;
                        currentLine++;
                    }
                    if (Character.DASH_PUNCTUATION == Character.getType(letter) | Character.isLetter(letter) | letter == '\'') {
                        wordBuilder.append(letter);
                    } else {
                        if (!wordBuilder.isEmpty()) {
                            wordString = String.valueOf(wordBuilder).toLowerCase();
                            countWordsInLine.set(currentLine, countWordsInLine.get(currentLine) + 1);
                            positionOfWord++;
                            temp = words.getOrDefault(wordString, new ArrayList<>(List.of(0)));
                            temp.add(countLine);
                            temp.add(positionOfWord);
                            temp.set(0, temp.get(0) + 1);
                            words.put(wordString, temp);
                        }
                        wordBuilder.setLength(0);
                    }
                }
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));
                    try {
                        for (String keyWord : words.keySet()) {
                            writer.write(keyWord + " " + words.get(keyWord).get(0) + " ");
                            for (int i = 2; i <= words.get(keyWord).size(); i = i + 2) {
                                if (i == (words.get(keyWord).size() - 1)) {
                                    writer.write(words.get(keyWord).get(i - 1) + ":" + (countWordsInLine.get(words.get(keyWord).get(i - 1) - 1) - words.get(keyWord).get(i)));
                                } else {
                                    writer.write(words.get(keyWord).get(i - 1) + ":" + (countWordsInLine.get(words.get(keyWord).get(i - 1) - 1) - words.get(keyWord).get(i)) + " ");
                                }
                            }
                            writer.newLine();
                        }
                    } finally {
                        writer.close();
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при записи в файл:" + e);
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении из файла:" + e);
        }
    }
}