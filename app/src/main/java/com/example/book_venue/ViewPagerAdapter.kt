package com.example.book_venue

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.api.Context
import kotlinx.android.synthetic.main.showimageslayout.view.*

class ViewPagerAdapter(context: android.content.Context, imageUrls: ArrayList<Uri>) :
    PagerAdapter() {

    private var ImageUrls:ArrayList<Uri> = imageUrls

    override fun getCount(): Int {
        return ImageUrls.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=LayoutInflater.from(container.context).inflate(R.layout.showimageslayout,container,false)
        view.image.setImageURI(ImageUrls[position])
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, Object: Any): Boolean {
        return view == Object
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (`object` as RelativeLayout).removeView(container)
    }
}
