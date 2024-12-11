package org.mule.extension.vectors.internal.helper;

import org.mule.extension.vectors.api.metadata.CompositeResponseAttributes;
import org.mule.extension.vectors.api.metadata.DocumentResponseAttributes;
import org.mule.extension.vectors.api.metadata.EmbeddingResponseAttributes;
import org.mule.extension.vectors.api.metadata.StoreResponseAttributes;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.api.streaming.CursorProvider;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.streaming.StreamingHelper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.toInputStream;

public final class ResponseHelper {

  private ResponseHelper() {
  }

  public static Result<InputStream, CompositeResponseAttributes> createCompositeResponse(
      String response,
      Map<String, Object> compositeAttributes) {

    return Result.<InputStream, CompositeResponseAttributes>builder()
        .attributes(new CompositeResponseAttributes((HashMap<String, Object>) compositeAttributes))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, StoreResponseAttributes> createStoreResponse(
      String response,
      Map<String, Object> storeAttributes) {

    return Result.<InputStream, StoreResponseAttributes>builder()
        .attributes(new StoreResponseAttributes((HashMap<String, Object>) storeAttributes))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, EmbeddingResponseAttributes> createEmbeddingResponse(
      String response,
      Map<String, Object> embeddingAttributes) {

    return Result.<InputStream, EmbeddingResponseAttributes>builder()
        .attributes(new EmbeddingResponseAttributes((HashMap<String, Object>) embeddingAttributes))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, DocumentResponseAttributes> createDocumentResponse(
      String response,
      Map<String, Object> documentAttributes) {

    return Result.<InputStream, DocumentResponseAttributes>builder()
        .attributes(new DocumentResponseAttributes((HashMap<String, Object>) documentAttributes))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static List<Result<CursorProvider, DocumentResponseAttributes>> createPageDocumentResponse(
      String response,
      Map<String, Object> documentAttributes,
      StreamingHelper streamingHelper) {

    List<Result<CursorProvider, DocumentResponseAttributes>> page =  new LinkedList();

    page.add(Result.<CursorProvider, DocumentResponseAttributes>builder()
        .attributes(new DocumentResponseAttributes((HashMap<String, Object>) documentAttributes))
        .output((CursorProvider) streamingHelper.resolveCursorProvider(toInputStream(response, StandardCharsets.UTF_8)))
        .mediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON)
        .attributesMediaType(org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA)
        .build());

    return page;
  }
}
