package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Md2Html {
    public static void main(String[] args) {
        try (Scanner scFile = new Scanner(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {

            List<String> blocksOfText = new ArrayList<>();
            StringBuilder tmp = new StringBuilder();
            String line;

            while (scFile.hasNextLine()) {
                line = scFile.nextLine();
                if (!line.isEmpty()) {
                    tmp.append(line).append(System.lineSeparator());
                } else if (!tmp.isEmpty()) {
                    blocksOfText.add(tmp.toString());
                    tmp.setLength(0);
                }
            }
            blocksOfText.add(tmp.toString());

            Parser pr = new Parser();
            blocksOfText = pr.parse(blocksOfText);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                for (String block : blocksOfText) {
                    writer.write(block);
                }
            } catch (IOException e) {
                System.err.println("Error:" + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}