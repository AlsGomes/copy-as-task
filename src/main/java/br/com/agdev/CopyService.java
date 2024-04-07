package br.com.agdev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class CopyService {

    public static Map<String, Integer> counter = new ConcurrentHashMap<>();

    private final List<String> ignoredFolders = new ArrayList<>(Arrays.asList(
            "/run/user/1000/gvfs/mtp:host=SAMSUNG_SAMSUNG_Android_RQCR5004KZL/Armazenamento interno/Android"));

    public void copy(String sourceRoot, String destRoot) {
        Instant begin = Instant.now();
        System.out.println(String.format("O processo de cópia dos arquivos será iniciado"));

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            call(sourceRoot, destRoot, executor);
        } catch (Exception e) {
            System.err.println(String.format("Erro ao realizar cópia. Causa: %s", e.getMessage()));
        }

        Instant end = Instant.now();
        long elapsedTime = ChronoUnit.SECONDS.between(begin, end);
        System.out.println(String.format("O processo de cópia dos arquivos levou %d segundos para ser realizdo", elapsedTime));
    }

    private void call(String sourceRoot, String destRoot, ExecutorService executor) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(sourceRoot))) {
            List<Path> filesList = files.toList();

            if (ignoredFolders.contains(sourceRoot)) {
                System.out.println(String.format("Conteúdos da pasta %s ignorados", sourceRoot));
                return;
            }

            int size = filesList.size();
            System.out.println(String.format("%d arquivos encontrados em %s", size, sourceRoot));
            counter.put(sourceRoot, size);

            for (Path path : filesList) {
                if (Files.isDirectory(path)) {
                    call(path.toString(), Paths.get(destRoot).resolve(path.getFileName()).toString(), executor);
                } else {
                    System.out.println(String.format("Disparando task de cópia do arquivo %s", path.toString()));
                    executor.submit(new CopyTask(path.toString(), Paths.get(destRoot).resolve(path.getFileName()).toString(), sourceRoot));
                }
            }
        }
    }
}
