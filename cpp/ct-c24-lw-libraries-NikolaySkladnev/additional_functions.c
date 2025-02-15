#include "additional_functions.h"

int32_t swr_initialize(AVCodecContext *codec, SwrContext *swr, int32_t sample_rate)
{
	int32_t err = swr_alloc_set_opts2(
		&swr,
		&codec->ch_layout,
		AV_SAMPLE_FMT_DBLP,
		sample_rate,
		&codec->ch_layout,
		codec->sample_fmt,
		codec->sample_rate,
		0,
		NULL);
	if (err != 0)
	{
		fprintf(stderr, "Error: Failed to allocate memory for swr\n");
		return ERROR_UNSUPPORTED;
	}

	err = swr_init(swr);
	if (err != 0)
	{
		fprintf(stderr, "Error: Failed to init swr\n");
		return ERROR_UNSUPPORTED;
	}
	return 0;
}

void clean_main(Buffer *buffer1, Buffer *buffer2, SwrContext **swr, File *file1, File *file2)
{
	if (buffer1->buffer)
		free(buffer1->buffer);

	if (buffer2->buffer)
		free(buffer2->buffer);

	if (*swr)
		swr_free(swr);

	if (file1->codecCtx)
		avcodec_free_context(&file1->codecCtx);

	if (file1->formatCtx)
		avformat_close_input(&file1->formatCtx);

	if (file2->codecCtx)
		avcodec_free_context(&file2->codecCtx);

	if (file2->formatCtx)
		avformat_close_input(&file2->formatCtx);
}

void clean_parse_packets(AVPacket *packet, AVFrame *frame)
{
	av_packet_free(&packet);
	av_frame_free(&frame);
}

void clean_parse_file(File *file)
{
	if (file->codecCtx)
		avcodec_free_context(&file->codecCtx);

	if (file->formatCtx)
		avformat_close_input(&file->formatCtx);
}
