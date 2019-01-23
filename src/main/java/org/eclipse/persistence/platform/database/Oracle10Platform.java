/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OraclePreparedStatement;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;

import oracle.jdbc.OraclePreparedStatement;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;

/**
 * ATTN: This is a literal copy of the class of the same name from the EclipseLink 2.6.3 source
 * except for {@link #canBatchWriteWithOptimisticLocking(DatabaseCall)} and
 * {@link #Oracle10Platform()}
 * <p>
 * Patched Oracle Platform so that
 * <li>batch writes are disabled in presence of optimistic locks (the default behavior of all the
 * other database platforms)
 * <li>no locator is used for LOB-writing (case 59795)
 *
 * <p>
 * <b>Purpose:</b> Supports usage of certain Oracle JDBC specific APIs.
 */
public class Oracle10Platform extends Oracle9Platform {

    public Oracle10Platform(){
        super();
        // PATCHED - see Case 59795
        usesLocatorForLOBWrite = false;
    }

    /**
     * Build the hint string used for first rows.
     *
     * Allows it to be overridden
     * @param max
     * @return
     */
    @Override
    protected String buildFirstRowsHint(int max){
        //bug 374136: override setting the FIRST_ROWS hint as this is not needed on Oracle10g
        return "";
    }

    /**
     * Internal: This gets called on each batch statement execution
     * Needs to be implemented so that it returns the number of rows successfully modified
     * by this statement for optimistic locking purposes (if useNativeBatchWriting is enabled, and
     * the call uses optimistic locking).
     *
     * @param isStatementPrepared - flag is set to true if this statement is prepared
     * @return - number of rows modified/deleted by this statement
     */
    @Override
    public int executeBatch(Statement statement, boolean isStatementPrepared) throws java.sql.SQLException {
        if (usesNativeBatchWriting() && isStatementPrepared) {
            int rowCount = 0;
            try {
                rowCount = ((OraclePreparedStatement)statement).sendBatch();
            } finally {
                ((OraclePreparedStatement) statement).setExecuteBatch(1);
            }
            return rowCount;
        } else {
            @SuppressWarnings("unused")
            int[] results = statement.executeBatch();
            return statement.getUpdateCount();
        }
    }

    /**
     * INTERNAL:
     * Indicate whether app. server should unwrap connection
     * to use lob locator.
     * No need to unwrap connection because
     * writeLob method doesn't use oracle proprietary classes.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return false;
    }

    /**
     * INTERNAL:
     * Supports Batch Writing with Optimistic Locking.
     *
     * PATCHED: Oracle Platform so that batch writes are disabled in presence of optimistic locks
     * (the default behavior of all the other database platforms)
     */
    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call){
        return false;
    }
}
