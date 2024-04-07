package br.com.agdev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CopyTask implements Runnable {

    private final String origin;
    private final String dest;
    private final String root;

    public CopyTask(String origin, String dest, String root) {
        this.origin = origin;
        this.dest = dest;
        this.root = root;
    }

    @Override
    public void run() {
        try {
            Path source = Path.of(origin);

            Path target = Path.of(dest);
            Path targetParent = target.getParent();
            Files.createDirectories(targetParent);

            if (Files.exists(target)) {
                System.out.println(String.format("Arquivo %s já existe", origin));

                if (Files.size(target) == Files.size(source)) {
                    System.out.println(String.format("Arquivo %s ignorado pois já está copiado completamente", origin));
                    CopyService.counter.put(root, CopyService.counter.get(root) - 1);
                    System.out.println(String.format("%d arquivos restantes na pasta %s", CopyService.counter.get(root), root));
                    return;
                } else {
                    System.out.println(String.format("Arquivo %s será substituído para evitar problemas de corrompimento", origin));
                }
            }

            Instant begin = Instant.now();

            System.out.println(String.format("Iniciando cópia do arquivo %s", origin));
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            Instant end = Instant.now();
            long elapsedTime = ChronoUnit.SECONDS.between(begin, end);

            CopyService.counter.put(root, CopyService.counter.get(root) - 1);
            System.out.println(String.format("%d arquivos restantes na pasta %s", CopyService.counter.get(root), root));

            System.out.println(String.format("Arquivo %s copiado com sucesso. Levou %d segundos para ser copiado", origin, elapsedTime));
        } catch (IOException e) {
            System.err.println(String.format("Arquivo %s nâo pôde ser copiado. Causa: %s", origin, e.getMessage()));
        }
    }
}
