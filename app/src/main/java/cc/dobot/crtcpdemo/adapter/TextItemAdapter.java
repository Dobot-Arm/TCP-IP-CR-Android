package cc.dobot.crtcpdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import cc.dobot.crtcpdemo.R;

public class TextItemAdapter extends RecyclerView.Adapter<TextItemAdapter.TextItemHolder> {

    private List<String> dataList;

    public TextItemAdapter(List<String> dataList) {
        if (dataList==null)
            this.dataList=new LinkedList<>();
        else
            this.dataList = dataList;
    }

    public List<String> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public TextItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TextItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_text_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextItemHolder holder, int position) {
        holder.mText.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    class TextItemHolder extends RecyclerView.ViewHolder{
        TextView mText;
    public TextItemHolder(@NonNull View itemView) {
        super(itemView);
        mText=itemView.findViewById(R.id.content_text_item);
    }
}

}
