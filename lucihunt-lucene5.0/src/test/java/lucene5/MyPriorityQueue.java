package lucene5;

import java.util.Random;

public class MyPriorityQueue {
    public static void main(String[] args) {
        MyPriorityQueue priQueue = new MyPriorityQueue(10);
        Random r = new Random();
        for (int i = 0; i < 25; i++) {
            priQueue.insert(r.nextInt(100));
        }
        // 打印结果
        Integer v = (Integer) priQueue.pop();
        while (v != null) {
            System.out.println(v);
            v = (Integer) priQueue.pop();
        }
    }

    private Object[] heap;
    private int size;
    private int maxSize;

    public MyPriorityQueue(int i) {
        initialize(i);
    }

    /**
     * Determines the ordering of objects in this priority queue. Subclasses
     * must define this one method.
     */
    protected boolean lessThan(Object a, Object b) {
        Integer a_ = (Integer) a;
        Integer b_ = (Integer) b;
        return a_ < b_;
    }

    /** Subclass constructors must call this. */
    protected final void initialize(int maxSize) {
        size = 0;
        int heapSize = maxSize + 1;
        heap = new Object[heapSize];
        this.maxSize = maxSize;
    }

    /**
     * Adds an Object to a PriorityQueue in log(size) time. If one tries to add
     * more objects than maxSize from initialize a RuntimeException
     * (ArrayIndexOutOfBound) is thrown.
     */
    public final void put(Object element) {// 如果队列未满，将ele添加到队尾，然后和父节点比较，使得较小的ele至于堆顶！
        size++;
        heap[size] = element;
        upHeap();
    }

    /**
     * Adds element to the PriorityQueue in log(size) time if either the
     * PriorityQueue is not full, or not lessThan(element, top()).
     * 
     * @param element
     * @return true if element is added, false otherwise.
     */
    public boolean insert(Object element) {
        if (size < maxSize) {
            put(element);
            return true;
        } else if (size > 0 && !lessThan(element, top())) {// 队列满，且ele大于队列中最小的ele！对于小根堆，top取得最小ele。
            heap[1] = element;
            adjustTop();
            return true;
        } else
            // 队列满，ele小于队列中最小ele，则直接pass！
            return false;
    }

    /** Returns the least element of the PriorityQueue in constant time. */
    public final Object top() {
        if (size > 0)
            return heap[1];
        else
            return null;
    }

    /**
     * Removes and returns the least element of the PriorityQueue in log(size)
     * time.
     */
    public final Object pop() {
        if (size > 0) {
            Object result = heap[1]; // save first value
            heap[1] = heap[size]; // move last to first
            heap[size] = null; // permit GC of objects
            size--;
            downHeap(); // 这里将堆顶ele弹出，继而用队列尾ele进行填充（heap[1]=heap[size]），那么需要重新调整heap的结构，使得其满足小根堆结构！
            return result;
        } else
            return null;
    }

    /**
     * Should be called when the Object at top changes values. Still log(n)
     * worst case, but it's at least twice as fast to
     * 
     * <pre>
     * {
     * 	pq.top().change();
     * 	pq.adjustTop();
     * }
     * </pre>
     * 
     * instead of
     * 
     * <pre>
     * {
     * 	o = pq.pop();
     * 	o.change();
     * 	pq.push(o);
     * }
     * </pre>
     */
    public final void adjustTop() {
        downHeap();
    }

    /** Returns the number of elements currently stored in the PriorityQueue. */
    public final int size() {
        return size;
    }

    /** Removes all entries from the PriorityQueue. */
    public final void clear() {
        for (int i = 0; i <= size; i++)
            heap[i] = null;
        size = 0;
    }

    private final void upHeap() {
        int i = size;
        Object node = heap[i]; // save bottom node
        int j = i >>> 1;
        while (j > 0 && lessThan(node, heap[j])) {
            heap[i] = heap[j]; // shift parents down
            i = j;
            j = j >>> 1;
        }
        heap[i] = node; // install saved node
    }

    private final void downHeap() {
        int i = 1;
        Object node = heap[i]; // save top node
        int j = i << 1; // find smaller child
        int k = j + 1;
        if (k <= size && lessThan(heap[k], heap[j])) {
            j = k;
        }
        while (j <= size && lessThan(heap[j], node)) {
            heap[i] = heap[j]; // shift up child
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= size && lessThan(heap[k], heap[j])) {
                j = k;
            }
        }
        heap[i] = node; // install saved node
    }
}
