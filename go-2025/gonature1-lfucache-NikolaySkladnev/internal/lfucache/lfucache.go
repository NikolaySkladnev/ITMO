package lfucache

import (
	"errors"
	"iter"

	"github.com/igoroutine-courses/gonature.lfucache/internal/linkedlist"
)

var ErrKeyNotFound = errors.New("key not found")

const DefaultCapacity = 5

type Cache[K comparable, V any] interface {
	Get(key K) (V, error)
	Put(key K, value V)
	All() iter.Seq2[K, V]
	Size() int
	Capacity() int
	GetKeyFrequency(key K) (int, error)
}

type entry[K comparable, V any] struct {
	Key      K
	Element  V
	Frequncy int
}

type cacheImpl[K comparable, V any] struct {
	capacity int
	elements map[K]*linkedlist.Element[*entry[K, V]]
	list     *linkedlist.List[*entry[K, V]]
	lists    map[int]*linkedlist.Element[*entry[K, V]]
}

func New[K comparable, V any](args ...int) *cacheImpl[K, V] {
	capacity := DefaultCapacity
	if len(args) != 0 {
		if args[0] < 0 {
			panic("capacity must be >= 0")
		}
		capacity = args[0]
	}
	return &cacheImpl[K, V]{
		capacity: capacity,
		elements: make(map[K]*linkedlist.Element[*entry[K, V]]),
		list:     linkedlist.New[*entry[K, V]](),
		lists:    make(map[int]*linkedlist.Element[*entry[K, V]]),
	}
}

func (c *cacheImpl[K, V]) Get(key K) (V, error) {
	if node, ok := c.elements[key]; ok {
		c.increment(node)
		return node.Value.Element, nil
	}

	var zero V
	return zero, ErrKeyNotFound
}

func (c *cacheImpl[K, V]) Put(key K, value V) {
	if c.capacity == 0 {
		return
	}

	if el, ok := c.elements[key]; ok {
		el.Value.Element = value
		c.increment(el)
		return
	}

	if c.Size() < c.capacity {
		c.insertNewKey(key, value)
		return
	}

	c.updateWithNewKey(key, value)
}

func (c *cacheImpl[K, V]) All() iter.Seq2[K, V] {
	return func(yield func(K, V) bool) {
		for n := c.list.Front(); n != nil; n = n.Next() {
			e := n.Value
			if !yield(e.Key, e.Element) {
				return
			}
		}
	}
}

func (c *cacheImpl[K, V]) Size() int     { return len(c.elements) }
func (c *cacheImpl[K, V]) Capacity() int { return c.capacity }

func (c *cacheImpl[K, V]) GetKeyFrequency(key K) (int, error) {
	if node, ok := c.elements[key]; ok {
		return node.Value.Frequncy, nil
	}
	return 0, ErrKeyNotFound
}

func (c *cacheImpl[K, V]) increment(node *linkedlist.Element[*entry[K, V]]) {
	oldF := node.Value.Frequncy
	newF := oldF + 1

	headOld := c.lists[oldF]

	var headNew *linkedlist.Element[*entry[K, V]]
	if h, ok := c.lists[newF]; ok {
		headNew = h
	}

	var headOldNext *linkedlist.Element[*entry[K, V]]
	if headOld == node {
		headOldNext = headOld.Next()
	}

	c.moveBefore(node, headNew, headOld)

	c.lists[newF] = node

	if headOld == node {
		if headOldNext != nil && headOldNext.Value.Frequncy == oldF && headOldNext.List == c.list {
			c.lists[oldF] = headOldNext
		} else {
			delete(c.lists, oldF)
		}
	}

	node.Value.Frequncy = newF
}

func (c *cacheImpl[K, V]) moveBefore(node *linkedlist.Element[*entry[K, V]], headNew *linkedlist.Element[*entry[K, V]], headOld *linkedlist.Element[*entry[K, V]]) {
	if headNew != nil {
		c.list.MoveBefore(node, headNew)
	} else {
		c.list.MoveBefore(node, headOld)
	}
}

func (c *cacheImpl[K, V]) updateWithNewKey(key K, value V) {
	tail, update := c.removeLast()

	update.Key = key
	update.Element = value
	update.Frequncy = 1

	if head, ok := c.lists[1]; ok && head.List == c.list {
		c.list.AdoptBefore(tail, head)
	} else {
		c.list.AdoptFront(tail)
		c.list.MoveToBack(tail)
	}

	c.lists[1] = tail
	c.elements[key] = tail
}

func (c *cacheImpl[K, V]) removeLast() (*linkedlist.Element[*entry[K, V]], *entry[K, V]) {
	tail := c.list.Back()
	update := tail.Value
	c.list.Remove(tail)
	delete(c.elements, update.Key)
	return tail, update
}

func (c *cacheImpl[K, V]) insertNewKey(key K, value V) {
	e := &entry[K, V]{Key: key, Element: value, Frequncy: 1}
	var el *linkedlist.Element[*entry[K, V]]
	if head, ok := c.lists[1]; ok {
		el = c.list.InsertBefore(e, head)
	} else {
		el = c.list.PushBack(e)
	}
	c.lists[1] = el
	c.elements[key] = el
}
