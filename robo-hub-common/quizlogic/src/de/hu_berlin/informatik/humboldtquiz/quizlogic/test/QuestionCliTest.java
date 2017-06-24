package de.hu_berlin.informatik.humboldtquiz.quizlogic.test;

import java.util.Scanner;

import de.hu_berlin.informatik.humboldtquiz.quizlogic.Question;
import de.hu_berlin.informatik.humboldtquiz.quizlogic.QuestionsCatalog;

/**
 * Created by IntelliJ IDEA.
 * User: robert
 * Date: 15.04.16
 * Time: 07:44
 * To change this template use File | Settings | File Templates.
 */
public class QuestionCliTest {

  public static void main(String[] args) {
    QuestionsCatalog qc = new QuestionsCatalog();
    qc.loadCatalog("/home/robert/UNI/PhD/2016/application/robo-hub/robo-hub-common/quizlogic/data/test_questions_2.csv");

    Scanner scanner = new Scanner(System.in, "UTF-8");
    scanner.useLocale(java.util.Locale.US);

    int ansInt = -1;


    while (ansInt != 0) {
      Question q = qc.getNextQuestion();
      if ( q == null ) break;
      System.out.println(qc.getStationTopic());
      boolean isCorrect = q.askQuestion();

      if ( isCorrect ) System.out.println("Richtig!");
      else System.out.println("Nöö!");

      System.out.print("Noch 'ne Frage (y/n)? ");
      String ans = scanner.next();
      ansInt = ((int)ans.charAt(0)-110); //n
    }
  }
}
