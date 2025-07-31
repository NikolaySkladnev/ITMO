package domain

// User - структура для хранения пользователя
type User struct {
	// ID - id пользователя
	ID int64 `json:"id" validator:"min:1"`
	// Name - имя пользователя
	Name string `json:"name" validator:"min:0"`
}

// SensorOwner - структура для связи пользователя и датчика
// Связь многие-ко-многим: пользователь может иметь доступ к нескольким датчикам, датчик может быть доступен для нескольких пользователей.
type SensorOwner struct {
	// UserID - id пользователя
	UserID int64 `json:"user_id" validator:"min:1"`
	// SensorID - id датчика
	SensorID int64 `json:"sensor_id" validator:"min:1"`
}
