/*
  Copyright (C) 2016 Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 
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

import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.TokenizerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class XmlCorpusForTagging extends Corpus<ObjectHandler<Tagging<String>>> {
    private List<Tagging<String>> tagging = new ArrayList<>();

    private InputStream input;
    private TokenizerFactory tokenizer;
    private String defaultTag;

    public XmlCorpusForTagging(File file, String defaultTag, TokenizerFactory tokenizer) throws IOException {
        this(new FileInputStream(file), defaultTag, tokenizer);
    }

    public XmlCorpusForTagging(InputStream input, String defaultTag, TokenizerFactory tokenizer) throws IOException {
        this.input = input;
        this.tokenizer = tokenizer;
        this.defaultTag = defaultTag;
        init();
    }

    Tagging<String> processLine(String line) {

        // Chuck opening and closing brackets.
        if (StringUtils.countMatches(line, "<") != StringUtils.countMatches(line, ">")) {
            throw new IllegalArgumentException("Number of opening and closing brackets does not match. " + line);
        }

        List<String> tokens = new ArrayList<>();
        List<String> tags = new ArrayList<>();

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
                if (StringUtils.isNotBlank(text)) {
                    // untagged text
                    addTokensAndTags(tokens, tags, defaultTag, text);
                }
                addTokensAndTags(tokens, tags, tag, data);
                tag = "";
                data = "";
                text = "";
            } else if (c == '>' && inStartTag) {
                inStartTag = false;
                inElement = true;
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
                if (inElement) {
                    data = data + c;
                } else {
                    text = text + c;
                }
            }
        }

        addTokensAndTags(tokens, tags, defaultTag, text);

        if (numberOfOpenTags != 0) {
            throw new IllegalArgumentException("Number of opening and closing tags does not match. " + line);
        }

        return new Tagging<>(tokens, tags);
    }

    /**
     * Tokenize the text. For each token add the token and the tag to the specified lists of tokens and tags.
     */
    private void addTokensAndTags(List<String> tokens, List<String> tags, String tag, String text) {
        for (String token : tokenizer.tokenizer(text.toCharArray(), 0, text.length())) {
            tokens.add(token);
            tags.add(tag);
        }
    }

    public void visitTrain(ObjectHandler<Tagging<String>> handler) {
        for (Tagging<String> t : tagging) {
            handler.handle(t);
        }
    }

    public void visitTest(ObjectHandler<Tagging<String>> handler) {
        for (Tagging<String> t : tagging) {
            handler.handle(t);
        }
    }

    private void init() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String line = in.readLine();

        if (line.startsWith("<?xml ")) {
            // This is a real XML document. Only process lines that start with "<line>".
            line = in.readLine();
            while (line != null) {
                String s = StringUtils.trim(line);
                if (s.startsWith("<line>") && s.endsWith("</line>")) {
                    tagging.add(processLine(StringUtils.substringBetween(line, "<line>", "</line>")));
                }
                line = in.readLine();
            }
        } else {
            // pseudo XML file
            while (line != null) {
                tagging.add(processLine(line));
                line = in.readLine();
            }
        }
    }
}
