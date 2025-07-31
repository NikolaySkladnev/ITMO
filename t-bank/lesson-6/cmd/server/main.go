package main

import (
	"errors"
	"fmt"
	httpGateway "homework/internal/gateways/http"
	eventRepository "homework/internal/repository/event/inmemory"
	sensorRepository "homework/internal/repository/sensor/inmemory"
	userRepository "homework/internal/repository/user/inmemory"
	"homework/internal/usecase"
	"log"
	"net/http"
	"os"
	"strconv"
)

func main() {
	er := eventRepository.NewEventRepository()
	sr := sensorRepository.NewSensorRepository()
	ur := userRepository.NewUserRepository()
	sor := userRepository.NewSensorOwnerRepository()

	useCases := httpGateway.UseCases{
		Event:  usecase.NewEvent(er, sr),
		Sensor: usecase.NewSensor(sr),
		User:   usecase.NewUser(ur, sor, sr),
	}

	host := os.Getenv("HTTP_HOST")
	port := os.Getenv("HTTP_PORT")

	if host == "" {
		host = "localhost"
	}
	if port == "" {
		port = "8080"
	}

	portUint16, err := strconv.ParseUint(port, 10, 16)
	if err != nil {
		_, _ = fmt.Fprintln(os.Stderr, err)
	}

	r := httpGateway.NewServer(useCases, httpGateway.WithHost(host), httpGateway.WithPort(uint16(portUint16)))

	if err := r.Run(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		log.Printf("error during server shutdown: %v", err)
	}
}
