package com.example.book_venue

import android.graphics.Canvas
import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class TouchHelper(adapter: VenueAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val adapter: VenueAdapter
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.LEFT) {
            adapter.updateData(position)
            adapter.notifyDataSetChanged()
        } else {
            adapter.deleteData(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        RecyclerViewSwipeDecorator.Builder(c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive)
            .addSwipeRightBackgroundColor(Color.RED)
            .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_outline_24)
            .addSwipeLeftBackgroundColor(R.color.purple_500)
            .addSwipeLeftActionIcon(R.drawable.ic_baseline_edit_24)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    init {
        this.adapter = adapter
    }
}
