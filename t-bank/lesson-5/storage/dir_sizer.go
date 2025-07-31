package storage

import (
	"context"
	"runtime"
	"sync"
)

type Result struct {
	Size  int64
	Count int64
}

type DirSizer interface {
	Size(ctx context.Context, d Dir) (Result, error)
}

type sizer struct {
	maxWorkersCount int
	wgWorkers       sync.WaitGroup
	wgTasks         sync.WaitGroup
	mu              sync.Mutex
	tasks           chan Dir
	errCh           chan error
}

func NewSizer() DirSizer {
	return &sizer{
		maxWorkersCount: runtime.NumCPU(),
		tasks:           make(chan Dir),
		errCh:           make(chan error, 1),
	}
}

func (a *sizer) Size(origCtx context.Context, d Dir) (Result, error) {
	var result Result

	ctx, cancel := context.WithCancel(origCtx)
	defer cancel()

	for i := 0; i < a.maxWorkersCount; i++ {
		a.wgWorkers.Add(1)
		go func() {
			defer a.wgWorkers.Done()
			if err := a.calcFiles(ctx, &result); err != nil {
				select {
				case a.errCh <- err:
				default:
				}
				cancel()
			}
		}()
	}

	a.wgTasks.Add(1)
	a.tasks <- d

	go func() {
		a.wgTasks.Wait()
		close(a.tasks)
	}()

	a.wgWorkers.Wait()

	select {
	case err := <-a.errCh:
		return result, err
	default:
		return result, nil
	}
}

func (a *sizer) calcFiles(ctx context.Context, result *Result) error {
	for {
		select {
		case <-ctx.Done():
			return ctx.Err()
		case dirObj, ok := <-a.tasks:
			if !ok {
				return nil
			}

			dirs, files, err := dirObj.Ls(ctx)
			if err != nil {
				a.wgTasks.Done()
				return err
			}

			for _, subdir := range dirs {
				a.wgTasks.Add(1)
				a.tasks <- subdir
			}

			for _, file := range files {
				size, err := file.Stat(ctx)
				if err != nil {
					a.wgTasks.Done()
					return err
				}
				a.mu.Lock()
				result.Size += size
				result.Count++
				a.mu.Unlock()
			}

			a.wgTasks.Done()
		}
	}
}
