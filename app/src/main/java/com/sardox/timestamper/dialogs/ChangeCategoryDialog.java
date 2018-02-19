package com.sardox.timestamper.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.Utils;

import java.util.List;

public class ChangeCategoryDialog {

    public ChangeCategoryDialog(Context context,  final List<Category> allCategories, final Consumer<JetUUID> newCategoryId){
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Move to");
        List<String> quickCategories = Utils.getListOfCategories(allCategories);
        b.setItems(quickCategories.toArray(new String[quickCategories.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int clickedPos) {
                dialog.dismiss();
                JetUUID newCategory = allCategories.get(clickedPos).getCategoryID();
                newCategoryId.accept(newCategory);
            }
        });
        b.show();
    }


}
