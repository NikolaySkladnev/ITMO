package inmemory

import (
	"context"
	"homework/internal/domain"
	"homework/internal/usecase"
	"sync"
)

type EventRepository struct {
	mu     sync.RWMutex
	events map[int64]domain.Event
}

func NewEventRepository() *EventRepository {
	return &EventRepository{
		events: make(map[int64]domain.Event),
	}
}

func (r *EventRepository) SaveEvent(ctx context.Context, event *domain.Event) error {
	if err := ctx.Err(); err != nil {
		return err
	}

	if event == nil {
		return usecase.ErrEventNotFound
	}

	r.mu.Lock()
	defer r.mu.Unlock()

	if existing, ok := r.events[event.SensorID]; ok {
		if event.Timestamp.After(existing.Timestamp) {
			r.update(event)
		}
		return nil
	}
	r.update(event)
	return nil
}

func (r *EventRepository) GetLastEventBySensorID(ctx context.Context, id int64) (*domain.Event, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	event, ok := r.events[id]
	if !ok {
		return nil, usecase.ErrEventNotFound
	}

	return &event, nil
}

func (r *EventRepository) update(event *domain.Event) {
	r.events[event.SensorID] = *event
}
