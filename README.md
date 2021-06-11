ActiveWatch (AW) is a set of Java modules for finite indexing of text,
representing items as M-dimensional numerical vectors. It notably defines
a statistically scaled similarity measure between pairs of vectors as an
alternative to the cosine similarity of Gerard Salton.

AW indexing recognizes only a finite set of user-selected full words plus
a finite set of frequent English word fragments. The latter elements allow
an approximation of indexing on every word. For example, if the word
CONFABULATE is missing as a full-word index key, we can still recognize
its overlapping fragments: CONF, NFA, FAB, ABU, ULAT, and LATE.

Indexing by fragments will be noisy, but can still be quite effective for
comparing entire text items. The finiteness of a combination of whole words
and word fragments gives us more control over descriptions of text content
and simplifies many theoretical problems.

In particular, we can more reliably estimate the probability of a given
index key showing up in a text item and thereby calculate the probability
of a particular inner product score occurring by chance for item vectors.
This will give us parameters of a noise distribution that we can use to
scale inner produce similarity scores and meke them easier to understand.

AW measures similarity according to the the number of standard deviations
that a raw inner product score falls above or below the mean of a noise
distribution. This distribution will be unimodal and roughly Gaussian. A
statistically scaled similary of 5 standard deviations should therefore be
highly significant (p < .0000001).

When running on actual text data, AW can usually operate effectively with
minimum similarity thresholds of 6 standard deviations. Some tuning is
helpful for better performance, but this will mainly involve adjustments of
index keys. Everything is statistically transparent.

Code for AW was first written in C around 1982. The Java version was done
around 1999. This is the core of the source code released in this repository.
All software is released under BSD licensing.

