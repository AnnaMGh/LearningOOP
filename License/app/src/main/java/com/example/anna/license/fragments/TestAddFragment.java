package com.learning.java.app.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.Test;

import java.util.ArrayList;

public class TestAddFragment extends BaseFragment {

    EditText title;
    EditText[] questions = new EditText[5];
    EditText[] answers = new EditText[5];
    RadioGroup[] type = new RadioGroup[5];
    RadioGroup[] typeRadio = new RadioGroup[5];
    EditText[] typeEdit1 = new EditText[3];
    EditText[] typeEdit2 = new EditText[3];
    EditText[] typeEdit3 = new EditText[3];
    EditText[] typeEdit4 = new EditText[3];
    EditText[] typeEdit5 = new EditText[3];
    Button button;
    LinearLayout[] layout = new LinearLayout[5];

    SqliteHelper database;
    Test test;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.add_test_fragment, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    void initialize() {

        //title:
        title = baseView.findViewById(R.id.addtest_title);

        //questions:
        questions[0] = baseView.findViewById(R.id.addtest_question1_editv);
        questions[1] = baseView.findViewById(R.id.addtest_question2_editv);
        questions[2] = baseView.findViewById(R.id.addtest_question3_editv);
        questions[3] = baseView.findViewById(R.id.addtest_question4_editv);
        questions[4] = baseView.findViewById(R.id.addtest_question5_editv);

        //answers:
        answers[0] = baseView.findViewById(R.id.addtest_question1_answer1);
        answers[1] = baseView.findViewById(R.id.addtest_question2_answer1);
        answers[2] = baseView.findViewById(R.id.addtest_question3_answer1);
        answers[3] = baseView.findViewById(R.id.addtest_question4_answer1);
        answers[4] = baseView.findViewById(R.id.addtest_question5_answer1);

        //type:
        type[0] = baseView.findViewById(R.id.addtest_question1_choose_radiogroup);
        type[1] = baseView.findViewById(R.id.addtest_question2_choose_radiogroup);
        type[2] = baseView.findViewById(R.id.addtest_question3_choose_radiogroup);
        type[3] = baseView.findViewById(R.id.addtest_question4_choose_radiogroup);
        type[4] = baseView.findViewById(R.id.addtest_question5_choose_radiogroup);

        //typeRadio:
        typeRadio[0] = baseView.findViewById(R.id.addtest_question1_answer_radiogroup);
        typeRadio[1] = baseView.findViewById(R.id.addtest_question2_answer_radiogroup);
        typeRadio[2] = baseView.findViewById(R.id.addtest_question3_answer_radiogroup);
        typeRadio[3] = baseView.findViewById(R.id.addtest_question4_answer_radiogroup);
        typeRadio[4] = baseView.findViewById(R.id.addtest_question5_answer_radiogroup);

        //typeEdit1
        typeEdit1[0] = baseView.findViewById(R.id.addtest_question1_et_answer11);
        typeEdit1[1] = baseView.findViewById(R.id.addtest_question1_et_answer12);
        typeEdit1[2] = baseView.findViewById(R.id.addtest_question1_et_answer13);

        //typeEdit2
        typeEdit2[0] = baseView.findViewById(R.id.addtest_question2_et_answer11);
        typeEdit2[1] = baseView.findViewById(R.id.addtest_question2_et_answer12);
        typeEdit2[2] = baseView.findViewById(R.id.addtest_question2_et_answer13);

        //typeEdit3
        typeEdit3[0] = baseView.findViewById(R.id.addtest_question3_et_answer11);
        typeEdit3[1] = baseView.findViewById(R.id.addtest_question3_et_answer12);
        typeEdit3[2] = baseView.findViewById(R.id.addtest_question3_et_answer13);

        //typeEdit4
        typeEdit4[0] = baseView.findViewById(R.id.addtest_question4_et_answer11);
        typeEdit4[1] = baseView.findViewById(R.id.addtest_question4_et_answer12);
        typeEdit4[2] = baseView.findViewById(R.id.addtest_question4_et_answer13);

        //typeEdit5
        typeEdit5[0] = baseView.findViewById(R.id.addtest_question5_et_answer11);
        typeEdit5[1] = baseView.findViewById(R.id.addtest_question5_et_answer12);
        typeEdit5[2] = baseView.findViewById(R.id.addtest_question5_et_answer13);

        //button
        button = baseView.findViewById(R.id.addtest_btn);

        //layouts
        layout[0] = baseView.findViewById(R.id.addtest_layout1);
        layout[1] = baseView.findViewById(R.id.addtest_layout2);
        layout[2] = baseView.findViewById(R.id.addtest_layout3);
        layout[3] = baseView.findViewById(R.id.addtest_layout4);
        layout[4] = baseView.findViewById(R.id.addtest_layout5);

        database = SqliteHelper.getInstance(getActivity());
        test = new Test();
    }

    boolean validation() {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "You did not write the title of the test!", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < 5; i++) {
            if (questions[i].getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "You did not write the sentance for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (type[i].getCheckedRadioButtonId() < 0) {
                Toast.makeText(getActivity(), "You did not choose the type for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                return false;
            }

            RadioButton radioButton = type[i].findViewById(type[i].getCheckedRadioButtonId());
            if (radioButton.getText().equals("Radio Button Test")) {
                if (typeRadio[i].getCheckedRadioButtonId() < 0) {
                    Toast.makeText(getActivity(), "You did not choose an answer for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                    return false;
                }

                for (int j = 0; j < 3; j++) {
                    switch (i) {
                        case 0: {
                            if (typeEdit1[j].getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "You did not write all the answers for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            break;
                        }
                        case 1: {
                            if (typeEdit2[j].getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "You did not write all the answers for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            break;
                        }
                        case 2: {
                            if (typeEdit3[j].getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "You did not write all the answers for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            break;
                        }
                        case 3: {
                            if (typeEdit4[j].getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "You did not write all the answers for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            break;
                        }
                        case 4: {
                            if (typeEdit5[j].getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "You did not write all the answers for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            break;
                        }
                    }
                }
            } else if (radioButton.getText().equals("Write Text Test")) {
                if (answers[i].getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You did not write the answer for question " + (i + 1) + " !", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    void setQuestions() {
        //title
        test.setTitle(title.getText().toString());

        //questions
        test.setQuestion1(questions[0].getText().toString());
        test.setQuestion2(questions[1].getText().toString());
        test.setQuestion3(questions[2].getText().toString());
        test.setQuestion4(questions[3].getText().toString());
        test.setQuestion5(questions[4].getText().toString());

        //answers
        test.setAnswer1(questions[0].getText().toString());
        test.setAnswer2(questions[1].getText().toString());
        test.setAnswer3(questions[2].getText().toString());
        test.setAnswer4(questions[3].getText().toString());
        test.setAnswer5(questions[4].getText().toString());

        //type
        checkType();

    }

    void checkType() {


        for (int i = 0; i < 5; i++) {
            RadioButton answer = type[i].findViewById(type[i].getCheckedRadioButtonId());
            if (answer == null) {
                return;
            }

            String[] arrayTypeAnswer = answer.getText().toString().split(" ");
            String typeAnswer = arrayTypeAnswer[0].toLowerCase();
            switch (i) {
                case 0: {
                    test.setType1(typeAnswer);
                    if (typeAnswer.equals("radio")) {
                        RadioButton answ1 = typeRadio[0].findViewById(typeRadio[0].getCheckedRadioButtonId());
                        if (answ1 == null) {
                            return;
                        }

                        if (answ1.getText().toString().equals("answer1")) {
                            test.setAnswer1(typeEdit1[0].getText().toString());
                        } else if (answ1.getText().toString().equals("answer2")) {
                            test.setAnswer1(typeEdit1[1].getText().toString());
                        } else {
                            test.setAnswer1(typeEdit1[2].getText().toString());
                        }
                        test.setAnswer11(typeEdit1[0].getText().toString());
                        test.setAnswer12(typeEdit1[1].getText().toString());
                        test.setAnswer13(typeEdit1[2].getText().toString());
                    } else {
                        test.setAnswer1(answers[0].getText().toString());
                    }
                    break;
                }
                case 1: {
                    test.setType2(typeAnswer);
                    if (typeAnswer.equals("radio")) {
                        RadioButton answ1 = typeRadio[1].findViewById(typeRadio[1].getCheckedRadioButtonId());
                        if (answ1 == null) {
                            return;
                        }
                        if (answ1.getText().toString().equals("answer1")) {
                            test.setAnswer2(typeEdit2[0].getText().toString());
                        } else if (answ1.getText().toString().equals("answer2")) {
                            test.setAnswer2(typeEdit2[1].getText().toString());
                        } else {
                            test.setAnswer2(typeEdit2[2].getText().toString());
                        }
                        test.setAnswer21(typeEdit2[0].getText().toString());
                        test.setAnswer22(typeEdit2[1].getText().toString());
                        test.setAnswer23(typeEdit2[2].getText().toString());
                    } else {
                        test.setAnswer2(answers[1].getText().toString());
                    }
                    break;
                }
                case 2: {
                    test.setType3(typeAnswer);
                    if (typeAnswer.equals("radio")) {
                        RadioButton answ1 = typeRadio[2].findViewById(typeRadio[2].getCheckedRadioButtonId());
                        if (answ1 == null) {
                            return;
                        }
                        if (answ1.getText().toString().equals("answer1")) {
                            test.setAnswer3(typeEdit3[0].getText().toString());
                        } else if (answ1.getText().toString().equals("answer2")) {
                            test.setAnswer3(typeEdit3[1].getText().toString());
                        } else {
                            test.setAnswer3(typeEdit3[2].getText().toString());
                        }
                        test.setAnswer31(typeEdit3[0].getText().toString());
                        test.setAnswer32(typeEdit3[1].getText().toString());
                        test.setAnswer33(typeEdit3[2].getText().toString());
                    } else {
                        test.setAnswer3(answers[2].getText().toString());
                    }
                    break;
                }
                case 3: {
                    test.setType4(typeAnswer);
                    if (typeAnswer.equals("radio")) {
                        RadioButton answ1 = typeRadio[3].findViewById(typeRadio[3].getCheckedRadioButtonId());
                        if (answ1 == null) {
                            return;
                        }
                        if (answ1.getText().toString().equals("answer1")) {
                            test.setAnswer4(typeEdit4[0].getText().toString());
                        } else if (answ1.getText().toString().equals("answer2")) {
                            test.setAnswer4(typeEdit4[1].getText().toString());
                        } else {
                            test.setAnswer4(typeEdit4[2].getText().toString());
                        }
                        test.setAnswer41(typeEdit4[0].getText().toString());
                        test.setAnswer42(typeEdit4[1].getText().toString());
                        test.setAnswer43(typeEdit4[2].getText().toString());
                    } else {
                        test.setAnswer4(answers[3].getText().toString());
                    }
                    break;
                }
                case 4: {

                    test.setType5(typeAnswer);
                    if (typeAnswer.equals("radio")) {
                        RadioButton answ1 = typeRadio[4].findViewById(typeRadio[4].getCheckedRadioButtonId());
                        if (answ1 == null) {
                            return;
                        }
                        if (answ1.getText().toString().equals("answer1")) {
                            test.setAnswer5(typeEdit5[0].getText().toString());
                        } else if (answ1.getText().toString().equals("answer2")) {
                            test.setAnswer5(typeEdit5[1].getText().toString());
                        } else {
                            test.setAnswer5(typeEdit5[2].getText().toString());
                        }
                        test.setAnswer51(typeEdit5[0].getText().toString());
                        test.setAnswer52(typeEdit5[1].getText().toString());
                        test.setAnswer53(typeEdit5[2].getText().toString());
                    } else {
                        test.setAnswer5(answers[4].getText().toString());
                    }
                    break;
                }
            }
        }
    }

    void onCheckedChange(final RadioGroup rType, final EditText editText, final LinearLayout layout) {
        rType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = rType.findViewById(rType.getCheckedRadioButtonId());


                if (radioButton.getText().toString().equals("Radio Button Test")) {
                    layout.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    editText.setText("");
                } else {
                    layout.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void onClickViews() {

        for (int i = 0; i < 5; i++) {
            onCheckedChange(type[i], answers[i], layout[i]);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {
                    return;
                }

                setQuestions();
                database.addTest(test);

//                getActivity().getFragmentManager().popBackStack("MainMenuFragment",0);
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
    }
}
