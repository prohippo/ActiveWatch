ActiveWatch (AW) is a set of Java software modules for building various
statistical text processing capabilities. It is unusual in its being based
on indexing text according to only a finite set of selected lexical
features instead of whole words. It represents text items as M-dimensional
numerical vectors, where M will be on the order of 10⁴.

Finite indexing lets us reliably estimate the probability that a given
lexical feature occurs in a text item of given length. A multinomial
model can then be applied to compute a statistically scaled inner-product
similarity measure between pairs of vectors. This offers an alternative
to the normalized, but unscaled, cosine similarity of Gerard Salton.

AW lexical features for indexing text currently fall into three types: 
(1) all alphanumeric 2-grams, like TH, F1, 2X, or 00; (2) selected
alphabetic n-grams (for n > 2), like QUE, REVE, and CLASS; and (3) a
fixed number of user-defined alphanumeric word beginnings and endings,
like THERMO- and -MOTHER; these can cover an entire word like MOTHER.

Indexing with word fragments will always be noisier than with whole
words. For example, if the word CONFABULATE is unrecognized, we must
break it into overlapping fragments: CONF, NFA, FAB, ABU, ULAT, and
LATE. Yet, by adding longer fragmentsi like ULATE to our indexing, we
can achieve ever better finite approximations to full-word indexing.

So, how big would our finite have to be to support some useful text
analysis? The ActiveWatch demonstration makes the case that 10⁴ should
be enough for automatic clustering of text items by content or for
other processing done with vectors. You should look elsewhere, however,
if you just want to find all documents containing a specific word.

In any case, the big advantage of a vector representation of text is
that it lets us organize its processing at a higher level of
abstraction. Once we have text as vectors, it should not really matter
where these vectors came from. We care only that they are easy to work
with and carry enough information for the purposes of users.

The advantage of finite indexing is that it makes a statistically scaled
similarity measure practical. This allows a text processing system to
make many decisions on its own without a human always hovering around like
a helicopter parent. A system then becomes much more manageable and also
more resiliant when something unexpected happens.

AW will score similarity by the the number of standard deviations that a
raw inner product falls above or below the mean of a noise distribution.
This noise will be roughly Gaussian; an AW scaled similarity of 3 standard
deviations would be significant at about p = .003. With actual text data,
We usually will expect AW scaled similarity above 5 standard deviations.

Some index tuning is needed to achieve such performance. This will mainly
involve adjustments of the index keys defined by a users for particular
target text data and, to a lesser extent, to stemming and stopword deletion.
One can also expect that AW will continue to expand its frequency-selected
n-grams indexings over time.

AW was first written in C around 1982 for information discovery in unfamiliar
text data. The current Java version dates back to around 1999, but has some
recent tweaks in its linguistic analysis and it addition of 4- and 5-letter
word fragments for indexing. Only 2- and 3-letter fragments, plus user-defined
indices, were xsemployed previously. 

The modules included in the AW GitHub repository mainly provide support for
simple clustering of text items by content. These are organized functionally
by their Java package identifications. The Java code was originally written on
Apple home computers running versions 7.*, 8.*, or 9.* of the Macintosh OS.
This was when Java was still a somewhat new programming language.

Java AW eventually evolved to support many kinds of statistical natural
processing, but this GitHub repository includes only a small subset of modules
for automatic clustering of text items in particular. This software should
give you a good overall idea of what you can do with AW finite indexing and
statistically scaled similarity between pairs of text items.

The latest AW release includes fourteen prebuilt AW modules that combine to
demonstrate automatic clustering. These are found in separate runnable jar
files in the subdirectory jars. Documentation is skimpy, but there is now
a PDF file (HowToIndexText.pdf) that should provide essential information. If
the creek don't rise, a user manual should be forthcoming.

To build out our basic AW clustering capability, run the 'build' shell script
included with the AW GitHub download. The script is for macOS Darwin Unix
and should be edited for your computing platform. You will have to install a
Java JDK if you do not have one already. Everything in the AW demonstration
still has to run from a command line.

AW software is free for all uses and is released under BSD licensing.

Release History:

v0.1    16jun2021  Initial upload of original AW Java source code.

v0.2    10ju12021  Clean up and reorganize code for SEGMTR module
                   Add code to dump AW output files
                   Collect news data set for demonstration

v0.3    13jul2021  Clean up and debug code in AW table building modules
                   Add unit testing

v0.4    16jul2021  Clean up and reorganize code for INDEXR module
                   Add code to dump AW output files
                   Update to expect UTF-8 text input, not ASCII

v0.5    06aug2021  Clean up code for SEGMTR module
                   Fix problems in UTF-8 handling
                   Add diagnostic tools
                   Build initial versions of AW clustering modules to test

v0.6    12aug2021  Clean up SEGMTR, UPDATR, SEQNCR, SQUEZR, SUMRZR, KEYWDR modules
                   Fix problems in UTF-8 handling, subsegmenting long text items
                   Add and extend diagnostic tools
                   Clean up text data sample for clustering demonstration
                   Edit and update documentation

v0.6.1  14aug2021  Fix mishandling of multi-segment items in AW clustering

v0.7    21aug2021  add and extend diagnostic tools
                   remove phonetic indexing
                   add 4- and 5-gram indices
                   reorganize startup of n-gram analysis
                   clean up problems with deprecation and type casting

v0.7.1  25aug2021  fix bug in LexicalGram in breaking out of extraction loop
                   replace incorrect 4- and 5-grams in GramMap lists
                   clean up literal file to align with current 4- and 5-grams
                   fix inconsistent stemming rules in suffix filw
                   simply Updater command line arguments
                   update Dprb for v0.7 changes in n-gram initialization
                   update documentation

v0,7.2  30aug2021  fix integration of AW-defined and user-defined indices
                   update documentation

v0.7.3  04sep2021  add suffix rule to fix stemming glitch
                   add general writeup on AW and finite indexing
                   upload jars of compiled and linked AW modules
                   update documentation

v0.8    20sep2021  update to latest inflectional stemming logic
                   update implementation of inflectional stemmer
                   update documentation

v0.9    30sep2021  expand morphological stemming
                   fix longstanding problems in stemming code
                   clean up and and extend code commentary
                   update documentation

v0.9.1  07oct2021  fix problems in n-gram extraction
                   fix problems in morphological stemming rule extension
                   fix typo in AW 5-gram table
                   update documentation

v1.0    14oct2021  clean up definitions of n-gram limits
                   clean up literal n-grams and morphological stemming
                   extend builtin 4-and 5-grams
                   fix a lookup bug for 4- and 5-grams
                   update documentation

v1.0.1  22oct2021  extend built-in 4-grams by 200
                   add built-in 5-grams not chosen by general frequency
                   edit default literal to reduce indexing redundancy
                   update documentation

v1.1    27oct2021  add WATCHR module to help monitor residuals
                   make index vector operations more transparent
                   update documentation

v1.1.1  07nov2021  add 4- and 5-grams to reduce indexing noise
                   clean up file defining default literal n-grams
                   fix problem with build file for WATCHR
                   update documentation

v1.1.2  11nov2021  add 4-grams to reduce indexing noise
                   extend and clean up default literal n-grams
                   clean up various source files for readability
                   clean up clustering source code, add comments
                   update documentATION

v1.1.3  16nov2021  add 4- and 5-grams to reduce indexing noise
                   extend and clean up default literal n-grams
                   add tools to identify where frequent n-grams come from
                   comment out diagnostic print statements in SQUEZR
                   update documentation

v1.1.4  19nov2021  add 4- and 5-grams to reduce indexing noise
                   clean up poorly formatted source files
                   update documentation

v1.1.5  21nov2021  fix bug in arguments for WATCHR
                   add diagnostic tools
                   clean up source files
                   update documentation

v1.1.6  24nov2021  add 4- and 5-grams to reduce indexing noise
                   clean up literals
                   add diagnostic tools
                   add scripting to build AW tools
                   update documentation

v1.1.7  26nov2021  add 4- and 5-grams to reduce indexing noise
                   clean up literals
                   add diagnostic tools
                   add scripting to build AW tools
                   update documentation

v1.1.8  01dec2021  add 4- and 5-grams to reduce indexing noise
                   clean up literals
                   update documentation

v1.1.9  10dec2021  fix bug on oriority of leading, trailing literals
                   add 4- and 5-grams to reduce indexing noise
                   clean up literals
                   update documentation
                   add AW User Guide

v1.1.10 20dec2021  add 4- and 5-grams to reduce indexing noise
                   update documentation 

v1.1.11 23dec2021  add 4- and 5-grams to reduce indexing noise
                   fix typo in AW banner
                   update documentation

v1.1.12 30dec2021  add 4-grams to reduce indexing noise
                   update documentation

v1.2    01jan2022  add 4- and 5-grams to reduce indexing noise
                   add diagnostic tools for profiles and match lists
                   update documentation

v1.2.1  06jan2022  add 4- and 5-grams to reduce indexing noise
                   allow indexing to stop a 3- or 4-grams
                   update documentation

v1.2.2  07jan2022  add 4- and 5-grams to reduce indexing noise
                   update documentation

