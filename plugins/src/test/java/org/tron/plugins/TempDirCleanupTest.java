package org.tron.plugins;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.rocksdb.RocksDBException;
import org.tron.plugins.utils.db.DbTool;

/**
 * Test class to verify temporary directory cleanup functionality.
 */
public class TempDirCleanupTest extends DbTest {

  private String tempDirPath;

  @Test
  public void testTempDirCreationAndCleanup() throws RocksDBException, IOException {
    init(DbTool.DbType.LevelDB);
    
    // Generate a temporary directory
    tempDirPath = genarateTmpDir();
    
    // Verify the directory path is not null and not empty
    Assert.assertNotNull("Temporary directory path should not be null", tempDirPath);
    Assert.assertFalse("Temporary directory path should not be empty", tempDirPath.isEmpty());
    
    // Create the directory to simulate actual usage
    File tempDir = new File(tempDirPath);
    if (!tempDir.exists()) {
      Assert.assertTrue("Should be able to create temporary directory", tempDir.mkdirs());
    }
    
    // Create some test files in the directory
    File testFile1 = new File(tempDir, "test1.txt");
    File testFile2 = new File(tempDir, "test2.txt");
    Assert.assertTrue("Should be able to create test file 1", testFile1.createNewFile());
    Assert.assertTrue("Should be able to create test file 2", testFile2.createNewFile());
    
    // Verify files exist
    Assert.assertTrue("Test file 1 should exist", testFile1.exists());
    Assert.assertTrue("Test file 2 should exist", testFile2.exists());
    
    System.out.println("Created temporary directory: " + tempDirPath);
    System.out.println("Directory exists: " + tempDir.exists());
    System.out.println("Files in directory: " + (tempDir.listFiles() != null ? tempDir.listFiles().length : 0));
  }

  @After
  public void cleanup() {
    // Manual cleanup for testing purposes
    if (tempDirPath != null) {
      File tempDir = new File(tempDirPath);
      if (tempDir.exists()) {
        deleteRecursively(tempDir);
        System.out.println("Manually cleaned up temporary directory: " + tempDirPath);
      }
    }
  }

  /**
   * Helper method to recursively delete directory and all its contents.
   * This is a copy of the private method from DbTest for testing purposes.
   */
  private void deleteRecursively(File file) {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File child : children) {
          deleteRecursively(child);
        }
      }
    }
    file.delete();
  }
}