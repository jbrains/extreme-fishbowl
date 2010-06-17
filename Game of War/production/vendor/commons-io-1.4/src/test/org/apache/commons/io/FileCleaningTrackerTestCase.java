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
package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.io.testtools.FileBasedTestCase;

/**
 * This is used to test {@link FileCleaningTracker} for correctness.
 *
 * @author Noel Bergman
 * @author Martin Cooper
 *
 * @version $Id: FileCleanerTestCase.java 482437 2006-12-05 01:13:05Z scolebourne $

 * @see FileCleaner
 */
public class FileCleaningTrackerTestCase extends FileBasedTestCase {
    protected FileCleaningTracker newInstance() {
        return new FileCleaningTracker();
    }

    private File testFile;
    private FileCleaningTracker theInstance;

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(FileCleaningTrackerTestCase.class);
    }

    public FileCleaningTrackerTestCase(String name) throws IOException {
        super(name);

        testFile = new File(getTestDirectory(), "file-test.txt");
    }

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        theInstance = newInstance();
        getTestDirectory().mkdirs();
    }

    /** @see junit.framework.TestCase#tearDown() */
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
        
        // reset file cleaner class, so as not to break other tests

        /**
         * The following block of code can possibly be removed when the
         * deprecated {@link FileCleaner} is gone. The question is, whether
         * we want to support reuse of {@link FileCleaningTracker} instances,
         * which we should, IMO, not.
         */
        {
            theInstance.q = new ReferenceQueue();
            theInstance.trackers.clear();
            theInstance.exitWhenFinished = false;
            theInstance.reaper = null;
        }
        
        theInstance = null;
    }

    //-----------------------------------------------------------------------
    public void testFileCleanerFile() throws Exception {
        String path = testFile.getPath();
        
        assertEquals(false, testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        assertEquals(true, testFile.exists());
        
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(path, r);
        assertEquals(1, theInstance.getTrackCount());
        
        r.close();
        testFile = null;
        r = null;

        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(false, new File(path).exists());
    }

    public void testFileCleanerDirectory() throws Exception {
        createFile(testFile, 100);
        assertEquals(true, testFile.exists());
        assertEquals(true, getTestDirectory().exists());
        
        Object obj = new Object();
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj);
        assertEquals(1, theInstance.getTrackCount());
        
        obj = null;

        waitUntilTrackCount();

        assertEquals(0, theInstance.getTrackCount());
        assertEquals(true, testFile.exists());  // not deleted, as dir not empty
        assertEquals(true, testFile.getParentFile().exists());  // not deleted, as dir not empty
    }

    public void testFileCleanerDirectory_NullStrategy() throws Exception {
        createFile(testFile, 100);
        assertEquals(true, testFile.exists());
        assertEquals(true, getTestDirectory().exists());
        
        Object obj = new Object();
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj, (FileDeleteStrategy) null);
        assertEquals(1, theInstance.getTrackCount());
        
        obj = null;

        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(true, testFile.exists());  // not deleted, as dir not empty
        assertEquals(true, testFile.getParentFile().exists());  // not deleted, as dir not empty
    }

    public void testFileCleanerDirectory_ForceStrategy() throws Exception {
        createFile(testFile, 100);
        assertEquals(true, testFile.exists());
        assertEquals(true, getTestDirectory().exists());
        
        Object obj = new Object();
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(getTestDirectory(), obj, FileDeleteStrategy.FORCE);
        assertEquals(1, theInstance.getTrackCount());
        
        obj = null;

        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(false, testFile.exists());
        assertEquals(false, testFile.getParentFile().exists());
    }

    public void testFileCleanerNull() throws Exception {
        try {
            theInstance.track((File) null, new Object());
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track((File) null, new Object(), FileDeleteStrategy.NORMAL);
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track((String) null, new Object());
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            theInstance.track((String) null, new Object(), FileDeleteStrategy.NORMAL);
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testFileCleanerExitWhenFinishedFirst() throws Exception {
        assertEquals(false, theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(null, theInstance.reaper);
        
        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(null, theInstance.reaper);
    }

    public void testFileCleanerExitWhenFinished_NoTrackAfter() throws Exception {
        assertEquals(false, theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(null, theInstance.reaper);
        
        String path = testFile.getPath();
        Object marker = new Object();
        try {
            theInstance.track(path, marker);
            fail();
        } catch (IllegalStateException ex) {
            // expected
        }
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(null, theInstance.reaper);
    }

    public void testFileCleanerExitWhenFinished1() throws Exception {
        String path = testFile.getPath();
        
        assertEquals(false, testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        assertEquals(true, testFile.exists());
        
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(path, r);
        assertEquals(1, theInstance.getTrackCount());
        assertEquals(false, theInstance.exitWhenFinished);
        assertEquals(true, theInstance.reaper.isAlive());
        
        assertEquals(false, theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(true, theInstance.reaper.isAlive());
        
        r.close();
        testFile = null;
        r = null;

        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(false, new File(path).exists());
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(false, theInstance.reaper.isAlive());
    }

    public void testFileCleanerExitWhenFinished2() throws Exception {
        String path = testFile.getPath();
        
        assertEquals(false, testFile.exists());
        RandomAccessFile r = new RandomAccessFile(testFile, "rw");
        assertEquals(true, testFile.exists());
        
        assertEquals(0, theInstance.getTrackCount());
        theInstance.track(path, r);
        assertEquals(1, theInstance.getTrackCount());
        assertEquals(false, theInstance.exitWhenFinished);
        assertEquals(true, theInstance.reaper.isAlive());
        
        r.close();
        testFile = null;
        r = null;

        waitUntilTrackCount();
        
        assertEquals(0, theInstance.getTrackCount());
        assertEquals(false, new File(path).exists());
        assertEquals(false, theInstance.exitWhenFinished);
        assertEquals(true, theInstance.reaper.isAlive());
        
        assertEquals(false, theInstance.exitWhenFinished);
        theInstance.exitWhenFinished();
        for (int i = 0; i < 20 && theInstance.reaper.isAlive(); i++) {
            Thread.sleep(500L);  // allow reaper thread to die
        }
        assertEquals(true, theInstance.exitWhenFinished);
        assertEquals(false, theInstance.reaper.isAlive());
    }

    //-----------------------------------------------------------------------
    private void waitUntilTrackCount() {
        while (theInstance.getTrackCount() != 0) {
            int total = 0;
            while (theInstance.getTrackCount() != 0) {
                byte[] b = new byte[1024 * 1024];
                b[0] = (byte) System.currentTimeMillis();
                total = total + b[0];
                System.gc();
            }
        }
    }
}
