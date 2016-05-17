## XmlCorpus for LingPipe

http://alias-i.com/lingpipe/

## Train chunkings
The XmlCorpus class can be used as a replacement for every Corpus used to train chunking. Instead of programming your own corpus-class you can simply use an instance of XmlCorpus:

	corpus = new XmlCorpus(new File(getClass().getResource("/training.xml").getFile()));

The file containing the training data must look like this. Each line will be used as one training input.

	<PER>Mary</PER> ran.
	The kid ran.
	<PER>John</PER> likes <PER>Mary</PER>.
	<PER>Tim</PER> lives in <LOC>Washington</LOC>.
	<PER>Mary Smith</PER> is in <LOC>New York City</LOC>.
	<LOC>New York City</LOC> is fun.
	<LOC>Chicago</LOC> is not like <LOC>Washington</LOC>.

## Print chunkings
Since the usual Chunking serialization is a bit difficult to read, a helper method XmlCorpus.chunkingToXml is provided that formats a chunking with XML tags.