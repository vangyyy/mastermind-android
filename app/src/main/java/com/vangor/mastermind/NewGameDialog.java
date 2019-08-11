package com.vangor.mastermind;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class NewGameDialog extends AppCompatDialogFragment {

	private SeekBar seekBarRows;
	private SeekBar seekBarCols;
	private TextView textViewRows;
	private TextView textViewCols;
	private NewGameListener listener;

	@Override
	public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

		Activity activity = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_layout, null);

		seekBarRows = view.findViewById(R.id.seekBarRows);
		seekBarCols = view.findViewById(R.id.seekBarCols);
		textViewRows = view.findViewById(R.id.textViewRows);
		textViewCols = view.findViewById(R.id.textViewCols);

		//TODO: Change to actual value not fixed size
		String rowsString = getResources().getString(R.string.rows, 8);
		textViewRows.setText(rowsString);

		//TODO: Change to actual value not fixed size
		String colsString = getResources().getString(R.string.columns, 4);
		textViewCols.setText(colsString);

		seekBarRows.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String rowsString = getResources().getString(R.string.rows, progress);
				textViewRows.setText(rowsString);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		seekBarCols.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				String colsString = getResources().getString(R.string.columns, progress);
				textViewCols.setText(colsString);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		builder.setView(view)
				.setTitle(R.string.new_game)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				})
				.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						int rows = seekBarRows.getProgress();
						int cols = seekBarCols.getProgress();
						listener.newGame(rows, cols);
					}
				});

		return builder.create();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		try {
			listener = (NewGameListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + "Must implement NewGameListener!");
		}
	}
}
