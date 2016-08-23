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

import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class XmlCorpusForTaggingTest {
    @Test
    public void realXml() throws IOException {
        XmlCorpusForTagging corpus = new XmlCorpusForTagging(
                getClass().getResourceAsStream("/train1.xml"),
                "O",
                IndoEuropeanTokenizerFactory.INSTANCE);
        corpus.visitTest(new StringCompareObjectHandler<Tagging<String>>(
                "John/PER ran/O ./O",
                "The/O kid/O ran/O ./O",
                "John/PER likes/O Mary/PER ./O",
                "This/O Tim/PER lives/O in/O Washington/LOC",
                "Mary/PER Smith/PER is/O in/O New/LOC York/LOC City/LOC",
                "New/LOC York/LOC City/LOC is/O fun/O ./O",
                "Chicago/LOC is/O not/O like/O Washington/LOC")
        );

    }
}
