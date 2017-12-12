package rewards.rewardsapp.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rewards.rewardsapp.R;

/**
 * Created by andre on 12/11/2017.
 */
public class CustomTabLayout extends TabLayout {
    private Typeface mTypeface;

    public CustomTabLayout(Context context) {
        super(context);
        init(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        mTypeface = context.getResources().getFont(R.font.bree_serif);
    }

    @Override
    public void addTab(Tab tab, boolean setSelected) {
        TextView tabView = (TextView) View.inflate(getContext(), R.layout.tab_text_view, null);
        tabView.setText(tab.getText());
        tab.setCustomView(tabView);
        super.addTab(tab, setSelected);
//        ViewGroup mainView = (ViewGroup) getChildAt(0);
//        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
//        int tabChildCount = tabView.getChildCount();
//        for (int i = 0; i < tabChildCount; i++) {
//            View tabViewChild = tabView.getChildAt(i);
//            if (tabViewChild instanceof TextView) {
//                ((TextView) tabViewChild).setTypeface(mTypeface);
//            }
//        }
    }

}
