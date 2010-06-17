/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.commons.beanutils;


import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


/**
 * <p>Mock object that implements enough of <code>java.sql.ResultSet</code>
 * to exercise the {@link ResultSetDynaClass} functionality.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 745080 $ $Date: 2009-02-17 14:05:02 +0000 (Tue, 17 Feb 2009) $
 */

public class TestResultSet implements InvocationHandler {


    // ----------------------------------------------------- Instance Variables


    /**
     * Current row number (0 means "before the first one").
     */
    protected int row = 0;


    /**
     * The constant (per run) value used to initialize date/time/timestamp.
     */
    protected long timestamp = System.currentTimeMillis();

    /**
     * Meta data for the result set.
     */
    protected ResultSetMetaData resultSetMetaData;

    /**
     * Factory method for creating {@link ResultSet} proxies.
     *
     * @return A result set proxy
     */
    public static ResultSet createProxy() {
        return TestResultSet.createProxy(new TestResultSet());
    }

    /**
     * Factory method for creating {@link ResultSet} proxies.
     *
     * @param invocationHandler Invocation Handler
     * @return A result set proxy
     */
    public static ResultSet createProxy(InvocationHandler invocationHandler) {
        ClassLoader classLoader = ResultSet.class.getClassLoader();
        Class[] interfaces = new Class[] { ResultSet.class };
        return (ResultSet)Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }
    
    /**
     * Create a proxy ResultSet.
     */
    public TestResultSet() {
        this(TestResultSetMetaData.createProxy());
    }
    
    /**
     * Create a proxy ResultSet with the specified meta data.
     *
     * @param resultSetMetaData The result set meta data
     */
    public TestResultSet(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * Handles method invocation on the ResultSet proxy. 
     *
     * @param proxy The proxy ResultSet object
     * @param method the method being invoked
     * @param args The method arguments
     * @return The result of invoking the method.
     * @throws Throwable if an error occurs.
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("close".equals(methodName)) {
            return null;
        } if ("getMetaData".equals(methodName)) {
            return getMetaData();
        } if ("getObject".equals(methodName)) {
            return getObject(columnName(args[0]));
        } if ("getDate".equals(methodName)) {
            return getDate(columnName(args[0]));
        } if ("getTime".equals(methodName)) {
            return getTime(columnName(args[0]));
        } if ("getTimestamp".equals(methodName)) {
            return getTimestamp(columnName(args[0]));
        } if ("next".equals(methodName)) {
            return (next() ? Boolean.TRUE : Boolean.FALSE);
        } if ("updateObject".equals(methodName)) {
            updateObject((String)args[0], args[1]);
            return null;
        }
        
        throw new UnsupportedOperationException(methodName + " not implemented");
    }

    private String columnName(Object arg) throws SQLException {
        if (arg instanceof Integer) {
            return resultSetMetaData.getColumnName(((Integer)arg).intValue());
        } else {
            return (String)arg;
        }
    }

    // ---------------------------------------------------- Implemented Methods


    public void close() throws SQLException {
        // No action required
    }


    public ResultSetMetaData getMetaData() throws SQLException {
        return resultSetMetaData;
    }


    public Object getObject(String columnName) throws SQLException {
        if (row > 5) {
            throw new SQLException("No current row");
        }
        if ("bigDecimalProperty".equals(columnName)) {
            return (new BigDecimal(123.45));
        } else if ("booleanProperty".equals(columnName)) {
            if ((row % 2) == 0) {
                return (Boolean.TRUE);
            } else {
                return (Boolean.FALSE);
            }
        } else if ("byteProperty".equals(columnName)) {
            return (new Byte((byte) row));
        } else if ("dateProperty".equals(columnName)) {
            return (new Date(timestamp));
        } else if ("doubleProperty".equals(columnName)) {
            return (new Double(321.0));
        } else if ("floatProperty".equals(columnName)) {
            return (new Float((float) 123.0));
        } else if ("intProperty".equals(columnName)) {
            return (new Integer(100 + row));
        } else if ("longProperty".equals(columnName)) {
            return (new Long(200 + row));
        } else if ("nullProperty".equals(columnName)) {
            return (null);
        } else if ("shortProperty".equals(columnName)) {
            return (new Short((short) (300 + row)));
        } else if ("stringProperty".equals(columnName)) {
            return ("This is a string");
        } else if ("timeProperty".equals(columnName)) {
            return (new Time(timestamp));
        } else if ("timestampProperty".equals(columnName)) {
            return (new Timestamp(timestamp));
        } else {
            throw new SQLException("Unknown column name " + columnName);
        }
    }

    public Date getDate(String columnName) throws SQLException {
        return (new Date(timestamp));
    }

    public Time getTime(String columnName) throws SQLException {
        return (new Time(timestamp));
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return (new Timestamp(timestamp));
    }

    public boolean next() throws SQLException {
        if (row++ < 5) {
            return (true);
        } else {
            return (false);
        }
    }


    public void updateObject(String columnName, Object x)
        throws SQLException {
        if (row > 5) {
            throw new SQLException("No current row");
        }
        // FIXME - updateObject()
    }


    // -------------------------------------------------- Unimplemented Methods


    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int findColumn(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Array getArray(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getAsciiStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    public BigDecimal getBigDecimal(int columnIndex, int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    public BigDecimal getBigDecimal(String columnName, int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getBinaryStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Blob getBlob(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean getBoolean(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte getByte(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public byte[] getBytes(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Reader getCharacterStream(int columnIndex)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Reader getCharacterStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Clob getClob(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }




    public Date getDate(String columnName, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public double getDouble(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public float getFloat(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getInt(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public long getLong(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(int columnIndex, Map map) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Object getObject(String columnName, Map map) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Ref getRef(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public short getShort(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public String getString(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }



    public Time getTime(String columnName, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public Timestamp getTimestamp(int columnIndex, Calendar cal)
        throws SQLException {
        throw new UnsupportedOperationException();
    }



    public Timestamp getTimestamp(String columnName, Calendar cal)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public int getType() throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    /** @deprecated */
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public URL getURL(String columnName) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void setFetchSize(int size) throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(int columnPosition, Array x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateArray(String columnName, Array x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(int columnPosition, InputStream x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateAsciiStream(String columnName, InputStream x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(int columnPosition, BigDecimal x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBigDecimal(String columnName, BigDecimal x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(int columnPosition, InputStream x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBinaryStream(String columnName, InputStream x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(int columnPosition, Blob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBlob(String columnName, Blob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(int columnPosition, boolean x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBoolean(String columnName, boolean x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(int columnPosition, byte x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateByte(String columnName, byte x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(int columnPosition, byte x[])
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateBytes(String columnName, byte x[])
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(int columnPosition, Reader x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateCharacterStream(String columnName, Reader x, int len)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(int columnPosition, Clob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateClob(String columnName, Clob x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(int columnPosition, Date x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDate(String columnName, Date x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(int columnPosition, double x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateDouble(String columnName, double x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateFloat(int columnPosition, float x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateFloat(String columnName, float x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(int columnPosition, int x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateInt(String columnName, int x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(int columnPosition, long x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateLong(String columnName, long x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(int columnPosition)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateNull(String columnName)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(int columnPosition, Object x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(int columnPosition, Object x, int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateObject(String columnName, Object x, int scale)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(int columnPosition, Ref x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRef(String columnName, Ref x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(int columnPosition, short x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateShort(String columnName, short x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(int columnPosition, String x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateString(String columnName, String x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(int columnPosition, Time x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTime(String columnName, Time x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(int columnPosition, Timestamp x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public void updateTimestamp(String columnName, Timestamp x)
        throws SQLException {
        throw new UnsupportedOperationException();
    }


    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }


}
