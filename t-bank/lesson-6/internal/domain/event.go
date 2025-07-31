package domain

import "time"

// Event - структура события по датчику
type Event struct {
	// Timestamp - время события
	Timestamp time.Time `json:"timestamp"`
	// SensorSerialNumber - серийный номер датчика
	SensorSerialNumber string `json:"sensor_serial_number" validator:"len:10"`
	// SensorID - id датчика
	SensorID int64 `json:"sensor_id " validator:"min:1"`
	// Payload - данные события
	Payload int64 `json:"payload"`
}
