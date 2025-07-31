package domain

import "time"

type SensorType string

const (
	SensorTypeContactClosure SensorType = "cc"
	SensorTypeADC            SensorType = "adc"
)

// Sensor - структура для хранения данных датчика
type Sensor struct {
	// ID - id датчика
	ID int64 `json:"id" validator:"min:1"`
	// SerialNumber - серийный номер датчика
	SerialNumber string `json:"serial_number" validator:"len:10"`
	// Type - тип датчика
	Type SensorType `json:"type"`
	// CurrentState - текущее состояние датчика
	CurrentState int64 `json:"current_state"`
	// Description - описание датчика
	Description string `json:"description"`
	// IsActive - активен ли датчик
	IsActive bool `json:"is_active"`
	// RegisteredAt - дата регистрации датчика
	RegisteredAt time.Time `json:"registered_at"`
	// LastActivity - дата последнего изменения состояния датчика
	LastActivity time.Time `json:"last_activity"`
}
