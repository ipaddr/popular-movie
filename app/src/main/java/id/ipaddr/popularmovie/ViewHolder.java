package id.ipaddr.popularmovie;

import android.view.View;
import android.widget.TextView;

/**
 * Created by ulfiaizzati on 10/27/16.
 */

public class ViewHolder {

    static class ViewHolderTrailer{
        TextView tv;
        public ViewHolderTrailer(View view){
            tv = (TextView)view.findViewById(R.id.trailer);
        }
    }

    static class ViewHolderReview{
        TextView tv1;
        TextView tv2;
        public ViewHolderReview(View view){
            tv1 = (TextView)view.findViewById(android.R.id.text1);
            tv2 = (TextView)view.findViewById(android.R.id.text2);
        }
    }

}
