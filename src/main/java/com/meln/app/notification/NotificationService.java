package com.meln.app.notification;

import com.meln.app.notification.discord.DiscordService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NotificationService {

  private final DiscordService discordService;

  //todo: in future can support multiple notification providers
  @Transactional(Transactional.TxType.NOT_SUPPORTED)
  public void sendMessage(String text) {
    discordService.sendMessage(text);
  }

}
