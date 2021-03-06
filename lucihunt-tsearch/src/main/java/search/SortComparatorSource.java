package search;

import java.io.IOException;
import java.io.Serializable;

import index.IndexReader;

/**
 * Expert: returns a comparator for sorting ScoreDocs.
 *
 * <p>Created: Apr 21, 2004 3:49:28 PM
 * 
 * @author  Tim Jones
 * @version $Id: SortComparatorSource.java 150348 2004-05-19 23:05:27Z tjones $
 * @since   1.4
 */
public interface SortComparatorSource extends Serializable {

    /**
     * Creates a comparator for the field in the given index.
     * @param reader Index to create comparator for.
     * @param fieldname  Field to create comparator for.
     * @return Comparator of ScoreDoc objects.
     * @throws IOException If an error occurs reading the index.
     */
    ScoreDocComparator newComparator(IndexReader reader, String fieldname) throws IOException;
}