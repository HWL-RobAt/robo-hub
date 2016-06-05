package de.hu_berlin.informatik.humboldtquiz.android;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import de.hu_berlin.informatik.humboldtquiz.R;
import de.hu_berlin.informatik.humboldtquiz.quizlogic.Question;
import de.hu_berlin.informatik.humboldtquiz.quizlogic.QuestionsCatalog;

/**
 * Created by robert on 23.05.16.
 */
public class QuizViewManager {

    AppCompatActivity appCompatActivity = null;
    QuestionsCatalog qc = null;
    Question q = null;

    public boolean lastAnswerCorrect = false;

    public QuizViewManager(AppCompatActivity aca) {
        appCompatActivity = aca;

        //get the application's resources
        Resources resources = appCompatActivity.getResources();
        qc = new QuestionsCatalog();

        try {
            //Create a InputStream to read the file into and get the file as a stream
            //InputStream inS = resources.getAssets().open("test_questions.csv");
            InputStream inS = resources.getAssets().open("quest_cata_002.csv");
            qc.loadCatalog(inS);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setLevel(int quizLevel) {
        qc.setLevel(quizLevel);
        qc.resetQuestionList();
    }

    public int getLevel() {
        return qc.getLevel();
    }

    public void onClickAnswerButton(int v) {
        System.out.println(v);

        final TextView textQuestion = (TextView) appCompatActivity.findViewById(R.id.question_result);

        lastAnswerCorrect = q.isCorrectAnswer(v);
        if ( lastAnswerCorrect ) textQuestion.setText("Die Antwort ist richtig!");
        else textQuestion.setText("Die Antwort ist leider falsch!");

        textQuestion.setVisibility(View.VISIBLE);

        final Button buttonR = (Button) appCompatActivity.findViewById(R.id.button_resume);
        buttonR.setEnabled(true);
        buttonR.setVisibility(View.VISIBLE);

        final Button buttonA = (Button) appCompatActivity.findViewById(R.id.button_a);
        buttonA.setEnabled(false);

        final Button buttonB = (Button) appCompatActivity.findViewById(R.id.button_b);
        buttonB.setEnabled(false);

        final Button buttonC = (Button) appCompatActivity.findViewById(R.id.button_c);
        buttonC.setEnabled(false);

        if ( q.getAnswers().length == 4 ) {
            final Button buttonD = (Button) appCompatActivity.findViewById(R.id.button_d);
            buttonD.setEnabled(false);
        }
    }

    public void nextQuestion() {
        System.out.println("question");
        appCompatActivity.setContentView(R.layout.quiz_layout);

        //System.out.println("#Quest pre: " + qc.questionListForLevel.size());
        q = qc.getNextQuestion();

        if ( q == null ) {
            System.out.println("Reset ql");
            qc.resetQuestionList();
            q = qc.getNextQuestion();
        }
        //System.out.println("#Quest post: " + qc.questionListForLevel.size());

        final Button buttonR = (Button) appCompatActivity.findViewById(R.id.button_resume);
        buttonR.setEnabled(false);
        buttonR.setVisibility(View.INVISIBLE);

        final TextView textQuestion = (TextView) appCompatActivity.findViewById(R.id.question);
        textQuestion.setText(q.getQuestion());

        final Button buttonA = (Button) appCompatActivity.findViewById(R.id.button_a);
        buttonA.setText(q.getAnswers()[0]);

        final Button buttonB = (Button) appCompatActivity.findViewById(R.id.button_b);
        buttonB.setText(q.getAnswers()[1]);

        final Button buttonC = (Button) appCompatActivity.findViewById(R.id.button_c);
        buttonC.setText(q.getAnswers()[2]);

        final Button buttonD = (Button) appCompatActivity.findViewById(R.id.button_d);
        if ( q.getAnswers().length < 4 ) {
            buttonD.setVisibility(View.INVISIBLE);
        } else {
            buttonD.setText(q.getAnswers()[3]);
        }
    }
}

