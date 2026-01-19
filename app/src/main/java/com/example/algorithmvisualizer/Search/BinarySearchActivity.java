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
import java.util.Collections;

public class BinarySearchActivity extends AppCompatActivity {

    private EditText input;
    private GridLayout indexRow;
    private GridLayout valueRow;

    private EditText inputTarget;
    private Button btnStartSearch;
    private Button btnEnterN;
    private TextView txtResult;
    private TextView txtStep;
    private TextView txtRange;


    private Button btnNextStep;
    private Button btnBackStep;


    private Button btnAutoSort;

    private int N = -1;
    private final ArrayList<TextView> valueCells = new ArrayList<>();
    private final ArrayList<Integer> arrayValues = new ArrayList<>();

    private boolean readingArray = false;
    private boolean isRunning = false;


    private static class State {
        int left, right, mid;
        State(int l, int r, int m) { left = l; right = r; mid = m; }
    }
    private final ArrayList<State> history = new ArrayList<>();
    private int pointer = -1;
    private int target = 0;
    private boolean finished = false;
    private int foundIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_search);

        input = findViewById(R.id.inputN);
        indexRow = findViewById(R.id.indexRow);
        valueRow = findViewById(R.id.valueRow);

        inputTarget = findViewById(R.id.inputTarget);
        btnStartSearch = findViewById(R.id.btnStartSearch);
        btnEnterN = findViewById(R.id.btnEnterN);
        txtResult = findViewById(R.id.txtResult);
        txtStep = findViewById(R.id.txtStep);
        txtRange = findViewById(R.id.txtRange);

        btnNextStep = findViewById(R.id.btnNextStep);
        btnBackStep = findViewById(R.id.btnBackStep);
        btnAutoSort = findViewById(R.id.btnAutoSort);

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

        btnAutoSort.setOnClickListener(v -> {
            if (isRunning) return;
            if (N <= 0 || valueCells.isEmpty()) return;
            if (!isArrayFullyFilled()) {
                txtResult.setTextColor(Color.parseColor("#E11D48"));
                txtResult.setText("Fill all N values first.");
                return;
            }
            autoSortArray();
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

            if (!isSortedAscending()) {
                txtResult.setTextColor(Color.parseColor("#E11D48"));
                txtResult.setText("Array must be sorted for Binary Search.\nPress Auto Sort.");
                return;
            }

            startBinaryManual();
        });

        btnNextStep.setOnClickListener(v -> doNext());
        btnBackStep.setOnClickListener(v -> doBack());
    }

    private void setupInputForN() {
        readingArray = false;
        input.setText("");
        input.setHint("Enter N (array size) then press Create");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

        txtStep.setText("");
        txtRange.setText("");
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
        input.setHint("Enter values with spaces (Auto Sort available)");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setImeOptions(EditorInfo.IME_ACTION_NONE);

        txtStep.setText("");
        txtRange.setText("");
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

    private boolean isSortedAscending() {
        for (int i = 1; i < N; i++) {
            Integer a = arrayValues.get(i - 1);
            Integer b = arrayValues.get(i);
            if (a == null || b == null) return false;
            if (a > b) return false;
        }
        return true;
    }

    private void autoSortArray() {
        ArrayList<Integer> tmp = new ArrayList<>();
        for (int i = 0; i < N; i++) tmp.add(arrayValues.get(i));

        Collections.sort(tmp);

        for (int i = 0; i < N; i++) {
            arrayValues.set(i, tmp.get(i));
            valueCells.get(i).setText(String.valueOf(tmp.get(i)));
            valueCells.get(i).setBackground(bgNormal());
        }

        txtStep.setText("");
        txtRange.setText("");
        txtResult.setTextColor(Color.parseColor("#16A34A"));
        txtResult.setText("Array sorted automatically.");
    }

    private void startBinaryManual() {
        isRunning = true;
        finished = false;
        foundIndex = -1;

        input.setEnabled(false);
        inputTarget.setEnabled(false);
        btnEnterN.setEnabled(false);
        btnAutoSort.setEnabled(false);

        txtResult.setText("");
        txtResult.setTextColor(Color.parseColor("#111827"));

        history.clear();
        pointer = -1;


        State s = new State(0, N - 1, -1);
        history.add(s);
        pointer = 0;

        renderState(history.get(pointer));
        txtStep.setText("Press Next to compute mid");
        setStepButtonsEnabled(true);
        btnNextStep.setEnabled(true);
        btnBackStep.setEnabled(false);
    }

    private void doNext() {
        if (!isRunning) return;
        if (finished) return;

        State cur = history.get(pointer);


        if (cur.left > cur.right) {
            finishedNotFound();
            return;
        }


        int mid = cur.left + (cur.right - cur.left) / 2;
        Integer midVal = arrayValues.get(mid);


        State shown = new State(cur.left, cur.right, mid);


        if (pointer == history.size() - 1) {
            history.add(shown);
            pointer++;
        } else {
            pointer++;
            history.set(pointer, shown);
            while (history.size() > pointer + 1) history.remove(history.size() - 1);
        }

        renderState(history.get(pointer));
        txtStep.setText("Checking mid = " + mid);

        if (midVal != null && midVal == target) {
            finished = true;
            foundIndex = mid;
            valueCells.get(mid).setBackground(bgFoundGreen());
            txtStep.setText("Found!");
            txtResult.setTextColor(Color.parseColor("#16A34A"));
            txtResult.setText("Found at index: " + mid);
            btnNextStep.setEnabled(false);
            btnBackStep.setEnabled(true);
            return;
        }

        if (midVal == null) {
            finished = true;
            txtResult.setTextColor(Color.parseColor("#E11D48"));
            txtResult.setText("Invalid array value at mid.");
            btnNextStep.setEnabled(false);
            btnBackStep.setEnabled(true);
            return;
        }

        int newLeft = cur.left;
        int newRight = cur.right;

        if (target < midVal) newRight = mid - 1;
        else newLeft = mid + 1;

        State nextRange = new State(newLeft, newRight, -1);
        history.add(nextRange);
        pointer++;

        renderState(history.get(pointer));
        txtStep.setText("Range updated. Press Next");
        updateRangeText(history.get(pointer));

        btnBackStep.setEnabled(pointer > 0);
    }

    private void doBack() {
        if (!isRunning) return;
        if (pointer <= 0) return;

        pointer--;

        finished = false;
        foundIndex = -1;
        txtResult.setText("");
        txtResult.setTextColor(Color.parseColor("#111827"));

        renderState(history.get(pointer));
        btnNextStep.setEnabled(true);
        btnBackStep.setEnabled(pointer > 0);

        if (history.get(pointer).mid == -1) txtStep.setText("Range view (Back). Press Next");
        else txtStep.setText("Checking mid = " + history.get(pointer).mid + " (Back)");
    }

    private void finishedNotFound() {
        finished = true;
        txtStep.setText("Finished");
        txtRange.setText("");
        txtResult.setTextColor(Color.parseColor("#E11D48"));
        txtResult.setText("Not found");
        btnNextStep.setEnabled(false);
        btnBackStep.setEnabled(true);
    }

    private void renderState(State s) {

        for (int i = 0; i < N; i++) {
            if (i < s.left || i > s.right) valueCells.get(i).setBackground(bgDimmedOutside());
            else valueCells.get(i).setBackground(bgInRange());
        }


        if (s.mid >= 0 && s.mid < N) valueCells.get(s.mid).setBackground(bgMidRed());

        updateRangeText(s);
    }

    private void updateRangeText(State s) {
        if (s.left <= s.right) txtRange.setText("Search range: [" + s.left + " .. " + s.right + "]");
        else txtRange.setText("Search range: empty");
    }

    private void setStepButtonsEnabled(boolean enabled) {
        btnNextStep.setEnabled(enabled);
        btnBackStep.setEnabled(enabled);
    }



    private GradientDrawable bgNormal() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#2F6BFF"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(2), Color.parseColor("#1F49B6"));
        return d;
    }

    private GradientDrawable bgInRange() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#2F6BFF"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(5), Color.parseColor("#7C3AED"));
        return d;
    }

    private GradientDrawable bgDimmedOutside() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#94A3B8"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(2), Color.parseColor("#64748B"));
        return d;
    }

    private GradientDrawable bgMidRed() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor("#2F6BFF"));
        d.setCornerRadius(dp(10));
        d.setStroke(dp(6), Color.parseColor("#FF2D2D"));
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
