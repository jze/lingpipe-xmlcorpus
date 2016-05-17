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

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ChunkFactory;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ChunkingImpl;
import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class XmlCorpus extends Corpus<ObjectHandler<Chunking>> {

    private List<Chunking> chunkings = new ArrayList<>();

    private File file;

    public XmlCorpus(File file) throws IOException {
        this.file = file;
        init();
    }

    static Chunking chunking(String s, Chunk... chunks) {
        ChunkingImpl chunking = new ChunkingImpl(s);
        for (Chunk chunk : chunks)
            chunking.add(chunk);
        return chunking;
    }

    static Chunk chunk(int start, int end, String type) {
        return ChunkFactory.createChunk(start, end, type);
    }

    static Chunking processLine(String line) {

        // Chuck opening and closing brackets.
        if (StringUtils.countMatches(line, "<") != StringUtils.countMatches(line, ">")) {
            throw new IllegalArgumentException("Number of opening and closing brackets does not match. " + line);
        }

        List<Chunk> chunks = new ArrayList<>();

        int numberOfOpenTags = 0;
        String tag = "";
        boolean inStartTag = false;
        boolean inEndTag = false;
        boolean inElement = false;
        String text = "";
        String data = "";
        int startData = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '<') {
                inStartTag = true;

            } else if (c == '/' && inStartTag) {
                inStartTag = false;
                inEndTag = true;
                chunks.add(chunk(startData, text.length(), tag));
                tag = "";
                data = "";
            } else if (c == '>' && inStartTag) {
                inStartTag = false;
                inElement = true;
                startData = text.length();
                numberOfOpenTags++;
                if (numberOfOpenTags > 1) {
                    throw new IllegalArgumentException("Nesting of tags is not allowed. " + line);
                }
            } else if (c == '>' && inEndTag) {
                inEndTag = false;
                inElement = false;
                numberOfOpenTags--;
            } else if (inStartTag) {
                tag = tag + c;
            } else if (!inEndTag) {
                text = text + c;
                if (inElement) {
                    data = data + c;
                }
            }
        }

        if (numberOfOpenTags != 0) {
            throw new IllegalArgumentException("Number of opening and closing tags does not match. " + line);
        }

        return chunking(text, chunks.toArray(new Chunk[chunks.size()]));
    }

    /**
     * Formats the specified chunking as with XML tags.
     */
    public static String chunkingToXml(Chunking chunking) {
        return chunkingToXml(chunking, false);
    }

    /**
     * Formats the specified chunking as with XML tags. Optionally the chunks scores are added as attribute .
     */
    public static String chunkingToXml(Chunking chunking, boolean includeScore) {
        StringBuilder result = new StringBuilder();

        CharSequence text = chunking.charSequence();
        LinkedList<Chunk> chunks = new LinkedList<>(chunking.chunkSet());

        if (chunks.isEmpty()) {
            return text.toString();
        }

        Collections.sort(chunks, new Comparator<Chunk>() {
            @Override
            public int compare(Chunk o1, Chunk o2) {
                return o1.start() - o2.start();
            }
        });

        // text before first chunk

        int last = 0;

        for (Chunk chunk : chunks) {

            if (chunk.start() > last) {
                result.append(text.subSequence(last, chunk.start()));
            }
            last = chunk.end();

            result.append('<');
            result.append(chunk.type());
            if (includeScore) {
                result.append(" score=\"");
                result.append(chunk.score());
                result.append('"');
            }
            result.append('>');

            result.append(text.subSequence(chunk.start(), chunk.end()));

            result.append("</");
            result.append(chunk.type());
            result.append('>');


        }

        // text after last chunk
        if (chunks.getLast().end() < text.length()) {
            result.append(text.subSequence(chunks.getLast().end(), text.length()));
        }

        return result.toString();
    }

    public void visitTrain(ObjectHandler<Chunking> handler) {
        for (Chunking chunking : chunkings) {
            handler.handle(chunking);
        }
    }

    private void init() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        while (line != null) {
            chunkings.add(processLine(line));
            line = in.readLine();
        }
    }

    public void visitTest(ObjectHandler<Chunking> handler) {
        /* no op */
    }

}
