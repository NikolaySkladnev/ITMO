package watcher

import (
	"context"
	"errors"
	"os"
	"path/filepath"
	"time"
)

type EventType string

const (
	EventTypeFileCreate  EventType = "file_created"
	EventTypeFileRemoved EventType = "file_removed"
)

var ErrDirNotExist = errors.New("dir does not exist")

type Event struct {
	Type EventType
	Path string
}

type Watcher struct {
	Events          chan Event
	refreshInterval time.Duration

	files map[string]struct{}
}

func NewDirWatcher(refreshInterval time.Duration) *Watcher {
	return &Watcher{
		refreshInterval: refreshInterval,
		Events:          make(chan Event),
		files:           make(map[string]struct{}),
	}
}

func (w *Watcher) WatchDir(ctx context.Context, path string) error {
	if ctx.Err() != nil {
		return ctx.Err()
	}

	if _, err := os.Stat(path); errors.Is(err, os.ErrNotExist) {
		return ErrDirNotExist
	}

	err := collectFiles(path, w.files)
	if err != nil {
		return err
	}

	ticker := time.NewTicker(w.refreshInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-ticker.C:
			currentFiles := make(map[string]struct{})

			err := collectFiles(path, currentFiles)
			if err != nil {
				return err
			}

			checkRemove(w, currentFiles)

			checkCreate(currentFiles, w)

			w.files = currentFiles
		}
	}
}

func collectFiles(path string, currentFiles map[string]struct{}) error {
	err := filepath.Walk(path, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			currentFiles[path] = struct{}{}
		}
		return nil
	})

	if err != nil {
		return err
	}
	return nil
}

func checkRemove(w *Watcher, currentFiles map[string]struct{}) {
	for path := range w.files {
		if _, ok := currentFiles[path]; !ok {
			w.Events <- Event{
				Type: EventTypeFileRemoved,
				Path: path,
			}
		}
	}
}

func checkCreate(currentFiles map[string]struct{}, w *Watcher) {
	for path := range currentFiles {
		if _, ok := w.files[path]; !ok {
			w.Events <- Event{
				Type: EventTypeFileCreate,
				Path: path,
			}
		}
	}
}

func (w *Watcher) Close() {
	close(w.Events)
}
