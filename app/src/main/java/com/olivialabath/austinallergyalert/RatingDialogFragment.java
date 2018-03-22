package com.olivialabath.austinallergyalert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.Calendar;

/**
 * Created by olivialabath on 11/20/17.
 */

public class RatingDialogFragment extends DialogFragment {

    private SeekBar mSeekBar;
    private ImageView mSeekBarImage;
    private EditText mEditText;

    private int rating;
    private String note;

    private final String TAG = "RatingDialogFrag";

    public RatingDialogFragment(){}

    public static RatingDialogFragment newInstance(String dateString, int rating, String note){
        RatingDialogFragment fragment = new RatingDialogFragment();

        Bundle args = new Bundle();
        args.putString("dateString", dateString);
        args.putInt("rating", rating);
        args.putString("note", note);
        fragment.setArguments(args);

        return fragment;
    }

    public interface EditRatingDialogListener {
        void onSave(int rating, String note);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // inflate the dialog and get args
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_rate, null);

        String dateString = getArguments().getString("dateString");
        this.rating = getArguments().getInt("rating");
        this.note = getArguments().getString("note");

        // initialize the seekbar and face image
        mSeekBar = (SeekBar) v.findViewById(R.id.rating_seek_bar);
        mSeekBarImage = (ImageView) v.findViewById(R.id.seekbar_face);
        initSeekBar();

        // initialize the edit text
        mEditText = (EditText) v.findViewById(R.id.rating_edit_text);
        if(!note.equals("")){
            mEditText.setText(note);
        }


        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
            // add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send the rating and note to the parent fragment to be saved
                        note = mEditText.getText().toString();

                        EditRatingDialogListener listener = (EditRatingDialogListener) getTargetFragment();
                        listener.onSave(rating, note);
                        Log.i(TAG, "rating: " + rating + ", note: " + note);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingDialogFragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle(dateString);

        return builder.create();
    }

    private void initSeekBar(){
        mSeekBar.setMax(4);
        mSeekBar.incrementProgressBy(1);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress) {
                    case 0:
                        Log.d(TAG, "setting face to very good");
                        if(rating == -1) rating = 0;
                        mSeekBarImage.setImageResource(R.mipmap.ic_very_good_round);
                        break;
                    case 1:
                        Log.d(TAG, "setting face to good");
                        mSeekBarImage.setImageResource(R.mipmap.ic_good_round);
                        break;
                    case 2:
                        Log.d(TAG, "setting face to neutral");
                        mSeekBarImage.setImageResource(R.mipmap.ic_neutral_round);
                        break;
                    case 3:
                        Log.d(TAG, "setting face to bad");
                        mSeekBarImage.setImageResource(R.mipmap.ic_bad_round);
                        break;
                    case 4:
                        Log.d(TAG, "setting face to very bad");
                        mSeekBarImage.setImageResource(R.mipmap.ic_very_bad_round);
                        break;
                    default:
                        Log.d(TAG, "setting face to very good");
                        mSeekBarImage.setImageResource(R.mipmap.ic_very_good_round);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rating = seekBar.getProgress();
            }
        });


        mSeekBar.setProgress(0);
        if(rating > -1){
            mSeekBar.setProgress(rating);
        }

    }
}
