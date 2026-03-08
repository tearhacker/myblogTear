package tear.conception.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import tear.conception.R;

public class VideoFragment extends Fragment {
    
    private GridView gridView;
    private LinearLayout emptyView;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        gridView = view.findViewById(R.id.grid_view);
        emptyView = view.findViewById(R.id.empty_view);
        
        loadData();
    }

    private void loadData() {
        gridView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
