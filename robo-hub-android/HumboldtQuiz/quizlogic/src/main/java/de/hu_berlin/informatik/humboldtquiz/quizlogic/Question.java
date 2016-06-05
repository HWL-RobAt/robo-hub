package de.hu_berlin.informatik.humboldtquiz.quizlogic;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 14.04.16
 * Time: 07:25
 * To change this template use File | Settings | File Templates.
 */
public class Question {
  String question;
  String answers[] = null;
  int rightAnswer = -1;
  int level = 0;

  static char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F'};

  public Question(String question, String[] answers) {
    this(question, answers, (answers.length > 0)?0:-1, 0);
  }

  public Question(String question, String[] answers, int rightAnswers) {
    this(question, answers, rightAnswers,0);
  }

  public Question(String question, String[] answers, int rightAnswers, int level) {
    this.question = question;
    this.answers = answers;
    this.rightAnswer = rightAnswers;
    this.level = level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public boolean isCorrectAnswer(int number) {
    return (rightAnswer == number);
  }

  public void printQuestion() {
    System.out.println(question + "?");
    for(int a = 0; a < answers.length; a++)
      System.out.println(letters[a] + ". " + answers[a] + ".");
  }

  public boolean askQuestion() {
    Scanner scanner = new Scanner(System.in, "UTF-8");
    scanner.useLocale(java.util.Locale.US);

    printQuestion();

    int ansIndex = -1;

    while (!((0 <= ansIndex) && (ansIndex < answers.length))) {
      System.out.print("? ");

      String ans = scanner.next();
      ansIndex = ((int)ans.charAt(0)-65);
    }

    return isCorrectAnswer(ansIndex);
  }

  public String[] getAnswers() {
    return answers;
  }

  public String getQuestion() {
    return question;
  }

  public void randAnswers() {
    Random rand = new Random();
    int randRotate = rand.nextInt(answers.length); //rotate using rand (0,length-1)
    if ( randRotate != 0 ) {
      String answer0 = answers[0];
      int r = 0;
      for( int i = 0; i < answers.length-1; i++ ) {
        answers[r] = answers[(r+randRotate)%answers.length];
        r = (r+randRotate)%answers.length;
      }
      answers[r] = answer0;
      rightAnswer = (rightAnswer+answers.length-randRotate)%answers.length;
    }
  }
}
