package inmemory

import (
	"context"
	"homework/internal/domain"
	"homework/internal/usecase"
	"sync"
)

type UserRepository struct {
	mu         sync.RWMutex
	users      map[int64]domain.User
	nextUserID int64
}

func NewUserRepository() *UserRepository {
	return &UserRepository{
		users:      make(map[int64]domain.User),
		nextUserID: 1,
	}
}

func (r *UserRepository) SaveUser(ctx context.Context, user *domain.User) error {
	if err := ctx.Err(); err != nil {
		return err
	}

	if user == nil {
		return usecase.ErrUserNotFound
	}

	r.mu.Lock()
	defer r.mu.Unlock()

	user.ID = r.nextUserID
	r.nextUserID++

	r.users[user.ID] = *user

	return nil
}

func (r *UserRepository) GetUserByID(ctx context.Context, id int64) (*domain.User, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	user, ok := r.users[id]
	if !ok {
		return nil, usecase.ErrUserNotFound
	}

	return &user, nil
}
