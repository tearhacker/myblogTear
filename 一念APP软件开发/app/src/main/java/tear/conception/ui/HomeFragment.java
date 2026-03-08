package tear.conception.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tear.conception.BlogDetailActivity;
import tear.conception.R;
import tear.conception.model.Blog;
import tear.conception.module.BlogApiService;
import tear.conception.ui.adapter.BlogListAdapter;

public class HomeFragment extends Fragment {
    
    private ListView listView;
    private LinearLayout emptyView;
    private ProgressBar progressBar;
    private TextView tvEmptyText;
    private LinearLayout loadMoreLayout;
    private TextView tvLoadMore;
    private ProgressBar pbLoadMore;

    private BlogListAdapter adapter;
    private List<Blog> blogList = new ArrayList<>();
    
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMore = true;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        listView = view.findViewById(R.id.list_view);
        emptyView = view.findViewById(R.id.empty_view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyText = view.findViewById(R.id.tv_empty_text);
        
        addLoadMoreFooter();
        
        adapter = new BlogListAdapter(getActivity(), blogList);
        adapter.setOnBlogItemClickListener(new BlogListAdapter.OnBlogItemClickListener() {
            @Override
            public void onBlogClick(Blog blog, int position) {
                openBlogDetail(blog);
            }
        });
        listView.setAdapter(adapter);
        
        loadBlogList();
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
                    loadMoreBlogList();
                }
            }
        });
        
        listView.addFooterView(loadMoreLayout);
    }

    private void loadBlogList() {
        if (isLoading) return;
        
        isLoading = true;
        showLoading();
        
        BlogApiService.getBlogList(currentPage, pageSize, new BlogApiService.BlogListCallback() {
            @Override
            public void onSuccess(List<Blog> blogs, int total, int pageNum, boolean hasNextPage) {
                if (getActivity() == null) return;
                
                isLoading = false;
                hideLoading();
                hasMore = hasNextPage;
                
                blogList.clear();
                blogList.addAll(blogs);
                adapter.notifyDataSetChanged();
                
                updateEmptyView();
                updateLoadMoreButton();
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

    private void loadMoreBlogList() {
        if (isLoading || !hasMore) return;
        
        isLoading = true;
        showLoadMoreLoading();
        
        currentPage++;
        
        BlogApiService.getBlogList(currentPage, pageSize, new BlogApiService.BlogListCallback() {
            @Override
            public void onSuccess(List<Blog> blogs, int total, int pageNum, boolean hasNextPage) {
                if (getActivity() == null) return;
                
                isLoading = false;
                hasMore = hasNextPage;
                
                blogList.addAll(blogs);
                adapter.notifyDataSetChanged();
                
                hideLoadMoreLoading();
                updateLoadMoreButton();
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

    private void openBlogDetail(Blog blog) {
        Intent intent = new Intent(getActivity(), BlogDetailActivity.class);
        intent.putExtra("blogId", blog.getId());
        intent.putExtra("blogTitle", blog.getTitle());
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
        if (blogList.isEmpty()) {
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
