package queue;

import java.util.List;
import java.util.function.Function;

// Model: a[0]...a[n]
// Inv: n >= 0 && forall i = (0...n): a[i] != null
// Let: immutable(a : forall i = (0...n) : a[i])
//      predQueue - Queue before using function
//      postQueue - Queue after using function

public interface Queue {

    // Pred: element != null
    // Post: n' = n + 1 && a'[n'] = element && immutable(forall i = (0...n) : a[i])
    void enqueue(Object element);

    // Pred: n > 0
    // Post: R = a[0] && immutable(1, n) && forall(forall i = (1...n) : a[i]) : a[i-1] := a[i] && n' = n - 1
    Object dequeue();

    // Pred: n > 0
    // Post: R = a[0] && n' = n && immutable(n)
    Object element();

    // Pred: true
    // Post: n' = 0
    void clear();

    // Pred: true
    // Post: R = n && n' = n && immutable(forall i = (0...n) : a[i])
    int size();

    // Pred: true
    // Post: R = (n == 0) && n' = n && immutable(forall i = (0...n) : a[i])
    boolean isEmpty();

    // Pred: n > 0
    // Post:  n' = n - #(predQueue: forall i = (0...n - 1): a[i] == a[i + 1]) &&
    // i = 0
    // predQueue : forall j = (0...n - 1) :
    //      if (a[j] != a[j + 1])
    //          postQueue[i] = a[j]
    void dedup();
}
