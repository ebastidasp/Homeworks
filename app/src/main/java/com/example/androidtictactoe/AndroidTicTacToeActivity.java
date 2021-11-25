package com.example.androidtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.widget.*;
import android.view.Menu;
import android.view.MenuItem;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;

    private boolean mGameOver;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private RelativeLayout mCounterWins;
    private TextView mInfoWins;
    private int mHumanWins;
    private int mComputerWins;
    private int mTies;
    private int mStart;
    private int turn;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.main);
        mGame = new TicTacToeGame();
        int currDiff = mPrefs.getInt("currDiff", 2);
        if(currDiff == 0)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if(currDiff == 1)
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mGameOver = false;
        mStart = 0;
        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoWins = (TextView) findViewById(R.id.infowins);
        mCounterWins = (RelativeLayout) findViewById(R.id.counterwins);
        mHumanWins = 0;
        mComputerWins = 0;
        mTies = 0;

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();

        if (savedInstanceState == null) {
            startNewGame();
        }
        else {
// Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mHumanWins = savedInstanceState.getInt("mHumanWins");
            mComputerWins = savedInstanceState.getInt("mComputerWins");
            mTies = savedInstanceState.getInt("mTies");
        }


        // Restore the scores
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        displayScores();

    }

    private void displayScores() {
        mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_ABOUT = 1;
    static final int DIALOG_RESET = 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT);
                return true;
            case R.id.reset:
                showDialog(DIALOG_RESET);
                return true;
        }
        return false;
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clear_board();
        mGameOver = false;

        mBoardView.invalidate();
        // Who goes first?
        if(mStart == 0)
            mInfoTextView.setText(R.string.first_human);
        else {
            mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mComputerMediaPlayer.start();
        }
        mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
    } // End of startNewGame

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true;
                    mTies++;
                    mStart++;
                    mStart = mStart % 2;
                    mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver = true;
                    mHumanWins++;
                    mStart++;
                    mStart = mStart % 2;
                    mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mGameOver = true;
                    mComputerWins++;
                    mStart++;
                    mStart = mStart % 2;
                    mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                }
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

                int selected = mPrefs.getInt("currDiff", 2);


// TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
// selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
// TODO: Set the diff level of mGame based on which item was selected.
                                TicTacToeGame.DifficultyLevel diff;
                                if(item == 0){
                                    diff = TicTacToeGame.DifficultyLevel.Easy;
                                }
                                else if(item == 1){
                                    diff = TicTacToeGame.DifficultyLevel.Harder;
                                }
                                else
                                    diff = TicTacToeGame.DifficultyLevel.Expert;
                                mGame.setDifficultyLevel(diff);
// Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],

                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_ABOUT:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
            case DIALOG_RESET:
                mHumanWins = 0;
                mComputerWins = 0;
                mTies = 0;
                displayScores();
                break;
        }
        return dialog;
    }

    private void DisableBoard(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }

    private void EnableBoard(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            if(mBoardButtons[i].getText().length() == 0)
                mBoardButtons[i].setEnabled(true);
        }
    }

    private boolean setMove(char player, int location) {
        if(mGame.canMove(location)){
            mGame.setMove(player, location);
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
// Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            int winner;
            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
// If no winner yet, let the computer make a move
                mHumanMediaPlayer.start();
                winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mComputerMediaPlayer.start();
                    winner = mGame.checkForWinner();
                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1){
                        mInfoTextView.setText(R.string.result_tie);
                        mGameOver = true;
                        mTies++;
                        mStart++;
                        mStart = mStart % 2;
                        mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                    }
                    else {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        mGameOver = true;
                        mComputerWins++;
                        mStart++;
                        mStart = mStart % 2;
                        mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                    }
                }
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true;
                    mTies++;
                    mStart++;
                    mStart = mStart % 2;
                    mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver = true;
                    mHumanWins++;
                    mStart++;
                    mStart = mStart % 2;
                    mInfoWins.setText("Human:"+String.valueOf(mHumanWins)+" Computer:"+String.valueOf(mComputerWins)+" Ties:"+String.valueOf(mTies));
                }
            }
// If no winner yet, let the computer make a move

// So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanturn);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computerturn);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mComputerWins", Integer.valueOf(mComputerWins));
        outState.putInt("mTies", Integer.valueOf(mTies));
        outState.putCharSequence("info", mInfoTextView.getText());
    }

    @Override
    protected void onStop() {
        super.onStop();
// Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mStart", mStart);
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        int currDiff;
        TicTacToeGame.DifficultyLevel diff = mGame.getmDifficultyLevel();
        if(diff == TicTacToeGame.DifficultyLevel.Easy)
            currDiff = 0;
        else if(diff == TicTacToeGame.DifficultyLevel.Harder)
            currDiff = 1;
        else
            currDiff = 2;
        ed.putInt("currDiff", currDiff);
        ed.commit();
    }

}

