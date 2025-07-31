package inmemory

import (
	"context"
	"homework/internal/domain"
	"homework/internal/usecase"
	"maps"
	"slices"
	"sync"
	"time"
)

type SensorRepository struct {
	mu           sync.RWMutex
	sensorsByID  map[int64]domain.Sensor
	sensorsBySN  map[string]domain.Sensor
	nextSensorID int64
}

func NewSensorRepository() *SensorRepository {
	return &SensorRepository{
		sensorsByID:  make(map[int64]domain.Sensor),
		sensorsBySN:  make(map[string]domain.Sensor),
		nextSensorID: 1,
	}
}

func (r *SensorRepository) SaveSensor(ctx context.Context, sensor *domain.Sensor) error {
	if err := ctx.Err(); err != nil {
		return err
	}

	if sensor == nil {
		return usecase.ErrSensorNotFound
	}

	r.mu.Lock()
	defer r.mu.Unlock()

	if _, ok := r.sensorsBySN[sensor.SerialNumber]; ok {
		return nil
	}

	sensor.ID = r.nextSensorID
	r.nextSensorID++

	sensor.RegisteredAt = time.Now()
	sensor.LastActivity = time.Time{}

	r.sensorsByID[sensor.ID] = *sensor
	r.sensorsBySN[sensor.SerialNumber] = *sensor

	return nil
}

func (r *SensorRepository) GetSensors(ctx context.Context) ([]domain.Sensor, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	return slices.Collect(maps.Values(r.sensorsByID)), nil
}

func (r *SensorRepository) GetSensorByID(ctx context.Context, id int64) (*domain.Sensor, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	sensor, ok := r.sensorsByID[id]
	if !ok {
		return nil, usecase.ErrSensorNotFound
	}

	return &sensor, nil
}

func (r *SensorRepository) GetSensorBySerialNumber(ctx context.Context, sn string) (*domain.Sensor, error) {
	if err := ctx.Err(); err != nil {
		return nil, err
	}

	sensor, ok := r.sensorsBySN[sn]
	if !ok {
		return nil, usecase.ErrSensorNotFound
	}

	return &sensor, nil
}
