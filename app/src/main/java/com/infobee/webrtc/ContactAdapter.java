package com.infobee.webrtc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

private List<Contact> contacts;
AdapterListener adapterListener;

public ContactAdapter(AdapterListener adapterListener,List<Contact> contacts) {
    this.adapterListener = adapterListener;
        this.contacts = contacts;
        }

@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.textName.setText(contact.getName());
        holder.buttonCall.setOnClickListener(v -> {

                adapterListener.onClickCall(position);

        });
}

@Override
public int getItemCount() {
        return contacts.size();
        }


//    private void initiateVideoCall(String phoneNumber) {
//        // Replace this with your own code to initiate a video call with the selected phone number
//
//        VideoCallLibrary.startVideoCall(MainActivity.this, phoneNumber);
//    }

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView textName;
    Button buttonCall;

    public ViewHolder(View itemView) {
        super(itemView);
        textName = itemView.findViewById(R.id.textName);
        buttonCall = itemView.findViewById(R.id.buttonCall);
    }
}



}