package org.michaelbel.ui.adapter;

public interface ItemBehavior {

    void onItemSwiped(int position);

    boolean onItemMoved(int fromPosition, int toPosition);
}