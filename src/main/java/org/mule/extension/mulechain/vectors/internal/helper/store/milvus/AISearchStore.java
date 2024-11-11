package org.mule.extension.mulechain.vectors.internal.helper.store.milvus;

import opennlp.tools.parser.Cons;
import org.checkerframework.checker.i18n.qual.LocalizableKey;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.extension.mulechain.vectors.internal.config.Configuration;
import org.mule.extension.mulechain.vectors.internal.constant.Constants;
import org.mule.extension.mulechain.vectors.internal.helper.parameter.EmbeddingModelNameParameters;
import org.mule.extension.mulechain.vectors.internal.helper.parameter.QueryParameters;
import org.mule.extension.mulechain.vectors.internal.helper.store.VectorStore;
import org.mule.extension.mulechain.vectors.internal.util.JsonUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static org.mule.extension.mulechain.vectors.internal.util.JsonUtils.readConfigFile;

public class AISearchStore extends VectorStore {

  private static final String API_VERSION = "2024-07-01";

  private String apiKey;
  private String url;

  public AISearchStore(String storeName, Configuration configuration, QueryParameters queryParams, EmbeddingModelNameParameters modelParams) {

    super(storeName, configuration, queryParams, modelParams);

    JSONObject config = readConfigFile(configuration.getConfigFilePath());
    JSONObject vectorStoreConfig = config.getJSONObject(Constants.VECTOR_STORE_AI_SEARCH);
    this.apiKey = vectorStoreConfig.getString("AI_SEARCH_KEY");
    this.url = vectorStoreConfig.getString("AI_SEARCH_URL");
  }

  public JSONObject listSources() {

    HashMap<String, JSONObject> sourcesJSONObjectHashMap = new HashMap<String, JSONObject>();

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("storeName", storeName);

    int segmentCount = 0; // Counter to track the number of documents processed
    int offset = 0; // Initialize offset for pagination

    try {

      boolean hasMore = true; // Flag to check if more pages are available

      // Loop to process pages until no more documents are available
      do {
        // Construct the URL with $top and $skip for pagination
        String urlString = this.url + "/indexes/" + storeName + "/docs?search=*&$top=" + queryParams.embeddingPageSize() +
            "&$skip=" + offset + "&$select=id,metadata&api-version=" + API_VERSION;

        // Nested loop to handle each page of results
        while (urlString != null) {

          URL url = new URL(urlString);

          // Open connection and configure HTTP request
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET");
          connection.setRequestProperty("Content-Type", "application/json");
          connection.setRequestProperty("api-key", apiKey);

          // Check the response code and handle accordingly
          if (connection.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            // Read response line by line
            while ((line = in.readLine()) != null) {
              responseBuilder.append(line);
            }
            in.close();

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
            JSONArray documents = jsonResponse.getJSONArray("value");

            // Iterate over each document in the current page
            for (int i = 0; i < documents.length(); i++) {

              JSONObject document = documents.getJSONObject(i);
              String id = document.getString("id"); // Document ID
              JSONObject metadata = document.optJSONObject("metadata"); // Metadata of the document

              if (metadata != null) {

                // Extract metadata attributes if available
                JSONArray attributes = metadata.optJSONArray("attributes");

                JSONObject metadataObject = new JSONObject(); // Object to store key-value pairs from attributes

                int index =0;
                // Iterate over attributes array to populate sourceObject
                for (int j = 0; j < attributes.length(); j++) {
                  JSONObject attribute = attributes.getJSONObject(j);
                  metadataObject.put(attribute.getString("key"), attribute.get("value"));
                  if(attribute.getString("key").compareTo(Constants.METADATA_KEY_INDEX) == 0) {
                    index = Integer.parseInt(metadataObject.getString("index"));
                  }
                }

                JSONObject sourceObject = getSourceObject(metadataObject);
                String sourceUniqueKey = getSourceUniqueKey(sourceObject);

                // Add sourceObject to sources only if it has at least one key-value pair and it's possible to generate a key
                if (!sourceObject.isEmpty() && sourceUniqueKey != null && !sourceUniqueKey.isEmpty()) {
                  // Overwrite sourceObject if current one has a greater index (greatest index represents the number of segments)
                  if(sourcesJSONObjectHashMap.containsKey(sourceUniqueKey)){
                    // Get current index
                    int currentSegmentCount = index + 1;
                    // Get previously stored index
                    int storedSegmentCount = (int) sourcesJSONObjectHashMap.get(sourceUniqueKey).get("segmentCount");
                    // Check if object need to be updated
                    if(currentSegmentCount > storedSegmentCount) {
                      sourcesJSONObjectHashMap.put(sourceUniqueKey, sourceObject);
                    }
                  } else {
                    sourcesJSONObjectHashMap.put(sourceUniqueKey, sourceObject);
                  }
                }

                LOGGER.debug("sourceObject: " + sourceObject);
                segmentCount++; // Increment document count
              } else {
                LOGGER.warn("No metadata available");
              }
            }

            // Check for the next page link in the response
            urlString = jsonResponse.optString("@odata.nextLink", null);

            // If there is no next page, check if fewer documents were returned than PAGE_SIZE
            if (urlString == null && documents.length() < queryParams.embeddingPageSize()) {
              hasMore = false; // No more documents to retrieve
            }

          } else {
            // Log any error responses from the server
            LOGGER.error("Error: " + connection.getResponseCode() + " " + connection.getResponseMessage());
            break;
          }
        }

        // Increment offset to fetch the next segment of documents
        offset += queryParams.embeddingPageSize();

      } while (hasMore); // Continue if more pages are available

      // Output total count of processed documents
      LOGGER.debug("segmentCount: " + segmentCount);

    } catch (Exception e) {

      // Handle any exceptions that occur during the process
      LOGGER.error("Error while listing sources", e);
    }

    jsonObject.put("sources", JsonUtils.jsonObjectCollectionToJsonArray(sourcesJSONObjectHashMap.values()));
    jsonObject.put("sourceCount", sourcesJSONObjectHashMap.size());

    return jsonObject;
  }
}
