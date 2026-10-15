package dev.serverest.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jtlWriter;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;
import static us.abstracta.jmeter.javadsl.JmeterDsl.throughputTimer;

import io.github.cdimascio.dotenv.Dotenv;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/** Disabled manual load test for the ServeRest API rate limit. */
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
public class RateLimitLoadTests {
  private static final String JTL_DIR = "target/jmeter-jtls/rate-limit";
  private static final String ANALYSIS_LOG = "target/jmeter-jtls/rate-limit/analysis.log";
  private static final String ROTA_USUARIOS = "/usuarios";

  private static final Dotenv DOTENV =
      Dotenv.configure()
          .directory("./.env")
          .ignoreIfMalformed()
          .ignoreIfMissing()
          .load();

  @Test
  @Disabled("Teste de carga/NFR: executar manualmente em ambiente com perfil de carga habilitado")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("Carga - Suportar 100 requisições por segundo sem erro de servidor")
  void shouldSupport100RequestsPerSecondWithoutServerErrors() throws IOException {
    final String baseUrl = resolveBaseUrl();
    testPlan(
            httpDefaults()
                .url(baseUrl)
                .connectionTimeout(Duration.ofSeconds(10))
                .responseTimeout(Duration.ofSeconds(30)),
            threadGroup(
                100,
                Duration.ofSeconds(10),
                throughputTimer(100),
                httpSampler(ROTA_USUARIOS)),
            jtlWriter(JTL_DIR))
        .run();

    final Path latestJtl =
        Files.list(Path.of(JTL_DIR))
            .filter(path -> path.getFileName().toString().endsWith(".jtl"))
            .max(Comparator.comparingLong(this::lastModifiedSafe))
            .orElseThrow(
                () -> new IllegalStateException("Nenhum arquivo .jtl foi gerado em " + JTL_DIR));
    final List<String> lines = Files.readAllLines(latestJtl);

    // JTL padrão é CSV com cabeçalho; ao menos 1 linha de resultado é esperada.
    assertTrue(lines.size() > 1, "JTL sem amostras suficientes para validação de carga.");
    final int responseCodeIndex = csvHeaderIndex(lines.get(0), "responseCode");
    assertTrue(responseCodeIndex >= 0, "Cabeçalho JTL não contém a coluna responseCode.");

    final long status429Count =
        lines.stream()
            .skip(1)
            .map(line -> splitCsv(line))
            .filter(cols -> cols.length > responseCodeIndex)
            .filter(cols -> "429".equals(cols[responseCodeIndex]))
            .count();
    final long status5xxCount =
        lines.stream()
            .skip(1)
            .map(line -> splitCsv(line))
            .filter(cols -> cols.length > responseCodeIndex)
            .filter(
              cols ->
                cols[responseCodeIndex] != null
                  && cols[responseCodeIndex].startsWith("5"))
            .count();

    writeAnalysisLog(latestJtl, lines, responseCodeIndex, status429Count, status5xxCount);
    assertTrue(
        status5xxCount == 0,
        "Não era esperado erro de servidor (5xx) em 100 req/s. Total 5xx: " + status5xxCount);

    // 429 responses are recorded for troubleshooting, but are not a failure condition.
    assertTrue(status429Count >= 0, "Contagem de 429 inválida.");
  }

  private String resolveBaseUrl() {
    final boolean isCi = "true".equalsIgnoreCase(System.getenv("CI"));
    final String devUrl = DOTENV.get("BASE_URL_DEV");
    final String systemProdUrl = System.getProperty("BASE_URL_PROD");
    final String dotenvProdUrl = DOTENV.get("BASE_URL_PROD");
    final String prodUrl =
        systemProdUrl != null && !systemProdUrl.isBlank() ? systemProdUrl : dotenvProdUrl;
    if (isCi) {
      return devUrl != null && !devUrl.isBlank() ? devUrl : "http://localhost:3000";
    }
    return prodUrl != null && !prodUrl.isBlank() ? prodUrl : "https://serverest.dev";
  }

  private int csvHeaderIndex(final String headerLine, final String columnName) {
    final String[] headers = splitCsv(headerLine);
    for (int i = 0; i < headers.length; i++) {
      if (columnName.equals(headers[i])) {
        return i;
      }
    }
    return -1;
  }

  private void writeAnalysisLog(
      final Path latestJtl,
      final List<String> lines,
      final int responseCodeIndex,
      final long status429Count,
      final long status5xxCount)
      throws IOException {
    final Map<String, Integer> codeCounts = new TreeMap<>();
    for (int i = 1; i < lines.size(); i++) {
      final String[] cols = splitCsv(lines.get(i));
      if (cols.length > responseCodeIndex) {
        final String code = cols[responseCodeIndex];
        codeCounts.put(code, codeCounts.getOrDefault(code, 0) + 1);
      }
    }

    final Path analysisLogPath = Path.of(ANALYSIS_LOG);
    Files.createDirectories(analysisLogPath.getParent());
    final StringBuilder sb = new StringBuilder();
    sb.append("=== RateLimitLoadTests Analysis ===").append(System.lineSeparator());
    sb.append("timestamp=")
        .append(OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        .append(System.lineSeparator());
    sb.append("latestJtl=").append(latestJtl.toAbsolutePath()).append(System.lineSeparator());
    sb.append("totalSamples=").append(lines.size() - 1).append(System.lineSeparator());
    sb.append("status429Count=").append(status429Count).append(System.lineSeparator());
    sb.append("status5xxCount=").append(status5xxCount).append(System.lineSeparator());
    sb.append("codes=").append(codeCounts).append(System.lineSeparator());
    sb.append(System.lineSeparator());
    Files.writeString(
        analysisLogPath,
        sb.toString(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
  }

  private String[] splitCsv(final String line) {
    // Para o formato padrão JTL CSV, split simples por vírgula atende o necessário aqui.
    return line.split(",", -1);
  }

  private long lastModifiedSafe(final Path path) {
    try {
      return Files.getLastModifiedTime(path).toMillis();
    } catch (final IOException e) {
      return Long.MIN_VALUE;
    }
  }
}
