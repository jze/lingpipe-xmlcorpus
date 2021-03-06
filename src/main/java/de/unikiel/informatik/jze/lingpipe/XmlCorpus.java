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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 * @deprecated Use {@link XmlCorpusForChunking} instead.
 */
public class XmlCorpus extends XmlCorpusForChunking {

    public XmlCorpus(File file) throws IOException {
        super(file);
    }

    public XmlCorpus(InputStream input) throws IOException {
        super(input);
    }
}
