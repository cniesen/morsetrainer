package com.niesens.morsetrainer.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.niesens.morsetrainer.MainActivity;
import com.niesens.morsetrainer.R;

import java.io.File;
import java.util.List;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder> {
    private List<File> wordListFiles;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FilePickerAdapter(List<File> wordListFiles) {
        this.wordListFiles = wordListFiles;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FilePickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.row_filepicker, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final File wordListFile = wordListFiles.get(position);
        holder.txtHeader.setText(wordListFile.getName());
        holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo: return file
                Activity activity = (Activity) view.getContext();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("wordListFileName", wordListFile.getName());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return wordListFiles.size();
    }

}