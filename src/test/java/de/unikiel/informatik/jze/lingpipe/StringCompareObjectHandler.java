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

import com.aliasi.corpus.ObjectHandler;
import org.junit.Assert;

/**
 * Compare the results returned by the "handle" method with the expected results by converting them to Strings and
 * do a string comparison.
 *
 * @author Jesper Zedlitz <j.zedlitz@email.uni-kiel.de>
 */
public class StringCompareObjectHandler<T> implements ObjectHandler<T> {

    private Object[] expectedResults;
    private int count = 0;

    public StringCompareObjectHandler(Object... expectedResults) {
        this.expectedResults = expectedResults;
    }

    @Override
    public void handle(T t) {
        Assert.assertEquals(expectedResults[count++].toString(), t.toString());
    }
}
