package my.shouheng.palmmarkdown.tools;

/**
 * Created by wangshouheng on 2017/6/29. */
public enum  MarkdownEffect {
    ITALIC(0),
    BOLD(1),
    QUOTE(2),
    CODE_BLOCK(3),
    STRIKE(4),
    H_LINE(5),
    H1(6),
    H2(7),
    H3(8),
    H4(9),
    H5(10),
    H6(11),
    XML(12),
    LINK(13),
    TABLE(14),
    NUMBER_LIST(15),
    NORMAL_LIST(16),
    IMAGE(17),
    MARK(18),
    CHECKBOX(19),
    MATH_JAX(20),
    SUB(21),
    SUP(22),
    FOOTNOTE(23);

    public final int typeCode;

    MarkdownEffect(int typeCode){
        this.typeCode = typeCode;
    }
}
