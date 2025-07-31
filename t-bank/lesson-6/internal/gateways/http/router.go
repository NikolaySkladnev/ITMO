package http

import (
	"encoding/json"
	"errors"
	"homework/internal/domain"
	"homework/internal/usecase"
	"homework/validator"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

func setupRouter(r *gin.Engine, useCases UseCases) {
	r.HandleMethodNotAllowed = true

	r.GET("/ping", func(c *gin.Context) {
		c.String(http.StatusOK, "pong")
	})

	r.POST("/users", func(c *gin.Context) {
		var user domain.User

		if c.ContentType() != "application/json" {
			c.JSON(http.StatusUnsupportedMediaType, gin.H{"error": "Unsupported Media Type"})
		}

		if err := c.ShouldBindJSON(&user); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
		}

		if err := validator.Validate(user); err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		createdUser, err := useCases.User.RegisterUser(c, &user)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		c.JSON(http.StatusOK, createdUser)
	})

	r.OPTIONS("/users", func(c *gin.Context) {
		c.Header("Allow", "OPTIONS,POST")
		c.Status(http.StatusNoContent)
	})

	r.GET("/sensors", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		sensors, err := useCases.Sensor.GetSensors(c)
		if err != nil {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": err})
		}

		for _, sensor := range sensors {
			if err := validator.Validate(sensor); err != nil {
				c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
			}
		}

		c.JSON(http.StatusOK, sensors)
	})

	r.HEAD("/sensors", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		sensors, err := useCases.Sensor.GetSensors(c)
		if err != nil {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": err})
		}

		for _, sensor := range sensors {
			if err := validator.Validate(sensor); err != nil {
				c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
			}
		}

		data, err := json.Marshal(sensors)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		}
		c.Header("Content-Length", strconv.Itoa(len(data)))
	})

	r.POST("/sensors", func(c *gin.Context) {
		var sensor domain.Sensor

		if c.ContentType() != "application/json" {
			c.JSON(http.StatusUnsupportedMediaType, gin.H{"error": "Unsupported Media Type"})
		}

		if err := c.ShouldBindJSON(&sensor); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
		}

		if err := validator.Validate(sensor); err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		createdSensor, err := useCases.Sensor.RegisterSensor(c, &sensor)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		c.JSON(http.StatusOK, createdSensor)
	})

	r.OPTIONS("/sensors", func(c *gin.Context) {
		c.Header("Allow", "GET,POST,OPTIONS,HEAD")
		c.Status(http.StatusNoContent)
	})

	r.GET("/sensors/:id", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		sensor, err := useCases.Sensor.GetSensorByID(c, id)
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{"error": err})
			return
		}

		if err := validator.Validate(*sensor); err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		c.JSON(http.StatusOK, sensor)
	})

	r.HEAD("/sensors/:id", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		sensor, err := useCases.Sensor.GetSensorByID(c, id)
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{"error": err})
			return
		}

		if err := validator.Validate(*sensor); err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		data, err := json.Marshal(sensor)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		}

		c.Header("Content-Length", strconv.Itoa(len(data)))
	})

	r.POST("/users/:user_id/sensors", func(c *gin.Context) {
		var sensorOwner domain.SensorOwner

		if c.ContentType() != "application/json" {
			c.JSON(http.StatusUnsupportedMediaType, gin.H{"error": "Unsupported Media Type"})
		}

		userID, err := strconv.ParseInt(c.Param("user_id"), 10, 64)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		if err := c.ShouldBindJSON(&sensorOwner); err != nil {
			c.JSON(http.StatusBadRequest, err)
		}

		if err := validator.Validate(sensorOwner); err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		err = useCases.User.AttachSensorToUser(c, userID, sensorOwner.SensorID)
		if err != nil {
			if errors.Is(err, usecase.ErrUserNotFound) {
				c.JSON(http.StatusNotFound, err)
			} else {
				c.JSON(http.StatusUnprocessableEntity, gin.H{"error": "Unsupported Media Type"})
			}
		}

		c.Status(http.StatusCreated)
	})

	r.OPTIONS("/sensors/:id", func(c *gin.Context) {
		c.Header("Allow", "GET,HEAD,OPTIONS")
		c.Status(http.StatusNoContent)
	})

	r.GET("/users/:user_id/sensors", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		userID, err := strconv.ParseInt(c.Param("user_id"), 10, 64)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		sensors, err := useCases.User.GetUserSensors(c, userID)
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{"error": err})
		}

		for _, sensor := range sensors {
			if err := validator.Validate(sensor); err != nil {
				c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
			}
		}

		c.JSON(http.StatusOK, sensors)
	})

	r.HEAD("/users/:user_id/sensors", func(c *gin.Context) {
		if c.GetHeader("Accept") != "application/json" {
			c.JSON(http.StatusNotAcceptable, gin.H{"error": "Unsupported Media Type"})
		}

		userID, err := strconv.ParseInt(c.Param("user_id"), 10, 64)
		if err != nil {
			c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
		}

		sensors, err := useCases.User.GetUserSensors(c, userID)
		if err != nil {
			c.JSON(http.StatusNotFound, gin.H{"error": err})
		}

		for _, sensor := range sensors {
			if err := validator.Validate(sensor); err != nil {
				c.JSON(http.StatusUnprocessableEntity, gin.H{"error": err})
			}
		}

		data, err := json.Marshal(sensors)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err})
		}

		c.Header("Content-Length", strconv.Itoa(len(data)))
	})

	r.OPTIONS("/users/:user_id/sensors", func(c *gin.Context) {
		c.Header("Allow", "OPTIONS,POST,GET,HEAD")
		c.Status(http.StatusNoContent)
	})

	r.POST("/events", func(c *gin.Context) {
		var event domain.Event

		if c.ContentType() != "application/json" {
			c.JSON(http.StatusUnsupportedMediaType, gin.H{"error": "Unsupported Media Type"})
		}

		if err := c.ShouldBindJSON(&event); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
		}

		if err := validator.Validate(event); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
		}

		if event.Timestamp.IsZero() {
			event.Timestamp = time.Now()
		}

		if err := useCases.Event.ReceiveEvent(c, &event); err != nil {
			c.JSON(http.StatusUnprocessableEntity, err)
		}

		c.JSON(http.StatusCreated, event)
	})

	r.OPTIONS("/events", func(c *gin.Context) {
		c.Header("Allow", "OPTIONS,POST")
		c.Status(http.StatusNoContent)
	})
}
