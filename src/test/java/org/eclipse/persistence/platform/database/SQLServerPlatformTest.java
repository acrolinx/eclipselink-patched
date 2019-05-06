/*******************************************************************************
 * (C) 2019 Acrolinx GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/

package org.eclipse.persistence.platform.database;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SQLServerPlatformTest
{
    @Test
    public void testDoesNotSupportsSequenceObjects() throws SQLException {
        final Connection connection = mock(Connection.class);
        final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        when(databaseMetaData.getDatabaseMajorVersion()).thenReturn(11);
        when(databaseMetaData.getDriverVersion()).thenReturn("4.0.0");
        when(connection.getMetaData()).thenReturn(databaseMetaData);

        final SQLServerPlatform sqlServerPlatform = new SQLServerPlatform();
        sqlServerPlatform.initializeConnectionData(connection);

        assertFalse(sqlServerPlatform.supportsSequenceObjects());
    }
}