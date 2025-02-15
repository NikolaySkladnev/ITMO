#ifndef CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_ADDITIONAL_FUNCTIONS_H
#define CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_ADDITIONAL_FUNCTIONS_H

#include "ffmpeg_functions.h"

int32_t swr_initialize(AVCodecContext *codec, SwrContext *swr, int32_t sample_rate);
void clean_main(Buffer *buffer_1, Buffer *buffer_2, SwrContext **swr, File *file_1, File *file_2);
void clean_parse_packets(AVPacket *packet, AVFrame *frame);
void clean_parse_file(File *file);
#endif	  // CT_C24_LW_LIBRARIES_NIKOLAYSKLADNEV_ADDITIONAL_FUNCTIONS_H