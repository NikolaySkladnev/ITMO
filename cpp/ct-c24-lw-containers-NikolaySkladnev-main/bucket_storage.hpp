#pragma once

#include <iostream>

template< typename T >
class BucketStorage
{
  public:
	using value_type = T;
	using reference = T&;
	using const_reference = const T&;
	using size_type = std::size_t;
	using difference_type = std::ptrdiff_t;

	class iterator;
	class const_iterator;

	explicit BucketStorage(size_type block_capacity = 64);
	BucketStorage(const BucketStorage& different_block);
	BucketStorage(BucketStorage&& different_block) noexcept;
	BucketStorage& operator=(const BucketStorage& different_block);
	BucketStorage& operator=(BucketStorage&& different_block) noexcept;
	~BucketStorage();

	template< typename U >
	iterator insert(U&& value);
	iterator erase(iterator it);
	bool empty() const noexcept;
	size_type size() const noexcept;
	size_type capacity() const noexcept;
	void shrink_to_fit();
	void clear() noexcept;
	void swap(BucketStorage& other) noexcept;

	iterator begin() noexcept;
	const_iterator begin() const noexcept;
	const_iterator cbegin() const noexcept;
	iterator end() noexcept;
	const_iterator end() const noexcept;
	const_iterator cend() const noexcept;

	iterator get_to_distance(iterator it, difference_type distance);

  private:
	struct Block
	{
		T* data;
		size_type* active;
		size_type size;
		size_type capacity;
		Block* next_block;

		explicit Block(size_type capacity);
		~Block();
	};

	Block* head;
	size_type block_capacity;
	size_type total_capacity;
	size_type total_size;

	void allocate_block();
	void copyFrom(const BucketStorage& different_block);
	template< typename IteratorType >
	IteratorType begin_impl() const noexcept;

	template< typename U, typename PointerType, typename ReferenceType >
	friend class common_iterator;
};

#include "bucket_storage_realization.hpp"
