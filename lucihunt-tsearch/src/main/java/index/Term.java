package index;

public class Term {

    private String field;
    private String text;

    public Term(String fld, String txt) {
        this(fld, txt, true);
    }

    Term(String fld, String txt, boolean intern) {
        field = intern ? fld.intern() : fld; // field names are interned
        text = txt; // unless already known to be
    }

    public final String field() {
        return field;
    }

    public final String text() {
        return text;
    }

    public final boolean equals(Object o) {
        if (o == null)
            return false;
        Term other = (Term) o;
        return field == other.field && text.equals(other.text);
    }

    /** Combines the hashCode() of the field and the text. */
    public final int hashCode() {
        return field.hashCode() + text.hashCode();
    }

    public int compareTo(Object other) {
        return compareTo((Term) other);
    }

    public final int compareTo(Term other) {
        if (field == other.field) // fields are interned
            return text.compareTo(other.text);
        else
            return field.compareTo(other.field);
    }
}
