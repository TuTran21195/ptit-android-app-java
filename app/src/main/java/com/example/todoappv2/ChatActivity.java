package com.example.todoappv2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoappv2.adapter.ChatAdapter;
import com.example.todoappv2.model.ChatMessage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private RecyclerView chatRecyclerView;
    private TextInputEditText messageInput;
    private MaterialButton sendButton;
    private CircularProgressIndicator progressIndicator;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private Executor executor;
    private static final String API_KEY = "AIzaSyBjsbr6_UzxI978q9EZjcK4sjo5GuixuxU";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        progressIndicator = findViewById(R.id.progressIndicator);

        // Initialize messages list and adapter
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        executor = Executors.newSingleThreadExecutor();

        // Set up send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Add welcome message
        messages.add(new ChatMessage("assistant", "Hello! I'm your AI assistant. How can I help you today?"));
        chatAdapter.notifyItemInserted(0);
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) return;

        Log.d(TAG, "Sending message: " + message);

        // Disable input while processing
        setInputEnabled(false);

        // Add user message to the chat
        ChatMessage userMessage = new ChatMessage("user", message);
        messages.add(userMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        messageInput.setText("");

        // Make API call
        executor.execute(() -> {
            try {
                Log.d(TAG, "Making API call to: " + API_URL);
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Prepare request body
                JSONObject requestBody = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", message);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);
                requestBody.put("contents", contents);

                String requestBodyStr = requestBody.toString();
                Log.d(TAG, "Request body: " + requestBodyStr);

                // Send request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBodyStr.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // Read error response
                    StringBuilder errorResponse = new StringBuilder();
                    try (Scanner scanner = new Scanner(conn.getErrorStream(), "utf-8")) {
                        while (scanner.hasNextLine()) {
                            errorResponse.append(scanner.nextLine());
                        }
                    }
                    Log.e(TAG, "Error response: " + errorResponse.toString());
                    throw new RuntimeException("HTTP error code: " + responseCode);
                }

                // Read response
                StringBuilder response = new StringBuilder();
                try (Scanner scanner = new Scanner(conn.getInputStream(), "utf-8")) {
                    while (scanner.hasNextLine()) {
                        response.append(scanner.nextLine());
                    }
                }

                String responseStr = response.toString();
                Log.d(TAG, "Response: " + responseStr);

                // Parse response
                JSONObject jsonResponse = new JSONObject(responseStr);
                String responseText = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

                Log.d(TAG, "Parsed response text: " + responseText);

                // Add AI response to the chat
                ChatMessage aiMessage = new ChatMessage("assistant", responseText);
                messages.add(aiMessage);
                runOnUiThread(() -> {
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                    setInputEnabled(true);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error making API call", e);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, 
                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setInputEnabled(true);
                });
            }
        });
    }

    private void setInputEnabled(boolean enabled) {
        messageInput.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        progressIndicator.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
} 