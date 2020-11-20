import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * FileSystemInterface Interface holding a default implementation for write
 * to file operations within the game.
 */
public interface FileSystemInterface {
    void writeToFile(String relativePath, String content);

    class DefaultImplementation implements FileSystemInterface {
        /**
         * the path all files should be written into
         */
        final String root;

        DefaultImplementation(String root) {
            this.root = root;
        }

        public void writeToFile(String relativePath, String content) {
            try {
                File f = new File(this.root + relativePath);
                f.getParentFile().mkdirs();
                f.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
