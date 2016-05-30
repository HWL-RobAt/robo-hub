package de.hu_berlin.informatik.humboldtquiz;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
  List<Question> questions = null;
  HashMap<String, String[]> answers4Question = null;
  HashMap<Integer, List<Question>> questionsWithLevel = null;

  int currentLevel = -1;
  List<Question> questionListForLevel = null;

  public QuestionsCatalog() {
    rand = new Random();
    rand.setSeed(0);
    questions = new ArrayList<Question>();
    answers4Question = new HashMap<String, String[]>();
    questionsWithLevel = new HashMap<Integer, List<Question>>();
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
      String line;
      while (null != (line = bufR.readLine())) {
        System.out.println("Read:" + line);
        String[] data = line.split(";");
        int level = new Integer(data[0]);
        String question = data[1];
        String answers[] = new String[data.length-2];
        for ( int i = 2; i < data.length; i++)
          answers[i-2] = data[i];
        Question q = new Question(question,answers,0,level);
        questions.add(q);
        answers4Question.put(question, answers);
        List<Question> questionListLevel = null;
        if ( ! questionsWithLevel.containsKey(level) ) {
          questionListLevel = new ArrayList<Question>();
          questionsWithLevel.put(level, questionListLevel);
        } else {
          questionListLevel = questionsWithLevel.get(level);
        }
        questionListLevel.add(q);
      }

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    questionListForLevel = new Vector<Question>();
    resetQuestionList();
  }

  public void setLevel(int level) {
    currentLevel = level;
  }

  //copy questionList to questionListForLevel. This can than manupilated (remove question)
  public void resetQuestionList() {
    questionListForLevel.clear();

    System.out.println("0: " + questionsWithLevel.get(0).size());
    System.out.println("1: " + questionsWithLevel.get(1).size());


    List<Question> copyList = questions;
    if ((currentLevel != -1) && (questionsWithLevel.containsKey(currentLevel))) {
      copyList = questionsWithLevel.get(currentLevel);
    }
    System.out.println("Size copyList: " + copyList.size());

    for  ( int i = 0; i < copyList.size(); i++) questionListForLevel.add(copyList.get(i));
  }

  public Question getNextQuestion() {
    if ( questionListForLevel.size() == 0 ) return null;

    int q_index = rand.nextInt(questionListForLevel.size());

    Question q = questionListForLevel.get(q_index);
    questionListForLevel.remove(q_index);

    q.randQuestion();

    return q;
  }

}
