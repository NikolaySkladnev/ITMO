package markup;

import java.util.List;

public class Paragraph implements Interface {
    public final List<Interface> list;

    public Paragraph(List<Interface> list) {
        this.list = list;
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        for (Interface classes : list) {
            classes.toMarkdown(stringBuilder);
        }
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        for (Interface classes : list) {
            classes.toBBCode(stringBuilder);
        }
    }
}