package com.meln.app.notification.discord;

import com.meln.app.notification.NotificationMessage;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class DiscordService {

  @RestClient
  DiscordWebhookClient webhookClient;

  public void sendMessage(String text) {
    NotificationMessage msg = new NotificationMessage(text);
    webhookClient.sendMessage(msg)
        .subscribe()
        .with(unused -> {},
            failure -> log.error("Failed to send message", failure)
        );
  }
}
