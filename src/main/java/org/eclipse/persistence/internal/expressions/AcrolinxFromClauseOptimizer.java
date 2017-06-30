/*******************************************************************************
 * (C) 2017 Acrolinx GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/

package org.eclipse.persistence.internal.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.internal.helper.DatabaseTable;

public class AcrolinxFromClauseOptimizer
{
    public static List<DatabaseTable> getAliasesOptimizedForTermDatabase(
            final Map<DatabaseTable, DatabaseTable> unsortedTableAliases)
    {
        final List<DatabaseTable> keySet = new ArrayList<>(unsortedTableAliases.keySet());
        if (!containsRelevantTermDatabaseTables(unsortedTableAliases)) {
            return keySet; // Nothing to do
        }

        Collections.sort(keySet, new Comparator<DatabaseTable>()
        {
            @Override
            public int compare(final DatabaseTable o1, final DatabaseTable o2)
            {
                final String name1 = unsortedTableAliases.get(o1).getQualifiedName();
                final String name2 = unsortedTableAliases.get(o2).getQualifiedName();

                if (name1.equals(name2)) {
                    return o1.getQualifiedName().compareTo(o2.getQualifiedName());
                }

                if ("CATEGORYEJB".equals(name1)) {
                    return -1;
                }
                if ("CATEGORYEJB".equals(name2)) {
                    return 1;
                }
                if ("TERMEJB".equals(name1)) {
                    return -1;
                }
                if ("TERMEJB".equals(name2)) {
                    return 1;
                }

                return o1.getQualifiedName().compareTo(o2.getQualifiedName());
            }
        });
        return keySet;
    }

    private static boolean containsRelevantTermDatabaseTables(
            final Map<DatabaseTable, DatabaseTable> unsortedTableAliases)
    {
        for (final Entry<DatabaseTable, DatabaseTable> mapping : unsortedTableAliases.entrySet()) {
            final String targetTable = mapping.getValue().getQualifiedName();
            if ("TERMEJB".equals(targetTable) || "CATEGORYEJB".equals(targetTable)) {
                return true;
            }
        }

        return false;
    }
}
