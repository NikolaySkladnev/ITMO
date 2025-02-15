package markup;

public class Text implements Interface {
    public String str;

    public Text(String str) {
        this.str = str;
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        stringBuilder.append(str);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        stringBuilder.append(str);
    }
}
