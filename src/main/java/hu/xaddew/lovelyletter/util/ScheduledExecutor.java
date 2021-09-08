package hu.xaddew.lovelyletter.util;

import hu.xaddew.lovelyletter.service.GameService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExecutor {

  private final GameService gameService;

  @Value("${close-game.after-hours}")
  private int gameCloseHours;

  @Value("${delete-game.after-months}")
  private int gameDeleteMonths;

  @Scheduled(cron = "${close-game.cron-expression}")
  public void closeOpenGamesInactiveThanAllowed() {
    LocalDateTime hours = LocalDateTime.now().minusHours(gameCloseHours);
    log.info("Scheduled task to close games inactive for {}", hours);
    gameService.closeOpenGamesInactiveFor(hours);
  }

  @Scheduled(cron = "${delete-game.cron-expression}")
  public void deleteClosedGamesOlderThanAllowed() {
    LocalDateTime months = LocalDateTime.now().minusMonths(gameDeleteMonths);
    log.info("Scheduled task to delete closed games older than {}", months);
    gameService.deleteClosedGamesOlderThan(months);
  }

}
