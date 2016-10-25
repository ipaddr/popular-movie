
package id.ipaddr.popularmovie;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Video {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideoResult> videoResults = new ArrayList<VideoResult>();

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The videoResults
     */
    public List<VideoResult> getVideoResults() {
        return videoResults;
    }

    /**
     * 
     * @param videoResults
     *     The videoResults
     */
    public void setVideoResults(List<VideoResult> videoResults) {
        this.videoResults = videoResults;
    }

}
