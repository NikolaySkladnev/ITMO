package usecase

import (
	"context"
	"errors"
	"homework/internal/domain"
	"time"
)

type Sensor struct {
	sensorRepo SensorRepository
}

func NewSensor(sr SensorRepository) *Sensor {
	return &Sensor{
		sensorRepo: sr,
	}
}

func (s *Sensor) RegisterSensor(ctx context.Context, sensor *domain.Sensor) (*domain.Sensor, error) {
	if sensor.Type != domain.SensorTypeADC && sensor.Type != domain.SensorTypeContactClosure {
		return nil, ErrWrongSensorType
	}

	if len(sensor.SerialNumber) != 10 {
		return nil, ErrWrongSensorSerialNumber
	}

	for _, ch := range sensor.SerialNumber {
		if ch < '0' || ch > '9' {
			return nil, ErrWrongSensorSerialNumber
		}
	}

	existing, err := s.sensorRepo.GetSensorBySerialNumber(ctx, sensor.SerialNumber)
	if err != nil && !errors.Is(err, ErrSensorNotFound) {
		return nil, err
	}

	if existing != nil {
		return existing, nil
	}

	sensor.RegisteredAt = time.Time{}
	sensor.LastActivity = time.Time{}
	if err := s.sensorRepo.SaveSensor(ctx, sensor); err != nil {
		return nil, err
	}
	return sensor, nil
}

func (s *Sensor) GetSensors(ctx context.Context) ([]domain.Sensor, error) {
	return s.sensorRepo.GetSensors(ctx)
}

func (s *Sensor) GetSensorByID(ctx context.Context, id int64) (*domain.Sensor, error) {
	return s.sensorRepo.GetSensorByID(ctx, id)
}
