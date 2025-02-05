package edu.illinois.phantom.parseengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.illinois.phantom.model.Resume;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.store.Directory;

@Slf4j
public class Main {
  public static void main(String[] args) throws IOException {

    if (args.length == 0 || "help".equalsIgnoreCase(args[0]) || args.length != 2) {
      System.out.println(
        "Usage: java edu.illinois.phantom.parseengine.ParseEngine <Location of Resumes doc/pdf> <Location to write " +
          "JSON>");
      return;
    }

    File directoryPath = new File(args[0]);
    File outputPath = new File(args[1]);
    if (!directoryPath.exists() || !directoryPath.isDirectory()) {
      log.error("{} either does not exist or is not a directory!", directoryPath);
      return;
    }
    if (!outputPath.exists()) {
      if (!outputPath.mkdirs()) {
        log.error("Could not create output directory at {}", outputPath);
        return;
      } else {
        log.info("Output directory at {} did not exist. Created it now...", outputPath);
      }
    }
    File[] filesList = directoryPath.listFiles();
    if (null == filesList || filesList.length == 0) {
      log.error("No files found to parse, at {}", directoryPath);
      return;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    log.info("Parsing resumes...");
    Arrays.stream(filesList)
      // filtering by extension, for example, to parse only PDF resume
      // .filter(file -> file.getName().endsWith(".pdf"))
      .map(ResumeParser.INSTANCE::parseResumeFile)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(resume -> {
        if (CollectionUtils.isEmpty(resume.getSkills())) {
          log.warn("No JSON file generated as no skills were found, in {}", resume.getLocation());
          return false;
        }
        return true;
      })
      .forEach(resume -> {
        File inputFile = new File(resume.getLocation());
        File outputFile = Paths.get(outputPath.getAbsolutePath(), inputFile.getName() + ".json").toFile();
        try {
          if (!outputFile.createNewFile()) {
            throw new DocParseException("Could not create file at " + outputFile.toString());
          }
          objectMapper.writeValue(outputFile, resume);
          log.info("Generated JSON file for {} at {}", inputFile.getName(), outputFile);
        } catch (Exception e) {
          log.error("Exception while writing JSON to {}: {}", outputFile, e.getMessage(), e);
        }
      });
    log.info("Finished parsing resumes!");
  }
}
