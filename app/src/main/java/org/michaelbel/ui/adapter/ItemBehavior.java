package org.michaelbel.ui.adapter;

@SuppressWarnings("all")
public interface ItemBehavior {

    void onItemSwiped(int position);

    boolean onItemMoved(int fromPosition, int toPosition);
}