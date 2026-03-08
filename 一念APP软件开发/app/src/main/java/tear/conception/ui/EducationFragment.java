package tear.conception.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import tear.conception.R;

public class EducationFragment extends Fragment {
    
    private ListView listView;
    private LinearLayout emptyView;
    private Button btnStartDiscussion;

    public static EducationFragment newInstance() {
        return new EducationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_education, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        listView = view.findViewById(R.id.list_view);
        emptyView = view.findViewById(R.id.empty_view);
        btnStartDiscussion = view.findViewById(R.id.btn_start_discussion);
        
        btnStartDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDiscussionClicked();
            }
        });
        
        loadData();
    }

    private void onStartDiscussionClicked() {
    }

    private void loadData() {
        listView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }
}
