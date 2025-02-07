package org.mule.extension.vectors.internal.connection.model.azureaivision;

import org.mule.extension.vectors.internal.connection.model.BaseModelConnection;
import org.mule.extension.vectors.internal.constant.Constants;
import org.mule.extension.vectors.internal.model.multimodal.azureaivision.AzureAIVisionClient;
import org.mule.extension.vectors.internal.model.multimodal.azureaivision.AzureAIVisionImageEmbeddingRequestBody;
import org.mule.extension.vectors.internal.model.multimodal.azureaivision.AzureAIVisionTextEmbeddingRequestBody;
import org.mule.runtime.api.connection.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class AzureAIVisionModelConnection implements BaseModelConnection {

  private static final Logger LOGGER = LoggerFactory.getLogger(AzureAIVisionModelConnection.class);

  private String endpoint;
  private String apiKey;
  private String apiVersion;
  private long timeout;
  private AzureAIVisionClient azureAIVisionClient;

  public AzureAIVisionModelConnection(String endpoint, String apiKey, String apiVersion, long timeout) {
    this.endpoint = endpoint;
    this.apiKey = apiKey;
    this.apiVersion = apiVersion;
    this.timeout = timeout;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getApiVersion() { return apiVersion; }

  @Override
  public String getEmbeddingModelService() {
    return Constants.EMBEDDING_MODEL_SERVICE_AZURE_AI_VISION;
  }

  @Override
  public void connect() throws ConnectionException {

    this.azureAIVisionClient = AzureAIVisionClient.builder()
      .endpoint(this.endpoint)
      .apiKey(this.apiKey)
      .apiVersion(this.apiVersion)
      .timeout(Duration.ofMillis(this.timeout))
      .build();

    LOGGER.debug("Connected to Azure Open AI.");
  }

  @Override
  public void disconnect() {

    if(this.azureAIVisionClient != null) {

      this.azureAIVisionClient.close();
      LOGGER.debug("Disconnecting from Azure AI Vision.");
    }
  }

  @Override
  public boolean isValid() {

    azureAIVisionClient.getModels();
    LOGGER.debug("Azure AI Vision connection is valid.");
    return true;
  }

  public List<float[]> embedText(String text, String modelName) {

    return this.azureAIVisionClient.embedText(new AzureAIVisionTextEmbeddingRequestBody(text), modelName).getVectors();
  }

  public List<float[]> embedImage(byte[] imageBytes, String modelName) {

    return this.azureAIVisionClient.embedImage(new AzureAIVisionImageEmbeddingRequestBody(imageBytes), modelName).getVectors();
  }
}
