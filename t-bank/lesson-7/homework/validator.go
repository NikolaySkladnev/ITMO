package homework

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
	return &ValidationError{field: field, err: err}
}

func (e *ValidationError) Error() string {
	return fmt.Sprintf("%s: %s", e.field, e.err)
}

func (e *ValidationError) Unwrap() error {
	return e.err
}

func Validate(input any) error {
	v := reflect.ValueOf(input)
	t := reflect.TypeOf(input)

	if v.Kind() != reflect.Struct {
		return ErrNotStruct
	}

	var errs []error
	for i := 0; i < v.NumField(); i++ {
		fv := v.Field(i)
		ft := t.Field(i)
		errs = append(errs, validateField(fv, ft)...)
	}

	if len(errs) > 0 {
		return errors.Join(errs...)
	}
	return nil
}

func validateField(fv reflect.Value, ft reflect.StructField) []error {
	var errs []error

	tag := ft.Tag.Get("validate")
	parts := strings.SplitN(tag, ":", 2)

	if ft.PkgPath != "" && tag != "" {
		errs = append(errs, NewValidationError(ErrValidateForUnexportedFields, ft.Name))
	}

	switch fv.Kind() {
	case reflect.Int:
		errs = append(errs, validateInt(fv.Int(), ft.Name, parts)...)
	case reflect.String:
		errs = append(errs, validateString(fv.String(), ft.Name, parts)...)
	case reflect.Slice:
		errs = append(errs, validateSlice(fv, ft.Name, parts)...)
	default:
		errs = append(errs, NewValidationError(ErrInValidationFailed, ft.Name))
	}

	return errs
}

func validateInt(val int64, field string, parts []string) []error {
	var errs []error
	op := parts[0]
	arg := ""

	if len(parts) > 1 {
		arg = parts[1]
	}

	switch op {
	case "in":
		errs = checkIn(arg, field, val, func(s string) (int64, error) {
			return strconv.ParseInt(s, 10, 64)
		})
	case "min":
		errs = checkMinMax(val, field, arg, errs, ErrMinValidationFailed, func(a int, b int) bool {
			return a < b
		})
	case "max":
		errs = checkMinMax(val, field, arg, errs, ErrMaxValidationFailed, func(a int, b int) bool {
			return a > b
		})
	}
	return errs
}

func validateString(s, field string, parts []string) []error {
	var errs []error
	op := parts[0]
	arg := ""

	if len(parts) > 1 {
		arg = parts[1]
	}

	switch op {
	case "len":
		errs = checkLen(s, field, arg, errs)
	case "in":
		errs = checkIn(arg, field, s, func(str string) (string, error) {
			return str, nil
		})
	case "min":
		errs = checkMinMax(s, field, arg, errs, ErrMinValidationFailed, func(a int, b int) bool {
			return a < b
		})

	case "max":
		errs = checkMinMax(s, field, arg, errs, ErrMaxValidationFailed, func(a int, b int) bool {
			return a > b
		})
	}
	return errs
}

func checkLen(s string, field string, arg string, errs []error) []error {
	l, err := strconv.Atoi(arg)
	if err != nil || l < 0 {
		errs = append(errs, NewValidationError(ErrInvalidValidatorSyntax, field))
	} else if len(s) != l {
		errs = append(errs, NewValidationError(ErrLenValidationFailed, field))
	}
	return errs
}

func checkMinMax(s any, field string, arg string, errs []error, errorMinMax error, comparator func(int, int) bool) []error {
	m, err := strconv.Atoi(arg)
	if err != nil {
		errs = append(errs, NewValidationError(ErrInvalidValidatorSyntax, field))
	} else if comparator(conv(s), m) {
		errs = append(errs, NewValidationError(errorMinMax, field))
	}
	return errs
}

func conv(a any) int {
	v := reflect.ValueOf(a)
	switch v.Kind() {
	case reflect.Int64:
		return int(v.Int())
	case reflect.String:
		return len(v.String())
	default:
		return 0
	}
}
func checkIn[T comparable](arg string, field string, val T, parse func(string) (T, error)) []error {
	if arg == "" {
		return []error{NewValidationError(ErrInvalidValidatorSyntax, field)}
	}
	items := strings.Split(arg, ",")
	var allowed []T
	for _, s := range items {
		n, err := parse(s)
		if err != nil {
			return []error{NewValidationError(ErrInvalidValidatorSyntax, field)}
		}
		allowed = append(allowed, n)
	}
	if !slices.Contains(allowed, val) {
		return []error{NewValidationError(ErrInValidationFailed, field)}
	}
	return nil
}
func validateSlice(fv reflect.Value, field string, parts []string) []error {
	var errs []error
	if parts[0] == "" {
		return nil
	}
	for i := 0; i < fv.Len(); i++ {
		elem := fv.Index(i)
		if elem.Kind() == reflect.Interface {
			elem = elem.Elem()
		}
		switch elem.Kind() {
		case reflect.Int:
			errs = append(errs, validateInt(elem.Int(), field, parts)...)
		case reflect.String:
			errs = append(errs, validateString(elem.String(), field, parts)...)
		default:
			errs = append(errs, NewValidationError(ErrInValidationFailed, field))
		}
	}
	return errs
}
