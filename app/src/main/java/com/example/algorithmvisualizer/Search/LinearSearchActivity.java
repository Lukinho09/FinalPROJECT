package com.example.algorithmvisualizer.Search;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.R;

import java.util.ArrayList;

public class LinearSearchActivity extends AppCompatActivity {

    private EditText input;
    private GridLayout indexRow;
    private GridLayout valueRow;

    private EditText inputTarget;
    private Button btnStartSearch;
    private Button btnEnterN;
    private TextView txtResult;
    private TextView txtStep;


    private Button btnNextStep;
    private Button btnBackStep;

    private int N = -1;
    private final ArrayList<TextView> valueCells = new ArrayList<>();
    private final ArrayList<Integer> arrayValues = new ArrayList<>();

    private boolean readingArray = false;
    private boolean isRunning = false;


    private int target = 0;
    private int stepIndex = -1;
    private int maxStep = -1;
    private int foundIndex = -1;
    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_search);

        input = findViewById(R.id.inputN);
        indexRow = findViewById(R.id.indexRow);
        valueRow = findViewById(R.id.valueRow);

        inputTarget = findViewById(R.id.inputTarget);
        btnStartSearch = findViewById(R.id.btnStartSearch);
        btnEnterN = findViewById(R.id.btnEnterN);
        txtResult = findViewById(R.id.txtResult);
        txtStep = findViewById(R.id.txtStep);

        btnNextStep = findViewById(R.id.btnNextStep);
        btnBackStep = findViewById(R.id.btnBackStep);

        setupInputForN();
        setStepButtonsEnabled(false);

        btnEnterN.setOnClickListener(v -> handleEnterForN());

        input.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter =
                    actionId == EditorInfo.IME_ACTION_DONE ||
                            actionId == EditorInfo.IME_ACTION_NEXT ||
                            (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                    && event.getAction() == KeyEvent.ACTION_DOWN);

            if (!isEnter) return false;

            if (!readingArray) {
                handleEnterForN();
                return true;
            }
            return true;
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!readingArray) return;
                if (isRunning) return;

                String text = s.toString().trim();
                if (text.isEmpty()) {
                    clearCells();
                    resetArrayValues();
                    return;
                }

                String[] parts = text.split("\\s+");
                fillCells(parts);
            }
        });

        btnStartSearch.setOnClickListener(v -> {
            if (N <= 0 || valueCells.isEmpty()) return;

            String t = inputTarget.getText().toString().trim();
            if (t.isEmpty()) return;

            try { target = Integer.parseInt(t); }
            catch (Exception e) { return; }

            if (!isArrayFullyFilled()) {
                txtResult.setTextColor(Color.parseColor("#E11D48"));
                txtResult.setText("Fill all N values first.");
                return;
            }

            startLinearManual();
        });

        btnNextStep.setOnClickListener(v -> doNext());
        btnBackStep.setOnClickListener(v -> doBack());
    }

    private void setupInputForN() {
        readingArray = false;
        input.setText("");
        input.setHint("Enter N (array size) then press Enter");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        txtStep.setText("");
        txtResult.setText("");
        txtResult.setTextColor(Color.parseColor("#111827"));
    }

    private void handleEnterForN() {
        if (readingArray) return;

        String s = input.getText().toString().trim();
        if (s.isEmpty()) return;

        int n;
        try { n = Integer.parseInt(s); }
        catch (Exception e) { return; }

        if (n <= 0 || n > 50) return;

        N = n;
        buildArrayView(N);

        readingArray = true;
        input.setText("");
        input.setHint("Enter values with spaces (ex: 4 3 2 4 1)");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setImeOptions(EditorInfo.IME_ACTION_NONE);

        txtStep.setText("");
        txtResult.setText("");
        txtResult.setTextColor(Color.parseColor("#111827"));
    }

    private void buildArrayView(int n) {
        indexRow.removeAllViews();
        valueRow.removeAllViews();
        valueCells.clear();
        arrayValues.clear();

        indexRow.setColumnCount(n);
        valueRow.setColumnCount(n);

        int cellW = dp(64);
        int cellH = dp(52);
        int gap = dp(3);

        for (int i = 0; i < n; i++) {
            TextView idx = new TextView(this);
            idx.setText(String.valueOf(i));
            idx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            idx.setTextColor(Color.parseColor("#111827"));
            idx.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams p1 = new GridLayout.LayoutParams();
            p1.width = cellW;
            p1.height = dp(30);
            p1.setMargins(gap, 0, gap, 0);
            idx.setLayoutParams(p1);
            indexRow.addView(idx);

            TextView val = new TextView(this);
            val.setText("");
            val.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            val.setTextColor(Color.WHITE);
            val.setGravity(Gravity.CENTER);
            val.setBackground(bgNormal());

            GridLayout.LayoutParams p2 = new GridLayout.LayoutParams();
            p2.width = cellW;
            p2.height = cellH;
            p2.setMargins(gap, gap, gap, gap);
            val.setLayoutParams(p2);

            valueRow.addView(val);
            valueCells.add(val);
            arrayValues.add(null);
        }
    }

    private void clearCells() {
        for (TextView cell : valueCells) {
            cell.setText("");
            cell.setBackground(bgNormal());
        }
    }

    private void resetArrayValues() {
        for (int i = 0; i < arrayValues.size(); i++) arrayValues.set(i, null);
    }

    private void fillCells(String[] parts) {
        for (int i = 0; i < valueCells.size(); i++) {
            valueCells.get(i).setBackground(bgNormal());
            valueCells.get(i).setText("");
        }

        resetArrayValues();

        int limit = Math.min(parts.length, valueCells.size());
        for (int i = 0; i < limit; i++) {
            String p = parts[i];
            if (!p.matches("-?\\d+")) continue;

            int val = Integer.parseInt(p);
            valueCells.get(i).setText(p);
            arrayValues.set(i, val);
        }
    }

    private boolean isArrayFullyFilled() {
        if (arrayValues.size() < N) return false;
        for (int i = 0; i < N; i++) if (arrayValues.get(i) == null) return false;
        return true;
    }


    private void startLinearManual() {
        isRunning = true;

        input.setEnabled(false);
        inputTarget.setEnabled(false);
        btnEnterN.setEnabled(false);

        txtResult.setText("");
        txtResult.setTextColor(Color.parseColor("#111827"));


        stepIndex = -1;
        maxStep = -1;
        foundIndex = -1;
        finished = false;

        renderLinearState();
        setStepButtonsEnabled(true);
        updateStepText();
    }

    private void doNext() {
        if (!isRunning) return;
        if (finished) return;


        stepIndex++;
        if (stepIndex > maxStep) maxStep = stepIndex;


        if (stepIndex >= N) {
            finished = true;
            stepIndex = N;
            renderLinearState();
            txtStep.setText("Finished");
            txtResult.setTextColor(Color.parseColor("#E11D48"));
            txtResult.setText("Not found");
            setNextEnabled(false);
            return;
        }


        Integer v = arrayValues.get(stepIndex);
        if (v != null && v == target) {
            foundIndex = stepIndex;
            finished = true;
            renderLinearState();
            txtStep.setText("Found!");
            txtResult.setTextColor(Color.parseColor("#16A34A"));
            txtResult.setText("Found at index: " + foundIndex);
            setNextEnabled(false);
            return;
        }


        renderLinearState();
        updateStepText();
    }

    private void doBack() {
        if (!isRunning) return;

        if (stepIndex <= -1) return;
        stepIndex--;


        finished = false;
        foundIndex = -1;


        if (stepIndex >= 0) {
            Integer v = arrayValues.get(stepIndex);
            if (v != null && v == target) {
                foundIndex = stepIndex;
                finished = true;
            }
        }

        renderLinearState();
        updateStepText();
        setNextEnabled(true);
        txtResult.setText("");
    }

    private void updateStepText() {
        if (stepIndex < 0) txtStep.setText("Press Next to start");
        else if (stepIndex >= N) txtStep.setText("Finished");
        else txtStep.setText("Checking index " + stepIndex + "...");
    }

    private void renderLinearState() {
        for (int i = 0; i < N; i++) {
            valueCells.get(i).setBackground(bgNormal());
        }

        // current red
        if (stepIndex >= 0 && stepIndex < N) {
            valueCells.get(stepIndex).setBackground(bgCurrentRed());
        }

        // found green overrides
        if (foundIndex >= 0 && foundIndex < N) {
            valueCells.get(foundIndex).setBackground(bgFoundGreen());
        }
    }

    private void setStepButtonsEnabled(boolean enabled) {
        btnNextStep.setEnabled(enabled);
        btnBackStep.setEnabled(enabled);
    }

    private void setNextEnabled(boolean enabled) {
        btnNextStep.setEnabled(enabled);
    }

    private GradientDrawable bgNormal() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#2F6BFF"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(2), Color.parseColor("#1F49B6"));
        return d;
    }

    private GradientDrawable bgCurrentRed() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#2F6BFF"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(5), Color.parseColor("#FF2D2D"));
        return d;
    }

    private GradientDrawable bgFoundGreen() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#22C55E"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(5), Color.parseColor("#15803D"));
        return d;
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics()
        );
    }
}
