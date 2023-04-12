package hcmute.edu.vn.mp3app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.model.item_recycleview;

public class ViewRecycleAdapter extends RecyclerView.Adapter<ViewRecycleAdapter.ItemViewHolded> {

    private List<item_recycleview> mListSong;
    public void setData(List<item_recycleview> list){
        this.mListSong = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ItemViewHolded onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.iterm_recycleview, parent,false);
        return new ItemViewHolded(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolded holder, int position) {
        item_recycleview Song = mListSong.get(position);
        if(Song == null ){
            return;
        }
        holder.img_song.setImageResource(Song.getResourceId());
        holder.txt_name.setText(Song.getName());
        holder.txt_author.setText((Song.getAuthor()));
    }

    @Override
    public int getItemCount() {
        if (mListSong!=null){
            mListSong.size();
        }
        return 0;
    }

    public class ItemViewHolded extends RecyclerView.ViewHolder{

        private ImageView img_song;
        private TextView txt_name;
        private TextView txt_author;

        public ItemViewHolded(@NonNull View itemView) {
            super(itemView);
            img_song = itemView.findViewById(R.id.img_item);
            txt_name = itemView.findViewById(R.id.tv_name);
            txt_author = itemView.findViewById(R.id.tv_author);
        }
    }

}
