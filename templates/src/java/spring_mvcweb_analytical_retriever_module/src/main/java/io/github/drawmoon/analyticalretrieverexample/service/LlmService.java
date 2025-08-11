package io.github.drawmoon.analyticalretrieverexample.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drawmoon.analyticalretrieverexample.proto.GenerateResult;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LlmService {

  @Value("${app.external-service.llm.provider}")
  private String llmProvider;

  @Value("${app.external-service.llm.api.generate}")
  private String llmGenerateApiPath;

  @Autowired private OkHttpClient okHttpClient;

  @Autowired private ObjectMapper objectMapper;

  public GenerateResult callLlmGenerate(
      String query, String domain, String v, Integer minK, Integer maxK, Boolean noCache) {
    String apiUrl = llmProvider + llmGenerateApiPath;
    HttpUrl httpUrl =
        HttpUrl.get(apiUrl)
            .newBuilder()
            .addQueryParameter("q", query)
            .addQueryParameter("domain", Optional.ofNullable(domain).orElse("default"))
            .addQueryParameter("v", Optional.ofNullable(v).orElse("latest"))
            .addQueryParameter("min_k", Optional.ofNullable(minK).orElse(0).toString())
            .addQueryParameter("max_k", Optional.ofNullable(maxK).orElse(0).toString())
            .addQueryParameter("no_cache", Boolean.TRUE.equals(noCache) ? "true" : "false")
            .build();

    Request request = new Request.Builder().url(httpUrl).get().build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      ResponseBody body = response.body();
      if (body != null) {
        return objectMapper.readValue(body.byteStream(), GenerateResult.class);
      }
      return null;
    } catch (IOException e) {
      log.error("Request LLM error: {}", e.getMessage());
      return null;
    }
  }
}
