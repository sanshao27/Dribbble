package com.assistne.dribbble.framestday004;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.assistne.dribbble.R;

/**
 * Created by assistne on 16/4/26.
 */
public class CardFragment extends Fragment{
    private static final String TAG = "#CardFragment";

    private static final String KEY_CARD = "CARD";
    private static final String KEY_STAR = "STAR";
    private static final String KEY_COMMENT = "COMMENT";
    private static final String KEY_AVATAR = "AVATAR";
    public static final String KEY_IS_SHOW = "IS_SHOW";

    private View mContainer;
    private ImageView mCard;
    private ImageView mStar;
    private TextView mComment;
    private ImageView mAvatar;

    private AnimatorSet mShowSet;
    private AnimatorSet mHideSet;

    private boolean mIsShow;

    public static CardFragment newInstance(@DrawableRes int card, @DrawableRes int star,
                                           @StringRes int comment, @DrawableRes int avatar, boolean isShow) {
        CardFragment fragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_CARD, card);
        bundle.putInt(KEY_STAR, star);
        bundle.putInt(KEY_COMMENT, comment);
        bundle.putInt(KEY_AVATAR, avatar);
        bundle.putBoolean(KEY_IS_SHOW, isShow);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fst_d4_card, container, false);
        mContainer = root.findViewById(R.id.fst_d4_container);
        mCard = (ImageView) root.findViewById(R.id.fst_d4_card_img);
        mStar = (ImageView) root.findViewById(R.id.fst_d4_star);
        mComment = (TextView) root.findViewById(R.id.fst_d4_card_comment);
        mAvatar = (ImageView) root.findViewById(R.id.fst_d4_card_avatar);
        Bundle bundle = getArguments();
        mCard.setImageResource(bundle.getInt(KEY_CARD));
        mStar.setImageResource(bundle.getInt(KEY_STAR));
        mComment.setText(bundle.getInt(KEY_COMMENT));
        mAvatar.setImageResource(bundle.getInt(KEY_AVATAR));
        mIsShow = bundle.getBoolean(KEY_IS_SHOW);
        initShowSet();
        initHideSet();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsShow) {
            mContainer.setScaleX(1f);
            mContainer.setScaleY(1f);
            mContainer.setAlpha(1f);
            mCard.setTranslationY(0);
            mAvatar.setAlpha(1f);
            mComment.setAlpha(1f);
            mStar.setTranslationY(0);
            mStar.setAlpha(1f);
        }
    }

    private void initShowSet() {
        mShowSet = new AnimatorSet();
        ObjectAnimator containAniX = ObjectAnimator.ofFloat(mContainer, "scaleX", 1f);
        ObjectAnimator containAniY = ObjectAnimator.ofFloat(mContainer, "scaleY", 1f);
        ObjectAnimator containAlpha = ObjectAnimator.ofFloat(mContainer, "alpha", 1f);
        ObjectAnimator imgAni = ObjectAnimator.ofFloat(mCard, "translationY", 0).setDuration(500);
        AnimatorSet containSet = new AnimatorSet();
        containSet.playTogether(containAniX, containAniY, containAlpha, imgAni);
        ObjectAnimator avatarAni = ObjectAnimator.ofFloat(mAvatar, "alpha", 1f);
        ObjectAnimator commentAni = ObjectAnimator.ofFloat(mComment, "alpha", 1f);
        AnimatorSet imgSet = new AnimatorSet();
        ObjectAnimator starAni = ObjectAnimator.ofFloat(mStar, "translationY", 0).setDuration(100);
        ObjectAnimator starAniAlpha = ObjectAnimator.ofFloat(mStar, "alpha", 1f);
        imgSet.playTogether(avatarAni, commentAni, starAni, starAniAlpha);
        mShowSet.playSequentially(containSet, imgSet);
    }

    private void initHideSet() {
        mHideSet = new AnimatorSet();
        ObjectAnimator containAniX = ObjectAnimator.ofFloat(mContainer, "scaleX", 0.5f);
        ObjectAnimator containAniY = ObjectAnimator.ofFloat(mContainer, "scaleY", 0.3f);
        ObjectAnimator containAlpha = ObjectAnimator.ofFloat(mContainer, "alpha", 0f);
        ObjectAnimator imgAni = ObjectAnimator.ofFloat(mCard, "translationY", dp2Px(60)).setDuration(500);
        AnimatorSet containSet = new AnimatorSet();
        containSet.playTogether(containAniX, containAniY, containAlpha, imgAni);

        ObjectAnimator avatarAni = ObjectAnimator.ofFloat(mAvatar, "alpha", 0f);
        ObjectAnimator commentAni = ObjectAnimator.ofFloat(mComment, "alpha", 0f);
        ObjectAnimator starAniAlpha = ObjectAnimator.ofFloat(mStar, "alpha", 0f).setDuration(600);
        AnimatorSet imgSet = new AnimatorSet();
        imgSet.playTogether(avatarAni, commentAni, starAniAlpha);
        mHideSet.playSequentially(containSet, imgSet);
    }

    public void show() {
        if (!mIsShow) {
            mIsShow = true;
            mContainer.setVisibility(View.VISIBLE);
            mShowSet.start();
        }
    }

    public void hide() {
        if (mIsShow) {
            mIsShow = false;
            mHideSet.start();
            mStar.setTranslationY(-1 * dp2Px(15));
        }
    }

    private float dp2Px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
