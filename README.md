# XmlCorpus for LingPipe

http://alias-i.com/lingpipe/

## Usage
To use the XmlCorpus in your project clone this repository. Build and install the library with Maven:

```bash
git clone https://github.com/jze/lingpipe-xmlcorpus.git
cd lingpipe-xmlcorpus/
mvn install
```

Now you can include it into your Maven projects like this:

```xml
<dependency>
    <groupId>de.unikiel.informatik.jze</groupId>
    <artifactId>lingpipe-xmlcorpus</artifactId>
    <version>0.1</version>
</dependency>
```

## Train chunkings
The XmlCorpus class can be used as a replacement for every Corpus used to train chunking. Instead of programming your own corpus-class you can simply use an instance of XmlCorpus:

```java
corpus = new XmlCorpus(new File(getClass().getResource("/training.xml").getFile()));
```

Two input formats are allowed:

1. a pseudo XML file with one training input per line
2. a real XML file with root element "training" and each input line wrapped with a "line" tag

### pseudo XML file input
The file containing the training data must look like this. Each line will be used as one training input.

```xml
<PER>Mary</PER> ran.
The kid ran.
<PER>John</PER> likes <PER>Mary</PER>.
<PER>Tim</PER> lives in <LOC>Washington</LOC>.
<PER>Mary Smith</PER> is in <LOC>New York City</LOC>.
<LOC>New York City</LOC> is fun.
<LOC>Chicago</LOC> is not like <LOC>Washington</LOC>.
```

### real XML input 
You can also use a real XML document as input. In that case the input file must look like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<training>
    <line><PER>John</PER> ran.</line>
    <line>The kid ran.</line>
    <line><PER>John</PER> likes <PER>Mary</PER>.</line>
    <line>This <PER>Tim</PER> lives in <LOC>Washington</LOC></line>
    <line><PER>Mary Smith</PER> is in <LOC>New York City</LOC></line>
    <line><LOC>New York City</LOC> is fun.</line>
    <line><LOC>Chicago</LOC> is not like <LOC>Washington</LOC></line>
</training>
```

## Print chunkings
Since the usual Chunking serialization is a bit difficult to read, a helper method XmlCorpus.chunkingToXml is provided that formats a chunking with XML tags.
