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
    this.question = question;
    this.answers = answers;
    if (answers.length > 0)
      rightAnswer = 0;
  }

  public Question(String question, String[] answers, int rightAnswers) {
    this.question = question;
    this.answers = answers;
    this.rightAnswer = rightAnswers;
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
}
