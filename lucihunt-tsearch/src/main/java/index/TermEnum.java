package index;

import java.io.IOException;

/**
 * 词的枚举类 
 * 代表的是 词典中词的信息
 *
 * @author WangYazhou
 * @date  2017年4月14日 上午11:43:20
 * @see
 */
public abstract class TermEnum {
    //下一个TERM 
    public abstract boolean next() throws IOException;

    //当前的词
    public abstract Term term();

    //词对应的文档频率
    public abstract int docFreq();

    //关闭
    public abstract void close() throws IOException;

    // Term Vector support

    //基于词的比较  跳到某个位置
    public boolean skipTo(Term target) throws IOException {
        do {
            if (!next())
                return false;
        } while (target.compareTo(term()) > 0);
        return true;
    }
}
