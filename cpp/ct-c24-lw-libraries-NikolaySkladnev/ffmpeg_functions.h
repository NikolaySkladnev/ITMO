#ifndef CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFMPEG_FUNCTIONS_H
#define CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFMPEG_FUNCTIONS_H

#include "fftw_functions.h"
#include "return_codes.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>

#include <math.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct file
{
	AVFormatContext *formatCtx;
	const AVCodec *codec;
	AVCodecContext *codecCtx;
	int32_t audioStreamIndex;
} File;

int32_t findAudioStream(File *file);

int32_t parse_packets(File *file, Buffer *buffer, SwrContext **swr, int32_t data);

int32_t parse_file(char *filename, File *file, int32_t argc);

#endif	  // CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_FFMPEG_FUNCTIONS_H