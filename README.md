ActiveWatch (AW) is a set of Java modules for finite indexing of text.
It will represent items as M-dimensional numerical vectors, where M is on
the order of 10⁴. AW notably employs a statistically scaled similarity
measure between pairs of vectors as an alternative to the cosine similarity
of Gerard Salton.

An AW index set consists of finitely many user-selected full words plus
finitely many English word fragments chosen by frequency. Fragmentary
elements allow an approximation of indexing by every word. For example,
if the word CONFABULATE is unknown as a full-word index key, we can still
recognize its overlapping fragments: CONF, NFA, FAB, ABU, ULAT, and LATE.

Indexing by fragments will be noisy, but can still be quite effective for
comparing the content of entire text items. Limiting an index set to a few
thousand whole words and several thousand word fragments gives us more
control over descriptions of text content and simplifies many problems in
computing proper statistics.

In particular, we can more reliably estimate the probability of a given
index key showing up in a text item and thereby calculate the probability
of a particular inner product score occurring by chance for item vectors.
This will give us parameters of a noise distribution that we can use to
scale inner produce similarity scores and meke them easier to interpret.

AW measures similarity by the the number of standard deviations that a raw
inner product score falls above or below the mean of a noise distribution.
This noise will be roughly Gaussian; an AW scaled similarity of 3 standard
deviations would roughly be significant at p = .003. With actual text data,
We can usually expect scaled similarity above 5 standard deviations.

Some index tuning is needed to achieve this kind of performance. This will
mainly involve adjustments of the whole-word index keys defined by a user
for particular target text data. We can also improve fragmentary indexing
in general by selecting the fragments more carefully and making them
subsume more frequent shorter fragments.

The first version of AW was written in C around 1982 and was employed for
information discovery in umfamiliar text data. The current Java version
dates back to around 1999, but has some recent tweaks in its linguistic
analysis and its inclusion of 4- and 5-letter word fragments for indexings,
along with the 2- and 3-letter fragments employed employed previously. 

The modules included in the AW GitHub repository mainly provide support for
simple clustering of text items by content. These are organized hierarchically
by their Java package identifications. Other AW modules may be added later,
but clustering should give you a good idea of what you can do with AW finite
indexing and statiscally scaled similarity between pairs of text items.

To build the AW clustering capability, just run the 'build' shell script in
the AW repository. A complicated makefile is unnecessary. You just need to
know which classes with main() modules have to be compiled. Java should then
be able to trace the dependencies in the source code and find the necessary
files in the AW repository file hierarchy. 

All AW software is free and is released under BSD licensing.

Release History:

v0.1    16jun2021  Initial upload of original Java source code.

