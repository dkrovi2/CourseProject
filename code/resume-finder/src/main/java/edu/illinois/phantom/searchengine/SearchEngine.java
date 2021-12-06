package edu.illinois.phantom.searchengine;

import edu.illinois.phantom.model.UserQuery;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

public class SearchEngine {

  private final String indexLocation;

  public SearchEngine(final String indexLocation) {
    this.indexLocation = indexLocation;
  }

  public Set<String> searchQuery(List<UserQuery> userQuery) throws IOException {
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
    IndexSearcher searcher = new IndexSearcher(reader);

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    userQuery.forEach(inputQuery -> {
      Query query;
      if (inputQuery.isMandatorySkill()) {
        query = new BoostQuery(IntPoint.newRangeQuery(inputQuery.getSkill(), inputQuery.getMinExperience(), Integer.MAX_VALUE), inputQuery.getMinExperience());
      } else {
        query = IntPoint.newRangeQuery(inputQuery.getSkill(), inputQuery.getMinExperience(), Integer.MAX_VALUE);
      }

      builder.add(query, BooleanClause.Occur.SHOULD);
    });

    BooleanQuery booleanQuery = builder.build();

    TopScoreDocCollector collector;
    HashSet<String> resultset = new LinkedHashSet<>();

    try {
      collector = TopScoreDocCollector.create(100, Integer.MAX_VALUE); //Scoring for all the documents.
      searcher.search(booleanQuery, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;

      for (int i = 0; i < Math.min(50, hits.length); ++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        resultset.add(d.get("location"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Result Document-->>" + resultset);

    return resultset;
  }

  @SuppressWarnings("unused")
  public static void main(String[] args) throws IOException {
    if (null == args || args.length == 0 || "help".equalsIgnoreCase(args[0])) {
      System.out.println("Usage: java -cp resume-finder-all.jar " + SearchEngine.class.getName() + " <Index Location>");
      return;
    }
    UserQuery query1 = new UserQuery("JAVA", 15, true);
    UserQuery query2 = new UserQuery("KAFKA", 5, true);
    UserQuery query3 = new UserQuery("ANGULAR", 2, false);

    ArrayList<UserQuery> userQueryArrayList = new ArrayList<>();
    userQueryArrayList.add(query1);
    userQueryArrayList.add(query2);
    userQueryArrayList.add(query3);

    SearchEngine searchEngine = new SearchEngine(args[0]);
    searchEngine.searchQuery(userQueryArrayList);
  }

}
