import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 14.04.16
 * Time: 07:30
 * To change this template use File | Settings | File Templates.
 */
public class QuestionsCatalog {
  Random rand = null;
  Vector<Question> questions = null;
  HashMap<String, String[]> answers4Question = null;

  public QuestionsCatalog() {
    rand = new Random();
    rand.setSeed(0);
    questions = new Vector<Question>();
    answers4Question = new HashMap<String, String[]>();
  }

  public void loadCatalog(InputStream is) {
    BufferedReader bufR = new BufferedReader(new InputStreamReader(is));

    loadCatalog(bufR);
  }


  public void loadCatalog(String filename) {
    try {
      FileReader fr = new FileReader(filename);
      BufferedReader bufR = new BufferedReader(fr);

      loadCatalog(bufR);

      fr.close();
    } catch (FileNotFoundException fnfE) {
      System.out.println("File " + filename + " not found");
      throw new RuntimeException();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public void loadCatalog(BufferedReader bufR) {
    try {
      String line = null;
      while (null != (line = bufR.readLine())) {
        System.out.println("Read:" + line);
        String[] data = line.split(";");
        int level = new Integer(data[0]);
        String question = data[1];
        String answers[] = new String[data.length-2];
        for ( int i = 2; i < data.length; i++)
          answers[i-2] = data[i];
        Question q = new Question(question,answers,0);
        q.setLevel(level);
        questions.add(q);
        answers4Question.put(question, answers);
      }

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }


  public Question getNextQuestion() {
    return questions.get(rand.nextInt(questions.size()));
  }

}
