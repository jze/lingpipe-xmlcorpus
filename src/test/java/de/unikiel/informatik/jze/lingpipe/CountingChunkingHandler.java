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

import com.aliasi.chunk.BioTagChunkCodec;
import com.aliasi.chunk.Chunking;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class CountingChunkingHandler implements ObjectHandler<Chunking> {
    private BioTagChunkCodec codec = new BioTagChunkCodec(IndoEuropeanTokenizerFactory.INSTANCE, true);
    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public void handle(Chunking chunking) {
        count++;
        codec.toTagging(chunking);
    }
}
