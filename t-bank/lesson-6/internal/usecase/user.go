package usecase

import (
	"context"
	"homework/internal/domain"
)

type User struct {
	userRepo        UserRepository
	sensorOwnerRepo SensorOwnerRepository
	sensorRepo      SensorRepository
}

func NewUser(ur UserRepository, sor SensorOwnerRepository, sr SensorRepository) *User {
	return &User{
		userRepo:        ur,
		sensorOwnerRepo: sor,
		sensorRepo:      sr,
	}
}

func (u *User) RegisterUser(ctx context.Context, user *domain.User) (*domain.User, error) {
	if user == nil || user.Name == "" {
		return nil, ErrInvalidUserName
	}

	if err := u.userRepo.SaveUser(ctx, user); err != nil {
		return nil, err
	}

	return user, nil
}

func (u *User) AttachSensorToUser(ctx context.Context, userID, sensorID int64) error {
	if _, err := u.userRepo.GetUserByID(ctx, userID); err != nil {
		return err
	}

	if _, err := u.sensorRepo.GetSensorByID(ctx, sensorID); err != nil {
		return err
	}

	so := domain.SensorOwner{
		UserID:   userID,
		SensorID: sensorID,
	}

	return u.sensorOwnerRepo.SaveSensorOwner(ctx, so)
}

func (u *User) GetUserSensors(ctx context.Context, userID int64) ([]domain.Sensor, error) {
	if _, err := u.userRepo.GetUserByID(ctx, userID); err != nil {
		return nil, err
	}

	sensorOwners, err := u.sensorOwnerRepo.GetSensorsByUserID(ctx, userID)
	if err != nil {
		return nil, err
	}

	sensors := make([]domain.Sensor, 0, len(sensorOwners))
	for _, so := range sensorOwners {
		s, err := u.sensorRepo.GetSensorByID(ctx, so.SensorID)
		if err != nil {
			return nil, err
		}
		sensors = append(sensors, *s)
	}
	return sensors, nil
}
