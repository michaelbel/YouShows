package org.michaelbel.ui.adapter.itemTouch;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.michaelbel.material.widget.Holder;

/**
 * Date: 27 APR 2018
 * Time: 23:22 MSK
 *
 * @author Michael Bel
 */

public class ItemTouchHelperSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private ItemBehavior listener;

    public ItemTouchHelperSimpleCallback(ItemBehavior listener, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            /*if (viewHolder instanceof BottomCellHolder) {
                return 0;
            }*/

            //final int dragFlags = 0;
            //final int swipeFlags = 0;
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        /*if (viewHolder instanceof Holder2) {
            return false;
        }*/

        listener.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof Holder) {
            listener.onItemSwiped(viewHolder.getAdapterPosition());
        }
    }
}