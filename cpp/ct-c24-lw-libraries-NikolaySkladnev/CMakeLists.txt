cmake_minimum_required(VERSION 3.27)
project(ct_c24_lw_libraries_NikolaySkladnev C)

set(CMAKE_C_STANDARD 11)

include_directories(.)
include_directories(ffmpeg-master-latest-win64-gpl-shared)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libavcodec)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libavdevice)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libavfilter)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libavformat)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libavutil)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libpostproc)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libswresample)
include_directories(ffmpeg-master-latest-win64-gpl-shared/include/libswscale)
include_directories(fftw)

link_directories(fftw)
link_directories(ffmpeg-master-latest-win64-gpl-shared/bin)

add_executable(ct_c24_lw_libraries_NikolaySkladnev
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/ac3_parser.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/adts_parser.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/avcodec.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/avdct.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/avfft.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/bsf.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/codec.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/codec_desc.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/codec_id.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/codec_par.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/d3d11va.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/defs.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/dirac.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/dv_profile.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/dxva2.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/jni.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/mediacodec.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/packet.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/qsv.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/vdpau.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/videotoolbox.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavcodec/vorbis_parser.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavdevice/avdevice.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavdevice/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavdevice/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavfilter/avfilter.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavfilter/buffersink.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavfilter/buffersrc.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavfilter/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavfilter/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavformat/avformat.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavformat/avio.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavformat/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavformat/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/adler32.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/aes.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/aes_ctr.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/ambient_viewing_environment.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/attributes.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/audio_fifo.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/avassert.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/avconfig.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/avstring.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/avutil.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/base64.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/blowfish.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/bprint.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/bswap.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/buffer.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/camellia.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/cast5.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/channel_layout.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/common.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/cpu.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/crc.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/csp.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/des.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/detection_bbox.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/dict.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/display.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/dovi_meta.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/downmix_info.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/encryption_info.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/error.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/eval.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/executor.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/ffversion.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/fifo.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/file.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/film_grain_params.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/frame.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hash.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hdr_dynamic_metadata.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hdr_dynamic_vivid_metadata.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hmac.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_cuda.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_d3d11va.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_d3d12va.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_drm.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_dxva2.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_mediacodec.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_opencl.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_qsv.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_vaapi.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_vdpau.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_videotoolbox.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/hwcontext_vulkan.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/iamf.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/imgutils.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/intfloat.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/intreadwrite.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/lfg.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/log.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/lzo.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/macros.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/mastering_display_metadata.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/mathematics.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/md5.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/mem.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/motion_vector.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/murmur3.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/opt.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/parseutils.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/pixdesc.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/pixelutils.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/pixfmt.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/random_seed.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/rational.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/rc4.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/replaygain.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/ripemd.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/samplefmt.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/sha.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/sha512.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/spherical.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/stereo3d.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/tea.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/threadmessage.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/time.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/timecode.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/timestamp.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/tree.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/twofish.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/tx.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/uuid.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/video_enc_params.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/video_hint.h
        ffmpeg-master-latest-win64-gpl-shared/include/libavutil/xtea.h
        ffmpeg-master-latest-win64-gpl-shared/include/libpostproc/postprocess.h
        ffmpeg-master-latest-win64-gpl-shared/include/libpostproc/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libpostproc/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswresample/swresample.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswresample/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswresample/version_major.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswscale/swscale.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswscale/version.h
        ffmpeg-master-latest-win64-gpl-shared/include/libswscale/version_major.h
        fftw/fftw3.h
        main.c
        return_codes.h
        ffmpeg_functions.h
        fftw_functions.h
        ffmpeg_functions.c
        fftw_functions.c
        additional_functions.h
        additional_functions.c
)

target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev libfftw3-3.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev avformat-61.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev avcodec-61.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev avdevice-61.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev avfilter-10.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev avutil-59.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev postproc-58.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev swresample-5.dll)
target_link_libraries(ct_c24_lw_libraries_NikolaySkladnev swscale-8.dll)