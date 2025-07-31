package main

import (
	"bytes"
	"errors"
	"flag"
	"fmt"
	"io"
	"os"
	"strings"
)

type Options struct {
	From      string
	To        string
	Offset    int
	Limit     int
	BlockSize int
	Conv      string
}

type convs struct {
	upperCase  bool
	lowerCase  bool
	trimSpaces bool
}

func ParseFlags() (*Options, error) {
	var opts Options

	flag.StringVar(&opts.From, "from", "", "file to read. by default - stdin")
	flag.StringVar(&opts.To, "to", "", "file to write. by default - stdout")
	flag.IntVar(&opts.Offset, "offset", 0, "offset to read. by default - stdin")
	flag.IntVar(&opts.Limit, "limit", 0, "limit to read. by default - stdin")
	flag.IntVar(&opts.BlockSize, "block-size", 1024, "block size to read from")
	flag.StringVar(&opts.Conv, "conv", "", "conv file to use for conversion")

	flag.Parse()

	if opts.Offset < 0 {
		return nil, errors.New("offset cannot be negative")
	}
	if opts.Limit < 0 {
		return nil, errors.New("limit cannot be negative")
	}

	return &opts, nil
}

func main() {
	opts, err := ParseFlags()
	if err != nil {
		fatal(err)
	}

	if opts.Offset < 0 {
		fatal(err)
	}

	input, err := openInput(opts)
	if err != nil {
		fatal(err)
	}

	err = skipOffSet(opts, input)
	if err != nil {
		fatal(err)
	}

	output, err := openOutput(opts)
	if err != nil {
		fatal(err)
	}

	data, err := readByBlock(opts, input)
	if err != nil {
		fatal(err)
	}

	data, err = parseConvs(opts, data)
	if err != nil {
		fatal(err)
	}

	err = writeByBlock(opts, data, output)
	if err != nil {
		fatal(err)
	}
}

func openInput(opts *Options) (io.Reader, error) {
	input := os.Stdin
	if opts.From != "" {
		_, err := os.Stat(opts.From)
		if errors.Is(err, os.ErrNotExist) {
			return nil, errors.New("file does not exist")
		}

		input, err = os.Open(opts.From)
		if err != nil {
			return nil, err
		}
	}
	return input, nil
}

func openOutput(opts *Options) (io.Writer, error) {
	output := os.Stdout
	var err error
	if opts.To != "" {
		if _, err := os.Stat(opts.To); !errors.Is(err, os.ErrNotExist) {
			return nil, errors.New("file exists")
		}

		if output, err = os.Create(opts.To); err != nil {
			return nil, errors.New("can not open output file")
		}
	}
	return output, nil
}

func writeByBlock(opts *Options, data []byte, output io.Writer) error {
	end := opts.BlockSize
	for i := 0; i < len(data); i += opts.BlockSize {
		if end > len(data) {
			end = len(data)
		}

		_, err := output.Write(data[i:end])
		if err != nil {
			return err
		}

		end += opts.BlockSize
	}
	return nil
}

func readByBlock(opts *Options, input io.Reader) ([]byte, error) {
	buffer := make([]byte, opts.BlockSize)
	data := make([]byte, 0, opts.BlockSize)
	allowedBytes := opts.Limit

	for {
		currentBytes, err := input.Read(buffer)
		if err != nil && err != io.EOF {
			return nil, err
		}

		if opts.Limit > 0 && allowedBytes-currentBytes < 0 {
			data = append(data, buffer[:allowedBytes]...)
			break
		}

		allowedBytes = allowedBytes - currentBytes

		data = append(data, buffer[:currentBytes]...)

		if err == io.EOF {
			break
		}
	}
	return data, nil
}

func parseConvs(opts *Options, data []byte) ([]byte, error) {
	var conv convs
	convsSlice := strings.Split(opts.Conv, ",")

	for _, value := range convsSlice {
		switch value {
		case "":
			continue
		case "upper_case":
			conv.upperCase = true
		case "lower_case":
			conv.lowerCase = true
		case "trim_spaces":
			conv.trimSpaces = true
		default:
			return nil, errors.New("invalid conv option: " + value)
		}
	}

	if conv.upperCase && conv.lowerCase {
		return nil, errors.New("upperCase and lowerCase options are mutually exclusive")
	}

	if conv.trimSpaces {
		data = bytes.TrimSpace(data)
	}

	if conv.upperCase {
		data = bytes.ToUpper(data)
	}

	if conv.lowerCase {
		data = bytes.ToLower(data)
	}

	return data, nil
}

func skipOffSet(opts *Options, input io.Reader) error {
	buffer := make([]byte, 1)

	if opts.Offset != 0 {
		for i := 0; i < opts.Offset; i++ {
			_, err := input.Read(buffer)
			if err == io.EOF {
				return errors.New("offset out of range")
			}

			if err != nil {
				return err
			}
		}
	}
	return nil
}

func fatal(err error) {
	_, _ = fmt.Fprintln(os.Stderr, err)
	os.Exit(1)
}
