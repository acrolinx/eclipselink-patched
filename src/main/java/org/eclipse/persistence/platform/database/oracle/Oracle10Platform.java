/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OraclePreparedStatement;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs.
 */
public class Oracle10Platform extends Oracle9Platform  {
    
    public Oracle10Platform(){
        super();
    }

    /**
     * Build the hint string used for first rows.
     * 
     * Allows it to be overridden
     * @param max
     * @return
     */
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
     * Write LOB value - Oracle 10 deprecates some methods used in the superclass
     */
    @Override
    public void writeLOB(DatabaseField field, Object value, ResultSet resultSet, AbstractSession session) throws SQLException {
        if (isBlob(field.getType())) {
            //change for 338585 to use getName instead of getNameDelimited
            java.sql.Blob blob = (java.sql.Blob)resultSet.getObject(field.getName());
            blob.setBytes(1, (byte[])value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_BLOB", Long.valueOf(blob.length()), field.getName());
        } else if (isClob(field.getType())) {
            //change for 338585 to use getName instead of getNameDelimited
            java.sql.Clob clob = (java.sql.Clob)resultSet.getObject(field.getName());
            clob.setString(1, (String)value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_CLOB", Long.valueOf(clob.length()), field.getName());
        } else {
            //do nothing for now, open to BFILE or NCLOB types
        }
    }
    
    /**
     * INTERNAL:
     * Supports Batch Writing with Optimistic Locking.
     */
    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call){
        return true;//usesNativeBatchWriting || !call.hasParameters();
    }
}
