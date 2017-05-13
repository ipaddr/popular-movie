package id.ipaddr.popularmovie;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ulfiaizzati on 10/27/16.
 */

public class ViewHolder {

    static class ViewHolderTrailer extends RecyclerView.ViewHolder{
        TextView tv;
        public ViewHolderTrailer(View view){
            super(view);
            tv = (TextView)view.findViewById(android.R.id.text1);
        }
    }

    static class ViewHolderReview extends RecyclerView.ViewHolder{
        TextView tv1;
        TextView tv2;
        public ViewHolderReview(View view){
            super(view);
            tv1 = (TextView)view.findViewById(android.R.id.text1);
            tv2 = (TextView)view.findViewById(android.R.id.text2);
        }
    }

}
