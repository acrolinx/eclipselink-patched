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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.junit.Test;

import com.google.common.collect.Lists;

public class AcrolinxFromClauseOptimizerTest
{

    @Test
    public void testOrderIsKeepedByDefault()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");

        unsortedTableAliases.put(a, a);
        unsortedTableAliases.put(b, b);
        unsortedTableAliases.put(c, table("d"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(unsortedTableAliases.keySet()), sorted);
    }

    @Test
    public void testTermEjbIsMovedUp()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");

        unsortedTableAliases.put(a, a);
        unsortedTableAliases.put(b, table("TERMEJB"));
        unsortedTableAliases.put(c, table("d"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(b, a, c), sorted);
    }

    @Test
    public void testCategoryEjbIsMovedUp()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");

        unsortedTableAliases.put(a, a);
        unsortedTableAliases.put(b, table("CATEGORYEJB"));
        unsortedTableAliases.put(c, table("d"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(b, a, c), sorted);
    }

    @Test
    public void testCategoryEjbIsOrderedBeforeTermEjb()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");

        unsortedTableAliases.put(a, a);
        unsortedTableAliases.put(b, table("CATEGORYEJB"));
        unsortedTableAliases.put(c, table("TERMEJB"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(b, c, a), sorted);
    }

    @Test
    public void testCategoryEjbIsOrderedBeforeTermEjb2()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");

        unsortedTableAliases.put(a, table("TERMEJB"));
        unsortedTableAliases.put(b, a);
        unsortedTableAliases.put(c, table("CATEGORYEJB"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(c, a, b), sorted);
    }

    @Test
    public void testTablesOrderedByAlias()
    {
        final Map<DatabaseTable, DatabaseTable> unsortedTableAliases = new LinkedHashMap<>();
        final DatabaseTable a = table("a");
        final DatabaseTable b = table("b");
        final DatabaseTable c = table("c");
        final DatabaseTable d = table("d");
        final DatabaseTable e = table("e");
        final DatabaseTable f = table("f");

        unsortedTableAliases.put(c, table("CATEGORYEJB"));
        unsortedTableAliases.put(f, a);
        unsortedTableAliases.put(d, a);
        unsortedTableAliases.put(b, a);
        unsortedTableAliases.put(e, a);
        unsortedTableAliases.put(a, table("TERMEJB"));

        final List<DatabaseTable> sorted = AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(unsortedTableAliases);

        assertEquals(Lists.newArrayList(c, a, b, d, e, f), sorted);
    }

    private static DatabaseTable table(final String name)
    {
        final DatabaseTable table = mock(DatabaseTable.class);
        when(table.getQualifiedName()).thenReturn(name);
        when(table.toString()).thenReturn(name);
        return table;
    }

    @Test
    public void testVersionIsSameAsThePatchedSQLSelectStatementClass()
    {
        final String assertMsg = "If this test fails you probably updated EclipseLink. Please make sure: \n"
                + "- to review the patched class:\n"
                + SQLSelectStatement.class.getCanonicalName()
                + "\nin line 541. The AcrolinxFromClauseOptimizer needs to be called for optimizing the order of the FROM-statement for ms sql:\n"
                + "AcrolinxFromClauseOptimizer.getAliasesOptimizedForTermDatabase(getTableAliases()).\n"
                + "If CategoryEJB is not the first table in FROM the 'default grouping term'-query will take about 5 minutes instead of 15 sec.";
        assertEquals(assertMsg, "2.6.5", Version.getVersion());
        assertEquals(assertMsg, "20170607", Version.getBuildDate());
    }
}
