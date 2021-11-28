package edu.illinois.phantom;

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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) throws IOException {

    if (args.length != 0 && "help".equalsIgnoreCase(args[0])) {
      System.out.println(
          "Usage: java edu.illinois.phantom.Main <Directory-where-docx-pdf-resumes-are-located>");
      return;
    }

    File directoryPath =
        args.length != 0
            ? new File(args[0])
            : new File(
                Objects.requireNonNull(Main.class.getClassLoader().getResource("Resumes"))
                    .getFile());
    if (!directoryPath.exists() || !directoryPath.isDirectory()) {
      log.error("{} either does not exist or is not a directory!", directoryPath);
      return;
    }
    File[] filesList = directoryPath.listFiles();
    if (null == filesList || filesList.length == 0) {
      log.error("No files found to parse, at {}", directoryPath);
      return;
    }

    log.info("Parsing resumes...");
    List<Resume> experienceObjectList =
        Arrays.stream(filesList)
            // filtering by extension, for example, to parse only PDF resume
            // .filter(file -> file.getName().endsWith(".pdf"))
            .map(ResumeParser.INSTANCE::parseResumeFile)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    log.info("Finished parsing resumes...");
    log.info("Writing parsed data...");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    objectMapper.writeValue(Paths.get("experiences.json").toFile(), experienceObjectList);
    log.info("Finished!");
  }
}
