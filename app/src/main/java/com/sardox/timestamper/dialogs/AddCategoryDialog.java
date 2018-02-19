package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.recyclerview.IconAdapter;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.List;

public class AddCategoryDialog {

    public AddCategoryDialog(Context context, List<TimestampIcon> icons, final Consumer<Category> onCategoryCreated) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.new_category, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input_cat);
        final IconAdapter iconPicker = new IconAdapter(icons, new Consumer<TimestampIcon>() {
            @Override
            public void accept(TimestampIcon icon) {
                input.setText(icon.getDescription());
            }
        }, context);

        RecyclerView iconRecycler = (RecyclerView) viewInflated.findViewById(R.id.recyclerView_icon);
        iconRecycler.setAdapter(iconPicker);
        iconRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(context);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        iconRecycler.setLayoutManager(linearLayoutManagerCat);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int lastAdapterPosition = iconPicker.getLastSelected(); // which icon was selected
                Category newCategory = new Category(input.getText().toString(), JetUUID.randomUUID(), lastAdapterPosition);
                onCategoryCreated.accept(newCategory);
                iconPicker.destroy();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iconPicker.destroy();
                dialog.cancel();
            }
        });
        builder.show();
    }
}
