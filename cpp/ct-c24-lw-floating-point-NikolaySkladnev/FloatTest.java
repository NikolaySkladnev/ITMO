import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class FloatTest {

    public static File stdout;
    public static File stderr;
    public static String programPath;

    @FunctionalInterface
    public interface Compose {
        float compose(float a, float b);
    }

    public static void randomTestRepresent(int testCount) {

        clear();

        Random random = new Random();
        int testPassedCount = 0;

        for (int i = 1; i < testCount + 1; i++) {
            float num = random.nextFloat();
            String[] command = new String[]{programPath, "f", "1", floatToHex(num)};
            testPassedCount = passTest(command, num, testPassedCount, testCount, i);
        }

        System.out.println("    For represent passed: " + testPassedCount + "/" + testCount);
    }

    public static void randomTestOperation(int testCount, String operation, String sign, Compose compose) {

        clear();
        Random random = new Random();
        int testPassedCount = 0;

        System.out.println("    TESTING " + operation.toUpperCase());

        for (int i = 1; i <= testCount; i++) {
            float num1 = random.nextFloat();
            float num2 = random.nextFloat();
            float num = compose.compose(num1, num2);
            String[] command = new String[]{programPath, "f", "1", floatToHex(num1), sign, floatToHex(num2)};
            testPassedCount = passTest(command, num, testPassedCount, testCount, i);
        }

        System.out.println("    For " + operation + " passed: " + testPassedCount + "/" + testCount);
    }

    public static int someFailingTests() {

        float nan = Float.NaN;
        float inf = Float.POSITIVE_INFINITY;
        float negInf = Float.NEGATIVE_INFINITY;
        float posInf = Float.POSITIVE_INFINITY;
        float zero = 0f;
        Random random = new Random();
        int testPassedCount = 0;

        int nanTestCount = 20;
        int infTestCount = 20;
        int zeroTestCount = 20;
        int allTestCount = nanTestCount +  infTestCount + zeroTestCount;

        for (int i = 1; i <= nanTestCount; i++) {
            testPassedCount = passSpecialTest(nan, random, testPassedCount, allTestCount, i);
        }

        for (int i = nanTestCount + 1; i <= nanTestCount + infTestCount; i++) {
            if (i > nanTestCount + infTestCount / 2) {
                inf = Float.NEGATIVE_INFINITY;
            }
            testPassedCount = passSpecialTest(inf, random, testPassedCount, allTestCount, i);
        }

        for (int i = nanTestCount + infTestCount + 1; i <= allTestCount; i++) {
            if (i > allTestCount - zeroTestCount / 2) {
                zero = -0f;
            }
            testPassedCount = passSpecialTest(zero, random, testPassedCount, allTestCount, i);
        }

        float[][] tests = {
                {0f, 0f, nan},
                {posInf, posInf, nan},
                {posInf, negInf, nan},
                {negInf, posInf, nan},
                {negInf, negInf, nan},
                {posInf, 0f, posInf},
                {posInf, -0f, negInf},
                {negInf, 0f, negInf},
                {negInf, -0f, posInf},
                {0f, posInf, 0f},
                {0f, negInf, -0f},
                {-0f, posInf, -0f},
                {-0f, negInf, 0f},
                {posInf, negInf, nan},
                {posInf, 0f, nan},
                {posInf, -0f, nan},
                {negInf, 0f, nan},
                {negInf, -0f, nan}
        };

        String[] signs = {
                "/", "/", "/", "/", "/", "/", "/", "/", "/", "/", "/", "/", "/",
                "+",
                "x", "x", "x", "x"
        };

        for (int i = 0; i < 18; i++) {
            System.out.println("TEST " + floatToNormalizedHex(tests[i][0]) + " " + signs[i] + " " + floatToNormalizedHex(tests[i][1]));
            String[] command = new String[]{programPath, "f", "1", floatToHex(tests[i][0]), signs[i], floatToHex(tests[i][1])};
            testPassedCount = passTest(command, tests[i][2], testPassedCount, 18, (i + 1));
        }
        return allTestCount + 18;
    }

    private static int passSpecialTest(float special, Random random, int testPassedCount, int allTestCount, int testNumber) {
        float num1 = testNumber % 2 == 0 ? random.nextFloat() : special;
        float num2 = testNumber % 2 == 1 ? random.nextFloat() : special;
        String sign = getSign(testNumber);
        float num = getNum(num1, num2, testNumber);
        String[] command = new String[]{programPath, "f", "1", floatToHex(num1), sign, floatToHex(num2)};
        testPassedCount = passTest(command, num, testPassedCount, allTestCount, testNumber);
        return testPassedCount;
    }

    public static float getNum(float num1, float num2, int testNumber) {
        return switch (testNumber % 4) {
            case 0 -> num1 + num2;
            case 1 -> num1 - num2;
            case 2 -> num1 * num2;
            case 3 -> num1 / num2;
            default -> 0f;
        };
    }

    public static String getSign(int testNumber) {
        return switch (testNumber % 4) {
            case 0 -> "+";
            case 1 -> "-";
            case 2 -> "x";
            case 3 -> "/";
            default -> "";
        };
    }



    public static int passTest(String[] command, float expected, int testPassedCount, int testCount, int testNumber) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(stdout);
            pb.redirectError(stderr);
            Process process = pb.start();
            try {
                process.waitFor();
            } finally {
                process.destroy();
            }
            String result = new BufferedReader(new InputStreamReader(new FileInputStream(stdout))).readLine();
            String reference = floatToNormalizedHex(expected);
            if (result == null || !result.equals(floatToNormalizedHex(expected))) {
                System.err.println(
                        "    Test #" + testNumber + " failed\n" +
                                "        for input '" + commandToString(command) + "':\n" +
                                "            expected: " + reference + "\n" +
                                "            actual: " + result + "\n"
                );
            } else {
                testPassedCount++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.err.println("    error running test #" + testNumber);
        }
        if (testNumber % 10 == 0) {
            System.out.println("    tests passed: " + testNumber + "/" + testCount);
        }
        clear();
        return testPassedCount;
    }



    public static void clear(File file) {
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file)
                    )
            );
            bw.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());;
        }
    }

    public static void clear() {
        clear(stdout);
        clear(stderr);
    }



    public static String floatToNormalizedHex(float number) {
        if (Float.isNaN(number)) {
            return "nan";
        } else if (Float.isInfinite(number)) {
            return (number < 0 ? "-" : "") + "inf";
        }
        String string = Float.toHexString(number);
        StringBuilder sb = new StringBuilder();
        int i = string.length() - 1;
        while (i >= 0 && string.charAt(i) != 'p') {
            sb.append(string.charAt(i));
            i--;
        }
        int exp = Integer.parseInt(sb.reverse().toString());
        sb.setLength(0);
        i--;
        while (i >= 0 && string.charAt(i) != '.') {
            sb.append(string.charAt(i));
            i--;
        }
        String man = sb.reverse() + "0".repeat(6 - sb.length());
        return string.substring(0, i + 1) + man + "p" + (exp >= 0 ? "+" : "") + exp;
    }

    public static String floatToHex(float number) {
        int num = Float.floatToIntBits(number);
        return "0x" + Integer.toHexString(num);
    }



    public static String commandToString(String[] command) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < command.length - 1; i++) {
            sb.append(command[i]).append(" ");
        }
        sb.append(command[command.length - 1]);
        return sb.toString();
    }



    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter path to your .exe file: ");
        programPath = sc.nextLine();

        stdout = new File("stdout.txt");
        stderr = new File("stderr.txt");
        clear();

        int repTestCount = 100;
        int addTestCount = 50;
        int subTestCount = 50;
        int mulTestCount = 50;
        int divTestCount = 50;
        int allTestCount = repTestCount + addTestCount + subTestCount + mulTestCount + divTestCount;

        long beginTme = System.currentTimeMillis();

        System.out.println("TESTING FLOAT REPRESENT");
        randomTestRepresent(repTestCount);

        System.out.println("TESTING FLOAT OPERATIONS, ROUNDING = 1");
        randomTestOperation(addTestCount, "add", "+", Float::sum);
        randomTestOperation(subTestCount, "subtract", "-", (a, b) -> a - b);
        randomTestOperation(mulTestCount, "multiply", "x", (a, b) -> a * b);
        randomTestOperation(divTestCount, "divide", "/", (a, b) -> a / b);

        System.out.println("SOME FAILING TESTS");
        allTestCount += someFailingTests();

        long endTime = System.currentTimeMillis();
        System.out.println(allTestCount + " tests passed in " + (endTime - beginTme) + "ms");
        stdout.delete();
        stderr.delete();
    }

}

