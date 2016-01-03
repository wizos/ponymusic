package me.wcy.ponymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.callback.JsonCallback;
import me.wcy.ponymusic.model.JOnlineMusic;
import me.wcy.ponymusic.model.JOnlineMusicList;
import me.wcy.ponymusic.model.MusicListInfo;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * 歌单列表适配器
 * Created by wcy on 2015/12/19.
 */
public class SongListAdapter extends BaseAdapter {
    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;
    private Context mContext;
    private List<MusicListInfo> mData;

    public SongListAdapter(Context context, List<MusicListInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_MUSIC_LIST;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType().equals("*")) {
            return TYPE_PROFILE;
        } else {
            return TYPE_MUSIC_LIST;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderProfile holderProfile;
        ViewHolderMusicList holderMusicList;
        MusicListInfo musicListInfo = mData.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_song_list_item_profile, parent, false);
                    holderProfile = new ViewHolderProfile();
                    holderProfile.tvProfile = (TextView) convertView.findViewById(R.id.tv_profile);
                    convertView.setTag(holderProfile);
                } else {
                    holderProfile = (ViewHolderProfile) convertView.getTag();
                }
                holderProfile.tvProfile.setText(musicListInfo.getTitle());
                break;
            case TYPE_MUSIC_LIST:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_song_list_item, parent, false);
                    holderMusicList = new ViewHolderMusicList();
                    holderMusicList.ivCover = (ImageView) convertView.findViewById(R.id.iv_cover);
                    holderMusicList.tvMusic1 = (TextView) convertView.findViewById(R.id.tv_music_1);
                    holderMusicList.tvMusic2 = (TextView) convertView.findViewById(R.id.tv_music_2);
                    holderMusicList.tvMusic3 = (TextView) convertView.findViewById(R.id.tv_music_3);
                    convertView.setTag(holderMusicList);
                } else {
                    holderMusicList = (ViewHolderMusicList) convertView.getTag();
                }
                getMusicListInfo(musicListInfo, holderMusicList);
                break;
        }
        return convertView;
    }

    private void getMusicListInfo(final MusicListInfo musicListInfo, final ViewHolderMusicList holderMusicList) {
        if (musicListInfo.getCoverUrl() == null) {
            OkHttpUtils.get().url(Constants.BASE_URL)
                    .addParams("method", Constants.METHOD_GET_MUSIC_LIST)
                    .addParams("type", musicListInfo.getType())
                    .addParams("size", "3")
                    .build()
                    .execute(new JsonCallback<JOnlineMusicList>(JOnlineMusicList.class) {
                        @Override
                        public void onResponse(JOnlineMusicList response) {
                            JOnlineMusic[] jOnlineMusics = response.getSong_list();
                            musicListInfo.setCoverUrl(response.getBillboard().getPic_s260());
                            if (jOnlineMusics.length >= 1) {
                                musicListInfo.setMusic1("1." + jOnlineMusics[0].getTitle() + " - " + jOnlineMusics[0].getArtist_name());
                            } else {
                                musicListInfo.setMusic1("");
                            }
                            if (jOnlineMusics.length >= 2) {
                                musicListInfo.setMusic2("2." + jOnlineMusics[1].getTitle() + " - " + jOnlineMusics[1].getArtist_name());
                            } else {
                                musicListInfo.setMusic2("");
                            }
                            if (jOnlineMusics.length >= 3) {
                                musicListInfo.setMusic3("3." + jOnlineMusics[2].getTitle() + " - " + jOnlineMusics[2].getArtist_name());
                            } else {
                                musicListInfo.setMusic3("");
                            }
                            ImageLoader.getInstance().displayImage(musicListInfo.getCoverUrl(), holderMusicList.ivCover, MusicUtils.getDefaultDisplayImageOptions());
                            holderMusicList.tvMusic1.setText(musicListInfo.getMusic1());
                            holderMusicList.tvMusic2.setText(musicListInfo.getMusic2());
                            holderMusicList.tvMusic3.setText(musicListInfo.getMusic3());
                        }

                        @Override
                        public void onError(Request request, Exception e) {
                        }
                    });
        } else {
            ImageLoader.getInstance().displayImage(musicListInfo.getCoverUrl(), holderMusicList.ivCover, MusicUtils.getDefaultDisplayImageOptions());
            holderMusicList.tvMusic1.setText(musicListInfo.getMusic1());
            holderMusicList.tvMusic2.setText(musicListInfo.getMusic2());
            holderMusicList.tvMusic3.setText(musicListInfo.getMusic3());
        }
    }

    class ViewHolderProfile {
        TextView tvProfile;
    }

    class ViewHolderMusicList {
        ImageView ivCover;
        TextView tvMusic1;
        TextView tvMusic2;
        TextView tvMusic3;
    }
}
