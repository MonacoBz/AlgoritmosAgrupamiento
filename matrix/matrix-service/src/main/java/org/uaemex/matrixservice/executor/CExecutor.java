package org.uaemex.matrixservice.executor;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CExecutor {

    public String execute(String wslBinaryPath, List<String> args, String stdinInput) {
        List<String> command = new ArrayList<>();
        command.add("wsl");
        command.add(wslBinaryPath);
        if (args != null) {
            command.addAll(args);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            Process process = processBuilder.start();

            // 1. Escribir la matriz en stdin y CERRAR el stream (el try-with-resources lo cierra solo)
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write(stdinInput);
                writer.flush();
            }

            // 2. Capturar la salida JSON (stdout) y posibles errores (stderr)
            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());

            // 3. Esperar a que termine con un timeout para evitar procesos colgados
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Timeout al ejecutar el binario: " + wslBinaryPath);
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("Error en binario C (código " + exitCode + "): " + stderr);
            }

            return stdout;

        } catch (IOException e) {
            throw new RuntimeException("Error de E/S al invocar WSL: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ejecución interrumpida para: " + wslBinaryPath, e);
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString().trim();
        }
    }
}
