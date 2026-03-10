package tear.conception.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tear.conception.DiscussionActivity;
import tear.conception.R;
import tear.conception.model.Discussion;
import tear.conception.module.BlogApiService;
import tear.conception.ui.adapter.DiscussionListAdapter;

public class EducationFragment extends Fragment {
    
    private ListView listView;
    private LinearLayout emptyView;
    private Button btnStartDiscussion;
    private ProgressBar progressBar;
    private TextView tvEmptyText;
    private LinearLayout loadMoreLayout;
    private TextView tvLoadMore;
    private ProgressBar pbLoadMore;

    private DiscussionListAdapter adapter;
    private List<Discussion> discussionList = new ArrayList<>();
    
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMore = true;

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
        
        addLoadMoreFooter();
        
        adapter = new DiscussionListAdapter(getActivity(), discussionList);
        adapter.setOnDiscussionItemClickListener(new DiscussionListAdapter.OnDiscussionItemClickListener() {
            @Override
            public void onDiscussionClick(Discussion discussion, int position) {
                openDiscussionDetail(discussion);
            }
        });
        listView.setAdapter(adapter);
        
        btnStartDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartDiscussionClicked();
            }
        });
        
        loadDiscussionList();
    }

    private void addLoadMoreFooter() {
        loadMoreLayout = new LinearLayout(getActivity());
        loadMoreLayout.setOrientation(LinearLayout.HORIZONTAL);
        loadMoreLayout.setGravity(android.view.Gravity.CENTER);
        loadMoreLayout.setPadding(0, 20, 0, 40);
        
        pbLoadMore = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleSmall);
        pbLoadMore.setVisibility(View.GONE);
        
        tvLoadMore = new TextView(getActivity());
        tvLoadMore.setText("加载更多");
        tvLoadMore.setTextSize(14);
        tvLoadMore.setPadding(10, 0, 0, 0);
        
        loadMoreLayout.addView(pbLoadMore);
        loadMoreLayout.addView(tvLoadMore);
        
        loadMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading && hasMore) {
                    loadMoreDiscussionList();
                }
            }
        });
        
        listView.addFooterView(loadMoreLayout);
    }

    private void onStartDiscussionClicked() {
        Intent intent = new Intent(getActivity(), DiscussionActivity.class);
        startActivity(intent);
    }

    private void loadDiscussionList() {
        if (isLoading) return;
        
        isLoading = true;
        showLoading();
        
        BlogApiService.getDiscussionList(currentPage, pageSize, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (getActivity() == null) return;
                
                isLoading = false;
                hideLoading();
                
                try {
                    int code = response.optInt("code", -1);
                    if (code == 200) {
                        JSONArray listArray = response.optJSONArray("data");
                        if (listArray != null) {
                            discussionList.clear();
                            for (int i = 0; i < listArray.length(); i++) {
                                JSONObject discussionJson = listArray.optJSONObject(i);
                                if (discussionJson != null) {
                                    discussionList.add(Discussion.fromJson(discussionJson));
                                }
                            }
                            adapter.notifyDataSetChanged();
                            hasMore = listArray.length() >= pageSize;
                        }
                        updateEmptyView();
                        updateLoadMoreButton();
                    } else {
                        String message = response.optString("message", "获取失败");
                        showError(message);
                        updateEmptyView();
                    }
                } catch (Exception e) {
                    showError("解析数据失败: " + e.getMessage());
                    updateEmptyView();
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                
                isLoading = false;
                hideLoading();
                showError(error);
                updateEmptyView();
            }
        });
    }

    private void loadMoreDiscussionList() {
        if (isLoading || !hasMore) return;
        
        isLoading = true;
        showLoadMoreLoading();
        
        currentPage++;
        
        BlogApiService.getDiscussionList(currentPage, pageSize, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (getActivity() == null) return;
                
                isLoading = false;
                
                try {
                    int code = response.optInt("code", -1);
                    if (code == 200) {
                        JSONArray listArray = response.optJSONArray("data");
                        if (listArray != null) {
                            for (int i = 0; i < listArray.length(); i++) {
                                JSONObject discussionJson = listArray.optJSONObject(i);
                                if (discussionJson != null) {
                                    discussionList.add(Discussion.fromJson(discussionJson));
                                }
                            }
                            adapter.notifyDataSetChanged();
                            hasMore = listArray.length() >= pageSize;
                        }
                    }
                    hideLoadMoreLoading();
                    updateLoadMoreButton();
                } catch (Exception e) {
                    currentPage--;
                    hideLoadMoreLoading();
                    Toast.makeText(getActivity(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                
                isLoading = false;
                currentPage--;
                hideLoadMoreLoading();
                Toast.makeText(getActivity(), "加载失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDiscussionDetail(Discussion discussion) {
        Intent intent = new Intent(getActivity(), DiscussionDetailActivity.class);
        intent.putExtra("discussionId", discussion.getId());
        startActivity(intent);
    }

    private void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (listView != null) {
            listView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showLoadMoreLoading() {
        if (pbLoadMore != null) {
            pbLoadMore.setVisibility(View.VISIBLE);
        }
        if (tvLoadMore != null) {
            tvLoadMore.setText("加载中...");
        }
    }

    private void hideLoadMoreLoading() {
        if (pbLoadMore != null) {
            pbLoadMore.setVisibility(View.GONE);
        }
    }

    private void updateLoadMoreButton() {
        if (tvLoadMore != null) {
            if (hasMore) {
                tvLoadMore.setText("加载更多");
            } else {
                tvLoadMore.setText("没有更多了");
            }
        }
    }

    private void updateEmptyView() {
        if (discussionList.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
