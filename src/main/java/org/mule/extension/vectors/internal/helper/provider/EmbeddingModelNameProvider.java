package org.mule.extension.vectors.internal.helper.provider;

import java.util.Collections;
import java.util.Set;

import org.mule.extension.vectors.internal.connection.model.BaseModelConnection;
import org.mule.extension.vectors.internal.constant.Constants;
import org.mule.extension.vectors.internal.helper.model.EmbeddingModelHelper;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

public class EmbeddingModelNameProvider implements ValueProvider {

  @Connection
  private BaseModelConnection modelConnection;

  private static final Set<Value> VALUES_FOR_AZURE_OPENAI = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_3_SMALL.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_3_LARGE.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_ADA_002.getModelName()
  );

  private static final Set<Value> VALUES_FOR_OPENAI = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_3_SMALL.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_3_LARGE.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.TEXT_EMBEDDING_ADA_002.getModelName()
  );

  private static final Set<Value> VALUES_FOR_MISTRAL_AI = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.MISTRAL_EMBED.getModelName()
  );

  private static final Set<Value> VALUES_FOR_NOMIC = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.NOMIC_EMBED_TEXT.getModelName()
  );

  private static final Set<Value> VALUES_FOR_HUGGING_FACE = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.FALCON_7B_INSTRUCT.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.MINI_LM_L6_V2.getModelName()
  );

  private static final Set<Value> VALUES_FOR_EINSTEIN = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.SFDC_TEXT_EMBEDDING_ADA_002.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.SFDC_AZURE_TEXT_EMBEDDING_ADA_002.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.SFDC_OPENAI_TEXT_EMBEDDING_ADA_002.getModelName()
  );

  private static final Set<Value> VALUES_FOR_VERTEX_AI = ValueBuilder.getValuesFor(
      EmbeddingModelHelper.TextEmbeddingModelNames.VERTEX_TEXT_EMBEDDING_GECKO_003.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.VERTEX_TEXT_EMBEDDING_004.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.VERTEX_TEXT_EMBEDDING_GECKO_MULTILINGUAL_001.getModelName(),
      EmbeddingModelHelper.TextEmbeddingModelNames.VERTEX_TEXT_MULTILINGUAL_EMBEDDING_002.getModelName(),
      EmbeddingModelHelper.MultimodalEmbeddingModelNames.VERTEX_MULTI_MODAL_EMBEDDING.getModelName()
  );

  @Override
  public Set<Value> resolve() throws ValueResolvingException {

    String embeddingModelService = modelConnection.getEmbeddingModelService();
    switch (embeddingModelService) {
      case Constants.EMBEDDING_MODEL_SERVICE_OPENAI:
        return VALUES_FOR_OPENAI;
      case Constants.EMBEDDING_MODEL_SERVICE_AZURE_OPENAI:
        return VALUES_FOR_AZURE_OPENAI;
      case Constants.EMBEDDING_MODEL_SERVICE_MISTRAL_AI:
        return VALUES_FOR_MISTRAL_AI;
      case Constants.EMBEDDING_MODEL_SERVICE_NOMIC:
        return VALUES_FOR_NOMIC;
      case Constants.EMBEDDING_MODEL_SERVICE_HUGGING_FACE:
        return VALUES_FOR_HUGGING_FACE;
      case Constants.EMBEDDING_MODEL_SERVICE_EINSTEIN:
        return VALUES_FOR_EINSTEIN;
      case Constants.EMBEDDING_MODEL_SERVICE_VERTEX_AI:
        return VALUES_FOR_VERTEX_AI;
      default:
        return Collections.emptySet();
    }
  }

}
