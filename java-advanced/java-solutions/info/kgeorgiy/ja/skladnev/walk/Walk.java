package info.kgeorgiy.ja.skladnev.walk;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Walk {
    static final String DEFAULT_VALUE = "0".repeat(16);
    static final int BUFFER_SIZE = 1024 * 16;
    static final String ALGORITHM = "SHA-256"; // PRIVATE?
    static final Charset STANDART = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Error: Invalid arguments.");
        } else {
            walk(args[0], args[1]);
        }
    }

    private static void walk(String input, String output) {
        try {
            // :NOTE: catch invalid path exception
            Path inputPath = Path.of(input).normalize();
            checkParentDir(inputPath);

            try (BufferedReader reader = Files.newBufferedReader(inputPath, STANDART)) {

                Path outPath = Path.of(output).normalize();
                checkParentDir(outPath);

                try (BufferedWriter writer = Files.newBufferedWriter(outPath, STANDART)) {

                    String file;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    MessageDigest toHex = MessageDigest.getInstance(ALGORITHM);
                    StringBuilder hexString = new StringBuilder();

                    while ((file = reader.readLine()) != null) {
                        try {
                            // NOTE: System.lineSeparator()
                            writer.write(calculateHashSum(Path.of(file).normalize(), toHex, buffer, hexString) + " " + file + "\n");
                        } catch (IllegalArgumentException e) {
                            writer.write(DEFAULT_VALUE + " " + file + "\n");
                        }
                        hexString.setLength(0);
                        toHex.reset();
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("Error: no such file " + output + " " + e.getMessage());
                } catch (SecurityException e) {
                    System.err.println("Error: security problem " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error: no such file " + input + " " + e.getMessage());
            } catch (SecurityException e) {
                System.err.println("Error: security problem " + e.getMessage());
            } catch (InvalidPathException e) {
                System.err.println("Error: invalid path " + output + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Error: illegal argument " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: " + ALGORITHM + " - invalid algorithm " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Error: no such file " + input + " " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Error: security problem " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Error: invalid path " + input + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: illegal argument " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // :NOTE: hardcoded hash
    // :NOTE: String -> Path
    // :NOTE: redundant hash recreation
    // :NOTE: redundant buffer recreation
    private static String calculateHashSum(Path file, MessageDigest toHex, byte[] buffer, StringBuilder hexString) {
        try {
            checkParentDir(file);

            File FileCheck = file.toFile();
            if (!FileCheck.exists() || !FileCheck.isFile() || !FileCheck.canRead()) {
                System.err.println("Error: file while working with " + file);
                // :NOTE: static string
                return DEFAULT_VALUE;
            }

            try (InputStream inputStream = Files.newInputStream(file)) {
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    toHex.update(buffer, 0, read);
                }
            }

            byte[] hashBytes = toHex.digest();

            for (int i = 0; i < 8; i++) {
                hexString.append(String.format("%02x", hashBytes[i]));
            }

            return hexString.toString();

        } catch (InvalidPathException e) {
            System.err.println("Error: invalid path " + file + e.getMessage());
            return DEFAULT_VALUE;
        } catch (FileNotFoundException e) {
            System.err.println("Error: no such file " + file + e.getMessage());
            return DEFAULT_VALUE;
        } catch (SecurityException e) {
            System.err.println("Error: security problem " + e.getMessage());
            return DEFAULT_VALUE;
        } catch (IOException e) {
            System.err.println("Error: in file " + e.getMessage());
            return DEFAULT_VALUE;
        }
    }

    private static void checkParentDir(Path file) throws IOException {
        if (file.getParent() != null && !Files.exists(file)) {
            Files.createDirectories(file.getParent());
        }
    }
}
