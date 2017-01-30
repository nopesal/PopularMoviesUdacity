package com.example.nopesal.projectmoviesudacity.itemdecorations;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Nico on 30/01/2017.
 */

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private int totalColumns;
    private boolean includeEdge;

    public ItemSpacingDecoration(int spacing, int columns, boolean includeEdge) {
        this.spacing = spacing;
        this.totalColumns = columns;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % totalColumns; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / totalColumns; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / totalColumns; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < totalColumns) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / totalColumns; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / totalColumns; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= totalColumns) {
                outRect.top = spacing; // item top
            }
        }
    }
}
