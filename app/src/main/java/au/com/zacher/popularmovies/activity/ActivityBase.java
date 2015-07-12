package au.com.zacher.popularmovies.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import au.com.zacher.popularmovies.ActivityInitialiser;
import au.com.zacher.popularmovies.R;
import au.com.zacher.popularmovies.ToolbarOptions;

/**
 * Created by Brad on 13/07/2015.
 */
public abstract class ActivityBase extends AppCompatActivity {
    protected ProgressBar progressBar;
    protected View noInternetSection;
    protected Button loadRetryButton;
    protected Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState, ToolbarOptions options, int layoutId) {
        super.onCreate(savedInstanceState);

        ActivityInitialiser.initActivity(options, savedInstanceState, this, layoutId);

        this.progressBar = (ProgressBar)this.findViewById(R.id.progress_bar);
        this.noInternetSection = this.findViewById(R.id.no_internet_section);
        this.loadRetryButton = (Button)this.noInternetSection.findViewById(R.id.retry_button);
        this.toolbar = (Toolbar)this.findViewById(R.id.toolbar);
    }

    public void setViewState(final ViewState state) {
        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                switch(state) {
                    case ERROR:
                        ActivityBase.this.progressBar.setVisibility(View.GONE);
                        ActivityBase.this.getMainViewItem().setVisibility(View.GONE);
                        ActivityBase.this.noInternetSection.setVisibility(View.VISIBLE);
                        break;

                    case IN_PROGRESS:
                        ActivityBase.this.progressBar.setVisibility(View.VISIBLE);
                        ActivityBase.this.getMainViewItem().setVisibility(View.GONE);
                        ActivityBase.this.noInternetSection.setVisibility(View.GONE);
                        break;

                    case SUCCESS:
                        ActivityBase.this.progressBar.setVisibility(View.GONE);
                        ActivityBase.this.getMainViewItem().setVisibility(View.VISIBLE);
                        ActivityBase.this.noInternetSection.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    public abstract View getMainViewItem();
}
