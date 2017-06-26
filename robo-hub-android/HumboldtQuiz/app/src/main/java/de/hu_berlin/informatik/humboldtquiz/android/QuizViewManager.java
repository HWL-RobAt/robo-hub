package de.hu_berlin.informatik.humboldtquiz.android;

import android.content.res.Resources;
import android.support.annotation.IdRes;
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
            InputStream inS = resources.getAssets().open("test_questions_2.csv");
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

    public void resetQuestions() {
        qc.resetQuestionList();
    }

    public void onClickAnswerButton(int v) {
        System.out.println(v);

        final TextView textQuestion = (TextView) appCompatActivity.findViewById(R.id.question_result);

        lastAnswerCorrect = q.isCorrectAnswer(v);
        if ( lastAnswerCorrect ) textQuestion.setText("Die Antwort ist richtig!");
        else                     textQuestion.setText("Die Antwort ist leider falsch!");

        textQuestion.setVisibility(View.VISIBLE);

        final Button buttonR = (Button) appCompatActivity.findViewById(R.id.button_resume);
        buttonR.setEnabled(true);
        buttonR.setVisibility(View.VISIBLE);

        @IdRes int[] buttonIDs = { R.id.button_a, R.id.button_b, R.id.button_c, R.id.button_d };

        for ( int b = (q.getAnswers().length-1); b >= 0; b--) {
            final Button button = (Button) appCompatActivity.findViewById(buttonIDs[b]);
            button.setEnabled(false);
        }
    }

    public void nextQuestion() {
        System.out.println("question");

        ((RoboCtrlActivity)appCompatActivity).switchToView(R.id.activity_quiz, R.layout.quiz_layout, RoboCtrlActivity.APP_MODE_QUIZ);

        //System.out.println("#Quest pre: " + qc.questionListForLevel.size());
        q = qc.getNextQuestion();

        assert ( q != null );

        //System.out.println("#Quest post: " + qc.questionListForLevel.size());

        final Button buttonR = (Button) appCompatActivity.findViewById(R.id.button_resume);
        buttonR.setEnabled(false);
        buttonR.setVisibility(View.INVISIBLE);

        final TextView textQuestion = (TextView) appCompatActivity.findViewById(R.id.question);
        String topic = qc.getStationTopic().replace("%",System.getProperty("line.separator"));
        textQuestion.setText(topic + "\n\n" + q.getQuestion());

        @IdRes int[] buttonIDs = { R.id.button_a, R.id.button_b, R.id.button_c, R.id.button_d };

        for ( int b = 0; b < buttonIDs.length; b++) {
            final Button button = (Button) appCompatActivity.findViewById(buttonIDs[b]);
            if ( q.getAnswers().length > b ) button.setText(q.getAnswers()[b]);
            else                             button.setVisibility(View.INVISIBLE);
        }
    }
}

