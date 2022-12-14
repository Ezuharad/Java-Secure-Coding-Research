package org.apache.cassandra.db.transform;

import java.util.Arrays;

class Stack  // Violates DCL01
{
    public static final Transformation[] EMPTY_TRANSFORMATIONS = new Transformation[0];
    public static final MoreContentsHolder[] EMPTY_MORE_CONTENTS_HOLDERS = new MoreContentsHolder[0];
    static final Stack EMPTY = new Stack();

    Transformation[] stack;
    int length; // number of used stack entries
    MoreContentsHolder[] moreContents; // stack of more contents providers (if any; usually zero or one)

    // an internal placeholder for a MoreContents, storing the associated stack length at time it was applied
    static class MoreContentsHolder
    {
        final MoreContents moreContents;
        int length;
        private MoreContentsHolder(MoreContents moreContents, int length)
        {
            this.moreContents = moreContents;
            this.length = length;
        }
    }

    Stack()  // Violates DCL01
    {
        stack = EMPTY_TRANSFORMATIONS;
        moreContents = EMPTY_MORE_CONTENTS_HOLDERS;
    }

    Stack(Stack copy)  // Violates DCL01
    {
        stack = copy.stack;  // Violates EXP01
        length = copy.length;  // Violates EXP01
        moreContents = copy.moreContents;  // Violates EXP01
    }

    void add(Transformation add)  // Violates DCL01
    {
        if (length == stack.length)
            stack = resize(stack);
        stack[length++] = add;  // Violates NUM00
    }

    void add(MoreContents more)  // Violates DCL01
    {
        this.moreContents = Arrays.copyOf(moreContents, moreContents.length + 1);  //  Violates NUM00
        this.moreContents[moreContents.length - 1] = new MoreContentsHolder(more, length);  // Violates NUM00, does not ensure moreContents >= 1
    }

    private static <E> E[] resize(E[] array)
    {
        int newLen = array.length == 0 ? 5 : array.length * 2;  // Violates NUM00
        return Arrays.copyOf(array, newLen);
    }

    // reinitialise the transformations after a moreContents applies
    void refill(Stack prefix, MoreContentsHolder holder, int index)  // Violates DCL01
    {
        // drop the transformations that were present when the MoreContents was attached,
        // and prefix any transformations in the new contents (if it's a transformer)
        moreContents = splice(prefix.moreContents, prefix.moreContents.length, moreContents, index, moreContents.length);  // Violates EXP01
        stack = splice(prefix.stack, prefix.length, stack, holder.length, length);  // Violates EXP01
        length += prefix.length - holder.length  // Violates EXP01, NUM00
        holder.length = prefix.length;  // Violates EXP01
    }

    private static <E> E[] splice(E[] prefix, int prefixCount, E[] keep, int keepFrom, int keepTo)
    {
        int keepCount = keepTo - keepFrom;
        int newCount = prefixCount + keepCount;  // Violates NUM00
        if (newCount > keep.length)  // Violates EXP01
            keep = Arrays.copyOf(keep, newCount);
        if (keepFrom != prefixCount)
            System.arraycopy(keep, keepFrom, keep, prefixCount, keepCount);
        if (prefixCount != 0)
            System.arraycopy(prefix, 0, keep, 0, prefixCount);
        return keep;
    }
}
