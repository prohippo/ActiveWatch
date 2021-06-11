ActiveWatch (AW) is a package of Java modules implementing finite indexing
of English text with a statistically scaled similarity measure between pairs
of text items represented as numerical vectors. It was developed as
alternative to the cosine similarity of Gerard Salton.

AW indexes text with a finite set of user-selected words plus a finite set
of frequent English word fragments allowing the presence of other words to
be at least partially noted. For example, if the word CONFABULATE is not a
full-word index key, it can be represented by the word fragmants CONF, NFA,
FAB, ABU, ULAT, and LTE.

Fragmentary indexing is noisy, but for comparisons of entire text items, it
can be quite effective. Indexing with a combination of whole words and word
fragments allows text items to be mapped into a vector space of finite
dimensionality, which simplifies many theoretical problems.

In particular, we can more reliably estimate the probability of a given
index key occuring in a text item and thereby calculate the probability of
a particular inner product score for item vectors occurring by chance.
This will give us parameters of a noise distribution that we can use to
scale inner produce similarity scores and meke them easier to understand.

AW measures similarity according to the the number of standard deviations
that a raw inner product score falls above or below the mean of a noise
distribution. This distribution will be unimodal and roughly Gaussian. A
statistically scaled similary of 5 standard deviations should therefore be
highly significant (p < .0u000001).

Code for AW was first written in C around 1982. The Java version was done
around 1999. This is the core of the source code in this repository.

