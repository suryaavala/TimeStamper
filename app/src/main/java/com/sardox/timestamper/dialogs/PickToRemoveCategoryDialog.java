package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.Utils;

import java.util.List;

public class PickToRemoveCategoryDialog {

    public PickToRemoveCategoryDialog(Context context, final List<Category> allCategories, final Consumer<Category> categoryToRemove) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        List<String> quickCategories = Utils.getListOfCategories(allCategories);
        b.setTitle(R.string.select_cat_to_remove);
        b.setItems(quickCategories.toArray(new String[quickCategories.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int clickedPos) {
                dialog.dismiss();
                categoryToRemove.accept(allCategories.get(clickedPos));
            }
        });
        b.show();
    }
}
