#include "additional_functions.h"
#include "ffmpeg_functions.h"
#include "fftw_functions.h"

int32_t main(int argc, char *argv[])
{
	av_log_set_level(AV_LOG_QUIET);
	if (argc != 2 && argc != 3)
	{
		fprintf(stderr, "Error: Wrong programme arguments\n");
		return ERROR_ARGUMENTS_INVALID;
	}

	int32_t err = ERROR_NOTENOUGH_MEMORY;
	File file_1 = { 0 }, file_2 = { 0 };
	Buffer buffer_1 = { 0 }, buffer_2 = { 0 };
	SwrContext *swr = NULL;

	if ((buffer_1.buffer = malloc(441000 * sizeof(double_t))) == NULL)
	{
		fprintf(stderr, "Error: Failed to allocate memory for buffer 1\n");
		goto clean_up;
	}

	if ((buffer_2.buffer = malloc(441000 * sizeof(double_t))) == NULL)
	{
		fprintf(stderr, "Error: Failed to allocate memory for buffer 2\n");
		goto clean_up;
	}

	if ((err = parse_file(argv[1], &file_1, argc)) != SUCCESS)
		goto clean_up;

	if ((err = parse_file(argv[(argc == 2) ? 1 : 2], &file_2, argc)) != SUCCESS)
		goto clean_up;

	if (!(swr = swr_alloc()))
	{
		fprintf(stderr, "Error: Failed to allocate memory for SwrContext\n");
		err = ERROR_NOTENOUGH_MEMORY;
		goto clean_up;
	}

	int32_t sample_rate =
		(file_1.codecCtx->sample_rate > file_2.codecCtx->sample_rate) ? file_1.codecCtx->sample_rate : file_2.codecCtx->sample_rate;

	if ((err = swr_initialize(file_1.codecCtx, swr, sample_rate)) != SUCCESS)
		goto clean_up;

	if ((err = parse_packets(&file_1, &buffer_1, &swr, 0)) != SUCCESS)
		goto clean_up;

	if ((err = swr_initialize(file_2.codecCtx, swr, sample_rate)) != SUCCESS)
		goto clean_up;

	if ((err = parse_packets(&file_2, &buffer_2, &swr, (argc == 2) ? 1 : 0)) != SUCCESS)
		goto clean_up;

	int32_t index = cross_correlation(&buffer_1, &buffer_2);
	if (index == -1)
	{
		err = ERROR_NOTENOUGH_MEMORY;
		goto clean_up;
	}

	printf("delta: %d samples\nsample rate: %i Hz\ndelta time: %d ms\n", index, sample_rate, index * 1000 / sample_rate);

	return SUCCESS;

clean_up:
	clean_main(&buffer_1, &buffer_2, &swr, &file_1, &file_2);
	return err;
}
