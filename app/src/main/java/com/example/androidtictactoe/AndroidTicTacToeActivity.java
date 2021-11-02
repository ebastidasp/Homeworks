package com.example.androidtictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoWins = (TextView) findViewById(R.id.infowins);
        mCounterWins = (RelativeLayout) findViewById(R.id.counterwins);
        mHumanWins = 0;
        mComputerWins = 0;
        mTies = 0;
        mGame = new TicTacToeGame();
        mGameOver = false;
        mStart = 0;

        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clear_board();
        mGameOver = false;
        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Who goes first?
        if(mStart == 0)
            mInfoTextView.setText(R.string.first_human);
        else {
            mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
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

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }


}

