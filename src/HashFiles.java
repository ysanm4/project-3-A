import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class HashFiles {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        generateFiles();

        int numHashes = 100000;
        double shortFileHashRate = hashFile("short_file.txt", numHashes);
        double largeFileHashRate = hashFile("large_file.txt", numHashes);

        System.out.println("Short file hash rate: " + shortFileHashRate + " hashes per second");
        System.out.println("Large file hash rate: " + largeFileHashRate + " hashes per second");


        double timeToCollisionShort = calculateCollisionTime(shortFileHashRate);
        double timeToCollisionLarge = calculateCollisionTime(largeFileHashRate);

        System.out.println("Time to find a collision in short file: " + timeToCollisionShort + " seconds");
        System.out.println("Time to find a collision in large file: " + timeToCollisionLarge + " seconds");
    }

    private static void generateFiles() throws IOException {

        try (FileOutputStream fos = new FileOutputStream("short_file.txt")) {
            fos.write("This is 1 Kbyte of data.".getBytes());
        }


        try (FileOutputStream fos = new FileOutputStream("large_file.txt")) {
            byte[] data = new byte[1024];
            for (int i = 0; i < data.length; i++) {
                data[i] = 'Y';
            }
            fos.write(data);
        }
    }

    private static double hashFile(String filePath, int numHashes) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = Files.readAllBytes(Paths.get(filePath));

        long startTime = System.nanoTime();
        for (int i = 0; i < numHashes; i++) {
            digest.update(data);
            digest.digest();
        }
        long endTime = System.nanoTime();

        double elapsedTime = (endTime - startTime) / 1_000_000_000.0;
        return numHashes / elapsedTime;
    }

    private static double calculateCollisionTime(double hashRate) {
        double nBits = 256;
        return Math.pow(2, nBits / 2) / hashRate;
    }
}
