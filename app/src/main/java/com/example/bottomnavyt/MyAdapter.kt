package com.example.bottomnavyt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private  val userList:ArrayList<User>): RecyclerView.Adapter<MyAdapter.CustomViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun getItemCount(): Int {
        return userList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.card_view,parent,false)

        return CustomViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val user :User=userList[position]
        holder.date.text=user.date
        holder.reason.text=user.reason
        holder.amount.text= user.amount.toString()


        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, user )
            }
        }

    }
    public class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date: TextView =itemView.findViewById(R.id.first_field)
        val reason: TextView =itemView.findViewById(R.id.last_field)
        val amount: TextView =itemView.findViewById(R.id.birth_field)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: User)
    }


}