package homework

import (
	"errors"
	"slices"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestValidateSlice(t *testing.T) {
	type args struct {
		v any
	}

	tests := []struct {
		name     string
		args     args
		wantErr  bool
		checkErr func(err error) bool
	}{
		{
			name: "valid int slice",
			args: args{
				v: struct {
					Max []int `validate:"max:10"`
					Min []int `validate:"min:10"`
					In  []int `validate:"in:1,2,3"`
				}{
					Max: []int{1, 2, 3, 4, 5},
					Min: []int{20, 30},
					In:  []int{1, 2, 3},
				},
			},
			wantErr: false,
		},
		{
			name: "valid string slice",
			args: args{
				v: struct {
					Len []string `validate:"len:3"`
					Max []string `validate:"max:10"`
					Min []string `validate:"min:2"`
					In  []string `validate:"in:dog,cat,name,tell"`
				}{
					Len: []string{"dog", "cat"},
					Max: []string{"dog", "cat", "name", "tag", "tell"},
					Min: []string{"dog", "cat", "name", "tag", "tell"},
					In:  []string{"dog", "cat", "tell"},
				},
			},
			wantErr: false,
		},
		{
			name: "valid int and string slice fields",
			args: args{
				v: struct {
					Tags  []string `validate:"in:cat,dog,tell"`
					Codes []int    `validate:"in:1,2,3"`
				}{
					Tags:  []string{"cat", "dog", "tell"},
					Codes: []int{1, 2},
				},
			},
			wantErr: false,
		},
		{
			name: "invalid max slice int string",
			args: args{
				v: struct {
					MaxErr       []int    `validate:"max:10"`
					MinStringErr []string `validate:"max:10"`
				}{
					MaxErr:       []int{1, 12},
					MinStringErr: []string{"112313123123", "asdasdasdasdasd"},
				},
			},
			wantErr: true,
			checkErr: func(err error) bool {
				expectedErrors := []struct {
					err   error
					field string
				}{
					{
						err:   ErrMaxValidationFailed,
						field: "MaxErr",
					},
					{
						err:   ErrMaxValidationFailed,
						field: "MinStringErr",
					},
				}
				coll, ok := err.(interface{ Unwrap() []error })
				if !ok {
					assert.Fail(t, "err должен быть создан через errors.Join")
					return false
				}
				errs := coll.Unwrap()
				foundErrors := expectedErrors
				for i := range errs {
					actualErr := &ValidationError{}
					if errors.As(errs[i], &actualErr) {
						for j := range expectedErrors {
							if errors.Is(actualErr, expectedErrors[j].err) && actualErr.field == expectedErrors[j].field {
								foundErrors = slices.Delete(foundErrors, j, j+1)
								break
							}
						}
					}
				}
				return len(foundErrors) == 0
			},
		},
		{
			name: "invalid min slice int string",
			args: args{
				v: struct {
					MinIntErr    []int    `validate:"min:5"`
					MinStringErr []string `validate:"min:5"`
				}{
					MinIntErr:    []int{1, 2},
					MinStringErr: []string{"dog", "cat"},
				},
			},
			wantErr: true,
			checkErr: func(err error) bool {
				expectedErrors := []struct {
					err   error
					field string
				}{
					{
						err:   ErrMinValidationFailed,
						field: "MinIntErr",
					},
					{
						err:   ErrMinValidationFailed,
						field: "MinStringErr",
					},
				}
				coll, ok := err.(interface{ Unwrap() []error })
				if !ok {
					assert.Fail(t, "err должен быть создан через errors.Join")
					return false
				}
				errs := coll.Unwrap()
				foundErrors := expectedErrors
				for i := range errs {
					actualErr := &ValidationError{}
					if errors.As(errs[i], &actualErr) {
						for j := range expectedErrors {
							if errors.Is(actualErr, expectedErrors[j].err) && actualErr.field == expectedErrors[j].field {
								foundErrors = slices.Delete(foundErrors, j, j+1)
								break
							}
						}
					}
				}
				return len(foundErrors) == 0
			},
		},
		{
			name: "invalid in slice int string",
			args: args{
				v: struct {
					InIntErr    []int    `validate:"in:5,6,8"`
					InStringErr []string `validate:"in:dog,cat,name,tell"`
				}{
					InIntErr:    []int{1, 2, 3},
					InStringErr: []string{"no", "not"},
				},
			},
			wantErr: true,
			checkErr: func(err error) bool {
				expectedErrors := []struct {
					err   error
					field string
				}{
					{
						err:   ErrInValidationFailed,
						field: "InIntErr",
					},
					{
						err:   ErrInValidationFailed,
						field: "InStringErr",
					},
				}
				coll, ok := err.(interface{ Unwrap() []error })
				if !ok {
					assert.Fail(t, "err должен быть создан через errors.Join")
					return false
				}
				errs := coll.Unwrap()
				foundErrors := expectedErrors
				for i := range errs {
					actualErr := &ValidationError{}
					if errors.As(errs[i], &actualErr) {
						for j := range expectedErrors {
							if errors.Is(actualErr, expectedErrors[j].err) && actualErr.field == expectedErrors[j].field {
								foundErrors = slices.Delete(foundErrors, j, j+1)
								break
							}
						}
					}
				}
				return len(foundErrors) == 0
			},
		},
		{
			name: "invalid len slice string",
			args: args{
				v: struct {
					LenErr []string `validate:"len:10"`
				}{
					LenErr: []string{"dog", "cat", "tell"},
				},
			},
			wantErr: true,
			checkErr: func(err error) bool {
				expectedErrors := []struct {
					err   error
					field string
				}{
					{
						err:   ErrLenValidationFailed,
						field: "LenErr",
					},
				}
				coll, ok := err.(interface{ Unwrap() []error })
				if !ok {
					assert.Fail(t, "err должен быть создан через errors.Join")
					return false
				}
				errs := coll.Unwrap()
				foundErrors := expectedErrors
				for i := range errs {
					actualErr := &ValidationError{}
					if errors.As(errs[i], &actualErr) {
						for j := range expectedErrors {
							if errors.Is(actualErr, expectedErrors[j].err) && actualErr.field == expectedErrors[j].field {
								foundErrors = slices.Delete(foundErrors, j, j+1)
								break
							}
						}
					}
				}
				return len(foundErrors) == 0
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := Validate(tt.args.v)
			if tt.wantErr {
				assert.Error(t, err)
				assert.True(t, tt.checkErr(err), "test expect an error, but got wrong error type")
			} else {
				assert.NoError(t, err)
			}
		})
	}
}
