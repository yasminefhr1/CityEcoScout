package com.ensa.CityScout.service;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;

@Service
public class DialogflowService {
    private final SessionsClient sessionsClient;
    private final String projectId;

    public DialogflowService(@Value("${dialogflow.project-id}") String projectId,
                           @Value("${dialogflow.credentials-path}") String credentialsPath) throws IOException {
        this.projectId = projectId;
        
        InputStream credentialsStream = new ClassPathResource(credentialsPath).getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/dialogflow"));
        
        SessionsSettings settings = SessionsSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();
            
        this.sessionsClient = SessionsClient.create(settings);
    }

    public String getDialogflowResponse(String message) {
        try {
            SessionName session = SessionName.of(projectId, UUID.randomUUID().toString());
            TextInput textInput = TextInput.newBuilder()
                .setText(message)
                .setLanguageCode("en-US")
                .build();
            QueryInput queryInput = QueryInput.newBuilder()
                .setText(textInput)
                .build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            return response.getQueryResult().getFulfillmentText();
        } catch (Exception e) {
            throw new RuntimeException("Erreur Dialogflow: " + e.getMessage());
        }
    }
}