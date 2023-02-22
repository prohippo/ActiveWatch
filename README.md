ActiveWatch (AW) is a set of Java software modules for building various
statistical text processing capabilities. It is unusual in its indexing
of text according to a finite set of selected lexical features instead
of whole words. It represents individual text items as N-dimensional
numerical vectors, where N will be on the order of 10⁴.

For a full AW User Guide, see the file AWug.pdf in this GitHub repository.
For a mathematical and historical description of finite and other kinds of
text indexing in general, see the repository file HowtoIndex.pdf.

Finite indexing lets us reliably estimate the probability that a given
lexical feature occurs in a text item of a given length. A multinomial
model can then be applied to compute a statistically scaled inner-product
similarity measure between pairs of vectors. This offers an alternative
to the normalized, but unscaled, cosine similarity of Gerard Salton.

AW lexical features for indexing text currently fall into three types: 
(1) all alphanumeric 2-grams, like TH, F1, 2X, or 00; (2) selected
alphabetic n-grams (for n > 2), like QUE, REVE, and CLASS; and (3) a
fixed number of user-defined alphanumeric word beginnings and endings,
like -000000000, THERMO-, and -MOTHER.

Indexing with word fragments will tend to be noisier than with whole
words. For example, if text item has a long word like CONFABULATE that
is missing as a indexing feature, AW will break it into overlapping
fragments like CONF, NFA, FAB, ABU, ULAT, and LATE to represent the
word in a finite indexing vector. This may seem outrageous, but any
crossword puzzle fan knows that word fragments do carry ihformation.

So, how big would our finite index set have to be to support useful text
analysis? The ActiveWatch demonstration makes the case that 10⁴ should
be enough in English for automatic clustering of short text items by
content or for detecting highly unusual content in a dynamic text stream.
You should look elsewhere, however, if you just want to find all documents
containing a specific word.

The advantage of a finite vector representation of text is that it lets us
organize information processing at a level of abstraction that simplifies
the computations cwa system must carry out. Once we encode text as vectors,
it should not matter where these vectors came from. We care only that they
are convenient to work with and carry enough information for the needs and
purposes of information users.

Vector data of finite dimensionality makes a statistically scaled measure
of similarity possible. Such scaling makes a measure easier to interpret
and allows a text processing system to make decisions reliably on its own.
Human users must otherwise hover around like a helicopter parent for quality
control. This is especiallly critical in real-time systems with dynamic data;
they become more manageable and more resiliant in unexpected situations.

AW will score similarity by the number of standard deviations that a raw
vector inner product similarity score falls above the mean of a theoretical
noise distribution. This noise will be roughly Gaussian, so that an AW scaled
similarity of 3 standard deviations should be significant at about p = .003.
With actual text data, AW should typically work with scaled similarity well
above 6 standard deviations.

Some index tuning is needed to achieve such performance. This will mainly
involve adjustments of the indexing features defined by a users for particular
target text data. Automatic stemming and stopword deletion also allows AW
users to exclude purely grammatical instances of n-grams like ING, MENT,
or ATION when indexing text for content.

AW was first written in C around 1982 for information discovery in unfamiliar
text data. The current Java version dates back to around 1999, but has some
recent tweaks in its linguistic analysis and addition of 4- and 5-letter word
fragments for more precise indexing. Only 2- and 3-letter fragments, plus
user-defined indices, were built into AW previously. 

The modules included in the AW GitHub repository mainly provide support for
simple clustering of text items by content. The code is organized functionally
Java packages. It was originally written on Apple home computers running
versions 7.*, 8.*, or 9.* of the Macintosh OS.  This was when Java was still
a somewhat new programming language.

Java AW eventually evolved to support many kinds of statistical natural
language processing, but this GitHub repository includes only a small subset of
modules to demonstrate automatic clustering of text items in particular. This
software should give you a good overall idea of what you can do with AW finite
indexing and statistically scaled similarity between pairs of finite item vectors.

The latest AW release includes fifteen prebuilt AW modules. These might support
military intelligence operations or the tagging of news streams for resale
to commercial businesses or the organization of text documents obtained by
legal discovery. The modules are in separate runnable jar files in the jars
subdireectory of this repository.

All Java source code is included in the GitHub repository. You can build out
all the AW modules by running the 'build' shell script included with the AW
GitHub download. The script is for macOS Darwin Unix and should be edited for
your own computing platform. You will have to install a Java JDK if you do
not have one already. Everything in the AW demonstration still has to run
from a command line.

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
	
	v0.7.2  30aug2021  fix integration of AW-defined and user-defined indices
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
	
	v1.1    27oct2021  expand WATCHR module to help monitor residuals
	                   make index vector operations more transparent
	                   update documentation
	
	v1.1.1  07nov2021  expand 4- and 5-grams to reduce indexing noise
	                   clean up file defining default literal n-grams
	                   fix problem with build file for WATCHR
	                   update documentation
	
	v1.1.2  11nov2021  expand 4-grams to reduce indexing noise
	                   extend and clean up default literal n-grams
	                   clean up various source files for readability
	                   clean up clustering source code, add comments
	                   update documentation
	
	v1.1.3  16nov2021  expand 4- and 5-grams to reduce indexing noise
	                   extend and clean up default literal n-grams
	                   add tools to identify where frequent n-grams come from
	                   comment out diagnostic print statements in SQUEZR
	                   update documentation
	
	v1.1.4  19nov2021  expand 4- and 5-grams to reduce indexing noise
	                   clean up poorly formatted source files
	                   update documentation
	
	v1.1.5  21nov2021  fix bug in arguments for WATCHR
	                   add diagnostic tools
	                   clean up source files
	                   update documentation
	
	v1.1.6  24nov2021  expand 4- and 5-grams to reduce indexing noise
	                   clean up literals
	                   add diagnostic tools
	                   add scripting to build AW tools
	                   update documentation
	
	v1.1.7  26nov2021  expand 4- and 5-grams to reduce indexing noise
	                   clean up literals
	                   add diagnostic tools
	                   add scripting to build AW tools
	                   update documentation
	
	v1.1.8  01dec2021  expand 4- and 5-grams to reduce indexing noise
	                   clean up literals
	                   update documentation
	
	v1.1.9  10dec2021  fix bug on oriority of leading, trailing literals
	                   expand 4- and 5-grams to reduce indexing noise
	                   clean up literals
	                   update documentation
	                   add AW User Guide
	
	v1.1.10 20dec2021  expand 4- and 5-grams to reduce indexing noise
	                   update documentation 
	
	v1.1.11 23dec2021  expand 4- and 5-grams to reduce indexing noise
	                   fix typo in AW banner
	                   update documentation
	
	v1.1.12 30dec2021  expand 4-grams to reduce indexing noise
	                   update documentation
	
	v1.2    01jan2022  expand 4- and 5-grams to reduce indexing noise
	                   add diagnostic tools for profiles and match lists
	                   update documentation
	
	v1.2.1  06jan2022  expand 4- and 5-grams to reduce indexing noise
	                   allow indexing to stop at 3- or 4-grams
	                   update documentation
	
	v1.2.2  07jan2022  expand 4- and 5-grams to reduce indexing noise
	                   update documentation
	
	v1.3    11jan2022  expand 4- and 5-grams to reduce indexing noise
	                   add new AW modules PROFLR and EXMPLR
	                   add missing source files
	                   update documentation
	
	v1.3.1  15jan2022  expand 4- and 5-grams to reduce indexing noise
	                   clean up literals
	                   clean up and extend stemming
	                   update documentation, fix this file for MD formatting

	v1.3.2  17jan2022  expand 4- and 5-grams to reduce indexing noise
	                   update documentation

	v1.3.3  19jan2022  expand 4-grams to reduce indexing noise
	                   add literals to reduce noise
	                   update documentation

	v1.3.4  20jan2022  expand 4-grams to reduce indexing noise
	                   add literals to reduce noise
	                   update documentation
	
	v1.4    25jan2022  add PHRASR module to AW complement
	                   update documentation

	v1.4.1  26jan2022  move Lines.java and Inputs.java to aw package

	v1.4.2  02feb2022  add ANALZR module to AW complement
	                   clean up CharArray source code
                           clean up phrase exrracrion source code
	                   update documentation

	v1.4.3  10feb2022  add PATBLD and ENDBLD support modules for phrase analysis
	                   add language processing tables for phrase analysis
	                   add reporting on failure to load language processing tables
	                   add bannder to ANALZR and PHRASR modules
	                   clean up source files
	                   update documentation

	v1.4.4  20feb2022  extensive reworking of ANALZR module
	                   fix incomplete LexicalAtomStream
	                   clean up CharArray class
	                   update documentation

	v1.4.5  04mar2022  extensive reworking of Start and Parse classes
	                   reorganize AW special hash tables
	                   clean up source files for entity type classes
	                   rework ANALZR and PHRASR modules
	                   add DXPH and DSPH tools
	                   update documentation

	v1.5    15jul2022  clean up AW CharArray classes
	                   clean up text char normalization
	                   clean up and simplify AW hash table code
	                   debug text lining methods
	                   improve source code commentary
	                   replace typo in 4-gram index list
	                   update documentation

	v2.0    26sep2022  change parsing data structures for longer text
	                   clean up reparsing for phrase extraction
	                   simplify signatures for phrase selection
	                   rework code and comments in ANALZR and PHRASR
	                   add diagnostic tools to check AW phrase analysis
	                   fix bug in token scoring for KEYWDR and PHRASR
	                   fix bug in word hash table for KEYWDR and PHRASR
	                   fix bugs for phrase scoring
	                   update documentation

	v2.1	28oct2022  expand builtin 4-grams to 2,500
	                   remove POLY- and MONO- from default literals
	                   fix bug in building stopword table
	                   fix bug in reading in syntactic type definitions
	                   fix bugs in loading rewriting rules
	                   fix ByteTool bug not keeping upper and lower case
	                   fix bug in syntax symbol lookup
	                   debug, clean up, and simplify syntax symbol table
	                   clean up DPRO output for content profiles
	                   fix problems in feature coding for phrase analysis
	                   clean up and test joining and splitting in Reparser
	                   make rules file for Reparser self-documenting
	                   update documentation

	v2.1.1  01nov2022  let users to try n-gram index sets with different n
	                   update documentation

	v2.2    08nov2022  fix bug with stopwords having periods and apostrophes 
	                   update documentation

	v2.3    20nov2022  expand builtin 4-grams past previous upper limit
	                   change numbering of n-gram indices to be more logical
	                   fix bug in squeezing of index vectors for clustering
	                   update documentation

	v2.4    06dec2022  fix bugs in buffering UTF-8 text input
	                   clean up and simplify Unicode conversion of UTF-8
	                   clean up AW segmentation code
	                   update documentation

	v2.5    17dec2022  fix display of match lists to indicate text subsegments
	                   improve reporting on cluster analysis
	                   add command line control of subsegmentation
	                   document command line control of cluster profile generation
	                   clean up and extend default literal n-grams for English
	                   add 4-grams to round up total to 2,600
	                   clean up indentation in source code
	                   update documentation

	v2.6	28dec2022  allow up to 16,384 vectors to be clustered in a batch
	                   allow for more clusters
	                   clean up indentation in source code
	                   fix subsegment designations in tools output
	                   update documentation 

	v2.6.1  10jan2023  fix bugs in inflectional stemming and test
	                   fix bug  in token substitution rule file name
	                   add TKNZR and DINF tools to AW repository
	                   clean build file for tools
	                   update documentation

	v2.7    18jan2023  add PLOTTR, RANKER, HUBBER analysis modules
	                   add general class to select items by degree of linkage
	                   move LinkMatrix to object package
	                   clean up clustering source file formatting
	                   update documentation

	v2.7.1  22jan2023  add DSRV, DQBK, DQBE tools for command line search
	                   clean up source code
		           update documentation

	v2.7.2  31jan2023  improve DSRV output
	                   clean up source code
	                   add DSMX and DSIM tools for testing
	                   update documentation

	v2.7.3  22feb2023  add DKYW tool to aid profile building
	                   add command line check for DQBE
	                   add command line option for DPRO
	                   clean up indentation of DQBK and DLST source
	                   update documentation
