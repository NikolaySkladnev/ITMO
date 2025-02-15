#include "constants.h"
#include "functions.h"
#include "return_codes.h"

#include <stdint.h>
#include <stdio.h>
#include <string.h>

typedef struct Number
{
	uint64_t mantissa;
	int16_t exponent;
	int8_t sign;
} Number;

int error(char *message)
{
	fprintf(stderr, "%s", message);
	return ERROR_ARGUMENTS_INVALID;
}

typedef struct Rest
{
	int8_t first;
	uint32_t rest;
	int16_t len;
} Rest;

const char *check(Number *number, int16_t shift)
{
	int8_t format = (shift == SINGLE_PRECISION_MANTISSA) ? 1 : 0;

	if (number->exponent == ((format) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT))
	{
		if (number->mantissa == ((format) ? SINGLE_PRECISION_FIRST_BIT_MANTISSA : HALF_PRECISION_FIRST_BIT_MANTISSA))
		{
			if (number->sign == 0)
				return "inf";
			else
				return "-inf";
		}
		else
			return "nan";
	}
	return NULL;
}

int8_t check_for_nan(Number *result, Number *number1, Number *number2, int16_t shift)
{
	if (IS_NAN(number1, shift) || IS_NAN(number2, shift))
	{
		result->exponent = (shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT;
		result->mantissa =
			(shift == SINGLE_PRECISION_MANTISSA) ? (SINGLE_PRECISION_FIRST_BIT_MANTISSA + 1) : (HALF_PRECISION_FIRST_BIT_MANTISSA + 1);
		return 1;
	}
	return 0;
}

void normalize(Number *number, Rest *rest, int16_t shift)
{
	uint64_t mantissa = number->mantissa;
	int16_t exponent = number->exponent;
	uint32_t rest_t = rest->rest;
	int16_t len = rest->len;

	if (rest->first != 1)
	{
		rest_t = 0;
		len = 0;
	}
	if (mantissa == 0)
		return;
	if (mantissa >> shift == 0)
	{
		while (mantissa >> shift != 1)
		{
			mantissa <<= 1;
			exponent--;
		}
	}
	else
	{
		int64_t count;
		count = (1 << len);
		while (mantissa >> shift != 1)
		{
			rest_t += (mantissa & 0x1) * count;
			len += 1;
			mantissa >>= 1;
			count <<= 1;
			exponent += 1;
		}
	}
	number->mantissa = mantissa;
	number->exponent = exponent;
	rest->rest = rest_t;
	rest->len = len;
}

int roundNumber(Number *number, Rest *rest, int8_t round, int16_t shift)
{
	int16_t size = (shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_LENGTH : HALF_PRECISION_LENGTH;

	uint64_t mantissa = number->mantissa;
	uint32_t rest_t = rest->rest;
	int8_t sign = number->sign;
	int16_t len = rest->len;

	switch (round)
	{
	case '0':
		break;
	case '1':
		if ((rest_t >> (len - 1)) == 1)
		{
			if (rest_t << (size - (len) + 1) == 0)
			{
				mantissa += mantissa & 0x1;
				break;
			}
			mantissa++;
		}
		break;
	case '2':
		mantissa += (rest_t > 0) ? ((sign) ? -1 : 1) : 0;
		break;
	case '3':
		mantissa -= (rest_t > 0) ? sign : 0;
		break;
	default:
		return error("incorrect round");
	}
	number->mantissa = mantissa;
	number->sign = sign;
	rest->rest = rest_t;
	rest->len = len;
	return 0;
}

void add(Number *result, Number *number1, Number *number2, int16_t shift)
{
	if (check_for_nan(result, number1, number2, shift))
		return;

	if (IS_ZERO(number1, shift) & IS_ZERO(number2, shift))
	{
		result->mantissa = 0;
		result->exponent = 0;
		result->sign = 0;
		return;
	}
	else if (IS_ZERO(number1, shift))
	{
		result->mantissa = number2->mantissa;
		result->exponent = number2->exponent;
		result->sign = number2->sign;
		return;
	}
	else if (IS_ZERO(number2, shift))
	{
		result->mantissa = number1->mantissa;
		result->exponent = number1->exponent;
		result->sign = number1->sign;
		return;
	}

	uint64_t mantissa_res;
	int8_t sign_res;
	int8_t sign_num1 = number1->sign;
	int8_t sign_num2 = number2->sign;
	uint64_t mantissa_num1 = number1->mantissa;
	uint64_t mantissa_num2 = number2->mantissa;
	int16_t exponent_num1 = number1->exponent;
	int16_t exponent_num2 = number2->exponent;

	Number *first_arg = number1;
	Number *second_arg = number2;
	if (first_arg->exponent < second_arg->exponent)
	{
		first_arg = number2;
		second_arg = number1;
	}
	result->exponent = first_arg->exponent;
	uint64_t mantissa = second_arg->mantissa;
	int16_t exponent = second_arg->exponent;
	int16_t exponent_c = first_arg->exponent;

	int8_t sign = (sign_num1 == sign_num2);

	if (sign == 1)
		sign_res = sign_num1;
	else if (mantissa_num1 > mantissa_num2)
		sign_res = sign_num1;
	else if (mantissa_num1 < mantissa_num2)
		sign_res = sign_num2;
	else if (exponent_num1 == exponent_num2)
		sign_res = 0;
	else if (exponent_num1 > exponent_num2)
		sign_res = sign_num1;
	else
		sign_res = sign_num2;

	while (exponent != exponent_c)
	{
		mantissa >>= 1;
		exponent++;
	}

	uint64_t mantissa_first = first_arg->mantissa;
	if (sign)
		mantissa_res = mantissa + mantissa_first;
	else if (mantissa >= mantissa_first)
		mantissa_res = mantissa - mantissa_first;
	else
		mantissa_res = mantissa_first - mantissa;

	second_arg->mantissa = mantissa;
	second_arg->exponent = exponent;
	result->sign = sign_res;
	result->mantissa = mantissa_res;
	number1->sign = sign_num1;
	number2->sign = sign_num2;
	number1->mantissa = mantissa_num1;
	number2->mantissa = mantissa_num2;
	number1->exponent = exponent_num1;
	number2->exponent = exponent_num2;
}

void multiply(Number *result, Number *number1, Number *number2, int16_t shift, Rest *rest)
{
	if (check_for_nan(result, number1, number2, shift))
		return;

	result->sign = number1->sign ^ number2->sign;
	result->exponent = number1->exponent + number2->exponent;
	result->mantissa = number1->mantissa * number2->mantissa;
	int16_t count_shift = 1;
	int16_t count = 1;
	rest->rest = 0;
	rest->len = 0;

	uint64_t mantissa = result->mantissa;
	uint32_t rest_rest = rest->rest;
	int16_t rest_len = rest->len;
	while (count_shift != shift + 1)
	{
		rest_rest += (mantissa & 0x1) * count;
		(rest_len)++;
		mantissa >>= 1;
		count <<= 1;
		count_shift++;
	}
	rest->len = rest_len;
	rest->rest = rest_rest;
	result->mantissa = mantissa;
	rest->first = 1;
}

void divide(Number *result, Number *number1, Number *number2, int16_t shift)
{
	if (check_for_nan(result, number1, number2, shift))
		return;

	if (IS_ZERO(number2, shift) == 1 & IS_ZERO(number1, shift) == 0)
	{
		result->exponent = (shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT;
		result->mantissa = (shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_MANTISSA : HALF_PRECISION_FIRST_BIT_MANTISSA;
		result->sign = number1->sign;
		return;
	}
	else if (IS_INF(number1, shift) & IS_INF(number2, shift))
	{
		result->exponent = (shift == SINGLE_PRECISION_MANTISSA) ? SINGLE_PRECISION_FIRST_BIT_EXPONENT : HALF_PRECISION_FIRST_BIT_EXPONENT;
		result->mantissa =
			(shift == SINGLE_PRECISION_MANTISSA) ? (SINGLE_PRECISION_FIRST_BIT_MANTISSA + 1) : (HALF_PRECISION_FIRST_BIT_MANTISSA + 1);
		return;
	}
	result->sign = number1->sign ^ number2->sign;
	result->exponent = number1->exponent - number2->exponent;
	number1->mantissa <<= shift;
	result->mantissa = number1->mantissa / number2->mantissa;
}

int parseNumber(char *number_to_read, Number *number, int16_t shift)
{
	int32_t number_to_parse;
	if (sscanf(number_to_read, "%x", &number_to_parse) != 1)
	{
		return error("failed number reading attempt");
	}

	int8_t format = (shift == SINGLE_PRECISION_MANTISSA);

	number->sign = number_to_parse >> ((format == 1) ? (SINGLE_PRECISION_LENGTH - 1) : (HALF_PRECISION_LENGTH - 1));
	number->exponent =
		((number_to_parse >> shift) & ((format == 1) ? SINGLE_PRECISION_BITS_OF_EXPONENT : HALF_PRECISION_BITS_OF_EXPONENT)) -
		(format ? SINGLE_PRECISION_ADDITION : HALF_PRECISION_ADDITION);
	number->mantissa = number_to_parse & ((format == 1) ? SINGLE_PRECISION_BITS_OF_MANTISSA : HALF_PRECISION_BITS_OF_MANTISSA);

	if (number->exponent == (shift == SINGLE_PRECISION_MANTISSA ? SINGLE_PRECISION_MIN_EXPONENT : HALF_PRECISION_MIN_EXPONENT))
	{
		Rest rest;
		number->exponent++;
		normalize(number, &rest, shift);
	}
	else
		number->mantissa += (shift == SINGLE_PRECISION_MANTISSA ? SINGLE_PRECISION_FIRST_BIT_MANTISSA : HALF_PRECISION_FIRST_BIT_MANTISSA);

	return 0;
}

void print(Number *number, int16_t shift)
{
	const char *checked = check(number, shift);
	if (checked != NULL)
	{
		printf("%s\n", checked);
		return;
	}

	int8_t format = (shift == SINGLE_PRECISION_MANTISSA);
	if ((number->mantissa == 0) &
		(number->exponent == 0 || number->exponent == (SINGLE_PRECISION_MIN_EXPONENT + 1) ||
		 number->exponent == (HALF_PRECISION_MIN_EXPONENT + 1)))
	{
		printf("0x0.%.*llxp+0\n",
			   (format == 1) ? 6 : 3,
			   ((number->mantissa & ((format == 1) ? SINGLE_PRECISION_BITS_OF_MANTISSA : HALF_PRECISION_BITS_OF_MANTISSA))
				<< ((format == 1) ? 1 : 2)));
		return;
	}
	if (number->sign)
		printf("-0x1.%.*llxp%+hi\n",
			   (format == 1) ? 6 : 3,
			   ((number->mantissa & ((format == 1) ? SINGLE_PRECISION_BITS_OF_MANTISSA : HALF_PRECISION_BITS_OF_MANTISSA))
				<< ((format == 1) ? 1 : 2)),
			   number->exponent);
	else
		printf("0x1.%.*llxp%+hi\n",
			   (format == 1) ? 6 : 3,
			   ((number->mantissa & ((format == 1) ? SINGLE_PRECISION_BITS_OF_MANTISSA : HALF_PRECISION_BITS_OF_MANTISSA))
				<< ((format == 1) ? 1 : 2)),
			   number->exponent);
}

int main(int argc, char *argv[])
{
	if (argc != 4 && argc != 6)
		return error("no arguments");

	if (strlen(argv[1]) != 1)
		error("incorrect format");
	int8_t format = argv[1][0];

	int16_t shift;
	if (format == 'f')
		shift = SINGLE_PRECISION_MANTISSA;
	else if (format == 'h')
		shift = HALF_PRECISION_MANTISSA;
	else
		return error("incorrect format");

	if (strlen(argv[2]) != 1)
		return error("incorrect round");
	int8_t round = argv[2][0];

	if (!('0' <= round && round < '4'))
		return error("incorrect round");

	if (argc == 4)
	{
		Number number;
		parseNumber(argv[3], &number, shift);
		print(&number, shift);
	}
	else
	{
		if (strlen(argv[4]) != 1)
			return error("incorrect operation");
		int8_t operation = argv[4][0];

		Number number1, number2, result;
		parseNumber(argv[3], &number1, shift);
		parseNumber(argv[5], &number2, shift);
		Rest rest;
		switch (operation)
		{
		case '*':
			multiply(&result, &number1, &number2, shift, &rest);
			break;
		case '+':
			add(&result, &number1, &number2, shift);
			break;
		case '-':
			number2.sign ^= 1;
			add(&result, &number1, &number2, shift);
			break;
		case '/':
			divide(&result, &number1, &number2, shift);
			break;
		default:
			return error("invalid operation");
		}
		normalize(&result, &rest, shift);
		roundNumber(&result, &rest, round, shift);
		print(&result, shift);
		return SUCCESS;
	}
}
