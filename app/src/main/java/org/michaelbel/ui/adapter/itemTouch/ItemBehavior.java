package org.michaelbel.ui.adapter.itemTouch;

/**
 * Date: 27 APR 2018
 * Time: 23:22 MSK
 *
 * @author Michael Bel
 */

public interface ItemBehavior {

    void onItemSwiped(int position);

    boolean onItemMoved(int fromPosition, int toPosition);
}