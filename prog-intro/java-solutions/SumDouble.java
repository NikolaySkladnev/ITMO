public class SumDouble {
    public static void main(String[] args) {
        double sum = 0;
        StringBuilder lineNum = new StringBuilder(); 

        for (String line : args) { 
            for (int j = 0; j < line.length(); j++) { // использовал "join" вместо 2 циклов
                if (!(Character.isWhitespace(line.charAt(j)))) {
                    lineNum.append(line.charAt(j));
                } else if (!(lineNum.toString().isEmpty())) { // Проверка была как str != ""
                    sum += Double.parseDouble(lineNum.toString());
                    lineNum.setLength(0); // Не обнулял существующий "StringBuilder", а создовал новый
                }
            }
            if (!(lineNum.toString().isEmpty())) {
                sum += Double.parseDouble(lineNum.toString());
                lineNum.setLength(0);
            }
        }
        System.out.println(sum);
    }
}