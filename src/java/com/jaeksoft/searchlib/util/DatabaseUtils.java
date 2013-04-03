/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2013 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.util;

import java.sql.SQLException;
import java.util.List;

import com.jaeksoft.pojodbc.Transaction;
import com.jaeksoft.searchlib.crawler.database.DatabaseCrawlSql.SqlUpdateMode;

public class DatabaseUtils {

	public final static String PRIMARY_KEY_VARIABLE_NAME = "$PK";

	final private static String toIdList(List<String> pkList, boolean quote) {
		StringBuffer sb = new StringBuffer();
		boolean b = false;
		for (String uk : pkList) {
			if (b)
				sb.append(',');
			else
				b = true;
			if (quote) {
				sb.append('\'');
				sb.append(uk.replace("'", "''"));
				sb.append('\'');
			} else
				sb.append(uk);
		}
		return sb.toString();
	}

	final public static void update(Transaction transaction, String pk,
			SqlUpdateMode sqlUpdateMode, String sqlUpdate) throws SQLException {
		if (sqlUpdateMode != SqlUpdateMode.ONE_CALL_PER_PRIMARY_KEY)
			return;
		String sql = sqlUpdate.replace(PRIMARY_KEY_VARIABLE_NAME, pk);
		transaction.update(sql);
		transaction.commit();
	}

	final public static void update(Transaction transaction,
			List<String> pkList, SqlUpdateMode sqlUpdateMode, String sqlUpdate)
			throws SQLException {
		if (sqlUpdateMode == SqlUpdateMode.NO_CALL)
			return;
		String lastSql = null;
		if (sqlUpdateMode == SqlUpdateMode.ONE_CALL_PER_PRIMARY_KEY) {
			for (String uk : pkList) {
				lastSql = sqlUpdate.replace(PRIMARY_KEY_VARIABLE_NAME, uk);
				transaction.update(lastSql);
			}
		} else if (sqlUpdateMode == SqlUpdateMode.PRIMARY_KEY_LIST) {
			lastSql = sqlUpdate.replace(PRIMARY_KEY_VARIABLE_NAME,
					toIdList(pkList, false));
			transaction.update(lastSql);
		} else if (sqlUpdateMode == SqlUpdateMode.PRIMARY_KEY_CHAR_LIST) {
			lastSql = sqlUpdate.replace(PRIMARY_KEY_VARIABLE_NAME,
					toIdList(pkList, true));
			transaction.update(lastSql);
		}
		transaction.commit();
	}
}