package analysisengine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ScoringEngine {
    private static final Logger LOGGER = Logger.getLogger(ScoringEngine.class.getName());

    private static Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<>();

    ScoringEngine() throws IOException {
        FSDirectory dir = FSDirectory.open(new File(getClass().getResource("/CORPUS").getFile()));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,analyzer);
        writer = new IndexWriter(dir, config);
    }

    public void indexFilesDirectory() throws IOException {

        addFiles(new File(getClass().getResource("/CORPUS").getFile()));

        int OriginalNumDocs = writer.numDocs();



        queue.forEach(file -> {
            try {

                FileReader fr = new FileReader(file);
                Object obj = new JSONParser().parse(fr);
                JSONObject jo = (JSONObject) obj;
                String location = (String) jo.get("location");
                JSONArray ja = (JSONArray) jo.get("skills");
                String allSkills = " ";
                Iterator itr2 = ja.iterator();
                Document document = new Document();
                document.add(new StringField("path", file.getPath(), Field.Store.YES));
                document.add(new StringField("filename", file.getName(), Field.Store.YES));
                document.add(new StringField("location", location, Field.Store.YES));
                while (itr2.hasNext()) {
                    Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
                    int duration=0;
                    String skill = null;
                    while (itr1.hasNext()) {
                        Map.Entry pair = itr1.next();
                        if(pair.getKey().toString().equalsIgnoreCase("duration")) {
                            duration = Integer.parseInt(pair.getValue().toString());
                        }
                        if(pair.getKey().toString().equalsIgnoreCase("skill")) {
                            skill = pair.getValue().toString();
                        }

                    }
                    allSkills = allSkills + skill;
                    String skills = skill+"_FIELD";
                    System.out.println("SKill with DUration-->>" + skills + " " + duration);
                    document.add(new IntField(skills, duration ,Field.Store.YES));

                    document.add(new StringField("allSkills", allSkills, Field.Store.YES));
                    writer.addDocument(document);
                }
                fr.close();
            }
            catch (Exception e) {
                e.printStackTrace();
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
        writer.commit();
        writer.close();


    }

    public Set<String> searchQuery(String userQuery) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(getClass().getResource("/CORPUS").getFile())));
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = NumericRangeQuery.newIntRange("Java_FIELD",5,50,true,true);
        Query query2 = NumericRangeQuery.newIntRange("Kafka_FIELD", 1,30,true,true);
        Query query3 = NumericRangeQuery.newIntRange("Angular_FIELD", 10,100,true,true);
        query.setBoost((float) 2.0);

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(query, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query3, BooleanClause.Occur.SHOULD);


        TopScoreDocCollector collector = null;
        HashSet<String> resultset = new LinkedHashSet<>();

        try {
            collector = TopScoreDocCollector.create(100,true); //Scoring for all the documents.
            searcher.search(booleanQuery, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            for (int i = 0; i < Math.min(50, hits.length); ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                resultset.add(d.get("location"));
                String location = d.get("location");
                System.out.println("File location--->>>" + location + " Score-->>>" + hits[i].score);
            }
            resultset.forEach(doc -> {
                System.out.println("New location--->>>" + doc);
            });
        }
        catch (Exception e) {
         e.printStackTrace();
        }
        System.out.println("Result Document-->>" + resultset);
        return resultset;
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
            if (filename.endsWith(".json")) {
                queue.add(file);
            } else {
                System.out.println("Skipped " + filename);
            }
        }
    }

   public static void main(String args[]) throws IOException {
       ScoringEngine scoringEngine = new ScoringEngine();
       scoringEngine.indexFilesDirectory();
       scoringEngine.searchQuery("Java");
   }
   
}
