package md2html;

import java.util.*;

public class Parser {
    private Map<String, Boolean> symoneGate = new HashMap<>();
    Map<String, String> toHTML = new HashMap<>();
    Map<String, Integer> symCount = new HashMap<>();
    Map<String, String> specialSym = new HashMap<>();

    public Parser() {
        symoneGate.put("*", false);
        symoneGate.put("_", false);
        symoneGate.put("`", false);
        symoneGate.put("**", false);
        symoneGate.put("__", false);
        symoneGate.put("--", false);
        symoneGate.put("''", false);

        toHTML.put("*", "em");
        toHTML.put("_", "em");
        toHTML.put("`", "code");
        toHTML.put("**", "strong");
        toHTML.put("__", "strong");
        toHTML.put("--", "s");
        toHTML.put("''", "q");

        symCount.put("*", 0);
        symCount.put("_", 0);
        symCount.put("`", 0);
        symCount.put("**", 0);
        symCount.put("__", 0);
        symCount.put("--", 0);
        symCount.put("''", 0);

        specialSym.put("<", "&lt;");
        specialSym.put(">", "&gt;");
        specialSym.put("&", "&amp;");
    }

    private String parseDouble(String text) {

        StringBuilder output = new StringBuilder();
        for (int i = 1; i < text.length(); i++) {
            if (Objects.equals(text.charAt(i - 1), '\\')) {
                output.append(text.charAt(i));
                i++;
            } else if (toHTML.containsKey(text.substring(i - 1, i + 1))) {
                if (symCount.get(text.substring(i - 1, i + 1)) == 1) {
                    output.append(text, i - 1, i + 1);
                    i++;
                } else if (symoneGate.get(text.substring(i - 1, i + 1))) {
                    output.append("</").append(toHTML.get(text.substring(i - 1, i + 1))).append(">");
                    symoneGate.replace(text.substring(i - 1, i + 1), false);
                    i++;
                } else {
                    output.append("<").append(toHTML.get(text.substring(i - 1, i + 1))).append(">");
                    symoneGate.replace(text.substring(i - 1, i + 1), true);
                    i++;
                }
            } else {
                output.append(text.charAt(i - 1));
            }
        }
        output.append(">").append(System.lineSeparator());
        return parseOne(output.toString());
    }

    public String parseOne(String text) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (Objects.equals(text.charAt(i), '\\')) {
                if (i + 1 != text.length()) {
                    output.append(text.charAt(i));
                }
                i++;
            } else if (toHTML.containsKey(String.valueOf(text.charAt(i)))) {
                if (symCount.get(String.valueOf(text.charAt(i))) == 1) {
                    output.append(text.charAt(i));
                } else {
                    if (symoneGate.get(String.valueOf(text.charAt(i)))) {
                        output.append("</").append(toHTML.get(String.valueOf(text.charAt(i)))).append(">");
                        symoneGate.replace(String.valueOf(text.charAt(i)), false);
                    } else {
                        output.append("<").append(toHTML.get(String.valueOf(text.charAt(i)))).append(">");
                        symoneGate.replace(String.valueOf(text.charAt(i)), true);
                    }
                }
            } else {
                output.append(text.charAt(i));
            }
        }
        symCount.put("*", 0);
        symCount.put("_", 0);
        symCount.put("`", 0);
        symCount.put("**", 0);
        symCount.put("__", 0);
        symCount.put("--", 0);
        symCount.put("''", 0);
        return output.toString();
    }


    public List<String> parse(List<String> blocksToParse) {
        for (int I = 0; I < blocksToParse.size(); I++) {
            StringBuilder parsedText = new StringBuilder();
            String blockToParse = blocksToParse.get(I);
            boolean paragraph = false;
            int hashCount = 0;

            char c = blockToParse.charAt(hashCount);
            if (Objects.equals(c, '#')) {
                while (Objects.equals(c, '#')) {
                    hashCount++;
                }

                if (Character.isWhitespace(c)) {
                    parsedText.append("<h").append(hashCount).append(">");
                } else {
                    parsedText.append("<p>");
                    for (int l = 0; l < hashCount; l++) {
                        parsedText.append("#");
                    }
                    parsedText.append(c);
                    paragraph = true;
                }
            } else {
                parsedText.append("<p>").append(blockToParse.charAt(0));
                paragraph = true;
            }
            if (hashCount + 1 < blockToParse.length()) {
                if (toHTML.containsKey(String.valueOf(c)) & !toHTML.containsKey(String.valueOf(blockToParse.charAt(hashCount + 1)))) {
                    symCount.put(String.valueOf(c), symCount.get(String.valueOf(c)) + 1);
                }
            }
            if (toHTML.containsKey(String.valueOf(c))) {
                symCount.put(String.valueOf(c), symCount.get(String.valueOf(c)) + 1);
            }
            for (int i = hashCount + 1; i < blockToParse.length(); i++) {
                if (Objects.equals(blockToParse.charAt(i), '\\')) {
                    i++;
                } else {
                    String substring = blockToParse.substring(i - 1, i + 1);
                    if (toHTML.containsKey(substring)) {
                        symCount.put(substring, symCount.get(substring) + 1);
                    } else if (toHTML.containsKey(String.valueOf(blockToParse.charAt(i)))) {
                        symCount.put(String.valueOf(blockToParse.charAt(i)), symCount.get(String.valueOf(blockToParse.charAt(i))) + 1);
                    }
                }
                if (specialSym.containsKey(String.valueOf(blockToParse.charAt(i)))) {
                    parsedText.append(specialSym.get(String.valueOf(blockToParse.charAt(i))));
                } else {
                    parsedText.append(blockToParse.charAt(i));
                }
            }

            for (int i = 0; i < System.lineSeparator().length(); i++) {
                parsedText.deleteCharAt(parsedText.length() - 1);
            }

            if (paragraph) {
                parsedText.append("</p>");
            } else {
                parsedText.append("</h").append(hashCount).append(">");
            }
            blocksToParse.set(I, parseDouble(parsedText.toString()));
        }
        return blocksToParse;
    }
}