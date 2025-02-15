#pragma once

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType >::common_iterator() noexcept :
	storage(nullptr), block(nullptr), index(0)
{
}

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType >::common_iterator(BucketStorage< T >* storage, Block* block, size_type index) noexcept
	: storage(storage), block(block), index(index)
{
}

template< typename T, typename PointerType, typename ReferenceType >
ReferenceType common_iterator< T, PointerType, ReferenceType >::operator*() const noexcept
{
	return block->data[index];
}

template< typename T, typename PointerType, typename ReferenceType >
PointerType common_iterator< T, PointerType, ReferenceType >::operator->() const noexcept
{
	return &block->data[index];
}

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType >& common_iterator< T, PointerType, ReferenceType >::operator++() noexcept
{
	increment();
	return *this;
}

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType > common_iterator< T, PointerType, ReferenceType >::operator++(int) noexcept
{
	common_iterator tmp = *this;
	increment();
	return tmp;
}

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType >& common_iterator< T, PointerType, ReferenceType >::operator--() noexcept
{
	decrement();
	return *this;
}

template< typename T, typename PointerType, typename ReferenceType >
common_iterator< T, PointerType, ReferenceType > common_iterator< T, PointerType, ReferenceType >::operator--(int) noexcept
{
	common_iterator tmp = *this;
	decrement();
	return tmp;
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator==(const common_iterator& different_block) const noexcept
{
	return equals(different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator!=(const common_iterator& different_block) const noexcept
{
	return !(*this == different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator<(const common_iterator& different_block) const noexcept
{
	return less_than(different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator<=(const common_iterator& different_block) const noexcept
{
	return *this < different_block || *this == different_block;
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator>(const common_iterator& different_block) const noexcept
{
	return !(*this <= different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator>=(const common_iterator& different_block) const noexcept
{
	return !(*this < different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
template< typename different_blockPointerType, typename different_blockReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator==(
	const common_iterator< T, different_blockPointerType, different_blockReferenceType >& different_block) const noexcept
{
	return storage == different_block.storage && block == different_block.block && index == different_block.index;
}

template< typename T, typename PointerType, typename ReferenceType >
template< typename different_blockPointerType, typename different_blockReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::operator!=(
	const common_iterator< T, different_blockPointerType, different_blockReferenceType >& different_block) const noexcept
{
	return !(*this == different_block);
}

template< typename T, typename PointerType, typename ReferenceType >
void common_iterator< T, PointerType, ReferenceType >::increment() noexcept
{
	do
	{
		if (++index >= block->capacity)
		{
			block = block->next_block;
			index = 0;
		}
	} while (block && block->active[index] != 0);
}

template< typename T, typename PointerType, typename ReferenceType >
void common_iterator< T, PointerType, ReferenceType >::decrement() noexcept
{
	do
	{
		if (index == 0)
		{
			Block* prev = storage->head;
			while (prev && prev->next_block != block)
			{
				prev = prev->next_block;
			}
			block = prev;
			index = block ? block->capacity - 1 : 0;
		}
		else
		{
			index--;
		}
	} while (block && block->active[index] != 0);
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::equals(const common_iterator& different_block) const noexcept
{
	return storage == different_block.storage && block == different_block.block && index == different_block.index;
}

template< typename T, typename PointerType, typename ReferenceType >
bool common_iterator< T, PointerType, ReferenceType >::less_than(const common_iterator& different_block) const noexcept
{
	if (block == different_block.block)
	{
		return index < different_block.index;
	}
	Block* curr = storage->head;
	while (curr)
	{
		if (curr == block)
			return true;
		if (curr == different_block.block)
			return false;
		curr = curr->next_block;
	}
	return false;
}

template< typename T >
typename BucketStorage< T >::iterator& BucketStorage< T >::iterator::operator=(const base& different_block) noexcept
{
	base::storage = different_block.storage;
	base::block = different_block.block;
	base::index = different_block.index;
	return *this;
}

template< typename T >
BucketStorage< T >::iterator::iterator(const base& different_block) noexcept : base(different_block)
{
}

template< typename T >
typename BucketStorage< T >::const_iterator& BucketStorage< T >::const_iterator::operator=(const base& different_block) noexcept
{
	base::storage = different_block.storage;
	base::block = different_block.block;
	base::index = different_block.index;
	return *this;
}

template< typename T >
BucketStorage< T >::const_iterator::const_iterator(const base& different_block) noexcept : base(different_block)
{
}
