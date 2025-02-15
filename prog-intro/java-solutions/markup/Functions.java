package markup;

import java.util.List;

abstract class Functions extends Paragraph implements Interface {
    protected String toMarkDownSym;
    protected String toBBCodeSym;

    protected Functions(List<Interface> list, String toMarkDownSym, String toBBCodeSym) {
        super(list);
        this.toMarkDownSym = toMarkDownSym;
        this.toBBCodeSym = toBBCodeSym;
    }

    @Override
    public void toMarkdown(StringBuilder stringBuilder) {
        stringBuilder.append(toMarkDownSym);
        for (Interface tmp : this.list) {
            tmp.toMarkdown(stringBuilder);
        }
        stringBuilder.append(toMarkDownSym);
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        stringBuilder.append("[").append(toBBCodeSym).append("]");
        for (Interface tmp : list) {
            tmp.toBBCode(stringBuilder);
        }
        stringBuilder.append("[/").append(toBBCodeSym).append("]");
    }
}