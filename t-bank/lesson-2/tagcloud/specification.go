package tagcloud

import "sort"

type TagCloud struct {
	tags map[string]int
}

type TagStat struct {
	Tag             string
	OccurrenceCount int
}

func New() *TagCloud {
	return &TagCloud{make(map[string]int)}
}

func (t *TagCloud) AddTag(tag string) {
	t.tags[tag]++
}

func (t *TagCloud) TopN(n int) []TagStat {
	if n <= 0 {
		return nil
	}

	tagTopN := make([]TagStat, 0, len(t.tags))

	for tag, count := range t.tags {
		tagTopN = append(tagTopN, TagStat{tag, count})
	}

	sort.Slice(tagTopN, func(i, j int) bool {
		if tagTopN[i].OccurrenceCount == tagTopN[j].OccurrenceCount {
			return tagTopN[i].Tag < tagTopN[j].Tag
		}
		return tagTopN[i].OccurrenceCount > tagTopN[j].OccurrenceCount
	})

	if len(t.tags) < n {
		return tagTopN
	}

	return tagTopN[:n]
}
