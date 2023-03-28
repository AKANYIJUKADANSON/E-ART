package com.example.eart.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eart.R
import com.example.eart.modules.Constants
import com.example.eart.modules.GlideLoader
import com.example.eart.modules.Order
import com.example.eart.ui.activities.OrderDetailsActivity
import com.example.eart.ui.fragments.OrdersFragment
import kotlinx.android.synthetic.main.orders_item_custom.view.*

class OrdersAdapter(
    private val context: Context,
    private val ordersList: ArrayList<Order>,
    private val fragment: OrdersFragment
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflateLayout = LayoutInflater.from(parent.context).inflate(R.layout.orders_item_custom, parent, false)
        return MyViewHolder(inflateLayout)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentOrder = ordersList[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadOrdersPicture( currentOrder.order_image,
                holder.itemView.recyclerView_order_image
            )
            holder.itemView.recyclerView_order_title.text = currentOrder.order_title
            holder.itemView.recyclerView_order_price.text = "$ ${currentOrder.sub_total}"


            // Setting the item click for each item in the recyclerview

            holder.itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                /**
                 * The product_extra_id will help us to be sent to the next activity thru the intent
                 * so that it can be used and assigned to the product_id
                 * so in real sense, the document id(the id representing each product uniquely coz
                 * a product is considered as a document
                 * And thus we use the opportunity that we have the currentProduct and get its document id
                 * to send it to the productDetails activity
                 */
//                intent.putExtra(Constants.PRODUCT_EXTRA_ID, currentOrder.order_id)
//                intent.putExtra(Constants.PRODUCT_EXTRA_OWNER_ID, currentOrder.user_id)
                context.startActivity(intent)
            }
        }
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
}