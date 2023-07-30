package hcmute.edu.vn.mp3app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.activity.AddSongActivity;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class PlaylistRVAdapter extends RecyclerView.Adapter<PlaylistRVAdapter.ViewHolder>{

    public static ArrayList<Playlist> playlistArrayList;
    private Context context;
    private ClickListener mListener;
    private Playlist playlist;

    public static int currentPlaylistIndex;
    
    private ImageView remove_playlist;
    public interface ClickListener {
        void onItemClick(int position);
    }
    public PlaylistRVAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
    }

    public PlaylistRVAdapter() {
    }

    public PlaylistRVAdapter(ClickListener  listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public PlaylistRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.playlist = playlistArrayList.get(position);
        holder.et_playlistName_rv.setText(holder.playlist.getName_playlist());

        // Set the playlist object for the ViewHolder
        holder.setPlaylist(holder.playlist);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPlaylistIndex = position;
                PlaylistActivity.currentPlaylistIndex = holder.playlist.getPlaylist_index();
                Intent intent = new Intent(context, PlaylistActivity.class);
                context.startActivity(intent);
            }
        });
        remove_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Playlist/user"+Global.GlobalUserID).child(String.valueOf(PlaylistActivity.currentPlaylistIndex));
                playlistArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, playlistArrayList.size());

                mDatabaseRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Child node deleted successfully
                                Toast.makeText(context, "Playlist removed", Toast.LENGTH_SHORT).show();
//                                // Shift the remaining playlists' indexes
//                                DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Playlist/user"+ Global.GlobalUserID);
//                                reff.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        int count = (int) dataSnapshot.getChildrenCount();
//
//                                        for (int i = 1; i <= count; i++) {
//                                            DataSnapshot playlistSnapshot = dataSnapshot.child(String.valueOf(i));
//                                            Playlist playlist = playlistSnapshot.getValue(Playlist.class);
//                                            reff.child(String.valueOf(i - 1)).setValue(playlist);
//                                            reff.child(String.valueOf(i)).removeValue();
//                                        }
//
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        // Handle any errors that may occur
//                                    }
//                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete child node
                                Toast.makeText(context, "Delete failed!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }
    public int getItemCount() {
        if(playlistArrayList != null){
            return playlistArrayList.size();
        }
        return 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private Playlist playlist;
        private EditText et_playlistName_rv;
        private ImageView img_done, img_cancel;
        public void setPlaylist(Playlist playList) {
            this.playlist = playList;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            et_playlistName_rv = itemView.findViewById(R.id.et_playlistName_rv);
            remove_playlist = itemView.findViewById(R.id.remove_playlist);
            img_done = itemView.findViewById(R.id.img_done);
            img_cancel = itemView.findViewById(R.id.img_cancel);

            img_done.setVisibility(View.GONE);
            img_cancel.setVisibility(View.GONE);

            int maxLength = 5; // Maximum length of characters allowed

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(maxLength);

            et_playlistName_rv.setFilters(filters);

            // Attach an OnClickListener to the EditText
            et_playlistName_rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hide the TextView and show the EditText
                    img_done.setVisibility(View.VISIBLE);
                    img_cancel.setVisibility(View.VISIBLE);

                    // Enable editing when clicked
                    et_playlistName_rv.setFocusableInTouchMode(true);
                    et_playlistName_rv.setFocusable(true);
                }
            });

            // Attach a focus change listener to the EditText
//            et_playlistName_rv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//                        // Show the edit controls when the EditText gains focus
//                        img_done.setVisibility(View.VISIBLE);
//                        img_cancel.setVisibility(View.VISIBLE);
//                    } else {
//                        // Hide the edit controls when the EditText loses focus
//                        img_done.setVisibility(View.GONE);
//                        img_cancel.setVisibility(View.GONE);
//                    }
//                }
//            });


            img_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playlist.setName_playlist(String.valueOf(et_playlistName_rv.getText()));

                    DatabaseReference reff2;
                    reff2 = FirebaseDatabase.getInstance().getReference().child("Playlist/user"+ Global.GlobalUserID).child(String.valueOf(playlist.getPlaylist_index()));
                    reff2.setValue(playlist);

                    img_done.setVisibility(View.GONE);
                    img_cancel.setVisibility(View.GONE);


                }
            });

            img_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    img_done.setVisibility(View.GONE);
                    img_cancel.setVisibility(View.GONE);
                    et_playlistName_rv.setText(playlist.getName_playlist());
                    et_playlistName_rv.clearFocus();
                }
            });

        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(context, Mp3Service.class);
        intent.putExtra("action_music", action);
        context.startService(intent);
    }

}
