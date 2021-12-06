package edu.illinois.phantom.analysisengine;

import edu.illinois.phantom.model.UserQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


public class ScoringEngine {
    private static final Logger LOGGER = Logger.getLogger(ScoringEngine.class.getName());

    private static Analyzer analyzer = new SimpleAnalyzer();
    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<>();

    private String corpusPath;

    public ScoringEngine(String dirPath) throws IOException {
        //FSDirectory dir = FSDirectory.open(Paths.get(getClass().getResource("/CORPUS").getFile()));

        corpusPath =  dirPath;
        FSDirectory dir = FSDirectory.open(Paths.get(corpusPath));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, config);
    }

    public void indexFilesDirectory() throws IOException {

        addFiles(new File(corpusPath));


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
                    String skills = skill.toUpperCase()+"_FIELD";
                    //document.add(new LegacyIntField(skills, duration ,Field.Store.YES));

                    document.add(new IntPoint(skills, duration));
                    document.add(new StoredField(skills,duration));
                    document.add(new StringField("allSkills", allSkills, Field.Store.YES));
                    writer.addDocument(document);
                }
                fr.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        });

        queue.clear();
        writer.commit();
        writer.close();


    }

    public Set<String> searchQuery(List<UserQuery> userQuery) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(corpusPath)));
        IndexSearcher searcher = new IndexSearcher(reader);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        userQuery.forEach(inputQuery -> {
            Query query;
            if(inputQuery.isMandatorySkill()) {
                 query = new BoostQuery(IntPoint.newRangeQuery(inputQuery.getSkill(), inputQuery.getMinExperience()
                        , Integer.MAX_VALUE), (float) inputQuery.getMinExperience());
            }
            else {
                query = IntPoint.newRangeQuery(inputQuery.getSkill(), inputQuery.getMinExperience()
                        , Integer.MAX_VALUE);
            }

            builder.add(query,BooleanClause.Occur.SHOULD);
        });

        BooleanQuery booleanQuery = builder.build();

        TopScoreDocCollector collector = null;
        HashSet<String> resultset = new LinkedHashSet<>();

        try {
            collector = TopScoreDocCollector.create(100,Integer.MAX_VALUE); //Scoring for all the documents.
            searcher.search(booleanQuery, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            for (int i = 0; i < Math.min(50, hits.length); ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                resultset.add(d.get("location"));
                String location = d.get("location");
               // System.out.println("File location--->>>" + location + " Score-->>>" + hits[i].score);
            }
        }
        catch (Exception e) {
         e.printStackTrace();
        }
        System.out.println("Result Document-->>" + resultset);

        /* just to test it out
        resultset = new HashSet<String>();
        resultset.add("Location 1");
        resultset.add("Location 2");
        */
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

       if (args.length ==0 || (args.length != 0 && "help".equalsIgnoreCase(args[0]))) {
           System.out.println(
                   "Usage: java edu.illinois.phantom.analysisengine.ScoringEngine <Directory-where-json-files-are-located>");
           return;
       }

       ScoringEngine scoringEngine = new ScoringEngine(args[0]);
       scoringEngine.indexFilesDirectory();
       //TODO: Remove Later
       UserQuery query1 = new UserQuery("JAVA",15,true);
       UserQuery query2 = new UserQuery("KAFKA",5,true);
       UserQuery query3 = new UserQuery("ANGULAR",2,false);

       ArrayList<UserQuery> userQueryArrayList = new ArrayList<>();
       userQueryArrayList.add(query1);
       userQueryArrayList.add(query2);
       userQueryArrayList.add(query3);

       scoringEngine.searchQuery(userQueryArrayList);
   }
   
}
