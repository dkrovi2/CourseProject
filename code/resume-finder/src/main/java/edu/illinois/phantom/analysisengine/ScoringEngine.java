package edu.illinois.phantom.analysisengine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Slf4j
public class ScoringEngine {

  private static final Analyzer analyzer = new SimpleAnalyzer();

  private final List<File> queue = new ArrayList<>();

  private final String corpusPath;
  private final String indexLocation;

  public ScoringEngine(final String corpusPath, final String indexLocation) {
    this.corpusPath = corpusPath;
    this.indexLocation = indexLocation;
  }

  public void indexFilesDirectory() throws IOException {

    addFiles(new File(corpusPath));

    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try (FSDirectory indexDir = FSDirectory.open(Paths.get(indexLocation));
         IndexWriter writer = new IndexWriter(indexDir, config)) {
      queue.forEach(file -> {
        try {
          FileReader fr = new FileReader(file);
          Object obj = new JSONParser().parse(fr);
          JSONObject jo = (JSONObject) obj;
          addDocumentToWriter(file, (String) jo.get("location"), (JSONArray) jo.get("skills"), writer);
          fr.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

      });
      writer.commit();
    }
    queue.clear();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void addDocumentToWriter(
    final File file,
    final String location,
    final JSONArray ja,
    final IndexWriter writer) throws IOException {
    String allSkills = " ";
    Iterator itr2 = ja.iterator();
    Document document = new Document();
    document.add(new StringField("path", file.getPath(), Field.Store.YES));
    document.add(new StringField("filename", file.getName(), Field.Store.YES));
    document.add(new StringField("location", location, Field.Store.YES));
    while (itr2.hasNext()) {
      Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
      int duration = 0;
      String skill = null;
      while (itr1.hasNext()) {
        Map.Entry pair = itr1.next();
        if (pair.getKey().toString().equalsIgnoreCase("duration")) {
          duration = Integer.parseInt(pair.getValue().toString());
        }
        if (pair.getKey().toString().equalsIgnoreCase("skill")) {
          skill = pair.getValue().toString();
        }

      }
      if (null != skill) {
        allSkills = allSkills + " " + skill;
        String skills = skill.toUpperCase() + "_FIELD";
        document.add(new IntPoint(skills, duration));
        document.add(new StoredField(skills, duration));
      }
    }
    document.add(new StringField("allSkills", allSkills, Field.Store.YES));
    writer.addDocument(document);
    log.info("Added {} to the scoring index, with skills:{}", file, allSkills);

  }

  private void addFiles(File file) {

    if (!file.exists()) {
      System.out.println(file + " does not exist.");
    }
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        Arrays.stream(files).forEach(this::addFiles);
      }
    } else {
      String filename = file.getName().toLowerCase();
      // ===================================================
      // Only index text files
      // ===================================================
      if (filename.endsWith(".json")) {
        queue.add(file);
      } else {
        System.out.println("Skipped " + filename);
      }
    }
  }

  public static void main(String[] args) throws IOException {

    if (args.length == 0 || "help".equalsIgnoreCase(args[0]) || args.length != 2) {
      System.out.println(
        "Usage: java -cp resume-finder-all.jar edu.illinois.phantom.analysisengine.ScoringEngine " +
          "<Directory-where-json-files-are-located> <Directory to store index");
      return;
    }

    ScoringEngine scoringEngine = new ScoringEngine(args[0], args[1]);
    scoringEngine.indexFilesDirectory();
  }

}
