package index;

public class Posting {

    Term term; // the Term  词本身 
    int freq; // its frequency in doc 词在文档中的频率
    int[] positions; // positions it occurs at  词在文档中的位置
    TermVectorOffsetInfo[] offsets;

    Posting(Term t, int position, TermVectorOffsetInfo offset) {
        term = t;
        freq = 1;
        positions = new int[1];
        positions[0] = position;
        if (offset != null) {
            offsets = new TermVectorOffsetInfo[1];
            offsets[0] = offset;
        } else
            offsets = null;
    }
}
