#ifndef CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFTW_FUNCTIONS_H
#define CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFTW_FUNCTIONS_H

#include "fftw_functions.h"
#include "return_codes.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>

#include <math.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct buffer
{
	double_t *buffer;
	int32_t buffer_size;
} Buffer;

int32_t cross_correlation(Buffer *audioBuffer1, Buffer *audioBuffer2);

#endif	  // CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFTW_FUNCTIONS_H