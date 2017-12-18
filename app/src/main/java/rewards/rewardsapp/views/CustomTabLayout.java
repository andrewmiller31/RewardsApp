package rewards.rewardsapp.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import rewards.rewardsapp.R;

/**
 * Created by andre on 12/11/2017.
 */
public class CustomTabLayout extends TabLayout {

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void addTab(Tab tab, boolean setSelected) {
        TextView tabView = (TextView) View.inflate(getContext(), R.layout.tab_text_view, null);
        tabView.setText(tab.getText());
        tab.setCustomView(tabView);
        super.addTab(tab, setSelected);
    }

}
