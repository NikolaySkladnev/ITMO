public class Sum {
    public static void main(String[] args) {
        int sum = 0;
        StringBuilder lineNum = new StringBuilder();

        for (String line : args) {
            for (int j = 0; j < line.length(); j++) {
                if (!(Character.isWhitespace(line.charAt(j)))) {
                    lineNum.append(line.charAt(j));
                } else if (!(lineNum.toString().isEmpty())) {
                    sum += Integer.parseInt(lineNum.toString());
                    lineNum.setLength(0);
                }
            }
            if (!(lineNum.toString().isEmpty())) {
                sum += Integer.parseInt(lineNum.toString());
                lineNum.setLength(0);
            }
        }
        System.out.println((long) sum);
    }
}