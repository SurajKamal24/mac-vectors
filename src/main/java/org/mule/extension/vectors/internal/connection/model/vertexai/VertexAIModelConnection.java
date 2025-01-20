package org.mule.extension.vectors.internal.connection.model.vertexai;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import org.mule.extension.vectors.internal.connection.model.BaseModelConnection;
import org.mule.extension.vectors.internal.constant.Constants;
import org.mule.runtime.api.connection.ConnectionException;

import java.io.ByteArrayInputStream;

public class VertexAIModelConnection implements BaseModelConnection {

  private String projectId;
  private String clientEmail;
  private String clientId;
  private String privateKeyId;
  private String privateKey;
  private PredictionServiceClient predictionClient;

  public VertexAIModelConnection(String projectId, String clientEmail, String clientId,
                                 String privateKeyId, String privateKey) {
    this.projectId = projectId;
    this.clientEmail = clientEmail;
    this.clientId = clientId;
    this.privateKeyId = privateKeyId;
    this.privateKey = privateKey;
  }

  public String getProjectId() {
    return projectId;
  }

  public String getClientEmail() {
    return clientEmail;
  }

  public String getClientId() {
    return clientId;
  }

  public String getPrivateKeyId() {
    return privateKeyId;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public PredictionServiceClient getPredictionClient() {
    return predictionClient;
  }

  private String buildJsonCredentials() {
    return new StringBuilder()
        .append("{")
        .append("\"type\": \"service_account\",")
        .append("\"project_id\": \"").append(this.projectId).append("\",")
        .append("\"private_key_id\": \"").append(this.privateKeyId).append("\",")
        .append("\"private_key\": \"").append(this.privateKey).append("\",")
        .append("\"client_email\": \"").append(this.clientEmail).append("\",")
        .append("\"client_id\": \"").append(this.clientId).append("\",")
        .append("\"auth_uri\": \"").append(Constants.GCP_AUTH_URI).append("\",")
        .append("\"token_uri\": \"").append(Constants.GCP_TOKEN_URI).append("\",")
        .append("\"auth_provider_x509_cert_url\": \"").append(Constants.GCP_AUTH_PROVIDER_X509_CERT_URL).append("\",")
        .append("\"client_x509_cert_url\": \"").append(Constants.GCP_CLIENT_X509_CERT_URL).append(this.clientEmail).append("\",")
        .append("\"universe_domain\": \"googleapis.com\"")
        .append("}")
        .toString();
  }

  @Override
  public void connect() throws ConnectionException {
    try {
      ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
          new ByteArrayInputStream(buildJsonCredentials().getBytes())
      );

      PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
          .setCredentialsProvider(() -> credentials)
          .build();

      this.predictionClient = PredictionServiceClient.create(settings);
    } catch (Exception e) {
      throw new ConnectionException("Failed to connect to Vertex AI.", e);
    }
  }

  @Override
  public void disconnect() {
    if (this.predictionClient != null) {
      this.predictionClient.close();
    }
  }

  @Override
  public boolean isValid() {
    try {

      return !predictionClient.isShutdown();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String getEmbeddingModelService() {
    return Constants.EMBEDDING_MODEL_SERVICE_VERTEX_AI;
  }
}
