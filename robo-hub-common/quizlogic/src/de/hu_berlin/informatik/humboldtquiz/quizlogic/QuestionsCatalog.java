package de.hu_berlin.informatik.humboldtquiz.quizlogic;

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
  QuestionsList questions = null;
  HashMap<Integer, List<QuestionsList>> questionsWithLevel = null;

  int currentLevel = 0;
  int currentStation = -1;
  //Questions for multiple stations
  List<QuestionsList> questionListForLevel = null;

  boolean randQuestion = false;

  public List<String> stationTopic = null;

  public QuestionsCatalog() {
    rand = new Random();
    rand.setSeed(0);
    questions = new QuestionsList();
    questionsWithLevel = new HashMap<Integer, List<QuestionsList>>();

    questionListForLevel = new ArrayList<QuestionsList>();

    stationTopic = new ArrayList<String>();
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

        int level = -1;
        int station = new Integer(data[1]) - 1;
        if ( data[0].equals("-") ) {
          stationTopic.add(station,data[2]);
          continue;
        } else {
          level = new Integer(data[0]);
        }

        String question = data[2];
        String answers[] = new String[data.length-3];
        for ( int i = 3; i < data.length; i++)
          answers[i-3] = data[i];

        Question q = new Question(question,answers,0,level);
        questions.addQuestion(q);

        List<QuestionsList> questionListLevel = null;
        if ( ! questionsWithLevel.containsKey(level) ) {
          questionsWithLevel.put(level, new ArrayList<QuestionsList>());
        }
        questionListLevel = questionsWithLevel.get(level);

        QuestionsList questionList = null;
        if ( questionListLevel.size() <= station ) {
           questionListLevel.add(station,new QuestionsList());
        }
        questionList = questionListLevel.get(station);

        questionList.addQuestion(q);
      }

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    resetQuestionList();
  }

  public void setLevel(int level) {
    currentLevel = level;
  }

  public int getLevel() {
    return currentLevel;
  }

  //copy questionList to questionListForLevel. This can than manupilated (remove question)
  public void resetQuestionList() {
    questionListForLevel.clear();

    System.out.println("0: " + questionsWithLevel.get(0).size());
    System.out.println("1: " + questionsWithLevel.get(1).size());

    List<QuestionsList> copyList = questionsWithLevel.get(currentLevel);
    if ((currentLevel != -1) && (questionsWithLevel.containsKey(currentLevel))) {
      copyList = questionsWithLevel.get(currentLevel);
    }
    System.out.println("Size copyList: " + copyList.size());

    for  ( int i = 0; i < copyList.size(); i++) questionListForLevel.add(copyList.get(i));

    currentStation = -1;
  }

  public Question getNextQuestion() {
    if ( questionListForLevel.size() == 0 ) return null;

    //get random station orjust next
    int q_index = randQuestion?rand.nextInt(questionListForLevel.size()):0;

    QuestionsList ql = questionListForLevel.get(q_index);
    questionListForLevel.remove(q_index);

    Question q = ql.getQuestion(rand.nextInt(ql.size()));
    currentStation++;

    q.randAnswers();

    return q;
  }

  public String getStationTopic() {
    if ( (currentStation < 0) || ( currentStation >= stationTopic.size()) ) return "";
    return stationTopic.get(currentStation);
  }

  public void setRandQuestion(boolean randQuestion) {
    this.randQuestion = randQuestion;
  }
}
