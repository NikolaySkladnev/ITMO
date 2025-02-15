#include "ffmpeg_functions.h"

#include "additional_functions.h"

int32_t findAudioStream(File *file)
{
	for (int32_t i = 0; i < file->formatCtx->nb_streams; i++)
	{
		if (file->formatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO)
		{
			return i;
		}
	}
	return -1;
}

int32_t parse_packets(File *file, Buffer *buffer, SwrContext **swr, int32_t data)
{
	int32_t index = 0;
	int32_t buffer_capacity = 441000;

	AVPacket *packet = av_packet_alloc();
	if (!packet)
	{
		fprintf(stderr, "Error: Failed to allocate memory for AVPacket\n");
		return ERROR_NOTENOUGH_MEMORY;
	}

	AVFrame *frame = av_frame_alloc();
	if (!frame)
	{
		av_packet_free(&packet);
		fprintf(stderr, "Error: Failed to allocate memory for AVFrame\n");
		return ERROR_NOTENOUGH_MEMORY;
	}

	int32_t audioStreamIndex = file->audioStreamIndex;
	int32_t channels = file->codecCtx->ch_layout.nb_channels;
	int32_t rate = file->codecCtx->sample_rate;
	AVCodecContext *codecCtx = file->codecCtx;
	AVFormatContext *formatCtx = file->formatCtx;

	while (av_read_frame(formatCtx, packet) >= 0)
	{
		if (packet->stream_index != audioStreamIndex)
		{
			av_packet_unref(packet);
			continue;
		}

		if (avcodec_send_packet(codecCtx, packet) != 0)
		{
			fprintf(stderr, "Error: Failed to send packet to decoder\n");
			goto clean_up;
		}

		uint8_t **output;
		int32_t in_samples;
		int32_t out_samples;
		while (avcodec_receive_frame(codecCtx, frame) == 0)
		{
			output = NULL;
			in_samples = frame->nb_samples;
			out_samples = (int32_t)av_rescale_rnd(swr_get_delay(*swr, rate) + in_samples, rate, rate, AV_ROUND_UP);

			if (index + out_samples >= buffer_capacity)
			{
				buffer_capacity *= 2;
				double_t *new_buffer = realloc(buffer->buffer, buffer_capacity * sizeof(double_t));
				if (!new_buffer)
				{
					fprintf(stderr, "Error: Failed to reallocate memory for buffer\n");
					goto clean_up;
				}
				buffer->buffer = new_buffer;
			}

			if (av_samples_alloc_array_and_samples(&output, NULL, channels, out_samples, AV_SAMPLE_FMT_DBLP, 0) < 0)
			{
				fprintf(stderr, "Error: Failed to allocate output samples\n");
				goto clean_up;
			}

			out_samples = swr_convert(*swr, output, out_samples, (const uint8_t *const *)frame->data, in_samples);
			memcpy(&(buffer->buffer[index]), output[data], out_samples * sizeof(double_t));
			index += out_samples;
			av_freep(&output);
		}

		av_packet_unref(packet);
	}

	buffer->buffer_size = index;
	clean_parse_packets(packet, frame);

	return SUCCESS;

clean_up:
	clean_parse_packets(packet, frame);
	return ERROR_NOTENOUGH_MEMORY;
}

int32_t parse_file(char *filename, File *file, int32_t argc)
{
	int32_t err;

	if ((err = avformat_open_input(&file->formatCtx, filename, NULL, NULL)) != 0)
	{
		switch (err)
		{
		case AVERROR(EINVAL):
			fprintf(stderr, "Error: Invalid data\n");
			return ERROR_DATA_INVALID;
		case AVERROR(EIO):
			fprintf(stderr, "Error: During reading data from the file\n");
			return ERROR_DATA_INVALID;
		case AVERROR(ENOMEM):
			fprintf(stderr, "Error: Not enough memory to open the file\n");
			return ERROR_NOTENOUGH_MEMORY;
		case AVERROR(EACCES):
			fprintf(stderr, "Error: Permission denied while opening the file\n");
			return ERROR_UNSUPPORTED;
		case AVERROR(ENOENT):
			fprintf(stderr, "Error: Wrong path to file\n");
			return ERROR_CANNOT_OPEN_FILE;
		default:
			fprintf(stderr, "Error: Failed to open file\n");
			return ERROR_CANNOT_OPEN_FILE;
		}
	}

	if (avformat_find_stream_info(file->formatCtx, NULL) < 0)
	{
		fprintf(stderr, "Error: Could not find stream information\n");
		err = ERROR_FORMAT_INVALID;
		goto clean_up;
	}

	if ((file->audioStreamIndex = findAudioStream(file)) == -1)
	{
		fprintf(stderr, "Error: No audio stream found\n");
		err = ERROR_FORMAT_INVALID;
		goto clean_up;
	}

	if (!(file->codec = avcodec_find_decoder(file->formatCtx->streams[file->audioStreamIndex]->codecpar->codec_id)))
	{
		fprintf(stderr, "Error: Codec not found\n");
		err = ERROR_DATA_INVALID;
		goto clean_up;
	}

	enum AVCodecID codec_id = file->codec->id;
	if (codec_id != AV_CODEC_ID_FLAC && codec_id != AV_CODEC_ID_AAC && codec_id != AV_CODEC_ID_MP3 &&
		codec_id != AV_CODEC_ID_MP2 && codec_id != AV_CODEC_ID_OPUS)
	{
		fprintf(stderr, "Error: Unsupported codec\n");
		err = ERROR_DATA_INVALID;
		goto clean_up;
	}

	if (!(file->codecCtx = avcodec_alloc_context3(file->codec)))
	{
		fprintf(stderr, "Error: Failed to allocate codec context\n");
		err = ERROR_DATA_INVALID;
		goto clean_up;
	}

	if (avcodec_parameters_to_context(file->codecCtx, file->formatCtx->streams[file->audioStreamIndex]->codecpar) != 0)
	{
		fprintf(stderr, "Error: Could not copy codec parameters to codec context\n");
		err = ERROR_DATA_INVALID;
		goto clean_up;
	}

	if (file->codecCtx->ch_layout.nb_channels != 2 && (argc == 2))
	{
		fprintf(stderr, "Error: Invalid channel layout for the operation\n");
		err = ERROR_FORMAT_INVALID;
		goto clean_up;
	}

	if (avcodec_open2(file->codecCtx, file->codec, NULL) != 0)
	{
		fprintf(stderr, "Error: Could not open codec\n");
		err = ERROR_DATA_INVALID;
		goto clean_up;
	}

	return SUCCESS;

clean_up:
	clean_parse_file(file);
	return err;
}