#pragma once

#include "bucket_iterators.hpp"

template< typename T >
BucketStorage< T >::Block::Block(size_type capacity) :
	data(static_cast< T* >(::operator new(capacity * sizeof(T)))),
	active(static_cast< size_type* >(::operator new(capacity * sizeof(size_type)))), size(0), capacity(capacity), next_block(nullptr)
{
	if (!data || !active)
		throw std::bad_alloc();

	for (size_type i = 0; i < capacity; i++)
		active[i] = 1;
}

template< typename T >
BucketStorage< T >::Block::~Block()
{
	for (size_type i = 0; i < capacity; i++)
		if (active[i] == 0)
			data[i].~T();

	::operator delete(data);
	::operator delete(active);
}

template< typename T >
BucketStorage< T >::BucketStorage(size_type capacity) :
	head(nullptr), block_capacity(capacity), total_size(0), total_capacity(0)
{
}

template< typename T >
BucketStorage< T >::BucketStorage(const BucketStorage& different_block) :
	head(nullptr), block_capacity(different_block.block_capacity), total_size(0), total_capacity(0)
{
	copyFrom(different_block);
}

template< typename T >
BucketStorage< T >::BucketStorage(BucketStorage&& different_block) noexcept :
	head(different_block.head), block_capacity(different_block.block_capacity), total_size(different_block.total_size),
	total_capacity(different_block.total_capacity)
{
	different_block.head = nullptr;
	different_block.total_size = 0;
	different_block.total_capacity = 0;
}

template< typename T >
BucketStorage< T >& BucketStorage< T >::operator=(const BucketStorage& different_block)
{
	if (this != &different_block)
	{
		clear();
		block_capacity = different_block.block_capacity;
		total_size = 0;
		total_capacity = 0;
		copyFrom(different_block);
	}
	return *this;
}

template< typename T >
void BucketStorage< T >::copyFrom(const BucketStorage& different_block)
{
	try
	{
		Block* current_head = different_block.head;
		while (current_head)
		{
			for (size_type i = 0; i < current_head->capacity; i++)
			{
				if (current_head->active[i] == 0)
				{
					insert(current_head->data[i]);
				}
			}
			current_head = current_head->next_block;
		}
	} catch (...)
	{
		clear();
		throw std::runtime_error("Error: error in BucketStorage copy construction.");
	}
}

template< typename T >
BucketStorage< T >& BucketStorage< T >::operator=(BucketStorage&& different_block) noexcept
{
	if (this != &different_block)
	{
		BucketStorage temp(std::move(different_block));
		swap(temp);
	}
	return *this;
}

template< typename T >
BucketStorage< T >::~BucketStorage()
{
	clear();
}

template< typename T >
void BucketStorage< T >::allocate_block()
{
	Block* new_block;
	try
	{
		new_block = new Block(block_capacity);
		new_block->next_block = head;
		total_capacity += block_capacity;
		head = new_block;
	} catch (...)
	{
		delete new_block;
		throw std::runtime_error("Error: failed to allocate memory");
	}
}

template< typename T >
template< typename U >
typename BucketStorage< T >::iterator BucketStorage< T >::insert(U&& value)
{
	if (total_size == total_capacity)
	{
		allocate_block();
	}

	Block* current = head;
	while (current)
	{
		for (size_type i = 0; i < current->capacity; i++)
		{
			if (current->active[i] > 0)
			{
				new (current->data + i) T(std::forward< U >(value));
				current->active[i] = 0;
				current->size++;
				total_size++;
				return iterator(this, current, i);
			}
		}
		current = current->next_block;
	}
	throw std::runtime_error("Error: unable to insert value.");
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::erase(iterator it)
{
	if (it == end())
		throw std::out_of_range("Error: iterator out of range");

	Block* block = it.block;
	size_type index = it.index;

	block->data[index].~T();
	block->active[index] = 1;
	block->size--;
	total_size--;

	if (block->size == 0)
	{
		if (block == head)
		{
			head = block->next_block;
		}
		else
		{
			Block* temp = head;
			while (temp->next_block != block)
				temp = temp->next_block;
			temp->next_block = block->next_block;
		}
		delete block;
		total_capacity -= block_capacity;
	}

	return iterator(this, it.block, it.index + 1);
}

template< typename T >
void BucketStorage< T >::shrink_to_fit()
{
	BucketStorage< T > new_storage(block_capacity);
	Block* current_block = head;

	try
	{
		while (current_block)
		{
			for (size_type i = 0; i < current_block->capacity; i++)
			{
				if (current_block->active[i] == 0)
				{
					new_storage.insert(std::move(current_block->data[i]));
					current_block->data[i].~T();
				}
			}
			current_block = current_block->next_block;
		}
	} catch (...)
	{
		new_storage.clear();
		throw std::runtime_error("Error: error during shrink_to_fit");
	}

	swap(new_storage);
}

template< typename T >
void BucketStorage< T >::clear() noexcept
{
	while (head)
	{
		Block* temp = head;
		head = head->next_block;
		delete temp;
	}
	total_size = 0;
	total_capacity = 0;
}

template< typename T >
void BucketStorage< T >::swap(BucketStorage& different_block) noexcept
{
	std::swap(block_capacity, different_block.block_capacity);
	std::swap(total_capacity, different_block.total_capacity);
	std::swap(total_size, different_block.total_size);
	std::swap(head, different_block.head);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::get_to_distance(iterator it, difference_type distance)
{
	while (distance != 0)
	{
		if (distance > 0)
		{
			it++;
			distance--;
		}
		else
		{
			it--;
			distance++;
		}
		if (it == end())
			throw std::out_of_range("Error: out of range");
	}
	return it;
}

template< typename T >
template< typename IteratorType >
IteratorType BucketStorage< T >::begin_impl() const noexcept
{
	Block* current_block = head;
	while (current_block)
	{
		for (size_type j = 0; j < current_block->capacity; j++)
		{
			if (current_block->active[j] == 0)
			{
				return IteratorType(const_cast< BucketStorage* >(this), current_block, j);
			}
		}
		current_block = current_block->next_block;
	}
	return IteratorType(const_cast< BucketStorage* >(this), current_block, 0);
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::begin() noexcept
{
	return begin_impl< iterator >();
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::begin() const noexcept
{
	return begin_impl< const_iterator >();
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::cbegin() const noexcept
{
	return begin();
}

template< typename T >
typename BucketStorage< T >::iterator BucketStorage< T >::end() noexcept
{
	return iterator(this, nullptr, 0);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::end() const noexcept
{
	return const_iterator(const_cast< BucketStorage* >(this), nullptr, 0);
}

template< typename T >
typename BucketStorage< T >::const_iterator BucketStorage< T >::cend() const noexcept
{
	return end();
}

template< typename T >
bool BucketStorage< T >::empty() const noexcept
{
	return total_size == 0;
}

template< typename T >
typename BucketStorage< T >::size_type BucketStorage< T >::size() const noexcept
{
	return total_size;
}

template< typename T >
typename BucketStorage< T >::size_type BucketStorage< T >::capacity() const noexcept
{
	return total_capacity;
}
