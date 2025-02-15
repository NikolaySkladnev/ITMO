#pragma once

#include <iostream>

template< typename T, typename PointerType, typename ReferenceType >
class common_iterator
{
  public:
	using iterator_category = std::bidirectional_iterator_tag;
	using value_type = T;
	using difference_type = std::ptrdiff_t;
	using pointer = PointerType;
	using reference = ReferenceType;
	using Block = typename BucketStorage< T >::Block;
	using size_type = typename BucketStorage< T >::size_type;

	common_iterator() noexcept;
	common_iterator(BucketStorage< T >* storage, Block* block, size_type index) noexcept;

	reference operator*() const noexcept;
	pointer operator->() const noexcept;

	common_iterator& operator++() noexcept;
	common_iterator operator++(int) noexcept;
	common_iterator& operator--() noexcept;
	common_iterator operator--(int) noexcept;

	bool operator==(const common_iterator& different_block) const noexcept;
	bool operator!=(const common_iterator& different_block) const noexcept;
	bool operator<(const common_iterator& different_block) const noexcept;
	bool operator<=(const common_iterator& different_block) const noexcept;
	bool operator>(const common_iterator& different_block) const noexcept;
	bool operator>=(const common_iterator& different_block) const noexcept;

	template< typename NewBlock_PointerType, typename NewBlock_OtherReferenceType >
	bool operator==(const common_iterator< T, NewBlock_PointerType, NewBlock_OtherReferenceType >& different_block) const noexcept;

	template< typename NewBlock_PointerType, typename NewBlock_OtherReferenceType >
	bool operator!=(const common_iterator< T, NewBlock_PointerType, NewBlock_OtherReferenceType >& different_block) const noexcept;

  protected:
	BucketStorage< T >* storage;
	Block* block;
	size_type index;

	void increment() noexcept;
	void decrement() noexcept;
	bool equals(const common_iterator& different_block) const noexcept;
	bool less_than(const common_iterator& different_block) const noexcept;

	template< typename, typename, typename >
	friend class common_iterator;

	friend class BucketStorage< T >;
};

template< typename T >
class BucketStorage< T >::iterator : public common_iterator< T, T*, T& >
{
	using base = common_iterator< T, T*, T& >;

  public:
	using base::base;

	iterator& operator=(const base& different_block) noexcept;
	iterator(const base& different_block) noexcept;
};

template< typename T >
class BucketStorage< T >::const_iterator : public common_iterator< T, const T*, const T& >
{
	using base = common_iterator< T, const T*, const T& >;

  public:
	using base::base;

	const_iterator& operator=(const base& different_block) noexcept;
	const_iterator(const base& different_block) noexcept;
};

#include "bucket_iterators_realization.hpp"
