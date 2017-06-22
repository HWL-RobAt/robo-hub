package de.hu_berlin.informatik.humboldtquiz.quizlogic;

import java.util.ArrayList;

/**
 * Created by robert on 20.06.17.
 */
public class QuestionsList {
   ArrayList<Question> questionList = null;

  public QuestionsList() {
    this.questionList = new ArrayList<Question>();
  }

  public void addQuestion(Question q) {
    questionList.add(q);
  }

  public Question getQuestion(int i) {
    if ( i < questionList.size() )
      return questionList.get(i);

    return null;
  }

  public int size() {
    return questionList.size();
  }
}
