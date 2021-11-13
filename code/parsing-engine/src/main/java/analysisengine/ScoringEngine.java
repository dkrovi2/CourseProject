package analysisengine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoringEngine {
    private static final Logger LOGGER = Logger.getLogger(ScoringEngine.class.getName());

    private static Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<>();
    private static final String CORPUS_PATH = "/Users/jatinsaxena/IdeaProjects/CourseProject/code/parsing-engine/src/main/resources/CORPUS";

    ScoringEngine() throws IOException {
        FSDirectory dir = FSDirectory.open(new File(CORPUS_PATH));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,analyzer);
        writer = new IndexWriter(dir, config);
    }

    public void indexFilesDirectory() throws IOException {

        addFiles(new File(getClass().getResource("/CORPUS").getFile()));

        int OriginalNumDocs = writer.numDocs();

        queue.forEach(file -> {
            FileReader fr = null;
            try {
                fr = new FileReader(file);
                Document document = new Document();
                //Add content from json file
                document.add(new TextField("contents",fr));
                document.add(new StringField("path", file.getPath(), Field.Store.YES));
                document.add(new StringField("filename", file.getName(), Field.Store.YES));
                writer.addDocument(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        int newNumDocs = writer.numDocs();

        if(OriginalNumDocs == newNumDocs) {
            LOGGER.log(Level.INFO,"All documents get indexed");
        }
        else {
            LOGGER.log(Level.INFO,"{} documents get indexed",OriginalNumDocs-newNumDocs);
        }
        queue.clear();
        writer.close();


    }

    public void searchQuery(String userQuery) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(CORPUS_PATH)));
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = null;

        File docFile = new File(getClass().getResource(".").getFile() + "/ScoreList.txt");
        FileWriter docFileWriter = new FileWriter(docFile);
        try {
            Query q = new QueryParser(Version.LUCENE_47, "contents", analyzer).parse(userQuery);
            collector = TopScoreDocCollector.create(1000,true); //Scoring for all the documents.
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            for (int i = 0; i < Math.min(100, hits.length); ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                String filename = d.get("filename");
                System.out.println("Filename--->>>" + filename + "Score-->>>" + hits[i].score);
                filename = filename.substring(0, filename.length() - 4);
                String concatenatedOutput =  filename + "   " + (i + 1) + "   " + hits[i].score + "   " + " LuceneModel\n";
                docFileWriter.write(concatenatedOutput);
            }

        }
        catch (Exception e) {
         e.printStackTrace();
        }
        docFileWriter.close();
    }

    private void addFiles(File file) {

        if (!file.exists()) {
            System.out.println(file + " does not exist.");
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                addFiles(f);
            }
        } else {
            String filename = file.getName().toLowerCase();
            // ===================================================
            // Only index text files
            // ===================================================
            if (filename.endsWith(".txt")) {
                queue.add(file);
            } else {
                System.out.println("Skipped " + filename);
            }
        }
    }

   public static void main(String args[]) throws IOException {
       ScoringEngine scoringEngine = new ScoringEngine();
       scoringEngine.indexFilesDirectory();
       scoringEngine.searchQuery("what articles exist which deal with tss time sharing system  an operating system for ibm computers \n");
   }



}
