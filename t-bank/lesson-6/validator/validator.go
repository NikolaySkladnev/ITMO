package validator

import (
	"errors"
	"fmt"
	"reflect"
	"slices"
	"strconv"
	"strings"
)

var (
	ErrNotStruct                   = errors.New("wrong argument given, should be a struct")
	ErrInvalidValidatorSyntax      = errors.New("invalid validator syntax")
	ErrValidateForUnexportedFields = errors.New("validation for unexported field is not allowed")
	ErrLenValidationFailed         = errors.New("len validation failed")
	ErrInValidationFailed          = errors.New("in validation failed")
	ErrMaxValidationFailed         = errors.New("max validation failed")
	ErrMinValidationFailed         = errors.New("min validation failed")
)

type ValidationError struct {
	field string
	err   error
}

func NewValidationError(err error, field string) error {
	return &ValidationError{
		field: field,
		err:   err,
	}
}

func (e *ValidationError) Error() string {
	return fmt.Sprintf("%s: %s", e.field, e.err)
}

func (e *ValidationError) Unwrap() error {
	return e.err
}

func Validate(el any) error {
	val := reflect.ValueOf(el)
	typ := reflect.TypeOf(el)

	if typ.Kind() != reflect.Struct {
		return ErrNotStruct
	}

	var errs []error

	for i := 0; i < val.NumField(); i++ {
		fieldVal := val.Field(i)
		fieldTyp := typ.Field(i)

		tag := fieldTyp.Tag.Get("validate")
		if tag == "" {
			continue
		}

		if !fieldTyp.IsExported() {
			return ErrValidateForUnexportedFields
		}

		validators := strings.Split(tag, ";")
		for _, validator := range validators {
			parts := strings.Split(validator, ":")
			if len(parts) != 2 {
				errs = append(errs, NewValidationError(ErrInvalidValidatorSyntax, fieldTyp.Name))
				continue
			}
			function := parts[0]
			arg := parts[1]

			switch function {
			case "len":
				errs = checkLen(fieldTyp, errs, arg, fieldVal)
			case "in":
				errs = checkIn(fieldTyp, errs, arg, fieldVal)
			case "max":
				errs = checkMax(fieldTyp, errs, arg, fieldVal)
			case "min":
				errs = checkMin(fieldTyp, errs, arg, fieldVal)
			default:
				errs = append(errs, NewValidationError(ErrInvalidValidatorSyntax, fieldTyp.Name))
			}
		}
	}

	if len(errs) > 0 {
		return errors.Join(errs...)
	}

	return nil
}

func checkMax(fieldTyp reflect.StructField, errs []error, arg string, fieldVal reflect.Value) []error {
	return checkBy(fieldTyp, errs, arg, fieldVal, ErrMaxValidationFailed, func(a, b int) bool { return a > b })
}

func checkMin(fieldTyp reflect.StructField, errs []error, arg string, fieldVal reflect.Value) []error {
	return checkBy(fieldTyp, errs, arg, fieldVal, ErrMinValidationFailed, func(a, b int) bool { return a < b })
}

func checkBy(fieldTyp reflect.StructField, errs []error, arg string, fieldVal reflect.Value, errType error, comparator func(a, b int) bool) []error {
	threshold, err := strconv.Atoi(arg)
	if err != nil {
		return append(errs, NewValidationError(ErrInvalidValidatorSyntax, fieldTyp.Name))
	}
	switch fieldVal.Kind() {
	case reflect.String:
		if comparator(len(fieldVal.String()), threshold) {
			return append(errs, NewValidationError(errType, fieldTyp.Name))
		}
	case reflect.Int:
		if comparator(int(fieldVal.Int()), threshold) {
			return append(errs, NewValidationError(errType, fieldTyp.Name))
		}
	case reflect.Slice:
		for i := 0; i < fieldVal.Len(); i++ {
			tempErrs := checkBy(fieldTyp, []error{}, arg, fieldVal.Index(i), errType, comparator)
			if len(tempErrs) > 0 {
				return append(errs, NewValidationError(errType, fieldTyp.Name))
			}
		}
	default:
		return append(errs, NewValidationError(errType, fieldTyp.Name))
	}
	return errs
}

func checkLen(fieldTyp reflect.StructField, errs []error, arg string, fieldVal reflect.Value) []error {
	expectedLength, err := strconv.Atoi(arg)
	if err != nil || expectedLength < 0 {
		return append(errs, NewValidationError(ErrInvalidValidatorSyntax, fieldTyp.Name))
	}
	if fieldVal.Kind() == reflect.Slice {
		for i := 0; i < fieldVal.Len(); i++ {
			tempErrs := checkLen(fieldTyp, []error{}, arg, fieldVal.Index(i))
			if len(tempErrs) > 0 {
				return append(errs, NewValidationError(ErrLenValidationFailed, fieldTyp.Name))
			}
		}
		return errs
	}
	if fieldVal.Kind() != reflect.String {
		return append(errs, NewValidationError(ErrLenValidationFailed, fieldTyp.Name))
	}
	if len(fieldVal.String()) != expectedLength {
		return append(errs, NewValidationError(ErrLenValidationFailed, fieldTyp.Name))
	}
	return errs
}

func checkIn(fieldTyp reflect.StructField, errs []error, arg string, fieldVal reflect.Value) []error {
	args := strings.Split(arg, ",")

	switch fieldVal.Kind() {
	case reflect.String:
		errs = checkArgs[string](fieldTyp, errs, args, fieldVal,
			func(s string) (string, error) { return s, nil },
			func(v reflect.Value) string { return v.String() },
		)
	case reflect.Int:
		errs = checkArgs[int](fieldTyp, errs, args, fieldVal,
			func(s string) (int, error) { return strconv.Atoi(s) },
			func(v reflect.Value) int { return int(v.Int()) },
		)
	case reflect.Slice:
		for i := 0; i < fieldVal.Len(); i++ {
			tempErrs := checkIn(fieldTyp, []error{}, arg, fieldVal.Index(i))
			if len(tempErrs) > 0 {
				return append(errs, NewValidationError(ErrInValidationFailed, fieldTyp.Name))
			}
		}
	default:
		errs = append(errs, NewValidationError(ErrInValidationFailed, fieldTyp.Name))
	}
	return errs
}

func checkArgs[T comparable](fieldTyp reflect.StructField, errs []error, args []string, fieldVal reflect.Value, parseFn func(string) (T, error), getValue func(reflect.Value) T) []error {
	allowed, err := parseArgs[T](args, parseFn)
	if err != nil || len(allowed) == 0 {
		return append(errs, NewValidationError(ErrInvalidValidatorSyntax, fieldTyp.Name))
	}
	if !slices.Contains(allowed, getValue(fieldVal)) {
		errs = append(errs, NewValidationError(ErrInValidationFailed, fieldTyp.Name))
	}
	return errs
}

func parseArgs[T comparable](args []string, parseFn func(string) (T, error)) ([]T, error) {
	var allowed []T
	for _, s := range args {
		if s == "" {
			continue
		}
		val, err := parseFn(s)
		if err != nil {
			return nil, err
		}
		allowed = append(allowed, val)
	}
	return allowed, nil
}
