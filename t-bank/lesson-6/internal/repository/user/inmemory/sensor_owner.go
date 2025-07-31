package inmemory

import (
	"context"
	"homework/internal/domain"
	"sync"
)

type SensorOwnerRepository struct {
	mu     sync.RWMutex
	owners map[int64]map[int64]struct{}
}

func NewSensorOwnerRepository() *SensorOwnerRepository {
	return &SensorOwnerRepository{
		owners: make(map[int64]map[int64]struct{}),
	}
}

func (r *SensorOwnerRepository) SaveSensorOwner(ctx context.Context, sensorOwner domain.SensorOwner) error {
	if err := ctx.Err(); err != nil {
		return err
	}

	r.mu.Lock()
	defer r.mu.Unlock()

	if _, ok := r.owners[sensorOwner.UserID]; !ok {
		r.owners[sensorOwner.UserID] = make(map[int64]struct{})
	}
	r.owners[sensorOwner.UserID][sensorOwner.SensorID] = struct{}{}
	return nil
}

func (r *SensorOwnerRepository) GetSensorsByUserID(ctx context.Context, userID int64) ([]domain.SensorOwner, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	var sensorOwners []domain.SensorOwner
	if sensorIDs, ok := r.owners[userID]; ok {
		for sensorID := range sensorIDs {
			sensorOwners = append(sensorOwners, domain.SensorOwner{
				UserID:   userID,
				SensorID: sensorID,
			})
		}
	}
	return sensorOwners, nil
}
