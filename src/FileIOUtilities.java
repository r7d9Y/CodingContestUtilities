import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileIOUtilities {
    public static void fileSplit(Path file, long maxBytes) throws IOException {
        InputStream in = Files.newInputStream(file);
        int splitSize = (int) (Files.size(file) / maxBytes) + 1;
        if (splitSize > 99999999) {
            throw new IllegalArgumentException("too many files");
        }
        for (int i = 0; i < splitSize; i++) {
            OutputStream out = Files.newOutputStream(Path.of(String.format("%s.%08d", file, i+1)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            int toRead = Math.min(in.available  (), (int) maxBytes);
            int size = Math.min(Math.min(2048, (int) maxBytes), in.available());
            byte[] buffer = new byte[size];
            int available = toRead;
            while (available > 0) {
                int change = in.read(buffer, 0, Math.min(size,available));
                out.write(buffer, 0, Math.min(size,available));
                available-=change;
            }
            out.close();
        }
        in.close();
    }

    public static void fileUnsplit(Path file) throws IOException {
        if (!Files.exists(Path.of(file.toString() + ".00000001"))) {
            throw new IllegalArgumentException("no parts found");
        }
        OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Path p;
        for (int i = 0; Files.exists((p = Path.of(String.format("%s.%08d", file, i+1)))); i++) {
            InputStream in = Files.newInputStream(p);
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            Files.delete(p);
        }
        out.close();
    }

    public static String[][] readInCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }

        } catch (IOException e) {
            throw new IOException("Error while trying to read in file [.csv]");
        }
        return data.toArray(new String[0][]);
    }





}
