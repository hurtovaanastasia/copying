import java.io.*;
import java.util.concurrent.*;

public class Main {

    // Метод для копирования одного файла (без NIO)
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, bytesRead, bytesRead);
            }
        }
    }

    // Последовательное копирование двух файлов
    public static void copyFilesSeq(File source1, File target1, File source2, File target2) throws IOException {
        copyFile(source1, target1);
        copyFile(source2, target2);
    }

    // Параллельное копирование двух файлов
    public static void copyFilesParallel(File source1, File target1, File source2, File target2)
            throws InterruptedException, ExecutionException {

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Callable<Void> copy1 = () -> {
            copyFile(source1, target1);
            return null;
        };

        Callable<Void> copy2 = () -> {
            copyFile(source2, target2);
            return null;
        };

        Future<Void> res1 = threadPool.submit(copy1);
        Future<Void> res2 = threadPool.submit(copy2);

        res1.get();
        res2.get();
        threadPool.shutdown();
    }

    public static void main(String[] args) {
        File in1 = new File("input1.txt");
        File out1 = new File("output1.txt");
        File in2 = new File("input2.txt");
        File out2 = new File("output2.txt");

        try {
            long startTime = System.nanoTime();
            copyFilesSeq(in1, out1, in2, out2);
            long endTime = System.nanoTime();
            System.out.println("Последовательное копирование прошло за: " + ((endTime - startTime) / 1_000_000) + " мс.");

            File parallelIn1 = new File("input1.txt");
            File parallelOut1 = new File("output1_par.txt");
            File parallelIn2 = new File("input2.txt");
            File parallelOut2 = new File("output2_par.txt");

            startTime = System.nanoTime();
            copyFilesParallel(parallelIn1, parallelOut1, parallelIn2, parallelOut2);
            endTime = System.nanoTime();
            System.out.println("Параллельное копирование прошло за: " + ((endTime - startTime) / 1_000_000) + " мс.");

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
