package com.devonfw.tools.ide.url;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.ide.url.model.report.UrlFinalReport;
import com.devonfw.tools.ide.url.updater.UpdateManager;

/**
 * Main program to run the updater of ide-urls and ide-urls-status repositories.
 */
public class UpdateInitiator {

  private static final Logger logger = LoggerFactory.getLogger(UpdateInitiator.class.getName());

  /**
   * @param args args[0] = path to ide-urls repository args[1] = path to ide-urls-status repository args[2] = timeout in Java Duration format (e.g. PT5H30M)
   *     args[3] = optional selected tool or updater classname
   */
  public static void main(String[] args) {

    if (args.length < 2) {
      logger.error("Error: Missing path to ide-urls and/or ide-urls-status repository.");
      logger.error("Usage: java UpdateInitiator <path_to_ide_urls> <path_to_ide_urls_status> <duration_string_format> <tool_to_test|updater_class_name>");
      System.exit(1);
    }

    String pathToUrlsRepo = args[0];
    String pathToStatusRepo = args[1];
    Instant expirationTime = null;
    String selectedTool = null;

    if (args.length < 3) {
      logger.warn("Timeout was not set, setting timeout to infinite instead.");
    } else {
      try {
        Duration duration = Duration.parse(args[2]);
        expirationTime = Instant.now().plus(duration);
        logger.info("Timeout was set to: {}.", expirationTime);
      } catch (DateTimeParseException e) {
        logger.error("Error: Provided timeout format is not valid.", e);
        System.exit(1);
      }
      if (args.length > 3) {
        selectedTool = args[3];
      }
    }

    Path urlsRepoPath = Path.of(pathToUrlsRepo);
    Path statusRepoPath = Path.of(pathToStatusRepo);

    if (!urlsRepoPath.toFile().isDirectory()) {
      logger.error("Error: Provided ide-urls path is not a valid directory.");
      System.exit(1);
    }
    if (!statusRepoPath.toFile().isDirectory()) {
      logger.error("Error: Provided ide-urls-status path is not a valid directory.");
      System.exit(1);
    }

    UrlFinalReport urlFinalReport = new UrlFinalReport();

    UpdateManager updateManager = new UpdateManager(urlsRepoPath, statusRepoPath, urlFinalReport, expirationTime);
    if (selectedTool == null) {
      updateManager.updateAll();
    } else {
      updateManager.update(selectedTool);
    }

    logger.info(urlFinalReport.toString());
  }
}
