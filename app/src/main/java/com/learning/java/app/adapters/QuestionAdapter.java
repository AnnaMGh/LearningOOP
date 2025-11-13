package com.learning.java.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.learning.java.app.R;
import com.learning.java.app.model.ICallbackListener;
import com.learning.java.app.model.Question;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

    public ICallbackListener listenerCallback;

    public QuestionAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Question> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    private class ViewHolder {

        //remove
        ImageView imgRemove;

        //type
        RadioGroup rdGrType;
        boolean justChanged = false;

        //simple
        LinearLayout layoutSimple;
        EditText edtSimpleQuestion;
        EditText edtSimpleAnswer;

        //insert
        LinearLayout layoutInsert;
        EditText edtInsertQuestion1;
        EditText edtInsertAnswer;
        EditText edtInsertQuestion2;

        //multiple
        LinearLayout layoutMultiple;
        EditText edtMultipleQuestion;
        RadioGroup rdGrMultiple;
        EditText edtMultipleAnswer1;
        EditText edtMultipleAnswer2;
        EditText edtMultipleAnswer3;

        //add new question
        LinearLayout layoutAdd;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//        if (convertView == null) {
        convertView = View.inflate(getContext(), R.layout.question_cell, null);

        ViewHolder holder = new ViewHolder();
        holder.imgRemove = convertView.findViewById(R.id.img_remove);
        holder.rdGrType = convertView.findViewById(R.id.rd_gr_type);
        holder.layoutSimple = convertView.findViewById(R.id.layout_simple);
        holder.edtSimpleQuestion = convertView.findViewById(R.id.edt_simple_question);
        holder.edtSimpleAnswer = convertView.findViewById(R.id.edt_simple_ansewer);
        holder.layoutInsert = convertView.findViewById(R.id.layout_insert);
        holder.edtInsertQuestion1 = convertView.findViewById(R.id.edt_insert_question1);
        holder.edtInsertAnswer = convertView.findViewById(R.id.edt_insert_ansewer);
        holder.edtInsertQuestion2 = convertView.findViewById(R.id.edt_insert_question2);
        holder.layoutMultiple = convertView.findViewById(R.id.layout_multiple);
        holder.edtMultipleQuestion = convertView.findViewById(R.id.edt_multiple_question);
        holder.rdGrMultiple = convertView.findViewById(R.id.rd_gr_multiple);
        holder.edtMultipleAnswer1 = convertView.findViewById(R.id.edt_multiple_answer1);
        holder.edtMultipleAnswer2 = convertView.findViewById(R.id.edt_multiple_answer2);
        holder.edtMultipleAnswer3 = convertView.findViewById(R.id.edt_multiple_answer3);
        holder.layoutAdd = convertView.findViewById(R.id.layout_add);

        convertView.setTag(holder);
//        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final Question question = getItem(position);

        clearViews(viewHolder);
        if (question != null) {
            handleTypes(viewHolder, question);
            putInfoInCell(viewHolder, question);
        }

        //add button at the last cell
        if (position == (getCount() - 1)) {
            viewHolder.layoutAdd.setVisibility(View.VISIBLE);
        } else {
            viewHolder.layoutAdd.setVisibility(View.GONE);
        }

        //change type
        viewHolder.rdGrType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                changeType(viewHolder, question);

                if (i != -1) {
                    String tag = String.valueOf(radioGroup.findViewById(i).getTag());
                    if (question != null && !question.type.equals(tag)) {
                        question.type = tag;
                        notifyDataSetChanged();
                        viewHolder.justChanged = true;
                    }
                }

            }
        });

        //handle remove question
        viewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerCallback != null && getCount() != 1) {
                    listenerCallback.onClickRemove(position);
                }
            }
        });

        //handle add question
        viewHolder.layoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerCallback != null) {
                    listenerCallback.onClickAdd();
                }
            }
        });


        return convertView;
    }

    private void handleTypes(ViewHolder viewHolder, Question question) {
        switch (question.type) {
            case "1": {
                handleTypeSimple(viewHolder, question);
                break;
            }
            case "2": {
                handleTypeInsert(viewHolder, question);
                break;
            }
            case "3": {
                handleTypeMultiple(viewHolder, question);
                break;
            }
        }
    }

    private void clearViews(final ViewHolder viewHolder) {

        //clean type radiobutton
//        RadioButton rdBtn = (RadioButton) viewHolder.rdGrType.getChildAt(0);
//        if (viewHolder.rdGrType.findViewById(viewHolder.rdGrType.getCheckedRadioButtonId()) != rdBtn && !viewHolder.justChanged) {
//            viewHolder.rdGrType.clearCheck();
//            rdBtn.setChecked(true);
//            viewHolder.justChanged = false;
//        }

        //clean simple type
        viewHolder.edtSimpleQuestion.setText("");
        viewHolder.edtSimpleAnswer.setText("");
        viewHolder.layoutSimple.setVisibility(View.GONE);

        //clean insert type
        viewHolder.edtInsertQuestion1.setText("");
        viewHolder.edtInsertAnswer.setText("");
        viewHolder.edtInsertQuestion2.setText("");
        viewHolder.layoutInsert.setVisibility(View.GONE);

        //clean multiple type
//        viewHolder.rdGrMultiple.clearCheck();
        viewHolder.layoutMultiple.setVisibility(View.GONE);
    }

    private void handleTypeSimple(final ViewHolder viewHolder, final Question question) {

        //check if questions have enough size
        int size = question.questions.size();
        if (size > 1) {
            for (int i = size; i > 1; i--) {
                question.questions.remove(i - 1);
            }
        } else {
            for (int i = size; i < 1; i++) {
                question.questions.add("");
            }
        }

        //check if answer has enough size
        size = question.answers.size();
        if (size > 1) {
            for (int i = size; i > 1; i--) {
                question.answers.remove(i - 1);
            }
        } else {
            for (int i = size; i < 1; i++) {
                question.answers.add("");
            }
        }

        viewHolder.edtSimpleQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtSimpleQuestion.getText().toString().isEmpty() && !viewHolder.edtSimpleQuestion.getText().toString().equals(question.questions.get(0))) {
                    question.questions.set(0, viewHolder.edtSimpleQuestion.getText().toString());
                }
            }
        });

        viewHolder.edtSimpleQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtSimpleQuestion.requestFocus();
            }
        });


        viewHolder.edtSimpleAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtSimpleAnswer.getText().toString().isEmpty() && !viewHolder.edtSimpleAnswer.getText().toString().equals(question.answers.get(0))) {
                    question.answers.set(0, viewHolder.edtSimpleAnswer.getText().toString());
                }
            }
        });

        viewHolder.edtSimpleAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtSimpleAnswer.requestFocus();
            }
        });

    }

    private void handleTypeInsert(final ViewHolder viewHolder, final Question question) {

        //check if questions have enough size
        int size = question.questions.size();
        if (size > 2) {
            for (int i = size; i > 2; i--) {
                question.questions.remove(i - 1);
            }
        } else {
            for (int i = size; i < 2; i++) {
                question.questions.add("");
            }
        }

        //check if answer has enough size
        size = question.answers.size();
        if (size > 1) {
            for (int i = size; i > 1; i--) {
                question.answers.remove(i - 1);
            }
        } else {
            for (int i = size; i < 1; i++) {
                question.answers.add("");
            }
        }

        //question 1
        viewHolder.edtInsertQuestion1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtInsertQuestion1.getText().toString().isEmpty() && !viewHolder.edtInsertQuestion1.getText().toString().equals(question.questions.get(0))) {
                    question.questions.set(0, viewHolder.edtInsertQuestion1.getText().toString());
                }
            }
        });

        viewHolder.edtInsertQuestion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtInsertQuestion1.requestFocus();
            }
        });


        //answer
        viewHolder.edtInsertAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtInsertAnswer.getText().toString().isEmpty() && !viewHolder.edtInsertAnswer.getText().toString().equals(question.answers.get(0))) {
                    question.answers.set(0, viewHolder.edtInsertAnswer.getText().toString());
                }
            }
        });

        viewHolder.edtInsertAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtInsertAnswer.requestFocus();
            }
        });


        //question 2
        viewHolder.edtInsertQuestion2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtInsertQuestion2.getText().toString().isEmpty() && !viewHolder.edtInsertQuestion2.getText().toString().equals(question.questions.get(1))) {
                    question.questions.set(1, viewHolder.edtInsertQuestion2.getText().toString());
                }
            }
        });

        viewHolder.edtInsertQuestion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtInsertQuestion2.requestFocus();
            }
        });

    }

    private void handleTypeMultiple(final ViewHolder viewHolder, final Question question) {

        //check if questions have enough size
        int size = question.questions.size();
        if (size > 1) {
            for (int i = size; i > 1; i--) {
                question.questions.remove(i - 1);
            }
        } else {
            for (int i = size; i < 1; i++) {
                question.questions.add("");
            }
        }

        //check if answer has enough size
        size = question.answers.size();
        if (size > 3) {
            for (int i = size; i > 3; i--) {
                question.answers.remove(i - 1);
            }
        } else {
            for (int i = size; i < 3; i++) {
                question.answers.add("");
            }
        }

        if (Integer.parseInt(question.multipleCorrectAnswerId) < 1) {
            question.multipleCorrectAnswerId = "1";
        }


        //question
        viewHolder.edtMultipleQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtMultipleQuestion.getText().toString().isEmpty() && !viewHolder.edtMultipleQuestion.getText().toString().equals(question.questions.get(0))) {
                    question.questions.set(0, viewHolder.edtMultipleQuestion.getText().toString());
                }
            }
        });

        viewHolder.edtMultipleQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtMultipleQuestion.requestFocus();
            }
        });

        //answer 1
        viewHolder.edtMultipleAnswer1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtMultipleAnswer1.getText().toString().isEmpty() && !viewHolder.edtMultipleAnswer1.getText().toString().equals(question.answers.get(0))) {
                    question.answers.set(0, viewHolder.edtMultipleAnswer1.getText().toString());
                }
            }
        });

        viewHolder.edtMultipleAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtMultipleAnswer1.requestFocus();
            }
        });

        //answer 2
        viewHolder.edtMultipleAnswer2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtMultipleAnswer2.getText().toString().isEmpty() && !viewHolder.edtMultipleAnswer2.getText().toString().equals(question.answers.get(1))) {
                    question.answers.set(1, viewHolder.edtMultipleAnswer2.getText().toString());
                }
            }
        });

        viewHolder.edtMultipleAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtMultipleAnswer2.requestFocus();
            }
        });

        //answer 3
        viewHolder.edtMultipleAnswer3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && !viewHolder.edtMultipleAnswer3.getText().toString().isEmpty() && !viewHolder.edtMultipleAnswer3.getText().toString().equals(question.answers.get(2))) {
                    question.answers.set(2, viewHolder.edtMultipleAnswer3.getText().toString());
                }
            }
        });

        viewHolder.edtMultipleAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                viewHolder.edtMultipleAnswer3.requestFocus();
            }
        });


        viewHolder.rdGrMultiple.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int idChecked = viewHolder.rdGrMultiple.getCheckedRadioButtonId();

                if (idChecked == R.id.rd_btn_multiple_answer_1) {
                    question.multipleCorrectAnswerId = "1";
                } else if (idChecked == R.id.rd_btn_multiple_answer_2) {
                    question.multipleCorrectAnswerId = "2";
                } else if (idChecked == R.id.rd_btn_multiple_answer_3) {
                    question.multipleCorrectAnswerId = "3";
                }
            }
        });
    }

    private void putInfoInCell(final ViewHolder viewHolder, final Question question) {
        switch (question.type) {
            case "1": {
                RadioButton rdBtnChecked = viewHolder.rdGrType.findViewById(viewHolder.rdGrType.getCheckedRadioButtonId());
                if (rdBtnChecked == null || !rdBtnChecked.getTag().equals("1")) {
                    ((RadioButton) viewHolder.rdGrType.getChildAt(0)).setChecked(true);
                }
                viewHolder.layoutSimple.setVisibility(View.VISIBLE);
                viewHolder.edtSimpleQuestion.setText(question.questions.get(0));
                viewHolder.edtSimpleAnswer.setText(question.answers.get(0));
                break;
            }
            case "2": {
                RadioButton rdBtnChecked = viewHolder.rdGrType.findViewById(viewHolder.rdGrType.getCheckedRadioButtonId());
                if (rdBtnChecked == null || !rdBtnChecked.getTag().equals("2")) {
                    ((RadioButton) viewHolder.rdGrType.getChildAt(1)).setChecked(true);
                }
                viewHolder.layoutInsert.setVisibility(View.VISIBLE);
                viewHolder.edtInsertQuestion1.setText(question.questions.get(0));
                viewHolder.edtInsertAnswer.setText(question.answers.get(0));
                viewHolder.edtInsertQuestion2.setText(question.questions.get(1));
                break;
            }
            case "3": {
                RadioButton rdBtnChecked = viewHolder.rdGrType.findViewById(viewHolder.rdGrType.getCheckedRadioButtonId());
                if (rdBtnChecked == null || !rdBtnChecked.getTag().equals("3")) {
                    ((RadioButton) viewHolder.rdGrType.getChildAt(2)).setChecked(true);
                }
                viewHolder.layoutMultiple.setVisibility(View.VISIBLE);
                viewHolder.edtMultipleQuestion.setText(question.questions.get(0));
                viewHolder.edtMultipleAnswer1.setText(question.answers.get(0));
                viewHolder.edtMultipleAnswer2.setText(question.answers.get(1));
                viewHolder.edtMultipleAnswer3.setText(question.answers.get(2));

                ((RadioButton) viewHolder.rdGrMultiple.getChildAt(Integer.parseInt(question.multipleCorrectAnswerId) - 1)).setChecked(true);
            }
        }
    }

}
