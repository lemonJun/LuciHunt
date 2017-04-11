package analysis;

/** 
 * Token内部维护的是开始结束的偏移量
 * 而位置是基于Term的  也就是Token的  因此每一个Token的产生  外部就会对位置+1
 * 当然这是低版本   事实上这个值完全可以在内部维护
 * 
 * 一个token代表着一个字段的文本产生了一个词，它包含了词的内容，词的启始线束偏移量 以及类型；
 * 起始结束的偏移量使得能够重新定位Tokin在文本中的位置，比如高亮显示；
 * 
 * offset 是基于字字母或汉字的   也就是一个utf-8的chr
 * 
 * @author WangYazhou
 * @date  2017年3月2日 下午4:27:05
 * @see   
 */
public final class Token {
    String termText; // the text of the term
    int startOffset; // start in source text
    int endOffset; // end in source text
    String type = "word"; // lexical type

    private int positionIncrement = 1;//内部默认词的增量是1  外面维护位置数据

    /** Constructs a Token with the given term text, and start & end offsets.
      The type defaults to "word." */
    public Token(String text, int start, int end) {
        termText = text;
        startOffset = start;
        endOffset = end;
    }
    
    //    /** Constructs a Token with the given text, start and end offsets, & type. */
    //    public Token(String text, int start, int end, String typ) {
    //        termText = text;
    //        startOffset = start;
    //        endOffset = end;
    //        type = typ;
    //    }

    /** Set the position increment.  This determines the position of this token
     * relative to the previous Token in a {@link TokenStream}, used in phrase
     * searching.
     *
     * <p>The default value is one.
     *
     * <p>Some common uses for this are:<ul>
     *
     * <li>Set it to zero to put multiple terms in the same position.  This is
     * useful if, e.g., a word has multiple stems.  Searches for phrases
     * including either stem will match.  In this case, all but the first stem's
     * increment should be set to zero: the increment of the first instance
     * should be one.  Repeating a token with an increment of zero can also be
     * used to boost the scores of matches on that token.
     *
     * <li>Set it to values greater than one to inhibit exact phrase matches.
     * If, for example, one does not want phrases to match across removed stop
     * words, then one could build a stop word filter that removes stop words and
     * also sets the increment to the number of stop words removed before each
     * non-stop word.  Then exact phrase queries will only match when the terms
     * occur with no intervening stop words.
     *
     * </ul>
     * @see org.apache.lucene.index.TermPositions
     */
    public void setPositionIncrement(int positionIncrement) {
        if (positionIncrement < 0)
            throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
        this.positionIncrement = positionIncrement;
    }

    /** Returns the position increment of this Token.
     * @see #setPositionIncrement
     */
    public int getPositionIncrement() {
        return positionIncrement;
    }

    /** Returns the Token's term text. */
    public final String termText() {
        return termText;
    }

    /** Returns this Token's starting offset, the position of the first character
    corresponding to this token in the source text.
    
    Note that the difference between endOffset() and startOffset() may not be
    equal to termText.length(), as the term text may have been altered by a
    stemmer or some other filter. */
    public final int startOffset() {
        return startOffset;
    }

    /** Returns this Token's ending offset, one greater than the position of the
    last character corresponding to this token in the source text. */
    public final int endOffset() {
        return endOffset;
    }

    /** Returns this Token's lexical type.  Defaults to "word". */
    public final String type() {
        return type;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(" + termText + "," + startOffset + "," + endOffset);
        if (!type.equals("word"))
            sb.append(",type=" + type);
        if (positionIncrement != 1)
            sb.append(",posIncr=" + positionIncrement);
        sb.append(")");
        return sb.toString();
    }
}
