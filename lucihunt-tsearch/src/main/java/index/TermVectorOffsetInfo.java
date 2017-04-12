package index;

/**
 * 词向量信息  
 * 这里面居然保存的是开始  结束的偏移
 * 
 * @author WangYazhou
 * @date  2017年4月12日 上午11:59:44
 * @see
 */
public class TermVectorOffsetInfo {
    public static final TermVectorOffsetInfo[] EMPTY_OFFSET_INFO = new TermVectorOffsetInfo[0];
    private int startOffset;
    private int endOffset;

    public TermVectorOffsetInfo() {
    }

    public TermVectorOffsetInfo(int startOffset, int endOffset) {
        this.endOffset = endOffset;
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TermVectorOffsetInfo))
            return false;

        final TermVectorOffsetInfo termVectorOffsetInfo = (TermVectorOffsetInfo) o;

        if (endOffset != termVectorOffsetInfo.endOffset)
            return false;
        if (startOffset != termVectorOffsetInfo.startOffset)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = startOffset;
        result = 29 * result + endOffset;
        return result;
    }
}
