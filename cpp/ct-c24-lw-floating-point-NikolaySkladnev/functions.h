#pragma once

#define IS_ZERO(number, shift)                                                                                         \
	((number->mantissa == 0) &                                                                                         \
	 (number->exponent == ((shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_MIN_EXPONENT + 1 : HALF_PRECISION_MIN_EXPONENT + 1)))

#define IS_INF(number, shift)                                                                                                                 \
	((number->mantissa == ((shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_MANTISSA : HALF_PRECISION_FIRST_BIT_MANTISSA)) & \
	 (number->exponent == ((shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT)))

#define IS_NAN(number, shift)                                                                                          \
	((number->mantissa != 0) &                                                                                         \
	 (number->exponent == ((shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT)))
