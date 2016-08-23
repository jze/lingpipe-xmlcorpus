/*
  Copyright (C) 2015 Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unikiel.informatik.jze.lingpipe;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static de.unikiel.informatik.jze.lingpipe.XmlCorpusForChunking.chunk;
import static de.unikiel.informatik.jze.lingpipe.XmlCorpusForChunking.chunking;
import static org.junit.Assert.assertEquals;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class XmlCorpusForChunkingTest {

    @Test
    public void testProcessLine() {
        assertEquals(chunking("John ran.",
                chunk(0, 4, "PER")), XmlCorpusForChunking.processLine("<PER>John</PER> ran."));

        assertEquals(chunking(""), XmlCorpusForChunking.processLine(""));
        assertEquals(chunking("The"), XmlCorpusForChunking.processLine("The"));

        assertEquals(chunking("Mary ran.",
                chunk(0, 4, "PER")), XmlCorpusForChunking.processLine("<PER>Mary</PER> ran."));
        assertEquals(chunking("The kid ran."), XmlCorpusForChunking.processLine("The kid ran."));
        assertEquals(chunking("John likes Mary.",
                chunk(0, 4, "PER"),
                chunk(11, 15, "PER")), XmlCorpusForChunking.processLine("<PER>John</PER> likes <PER>Mary</PER>."));
        assertEquals(chunking("Tim lives in Washington",
                chunk(0, 3, "PER"),
                chunk(13, 23, "LOC")), XmlCorpusForChunking.processLine("<PER>Tim</PER> lives in <LOC>Washington</LOC>"));
        assertEquals(chunking("Mary Smith is in New York City",
                chunk(0, 10, "PER"),
                chunk(17, 30, "LOC")), XmlCorpusForChunking.processLine("<PER>Mary Smith</PER> is in <LOC>New York City</LOC>"));
        assertEquals(chunking("New York City is fun",
                chunk(0, 13, "LOC")), XmlCorpusForChunking.processLine("<LOC>New York City</LOC> is fun"));
        assertEquals(chunking("Chicago is not like Washington",
                chunk(0, 7, "LOC"),
                chunk(20, 30, "LOC")), XmlCorpusForChunking.processLine("<LOC>Chicago</LOC> is not like <LOC>Washington</LOC>"));
    }

    @Test
    public void processLine_adjacentTags() {
        assertEquals(chunking("Hof u. Mühlenpächter",
                chunk(0, 3, "oc-head"), chunk(7, 13, "oc-head"), chunk(13, 20, "oc-body")),
                XmlCorpusForChunking.processLine("<oc-head>Hof</oc-head> u. <oc-head>Mühlen</oc-head><oc-body>pächter</oc-body>"));
    }

    @Test
    public void testProcessLine_InvalidInput() {
        try {
            XmlCorpusForChunking.processLine("<PERJohn ran.");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            XmlCorpusForChunking.processLine("<PER>John ran.");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            XmlCorpusForChunking.processLine("<B><A>John<A> ran.</B>");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void file() throws IOException {
        XmlCorpusForChunking corpus = new XmlCorpusForChunking(new File(getClass().getResource("/wk1vermisste.xml").getFile()));
        CountingChunkingHandler handler = new CountingChunkingHandler();
        corpus.visitTrain(handler);
        assertEquals(50, handler.getCount());
    }

    @Test
    public void inputStream() throws IOException {
        XmlCorpusForChunking corpus = new XmlCorpusForChunking(getClass().getResourceAsStream("/wk1vermisste.xml"));
        CountingChunkingHandler handler = new CountingChunkingHandler();
        corpus.visitTrain(handler);
        assertEquals(50, handler.getCount());
    }

    @Test
    public void chunkingToXml() {
        assertEquals("<PER>John</PER> ran.", XmlCorpusForChunking.chunkingToXml(chunking("John ran.",
                chunk(0, 4, "PER"))));

        assertEquals("", XmlCorpusForChunking.chunkingToXml(chunking("")));
        assertEquals("The", XmlCorpusForChunking.chunkingToXml(chunking("The")));

        assertEquals("<PER>Mary</PER> ran.", XmlCorpusForChunking.chunkingToXml(chunking("Mary ran.",
                chunk(0, 4, "PER"))));
        assertEquals("The kid ran.", XmlCorpusForChunking.chunkingToXml(chunking("The kid ran.")));
        assertEquals("<PER>John</PER> likes <PER>Mary</PER>.", XmlCorpusForChunking.chunkingToXml(chunking("John likes Mary.",
                chunk(0, 4, "PER"),
                chunk(11, 15, "PER"))));
        assertEquals("<PER>Tim</PER> lives in <LOC>Washington</LOC>", XmlCorpusForChunking.chunkingToXml(chunking("Tim lives in Washington",
                chunk(0, 3, "PER"),
                chunk(13, 23, "LOC"))));
        assertEquals("<PER>Mary Smith</PER> is in <LOC>New York City</LOC>", XmlCorpusForChunking.chunkingToXml(chunking("Mary Smith is in New York City",
                chunk(0, 10, "PER"),
                chunk(17, 30, "LOC"))));
        assertEquals("<LOC>New York City</LOC> is fun", XmlCorpusForChunking.chunkingToXml(chunking("New York City is fun",
                chunk(0, 13, "LOC"))));
        assertEquals("<LOC>Chicago</LOC> is not like <LOC>Washington</LOC>", XmlCorpusForChunking.chunkingToXml(chunking("Chicago is not like Washington",
                chunk(0, 7, "LOC"),
                chunk(20, 30, "LOC"))));
    }

    @Test
    public void realXml() throws IOException {
        XmlCorpusForChunking corpus = new XmlCorpusForChunking(getClass().getResourceAsStream("/train1.xml"));
        CountingChunkingHandler handler = new CountingChunkingHandler();
        corpus.visitTrain(handler);
        assertEquals(7, handler.getCount());

    }
}
