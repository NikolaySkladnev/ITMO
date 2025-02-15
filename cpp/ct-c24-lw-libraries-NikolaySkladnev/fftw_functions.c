#include "fftw_functions.h"

#include "fftw3.h"

#include <math.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

int32_t cross_correlation(Buffer *buffer_1, Buffer *buffer_2)
{
	int32_t n = (buffer_1->buffer_size > buffer_2->buffer_size) ? buffer_1->buffer_size : buffer_2->buffer_size;

	if (buffer_1->buffer_size == n && buffer_2->buffer_size != n)
	{
		if (!(buffer_2->buffer = realloc(buffer_2->buffer, n * sizeof(double_t))))
		{
			fprintf(stderr, "Error: Failed to reallocate memory for audio buffer\n");
			return -1;
		}
	}
	else
	{
		if (!(buffer_1->buffer = realloc(buffer_1->buffer, n * sizeof(double_t))))
		{
			fprintf(stderr, "Error: Failed to reallocate memory for audio buffer\n");
			return -1;
		}
	}

	fftw_complex *data = fftw_malloc(4 * n * sizeof(fftw_complex));
	if (!data)
	{
		fprintf(stderr, "Error: Failed to allocate memory for FFTW arrays\n");
		return -1;
	}
	fftw_complex *in_1 = data;
	fftw_complex *in_2 = data + n;
	fftw_complex *out_1 = data + 2 * n;
	fftw_complex *out_2 = data + 3 * n;

	for (int32_t i = 0; i < n; i++)
	{
		in_1[i][0] = buffer_1->buffer[i];
		in_2[i][0] = buffer_2->buffer[i];
	}

	fftw_plan p1 = fftw_plan_dft_1d(n, in_1, out_1, FFTW_FORWARD, FFTW_ESTIMATE);
	fftw_execute(p1);
	fftw_destroy_plan(p1);

	fftw_plan p2 = fftw_plan_dft_1d(n, in_2, out_2, FFTW_FORWARD, FFTW_ESTIMATE);
	fftw_execute(p2);
	fftw_destroy_plan(p2);

	for (int32_t i = 0; i < n; i++)
	{
		double_t a_real = out_1[i][0];
		double_t a_imag = out_1[i][1];
		double_t b_real = out_2[i][0];
		double_t b_imag = out_2[i][1];

		out_1[i][0] = a_real * b_real + a_imag * b_imag;
		out_1[i][1] = a_imag * b_real - a_real * b_imag;
	}

	fftw_plan p3 = fftw_plan_dft_1d(n, out_1, in_1, FFTW_BACKWARD, FFTW_ESTIMATE);
	fftw_execute(p3);
	fftw_destroy_plan(p3);

	int32_t index = 0;
	double_t max = -INFINITY;
	for (int32_t i = 0; i < n; ++i)
	{
		if (data[i][0] > max)
		{
			max = data[i][0];
			index = i;
		}
	}

	if (index > n / 2)
	{
		index -= n;
	}

	fftw_free(data);

	return index;
}
